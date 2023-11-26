package org.w3c.speech

import js_interop.JsAny
import org.w3c.dom.events.EventTarget

/**
 * The SpeechSynthesisUtterance interface of the Web Speech API represents a speech request.
 * It contains the content the speech service should read and
 * information about how to read it (e.g. language, pitch and volume.)
 */
actual external class SpeechSynthesisUtterance : EventTarget {
    actual constructor()

    actual constructor(text: String)

    /** Gets and sets the language of the utterance. */
    actual var lang: String

    /** Gets and sets the pitch at which the utterance will be spoken at. */
    actual var pitch: Float

    /** Gets and sets the speed at which the utterance will be spoken at. */
    actual var rate: Float

    /** Gets and sets the text that will be synthesised when the utterance is spoken. */
    actual var text: String

    /** Gets and sets the voice that will be used to speak the utterance. */
    actual var voice: SpeechSynthesisVoice?

    /** Gets and sets the volume that the utterance will be spoken at. */
    actual var volume: Float

    actual var onstart: ((event: JsAny?) -> Unit)?

    actual var onend: ((event: JsAny?) -> Unit)?
}
