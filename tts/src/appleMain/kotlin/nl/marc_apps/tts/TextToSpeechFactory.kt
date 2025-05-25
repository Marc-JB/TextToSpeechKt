package nl.marc_apps.tts

import platform.AVFAudio.AVSpeechSynthesizer

actual class TextToSpeechFactory {
    actual val isSupported = true

    actual val canChangeVolume = false

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return Result.success(TextToSpeechIOS(AVSpeechSynthesizer()))
    }

    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
