package nl.marc_apps.tts

import android.content.Intent

object TextToSpeechSystemSettings {
    const val ACTION_TTS_SETTINGS = "com.android.settings.TTS_SETTINGS"

    fun getIntent() = Intent(ACTION_TTS_SETTINGS)
}