package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class GeoDetailResponse {

    @SerializedName("success")
    val success: Boolean =true

    @SerializedName("msg")
    val message: String? = null

    @SerializedName("geonames")
    val  geonameList:ArrayList<Geoname> = ArrayList<Geoname>()
}

class Geoname(
    @SerializedName("place_name")
    val placeName: String?,

    @SerializedName("latitude")
    val latitude: String?,

    @SerializedName("longitude")
    val longitude: String?,

    @SerializedName("timezone_id")
    val timezoneId: String?,

    @SerializedName("country_code")
    val countryCode: String?

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )


    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(placeName)
        parcel?.writeString(latitude)
        parcel?.writeString(longitude)
        parcel?.writeString(timezoneId)
        parcel?.writeString(countryCode)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Geoname> {
        override fun createFromParcel(parcel: Parcel): Geoname {
            return Geoname(parcel)
        }

        override fun newArray(size: Int): Array<Geoname?> {
            return arrayOfNulls(size)
        }
    }
}