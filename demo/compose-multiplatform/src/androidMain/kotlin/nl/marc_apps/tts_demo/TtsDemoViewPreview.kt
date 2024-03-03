package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.experimental.ExperimentalDesktopTarget
import nl.marc_apps.tts.rememberTextToSpeechOrNull

@OptIn(ExperimentalDesktopTarget::class)
@Preview
@Composable
private fun TtsDemoViewPreview() {
    val textToSpeech = rememberTextToSpeechOrNull(TextToSpeechEngine.Google)

    AppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            TtsDemoView(textToSpeech = textToSpeech, paddingValues = it)
        }
    }
}
