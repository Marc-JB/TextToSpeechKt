package nl.marc_apps.tts

import androidx.compose.runtime.*
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget

@ExperimentalDesktopTarget
@Composable
actual fun rememberTextToSpeechOrNull(): TextToSpeechInstance? {
    var textToSpeech by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeechFactory().createOrNull()
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.close()
        }
    }

    return textToSpeech
}
