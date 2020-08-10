@file:Suppress("unused")

package nl.marc.tts

import org.w3c.dom.Window
import kotlin.browser.window
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import org.w3c.speech.speechSynthesis
import kotlin.math.roundToInt

/** A TTS instance. Should be [close]d when no longer in use. */
internal class TextToSpeechJS(context: Window = window) : TextToSpeechInstance {
    private val speechSynthesis: SpeechSynthesis = context.speechSynthesis

    private val speechSynthesisUtterance = SpeechSynthesisUtterance()

    private val internalVolume: Float
        get() = if(!isMuted) volume.toFloat() else 0f

    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     * Changes only affect new calls to the [say] method.
     */
    override var volume: Int = 100
        set(value) {
            field = value
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

    override var pitch: Float
        get() = speechSynthesisUtterance.pitch
        set(value) {
            speechSynthesisUtterance.pitch = value
        }

    override var rate: Float
        get() = speechSynthesisUtterance.rate
        set(value) {
            speechSynthesisUtterance.rate = value
        }

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    override val language: String
        get() = speechSynthesisUtterance.voice.lang

    /**
     * Behaviour of this method:
     *
     * 1A) [clearQueue] is true: Clears the internal queue (like the [stop] method).
     * 1B) [clearQueue] is false: Retains the internal queue.
     *
     * 2A) [isMuted] is true, or [volume] is zero: No text is added to the queue.
     * 2B) [isMuted] is false and [volume] is above zero: Adds the text with [volume], [rate] and [pitch] to the internal queue.
     */
    override fun say(text: String, clearQueue: Boolean) {
        if(clearQueue) speechSynthesis.cancel()
        if(isMuted || internalVolume == 0f) return
        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)
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

