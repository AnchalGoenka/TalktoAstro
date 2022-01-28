package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.model.ForgotPasswordModel
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier

class ForgotPasswordViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) :
    BaseViewModel(app) {
    val mutableForgotPasswordData = MutableLiveData<ForgotPasswordModel>()

    fun changePassword(json: JSONObject) {
        doApiRequest(
            RequestType.POST,
            RequestIdentifier.FORGOTPASSWORD.ordinal,
            ApplicationConstant.FORGOT_PASSWORD,
            json,
            null,
            null,
            false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == RequestIdentifier.FORGOTPASSWORD.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response.toString(), ForgotPasswordModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ForgotPasswordModel>() {
                        override fun onParseComplete(data: ForgotPasswordModel) {
                            mutableForgotPasswordData.postValue(data)
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