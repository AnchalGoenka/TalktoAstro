package tta.destinigo.talktoastro.volley.ext;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * * Custom implementation for {@link com.android.volley.toolbox.JsonObjectRequest} to override caching logic
 */
public class JsonObjRequest extends JsonRequest<JSONObject> {

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonObjRequest(int method, String url, JSONObject jsonRequest,
                          Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #JsonObjRequest(int, String, JSONObject, Listener, ErrorListener)
     */
    public JsonObjRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
                          ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParserCustom.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParserCustom.parseCacheHeaders(response));

//                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
//                if (cacheEntry == null) {
//                    cacheEntry = new Cache.Entry();
//                }
//
//                long now = System.currentTimeMillis();
//                final long softExpire = now + VollyController.CACHE_HIT_BUT_REFRESHED;
//                final long ttl = now + VollyController.CACHE_EXPIRED;
//                cacheEntry.data = response.data;
//                cacheEntry.softTtl = softExpire;
//                cacheEntry.ttl = ttl;
//                String headerValue;
//                headerValue = response.headers.get("Date");
//                if (headerValue != null) {
//                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
//                }
//                headerValue = response.headers.get("Last-Modified");
////                if (headerValue != null) {
////                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
////                }
//                cacheEntry.responseHeaders = response.headers;
//                final String jsonString = new String(response.data,
//                        HttpHeaderParser.parseCharset(response.headers));
//                return Response.success(new JSONObject(jsonString), cacheEntry);



        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
