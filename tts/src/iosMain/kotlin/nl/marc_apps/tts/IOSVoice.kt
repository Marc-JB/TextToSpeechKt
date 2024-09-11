package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalIOSTarget
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import platform.AVFAudio.AVSpeechSynthesisVoice

@ExperimentalVoiceApi
@ExperimentalIOSTarget
internal data class IOSVoice constructor(
    override val name: String,
    override val isDefault: Boolean,
    override val isOnline: Boolean,
    override val languageTag: String,
    override val language: String,
    override val region: String?,
    val iosVoice: AVSpeechSynthesisVoice
) : Voice {
    constructor(voice: AVSpeechSynthesisVoice, isDefault: Boolean) : this(
        voice.name,
        isDefault,
        false,
        voice.language,
        voice.language.substringBefore("-"),
        if("-" in voice.language) voice.language.substringAfter("-") else null,
        voice
    )
}