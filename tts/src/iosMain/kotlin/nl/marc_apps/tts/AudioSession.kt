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
    @OptIn(ExperimentalForeignApi::class)
    fun initialiseForTextToSpeech() {
        val audioSession = AVAudioSession.sharedInstance()

        val errorPtr = ErrorPointerUtils.createErrorPointer()

        audioSession.setCategory(AVAudioSessionCategoryPlayback,
            mode = AVAudioSessionModeDefault,
            options = AVAudioSessionCategoryOptionDuckOthers,
            errorPtr.ptr)

        errorPtr.throwOnError()

        audioSession.setActive(true, errorPtr.ptr)

        errorPtr.throwOnError()
    }
}