package nl.marc_apps.tts

import android.os.Parcelable
import java.io.Serializable

actual interface Voice : CommonVoice, Parcelable, Serializable
