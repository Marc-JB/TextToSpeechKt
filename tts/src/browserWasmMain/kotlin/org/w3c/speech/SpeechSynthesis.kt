package org.w3c.speech

import js_interop.Event
import js_interop.EventListener
import js_interop.EventTargetCommon

actual class SpeechSynthesis : EventTargetCommon() {
    private val actualImplementation = getBrowserSynthesis()

    actual val paused: Boolean
        get() = actualImplementation.paused

    actual val pending: Boolean
        get() = actualImplementation.pending

    actual val speaking: Boolean
        get() = actualImplementation.speaking

    actual fun cancel(){
        actualImplementation.cancel()
    }

    actual fun getVoices(): Array<SpeechSynthesisVoice> {
        val list = mutableListOf<SpeechSynthesisVoice>()
        val voices = getBrowserSynthesisVoices()
        do {
            val iteration = voices.next()
            list += iteration.value
        } while (!iteration.done)
        return list.toTypedArray()
    }

    actual fun pause(){
        actualImplementation.pause()
    }

    actual fun resume(){
        actualImplementation.resume()
    }

    actual fun speak(utterance: SpeechSynthesisUtterance) {
        actualImplementation.speak(utterance)
    }

    actual var voiceschanged: ((event: Event?) -> Unit)? = null

    override fun addEventListener(type: String, callback: EventListener?) {
        actualImplementation.addEventListener(type, callback)
    }

    override fun removeEventListener(type: String, callback: EventListener?) {
        actualImplementation.removeEventListener(type, callback)
    }
}