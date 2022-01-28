package tta.destinigo.talktoastro.volley

import android.content.Context
import android.text.TextUtils
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.Volley
import java.io.File

/**
 * Created by Sonia Gupta on 2/13/2019.
 */

open class VollyRequestManager protected constructor(
    protected var mContext: Context,
    private val mConfig: Config?
)//initializeWith(context, config);
{
    private lateinit var mDataRequestQueue: RequestQueue
    private lateinit var mImageQueue: RequestQueue


    protected//            OkHttpClient client = new OkHttpClient();
    //            client.networkInterceptors().add(new StethoInterceptor());
    //            mDataRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(), new OkHttpStack(client));
    val requestQueue: RequestQueue
        @Synchronized get() {

            mDataRequestQueue = Volley.newRequestQueue(mContext.applicationContext)
            return mDataRequestQueue
        }


    //	private static String mDefaultRequestTag;
    class Config(
        val mImageCachePath: String,
        val mDefaultDiskUsageBytes: Int,
        val mThreadPoolSize: Int
    )

    @Synchronized
    private fun loader(): RequestQueue {
        if (this.mConfig == null) {
            throw IllegalStateException(Config::class.java.simpleName + " is not initialized, call initializeWith(..) method first.")
        }
        if (mImageQueue == null) {
            var rootCache = mContext.externalCacheDir
            if (rootCache == null) {
                rootCache = mContext.cacheDir
            }

            val cacheDir = File(rootCache, mConfig.mImageCachePath)
            cacheDir.mkdirs()

            val stack = HurlStack()
            val network = BasicNetwork(stack)
            val diskBasedCache = DiskBasedCache(cacheDir, mConfig.mDefaultDiskUsageBytes)
            mImageQueue = RequestQueue(diskBasedCache, network, mConfig.mThreadPoolSize)
            mImageQueue.start()
        }
        return mImageQueue
    }

    /**
     * @param pRequest
     * @param <T>
    </T> */
    fun <T> addRequest(pRequest: Request<T>) {
        //        if (instance == null) {
        //            throw new IllegalStateException(VollyRequestManager.class.getSimpleName() +
        //                    " is not initialized, call initializeWith(..) method first.");
        //        }
        if (pRequest.tag == null) { //request should have a unique tag to identify this request
            pRequest.tag = pRequest.url
            // new IllegalArgumentException("Request Object Tag is not specified.");
        }
        pRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 20,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val queue = requestQueue

        queue.add(pRequest)
    }

    /**
     * @param pRequest
     * @param tag
     * @param <T>
    </T> */
    fun <T> addRequest(pRequest: Request<T>, tag: String) {
        //        if (instance == null) {
        //            throw new IllegalStateException(VollyRequestManager.class.getSimpleName() +
        //                    " is not initialized, call initializeWith(..) method first.");
        //        }
        pRequest.tag = if (TextUtils.isEmpty(tag)) pRequest.url else tag
        addRequest(pRequest)
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param pRequestTag
     */
    fun cancelPendingRequests(pRequestTag: Any) {
        //        if (instance == null) {
        //            throw new IllegalStateException(VollyRequestManager.class.getSimpleName() +
        //                    " is not initialized, call initializeWith(..) method first.");
        //        }
        if (requestQueue != null) {
            requestQueue.cancelAll(pRequestTag)
        }
    }
}
