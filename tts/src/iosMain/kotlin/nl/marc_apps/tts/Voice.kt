package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalIOSTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi

@ExperimentalVoiceApi
@ExperimentalIOSTarget
actual interface Voice : CommonVoice
