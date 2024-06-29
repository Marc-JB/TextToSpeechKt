package nl.marc_apps.tts

import js_interop.Window
import js_interop.getSpeechSynthesis
import js_interop.window
import nl.marc_apps.tts.internal.CallbackQueueHandler
import nl.marc_apps.tts.internal.EnqueueOptions
import nl.marc_apps.tts.internal.TextToSpeechHandler
import org.w3c.speech.SpeechSynthesis
import org.w3c.speech.SpeechSynthesisUtterance
import kotlin.random.Random

class TextToSpeechHandler(context: Window = window) : TextToSpeechHandler, CallbackQueueHandler {
    private val speechSynthesis: SpeechSynthesis = getSpeechSynthesis(context)

    private var onStart: ((Any) -> Unit)? = null
    private var onComplete: ((Any, Result<Unit>) -> Unit)? = null

    override fun createUtteranceId(): Any = Random.Default.nextLong()

    override fun enqueue(text: String, utteranceId: Any, options: EnqueueOptions) {
        val speechSynthesisUtterance = SpeechSynthesisUtterance()

        speechSynthesisUtterance.onstart = {
            onStart?.invoke(utteranceId)
        }

        speechSynthesisUtterance.onend = {
            onComplete?.invoke(utteranceId, Result.success(Unit))
        }

        speechSynthesisUtterance.text = text
        speechSynthesisUtterance.volume = options.volume / 100f
        speechSynthesis.speak(speechSynthesisUtterance)
    }

    override fun registerListeners(onStart: (Any) -> Unit, onComplete: (Any, Result<Unit>) -> Unit) {
        this.onStart = onStart
        this.onComplete = onComplete
    }

    override fun clearQueue() {
        speechSynthesis.cancel()
    }

    override fun close() {
        speechSynthesis.cancel()
    }
}
