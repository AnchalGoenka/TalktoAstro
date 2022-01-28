package tta.destinigo.talktoastro.volley.ext;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import java.io.UnsupportedEncodingException;

/**
 * Custom implementation for {@link com.android.volley.toolbox.StringRequest} to override caching logic
 */
public class StringObjRequest extends Request<String> {
    private final Listener<String> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringObjRequest(int method, String url, Listener<String> listener,
                            ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringObjRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParserCustom.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParserCustom.parseCacheHeaders(response));
//        try {
//
//            Cache.Entry cacheEntry = HttpHeaderParserCustom.parseCacheHeaders(response);
//            if (cacheEntry == null) {
//                cacheEntry = new Cache.Entry();
//            }
//
//            long now = System.currentTimeMillis();
//            final long softExpire = now + VollyController.CACHE_HIT_BUT_REFRESHED;
//            final long ttl = now + VollyController.CACHE_EXPIRED;
//            cacheEntry.data = response.data;
//            cacheEntry.softTtl = softExpire;
//            cacheEntry.ttl = ttl;
//            String headerValue;
//            headerValue = response.headers.get("Date");
//            if (headerValue != null) {
//                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
//            }
//            cacheEntry.responseHeaders = response.headers;
//
//            final String jsonString = new String(response.data,
//                    HttpHeaderParserCustom.parseCharset(response.headers));
//            cacheEntry.etag=jsonString;
//            return Response.success(jsonString, cacheEntry);
//
//        } catch (UnsupportedEncodingException e) {
//            return Response.success(new String(response.data), HttpHeaderParser.parseCacheHeaders(response));
//        }
    }


}
