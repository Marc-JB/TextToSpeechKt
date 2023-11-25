package org.w3c.speech

import js_interop.EventTarget
import js_interop.JsAny

/**
 * The SpeechSynthesis interface of the Web Speech API is the controller interface for the speech service;
 * this can be used to retrieve information about the synthesis voices available on the device,
 * start and pause speech, and other commands besides.
 */
expect abstract class SpeechSynthesis {
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
     * Fired when the list of [SpeechSynthesisVoice] objects that would be returned by the [getVoices] method has changed.
     */
    var voiceschanged: ((event: JsAny?) -> Unit)?
}
