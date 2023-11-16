package js_interop

import org.w3c.speech.SpeechSynthesisVoice

external interface VoiceIterable {
    fun next(): Iteration

    interface Iteration {
        val value: SpeechSynthesisVoice

        val done: Boolean
    }
}