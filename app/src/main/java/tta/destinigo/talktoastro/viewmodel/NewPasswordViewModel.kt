package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.model.ForgotPasswordModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType


/**

 * Created by Vivek Singh on 2019-10-29.

 */
class NewPasswordViewModel(app: BaseApplication) : BaseViewModel(app) {
    val mutableNewPasswordData = MutableLiveData<ForgotPasswordModel>()

    fun changePassword(json: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.RESETPASSWORD.ordinal,
            ApplicationConstant.RESET_NEW_PASSWORD, json, null, null, false)
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == RequestIdentifier.RESETPASSWORD.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response.toString(), ForgotPasswordModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ForgotPasswordModel>() {
                        override fun onParseComplete(data: ForgotPasswordModel) {
                            mutableNewPasswordData.postValue(data)
                        }

                        override fun onParseFailure(data: ForgotPasswordModel) {
                            LogUtils.d(
                                "forgot password model",
                                "parse failed due to error "
                            )
                        }
                    })
        }
    }
}