package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.model.CoupanCodeModel
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.volley.gson.GsonHelper
import tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener

class OrderPreviewViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app)  {

    var coupanDidAvailed = MutableLiveData<CoupanCodeModel>()
    var coupanDidFailed = MutableLiveData<String>()

    fun applyCoupanCode(jsonObj: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.COUPAN.ordinal,
            ApplicationConstant.COUPAN, jsonObj, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.COUPAN.ordinal) {
            GsonHelper.getInstance().parse(response.toString(), CoupanCodeModel::class.java,
                object : OnGsonParseCompleteListener<CoupanCodeModel>() {
                    override fun onParseComplete(data: CoupanCodeModel) {
                        when (data.success){
                            1 -> {
                                coupanDidAvailed.postValue(data)
                            }
                            0 -> {
                                coupanDidFailed.postValue(data.message)
                            }
                        }
                    }

                    override fun onParseFailure(data: CoupanCodeModel) {
                        LogUtils.d("coupan code parsing failed", "coupan code parsing failed")
                    }
                })
        }
    }
}