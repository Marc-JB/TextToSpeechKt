package nl.marc_apps.tts_demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance
import kotlin.math.roundToInt

@Composable
fun TtsDemoView(
    textToSpeech: TextToSpeechInstance?,
    paddingValues: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
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

                OptionsCard(
                    defaultVolume = textToSpeech.volume,
                    defaultRate = textToSpeech.rate,
                    onVolumeChanged = {
                        textToSpeech.volume = it
                    },
                    onRateChanged = {
                        textToSpeech.rate = it
                    }
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            textToSpeech.say(text)
                        }
                    },
                    enabled = text.isNotBlank(),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null)

                    Spacer(Modifier.width(16.dp))

                    Text("Say!")
                }
            }
        }
    }
}

@Composable
private fun OptionsCard(
    defaultVolume: Int,
    defaultRate: Float,
    onVolumeChanged: (Int) -> Unit,
    onRateChanged: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colors.surface,
        shape = MaterialTheme.shapes.medium,
        elevation = if (expanded) 6.dp else 2.dp
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
                    style = MaterialTheme.typography.subtitle1,
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

                    var preferredSoundLevel by remember { mutableStateOf(defaultVolume.toFloat()) }

                    Slider(
                        value = preferredSoundLevel,
                        onValueChange = {
                            preferredSoundLevel = it.roundToInt().toFloat()
                            onVolumeChanged(it.roundToInt())
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

                    var preferredRate by remember { mutableStateOf(defaultRate * 10) }

                    Slider(
                        value = preferredRate,
                        onValueChange = {
                            preferredRate = it
                            onRateChanged(it * 0.1f)
                        },
                        valueRange = 1f..20f,
                        steps = 20,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}