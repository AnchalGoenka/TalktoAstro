package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.model.PrivacyPolicyModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject

class PrivacyPolicyViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {
    var getPrivacyPolicyObserver = MutableLiveData<PrivacyPolicyModel>()

    init {
        getPrivacyPolicy()
    }

    private fun getPrivacyPolicy() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.PRIVACYPOLICY.ordinal,
            ApplicationConstant.PRIVACY_POLICY, null, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PRIVACYPOLICY.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), PrivacyPolicyModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PrivacyPolicyModel>() {
                    override fun onParseComplete(data: PrivacyPolicyModel) {
                        getPrivacyPolicyObserver.postValue(data)
                    }
                    override fun onParseFailure(data: PrivacyPolicyModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
    }

}