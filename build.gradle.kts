plugins {
    val kotlinVersion = "1.8.10"

    id("com.android.application") version "7.4.2" apply false
    kotlin("multiplatform") version kotlinVersion apply false
    id("org.jetbrains.dokka") version kotlinVersion apply false
}
