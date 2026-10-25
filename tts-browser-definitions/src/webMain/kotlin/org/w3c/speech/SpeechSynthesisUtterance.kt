package org.w3c.speech

import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.EventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

/**
 * The SpeechSynthesisUtterance interface of the Web Speech API represents a speech request.
 * It contains the content the speech service should read and
 * information about how to read it (e.g. language, pitch and volume.)
 */
@ExperimentalWasmJsInterop
@Suppress("unused")
external class SpeechSynthesisUtterance() : JsAny {
    constructor(text: String)

    /** Gets and sets the language of the utterance. */
    var lang: String

    /** Gets and sets the pitch at which the utterance will be spoken at. */
    var pitch: Float

    /** Gets and sets the speed at which the utterance will be spoken at. */
    var rate: Float

    /** Gets and sets the text that will be synthesised when the utterance is spoken. */
    var text: String

    /** Gets and sets the voice that will be used to speak the utterance. */
    var voice: SpeechSynthesisVoice?

    /** Gets and sets the volume that the utterance will be spoken at. */
    var volume: Float

    /** Fired when the utterance has finished being spoken. */
    var onend: ((Event) -> Unit)??

    /** Fired when an error occurs that prevents the utterance from being successfully spoken. */
    var onerror: ((Event) -> Unit)??

    /** Fired when the utterance has begun to be spoken. */
    var onstart: ((Event) -> Unit)??

    fun addEventListener(type: String, callback: EventListener?, options: AddEventListenerOptions)
    fun addEventListener(type: String, callback: ((Event) -> Unit)?, options: AddEventListenerOptions)
    fun addEventListener(type: String, callback: EventListener?, options: Boolean)
    fun addEventListener(type: String, callback: ((Event) -> Unit)?, options: Boolean)
    fun addEventListener(type: String, callback: EventListener?)
    fun addEventListener(type: String, callback: ((Event) -> Unit)?)

    fun removeEventListener(type: String, callback: EventListener?, options: EventListenerOptions)
    fun removeEventListener(type: String, callback: ((Event) -> Unit)?, options: EventListenerOptions)
    fun removeEventListener(type: String, callback: EventListener?, options: Boolean)
    fun removeEventListener(type: String, callback: ((Event) -> Unit)?, options: Boolean)
    fun removeEventListener(type: String, callback: EventListener?)
    fun removeEventListener(type: String, callback: ((Event) -> Unit)?)

    fun dispatchEvent(event: Event): Boolean
}
