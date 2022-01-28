package tta.destinigo.talktoastro.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AstroProfileModel(
    @SerializedName("about")
    val about: String,
    @SerializedName("availability")
    val availability: List<Availability>,
    @SerializedName("experience")
    val experience: String,
    @SerializedName("expertise")
    val expertise: String,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("INRPrice")
    val iNRPrice: Int,
    @SerializedName("isReport")
    val isReport: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("reviews")
    val reviews: ArrayList<Review>,
    @SerializedName("USDDisplayPrice")
    val uSDDisplayPrice: String,
    @SerializedName("USDPrice")
    val uSDPrice: Int
)

data class Availability(
    @SerializedName("day")
    val day: String,
    @SerializedName("time")
    val time: String
)

data class Review(
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("rating")
    val rating: String?
): Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(userName)
        parcel?.writeString(message)
        parcel?.writeString(rating)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}