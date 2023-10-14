package nl.marc_apps.tts_demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import kotlin.math.roundToInt

@Composable
fun TtsDemoView(
    textToSpeech: TextToSpeechInstance?,
    paddingValues: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            if (textToSpeech == null) {
                Text("Text-to-Speech is not available.")
            } else {
                var text by remember { mutableStateOf("") }

                val isSynthesizing by textToSpeech.isSynthesizing.collectAsState()

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(0.dp, 8.dp)
                ) {
                    Icon(Icons.Rounded.GraphicEq, contentDescription = null)

                    Text(if (isSynthesizing) "Synthesizing" else "Not active")
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text") },
                    modifier = Modifier.fillMaxWidth()
                )

                OptionsCard(textToSpeech)

                Spacer(Modifier.weight(1f))

                ElevatedButton(
                    onClick = {
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

                    Text("Say!")
                }
            }
        }
    }
}

@OptIn(ExperimentalVoiceApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun OptionsCard(
    textToSpeech: TextToSpeechInstance
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (expanded) 6.dp else 2.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(16.dp, 8.dp)
                    .height(48.dp)
            ) {
                Icon(Icons.Rounded.Settings, contentDescription = null)

                Text(
                    "Options",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded){
                Divider()

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.VolumeMute, contentDescription = null)

                    var preferredSoundLevel by remember { mutableStateOf(textToSpeech.volume.toFloat()) }

                    Slider(
                        value = preferredSoundLevel,
                        onValueChange = {
                            preferredSoundLevel = it.roundToInt().toFloat()
                            textToSpeech.volume = it.roundToInt()
                        },
                        valueRange = 0f..100f,
                        steps = 10,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(Icons.Rounded.VolumeUp, contentDescription = null)
                }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.Speed, contentDescription = null)

                    var preferredRate by remember { mutableStateOf(textToSpeech.rate * 10) }

                    Slider(
                        value = preferredRate,
                        onValueChange = {
                            preferredRate = it
                            textToSpeech.rate = it * 0.1f
                        },
                        valueRange = 1f..20f,
                        steps = 20,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                ) {
                    Icon(Icons.Rounded.Moving, contentDescription = null)

                    var preferredPitch by remember { mutableStateOf(textToSpeech.pitch * 10 - 5) }

                    Slider(
                        value = preferredPitch,
                        onValueChange = {
                            preferredPitch = it
                            textToSpeech.pitch = it * 0.1f + 0.5f
                        },
                        valueRange = 1f..10f,
                        steps = 10,
                        modifier = Modifier.weight(1f)
                    )
                }

                var showDialog by remember { mutableStateOf(false) }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                        .clickable {
                            showDialog = true
                        }
                ) {
                    Icon(Icons.Rounded.Language, contentDescription = null)

                    Text(
                        textToSpeech.currentVoice?.name ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            textToSpeech.volume = TextToSpeechInstance.VOLUME_DEFAULT
                            textToSpeech.pitch = TextToSpeechInstance.VOICE_PITCH_DEFAULT
                            textToSpeech.rate = TextToSpeechInstance.VOICE_RATE_DEFAULT
                            textToSpeech.currentVoice = textToSpeech.voices.firstOrNull { it.isDefault }
                            expanded = false
                        }
                    ) {
                        Text("Reset")
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                        modifier = Modifier.defaultMinSize(160.dp, 160.dp)
                    ) {
                        Surface(
                            tonalElevation = 8.dp
                        ) {
                            LazyColumn {
                                items(textToSpeech.voices.toList(), key = { it.hashCode() }) {
                                    Text(
                                        it.name,
                                        modifier = Modifier
                                            .padding(16.dp, 4.dp)
                                            .clickable {
                                                textToSpeech.currentVoice = it
                                                showDialog = false
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}