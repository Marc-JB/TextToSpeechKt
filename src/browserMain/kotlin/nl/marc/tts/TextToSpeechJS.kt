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

    override var volume: Int
        get() = (speechSynthesisUtterance.volume * 100).roundToInt()
        set(value) {
            speechSynthesisUtterance.volume = value / 100f
        }

    override fun say(text: String, clearQueue: Boolean) {
        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)
    }

    override fun close() {
        speechSynthesis.pause()
        speechSynthesis.cancel()
    }
}

