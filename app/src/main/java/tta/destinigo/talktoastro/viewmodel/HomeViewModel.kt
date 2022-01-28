package tta.destinigo.talktoastro.viewmodel

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.*
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier

/**

 * Created by Vivek Singh on 2019-06-09.

 */

class HomeViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {

    var arrayListMutableLiveData = MutableLiveData<ArrayList<AstrologerListModel>>()
    var arrayParentModelData = ArrayList<AstrologerListModel>()
    val arrayListMutableReportData = MutableLiveData<ArrayList<AstrologerListModel>>()
    val MutableCallData = MutableLiveData<CallModel>()
    val  notificationRespone = MutableLiveData<String>()
    val notificationResponeFailed = MutableLiveData<String>()
    val checkNotification = MutableLiveData<HashMap<String, String>>()
    val removeNotification = MutableLiveData<HashMap<String, String>>()
    val rtmToken= MutableLiveData<String>()
    var balanceUpdateResp = MutableLiveData<String>()
    var astroStatus = MutableLiveData<String>()

    init {
     //  rtmToken()
    }

    fun getAstrologersList(){
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.ASTROLOGER_LIST.ordinal,
            ApplicationConstant.ASTROLOGER_LIST, null, null, null, false)
    }

    fun getChatList() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.CHAT_LIST.ordinal,
            ApplicationConstant.ASTROLOGER_LIST, null, null, null, false)
    }

    fun getReports() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTS.ordinal,
            ApplicationConstant.REPORTS, null, null, null, false
        )
    }

    fun getAstroStatus(json: JSONObject?) {
        doApiRequest(
            RequestType.POST,RequestIdentifier.GETASTROSTATUS.ordinal,
            ApplicationConstant.GET_ASTRO_Chat_STATUS, json, null, null, false
        )

    }

    fun getWalletTransactions(json: JSONObject?) {
        doApiRequest(
            RequestType.POST,
            tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal,
            ApplicationConstant.USERWALLETHISTORY,
            json,
            null,
            null,
            false
        )
    }

    fun callAstrologer(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.CALLASTROLOGER.ordinal,
            ApplicationConstant.CALLAPI, json, null, null, false
        )
    }

    fun setNotifyIcon(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.NOTIFYICON.ordinal,
            ApplicationConstant.notificationIcon, json, null, null, false
        )
    }

    fun removeNotification(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.REMOVENOTIFICATION.ordinal,
            ApplicationConstant.removeNotifcation, json, null, null, false
        )
    }

    fun checkNotification(){
        val userID = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        var json = JSONObject();
        json.put("userID", userID)
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.CHECKNOTIFICATION.ordinal,
            ApplicationConstant.checkNotifcation, json, null, null, false
        )
    }

    fun rtmToken(){
        val userID = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        var json = JSONObject();
        json.put("userID", userID)
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.RTMTOKEN.ordinal,
            ApplicationConstant.RTMTOKEN, json, null, null, false
        )
    }


    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.ASTROLOGER_LIST.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response, ProfileList::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ProfileList>() {
                    override fun onParseComplete(data: ProfileList) {
                        data.astrologerModel.forEach {
                            val astrologerList: AstrologerListModel = AstrologerListModel(
                                it.id, it.firstName, it.lastName,
                                it.url, it.metaTitle, it.metaDescription,
                                it.totalRatings, it.phoneStatus, "", it.image,

                                it.expertise, "${it.experience} years",
                                it.price, it.foreigndisplayprice, it.languages, it.ratingAvg,it.justComing,
                                it.about, it.isReport, it.orderBy, it.isflash, it.flashdisplayprice, "", "", "", it.isFreeMin ,"",it.callMin,it.reportNum,it.verified,it.audio
                            )

                            arrayParentModelData.add(astrologerList)
                        }
                        val busyArr = arrayParentModelData.filter { obj -> obj.phoneStatus == "busy" }
                        val onlineArr = arrayParentModelData.filter { obj -> obj.phoneStatus == "online" }
                        val sortOnlineArr = onlineArr.sortedBy { obj -> obj.orderBy }
                        val offlineArr = arrayParentModelData.filter { obj -> obj.phoneStatus == "offline" }
                        arrayParentModelData.clear()
                        busyArr.forEach {
                            arrayParentModelData.add(it)
                        }
                        sortOnlineArr.forEach {
                            arrayParentModelData.add(it)
                        }
                        offlineArr.forEach {
                            arrayParentModelData.add(it)
                        }

                        arrayListMutableLiveData.postValue(arrayParentModelData)
                    }

                    override fun onParseFailure(data: ProfileList) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.CHAT_LIST.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response, ProfileList::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ProfileList>() {
                    override fun onParseComplete(data: ProfileList) {
                        data.astrologerModel.forEach {
                            if (it.isChat == "1") {
                                val astrologerList: AstrologerListModel = AstrologerListModel(
                                    it.id, it.firstName, it.lastName, it.url, it.metaTitle,
                                    it.metaDescription, it.totalRatings, it.phoneStatus, it.chatStatus,
                                    it.image, it.expertise, "Exp: ${it.experience} years",
                                    it.price, it.foreigndisplayprice, it.languages, it.ratingAvg,
                                    it.justComing, it.about, it.isReport, it.orderBy, it.isflash,
                                    it.flashdisplayprice, it.chatPrice, it.chatforeignDisplayPrice,
                                    it.isChat, it.isFreeMin, "", it.callMin,it.reportNum, it.verified,it.audio
                                )

                                arrayParentModelData.add(astrologerList)
                            }
                        }
                        val busyArr = arrayParentModelData.filter { obj -> obj.chatStatus == "busy" }
                        val onlineArr = arrayParentModelData.filter { obj -> obj.chatStatus == "online" }
                        val sortOnlineArr = onlineArr.sortedBy { obj -> obj.orderBy }
                        val offlineArr = arrayParentModelData.filter { obj -> obj.chatStatus == "offline" }
                        arrayParentModelData.clear()
                        busyArr.forEach {
                            arrayParentModelData.add(it)
                        }
                        sortOnlineArr.forEach {
                            arrayParentModelData.add(it)
                        }
                        offlineArr.forEach {
                            arrayParentModelData.add(it)
                        }

                        arrayListMutableLiveData.postValue(arrayParentModelData)
                    }

                    override fun onParseFailure(data: ProfileList) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.CALLASTROLOGER.ordinal) {

        }
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)

        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.CALLASTROLOGER.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), CallModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<CallModel>() {
                    override fun onParseComplete(data: CallModel) {
                        MutableCallData.postValue(data)
                    }

                    override fun onParseFailure(data: CallModel) {
                        LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.NOTIFYICON.ordinal) {
            if (response["success"] == 1) {
                notificationRespone.postValue(response["message"] as String?)
                checkNotification()
            } else {
                notificationResponeFailed.postValue(response["message"] as String?)
            }

        }
        if (identifier == RequestIdentifier.GETASTROSTATUS.ordinal) {
            if (response["phone_status"] == "online") {
                Log.d("phone_status",response["phone_status"].toString())
                astroStatus.postValue(ApplicationConstant.ONLINE)
            }else if (response["phone_status"] == ApplicationConstant.BUSYSTATUS){
                astroStatus.postValue(ApplicationConstant.BUSY)
            }
            else {
                astroStatus.postValue(ApplicationConstant.OFFLINE)
            }

        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REPORTS.ordinal) {
            val jsonObj = response.get("1") as JSONObject
            jsonObj.keys().forEach {
                val json = jsonObj.get(it) as JSONObject
                val reportList = Gson().fromJson(json.toString(), ReportAstrologerList::class.java)
                val astrologerList: AstrologerListModel = AstrologerListModel(
                    reportList.id, reportList.firstName, reportList.lastName,
                    reportList.url, reportList.metaTitle, reportList.metaDescription,
                    reportList.totalRatings, reportList.phoneStatus, "", reportList.image,
                    reportList.expertise, "${reportList.experience} years",
                    reportList.price, "", reportList.languages, reportList.ratingAvg,reportList.justComing,
                    reportList.about, reportList.isReport, "", "", "", "", "",
                    "", reportList.isFreeMin,"",reportList.callMin,reportList.reportNum,reportList.verified,reportList.audio
                )
                arrayParentModelData.add(astrologerList)
            }
            arrayListMutableReportData.postValue(arrayParentModelData)
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.CHECKNOTIFICATION.ordinal) {
            if (response["message"] != "There is no notification associated with this user") {
                var hashValue = HashMap<String, String>()
                hashValue.put("astroID", response["astroID"].toString())
                hashValue.put("message", response["message"].toString())
                checkNotification.postValue(hashValue)
            }
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.REMOVENOTIFICATION.ordinal) {
            var hashValue = HashMap<String, String>()
            hashValue.put("success", response["success"].toString())
            hashValue.put("message", response["message"].toString())
            removeNotification.postValue(hashValue)
        }
        if(identifier==RequestIdentifier.RTMTOKEN.ordinal){
            if (response["success"] == 1) {
                rtmToken.postValue(response["token"] as String?)
            } else {

            }

        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.UPDATEUSERWALLET.ordinal) {
            val userBalance = (response["0"] as JSONObject).get("userWalletBalance").toString()
            balanceUpdateResp.postValue(userBalance)
        }

    }

}
