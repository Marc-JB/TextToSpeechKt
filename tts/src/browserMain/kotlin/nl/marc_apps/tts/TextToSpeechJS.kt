@file:Suppress("unused")

package nl.marc_apps.tts

import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import org.w3c.speech.speechSynthesis
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/** A TTS instance. Should be [close]d when no longer in use. */
@ExperimentalJsExport
internal class TextToSpeechJS(context: Window = window) : TextToSpeechInstanceJS(), TextToSpeechInstance {
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
        get() = speechSynthesisUtterance.voice.lang

    private fun resetCurrentUtterance() {
        speechSynthesisUtterance = SpeechSynthesisUtterance().also {
            it.volume = internalVolume
            it.pitch = pitch
            it.rate = rate
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun enqueue(text: String, clearQueue: Boolean) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            return
        }

        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)

        resetCurrentUtterance()
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun say(text: String, clearQueue: Boolean, callback: (Result<TextToSpeechInstance.Status>) -> Unit) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            callback(Result.success(TextToSpeechInstance.Status.FINISHED))
            return
        }

        speechSynthesisUtterance.onstart = {
            callback(Result.success(TextToSpeechInstance.Status.STARTED))
        }
        speechSynthesisUtterance.onend = {
            callback(Result.success(TextToSpeechInstance.Status.FINISHED))
        }

        enqueue(text, clearQueue)
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override suspend fun say(text: String, clearQueue: Boolean, resumeOnStatus: TextToSpeechInstance.Status) {
        suspendCoroutine<Unit> { cont ->
            say(text, clearQueue) {
                if (it.isSuccess && it.getOrNull() in arrayOf(resumeOnStatus, TextToSpeechInstance.Status.FINISHED)) {
                    cont.resume(Unit)
                } else if (it.isFailure) {
                    it.exceptionOrNull()?.let { thr -> cont.resumeWithException(thr) }
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
