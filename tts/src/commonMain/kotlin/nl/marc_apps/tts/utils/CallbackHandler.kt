package nl.marc_apps.tts.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CallbackHandler<TNativeUtteranceId> {
    private val resultHandlers = mutableMapOf<Uuid, ResultHandler>()

    private val utteranceIds = mutableMapOf<TNativeUtteranceId, Uuid>()

    private var queueSize = 0

    val isQueueEmpty
        get() = queueSize == 0

    fun add(utteranceId: Uuid, nativeObject: TNativeUtteranceId, resultHandler: ResultHandler) {
        resultHandlers[utteranceId] = resultHandler
        utteranceIds[nativeObject] = utteranceId
        queueSize++
    }

    fun getUtteranceId(nativeObject: TNativeUtteranceId): Uuid? = utteranceIds[nativeObject]

    fun onResult(utteranceId: Uuid, result: Result<Unit>) {
        if (queueSize-- < 0) {
            queueSize = 0
        }

        val continuation = resultHandlers.remove(utteranceId)
        continuation?.setResult(result)
    }

    fun onStopped() {
        queueSize = 0
    }

    fun clear() {
        resultHandlers.clear()
        queueSize = 0
    }
}
