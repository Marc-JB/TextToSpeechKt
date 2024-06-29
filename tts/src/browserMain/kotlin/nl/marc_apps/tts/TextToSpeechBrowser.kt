@file:Suppress("unused")

package nl.marc_apps.tts

import js_interop.Window
import js_interop.getSpeechSynthesis
import js_interop.getVoiceList
import js_interop.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.errors.UnknownTextToSpeechSynthesisError
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

/** A TTS instance. Should be [close]d when no longer in use. */
internal class TextToSpeechBrowser(context: Window = window) {
    private val callbacks = mutableMapOf<Long, (Result<Unit>) -> Unit>()

    val isSynthesizing = MutableStateFlow(false)

    val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private val speechSynthesis: SpeechSynthesis = getSpeechSynthesis(context)

    private var speechSynthesisUtterance = SpeechSynthesisUtterance()

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [say] method.
     */
    var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            field = when {
                value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                else -> value
            }
            speechSynthesisUtterance.volume = volume / 100f
        }

    var pitch = TextToSpeechInstance.VOICE_PITCH_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.pitch = value
        }

    var rate = TextToSpeechInstance.VOICE_RATE_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.rate = value
        }

    private val voiceList by lazy {
        getVoiceList(speechSynthesis)
    }

    private val defaultVoice by lazy {
        voiceList.find { it.default }?.let { BrowserVoice(it) }
    }

    var currentVoice: Voice? = null
        get() = field ?: defaultVoice
        set(value) {
            if (value is BrowserVoice) {
                speechSynthesisUtterance.voice = value.browserVoice
                field = value
            }
        }

    val voices: Sequence<Voice> by lazy {
        voiceList.asSequence().map { BrowserVoice(it) }
    }

    private fun resetCurrentUtterance() {
        speechSynthesisUtterance = SpeechSynthesisUtterance().also {
            it.volume = volume / 100f
            it.pitch = pitch
            it.rate = rate
            it.voice = (currentVoice as? BrowserVoice)?.browserVoice
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun enqueue(text: String, clearQueue: Boolean) {
        if(volume == 0) {
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
    fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        if(volume == 0) {
            if(clearQueue) stop()
            callback(Result.success(Unit))
            return
        }

        val utteranceId = Random.Default.nextLong()

        callbacks += utteranceId to {
            callback(it)
            callbacks.remove(utteranceId)
        }

        speechSynthesisUtterance.onstart = {
            onTtsStarted(utteranceId)
        }

        speechSynthesisUtterance.onend = {
            onTtsCompleted(utteranceId, Result.success(Unit))
        }

        enqueue(text, clearQueue)
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean) {
        suspendCancellableCoroutine { cont ->
            say(text, clearQueue) {
                if (it.isSuccess) {
                    cont.resume(it.getOrThrow())
                } else if (it.isFailure) {
                    val error = it.exceptionOrNull() ?: UnknownTextToSpeechSynthesisError()
                    cont.resumeWithException(error)
                }
            }
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            }
        }
    }

    private fun onTtsStarted(utteranceId: Long) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utteranceId: Long, result: Result<Unit>) {
        isWarmingUp.value = false
        isSynthesizing.value = false
        callbacks[utteranceId]?.invoke(result)
    }

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop() {
        speechSynthesis.cancel()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    fun close() {
        speechSynthesis.cancel()
    }
}
