package tta.destinigo.talktoastro.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.model.AnswerList
import tta.destinigo.talktoastro.model.AnswerModel
import tta.destinigo.talktoastro.model.NotificationList
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType
import tta.destinigo.talktoastro.volley.gson.GsonHelper
import tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener
import java.util.*
import kotlin.collections.ArrayList

class AnswerViewModel constructor(app:BaseApplication) :BaseViewModel(app){
    val arrayAnswerMutableLiveData = MutableLiveData<ArrayList<AnswerList>>()
    var arrayAnswerModel =ArrayList<AnswerList>()
    var answerSubmitListMutableLiveData = MutableLiveData<String>()

    fun getAnswer(jsonObject: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.HUBANSWER.ordinal,
            ApplicationConstant.hubAnswer, jsonObject, null, null, false)
    }

    fun getAnswerSubmit(jsonObject: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.HUBANSWERSUBMIT.ordinal,
            ApplicationConstant.hubAnswerSubmit, jsonObject, null, null, false)
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier==RequestIdentifier.HUBANSWER.ordinal){
            GsonHelper.getInstance().parse(response.toString(),AnswerModel::class.java,object:OnGsonParseCompleteListener<AnswerModel>(){
                override fun onParseComplete(data: AnswerModel) {
                    arrayAnswerModel = data.answerList
                    Collections.reverse(arrayAnswerModel)
                    arrayAnswerMutableLiveData.postValue(arrayAnswerModel)
                }

                override fun onParseFailure(data: AnswerModel?) {
                    tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    Toast.makeText(ApplicationUtil.getContext(),data?.message, Toast.LENGTH_SHORT).show()
                }

            } )
        }
        if(identifier==RequestIdentifier.HUBANSWERSUBMIT.ordinal){
            if (response.get("success") == 1){
                answerSubmitListMutableLiveData.postValue(response.get("message").toString())
            }
        }


    }
}