package nl.marc_apps.tts.utils

import android.os.Parcel
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import nl.marc_apps.tts.Voice
import java.util.*

@Parcelize
internal data class VoiceAndroidLegacy(
    override val locale: Locale,
    override val isDefault: Boolean
) : Voice {
    override val name: String = locale.displayName
    override val isOnline: Boolean = false
    override val languageTag: String = locale.language
    override val language: String = locale.displayLanguage
    override val region: String? = locale.displayCountry

    override fun equals(other: Any?) = (other as? VoiceAndroidLegacy)?.locale == locale

    override fun hashCode() = locale.hashCode()

    private companion object : Parceler<VoiceAndroidLegacy> {
        override fun VoiceAndroidLegacy.write(parcel: Parcel, flags: Int) {
            parcel.writeSerializable(locale)
            parcel.writeByte(if (isDefault) 1 else 0)
        }

        override fun create(parcel: Parcel): VoiceAndroidLegacy {
            return VoiceAndroidLegacy(
                parcel.readSerializable() as Locale,
                parcel.readByte() != 0.toByte()
            )
        }
    }
}
