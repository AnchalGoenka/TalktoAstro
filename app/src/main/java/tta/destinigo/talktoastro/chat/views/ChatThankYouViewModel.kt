package tta.destinigo.talktoastro.chat.views

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.json.JSONObject
import tta.destinigo.talktoastro.chat.models.ChatFormModel
import tta.destinigo.talktoastro.chat.models.ChatFormSubmitResponseModel
import tta.destinigo.talktoastro.chat.models.ChatHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType
import tta.destinigo.talktoastro.volley.gson.GsonHelper
import tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener

class ChatThankYouViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app)  {
    var joinStatusCheck = MutableLiveData<Boolean>()
    var mesageCheck = MutableLiveData<String>()
    fun checkAstroChatJoinStatus(chatId: String){
        val url = ApplicationConstant.ChatBaseUrl +"$chatId"+"/status"
        doApiRequest(
            RequestType.GET, RequestIdentifier.CHATFCMNOTIFICATION.ordinal,
            url, null, null, null, false
        )
    }

    fun changeChatStatus(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.CHANGECHATSTATUS.ordinal,
            ApplicationConstant.CHANGECHATSTATUS, json, null, null, false
        )
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)


        if(identifier == RequestIdentifier.CHATFCMNOTIFICATION.ordinal){

            if(response["success"]== 1){
                joinStatusCheck.postValue(response["status"] as Boolean?)
                mesageCheck.postValue(response["message"].toString())

            } else{

            }

        }

        if (identifier == RequestIdentifier.CHANGECHATSTATUS.ordinal) {
            // LogUtils.d(response.toString())
        }
    }
}