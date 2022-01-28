package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class hubQuestion {

@SerializedName("data")
val hubQuestionListItem: ArrayList<hubQuestionList> = ArrayList<hubQuestionList>()
}

class hubQuestionList(
    @SerializedName("id")
    val id: String?,

    @SerializedName("question")
    val question: String?,

    @SerializedName("user_type")
    val userType: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(id)
        parcel?.writeString(question)
        parcel?.writeString(userType)
        parcel?.writeString(status)
        parcel?.writeString(createdAt)
        parcel?.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArticleList> {
        override fun createFromParcel(parcel: Parcel): ArticleList {
            return ArticleList(parcel)
        }

        override fun newArray(size: Int): Array<ArticleList?> {
            return arrayOfNulls(size)
        }
    }
}