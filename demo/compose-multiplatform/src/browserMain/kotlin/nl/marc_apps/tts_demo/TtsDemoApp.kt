package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.marc_apps.tts.rememberTextToSpeechOrNull
import org.jetbrains.skiko.loadBytesFromPath

suspend fun defaultFontFamily(): FontFamily {
    return FontFamily(
        Font(
            identity = "Roboto-Medium",
            data = loadBytesFromPath("Roboto-Medium.ttf"),
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            identity = "Roboto-MediumItalic",
            data = loadBytesFromPath("Roboto-MediumItalic.ttf"),
            weight = FontWeight.Medium,
            style = FontStyle.Italic
        ),
        Font(
            identity = "Roboto-Regular",
            data = loadBytesFromPath("Roboto-Regular.ttf"),
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            identity = "Roboto-Italic",
            data = loadBytesFromPath("Roboto-Italic.ttf"),
            weight = FontWeight.Normal,
            style = FontStyle.Italic
        )
    )
}

@Composable
fun TtsDemoApp(darkMode: Boolean, useRoboto: Boolean = true) {
    val textToSpeech = rememberTextToSpeechOrNull()

    val defaultTypography = MaterialTheme.typography
    var typography by remember { mutableStateOf<Typography?>(null) }
    LaunchedEffect(useRoboto) {
        if (useRoboto) {
            typography = withContext(Dispatchers.Default) {
                Typography(defaultFontFamily())
            }
        }
    }

    val isLoadingTypography by derivedStateOf {
        useRoboto && typography == null
    }

    val currentTypography by derivedStateOf {
        typography ?: defaultTypography
    }

    MaterialTheme(
        colors = if(darkMode) darkColors() else lightColors(),
        typography = currentTypography
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoadingTypography) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    Text("Loading...")
                }
            } else {
                TtsDemoView(textToSpeech, it)
            }
        }
    }
}
