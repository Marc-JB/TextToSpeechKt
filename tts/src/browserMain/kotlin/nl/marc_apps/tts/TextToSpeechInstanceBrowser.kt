package nl.marc_apps.tts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextToSpeechInstanceBrowser(
    private val speechSynthesis: SpeechSynthesis
) : TextToSpeechInstance {
    override val isSynthesizing = MutableStateFlow(false)

    override var volume = 100
        set(value) {
            if (value in 0..100) {
                field = value
            }
        }

    override var isMuted = false

    override var pitch = 1f

    override var rate = 1f

    private val defaultVoice = (SpeechSynthesisUtterance().voice ?: speechSynthesis.getVoices().find { it.default })?.let { BrowserVoice(it) }

    override var currentVoice: Voice? = defaultVoice
        set(value) {
            (value as? BrowserVoice)?.let {
                field = it
            }
        }

    override val voices: Flow<Set<Voice>> = flow {
        emit(speechSynthesis.getVoices().map { BrowserVoice(it) }.toSet())
    }

    override fun enqueue(text: String) {
        val synthesisUtterance = SpeechSynthesisUtterance(text)

        (currentVoice as? BrowserVoice)?.let { synthesisUtterance.voice = it.browserVoice }

        synthesisUtterance.volume = if (isMuted) 0f else volume / 100f
        synthesisUtterance.pitch = pitch
        synthesisUtterance.rate = rate
        synthesisUtterance.onstart = {
            isSynthesizing.value = true
        }
        synthesisUtterance.onend = {
            isSynthesizing.value = false
        }

        speechSynthesis.speak(synthesisUtterance)
    }

    override suspend fun say(text: String) {
        suspendCoroutine { continuation ->
            val synthesisUtterance = SpeechSynthesisUtterance(text)

            (currentVoice as? BrowserVoice)?.let { synthesisUtterance.voice = it.browserVoice }

            synthesisUtterance.volume = if (isMuted) 0f else volume / 100f
            synthesisUtterance.pitch = pitch
            synthesisUtterance.rate = rate
            synthesisUtterance.onstart = {
                isSynthesizing.value = true
            }
            synthesisUtterance.onend = {
                isSynthesizing.value = false
                continuation.resume(Unit)
            }

            speechSynthesis.speak(synthesisUtterance)
        }
    }

    override fun plusAssign(text: String) {
        enqueue(text)
    }

    override fun stop() {
        speechSynthesis.cancel()
    }

    override fun close() {
        speechSynthesis.cancel()
    }
}
