package nl.marc_apps.tts.errors

/** Error that is thrown when creating a [nl.marc_apps.tts.TextToSpeechInstance] fails. */
sealed class TextToSpeechInitialisationError(
    message: String? = "Error while trying to load Text-to-Speech service",
    cause: Throwable? = null
) : Exception(message, cause)

/** Error that is thrown when creating a [nl.marc_apps.tts.TextToSpeechInstance] fails. */
class UnknownTextToSpeechInitialisationError(
    cause: Throwable? = null
) : TextToSpeechInitialisationError(cause = cause)

/** Error that is thrown when a platform does not have TTS support */
class TextToSpeechNotSupportedError(
    cause: Throwable? = null
) : TextToSpeechInitialisationError("Text-to-Speech is not supported on this platform", cause)

/** Error that is thrown on some Samsung devices while creating a TTS instance. */
class TextToSpeechSecurityError(
    cause: Throwable? = null
) : TextToSpeechInitialisationError("Text-to-Speech security error", cause)
