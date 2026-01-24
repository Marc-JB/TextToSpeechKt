package nl.marc_apps.tts_demo

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun dynamicDarkColorScheme(): ColorScheme

@Composable
expect fun dynamicLightColorScheme(): ColorScheme

@Composable
expect fun supportsDynamicColorScheme(): Boolean
