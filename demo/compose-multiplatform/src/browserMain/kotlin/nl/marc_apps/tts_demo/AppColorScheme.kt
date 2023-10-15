package nl.marc_apps.tts_demo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun appColorScheme(): ColorScheme {
    val darkTheme = isSystemInDarkTheme()
    return when {
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
}
