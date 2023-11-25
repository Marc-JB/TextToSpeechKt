package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.io.Serializable
import java.util.*

@ExperimentalVoiceApi
actual interface Voice : CommonVoice, Serializable {
    val locale: Locale
}