package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class NotificationModel {
    @SerializedName("success")
   val success: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("data")
    val  notificationlist:ArrayList<NotificationList> = ArrayList<NotificationList>()


}


class NotificationList(

    @SerializedName("title")
    var title: String?,

    @SerializedName("text")
    var text: String?,

    @SerializedName("created_at")
    var time: String?

):Parcelable{
    constructor(parcel: Parcel):this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    )


    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(text)
        parcel?.writeString(title)
        parcel?.writeString(time)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationList> {
        override fun createFromParcel(parcel: Parcel): NotificationList {
            return NotificationList(parcel)
        }

        override fun newArray(size: Int): Array<NotificationList?> {
            return arrayOfNulls(size)
        }
    }
}