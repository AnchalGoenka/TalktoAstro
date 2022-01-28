package tta.destinigo.talktoastro.model


import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("balance")
    val balance: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("phonecode")
    val phonecode: String,
    @SerializedName("success")
    val success: Int,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("verified")
    val verified: String,
    @SerializedName("otp")
    val otp: String?,
    @SerializedName("notifiycount")
    val notifyCount: NotifyCountModel?
)

data class NotifyCountModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user_id")
    val userID: String?,
    @SerializedName("astro_id")
    val astroID: String?,
    @SerializedName("notify")
    val notify: String?
)

data class LoginModelWithOTP(
    @SerializedName("Status")
    val status: String,
    @SerializedName("Details")
    val details: String
)