package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-10-29.

 */
data class ForgotPasswordModel(
    @SerializedName("message")
    val message: String,
    @SerializedName("otp")
    val otp: Int,
    @SerializedName("success")
    val success: Int,
    @SerializedName("userID")
    val userID: String
)