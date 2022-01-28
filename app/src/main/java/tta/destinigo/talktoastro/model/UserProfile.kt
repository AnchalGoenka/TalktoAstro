package tta.destinigo.talktoastro.model


import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("balance")
    val balance: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("phonecode")
    val phonecode: String,
    @SerializedName("verified")
    val verified: String
)