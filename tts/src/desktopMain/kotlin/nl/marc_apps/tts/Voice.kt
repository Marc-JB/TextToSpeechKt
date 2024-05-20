package nl.marc_apps.tts

import java.io.Serializable
import java.util.*

actual interface Voice : CommonVoice, Serializable {
    val locale: Locale
}
