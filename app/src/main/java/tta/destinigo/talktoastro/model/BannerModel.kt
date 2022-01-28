package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-06-14.

 */
class BannerModel (
    @SerializedName("slider")
    val bannerList: ArrayList<BannerList> = ArrayList<BannerList>(),
    @SerializedName("marquee")
    val marquee: ArrayList<marqueeData> = ArrayList<marqueeData>()
)

class BannerList(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("mainText")
    val mainText: String,
    @SerializedName("subText")
    val subText: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("sort")
    val sort: String
)

class marqueeData(
    @SerializedName("id")
    val id: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("status")
    val status: String
)