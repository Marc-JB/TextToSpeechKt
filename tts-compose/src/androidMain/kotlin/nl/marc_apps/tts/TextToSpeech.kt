package nl.marc_apps.tts

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberTextToSpeechOrNull(): TextToSpeechInstance? {
    val context = LocalContext.current.applicationContext

    var textToSpeech by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeechFactory(context).createOrNull()
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.close()
        }
    }

    return textToSpeech
}
