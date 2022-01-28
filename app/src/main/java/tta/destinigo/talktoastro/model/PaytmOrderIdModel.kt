package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-07-26.

 */
class PaytmOrderIdModel(
    @SerializedName("success")
    val success: Int,
    @SerializedName("payTMCheckSum")
    val payTMCheckSum: String?,
    @SerializedName("message")
    val msg: String?
//    @SerializedName("payt_STATUS")
//    val success: String,
//    @SerializedName("CHECKSUMHASH")
//    val payTMCheckSum: String,
//    @SerializedName("ORDER_ID")
//    val msg: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(success)
        parcel?.writeString(payTMCheckSum)
        parcel?.writeString(msg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaytmOrderIdModel> {
        override fun createFromParcel(parcel: Parcel): PaytmOrderIdModel {
            return PaytmOrderIdModel(parcel)
        }

        override fun newArray(size: Int): Array<PaytmOrderIdModel?> {
            return arrayOfNulls(size)
        }
    }
}