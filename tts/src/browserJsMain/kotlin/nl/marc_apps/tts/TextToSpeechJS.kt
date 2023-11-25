@file:Suppress("unused")

package nl.marc_apps.tts

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import nl.marc_apps.tts.errors.UnknownTextToSpeechSynthesisError
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import org.w3c.dom.Window
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import org.w3c.speech.speechSynthesis
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/** A TTS instance. Should be [close]d when no longer in use. */
internal class TextToSpeechJS(context: Window = window) : TextToSpeechInstance {
    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private val speechSynthesis: SpeechSynthesis = context.speechSynthesis

    private var speechSynthesisUtterance = SpeechSynthesisUtterance()

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [say] method.
     */
    override var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            field = when {
                value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                else -> value
            }
            speechSynthesisUtterance.volume = internalVolume
        }

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [say] method.
     */
    override var isMuted = false
        set(value) {
            field = value
            speechSynthesisUtterance.volume = internalVolume
        }

    override var pitch = TextToSpeechInstance.VOICE_PITCH_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.pitch = value
        }

    override var rate = TextToSpeechInstance.VOICE_RATE_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.rate = value
        }

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    override val language: String
        get() {
            val reportedLanguage = speechSynthesisUtterance.voice?.lang ?: speechSynthesisUtterance.lang
            return reportedLanguage.ifBlank {
                val defaultLanguage = speechSynthesis.getVoices().find { it.default }?.lang
                if (defaultLanguage.isNullOrBlank()) "Unknown" else defaultLanguage
            }
        }

    @ExperimentalVoiceApi
    private val defaultVoice by lazy {
        speechSynthesis.getVoices().find { it.default }?.let { BrowserVoice(it) }
    }

    @ExperimentalVoiceApi
    override var currentVoice: Voice? = null
        get() = field ?: defaultVoice
        set(value) {
            if (value is BrowserVoice) {
                speechSynthesisUtterance.voice = value.browserVoice
                field = value
            }
        }

    @ExperimentalVoiceApi
    override val voices: Sequence<Voice> by lazy {
        speechSynthesis.getVoices().asSequence().map { BrowserVoice(it) }
    }

    @OptIn(ExperimentalVoiceApi::class)
    private fun resetCurrentUtterance() {
        speechSynthesisUtterance = SpeechSynthesisUtterance().also {
            it.volume = internalVolume
            it.pitch = pitch
            it.rate = rate
            it.voice = (currentVoice as? BrowserVoice)?.browserVoice
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun enqueue(text: String, clearQueue: Boolean) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            return
        }

        if (!hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)

        resetCurrentUtterance()
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            callback(Result.success(Unit))
            return
        }

        speechSynthesisUtterance.onstart = {
            isWarmingUp.value = false
            isSynthesizing.value = true
        }

        speechSynthesisUtterance.onend = {
            isWarmingUp.value = false
            isSynthesizing.value = false
            callback(Result.success(Unit))
        }

        enqueue(text, clearQueue)
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override suspend fun say(text: String, clearQueue: Boolean) {
        suspendCoroutine { cont ->
            say(text, clearQueue) {
                if (it.isSuccess) {
                    cont.resume(it.getOrThrow())
                } else if (it.isFailure) {
                    val error = it.exceptionOrNull() ?: UnknownTextToSpeechSynthesisError()
                    cont.resumeWithException(error)
                }
            }
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun plusAssign(text: String) {
        enqueue(text, false)
    }

    /** Clears the internal queue, but doesn't close used resources. */
    override fun stop() {
        speechSynthesis.cancel()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close() {
        speechSynthesis.cancel()
    }
}
