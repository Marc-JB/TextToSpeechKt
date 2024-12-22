package js_interop

import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisVoice

/** @hide */
actual fun getVoiceList(speechSynthesis: SpeechSynthesis) = speechSynthesis.getVoices().toArray()

/**
 * Returns a new [Array] containing all the elements of this [JsArray].
 */
private fun <T : JsAny?> JsArray<T>.toArray(): Array<T> {
    @Suppress("UNCHECKED_CAST", "TYPE_PARAMETER_AS_REIFIED")
    return Array(this.length) { this[it] as T }
}
