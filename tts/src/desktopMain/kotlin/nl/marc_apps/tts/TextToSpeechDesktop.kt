package nl.marc_apps.tts

import com.sun.speech.freetts.FreeTTSSpeakableImpl
import com.sun.speech.freetts.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class TextToSpeechDesktop(voiceManager: VoiceManager, private var voice: com.sun.speech.freetts.Voice) {
    val isSynthesizing = MutableStateFlow(false)

    val isWarmingUp = MutableStateFlow(false)

    var volume = 100
        set(value) {
            if (value in 0..100) {
                voice.volume = value / 100f
                field = value
            }
        }

    private val defaultPitch = voice.pitch

    var pitch = 1f
        set(value) {
            voice.pitch = value * defaultPitch
            field = value
        }

    private val defaultRate = voice.rate

    var rate = 1f
        set(value) {
            voice.rate = value * defaultRate
            field = value
        }

    private val defaultVoice = DesktopVoice(voice, true)

    var currentVoice: Voice? = defaultVoice
        set(newVoice) {
            if (newVoice is DesktopVoice) {
                isWarmingUp.value = true
                voice.deallocate()
                voice = newVoice.desktopVoice
                field = newVoice
                newVoice.desktopVoice.allocate()
                isWarmingUp.value = false
            }
        }

    val voices: Sequence<Voice> = voiceManager.voices.asSequence().map { DesktopVoice(it, it.name == defaultVoice.name) }

    fun enqueue(text: String, clearQueue: Boolean) {
        isSynthesizing.value = true
        voice.speak(text)
        isSynthesizing.value = false
    }

    suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean) {
        isSynthesizing.value = true
        coroutineScope {
            withContext(Dispatchers.Default) {
                suspendCancellableCoroutine { cont ->
                    try {
                        voice.speak(FreeTTSSpeakableImpl(text))
                        cont.resume(Unit)
                    } catch (throwable: Throwable) {
                        cont.resumeWithException(throwable)
                    }
                    cont.invokeOnCancellation {
                        if (clearQueueOnCancellation) {
                            stop()
                        }
                    }
                }
            }

            isSynthesizing.value = false
        }
    }

    fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        isSynthesizing.value = true
        voice.speak(text)
        isSynthesizing.value = false
        callback(Result.success(Unit))
    }

    fun stop() {
        voice.outputQueue.removeAll()
    }

    fun close() {
        voice.deallocate()
    }
}
