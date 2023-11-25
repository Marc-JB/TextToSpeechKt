package js_interop

import org.w3c.speech.SpeechSynthesis

actual fun getVoiceList(speechSynthesis: SpeechSynthesis) = speechSynthesis.getVoices()
