package nl.marc_apps.tts

import org.w3c.speech.SpeechSynthesisVoice

internal data class BrowserVoice(
    val browserVoice: SpeechSynthesisVoice
) : Voice {
    override val name: String = browserVoice.name
    override val isDefault: Boolean = browserVoice.default
    override val isOnline: Boolean = !browserVoice.localService
    override val languageTag: String = browserVoice.lang
    override val language: String = browserVoice.lang.substringBefore("-")
    override val region: String? = if("-" in browserVoice.lang) browserVoice.lang.substringAfter("-") else null
}
