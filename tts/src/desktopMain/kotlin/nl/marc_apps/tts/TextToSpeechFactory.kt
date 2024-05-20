package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager

actual class TextToSpeechFactory {
    private val providedVoices = arrayOf(
        "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"
    )

    actual val isSupported = true

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        System.setProperty("freetts.voices", providedVoices.joinToString(separator = ","))
        val voiceManager = VoiceManager.getInstance()
        return Result.success(TextToSpeechDesktop(voiceManager))
    }

    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
