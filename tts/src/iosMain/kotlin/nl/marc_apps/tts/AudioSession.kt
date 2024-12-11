package nl.marc_apps.tts

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import nl.marc_apps.tts.utils.ErrorPointerUtils
import nl.marc_apps.tts.utils.throwOnError
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDuckOthers
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive

object AudioSession {
    @ExperimentalForeignApi
    fun initialiseForTextToSpeech() {
        val audioSession = AVAudioSession.sharedInstance()

        ErrorPointerUtils.createErrorPointer { errorPtr ->
            audioSession.setCategory(AVAudioSessionCategoryPlayback,
                mode = AVAudioSessionModeDefault,
                options = AVAudioSessionCategoryOptionDuckOthers,
                errorPtr.ptr)

            errorPtr.throwOnError()

            audioSession.setActive(true, errorPtr.ptr)

            errorPtr.throwOnError()
        }
    }
}