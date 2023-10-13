package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nl.marc_apps.tts.rememberTextToSpeechOrNull

@Composable
fun TtsDemoApp(darkMode: Boolean) {
    val textToSpeech = rememberTextToSpeechOrNull()

    MaterialTheme(
        colorScheme = if(darkMode) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            TtsDemoView(textToSpeech, it)
        }
    }
}
