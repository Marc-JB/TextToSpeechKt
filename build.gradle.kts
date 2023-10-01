plugins {
    val androidVersion = "8.0.2"
    val kotlinVersion = "1.9.10"

    id("com.android.application") version androidVersion apply false
    id("com.android.library") version androidVersion apply false

    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version "1.9.0" apply false

    id("org.jetbrains.compose") version "1.5.2" apply false
}
