package nl.marc_apps.tts.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CallbackHandler<TNativeObject> {
    private val callbacks = mutableMapOf<Uuid, (Result<Unit>) -> Unit>()

    private val utteranceIds = mutableMapOf<TNativeObject, Uuid>()

    private var queueSize = 0

    val isQueueEmpty
        get() = queueSize == 0

    fun add(utteranceId: Uuid, nativeObject: TNativeObject, callback: (Result<Unit>) -> Unit) {
        callbacks[utteranceId] = callback
        utteranceIds[nativeObject] = utteranceId
        queueSize++
    }

    fun onResult(nativeObject: TNativeObject, result: Result<Unit>) {
        val utteranceId = utteranceIds[nativeObject]
        if (utteranceId != null) {
            onResult(utteranceId, result)
        }
    }

    fun onResult(utteranceId: Uuid, result: Result<Unit>) {
        if (queueSize-- < 0) {
            queueSize = 0
        }
        val callback = callbacks.remove(utteranceId)
        callback?.invoke(result)
    }

    fun onStopped() {
        queueSize = 0
    }

    fun clear() {
        callbacks.clear()
        queueSize = 0
    }
}
