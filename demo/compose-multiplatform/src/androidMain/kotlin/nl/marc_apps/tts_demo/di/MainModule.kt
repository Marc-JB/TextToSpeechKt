package nl.marc_apps.tts_demo.di

import android.content.Context
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.TextToSpeechFactory
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("nl.marc_apps.tts_demo.ui")
class MainModule {
    @Single
    fun textToSpeechFactory(context: Context): TextToSpeechFactory {
        return TextToSpeechFactory(context, TextToSpeechEngine.Google)
    }
}
