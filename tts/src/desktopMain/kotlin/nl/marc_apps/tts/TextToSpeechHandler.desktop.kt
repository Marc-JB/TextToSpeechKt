package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.EnqueueOptions
import nl.marc_apps.tts.internal.TextToSpeechHandler

class TextToSpeechHandler(voiceManager: VoiceManager, private var voice: com.sun.speech.freetts.Voice) : TextToSpeechHandler, BlockingSynthesisHandler {
    private val defaultPitch = voice.pitch
    private val defaultRate = voice.rate

    override fun enqueue(text: String, options: EnqueueOptions) {
        if (options.clearQueue)
        {
            voice.outputQueue.removeAll()
        }
        voice.volume = options.volume / 100f
        voice.pitch = defaultPitch * options.pitch
        voice.rate = defaultRate * options.rate
        voice.speak(text)
    }

    override fun clearQueue() {
        voice.outputQueue.removeAll()
    }

    override fun close() {
        voice.deallocate()
    }
}
