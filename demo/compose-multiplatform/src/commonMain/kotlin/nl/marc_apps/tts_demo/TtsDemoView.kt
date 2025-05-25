package nl.marc_apps.tts_demo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance
import org.jetbrains.compose.resources.stringResource
import nl.marc_apps.tts_demo.resources.*

@Composable
fun TtsDemoView(
    textToSpeech: TextToSpeechInstance?,
    paddingValues: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(paddingValues)
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            if (textToSpeech == null) {
                Text(stringResource(Res.string.tts_not_available))
            } else {
                val focusManager = LocalFocusManager.current

                var text by rememberSaveable { mutableStateOf("") }

                val isSynthesizing by textToSpeech.isSynthesizing.collectAsState()

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Rounded.GraphicEq, contentDescription = null)

                    Text(stringResource(if (isSynthesizing) Res.string.synthesizing_status_active else Res.string.synthesizing_status_inactive))
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(Res.string.tts_input_label)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OptionsCard(textToSpeech)

                Spacer(Modifier.weight(1f))

                ElevatedButton(
                    onClick = {
                        focusManager.clearFocus()
                        coroutineScope.launch {
                            textToSpeech.say(text)
                        }
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(32.dp, 16.dp)
                ) {
                    Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null)

                    Spacer(Modifier.width(16.dp))

                    Text(stringResource(Res.string.action_say))
                }
            }
        }
    }
}
