package nl.marc_apps.tts_demo

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicDarkColorScheme() = darkColorScheme()

@Composable
actual fun dynamicLightColorScheme() = lightColorScheme()

@Composable
actual fun supportsDynamicColorScheme() = false
