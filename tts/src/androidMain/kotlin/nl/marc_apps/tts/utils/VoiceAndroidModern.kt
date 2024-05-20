package nl.marc_apps.tts.utils

import android.os.Build
import android.os.Parcel
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import nl.marc_apps.tts.Voice
import java.util.*

@Parcelize
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal data class VoiceAndroidModern(
    val androidVoice: android.speech.tts.Voice,
    override val isDefault: Boolean
) : Voice {
    override val locale: Locale = androidVoice.locale

    override val name: String = androidVoice.locale.displayName

    override val isOnline: Boolean = androidVoice.isNetworkConnectionRequired
    override val languageTag: String = androidVoice.locale.toLanguageTag()
    override val language: String = androidVoice.locale.displayLanguage
    override val region: String? = androidVoice.locale.displayCountry

    override fun equals(other: Any?) = (other as? VoiceAndroidModern)?.androidVoice == androidVoice

    override fun hashCode() = androidVoice.hashCode()

    private companion object : Parceler<VoiceAndroidModern> {
        override fun VoiceAndroidModern.write(parcel: Parcel, flags: Int) {
            androidVoice.writeToParcel(parcel, flags)
            parcel.writeByte(if (isDefault) 1 else 0)
        }

        override fun create(parcel: Parcel): VoiceAndroidModern {
            return VoiceAndroidModern(
                android.speech.tts.Voice.CREATOR.createFromParcel(parcel),
                parcel.readByte() != 0.toByte()
            )
        }
    }
}
