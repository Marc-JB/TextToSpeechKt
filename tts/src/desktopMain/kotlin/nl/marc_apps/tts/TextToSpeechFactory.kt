package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class TextToSpeechFactory {
    private val providedVoices = arrayOf(
        "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"
    )

    actual val isSupported = true

    actual val canChangeVolume = true

    actual suspend fun create(): Result<TextToSpeechInstance> {
        System.setProperty("freetts.voices", providedVoices.joinToString(separator = ","))
        val voiceManager = VoiceManager.getInstance()
        val voice = voiceManager.getVoice(VOICE_NAME)
        withContext(Dispatchers.Default) {
            voice.allocate()
        }
        return Result.success(TextToSpeechDesktop(voiceManager, voice))
    }

    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }

    companion object {
        private const val VOICE_NAME = "kevin16"
    }
}
