package tta.destinigo.talktoastro.agora.chat

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.mooveall.driver.base.appUpdate.AppVersionResponse
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.model.viewChatModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType

class ViewChatHistoryViewModel constructor(app: BaseApplication) : BaseViewModel(app)  {

    var ViewChatResponse = MutableLiveData<viewChatModel>()
    fun ViewChat(chatId:String){
        val url = ApplicationConstant.ChatBaseUrl +"$chatId"+"/details"
        doApiRequest(
            RequestType.GET, RequestIdentifier.VIEWCHAT.ordinal,
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
        if (identifier == RequestIdentifier.VIEWCHAT.ordinal) {
            val mParsedResponse = Gson().fromJson(response.toString(), viewChatModel::class.java)
            ViewChatResponse.postValue(mParsedResponse)
        }
    }
}