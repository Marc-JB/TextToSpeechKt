package nl.marc_apps.tts

@AllowExport
@Parcelize
data class Voice(
    val name: String,
    val isDefault: Boolean,
    val isOnline: Boolean,
    val languageTag: String,
    val language: String,
    val region: String?
) : Parcelable
