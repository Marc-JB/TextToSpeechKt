package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager

actual class TextToSpeechFactory {
    actual val isSupported: Boolean = true

    @Throws(RuntimeException::class)
    actual suspend fun create(): TextToSpeechInstance {
        TODO("Not yet implemented")
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory")
        val voiceManager = VoiceManager.getInstance()
        return TextToSpeechInstanceDesktop(voiceManager)
    }
}
