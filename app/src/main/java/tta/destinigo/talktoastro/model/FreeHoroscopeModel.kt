package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FreeHoroscopeModel(
    @SerializedName("Charan")
    val Charan: Int,
    @SerializedName("Gan")
    val Gan: String?,
    @SerializedName("Karan")
    val Karan: String?,
    @SerializedName("Nadi")
    val Nadi: String?,
    @SerializedName("Naksahtra")
    val Naksahtra: String?,
    @SerializedName("NaksahtraLord")
    val NaksahtraLord: String?,
    @SerializedName("SignLord")
    val SignLord: String?,
    @SerializedName("Tithi")
    val Tithi: String?,
    @SerializedName("Varna")
    val Varna: String?,
    @SerializedName("Vashya")
    val Vashya: String?,
    @SerializedName("Yog")
    val Yog: String?,
    @SerializedName("Yoni")
    val Yoni: String?,
    @SerializedName("ascendant")
    val ascendant: String?,
    @SerializedName("ascendant_lord")
    val ascendant_lord: String?,
    @SerializedName("name_alphabet")
    val name_alphabet: String?,
    @SerializedName("paya")
    val paya: String?,
    @SerializedName("sign")
    val sign: String?,
    @SerializedName("tatva")
    val tatva: String?,
    @SerializedName("yunja")
    val yunja: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(Charan)
        parcel?.writeString(Gan)
        parcel?.writeString(Karan)
        parcel?.writeString(Nadi)
        parcel?.writeString(Naksahtra)
        parcel?.writeString(NaksahtraLord)
        parcel?.writeString(SignLord)
        parcel?.writeString(Tithi)
        parcel?.writeString(Varna)
        parcel?.writeString(Vashya)
        parcel?.writeString(Yog)
        parcel?.writeString(Yoni)
        parcel?.writeString(ascendant)
        parcel?.writeString(ascendant_lord)
        parcel?.writeString(name_alphabet)
        parcel?.writeString(paya)
        parcel?.writeString(sign)
        parcel?.writeString(tatva)
        parcel?.writeString(yunja)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FreeHoroscopeModel> {
        override fun createFromParcel(parcel: Parcel): FreeHoroscopeModel {
            return FreeHoroscopeModel(parcel)
        }

        override fun newArray(size: Int): Array<FreeHoroscopeModel?> {
            return arrayOfNulls(size)
        }
    }
}