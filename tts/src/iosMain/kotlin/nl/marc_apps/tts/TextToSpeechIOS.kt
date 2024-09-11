package nl.marc_apps.tts

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.experimental.ExperimentalIOSTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.TtsProgressConverter
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate
import platform.Foundation.mutableArrayValueForKeyPath
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@ExperimentalIOSTarget
internal class TextToSpeechIOS(private val synthesizer: AVSpeechSynthesizer) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<AVSpeechUtterance, (Result<Unit>) -> Unit>()

    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    override var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            field = when {
                value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                else -> value
            }
        }

    override var isMuted: Boolean = false

    override var pitch: Float = TextToSpeechInstance.VOICE_PITCH_DEFAULT

    override var rate: Float = TextToSpeechInstance.VOICE_RATE_DEFAULT

    override val language: String
        get() = AVSpeechSynthesisVoice.currentLanguageCode()

    @ExperimentalVoiceApi
    private val defaultVoice: Voice? = AVSpeechUtterance().voice?.let {
        IOSVoice(it, true)
    } ?: AVSpeechSynthesisVoice.speechVoices().map { it as AVSpeechSynthesisVoice }.firstOrNull {
        it.language == AVSpeechSynthesisVoice.currentLanguageCode()
    }?.let { IOSVoice(it, true) }

    @ExperimentalVoiceApi
    override var currentVoice: Voice? = defaultVoice

    @ExperimentalVoiceApi
    override val voices: Sequence<Voice> = AVSpeechSynthesisVoice.speechVoices().asSequence().map {
        IOSVoice(it as AVSpeechSynthesisVoice, it == (defaultVoice as? IOSVoice)?.iosVoice)
    }

    private val delegate = TtsProgressConverter(::onTtsStarted, ::onTtsCompleted)

    init {
        synthesizer.delegate = delegate
    }

    private fun onTtsStarted(utterance: AVSpeechUtterance) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utterance: AVSpeechUtterance, result: Result<Unit>) {
        isWarmingUp.value = false
        isSynthesizing.value = false
        callbacks[utterance]?.invoke(result)
    }

    override fun enqueue(text: String, clearQueue: Boolean) {
        say(text, clearQueue) {}
    }

    @OptIn(ExperimentalVoiceApi::class)
    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        if(isMuted || volume == 0) {
            if(clearQueue) stop()
            callback(Result.success(Unit))
            return
        }

        if(clearQueue) {
            stop()
        }

        val utterance = AVSpeechUtterance(string = text)
        utterance.volume = internalVolume
        utterance.rate = rate * AVSpeechUtteranceDefaultSpeechRate
        utterance.pitchMultiplier = pitch
        val voice = currentVoice
        if (voice is IOSVoice) {
            utterance.voice = voice.iosVoice
        }

        val localCallback: (Result<Unit>) -> Unit = {
            callback(it)
            callbacks.remove(utterance)
        }
        callbacks += utterance to localCallback

        if (!hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        synthesizer.speakUtterance(utterance)
    }

    override suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean) {
        suspendCancellableCoroutine { cont ->
            say(text, clearQueue) {
                if (it.isSuccess) {
                    cont.resume(Unit)
                } else if (it.isFailure) {
                    val error = it.exceptionOrNull() ?: Exception()
                    cont.resumeWithException(error)
                }
            }
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            }
        }
    }

    override fun plusAssign(text: String) {
        enqueue(text, clearQueue = false)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun close() {
        stop()
        synthesizer.setDelegate(null)
        callbacks.clear()
    }
}