package tta.destinigo.talktoastro.chat.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import tta.destinigo.talktoastro.chat.ChannelModel


/**

 * Created by Vivek Singh on 5/11/20.

 */

data class ChatFormModel(
    @SerializedName("userID")
    val userID: String?,
    @SerializedName("astroID")
    val astroID: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("dateofbirth")
    val dateOfBirth: String?,
    @SerializedName("timeofbirth")
    val timeOfBirth: String?,
    @SerializedName("placeofbirth")
    val placeOfBirth: String?,
    @SerializedName("countryofbirth")
    val countryOfBirth: String?,
    @SerializedName("question")
    val question: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Int=2
): Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(userID)
        parcel?.writeString(astroID)
        parcel?.writeString(name)
        parcel?.writeString(gender)
        parcel?.writeString(dateOfBirth)
        parcel?.writeString(timeOfBirth)
        parcel?.writeString(placeOfBirth)
        parcel?.writeString(countryOfBirth)
        parcel?.writeString(question)
        parcel?.writeString(message)
        parcel?.writeInt(success)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatFormModel> {
        override fun createFromParcel(parcel: Parcel): ChatFormModel {
            return ChatFormModel(parcel)
        }

        override fun newArray(size: Int): Array<ChatFormModel?> {
            return arrayOfNulls(size)
        }
    }
}


data class ChatFormSubmitResponseModel(
    @SerializedName("success")
    val success: Int = 1,
    @SerializedName("message")
    val message: String?,
    @SerializedName("chatID")
    val chatID: String?,
    @SerializedName("duration")
    val duration: Int?
)

data class ChatHistoryModel(
    @SerializedName("astroName")
    val astroName: String?,
    @SerializedName("astro_id")
    val astroId: String?,
    @SerializedName("chat_id")
    val chatID: String?,
    @SerializedName("session_id")
    val sessionID: String?,
    @SerializedName("chat_duration")
    val chat_duration: String?,
    @SerializedName("price")
    val price: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("chatTime")
    val chatTime: String?
)