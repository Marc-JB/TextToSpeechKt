package nl.marc_apps.tts_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.ui.res.stringResource
import nl.marc_apps.tts.rememberTextToSpeechOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val textToSpeech = rememberTextToSpeechOrNull()

            MaterialTheme(
                colors = if(isSystemInDarkTheme()) darkColors() else lightColors()
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(stringResource(id = R.string.app_name))
                        })
                    }
                ) {
                    TtsDemoView(textToSpeech = textToSpeech, paddingValues = it)
                }
            }
        }
    }
}
