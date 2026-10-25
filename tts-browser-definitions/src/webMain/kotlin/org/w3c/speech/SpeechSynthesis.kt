package org.w3c.speech

import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.EventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsArray

/**
 * The SpeechSynthesis interface of the Web Speech API is the controller interface for the speech service;
 * this can be used to retrieve information about the synthesis voices available on the device,
 * start and pause speech, and other commands besides.
 */
@ExperimentalWasmJsInterop
@Suppress("unused")
abstract external class SpeechSynthesis : JsAny {
    /** A [Boolean] that returns true if the SpeechSynthesis object is in a paused state. */
    val paused: Boolean

    /** A [Boolean] that returns true if the utterance queue contains as-yet-unspoken utterances. */
    val pending: Boolean

    /**
     * A [Boolean] that returns true if an utterance is currently
     * in the process of being spoken — even if SpeechSynthesis is in a paused state.
     */
    val speaking: Boolean

    /** Removes all utterances from the utterance queue. */
    fun cancel()

    /** Returns a list of [SpeechSynthesisVoice] objects representing all the available voices on the current device. */
    fun getVoices(): JsArray<SpeechSynthesisVoice>

    /** Puts the SpeechSynthesis object into a paused state. */
    fun pause()

    /** Puts the SpeechSynthesis object into a non-paused state: resumes it if it was already paused. */
    fun resume()

    /**
     * Adds an [utterance] to the utterance queue;
     * it will be spoken when any other utterances queued before it have been spoken.
     */
    fun speak(utterance: SpeechSynthesisUtterance)

    /**
     * Fired when the list of [SpeechSynthesisVoice] objects that would be returned
     * by the [getVoices] method has changed.
     */
    var onvoiceschanged: ((Event) -> Unit)??

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