package nl.marc_apps.tts_demo

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import nl.marc_apps.tts.AudioSession
import org.jetbrains.compose.resources.stringResource
import nl.marc_apps.tts_demo.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController {
    LaunchedEffect(Unit) {
        AudioSession.initialiseForTextToSpeech()
    }

    TtsDemoApp(
        topAppBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.app_name))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    )
}
