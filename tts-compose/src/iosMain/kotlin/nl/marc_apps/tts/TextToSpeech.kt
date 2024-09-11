package nl.marc_apps.tts

import androidx.compose.runtime.*
import nl.marc_apps.tts.experimental.ExperimentalIOSTarget

@ExperimentalIOSTarget
@Composable
actual fun rememberTextToSpeechOrNull(requestedEngine: TextToSpeechEngine): TextToSpeechInstance? {
    var textToSpeech by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeechFactory().createOrNull()
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.close()
            textToSpeech = null
        }
    }

    return textToSpeech
}
