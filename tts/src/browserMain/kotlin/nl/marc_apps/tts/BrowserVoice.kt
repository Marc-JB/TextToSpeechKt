package nl.marc_apps.tts

import org.w3c.speech.SpeechSynthesisVoice

internal data class BrowserVoice(
    override val name: String,
    override val isDefault: Boolean,
    override val isOnline: Boolean,
    override val languageTag: String,
    override val language: String,
    override val region: String?,
    val browserVoice: SpeechSynthesisVoice
) : Voice {
    constructor(voice: SpeechSynthesisVoice) : this(
        voice.name,
        voice.default,
        !voice.localService,
        voice.lang,
        voice.lang.substringBefore("-"),
        if("-" in voice.lang) voice.lang.substringAfter("-") else null,
        voice
    )
}
