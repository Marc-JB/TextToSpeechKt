package nl.marc_apps.tts

import com.sun.speech.freetts.VoiceManager
import nl.marc_apps.tts.internal.BlockingSynthesisHandler
import nl.marc_apps.tts.internal.TextToSpeechHandler

class TextToSpeechHandler(voiceManager: VoiceManager, private var voice: com.sun.speech.freetts.Voice) : TextToSpeechHandler, BlockingSynthesisHandler {
    override fun enqueue(text: String, clearQueue: Boolean) {
        if (clearQueue)
        {
            voice.outputQueue.removeAll()
        }
        voice.speak(text)
    }

    override fun close() {
        voice.deallocate()
    }
}
