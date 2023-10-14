package nl.marc_apps.tts.utils

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import nl.marc_apps.tts.Voice
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi

@ExperimentalVoiceApi
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal data class VoiceAndroidModern(
    override val name: String,
    override val isDefault: Boolean,
    override val isOnline: Boolean,
    override val languageTag: String,
    override val language: String,
    override val region: String?,
    val androidVoice: android.speech.tts.Voice
) : Voice {
    override val locale = androidVoice.locale

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(android.speech.tts.Voice::class.java.classLoader)!!,
        parcel.readByte() != 0.toByte()
    )

    constructor(androidVoice: android.speech.tts.Voice, isDefault: Boolean) : this(
        androidVoice.name,
        isDefault,
        androidVoice.isNetworkConnectionRequired,
        androidVoice.locale.toLanguageTag(),
        androidVoice.locale.displayLanguage,
        androidVoice.locale.displayCountry,
        androidVoice
    )

    override fun equals(other: Any?) = (other as? VoiceAndroidModern)?.androidVoice == androidVoice

    override fun hashCode() = androidVoice.hashCode()
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(androidVoice, flags)
        parcel.writeByte(if (isDefault) 1 else 0)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<VoiceAndroidModern> {
        override fun createFromParcel(parcel: Parcel) = VoiceAndroidModern(parcel)

        override fun newArray(size: Int) = arrayOfNulls<VoiceAndroidModern>(size)
    }
}