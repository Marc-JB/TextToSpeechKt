import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import java.net.URI
import javax.inject.Inject

interface OssrhConfig {
    val username: Property<String>
    val token: Property<String>
}

interface GitHubPackagesConfig {
    val organization: Property<String>
    val project: Property<String>
    val username: Property<String>
    val token: Property<String>
}

open class RepositoryConfigExtension @Inject constructor(objects: org.gradle.api.model.ObjectFactory) {
    val isSnapshot: Property<Boolean> = objects.property(Boolean::class.java)

    val ossrh: OssrhConfig = objects.newInstance(OssrhConfigImpl::class.java)
    fun ossrh(configure: OssrhConfig.() -> Unit) { configure(ossrh) }
    fun ossrh(configure: org.gradle.api.Action<OssrhConfig>) { configure.execute(ossrh) }

    val githubPackages: GitHubPackagesConfig = objects.newInstance(GitHubPackagesConfigImpl::class.java)
    fun githubPackages(configure: GitHubPackagesConfig.() -> Unit) { configure(githubPackages) }
    fun githubPackages(configure: org.gradle.api.Action<GitHubPackagesConfig>) { configure.execute(githubPackages) }
}

open class OssrhConfigImpl @Inject constructor(objects: org.gradle.api.model.ObjectFactory) : OssrhConfig {
    override val username: Property<String> = objects.property(String::class.java)
    override val token: Property<String> = objects.property(String::class.java)
}

open class GitHubPackagesConfigImpl @Inject constructor(objects: org.gradle.api.model.ObjectFactory) : GitHubPackagesConfig {
    override val organization: Property<String> = objects.property(String::class.java)
    override val project: Property<String> = objects.property(String::class.java)
    override val username: Property<String> = objects.property(String::class.java)
    override val token: Property<String> = objects.property(String::class.java)
}

class MavenRepositoryConfigurationPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        val extension = extensions.create<RepositoryConfigExtension>("repositoryConfig")

        configure<PublishingExtension> {
            repositories {
                if (extension.ossrh.username.isPresent && extension.ossrh.token.isPresent) {
                    maven {
                        name = "OSSRH"
                        url = URI.create(
                            if (extension.isSnapshot.getOrElse(false)) {
                                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                            } else {
                                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                            }
                        )
                        credentials {
                            username = extension.ossrh.username.get()
                            password = extension.ossrh.token.get()
                        }
                    }
                    println("Configured the Sonatype OSSRH repository")
                }

                if (extension.githubPackages.organization.isPresent && extension.githubPackages.project.isPresent
                    && extension.githubPackages.username.isPresent && extension.githubPackages.token.isPresent) {
                    maven {
                        name = "GitHubPackages"
                        url = URI.create("https://maven.pkg.github.com/${extension.githubPackages.organization.get()}/${extension.githubPackages.project.get()}")
                        credentials {
                            username = extension.githubPackages.username.get()
                            password = extension.githubPackages.token.get()
                        }
                    }
                    println("Configured the GitHub Packages repository")
                }
            }
        }
    }
}

fun Project.publishingRepositories(action: RepositoryConfigExtension.() -> Unit) {
    configure<RepositoryConfigExtension>(action)
}
