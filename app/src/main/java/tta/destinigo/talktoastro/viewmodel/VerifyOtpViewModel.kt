package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import org.json.JSONObject
import tta.destinigo.talktoastro.model.LoginModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType


/**

 * Created by Vivek Singh on 2019-06-25.

 */
class VerifyOtpViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {

    var arrayListMutableLiveData = MutableLiveData<LoginModel>()
    var loginDidFailed = MutableLiveData<String>()
    var resendOTPMutableLiveData = MutableLiveData<LoginModel>()
    var verifyOTPWithTWOFACTOR = MutableLiveData<String>()
    var resendVia2Factor = MutableLiveData<String>()

    init {
    }

    fun verifyOtp(jsonObj: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTP.ordinal,
            ApplicationConstant.LOGIN, jsonObj, null, null, false)
    }

    fun resendOTPRequest(jsonObj: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.LOGIN.ordinal,
            ApplicationConstant.LOGIN, jsonObj, null, null, false)
    }

    fun verifyOTPWITHTWOFACTOR(session_id:String,otp:String){
        val  loginVIaSMS = "${ApplicationConstant.LOGINVIASMS}VERIFY/${session_id}/${otp}  "

        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTPWITH.ordinal,
            loginVIaSMS, null, null, null, false)
    }

    fun resendWith2Factor(mobileNo:String){

        val  loginVIaSMS = "${ApplicationConstant.LOGINVIASMS}${mobileNo}/AUTOGEN/${ApplicationConstant.TWOfactorTEMPLATE_NAME}  "
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.LOGINVIASMS.ordinal,
            loginVIaSMS , null, null, null, false)
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTP.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), LoginModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<LoginModel>() {
                    override fun onParseComplete(data: LoginModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("OTP verify response: $data")
                        when(data.success) {
                            1 -> {
                                arrayListMutableLiveData.postValue(data)
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
        else if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.LOGIN.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), LoginModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<LoginModel>() {
                    override fun onParseComplete(data: LoginModel) {
                        when(data.success) {
                            1 -> {
                                resendOTPMutableLiveData.postValue(data)
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
        else if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTPWITH.ordinal){
            val Details = response["Details"].toString()
                if(response["Status"].toString()=="Success"){
                    verifyOTPWithTWOFACTOR.postValue(Details)

                }
            else{
                    loginDidFailed.postValue("Api Error")
                }

        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.LOGINVIASMS.ordinal ){

            val Details = response["Details"].toString()
            if(response["Status"].toString()=="Success"){
                resendVia2Factor.postValue(Details)

            } else{
                loginDidFailed.postValue(response["Status"].toString())
            }

        }
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {
        super.onApiError(identifier, error, errorCode)
         if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTPWITH.ordinal){
             loginDidFailed.postValue("Incorrect Pin")

        }
    }
    //    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
//        super.onApiResponse(identifier, response, serverDate, lastModified)
//        var response = response
//        response = response.substring(response.indexOf("{", 0, true),response.length)
//        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.VERIFYOTP.ordinal) {
//            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response, LoginModel::class.java,
//                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<LoginModel>() {
//                    override fun onParseComplete(data: LoginModel) {
//                        tta.destinigo.talktoastro.util.LogUtils.d("OTP verify response: $data")
//                        when(data.success) {
//                            1 -> {
//                                arrayListMutableLiveData.postValue(data)
//                            }
//                            0 -> {
////                                SharedPreferenceUtils.put(ApplicationConstant.USERNAME, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
////                                SharedPreferenceUtils.put(ApplicationConstant.PASSWORD, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
//                            }
//                        }
//                    }
//
//                    override fun onParseFailure(data: LoginModel) {
//                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
//                        Toast.makeText(ApplicationUtil.getContext(),data.message, Toast.LENGTH_SHORT).show()
//                    }
//                })
//        }
//    }
}