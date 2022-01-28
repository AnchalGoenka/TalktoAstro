package tta.destinigo.talktoastro.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-06-15.

 */
class ArticleModel {
    @SerializedName("data")
    val articleList: ArrayList<ArticleList> = ArrayList<ArticleList>()
}

class ArticleList(
    @SerializedName("articleID")
    val id: String?,

    @SerializedName("categoryName")
    val category: String?,

    @SerializedName("articleTitle")
    val title: String?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("meta_title")
    val meta_title: String?,

    @SerializedName("meta_description")
    val meta_description: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel?.writeString(category)
        parcel?.writeString(title)
        parcel?.writeString(image)
        parcel?.writeString(description)
        parcel?.writeString(meta_title)
        parcel?.writeString(meta_description)
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