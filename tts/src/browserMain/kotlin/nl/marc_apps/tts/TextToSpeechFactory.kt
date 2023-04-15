package nl.marc_apps.tts

import kotlinx.browser.window
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import org.w3c.dom.Window

actual class TextToSpeechFactory(
    private val context: Window = window
) {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return if (TextToSpeech.isSupported) {
            Result.success(TextToSpeechJS(context))
        } else {
            Result.failure(TextToSpeechNotSupportedError())
        }
    }

    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}