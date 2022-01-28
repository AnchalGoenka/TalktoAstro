package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.model.*
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType

class NotificationViewModel constructor(app:BaseApplication):BaseViewModel(app) {

    val arrayNotificationMutableLiveData = MutableLiveData<ArrayList<NotificationList>>()
    var arrayNotificationModel =ArrayList<NotificationList>()
    var notificationDidFailed = MutableLiveData<String>()


    init {
        notification()
    }

    fun notification(){

        var jsonObj = JSONObject()
        jsonObj.put("apptype", "user")

        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.APPNOTIFICATION.ordinal,
            ApplicationConstant.APP_NOTIFICATION, jsonObj, null, null, false)
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)

        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.APPNOTIFICATION.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), NotificationModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<NotificationModel>() {
                    override fun onParseComplete(data: NotificationModel) {
                        arrayNotificationModel = data.notificationlist
                        arrayNotificationMutableLiveData.postValue(data.notificationlist)
                    }

                    override fun onParseFailure(data: NotificationModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error ")
                    }
                })
        }

    }

}
