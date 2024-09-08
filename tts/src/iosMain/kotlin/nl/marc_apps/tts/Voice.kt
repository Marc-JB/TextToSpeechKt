package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalIosTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi

@ExperimentalVoiceApi
@ExperimentalIosTarget
actual interface Voice : CommonVoice
