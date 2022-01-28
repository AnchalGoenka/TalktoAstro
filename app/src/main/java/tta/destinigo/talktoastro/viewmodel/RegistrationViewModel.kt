package tta.destinigo.talktoastro.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import tta.destinigo.talktoastro.model.RegistrationModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.volley.RequestIdentifier


/**

 * Created by Vivek Singh on 2019-06-25.

 */
class RegistrationViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {

    var arrayListMutableLiveData = MutableLiveData<RegistrationModel>()
    var registrationFailed = MutableLiveData<String>()
    var loginViaSMs = MutableLiveData<String>()

    init {
    }

    fun registerUser(json: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REGISTRATION.ordinal,
            ApplicationConstant.REGISTRATIONS, json, null, null, false)
    }
    fun loginViaSMS(mobileNo:String){

        val  loginVIaSMS = "${ApplicationConstant.LOGINVIASMS}${mobileNo}/AUTOGEN/${ApplicationConstant.TWOfactorTEMPLATE_NAME}  "
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.LOGINVIASMS.ordinal,
            loginVIaSMS , null, null, null, false)
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
    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
//        var response = response
//        response = response.substring(response.indexOf("{", 0, true),response.length)
        Log.d("register",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REGISTRATION.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), RegistrationModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<RegistrationModel>() {
                    override fun onParseComplete(data: RegistrationModel) {
                        when (data.success) {
                            "1" -> {
                                /*tta.destinigo.talktoastro.util.LogUtils.d("OTP: ${data.otp}")
                                Toast.makeText(ApplicationUtil.getContext()," a ${data.otp}",Toast.LENGTH_LONG).show()
                                tta.destinigo.talktoastro.util.LogUtils.d("UserID: ${data.userId}")*/
                                SharedPreferenceUtils.put(ApplicationConstant.OTP, data.otp, SharedPreferenceUtils.getSharedPref(
                                    ApplicationUtil.getContext()))
                                SharedPreferenceUtils.put(ApplicationConstant.USERID, data.userId, SharedPreferenceUtils.getSharedPref(
                                    ApplicationUtil.getContext()))
                                arrayListMutableLiveData.postValue(data)
                            }
                            "0" -> {
                                registrationFailed.postValue(data.message)
                            }
                        }
                    }

                    override fun onParseFailure(data: RegistrationModel) {
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
                registrationFailed.postValue(response["Status"].toString())
            }

        }
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {
        super.onApiError(identifier, error, errorCode)
       // registrationFailed.postValue(error.toString() )
        Toast.makeText(ApplicationUtil.getContext(),"Error"+error +"   "+errorCode,Toast.LENGTH_LONG).show()
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
}