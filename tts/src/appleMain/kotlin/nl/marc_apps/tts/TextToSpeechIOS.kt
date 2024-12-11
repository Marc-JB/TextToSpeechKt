package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.ResultHandler
import nl.marc_apps.tts.utils.TtsProgressConverter
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class TextToSpeechIOS(private val synthesizer: AVSpeechSynthesizer) : TextToSpeech<AVSpeechUtterance>() {
    override val canDetectSynthesisStarted = true

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

    private val delegate = TtsProgressConverter(callbackHandler, ::onTtsStarted, ::onTtsCompleted)

    init {
        synthesizer.delegate = delegate
    }

    @OptIn(ExperimentalVoiceApi::class)
    override fun enqueueInternal(text: String, resultHandler: ResultHandler) {
        val utterance = AVSpeechUtterance(string = text)
        utterance.volume = internalVolume
        utterance.rate = rate * AVSpeechUtteranceDefaultSpeechRate
        utterance.pitchMultiplier = pitch
        val voice = currentVoice
        if (voice is IOSVoice) {
            utterance.voice = voice.iosVoice
        }

        callbackHandler.add(Uuid.random(), utterance, resultHandler)

        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
        super.stop()
    }

    override fun close() {
        super.close()
        synthesizer.setDelegate(null)
    }
}