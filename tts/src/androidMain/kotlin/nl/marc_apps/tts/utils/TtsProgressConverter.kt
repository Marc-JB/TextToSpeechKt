package nl.marc_apps.tts.utils

import android.os.Build
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import nl.marc_apps.tts.errors.TextToSpeechSynthesisInterruptedError
import nl.marc_apps.tts.errors.UnknownTextToSpeechSynthesisError
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @hide
 */
@OptIn(ExperimentalUuidApi::class)
@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
class TtsProgressConverter(
    val onStart: (Uuid) -> Unit,
    val onComplete: (Uuid, Result<Unit>) -> Unit
) : UtteranceProgressListener() {
    override fun onStart(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onStart(id)
    }

    override fun onDone(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onComplete(id, Result.success(Unit))
    }

    override fun onError(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onComplete(id, Result.failure(UnknownTextToSpeechSynthesisError()))
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        val id = getContinuationId(utteranceId) ?: return
        onComplete(id, Result.failure(ErrorCodes.mapToThrowable(errorCode)))
    }

    override fun onStop(utteranceId: String?, interrupted: Boolean) {
        val id = getContinuationId(utteranceId) ?: return
        onComplete(id, Result.failure(TextToSpeechSynthesisInterruptedError()))
    }
}