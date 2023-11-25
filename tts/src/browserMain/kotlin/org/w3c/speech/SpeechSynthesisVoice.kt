package org.w3c.speech

import js_interop.JsAny

/**
 * The SpeechSynthesisVoice interface of the Web Speech API represents a voice that the system supports.
 * Every SpeechSynthesisVoice has its own relative speech service including information about language, name and URI.
 */
expect interface SpeechSynthesisVoice {
    /**
     * A [Boolean] indicating whether the voice is the default voice
     * for the current app language (true), or not (false.)
     */
    val default: Boolean

    /** Returns a BCP 47 language tag indicating the language of the voice. */
    val lang: String

    /**
     * A [Boolean] indicating whether the voice is supplied by
     * a local speech synthesizer service (true), or a remote speech synthesizer service (false.)
     */
    val localService: Boolean

    /** Returns a human-readable name that represents the voice. */
    val name: String

    /** Returns the type of URI and location of the speech synthesis service for this voice. */
    val voiceURI: String
}
