package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.ReportHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-08-29.

 */
class ReportHistoryViewModel constructor(private val mApplication: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(mApplication), JSONConvertable {
    var arrayMutableReportHistory = MutableLiveData<ArrayList<ReportHistoryModel>>()
    var arrOfReportHistoryModel = ArrayList<ReportHistoryModel>()

    init {
        getReportHistory()
    }

    fun getReportHistory() {
        var json = JSONObject()
        //json.put("userID", "469")
        json.put(
            "userID", SharedPreferenceUtils.readString(
                ApplicationConstant.USERID, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        )
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTHISTORY.ordinal,
            ApplicationConstant.USERREPORTHISTORY, json, null, null, false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTHISTORY.ordinal) {
            response.keys().forEach {
                val jsonObj = response.get(it) as JSONObject
                val reportHistory = Gson().fromJson(jsonObj.toString(), ReportHistoryModel::class.java)
                arrOfReportHistoryModel.add(reportHistory)
            }
            arrayMutableReportHistory.postValue(arrOfReportHistoryModel)

        }

    }
}