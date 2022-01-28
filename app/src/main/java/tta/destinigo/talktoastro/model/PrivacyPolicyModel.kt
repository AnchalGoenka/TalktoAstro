package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName

data class PrivacyPolicyModel(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("meta_description")
    val meta_description: String,
    @SerializedName("meta_title")
    val meta_title: String
)