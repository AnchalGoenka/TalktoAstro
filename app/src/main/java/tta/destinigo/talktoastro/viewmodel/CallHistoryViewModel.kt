package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.CallHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-08-29.

 */
class CallHistoryViewModel constructor(private val mApplication: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(mApplication), JSONConvertable {
    var arrayMutableCallHistory = MutableLiveData<ArrayList<CallHistoryModel>>()
    var arrOfCallHistoryModel = ArrayList<CallHistoryModel>()

    init {
        getCallHistory()
    }

    fun getCallHistory() {
        var json = JSONObject()
        json.put(
            "userID", SharedPreferenceUtils.readString(
                ApplicationConstant.USERID, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        )
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.CALLHISTORY.ordinal,
            ApplicationConstant.USER_CALL_HISTORY, json, null, null, false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.CALLHISTORY.ordinal) {
            response.keys().forEach {
                val jsonObj = response.get(it) as JSONObject
                val callHistory = Gson().fromJson(jsonObj.toString(), CallHistoryModel::class.java)
                arrOfCallHistoryModel.add(callHistory)
            }
            arrayMutableCallHistory.postValue(arrOfCallHistoryModel)

        }

    }
}