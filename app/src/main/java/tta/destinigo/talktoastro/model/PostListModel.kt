package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName

class PostListModel (
    @SerializedName("astro_id")
    val astroId: String?=null,

    @SerializedName("yt_link")
    val ytLink: String?=null,

    @SerializedName("text")
    val textmsg: String?=null,

    @SerializedName("created_at")
    val createdAt: String?=null,

    @SerializedName("updated_at")
    val updatedAt: String? =null
)