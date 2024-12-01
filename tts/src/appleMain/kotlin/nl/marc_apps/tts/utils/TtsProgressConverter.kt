package nl.marc_apps.tts.utils

import kotlinx.cinterop.ObjCSignatureOverride
import nl.marc_apps.tts.errors.TextToSpeechSynthesisInterruptedError
import platform.AVFAudio.*
import platform.darwin.NSObject

class TtsProgressConverter(
    val onStart: (AVSpeechUtterance) -> Unit,
    val onComplete: (AVSpeechUtterance, Result<Unit>) -> Unit
) : NSObject(), AVSpeechSynthesizerDelegateProtocol {
    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didStartSpeechUtterance: AVSpeechUtterance) {
        onStart(didStartSpeechUtterance)
    }

    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didFinishSpeechUtterance: AVSpeechUtterance) {
        onComplete(didFinishSpeechUtterance, Result.success(Unit))
    }

    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didCancelSpeechUtterance: AVSpeechUtterance) {
        onComplete(didCancelSpeechUtterance, Result.failure(TextToSpeechSynthesisInterruptedError()))
    }
}
