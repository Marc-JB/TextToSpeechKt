package nl.marc_apps.tts.internal

data class EnqueueOptions(
    val volume: Int = 100,
    val pitch: Float = 1f,
    val rate: Float = 1f,
    val clearQueue: Boolean = false
)
