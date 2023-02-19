package nl.marc_apps.tts

import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.speech.speechSynthesis

actual class TextToSpeechFactory(private val context: Window = window) {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual suspend fun create(): TextToSpeechInstance {
        TODO("Not yet implemented")
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return if (isSupported) TextToSpeechInstanceBrowser(context.speechSynthesis) else null
    }
}
