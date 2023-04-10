package nl.marc_apps.tts.utils

import android.os.Build
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.errors.*
import java.util.*

@RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
class TtsProgressConverter(val onTtsStatusUpdate: (UUID, Result<TextToSpeechInstance.Status>) -> Unit) : UtteranceProgressListener() {
    override fun onStart(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onTtsStatusUpdate(id, Result.success(TextToSpeechInstance.Status.STARTED))
    }

    override fun onDone(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onTtsStatusUpdate(id, Result.success(TextToSpeechInstance.Status.FINISHED))
    }

    override fun onError(utteranceId: String?) {
        val id = getContinuationId(utteranceId) ?: return
        onTtsStatusUpdate(id, Result.failure(UnknownTextToSpeechSynthesisError()))
    }

    override fun onError(utteranceId: String?, errorCode: Int) {
        val id = getContinuationId(utteranceId) ?: return
        onTtsStatusUpdate(id, Result.failure(mapErrorCodeToThrowable(errorCode)))
    }

    override fun onStop(utteranceId: String?, interrupted: Boolean) {
        val id = getContinuationId(utteranceId) ?: return
        onTtsStatusUpdate(id, Result.failure(TextToSpeechSynthesisInterruptedError()))
    }

    private fun mapErrorCodeToThrowable(errorCode: Int): TextToSpeechSynthesisError {
        return when(errorCode) {
            ErrorCodes.ERROR_SYNTHESIS -> TextToSpeechFlawedTextInputError()
            ErrorCodes.ERROR_SERVICE -> TextToSpeechServiceFailureError()
            ErrorCodes.ERROR_OUTPUT -> DeviceAudioOutputError()
            ErrorCodes.ERROR_NETWORK -> NetworkConnectivityError()
            ErrorCodes.ERROR_NETWORK_TIMEOUT -> NetworkTimeoutError()
            ErrorCodes.ERROR_INVALID_REQUEST -> TextToSpeechRequestInvalidError()
            ErrorCodes.ERROR_NOT_INSTALLED_YET -> TextToSpeechEngineUnavailableError()
            else -> UnknownTextToSpeechSynthesisError()
        }
    }
}