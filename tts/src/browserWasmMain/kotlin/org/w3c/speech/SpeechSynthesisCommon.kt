package org.w3c.speech

actual external class Event

actual interface SpeechSynthesisCommon {
    actual val paused: Boolean

    actual val pending: Boolean

    actual val speaking: Boolean

    actual fun cancel()

    actual fun getVoices(): Array<SpeechSynthesisVoice>

    actual fun pause()

    actual fun resume()

    actual fun speak(utterance: SpeechSynthesisUtterance)

    actual var voiceschanged: ((event: Event?) -> Unit)?
}

class SpeechSynthesisImpl : SpeechSynthesisCommon {
    val actualImplementation = getBrowserSynthesis()

    override val paused: Boolean
        get() = actualImplementation.paused

    override val pending: Boolean
        get() = actualImplementation.pending

    override val speaking: Boolean
        get() = actualImplementation.speaking

    override fun cancel() = actualImplementation.cancel()

    override fun getVoices(): Array<SpeechSynthesisVoice> {
        return emptyArray()
    }

    override fun pause() = actualImplementation.pause()

    override fun resume() = actualImplementation.resume()

    override fun speak(utterance: SpeechSynthesisUtterance) = actualImplementation.speak(utterance)

    override var voiceschanged: ((event: Event?) -> Unit)? = null
}