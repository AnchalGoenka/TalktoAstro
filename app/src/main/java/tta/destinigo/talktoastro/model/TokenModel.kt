package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName

data class TokenModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Int
)