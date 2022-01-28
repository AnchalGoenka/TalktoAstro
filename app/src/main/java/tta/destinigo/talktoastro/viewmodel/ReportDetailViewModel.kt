package tta.destinigo.talktoastro.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.ReportBirthDetailModel
import tta.destinigo.talktoastro.model.ReportDetailModel
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-09-18.

 */
class ReportDetailViewModel constructor(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
    BaseViewModel(mApplication), JSONConvertable {
    var arrayMutableReportDetail = MutableLiveData<ArrayList<ReportDetailModel>>()
    var arrReportDetail = ArrayList<ReportDetailModel>()
    var reportBirthDetail = MutableLiveData<ReportBirthDetailModel>()
    var reportCommentResp = MutableLiveData<String>()

    fun getReportDetails(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTDETAIL.ordinal,
            ApplicationConstant.REPORTCOMMENTS, json, null, null, false
        )
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTREQUIREMENT.ordinal,
            ApplicationConstant.REPORT_REQIUREMENT, json, null, null, false
        )
    }

    fun submitReportComments(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTCOMMENTSUBMIT.ordinal,
            ApplicationConstant.REPORT_COMMENTS_SUBMIT, json, null, null, false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTDETAIL.ordinal) {
            arrReportDetail.clear()
            response.keys().forEach {
                val jsonObj = response.get(it) as JSONObject
                val reportDetail =
                    Gson().fromJson(jsonObj.toString(), ReportDetailModel::class.java)
                arrReportDetail.add(reportDetail)
            }
            arrayMutableReportDetail.postValue(arrReportDetail)
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTREQUIREMENT.ordinal) {
            val jsonObj = response.get("0") as JSONObject
            val birthDetail =
                Gson().fromJson(jsonObj.toString(), ReportBirthDetailModel::class.java)
            reportBirthDetail.postValue(birthDetail)

            arrayMutableReportDetail.postValue(arrReportDetail)
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTCOMMENTSUBMIT.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), TokenModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<TokenModel>() {
                    override fun onParseComplete(data: TokenModel) {
                        if (data.success == 1) {
                            reportCommentResp.postValue(data.message)
                        } else {
                            Toast.makeText(ApplicationUtil.getContext(), data.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onParseFailure(data: TokenModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }

    }

}