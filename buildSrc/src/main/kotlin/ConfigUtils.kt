import org.gradle.api.Project

fun Project.getConfigProperty(vararg path: String): String? {
    return findProperty(path.joinToString(".")) as? String
        ?: System.getenv(path.joinToString("_") { it.uppercase() })
}

fun Project.getTtsProperty(vararg path: String) = getConfigProperty("tts", *path)
