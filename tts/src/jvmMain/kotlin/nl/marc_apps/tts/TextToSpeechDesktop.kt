package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.CallbackHandler
import nl.marc_apps.tts.utils.ResultHandler
import nl.marc_apps.tts.utils.SynthesisScope
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val VOICE_NAME = "kevin16"

@OptIn(ExperimentalUuidApi::class)
@ExperimentalDesktopTarget
internal class TextToSpeechDesktop(voiceManager: VoiceManager) : TextToSpeechInstance {
    private val supervisor = SupervisorJob()
    private val synthesisScope = SynthesisScope(supervisor)

    private val callbackHandler = CallbackHandler<Nothing?>()

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

    private fun enqueueInternal(text: String, clearQueue: Boolean, resultHandler: ResultHandler) {
        if (clearQueue) {
            stop()
        }

        val utteranceId = Uuid.random()

        callbackHandler.add(utteranceId, null, resultHandler)

        isSynthesizing.value = true
        synthesisScope.launch {
            voice.speak(text)
            onTtsCompleted(utteranceId, Result.success(Unit))
        }
    }

    private fun onTtsCompleted(utteranceId: Uuid, result: Result<Unit>) {
        isSynthesizing.value = false
        callbackHandler.onResult(utteranceId, result)
    }

    override fun enqueue(text: String, clearQueue: Boolean) {
        enqueueInternal(text, clearQueue, ResultHandler.Empty)
    }

    override suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean) {
        suspendCancellableCoroutine { cont ->
            enqueueInternal(text, clearQueue, ResultHandler.ContinuationHandler(cont))
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            }
        }
    }

    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        enqueueInternal(text, clearQueue, ResultHandler.CallbackHandler(callback))
    }

    override fun plusAssign(text: String) {
        enqueue(text)
    }

    override fun stop() {
        voice.outputQueue.removeAll()
        callbackHandler.onStopped()
    }

    override fun close() {
        voice.deallocate()
        supervisor.cancel()
        callbackHandler.clear()
    }
}
