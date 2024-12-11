@file:Suppress("unused")

package nl.marc_apps.tts

import js_interop.Window
import js_interop.getSpeechSynthesis
import js_interop.getVoiceList
import js_interop.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.errors.UnknownTextToSpeechSynthesisError
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.CallbackHandler
import nl.marc_apps.tts.utils.ResultHandler
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class TextToSpeechBrowser(context: Window = window) : TextToSpeechInstance {
    private val callbackHandler = CallbackHandler<Nothing?>()

    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private val speechSynthesis: SpeechSynthesis = getSpeechSynthesis(context)

    private var speechSynthesisUtterance = SpeechSynthesisUtterance()

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    override var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            field = when {
                value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                else -> value
            }
            speechSynthesisUtterance.volume = internalVolume
        }

    override var isMuted = false
        set(value) {
            field = value
            speechSynthesisUtterance.volume = internalVolume
        }

    override var pitch = TextToSpeechInstance.VOICE_PITCH_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.pitch = value
        }

    override var rate = TextToSpeechInstance.VOICE_RATE_DEFAULT
        set(value) {
            field = value
            speechSynthesisUtterance.rate = value
        }

    private val voiceList by lazy {
        getVoiceList(speechSynthesis)
    }

    override val language: String
        get() {
            val reportedLanguage = speechSynthesisUtterance.voice?.lang ?: speechSynthesisUtterance.lang
            return reportedLanguage.ifBlank {
                val defaultLanguage = voiceList.find { it.default }?.lang
                if (defaultLanguage.isNullOrBlank()) "Unknown" else defaultLanguage
            }
        }

    @ExperimentalVoiceApi
    private val defaultVoice by lazy {
        voiceList.find { it.default }?.let { BrowserVoice(it) }
    }

    @ExperimentalVoiceApi
    override var currentVoice: Voice? = null
        get() = field ?: defaultVoice
        set(value) {
            if (value is BrowserVoice) {
                speechSynthesisUtterance.voice = value.browserVoice
                field = value
            }
        }

    @ExperimentalVoiceApi
    override val voices: Sequence<Voice> by lazy {
        voiceList.asSequence().map { BrowserVoice(it) }
    }

    @OptIn(ExperimentalVoiceApi::class)
    private fun resetCurrentUtterance() {
        speechSynthesisUtterance = SpeechSynthesisUtterance().also {
            it.volume = internalVolume
            it.pitch = pitch
            it.rate = rate
            it.voice = (currentVoice as? BrowserVoice)?.browserVoice
        }
    }

    override fun enqueue(text: String, clearQueue: Boolean) {
        enqueueInternal(text, clearQueue, ResultHandler.Empty)
    }

    private fun enqueueInternal(text: String, clearQueue: Boolean, resultHandler: ResultHandler) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) {
                stop()
            }
            resultHandler.setResult(Result.success(Unit))
            return
        }

        if (!hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        val utteranceId = Uuid.random()

        callbackHandler.add(utteranceId, null, resultHandler)

        speechSynthesisUtterance.onstart = { onTtsStarted(utteranceId) }
        speechSynthesisUtterance.onend = { onTtsCompleted(utteranceId, Result.success(Unit)) }

        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)

        resetCurrentUtterance()
    }

    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        enqueueInternal(text, clearQueue, ResultHandler.CallbackHandler(callback))
    }

    private fun onTtsStarted(utteranceId: Uuid) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utteranceId: Uuid, result: Result<Unit>) {
        isWarmingUp.value = false
        isSynthesizing.value = false
        callbackHandler.onResult(utteranceId, result)
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

    override fun plusAssign(text: String) {
        enqueue(text, false)
    }

    override fun stop() {
        speechSynthesis.cancel()
        callbackHandler.onStopped()
    }

    override fun close() {
        stop()
        callbackHandler.clear()
    }
}
