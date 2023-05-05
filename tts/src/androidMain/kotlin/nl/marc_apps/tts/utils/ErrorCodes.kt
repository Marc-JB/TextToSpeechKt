package nl.marc_apps.tts.utils

import nl.marc_apps.tts.errors.*

object ErrorCodes {
    /** Denotes a failure of a TTS engine to synthesize the given input. */
    const val ERROR_SYNTHESIS = -3

    /** Denotes a failure of a TTS service. */
    const val ERROR_SERVICE = -4

    /** Denotes a failure related to the output (audio device or a file). */
    const val ERROR_OUTPUT = -5

    /** Denotes a failure caused by a network connectivity problems. */
    const val ERROR_NETWORK = -6

    /** Denotes a failure caused by network timeout.*/
    const val ERROR_NETWORK_TIMEOUT = -7

    /** Denotes a failure caused by an invalid request. */
    const val ERROR_INVALID_REQUEST = -8

    /** Denotes a failure caused by an unfinished download of the voice data. */
    const val ERROR_NOT_INSTALLED_YET = -9

    /**
     * @hide
     */
    fun mapToThrowable(errorCode: Int): TextToSpeechSynthesisError {
        return when(errorCode) {
            ErrorCodes.ERROR_SYNTHESIS -> TextToSpeechFlawedTextInputError()
            ErrorCodes.ERROR_SERVICE -> TextToSpeechServiceFailureError()
            ErrorCodes.ERROR_OUTPUT -> DeviceAudioOutputError()
            ErrorCodes.ERROR_NETWORK -> NetworkConnectivityError()
            ErrorCodes.ERROR_NETWORK_TIMEOUT -> NetworkTimeoutError()
            ErrorCodes.ERROR_INVALID_REQUEST -> TextToSpeechRequestInvalidError()
            ErrorCodes.ERROR_NOT_INSTALLED_YET -> TextToSpeechEngineUnavailableError()
            else -> UnknownTextToSpeechSynthesisError()
        }
    }
}