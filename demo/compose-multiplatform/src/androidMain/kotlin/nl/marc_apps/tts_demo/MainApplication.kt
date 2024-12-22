package nl.marc_apps.tts_demo

import android.app.Application
import nl.marc_apps.tts_demo.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.ksp.generated.module

class MainApplication : Application() {
    init {
        onKoinStartup {
            androidContext(this@MainApplication)
            androidLogger()
            modules(MainModule().module)
        }
    }
}
