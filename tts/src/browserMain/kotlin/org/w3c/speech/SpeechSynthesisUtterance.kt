package org.w3c.speech

import js_interop.EventTarget
import js_interop.JsAny

/**
 * The SpeechSynthesisUtterance interface of the Web Speech API represents a speech request.
 * It contains the content the speech service should read and
 * information about how to read it (e.g. language, pitch and volume.)
 */
expect class SpeechSynthesisUtterance : EventTarget {
    constructor()

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

    var onstart: ((event: JsAny?) -> Unit)?

    var onend: ((event: JsAny?) -> Unit)?
}
