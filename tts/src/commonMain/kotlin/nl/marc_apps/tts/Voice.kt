package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalVoiceApi

/**
 * @hide
 */
interface CommonVoice {
    val name: String
    val isDefault: Boolean
    val isOnline: Boolean
    val languageTag: String
    val language: String
    val region: String?
}

@ExperimentalVoiceApi
expect interface Voice : CommonVoice