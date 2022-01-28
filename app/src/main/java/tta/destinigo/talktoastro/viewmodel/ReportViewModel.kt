package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.ReportList
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-07-17.

 */
class ReportViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {
    val arrayListMutableReportData = MutableLiveData<ArrayList<ReportList>>()//MutableLiveData<ArrayList<ReportList>>()
    var arrayParentModelData = ArrayList<ReportList>()

    init {
    }

    fun getReports(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTPRICE.ordinal,
            ApplicationConstant.REPORT_PRICE, json, null, null, false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTPRICE.ordinal) {
            response.keys().forEach {
                val jsonObj = response.get(it) as JSONObject
                val reportList = Gson().fromJson(jsonObj.toString(), ReportList::class.java)
                arrayParentModelData.add(reportList)
            }
            arrayListMutableReportData.postValue(arrayParentModelData)
        }
    }
}