package tta.destinigo.talktoastro.chat.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.chat.models.ChatFormModel
import tta.destinigo.talktoastro.chat.models.ChatFormSubmitResponseModel
import tta.destinigo.talktoastro.chat.models.ChatHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.RequestIdentifier
import tta.destinigo.talktoastro.volley.ext.RequestType
import tta.destinigo.talktoastro.volley.gson.GsonHelper
import tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener


/**

 * Created by Vivek Singh on 5/11/20.

 */

class ChatFormViewModel constructor(app: BaseApplication) : BaseViewModel(app) {
    var arrayListMutableLiveData = MutableLiveData<ChatFormModel>()

    var chatFormSubmitSuccess  = MutableLiveData<ChatFormSubmitResponseModel>()
    var chatFormSubmitFailure  = MutableLiveData<String>()

    var arrayMutableChatHistory = MutableLiveData<ArrayList<ChatHistoryModel>>()
    var arrOfChatHistoryModel = ArrayList<ChatHistoryModel>()
    var astrochatStatus = MutableLiveData<String>()
    var notificationsend = MutableLiveData<Boolean>()

    fun getChatForm(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.GETCHATFORM.ordinal,
            ApplicationConstant.GETCHATFORM, json, null, null, false
        )
    }

    fun submitChatForm(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.SUBMITCHATFORM.ordinal,
            ApplicationConstant.SUBMITCHATFORM, json, null, null, false
        )
    }

    fun getAstroChatStatus(json: JSONObject?) {
        doApiRequest(
            RequestType.POST,RequestIdentifier.GETASTROSTATUS.ordinal,
            ApplicationConstant.GET_ASTRO_Chat_STATUS, json, null, null, false
        )

    }

    fun changeChatStatus(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.CHANGECHATSTATUS.ordinal,
            ApplicationConstant.CHANGECHATSTATUS, json, null, null, false
        )
    }

    fun sendChatLog(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.CHATLOG.ordinal,
            ApplicationConstant.CHATLOG, json, null, null, false
        )
    }

    fun getChatHistory(json: JSONObject) {
        doApiRequest(
            RequestType.POST, RequestIdentifier.CHATHISTORY.ordinal,
            ApplicationConstant.CHATHISTORY, json, null, null, false
        )
    }

    fun fcmNotification(chatId: String){
        val url =ApplicationConstant.ChatBaseUrl +"$chatId"+"/notification"
        doApiRequest(
            RequestType.POST, RequestIdentifier.CHATFCMNOTIFICATION.ordinal,
            url, null, null, null, false
        )
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)

        if (identifier == RequestIdentifier.GETCHATFORM.ordinal) {
            GsonHelper.getInstance().parse(response.toString(), ChatFormModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ChatFormModel>() {
                    override fun onParseComplete(data: ChatFormModel) {
                        arrayListMutableLiveData.postValue(data)
                    }

                    override fun onParseFailure(data: ChatFormModel) {
                      //  LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
        if (identifier == RequestIdentifier.GETASTROSTATUS.ordinal) {
            Log.d("chat_status",response["chat_status"].toString())
            if (response["chat_status"] == "online") {
                astrochatStatus.postValue(ApplicationConstant.ONLINE)

            }else if (response["chat_status"] == ApplicationConstant.BUSYSTATUS){

                astrochatStatus.postValue(ApplicationConstant.BUSY)

            }
            else {
                astrochatStatus.postValue(ApplicationConstant.OFFLINE)
            }

        }
        if (identifier == RequestIdentifier.SUBMITCHATFORM.ordinal) {
            GsonHelper.getInstance().parse(response.toString(), ChatFormSubmitResponseModel::class
                .java,
                object : OnGsonParseCompleteListener<ChatFormSubmitResponseModel>() {
                    override fun onParseComplete(data: ChatFormSubmitResponseModel?) {
                        if (data?.success == 1) {
                            chatFormSubmitSuccess.postValue(data)
                        } else {
                            chatFormSubmitFailure.postValue(data?.message)
                        }
                    }

                    override fun onParseFailure(data: ChatFormSubmitResponseModel?) {
                      //  LogUtils.d("Parsing failed for chat form submit response.")
                    }
                }
            )
        }
        if (identifier == RequestIdentifier.CHATLOG.ordinal) {
           // LogUtils.d(response.toString())
        }
        if (identifier == RequestIdentifier.CHANGECHATSTATUS.ordinal) {
           // LogUtils.d(response.toString())
        }
        if (identifier == RequestIdentifier.CHATHISTORY.ordinal) {
            GsonHelper.getInstance().parse(response.toString(), ChatHistoryModel::class
                .java,
                object : OnGsonParseCompleteListener<ChatHistoryModel>() {
                    override fun onParseComplete(data: ChatHistoryModel?) {
                        response.keys().forEach {
                            val jsonObj = response.get(it) as JSONObject
                            val chatHistory = Gson().fromJson(jsonObj.toString(), ChatHistoryModel::class.java)
                            arrOfChatHistoryModel.add(chatHistory)
                        }
                        arrayMutableChatHistory.postValue(arrOfChatHistoryModel)
                    }
                    override fun onParseFailure(data: ChatHistoryModel?) {
                       // LogUtils.d("Parsing failed for chat form submit response.")
                    }
                }
            )
        }

        if(identifier == RequestIdentifier.CHATFCMNOTIFICATION.ordinal){
            notificationsend.postValue(response["status"] as Boolean?)
           // BaseApplication.instance.showToast(">>>>>${response["status"]}")
        }
    }
}