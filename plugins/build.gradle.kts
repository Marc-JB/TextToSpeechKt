plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("nl.marc-apps.tts-plugin") {
            id = "nl.marc-apps.tts-plugin"
            implementationClass = "RepositoryConfigurationPlugin"
        }
    }
}
