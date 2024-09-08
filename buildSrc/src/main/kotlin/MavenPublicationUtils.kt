import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar

fun configureMavenPublication(project: Project, mavenPublication: MavenPublication, javadocJar: TaskProvider<Jar>, baseArtifactId: String) {
    with(project) {
        with(mavenPublication) {
            groupId = getTtsProperty("groupId")

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
