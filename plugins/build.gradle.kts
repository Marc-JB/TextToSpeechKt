plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("nl.marc-apps.maven-repository-configuration-plugin") {
            id = "nl.marc-apps.maven-repository-configuration-plugin"
            implementationClass = "MavenRepositoryConfigurationPlugin"
        }
    }
}
