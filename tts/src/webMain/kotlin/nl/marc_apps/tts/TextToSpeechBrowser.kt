@file:Suppress("unused")

package nl.marc_apps.tts

import kotlinx.browser.window
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.ResultHandler
import org.w3c.dom.Window
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import org.w3c.speech.speechSynthesis
import kotlin.Float
import kotlin.Int
import kotlin.Nothing
import kotlin.OptIn
import kotlin.Result
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.also
import kotlin.collections.asSequence
import kotlin.collections.find
import kotlin.getValue
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toList
import kotlin.lazy
import kotlin.let
import kotlin.sequences.Sequence
import kotlin.sequences.map
import kotlin.text.ifBlank
import kotlin.text.isNullOrBlank
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalWasmJsInterop::class)
internal class TextToSpeechBrowser(context: Window = window) : TextToSpeech<Nothing?>() {
    override val canDetectSynthesisStarted = true

    private val speechSynthesis: SpeechSynthesis = context.speechSynthesis

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
        speechSynthesis.getVoices().toList()
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

    override fun enqueueInternal(text: String, resultHandler: ResultHandler) {
        val utteranceId = Uuid.random()

        callbackHandler.add(utteranceId, null, resultHandler)

        speechSynthesisUtterance.addEventListener("onstart") {
            onTtsStarted(utteranceId)
        }

        speechSynthesisUtterance.addEventListener("onend") {
            onTtsCompleted(utteranceId, Result.success(Unit))
        }

        speechSynthesisUtterance.text = text
        speechSynthesis.speak(speechSynthesisUtterance)

        resetCurrentUtterance()
    }

    override fun stop() {
        speechSynthesis.cancel()
        super.stop()
    }

    override fun close() {
        super.close()
        callbackHandler.clear()
    }
}
