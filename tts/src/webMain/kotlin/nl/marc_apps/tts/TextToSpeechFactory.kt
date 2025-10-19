package nl.marc_apps.tts

import kotlinx.browser.window
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import org.w3c.dom.Window
import org.w3c.speech.windowHasSpeechSynthesis
import kotlin.js.ExperimentalWasmJsInterop

/**
 * Factory class to create a Text-to-Speech instance.
 */
@OptIn(ExperimentalWasmJsInterop::class)
actual class TextToSpeechFactory(
    private val context: Window = window
) {
    actual val isSupported = windowHasSpeechSynthesis

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return if (isSupported) {
            Result.success(TextToSpeechBrowser(context))
        } else {
            Result.failure(TextToSpeechNotSupportedError())
        }
    }

    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
