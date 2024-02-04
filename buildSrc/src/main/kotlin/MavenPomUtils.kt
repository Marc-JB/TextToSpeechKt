import org.gradle.api.publish.maven.MavenPom

fun MavenPom.configureMitLicense() {
    licenses {
        license {
            name.set("MIT")
            url.set("https://opensource.org/licenses/MIT")
        }
    }
}

fun MavenPom.configureGitHubRepository(organizationOrDeveloperId: String, projectId: String) {
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
