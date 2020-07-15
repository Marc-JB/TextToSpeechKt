package nl.marc.tts

import java.io.Closeable

actual interface TextToSpeechInstance : Closeable {
    actual var volume: Int

    actual var isMuted: Boolean

    actual fun say(text: String, clearQueue: Boolean)

    actual fun stop()

    actual override fun close()
}
