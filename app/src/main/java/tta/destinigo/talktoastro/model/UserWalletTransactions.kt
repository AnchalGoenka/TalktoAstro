package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-07-28.

 */
data class UserWalletTransactions(
    @SerializedName("0")
    val walletBalance: String,
    @SerializedName("1")
    val walletHistoy: ArrayList<WalletHistoryData>
)

data class WalletHistoryData(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("order_status")
    val orderStatus: String,
    @SerializedName("paidAmount")
    val paidAmount: String,
    @SerializedName("paymentTime")
    val paymentTime: String,
    @SerializedName("talkValue")
    val talkValue: String
)
