package org.w3c.speech

/**
 * The SpeechSynthesisVoice interface of the Web Speech API represents a voice that the system supports.
 * Every SpeechSynthesisVoice has its own relative speech service including information about language, name and URI.
 */
actual external interface SpeechSynthesisVoice : JsAny {
    /**
     * A [Boolean] indicating whether the voice is the default voice
     * for the current app language (true), or not (false.)
     */
    actual val default: Boolean

    /** Returns a BCP 47 language tag indicating the language of the voice. */
    actual val lang: String

    /**
     * A [Boolean] indicating whether the voice is supplied by
     * a local speech synthesizer service (true), or a remote speech synthesizer service (false.)
     */
    actual val localService: Boolean

    /** Returns a human-readable name that represents the voice. */
    actual val name: String

    /** Returns the type of URI and location of the speech synthesis service for this voice. */
    actual val voiceURI: String
}
