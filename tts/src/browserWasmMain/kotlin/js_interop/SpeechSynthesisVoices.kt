package js_interop

import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisVoice

/** @hide */
actual fun getVoiceList(speechSynthesis: SpeechSynthesis) = speechSynthesis.getVoices().toArray()
