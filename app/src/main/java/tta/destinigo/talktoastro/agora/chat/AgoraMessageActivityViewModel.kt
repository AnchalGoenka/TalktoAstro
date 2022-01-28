package tta.destinigo.talktoastro.agora.chat

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType

class AgoraMessageActivityViewModel constructor(app: BaseApplication) : BaseViewModel(app) {
    var saveChat = MutableLiveData<Boolean>()

    fun saveChat(jsonObject: JSONObject, chatId: String){
        val url = ApplicationConstant.ChatBaseUrl +"$chatId"+"/save"
        doApiRequest(
            RequestType.POST, RequestIdentifier.SAVECHAT.ordinal,
            url, jsonObject, null, null, false
        )
    }

    fun endchat( chatId: String){
        val url = ApplicationConstant.ChatBaseUrl +"$chatId"+"/end"
        doApiRequest(
            RequestType.POST, RequestIdentifier.ENDCHAT.ordinal,
            url, null, null, null, false
        )
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
            if(identifier==RequestIdentifier.SAVECHAT.ordinal){
               saveChat.postValue(response["status"] as Boolean)
            }
            if(identifier==RequestIdentifier.ENDCHAT.ordinal){

            }
    }
}