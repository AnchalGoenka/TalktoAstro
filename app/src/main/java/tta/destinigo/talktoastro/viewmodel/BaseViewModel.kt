package tta.destinigo.talktoastro.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import tta.destinigo.talktoastro.volley.VollyResponseListener
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.volley.RequestIdentifier


/**

 * Created by Vivek Singh on 2019-06-09.

 */

open class BaseViewModel (val app: tta.destinigo.talktoastro.BaseApplication) : AndroidViewModel(app), VollyResponseListener {


    init {
        tta.destinigo.talktoastro.util.LogUtils.d("BaseViewModel class")
    }

    //****************************************************************************************//
    //********************************API request/response handlers -start**************************//
    //****************************************************************************************//

    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        showProgress: Boolean,
        offileReq: Boolean
    ) {
//        if (showProgress)
//            showProgressBar()
        Log.e("TAG","+pReqType : "+pReqType);
        Log.e("TAG","+pUrl :"+pUrl);
        Log.e("TAG","+pJsonReqBody :"+pJsonReqBody);
        app.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq
        )

    }

    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request or null
     * @param pParams-     request params or null
     * @param pReqHeaders- request headers if any as key, value map. or null if no headers requied
     * @param offileReq    - true when caching is required, false otherwise
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        offileReq: Boolean
    ) {
        Log.e("TAG","+pReqType : "+pReqType);
        Log.e("TAG","+pUrl :"+pUrl);
        Log.e("TAG","+pJsonReqBody :"+pJsonReqBody);
        app.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq
        )

    }


    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request or null
     * @param pParams-     request params or null
     * @param pReqHeaders- request headers if any as key, value map. or null if no headers requied
     * @param offileReq    - true when caching is required, false otherwise
     * @param latitude - latitude from place view modal
     * @param longitude - longitude from place view modal
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        offileReq: Boolean, latitude: Double, longitude: Double
    ) {
        try {
            Log.e("TAG","+pReqType : "+pReqType);
            Log.e("TAG","+pUrl :"+pUrl);
            Log.e("TAG","+pJsonReqBody :"+pJsonReqBody);
            app.vollyController.doApiRequest(
                pReqType, identifier,
                pUrl, pJsonReqBody, pParams,
                pReqHeaders, this, offileReq, latitude, longitude
            )

        } catch (e: java.lang.Exception) {
            tta.destinigo.talktoastro.util.LogUtils.e("Latitude: " + latitude + "Longitude: " + longitude +". Error=" + e.message)
        }
    }

    /**
     * Override for above method with least required params
     *
     * @param pReqType   -request type get, put or post
     * @param identifier - identifier integer for this request
     * @param pUrl       -url
     * @param pParams-   request params or null
     * @param offlineReq - true when caching is required, false otherwise
     */
    fun doApiRequest(
        pReqType: RequestType,
        identifier: Int,
        pUrl: String,
        pParams: Map<String, String>?,
        offlineReq: Boolean
    ) {
        //showProgressBar()
        Log.e("TAG","+pReqType : "+pReqType);
        Log.e("TAG","+pUrl :"+pUrl);
        app.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, null, pParams, null, this, offlineReq
        )

    }

    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long, latitude: Double, longitude: Double) {
        //hide progress bar
    }

    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
        // hideProgressBar()
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        //  hideProgressBar()
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {
        onApiError(identifier, error)
        LogUtils.e("onApiError: " + error)
       // BaseApplication.instance.showToast("${error.toString()}")
    }

    fun onApiError(identifier: Int, error: String?) {
        tta.destinigo.talktoastro.util.LogUtils.d("API error $identifier  $error")
        //hideProgressBar()
          // showError(error, MESSAGETYPE.TOAST)
    }

}