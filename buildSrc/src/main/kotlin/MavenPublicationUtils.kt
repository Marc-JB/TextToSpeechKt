import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

fun configureMavenPublication(project: Project, mavenPublication: MavenPublication, javadocJar: TaskProvider<Jar>, baseArtifactId: String) {
    with(project) {
        with(mavenPublication) {
            groupId = getTtsProperty("groupId")

            /*artifactId = baseArtifactId + when {
                artifactId.endsWith("-android", ignoreCase = true) || name == "android" -> "-android"
                artifactId.endsWith("-browser", ignoreCase = true) -> "-browser"
                artifactId.endsWith("-browserJs", ignoreCase = true) -> "-browser-js"
                artifactId.endsWith("-browserWasm", ignoreCase = true) -> "-browser-wasm"
                artifactId.endsWith("-desktop", ignoreCase = true) -> "-desktop"
                artifactId.endsWith("-ios-x64", ignoreCase = true) -> "-ios-x64"
                artifactId.endsWith("-ios-arm64", ignoreCase = true) -> "-ios-arm64"
                artifactId.endsWith("-ios-simulator-arm64", ignoreCase = true) -> "-ios-simulator-arm64"
                else -> ""
            }*/

            artifact(javadocJar.get())

            pom {
                name.set(getTtsProperty("name"))
                description.set(getTtsProperty("description"))
                url.set("https://${getTtsProperty("git", "location")}")
                inceptionYear.set("2020")

                organization {
                    name.set(getTtsProperty("developer", "orgName"))
                    url.set(getTtsProperty("developer", "website"))
                }

                developers {
                    developer {
                        id.set(getTtsProperty("developer", "github", "username"))
                        name.set(getTtsProperty("developer", "name"))
                        email.set(getTtsProperty("developer", "email"))
                        url.set(getTtsProperty("developer", "website"))
                        organization.set(getTtsProperty("developer", "orgName"))
                        organizationUrl.set(getTtsProperty("developer", "website"))
                    }
                }

                configureMitLicense()

                configureGitHubRepository("Marc-JB", "TextToSpeechKt")
            }
        }
    }
}
