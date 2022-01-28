package tta.destinigo.talktoastro.volley

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.JsonArray
import okhttp3.Credentials
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.ext.RequestType


/**
 * Created by Sonia Gupta on 2/13/2019.
 */

class VollyController private constructor(context: Context, config: Config)//initializeWith(context, config);
    : VollyRequestManager(context, config) {

    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType      -request type get, put or post
     * @param identifier    - identifier integer for this request
     * @param pUrl          -url
     * @param pJsonReqBody- request body to be sent with url request
     * @param pParams-      request params, null if no params
     * @param pReqHeaders-  request headers if any as key, value map. null if no headers requied
     * @param pListener-    response callback listener. if provided caller will get callback
     */
    private fun doJsonRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?,
        pReqHeaders: Map<String, String>?,
        pListener: VollyResponseListener?, offlineReq: Boolean
    ) {
        var pJsonReqBody = pJsonReqBody

        if (pJsonReqBody == null)
            pJsonReqBody = JSONObject()

        val jsonObjReq = object : JsonObjectRequest(getReqType(pReqType), pUrl, pJsonReqBody, object : Response.Listener<JSONObject> {

            override fun onResponse(response: JSONObject) {
                if (pListener != null) {
                    pListener.onApiResponse(
                        identifier,
                        response,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                    )
                }
            }
        }, Response.ErrorListener { error ->
            pListener?.onApiError(identifier, error.message, "-1")
            Log.d("TAG", "doJsonRequest: "+error)
        }){

            override fun getHeaders(): Map<String, String> {
                if (pUrl == ApplicationConstant.FREE_HOROSCOPE || pUrl == ApplicationConstant.LAT_LONG_FREEHOROSCOPE ||
                    pUrl == ApplicationConstant.GET_TIMEZONE||pUrl==ApplicationConstant.CHART||pUrl==ApplicationConstant.BIRTH_DETAILS||
                    pUrl==ApplicationConstant.General_Ascendant_Report){
                    val basic = Credentials.basic(ApplicationConstant.ASTROLOGYAPI_USERID, ApplicationConstant.ASTROLOGYAPI_API_KEY)
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = basic
                    return headers
                }else if (pUrl == ApplicationConstant.PAYPACKAGES){
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }

                return pReqHeaders ?: emptyMap()
            }


            override fun getParams(): Map<String, String> {
                return pParams ?: super.getParams()

            }
        }
        jsonObjReq.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        jsonObjReq.setShouldCache(false)
        jsonObjReq.tag = getTagFromUrl(pUrl)
        addRequest(jsonObjReq)
    }



    /*fun dojsonArrayRequest(pReqType: RequestType, identifier: Int, pUrl: String, pjsonArray: JSONArray?, pParams: Map<String, String>?
                           , pReqHeaders: Map<String, String>?, pListener: VollyResponseListener?, offlineReq: Boolean){
        var JsonArray = pjsonArray

        if (JsonArray == null)
            JsonArray = JSONArray()

        val jsonArrayRequest = JsonArrayRequest(getReqType(pReqType), pUrl, JsonArray, Response.Listener {
            if (pListener != null) {
                pListener.onApiResponse(
                    identifier,
                    it,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                )
            }
        },
            Response.ErrorListener { error -> VolleyLog.wtf(error.message, "utf-8") })

    }
*/
    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request
     * @param pParams-     request params, null if no params
     * @param pReqHeaders- request headers if any as key, value map. null if no headers requied
     * @param pListener-   response callback listener. if provided caller will get callback
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?,
        pReqHeaders: Map<String, String>?,
        pListener: VollyResponseListener?, offlineReq: Boolean
    ) {
        //offlineReq=false;//todo:caching is disabled, enable it later

        if (!ApplicationUtil.isNetworkAvailable(mContext)) {
            if (offlineReq) {
               // SugarBaseHelper.saveOfflineRequest(pUrl, pJsonReqBody?.toString() ?: "")
                if (pListener != null) {
                    pListener.onApiResponse(identifier, "{\"status\":0}", 0, 0)
                    try {
                        pListener.onApiResponse(identifier, JSONObject("{\"status\":0}"), 0, 0)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                return
            }
            return
        }
        if (pJsonReqBody != null) {
            doJsonRequest(pReqType, identifier, pUrl, pJsonReqBody, pParams, pReqHeaders, pListener, offlineReq)
            return
        }

        val strReq = object : tta.destinigo.talktoastro.volley.ext.StringObjRequest(getReqType(pReqType),
            pUrl, Response.Listener<String> { response ->
                //if (BuildConfig.DEBUG)
                //SugarBaseHelper.saveResponseToCache(pUrl, response ?: "{}")
                if (pListener != null) {
                    pListener.onApiResponse(
                        identifier,
                        response,
                        System.currentTimeMillis(),
                        System.currentTimeMillis()
                    )
                    try {
                        pListener.onApiResponse(
                            identifier,
                            JSONObject(response),
                            System.currentTimeMillis(),
                            System.currentTimeMillis()
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                //LogUtils.d(response);
            }, Response.ErrorListener { error ->
                Log.d("response","jumped into error: "+error)
                pListener?.onApiError(identifier, error.message, "-1")

            }) {

            override fun getHeaders(): Map<String, String> {
                return pReqHeaders ?: emptyMap()
            }


            override fun getParams(): Map<String, String>? {
                return pParams ?: super.getParams()

            }
        }
        strReq.setRetryPolicy(
            DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )
        strReq.setShouldCache(false)
        strReq.tag = getTagFromUrl(pUrl)
        addRequest(strReq)

    }

    /**
     * Use this method to make a network request with or without a request body for place list view modal.
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request
     * @param pParams-     request params, null if no params
     * @param pReqHeaders- request headers if any as key, value map. null if no headers requied
     * @param pListener-   response callback listener. if provided caller will get callback
     * @param latitude -    latitude from place view modal
     * @param longitude -     longitude from place view modal
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?,
        pReqHeaders: Map<String, String>?,
        pListener: VollyResponseListener?, offlineReq: Boolean, latitude: Double, longitude: Double
    ) {
        //offlineReq=false;//todo:caching is disabled, enable it later

        if (!ApplicationUtil.isNetworkAvailable(mContext)) {
            if (offlineReq) {
                // SugarBaseHelper.saveOfflineRequest(pUrl, pJsonReqBody?.toString() ?: "")
                if (pListener != null) {
                    pListener.onApiResponse(identifier, "{\"status\":0}", 0, 0, latitude, longitude)
                    try {
                        pListener.onApiResponse(identifier, JSONObject("{\"status\":0}"), 0, 0)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                //  ToastUtils.showToast(mContext, mContext.getString(R.string.txt_reqoffline_saved))
                return
            }
//            val reqCache = SugarBaseHelper.getRequestChace(pUrl)
//            if (pListener != null && reqCache != null && !StringUtils.isNullOrEmpty(reqCache!!.getResponse())) {
//                val res = reqCache!!.getResponse()//SharedPrefUtils.readString(pUrl);
//                pListener!!.onApiResponse(identifier, res, 0, 0)
//                try {
//                    pListener!!.onApiResponse(identifier, JSONObject(res), 0, 0)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//
//            } else {
//                ToastUtils.showToast(mContext, NO_CONNECTION_MSG, false)
//            }
            return
        }
        if (pJsonReqBody != null) {
            doJsonRequest(pReqType, identifier, pUrl, pJsonReqBody, pParams, pReqHeaders, pListener, offlineReq)
            return
        }

        //        String url = pUrl;
        //        if(pReqType==RequestType.GET){
        //            url=generateGetUrl(pUrl,pParams);
        //        }
        val strReq = object : tta.destinigo.talktoastro.volley.ext.StringObjRequest(getReqType(pReqType),
            pUrl, Response.Listener<String> { response ->
                //if (BuildConfig.DEBUG)
                //SugarBaseHelper.saveResponseToCache(pUrl, response ?: "{}")
                if (pListener != null) {
                    pListener.onApiResponse(
                        identifier,
                        response,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        latitude, longitude
                    )
                    try {
                        pListener.onApiResponse(
                            identifier,
                            JSONObject(response),
                            System.currentTimeMillis(),
                            System.currentTimeMillis()
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
                //LogUtils.d(response);
            }, Response.ErrorListener { error ->
                pListener?.onApiError(identifier, error.message, "-1")

            }) {

            override fun getHeaders(): Map<String, String> {
                return pReqHeaders ?: emptyMap()
            }


            override fun getParams(): Map<String, String>? {
                return pParams ?: super.getParams()

            }
        }
        strReq.setShouldCache(false)
        strReq.tag = getTagFromUrl(pUrl)
        addRequest(strReq)

    }





    /**
     * Call this method to get unique tag for the `volly request` using the request url. Tag to identify the volly request and perform cancel operations
     *
     * @param url - url for which tag needs to be created
     * @return - tag for the request to identify the volly request and perform cancel operations
     */
    fun getTagFromUrl(url: String): String {
        return url.replace("/", "").replace(":", "")
    }

    /**
     * Convert {@param type} to Volly request type
     *
     * @param type
     * @return
     */
    private fun getReqType(type: RequestType): Int {
        var reqType = Request.Method.GET
        when (type) {
            RequestType.GET -> reqType = Request.Method.GET
            RequestType.POST -> reqType = Request.Method.POST
            RequestType.PUT -> reqType = Request.Method.PUT
            RequestType.IMAGE -> {
            }
        }
        return reqType
    }

    companion object {
        val CACHE_HIT_BUT_REFRESHED: Long = 30000 // in 30 seconds cache will be hit, but also refreshed on background;
        //3 * 60 * 1000; // in 3 minute cache will be hit, but also refreshed on background
        val CACHE_EXPIRED =
            (24 * 5 * 60 * 60 * 1000).toLong() // in 5 days (24*5 hours) this cache entry expires completely

        private val NO_CONNECTION_MSG = "No Internet Connection!"
        private val BUTTON_TEXT = "Settings"
        val ERRCODE_SESSION_EXPIRED = "GEN-001"
        val ERRMESSAGE_SESSION_EXPIRED = "Unauthorized access"
        private var mController: VollyController? = null
        private val PROTOCOL_CONTENT_TYPE = "application/json"

        /**
         * Initialize this on application onCreate()
         */
        @Synchronized
        fun initialize(context: Context, config: Config): VollyController {
            if (mController == null)
                mController = VollyController(context, config)
            return mController as VollyController
        }

        /**
         * Encode get request params into get url
         *
         * @param url    - root url
         * @param params - params to url encode
         * @return - url with encoded params
         */
        fun generateGetUrl(url: String, params: Map<String, String>?): String {
            if (params != null) {
                val uriBuilder = Uri.parse(url).buildUpon()
                val keySet = params.keys
                for (key in keySet) {
                    var value = params[key]
                    if (TextUtils.isEmpty(value)) {
                        value = ""
                    }
                    uriBuilder.appendQueryParameter(key, value)
                }
                return uriBuilder.build().toString()
            } else {
                return url
            }

        }
    }
}
