import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun RepositoryHandler.configureOssrhRepository(isSnapshot: Boolean, uname: String?, pass: String?) {
    maven {
        name = "OSSRH"
        url = URI.create(
            if(isSnapshot) {
                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
        )

        credentials {
            username = uname
            password = pass
        }
    }
}

fun RepositoryHandler.configureGitHubPackagesRepository(organizationOrDeveloperId: String, projectId: String, uname: String?, pass: String?){
    maven {
        name = "GitHubPackages"
        url = URI.create("https://maven.pkg.github.com/${organizationOrDeveloperId}/${projectId}")

        credentials {
            username = uname
            password = pass
        }
    }
}
