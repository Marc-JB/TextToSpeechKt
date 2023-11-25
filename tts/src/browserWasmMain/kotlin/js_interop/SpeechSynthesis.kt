package js_interop

import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisVoice

actual fun getVoiceList(speechSynthesis: SpeechSynthesis) : Array<SpeechSynthesisVoice> {
    return speechSynthesis.getVoices().iterator().asSequence().filterNotNull().toList().toTypedArray()
}
