package nl.marc_apps.tts_demo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget
import nl.marc_apps.tts.rememberTextToSpeechOrNull

@OptIn(ExperimentalDesktopTarget::class)
@Composable
fun TtsDemoApp() {
    val textToSpeech = rememberTextToSpeechOrNull()

    MaterialTheme(
        colors = if(isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            TtsDemoView(textToSpeech, it)
        }
    }
}
