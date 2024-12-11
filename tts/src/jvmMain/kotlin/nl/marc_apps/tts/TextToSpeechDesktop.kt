package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val VOICE_NAME = "kevin16"

@ExperimentalDesktopTarget
internal class TextToSpeechDesktop(voiceManager: VoiceManager) : TextToSpeechInstance {
    private val voice = voiceManager.getVoice(VOICE_NAME)

    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(true)

    override val language = voice.locale.toLanguageTag()

    override var volume = 100
        set(value) {
            if (value in 0..100) {
                voice.volume = if(isMuted) 0f else value / 100f
                field = value
            }
        }

    override var isMuted = false
        set(value) {
            voice.volume = if(value) 0f else volume / 100f
            field = value
        }

    private val defaultPitch = voice.pitch

    override var pitch = 1f
        set(value) {
            voice.pitch = value * defaultPitch
            field = value
        }

    private val defaultRate = voice.rate

    override var rate = 1f
        set(value) {
            voice.rate = value * defaultRate
            field = value
        }

    @ExperimentalVoiceApi
    private val defaultVoice = object : Voice {
        override val name = "Kevin"

        override val isDefault = true

        override val isOnline = false

        override val languageTag = voice.locale.toLanguageTag()

        override val language = voice.locale.displayLanguage

        override val region = voice.locale.displayCountry

        override val locale = voice.locale
    }

    @ExperimentalVoiceApi
    @Suppress("SetterBackingFieldAssignment")
    override var currentVoice: Voice? = defaultVoice
        set(_) {
            // Ignored: Not supported yet
        }

    @ExperimentalVoiceApi
    override val voices: Sequence<Voice> = sequence {
        yield(defaultVoice)
    }

    init {
        voice.allocate()
        isWarmingUp.value = false;
    }

    override fun enqueue(text: String, clearQueue: Boolean) {
        isSynthesizing.value = true
        voice.speak(text)
        isSynthesizing.value = false
    }

    override suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean) {
        isSynthesizing.value = true
        coroutineScope {
            withContext(Dispatchers.Default) {
                suspendCancellableCoroutine { cont ->
                    try {
                        voice.speak(text)
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

    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        isSynthesizing.value = true
        voice.speak(text)
        isSynthesizing.value = false
        callback(Result.success(Unit))
    }

    override fun plusAssign(text: String) {
        enqueue(text)
    }

    override fun stop() {
        voice.outputQueue.removeAll()
    }

    override fun close() {
        voice.deallocate()
    }
}
