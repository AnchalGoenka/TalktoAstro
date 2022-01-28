package tta.destinigo.talktoastro.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import tta.destinigo.talktoastro.model.AstroProfileModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.model.PostListModel
import tta.destinigo.talktoastro.model.ReportList


/**

 * Created by Vivek Singh on 2019-08-17.

 */
class AstroProfileViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {
    var arrayListMutableLiveData = MutableLiveData<AstroProfileModel>()
    var  postListMutableLiveData = MutableLiveData<ArrayList<PostListModel>>()
    var reviewListMutableLiveData = MutableLiveData<String>()
    var item = ArrayList<PostListModel>()
    init {

    }

    fun getAstroProfile(json: JSONObject) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.ASTROPROFILE.ordinal,
            ApplicationConstant.ASTRO_PROFILE, json, null, null, false
        )
    }

    fun getPost(astroid :String){
        var json = JSONObject()
        json.put("astroid", astroid)

        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.Post.ordinal,
            ApplicationConstant.post, json, null, null, false
        )
    }

    fun submitReview(json: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.WRITEREVIEW.ordinal,
            ApplicationConstant.ASTRO_REVIEW, json, null, null, false
        )
    }



    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.ASTROPROFILE.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), AstroProfileModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<AstroProfileModel>() {
                    override fun onParseComplete(data: AstroProfileModel) {
                        arrayListMutableLiveData.postValue(data)
                    }

                    override fun onParseFailure(data: AstroProfileModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error ")
                    }
                })
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.Post.ordinal) {
            /*if (response.get("success") == 0){
                postListMutableLiveData.postValue(response.get("message").toString())

            }*/
            response.keys().forEach {
               // Toast.makeText(context,"$it",Toast.LENGTH_LONG).show()
                var jsonObject=response.get(it) as JSONObject
                val postList = Gson().fromJson(jsonObject.toString(), PostListModel::class.java)
                item.add(postList)
            }
            postListMutableLiveData.postValue(item)
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.WRITEREVIEW.ordinal) {
            if (response.get("success") == 1){
                reviewListMutableLiveData.postValue(response.get("message").toString())

            }
        }

    }

}