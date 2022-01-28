package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName

data class PaymentStatusModel(
    @SerializedName("creditedTalkValue")
    val creditedTalkValue: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("smsSent")
    val smsSent: String,
    @SerializedName("success")
    val success: Int,
    @SerializedName("walletNewBalance")
    val walletNewBalance: Double,
    @SerializedName("walletOldBalance")
    val walletOldBalance: String
)