[![Gradle deployment](https://github.com/Marc-JB/TextToSpeechKt/actions/workflows/deployment.yml/badge.svg)](https://github.com/Marc-JB/TextToSpeechKt/actions)
# TextToSpeechKt (preview)
Multiplatform Text-to-Speech library for Android and Browser (JS).
This library will enable you to use Text-to-Speech in multiplatform Kotlin projects and is useful when working with [Jetpack Compose on Android](https://developer.android.com/jetpack/compose) & [web (currently in preview)](https://compose-web.ui.pages.jetbrains.team/).

## Setup
Configure the maven central repository:
```Kotlin
repositories {
    mavenCentral()
}
```

And add the library to your dependencies: 
```Kotlin
dependencies {
    implementation("nl.marc-apps:tts:0.7.1")
}
```

## Usage example
Note: the examples are not using Jetpack Compose yet. 

### Android
Example is coming soon (will be published in the [/app](/app) directory)

### Browser (Kotlin/JS)
See the [/browser](/browser) directory for a working example that you can try out in the browser. 
This example is written using Kotlin/JS.

### Browser (plain JavaScript)
**Note: The typings generated (the .d.ts file) are currently incomplete.**

Example is coming soon.

### Pseudocode-like example
```Kotlin
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.TextToSpeechInitialisationError

var tts: TextToSpeechInstance? = null

@Throws(TextToSpeechInitialisationError::class)
suspend fun sayHello(name: String = "world") {
    // Use TextToSpeech.createOrNull to ignore errors.
    tts = tts ?: TextToSpeech.createOrThrow(applicationContext)
    
    // Use status STARTED to resume coroutine when the TTS engine starts speaking. The status of FINISHED will wait until the TTS engine has finished speaking.
    tts?.say("Hello $name!", clearQueue = false, resumeOnStatus = TextToSpeechInstance.Status.FINISHED)
}

fun onApplicationExit() {
    tts?.close()
}
```
