package nl.marc_apps.tts.errors

/** Error that is thrown when synthesising text input fails. */
sealed class TextToSpeechSynthesisError(
    message: String? = "Error while trying to synthesise text input",
    cause: Throwable? = null
) : Exception(message, cause)

/** Error that is thrown when synthesising text input fails. */
class UnknownTextToSpeechSynthesisError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError(cause = cause)

/** Error that is thrown when synthesising text input failed, usually when stop() or close() are called. */
class TextToSpeechSynthesisInterruptedError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("TTS synthesis was interrupted by a call to stop() or close()", cause)

/** Error that is thrown when synthesising text input fails, because the TTS Engine can't handle the provided text input. */
class TextToSpeechFlawedTextInputError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS engine can't synthesise the provided text input", cause)

/** Error that is thrown when synthesising text input fails, because the TTS Engine crashed. */
class TextToSpeechServiceFailureError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS engine crashed while processing the request", cause)

/** Error that is thrown when synthesising text input fails, because something is wrong with the device audio output. */
class DeviceAudioOutputError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("TTS synthesis unavailable due to device audio output error", cause)

/** Error that is thrown when synthesising text input fails, because something is wrong with the network. */
class NetworkConnectivityError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS engine requires network access, but this was not available", cause)

/** Error that is thrown when synthesising text input fails, because something is wrong with the network. */
class NetworkTimeoutError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS engine requires network access, but the network timed out", cause)

/** Error that is thrown when synthesising text input fails, because the TTS request was invalid. */
class TextToSpeechRequestInvalidError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS request was invalid", cause)

/** Error that is thrown when synthesising text input fails, because the TTS engine has not been installed (yet). */
class TextToSpeechEngineUnavailableError(
    cause: Throwable? = null
) : TextToSpeechSynthesisError("The TTS engine that should handle this request has not been installed (yet)", cause)
