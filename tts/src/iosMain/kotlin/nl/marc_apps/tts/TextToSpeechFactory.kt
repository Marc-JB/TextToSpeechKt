package nl.marc_apps.tts

import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import nl.marc_apps.tts.experimental.ExperimentalIosTarget

@ExperimentalIosTarget
actual class TextToSpeechFactory {
    @ExperimentalIosTarget
    actual val isSupported = false

    @ExperimentalIosTarget
    actual val canChangeVolume = false

    @ExperimentalIosTarget
    actual suspend fun create(): Result<TextToSpeechInstance> {
        return Result.failure(TextToSpeechNotSupportedError())
    }

    @ExperimentalIosTarget
    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    @ExperimentalIosTarget
    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
