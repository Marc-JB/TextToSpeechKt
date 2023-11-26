package nl.marc_apps.tts

import android.os.Parcelable
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.io.Serializable
import java.util.*

@ExperimentalVoiceApi
actual interface Voice : CommonVoice, Parcelable, Serializable {
    val locale: Locale
}