package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-07-03.

 */
class PayPackageModel {
    @SerializedName("userID")
    val userID: String=""
    @SerializedName("data")
    val payPackages: ArrayList<ArrayList<PackageList>> = ArrayList<ArrayList<PackageList>>()
}

class PackageList(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("cashback")
    val cashback: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("currency")
    val currency: String

)