package nl.marc_apps.tts.utils

import android.os.Parcel
import android.os.Parcelable
import nl.marc_apps.tts.Voice
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.util.*

@ExperimentalVoiceApi
internal data class VoiceAndroidLegacy(
    override val name: String,
    override val isDefault: Boolean,
    override val isOnline: Boolean,
    override val languageTag: String,
    override val language: String,
    override val region: String?,
    override val locale: Locale
) : Voice {
    constructor(parcel: Parcel) : this(
        parcel.readSerializable() as Locale,
        parcel.readByte() != 0.toByte()
    )

    constructor(locale: Locale, isDefault: Boolean) : this(
        locale.displayName,
        isDefault,
        false,
        locale.language,
        locale.displayLanguage,
        locale.displayCountry,
        locale
    )

    override fun equals(other: Any?) = (other as? VoiceAndroidLegacy)?.locale == locale

    override fun hashCode() = locale.hashCode()
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(locale)
        parcel.writeByte(if (isDefault) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<VoiceAndroidLegacy> {
        override fun createFromParcel(parcel: Parcel) = VoiceAndroidLegacy(parcel)

        override fun newArray(size: Int) = arrayOfNulls<VoiceAndroidLegacy>(size)
    }
}