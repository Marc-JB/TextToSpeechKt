package nl.marc_apps.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.CallbackQueueHandler
import nl.marc_apps.tts.internal.TextToSpeechHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TextToSpeech(private val implementation: TextToSpeechHandler) {
    private val callbacks = mutableMapOf<Any, (Result<Unit>) -> Unit>()
    private val continuations = mutableMapOf<Any, Continuation<Unit>>()

    val isSynthesizing = MutableStateFlow(false)

    val isWarmingUp = MutableStateFlow(false)

    init {
        if (implementation is CallbackQueueHandler){
            implementation.registerListeners(::onTtsStarted, ::onTtsCompleted)
        }
    }

    fun enqueue(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit){
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            callbacks += utteranceId to callback

            implementation.enqueue(text, clearQueue)
        } else if (implementation is BlockingSynthesisHandler){
            isWarmingUp.value = false
            isSynthesizing.value = true

            implementation.enqueue(text, clearQueue)

            isWarmingUp.value = false
            isSynthesizing.value = false
        }
    }

    suspend fun enqueue(text: String, clearQueue: Boolean){
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            suspendCancellableCoroutine { cont ->
                continuations += utteranceId to cont

                implementation.enqueue(text, clearQueue)

                cont.invokeOnCancellation {
                    // TODO
                }
            }
        } else if (implementation is BlockingSynthesisHandler){
            isWarmingUp.value = false
            isSynthesizing.value = true
            coroutineScope {
                withContext(Dispatchers.Default) {
                    suspendCancellableCoroutine { cont ->
                        try {
                            implementation.enqueue(text, clearQueue)
                            cont.resume(Unit)
                        } catch (throwable: Throwable) {
                            cont.resumeWithException(throwable)
                        }

                        cont.invokeOnCancellation {
                            // TODO
                        }
                    }
                }

                isWarmingUp.value = false
                isSynthesizing.value = false
            }
        }
    }

    private fun onTtsStarted(utteranceId: Any) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utteranceId: Any, result: Result<Unit>) {
        isWarmingUp.value = false
        isSynthesizing.value = false

        callbacks.remove(utteranceId)?.invoke(result)
        continuations.remove(utteranceId)?.resumeWith(result)
    }
}
