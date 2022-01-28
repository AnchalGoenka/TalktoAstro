package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-09-01.

 */
class ProcessReportViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {

    var respMutableData = MutableLiveData<String>()
    var respMutableError = MutableLiveData<String>()

    init {
    }

    fun getReports(data: HashMap<String, String>) {
        val json = JSONObject(data as Map<*, *>)
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.PROCESSREPORT.ordinal,
            ApplicationConstant.PROCESSREPORT, json, null, null, false
        )
    }


    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PROCESSREPORT.ordinal) {
            if (response.get("success") == 1) {
                respMutableData.postValue("success")
            } else {
                respMutableError.postValue(response.get("message").toString())
            }
        }
    }
}