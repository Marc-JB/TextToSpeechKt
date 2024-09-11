package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.rememberTextToSpeechOrNull

@Composable
fun TtsDemoApp(topAppBar: @Composable () -> Unit = {}) {
    val textToSpeech = rememberTextToSpeechOrNull(TextToSpeechEngine.Google)

    AppTheme {
        Scaffold(
            topBar = topAppBar,
            modifier = Modifier.fillMaxSize()
        ) {
            TtsDemoView(textToSpeech = textToSpeech, paddingValues = it)
        }
    }
}
