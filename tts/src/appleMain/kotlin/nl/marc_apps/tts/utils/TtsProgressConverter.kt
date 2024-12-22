package nl.marc_apps.tts.utils

import kotlinx.cinterop.ObjCSignatureOverride
import nl.marc_apps.tts.errors.TextToSpeechSynthesisInterruptedError
import platform.AVFAudio.*
import platform.darwin.NSObject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TtsProgressConverter(
    private val callbackHandler: CallbackHandler<AVSpeechUtterance>,
    private val onStart: (Uuid) -> Unit,
    private val onComplete: (Uuid, Result<Unit>) -> Unit
) : NSObject(), AVSpeechSynthesizerDelegateProtocol {
    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didStartSpeechUtterance: AVSpeechUtterance) {
        val utteranceId = callbackHandler.getUtteranceId(didStartSpeechUtterance)
        if (utteranceId != null) {
            onStart(utteranceId)
        }
    }

    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didFinishSpeechUtterance: AVSpeechUtterance) {
        val utteranceId = callbackHandler.getUtteranceId(didFinishSpeechUtterance)
        if (utteranceId != null) {
            onComplete(utteranceId, Result.success(Unit))
        }
    }

    @ObjCSignatureOverride
    override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didCancelSpeechUtterance: AVSpeechUtterance) {
        val utteranceId = callbackHandler.getUtteranceId(didCancelSpeechUtterance)
        if (utteranceId != null) {
            onComplete(utteranceId, Result.failure(TextToSpeechSynthesisInterruptedError()))
        }
    }
}
