package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalIOSTarget
import platform.AVFAudio.AVSpeechSynthesizer

@ExperimentalIOSTarget
actual class TextToSpeechFactory {
    @ExperimentalIOSTarget
    actual val isSupported = true

    @ExperimentalIOSTarget
    actual val canChangeVolume = false

    @ExperimentalIOSTarget
    actual suspend fun create(): Result<TextToSpeechInstance> {
        return Result.success(TextToSpeechIOS(AVSpeechSynthesizer()))
    }

    @ExperimentalIOSTarget
    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    @ExperimentalIOSTarget
    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
