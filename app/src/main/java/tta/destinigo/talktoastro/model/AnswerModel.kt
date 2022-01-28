package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AnswerModel {
    @SerializedName("success")
    val success: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("answer")
    val  answerList:ArrayList<AnswerList> = ArrayList<AnswerList>()

}

class AnswerList(
    @SerializedName("id")
    val id: String?,

    @SerializedName("question_id")
    val questionId: String?,

    @SerializedName("answer")
    val answer: String?,

    @SerializedName("user_type")
    val userType: String?,

    @SerializedName("user_id")
    val userId: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("image")
    val image: String?

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
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(id)
        parcel?.writeString(questionId)
        parcel?.writeString(answer)
        parcel?.writeString(userType)
        parcel?.writeString(userId)
        parcel?.writeString(name)
        parcel?.writeString(createdAt)
        parcel?.writeString(updatedAt)
        parcel?.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AnswerList> {
        override fun createFromParcel(parcel: Parcel): AnswerList {
            return AnswerList(parcel)
        }

        override fun newArray(size: Int): Array<AnswerList?> {
            return arrayOfNulls(size)
        }
    }
}