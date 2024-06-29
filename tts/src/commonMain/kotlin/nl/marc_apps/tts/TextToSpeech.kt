package nl.marc_apps.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.CallbackQueueHandler
import nl.marc_apps.tts.internal.EnqueueOptions
import nl.marc_apps.tts.internal.TextToSpeechHandler
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TextToSpeech(private val implementation: TextToSpeechHandler) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<Any, (Result<Unit>) -> Unit>()
    private val continuations = mutableMapOf<Any, Continuation<Unit>>()

    override val currentState = MutableStateFlow(TextToSpeechInstance.State.QUEUE_EMPTY)

    override val currentVolume = MutableStateFlow(TextToSpeechInstance.VOLUME_DEFAULT)
    override var volume: Int
        get() = currentVolume.value
        set(value) {
            currentVolume.value = when {
                value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                else -> value
            }
        }

    override val currentPitch = MutableStateFlow(TextToSpeechInstance.VOICE_PITCH_DEFAULT)
    override var pitch: Float
        get() = currentPitch.value
        set(value) {
            currentPitch.value = value
        }

    override val currentRate = MutableStateFlow(TextToSpeechInstance.VOICE_RATE_DEFAULT)
    override var rate: Float
        get() = currentRate.value
        set(value) {
            currentRate.value = value
        }

    override val voice: Voice?
        get() = implementation.voice

    override val voices: Sequence<Voice>
        get() = implementation.voices

    init {
        if (implementation is CallbackQueueHandler){
            implementation.registerListeners(::onTtsStarted, ::onTtsCompleted)
        }
    }

    private val currentOptions
        get() = EnqueueOptions(volume = volume, pitch = pitch, rate = rate)

    override fun say(text: String, callback: (Result<Unit>) -> Unit) {
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            callbacks += utteranceId to callback

            implementation.enqueue(text, utteranceId, currentOptions)
        } else if (implementation is BlockingSynthesisHandler){
            currentState.value = TextToSpeechInstance.State.SYNTHESIZING

            implementation.enqueue(text, currentOptions)

            currentState.value = TextToSpeechInstance.State.QUEUE_EMPTY
        }
    }

    override suspend fun say(text: String){
        if (implementation is CallbackQueueHandler){
            val utteranceId = implementation.createUtteranceId()

            suspendCancellableCoroutine { cont ->
                continuations += utteranceId to cont

                implementation.enqueue(text, utteranceId, currentOptions)

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
                            implementation.enqueue(text, currentOptions)
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

    override fun enqueue(text: String) = say(text) {}

    override fun plusAssign(text: String) = enqueue(text)

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
