package tta.destinigo.talktoastro.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ReportHistoryModel(
    @SerializedName("astro_id")
    val astroId: String?,
    @SerializedName("astroName")
    val astroName: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("place")
    val place: String?,
    @SerializedName("requirement")
    val requirement: String?,
    @SerializedName("service_id")
    val serviceId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
): Parcelable {
    constructor(parcel: Parcel): this(
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
        parcel?.writeString(astroId)
        parcel?.writeString(astroName)
        parcel?.writeString(date)
        parcel?.writeString(gender)
        parcel?.writeString(id)
        parcel?.writeString(message)
        parcel?.writeString(name)
        parcel?.writeString(place)
        parcel?.writeString(requirement)
        parcel?.writeString(serviceId)
        parcel?.writeString(status)
        parcel?.writeString(time)
        parcel?.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportHistoryModel> {
        override fun createFromParcel(parcel: Parcel): ReportHistoryModel {
            return ReportHistoryModel(parcel)
        }

        override fun newArray(size: Int): Array<ReportHistoryModel?> {
            return arrayOfNulls(size)
        }
    }
}