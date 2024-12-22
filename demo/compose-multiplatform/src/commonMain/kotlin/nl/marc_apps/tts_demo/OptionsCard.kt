package nl.marc_apps.tts_demo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeMute
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.marc_apps.tts.TextToSpeechInstance
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import nl.marc_apps.tts_demo.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsCard(
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
                    stringResource(Res.string.options_title),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded){
                HorizontalDivider()

                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeMute, contentDescription = null)

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

                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = null)
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
                        textToSpeech.currentVoice?.let { "${it.languageTag} (${it.name})" } ?: stringResource(Res.string.placeholder_voice_unknown),
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
                        Text(stringResource(Res.string.action_options_reset_all))
                    }
                }

                if (showDialog) {
                    BasicAlertDialog(
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
                                        "${it.languageTag} (${it.name})",
                                        maxLines = 1,
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
