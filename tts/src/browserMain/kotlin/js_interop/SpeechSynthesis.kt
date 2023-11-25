package js_interop

import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisVoice

expect fun getVoiceList(speechSynthesis: SpeechSynthesis): Array<SpeechSynthesisVoice>
