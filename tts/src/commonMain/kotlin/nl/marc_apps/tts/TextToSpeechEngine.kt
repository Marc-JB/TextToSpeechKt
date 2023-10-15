package nl.marc_apps.tts

sealed interface TextToSpeechEngine {
    val androidPackage: String?

    data object SystemDefault : TextToSpeechEngine {
        override val androidPackage = null
    }

    data object Google : TextToSpeechEngine {
        override val androidPackage = "com.google.android.tts"
    }

    data class Custom(override val androidPackage: String) : TextToSpeechEngine
}
