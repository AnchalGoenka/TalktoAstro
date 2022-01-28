package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class RazorPayOrderIdModel(
    @SerializedName("success")
    val success: String?,
    @SerializedName("ttaOrderID")
    val ttaOrderId: Long,
    @SerializedName("razorPayOrderID")
    val razorPayOrderId: String?,
    @SerializedName("packageName")
    val packageName: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("talkValue")
    val talkValue: String?,
    @SerializedName("taxName")
    val taxName: String?,
    @SerializedName("taxPercent")
    val taxPercent: String?,
    @SerializedName("billingAmount")
    val billingAmount: String?,
    @SerializedName("message")
    val msg: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
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
        parcel?.writeString(success)
        parcel?.writeLong(ttaOrderId)
        parcel?.writeString(razorPayOrderId)
        parcel?.writeString(packageName)
        parcel?.writeString(currency)
        parcel?.writeString(talkValue)
        parcel?.writeString(taxName)
        parcel?.writeString(taxPercent)
        parcel?.writeString(billingAmount)
        parcel?.writeString(msg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RazorPayOrderIdModel> {
        override fun createFromParcel(parcel: Parcel): RazorPayOrderIdModel {
            return RazorPayOrderIdModel(parcel)
        }

        override fun newArray(size: Int): Array<RazorPayOrderIdModel?> {
            return arrayOfNulls(size)
        }
    }
}