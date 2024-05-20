package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget

@ExperimentalDesktopTarget
actual class TextToSpeechFactory {
    @ExperimentalDesktopTarget
    actual val isSupported = true

    @ExperimentalDesktopTarget
    actual val canChangeVolume = true

    @ExperimentalDesktopTarget
    actual suspend fun create(): Result<TextToSpeechInstance> {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory,com.sun.speech.freetts.en.us.cmu_us_awb.AlanVoiceDirectory,de.dfki.lt.freetts.en.us.MbrolaVoiceDirectory")
        val voiceManager = VoiceManager.getInstance()
        return Result.success(TextToSpeechDesktop(voiceManager))
    }

    @ExperimentalDesktopTarget
    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return create().getOrThrow()
    }

    @ExperimentalDesktopTarget
    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}
