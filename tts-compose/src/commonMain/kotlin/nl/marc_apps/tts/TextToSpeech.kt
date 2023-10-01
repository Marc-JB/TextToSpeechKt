package nl.marc_apps.tts

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTextToSpeechOrNull(): TextToSpeechInstance?
