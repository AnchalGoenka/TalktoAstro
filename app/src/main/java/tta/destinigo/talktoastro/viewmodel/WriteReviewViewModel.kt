package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-08-18.

 */
class WriteReviewViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {
    var arrayListMutableLiveData = MutableLiveData<String>()

    fun submitReview(json: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.WRITEREVIEW.ordinal,
            ApplicationConstant.ASTRO_REVIEW, json, null, null, false
        )
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.WRITEREVIEW.ordinal) {
            if (response.get("success") == 1){
                arrayListMutableLiveData.postValue(response.get("message").toString())
            }
        }
    }
}