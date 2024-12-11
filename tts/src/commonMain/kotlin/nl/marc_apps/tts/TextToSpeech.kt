package nl.marc_apps.tts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.utils.CallbackHandler
import nl.marc_apps.tts.utils.ResultHandler
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal abstract class TextToSpeech<TNativeUtteranceId> : TextToSpeechInstance {
    protected abstract val canDetectSynthesisStarted: Boolean

    protected val callbackHandler = CallbackHandler<TNativeUtteranceId>()

    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    protected abstract fun enqueueInternal(text: String, resultHandler: ResultHandler)

    private fun enqueue(text: String, clearQueue: Boolean, resultHandler: ResultHandler){
        if (clearQueue) {
            stop()
        }

        if (canDetectSynthesisStarted && !hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        if (!canDetectSynthesisStarted) {
            isSynthesizing.value = true
        }

        enqueueInternal(text, resultHandler)
    }

    override fun enqueue(text: String, clearQueue: Boolean) {
        enqueue(text, clearQueue, ResultHandler.Empty)
    }

    override fun plusAssign(text: String) {
        enqueue(text, false, ResultHandler.Empty)
    }

    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        enqueue(text, clearQueue, ResultHandler.CallbackHandler(callback))
    }

    override suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean){
        suspendCancellableCoroutine { cont ->
            enqueue(text, clearQueue, ResultHandler.ContinuationHandler(cont))
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            }
        }
    }

    protected fun onTtsStarted(utteranceId: Uuid) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    protected fun onTtsCompleted(utteranceId: Uuid, result: Result<Unit>) {
        isWarmingUp.value = false

        callbackHandler.onResult(utteranceId, result)

        if (!canDetectSynthesisStarted) {
            if (callbackHandler.isQueueEmpty)
            {
                isSynthesizing.value = false
            }
        } else {
            isSynthesizing.value = false
        }
    }

    override fun stop() {
        callbackHandler.onStopped()
        if (!canDetectSynthesisStarted) {
            isSynthesizing.value = false
        }
    }

    override fun close() {
        stop()
        callbackHandler.clear()
    }
}