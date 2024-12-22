package nl.marc_apps.tts.utils

import kotlin.coroutines.Continuation

sealed interface ResultHandler {
    fun setResult(result: Result<Unit>)

    class ContinuationHandler(private val continuation: Continuation<Unit>) : ResultHandler {
        override fun setResult(result: Result<Unit>) {
            continuation.resumeWith(result)
        }
    }

    class CallbackHandler(private val callback: (result: Result<Unit>) -> Unit) : ResultHandler {
        override fun setResult(result: Result<Unit>) {
            callback(result)
        }
    }

    data object Empty : ResultHandler {
        override fun setResult(result: Result<Unit>) {}
    }
}
