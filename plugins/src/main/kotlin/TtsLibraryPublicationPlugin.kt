import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

interface TtsPublicationConfiguration {
    val javadocJarTask: Property<AbstractArchiveTask>
}

class TtsLibraryPublicationPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = extensions.create<TtsPublicationConfiguration>("ttsPublication")

        afterEvaluate {
            configure<PublishingExtension> {
                publications {
                    withType<MavenPublication> {
                        groupId = "nl.marc-apps"

                        if (extension.javadocJarTask.isPresent) {
                            artifact(extension.javadocJarTask.get())
                        }

                        pom {
                            name.set("TextToSpeechKt")
                            description.set("Kotlin Multiplatform Text-to-Speech library for Android and browser (Kotlin/JS & Kotlin/Wasm). This library will enable you to use Text-to-Speech in multiplatform Kotlin projects.")
                            url.set("https://github.com/Marc-JB/TextToSpeechKt")
                            inceptionYear.set("2020")

                            organization {
                                name.set("Marc Apps & Software")
                                url.set("https://marc-apps.nl")
                            }

                            developers {
                                developer {
                                    id.set("Marc-JB")
                                    name.set("Marc")
                                    email.set("16156117+Marc-JB@users.noreply.github.com")
                                    url.set("https://marc-apps.nl")
                                    organization.set("Marc Apps & Software")
                                    organizationUrl.set("https://marc-apps.nl")
                                }
                            }

                            licenses {
                                mitLicense()
                            }

                            configureGitHubRepository("Marc-JB", "TextToSpeechKt")
                        }
                    }
                }
            }
        }
    }
}

fun Project.configureTtsPublication(action: TtsPublicationConfiguration.() -> Unit) {
    configure<TtsPublicationConfiguration>(action)
}

private fun MavenPomLicenseSpec.mitLicense() {
    license {
        name.set("MIT")
        url.set("https://opensource.org/licenses/MIT")
    }
}

private fun MavenPom.configureGitHubRepository(organizationOrDeveloperId: String, projectId: String) {
    val locationCore = "github.com/${organizationOrDeveloperId}/${projectId}"

    issueManagement {
        url.set("https://${locationCore}/issues")
    }

    ciManagement {
        url.set("https://${locationCore}/actions")
    }

    scm {
        connection.set("scm:git:git://${locationCore}.git")
        developerConnection.set("scm:git:ssh://${locationCore}.git")
        url.set("https://${locationCore}")
    }
}
