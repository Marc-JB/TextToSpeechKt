package nl.marc_apps.tts

import js_interop.Window
import js_interop.isSpeechSynthesisSupported
import js_interop.window
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError

/**
 * Factory class to create a Text-to-Speech instance.
 */
actual class TextToSpeechFactory(
    private val context: Window = window
) {
    actual val isSupported = isSpeechSynthesisSupported

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return if (isSupported) {
            Result.success(TextToSpeechBrowser(context))
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
