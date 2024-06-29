package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.EnqueueOptions
import nl.marc_apps.tts.internal.TextToSpeechHandler

class TextToSpeechHandler(voiceManager: VoiceManager, private var tts: com.sun.speech.freetts.Voice) : TextToSpeechHandler, BlockingSynthesisHandler {
    private val defaultPitch = tts.pitch
    private val defaultRate = tts.rate

    private val defaultVoice = DesktopVoice(tts, true)

    override val voice: Voice = defaultVoice

    override val voices: Sequence<Voice> = voiceManager.voices.asSequence().map { DesktopVoice(it, it.name == defaultVoice.name) }

    override fun enqueue(text: String, options: EnqueueOptions) {
        if (options.clearQueue)
        {
            tts.outputQueue.removeAll()
        }
        tts.volume = options.volume / 100f
        tts.pitch = defaultPitch * options.pitch
        tts.rate = defaultRate * options.rate
        tts.speak(text)
    }

    override fun clearQueue() {
        tts.outputQueue.removeAll()
    }

    override fun close() {
        tts.deallocate()
    }
}
