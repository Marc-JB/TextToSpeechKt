package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.io.Serializable

@ExperimentalVoiceApi
actual interface Voice : CommonVoice, Serializable