package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.model.UserProfile
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject


/**

 * Created by Vivek Singh on 2019-08-03.

 */
class UserProfileViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app)  {
    val arrayMutableUserProfile = MutableLiveData<UserProfile>()
    val arrayEditMutable = MutableLiveData<Boolean>()
    init {
    }

    fun getUserProfile(jsonObj: JSONObject?) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.USERPROFILE.ordinal,
            ApplicationConstant.USERPROFILE, jsonObj, null, null, false)
    }

    fun updateProfile(param: HashMap<String, String>?){
        val json = JSONObject(param as Map<*, *>)
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.EDITUSERPROFILE.ordinal,
            ApplicationConstant.EDITUSERPROFILE, json, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.USERPROFILE.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), UserProfile::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<UserProfile>() {
                    override fun onParseComplete(data: UserProfile) {
                        arrayMutableUserProfile.postValue(data)
                    }

                    override fun onParseFailure(data: UserProfile) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.EDITUSERPROFILE.ordinal) {
            if (response["success"] == 1){
                arrayEditMutable.postValue(true)
            } else {
                arrayEditMutable.postValue(false)
            }
        }
    }
}