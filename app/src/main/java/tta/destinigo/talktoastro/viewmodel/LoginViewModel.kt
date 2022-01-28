package tta.destinigo.talktoastro.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import tta.destinigo.talktoastro.model.LoginModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.model.LoginModelWithOTP
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier


/**

 * Created by Vivek Singh on 2019-06-30.

 */
class LoginViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {

    var arrayListMutableLiveData = MutableLiveData<LoginModel>()
    var loginWithOTPMutableLiveData = MutableLiveData<LoginModelWithOTP>()
    var loginDidFailed = MutableLiveData<String>()
    var loginViaSMs = MutableLiveData<String>()

    init {

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


         val jsonObj =JSONObject()
            jsonObj.put("udid",token)
            jsonObj.put("device_token",token)
            jsonObj.put("user_id",userID)

            doApiRequest(
                RequestType.POST, RequestIdentifier.DEVICTOKEN.ordinal,
                ApplicationConstant.DEVICE_TOKEN, jsonObj, null, null, false
            )

    }

    fun loginUser(jsonObj: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.LOGIN.ordinal,
            ApplicationConstant.LOGIN, jsonObj, null, null, false)
    }

    fun loginViaSMS(mobileNo:String){

        val  loginVIaSMS = "${ApplicationConstant.LOGINVIASMS}${mobileNo}/AUTOGEN/${ApplicationConstant.TWOfactorTEMPLATE_NAME}  "
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.LOGINVIASMS.ordinal,
            loginVIaSMS , null, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.LOGIN.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), LoginModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<LoginModel>() {
                    override fun onParseComplete(data: LoginModel) {
                        when(data.success) {
                             1 -> {
                                arrayListMutableLiveData.postValue(data)
                                // loginWithOTPMutableLiveData.postValue(data)

                            }
                            0 -> {
                                loginDidFailed.postValue(data.message)
                            }
                        }
                    }

                    override fun onParseFailure(data: LoginModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                        Toast.makeText(ApplicationUtil.getContext(),data.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }

        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.LOGINVIASMS.ordinal ){

            val Details = response["Details"].toString()
            if(response["Status"].toString()=="Success"){
            loginViaSMs.postValue(Details)

            } else{
                loginDidFailed.postValue(response["Status"].toString())
            }

        }
    }

    override fun onApiResponse(
        identifier: Int,
        response: String,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.DEVICTOKEN.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response, TokenModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<TokenModel>() {
                        override fun onParseComplete(data: TokenModel) {
                            if (data.success == 1) {
                                Toast.makeText(ApplicationUtil.getContext(), "Token sent ${data.message}", Toast.LENGTH_LONG).show()
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
}