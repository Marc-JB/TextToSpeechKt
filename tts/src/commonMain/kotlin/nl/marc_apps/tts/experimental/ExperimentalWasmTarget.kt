package nl.marc_apps.tts.experimental

@Deprecated("The wasm text-to-speech target is no longer experimental. You cna remove this annotation.")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class ExperimentalWasmTarget
