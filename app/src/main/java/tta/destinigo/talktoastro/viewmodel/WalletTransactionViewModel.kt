package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.UserWalletTransactions
import tta.destinigo.talktoastro.model.WalletHistoryData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-08-05.

 */
class WalletTransactionViewModel constructor(private val mApplication: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(mApplication), JSONConvertable {
    var arrayMutableWalletViewModel = MutableLiveData<UserWalletTransactions>()
    var errorMutableViewModel = MutableLiveData<String>()

    fun getWalletTransactions(json: JSONObject?){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal,
            ApplicationConstant.USERWALLETHISTORY, json, null, null, false
        )
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal) {
            val userBalance = (response["0"] as JSONObject).get("userWalletBalance").toString()
            if (!(response["1"] as JSONObject).isNull("userOrderHistory")) {
                var arrWalletHistory = ArrayList<WalletHistoryData>()
                (response["1"] as JSONObject).getJSONObject("userOrderHistory").keys().forEach {
                    val jsonObj = ((response["1"] as JSONObject).getJSONObject("userOrderHistory").get(it) as JSONObject)
                    val walletHistory = Gson().fromJson(jsonObj.toString(), WalletHistoryData::class.java)
                    arrWalletHistory.add(walletHistory)
                }
                val userWallet = UserWalletTransactions(userBalance, arrWalletHistory)
                arrayMutableWalletViewModel.postValue(userWallet)
            } else {
                errorMutableViewModel.postValue("Error")
            }
        }
    }
}

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T: JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)