plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("nl.marc-apps.maven-repository-configuration-plugin") {
            id = "nl.marc-apps.maven-repository-configuration-plugin"
            implementationClass = "MavenRepositoryConfigurationPlugin"
        }

        create("nl.marc-apps.tts-publication-plugin") {
            id = "nl.marc-apps.tts-publication-plugin"
            implementationClass = "TtsLibraryPublicationPlugin"
        }
    }
}
