package nl.marc_apps.tts_demo

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun dynamicDarkColorScheme(): ColorScheme {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        darkColorScheme()
    }
}

@Composable
actual fun dynamicLightColorScheme(): ColorScheme {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(LocalContext.current)
    } else {
        lightColorScheme()
    }
}

@Composable
actual fun supportsDynamicColorScheme() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
