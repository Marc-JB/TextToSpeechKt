package js_interop

import org.w3c.speech.SpeechSynthesis

/** @hide */
actual fun getVoiceList(speechSynthesis: SpeechSynthesis) = speechSynthesis.getVoices()
