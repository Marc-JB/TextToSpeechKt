package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

private const val VOICE_NAME = "kevin16"

class TextToSpeechInstanceDesktop(voiceManager: VoiceManager) : TextToSpeechInstance {
    private val voice = voiceManager.getVoice(VOICE_NAME)

    override val isSynthesizing = MutableStateFlow(false)

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

    override var pitch = 1f
        set(value) {
            voice.pitch = value
            field = value
        }

    override var rate = 1f
        set(value) {
            voice.rate = value
            field = value
        }

    private val defaultVoice = object : Voice {
        override val name = "Kevin"

        override val isDefault = true

        override val isOnline = false

        override val languageTag = voice.locale.toLanguageTag()

        override val language = voice.locale.language

        override val region = voice.locale.country
    }

    @Suppress("SetterBackingFieldAssignment")
    override var currentVoice: Voice? = defaultVoice
        set(_) {}

    override val voices: Flow<Set<Voice>> = flow {
        emit(setOf(defaultVoice))
    }

    init {
        voice.allocate()
    }

    override fun enqueue(text: String) {
        isSynthesizing.value = true
        voice.speak(text)
        isSynthesizing.value = false
    }

    override suspend fun say(text: String) {
        isSynthesizing.value = true
        coroutineScope {
            withContext(Dispatchers.Default) {
                voice.speak(text)
            }

            isSynthesizing.value = false
        }
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
