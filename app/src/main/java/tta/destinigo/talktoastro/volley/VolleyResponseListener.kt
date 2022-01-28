package tta.destinigo.talktoastro.volley

import org.json.JSONObject

/**
 * Created by Sonia Gupta on 2/13/2019.
 */

interface VollyResponseListener {

    /**
     * Handle string response received from API
     * @param identifier identifier integer for this request
     * @param response - response string
     * @param serverDate - server date timestamp
     * @param lastModified - lastmodified timestamp
     */
    fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long)

    /**
     *
     * Handle string response received from API
     * @param identifier identifier integer for this request
     * @param response - response as jsonobject
     * @param serverDate - server date timestamp
     * @param lastModified - lastmodified timestamp
     */
    fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long)

    /**
     *
     * Handle string response received from API
     * @param identifier identifier integer for this request
     * @param response - response as jsonobject
     * @param serverDate - server date timestamp
     * @param lastModified - lastmodified timestamp
     */
    fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long, latitude: Double, longitude:Double)

    /**
     * API service error callback
     * @param identifier - identifier integer for this request
     * @param error - String
     */

    fun onApiError(identifier: Int, error: String?, errorCode: String?)
}
