package tta.destinigo.talktoastro.volley.ext;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;

import java.util.Map;

class HttpHeaderParserCustom {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     *
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is
     * not cacheable.
     */

    public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;

        long serverDate = 0;
        long softExpire = 0;
        long hardExpire;

        String serverEtag = null;


        serverEtag = headers.get("ETag");


        String contentType = response.headers.get("Content-Type");
//        if (TextUtils.isEmpty(contentType) || !contentType.contains("image")) {
//
//            String softCache = headers.get("soft_cache");
//            String hardCache = headers.get("hard_cache");
//
//            if (!TextUtils.isEmpty(hardCache)) {
//                int softExpireDelay = 0;
//                try {
//                    softExpireDelay = Integer.parseInt(hardCache);
//                } catch (Exception e) {
//                    softExpireDelay = 5 * 60 * 1000;
//                }
//                softExpire = now + softExpireDelay;
//            } else {
//                softExpire = now + 5 * 60 * 1000;
//            }
//
//            if (!TextUtils.isEmpty(softCache)) {
//                int hardExpireDelay = 0;
//                try {
//                    hardExpireDelay = Integer.parseInt(softCache);
//                } catch (Exception e) {
//                    hardExpireDelay = 24 * 60 * 60 * 1000;
//                }
//                hardExpire = now + hardExpireDelay;
//            } else {
//                hardExpire = now + 24l * 60 * 60 * 1000l;
//            }
//
//        }
//        else
        {
            softExpire = Long.MAX_VALUE;
            hardExpire = Long.MAX_VALUE;
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;

        entry.ttl = hardExpire;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

//    /**
//     * Parse date in RFC1123 format, and return its value as epoch
//     */
//    public static long parseDateAsEpoch(String dateStr) {
//        try {
//            // Parse date in RFC1123 format if this header contains one
//            return DateUtils.parseDate(dateStr).getTime();
//        } catch (DateParseException e) {
//            // Date in invalid format, fallback to 0
//            return 0;
//        }
//    }

    /**
     * Returns the charset specified in the Content-Type of this header, or the
     * HTTP default (ISO-8859-1) if none can be found.
     */

    public static String parseCharset(Map<String, String> headers) {
        String contentType = headers.get(CONTENT_TYPE);
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset"))
                        return pair[1];
                }
            }
        }

        return DEFAULT_CONTENT_CHARSET;
    }
}
