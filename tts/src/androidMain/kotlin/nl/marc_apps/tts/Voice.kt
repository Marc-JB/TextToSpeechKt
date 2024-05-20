package nl.marc_apps.tts

import android.os.Parcelable
import java.io.Serializable
import java.util.*

actual interface Voice : CommonVoice, Parcelable, Serializable {
    val locale: Locale
}
