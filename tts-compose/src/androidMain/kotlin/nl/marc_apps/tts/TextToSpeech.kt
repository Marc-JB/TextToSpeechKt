package nl.marc_apps.tts

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberTextToSpeechOrNull(requestedEngine: TextToSpeechEngine): TextToSpeechInstance? {
    val context = LocalContext.current.applicationContext

    var textToSpeech by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech = TextToSpeechFactory(context, requestedEngine.androidPackage).createOrNull()
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.close()
            textToSpeech = null
        }
    }

    return textToSpeech
}
