package tta.destinigo.talktoastro.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mooveall.driver.base.appUpdate.AppVersionResponse
import org.json.JSONObject
import tta.destinigo.talktoastro.model.BannerModel
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType


/**

 * Created by Vivek Singh on 2019-06-18.

 */
class MainViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {
    var arrayBannerModelData = ArrayList<BannerModel>()
    var arrayListMutableBannerData = MutableLiveData<ArrayList<BannerModel>>()
    var balanceUpdateResp = MutableLiveData<String>()
    var appVersionReq = MutableLiveData<AppVersionResponse>()

    init {
        getBanner()
     //   passDeviceTokenToServer()
    }

    fun passDeviceTokenToServer() {
        val userID = SharedPreferenceUtils.readString(
            ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        var token = SharedPreferenceUtils.readString(
            ApplicationConstant.DEVICETOKEN, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val isNotificationEnabled = SharedPreferenceUtils.readBoolean(
            ApplicationConstant.ISDEVICETOKENENABLED, true,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        if (userID!!.isNotEmpty() && token!!.isNotEmpty()) {
            Log.d("token>>>>>>>>",token)
            var url: String = ""
            if (isNotificationEnabled) {
                url =
                    "${ApplicationConstant.BASE_URL}/udid.php?udid=$token&device_token=$token&user_id=$userID"
            } else {
                token = ""
                url =
                    "${ApplicationConstant.BASE_URL}/udid.php?udid=$token&device_token=$token&user_id=$userID"
            }

            doApiRequest(
                RequestType.GET, RequestIdentifier.DEVICTOKEN.ordinal,
                url, null, null, null, false
            )
        }
    }

    private fun getBanner() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.BANNER.ordinal,
            ApplicationConstant.ACTIVE_BANNER, null, null, null, false
        )
    }

    fun getAppVersion() {
        doApiRequest(
            RequestType.GET,
            tta.destinigo.talktoastro.volley.RequestIdentifier.GETAPPVERSION.ordinal,
            ApplicationConstant.GET_APP_VERSION,
            null,
            null,
            null,
            false
        )
    }

    fun getWalletTransactions(json: JSONObject?) {
        doApiRequest(
            RequestType.POST,
            tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal,
            ApplicationConstant.USERWALLETHISTORY,
            json,
            null,
            null,
            false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: String,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        Log.i("TAG", "" + response)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.BANNER.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response, BannerModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<BannerModel>() {
                        override fun onParseComplete(data: BannerModel) {
                            arrayBannerModelData.add(data)
                            arrayListMutableBannerData.postValue(arrayBannerModelData)
                        }

                        override fun onParseFailure(data: BannerModel) {
                            tta.destinigo.talktoastro.util.LogUtils.d(
                                "place list view model",
                                "parse failed due to error "
                            )
                        }
                    })
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.DEVICTOKEN.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response, TokenModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<TokenModel>() {
                        override fun onParseComplete(data: TokenModel) {
                            if (data.success == 1) {
                                //Toast.makeText(ApplicationUtil.getContext(), "Token sent ${data.message}", Toast.LENGTH_LONG).show()
                            } else {
                                //Toast.makeText(ApplicationUtil.getContext(), data.message, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onParseFailure(data: TokenModel) {
                            tta.destinigo.talktoastro.util.LogUtils.d(
                                "place list view model",
                                "parse failed due to error "
                            )
                        }
                    })
        }
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        Log.i("TAG", "" + response)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal) {
            val userBalance = (response["0"] as JSONObject).get("userWalletBalance").toString()
            balanceUpdateResp.postValue(userBalance)
        } else if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.GETAPPVERSION.ordinal) {
            val mParsedResponse = Gson().fromJson(response.toString(), AppVersionResponse::class.java)
            appVersionReq.postValue(mParsedResponse)
        }
    }
}