@file:Suppress("unused")

package nl.marc.tts

import org.w3c.dom.Window
import kotlin.browser.window
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import org.w3c.speech.speechSynthesis
import kotlin.math.roundToInt

internal class TextToSpeechJS(context: Window = window) : TextToSpeechInstance {
    private val speechSynthesis: SpeechSynthesis = context.speechSynthesis

    private val speechSynthesisUtterance = SpeechSynthesisUtterance()

    private val internalVolume: Float
        get() = if(!isMuted) volume.toFloat() else 0f

    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     */
    override var volume: Int = 100
        set(value) {
            field = value
            speechSynthesisUtterance.volume = internalVolume
        }

    override var isMuted = false
        set(value) {
            field = value
            speechSynthesisUtterance.volume = internalVolume
        }

    override fun say(text: String, clearQueue: Boolean) {
        if(clearQueue) speechSynthesis.cancel()
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

