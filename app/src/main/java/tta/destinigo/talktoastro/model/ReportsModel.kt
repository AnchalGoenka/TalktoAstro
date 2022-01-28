package tta.destinigo.talktoastro.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ReportsModel(
    @SerializedName("data")
    val data: ArrayList<Any>
)

data class ReportList(
    @SerializedName("astroID")
    val astroID: String?,
    @SerializedName("service_id")
    val serviceId: String?,
    @SerializedName("servicename")
    val name: String?,
    @SerializedName("indianprice")
    val reportInrPrice: Double,
    @SerializedName("foreignprice")
    val reportUsdPrice: Double,
    var checkBox: Boolean
)

data class ArrayReportList(
    val arrReportList: ArrayList<ReportList>
)

data class ReportAstrologerList(
    @SerializedName("about")
    val about: String?,
    @SerializedName("experience")
    val experience: String?,
    @SerializedName("expertise")
    val expertise: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("isReport")
    val isReport: String?,
    @SerializedName("languages")
    val languages: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("meta_description")
    val metaDescription: String?,
    @SerializedName("meta_title")
    val metaTitle: String?,
    @SerializedName("phone_status")
    val phoneStatus: String?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("ratingAvg")
    val ratingAvg: String?,
    @SerializedName("just_coming")
    val justComing : String?,
    @SerializedName("totalRatings")
    val totalRatings: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("isFreeMin")
    val isFreeMin: String?,
    @SerializedName("call_min")
    val callMin: String?,
    @SerializedName("report_num")
    val reportNum: String?,
    @SerializedName("verified")
    val verified: String?,
    @SerializedName("audio")
    val audio: String?


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
        parcel?.writeString(id)
        parcel?.writeString(firstName)
        parcel?.writeString(lastName)
        parcel?.writeString(url)
        parcel?.writeString(metaTitle)
        parcel?.writeString(metaDescription)
        parcel?.writeString(totalRatings)
        parcel?.writeString(phoneStatus)
        parcel?.writeString(image)
        parcel?.writeString(expertise)
        parcel?.writeString(experience)
        parcel?.writeString(price)
        parcel?.writeString(verified)
        parcel?.writeString(languages)
        parcel?.writeString(ratingAvg)
        parcel?.writeString(justComing)
        parcel?.writeString(about)
        parcel?.writeString(isReport)
        parcel?.writeString(isFreeMin)
        parcel?.writeString(callMin!!)
        parcel?.writeString(reportNum!!)
        parcel?.writeString(audio!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AstrologerListModel> {
        override fun createFromParcel(parcel: Parcel): AstrologerListModel {
            return AstrologerListModel(parcel)
        }

        override fun newArray(size: Int): Array<AstrologerListModel?> {
            return arrayOfNulls(size)
        }
    }
}