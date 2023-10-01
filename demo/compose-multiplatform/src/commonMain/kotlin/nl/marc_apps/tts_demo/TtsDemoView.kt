package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance

@Composable
fun TtsDemoView(
    textToSpeech: TextToSpeechInstance?,
    paddingValues: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (textToSpeech == null) {
            Text("Text-to-Speech is not available.")
        } else {
            var text by remember { mutableStateOf("") }

            val isSynthesizing by textToSpeech.isSynthesizing.collectAsState()

            Text("TTS is currently ${if (isSynthesizing) "synthesizing" else "not synthesizing"}")

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Text") }
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        textToSpeech.say(text)
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text("Say!")
            }
        }
    }
}
