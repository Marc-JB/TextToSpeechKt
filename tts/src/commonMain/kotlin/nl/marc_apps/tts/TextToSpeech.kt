package nl.marc_apps.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.CallbackQueueHandler
import nl.marc_apps.tts.internal.TextToSpeechHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TextToSpeech(private val implementation: TextToSpeechHandler) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<Any, (Result<Unit>) -> Unit>()
    private val continuations = mutableMapOf<Any, Continuation<Unit>>()

    override val currentState = MutableStateFlow(TextToSpeechInstance.State.QUEUE_EMPTY)

    override val volume: MutableStateFlow<Int>
        get() = TODO("Not yet implemented")
    override val pitch: MutableStateFlow<Float>
        get() = TODO("Not yet implemented")
    override val rate: MutableStateFlow<Float>
        get() = TODO("Not yet implemented")
    override val currentVoice: StateFlow<Voice?>
        get() = TODO("Not yet implemented")
    override val voices: Sequence<Voice>
        get() = TODO("Not yet implemented")

    init {
        if (implementation is CallbackQueueHandler){
            implementation.registerListeners(::onTtsStarted, ::onTtsCompleted)
        }
    }

    override fun say(text: String, callback: (Result<Unit>) -> Unit){
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            callbacks += utteranceId to callback

            implementation.enqueue(text, false, utteranceId)
        } else if (implementation is BlockingSynthesisHandler){
            currentState.value = TextToSpeechInstance.State.SYNTHESIZING

            implementation.enqueue(text, false)

            currentState.value = TextToSpeechInstance.State.QUEUE_EMPTY
        }
    }

    override suspend fun say(text: String){
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            suspendCancellableCoroutine { cont ->
                continuations += utteranceId to cont

                implementation.enqueue(text, false, utteranceId)

                cont.invokeOnCancellation {
                    // TODO
                }
            }
        } else if (implementation is BlockingSynthesisHandler){
            currentState.value = TextToSpeechInstance.State.SYNTHESIZING

            coroutineScope {
                withContext(Dispatchers.Default) {
                    suspendCancellableCoroutine { cont ->
                        try {
                            implementation.enqueue(text, false)
                            cont.resume(Unit)
                        } catch (throwable: Throwable) {
                            cont.resumeWithException(throwable)
                        }

                        cont.invokeOnCancellation {
                            // TODO
                        }
                    }
                }
            }

            currentState.value = TextToSpeechInstance.State.QUEUE_EMPTY
        }
    }

    override fun enqueue(text: String) {
        TODO("Not yet implemented")
    }

    override fun plusAssign(text: String) {
        TODO("Not yet implemented")
    }

    private fun onTtsStarted(utteranceId: Any) {
        currentState.value = TextToSpeechInstance.State.SYNTHESIZING
    }

    private fun onTtsCompleted(utteranceId: Any, result: Result<Unit>) {
        currentState.value = TextToSpeechInstance.State.QUEUE_EMPTY

        callbacks.remove(utteranceId)?.invoke(result)
        continuations.remove(utteranceId)?.resumeWith(result)
    }

    override fun stop() {
        callbacks.clear()
        continuations.clear()
        implementation.clearQueue()
    }

    override fun close() {
        implementation.close()
        callbacks.clear()
        continuations.clear()
    }
}
