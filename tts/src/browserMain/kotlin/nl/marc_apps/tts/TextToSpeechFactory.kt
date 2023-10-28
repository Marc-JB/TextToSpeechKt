package nl.marc_apps.tts

import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import org.w3c.speech.Window
import org.w3c.speech.getWindow

/**
 * Factory class to create a Text-to-Speech instance.
 */
actual class TextToSpeechFactory(
    private val context: Window = getWindow()
) {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return if (isSupported) {
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