package nl.marc_apps.tts

import android.os.Parcelable
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.io.Serializable

@ExperimentalVoiceApi
actual interface Voice : CommonVoice, Parcelable, Serializable
