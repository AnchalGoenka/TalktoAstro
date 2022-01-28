package tta.destinigo.talktoastro

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.awesomedialog.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import tta.destinigo.talktoastro.interfaces.OkValueCallback
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ConnectivityReceiver
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.util.isNetworkConnected
import tta.destinigo.talktoastro.view.MainFragment
import tta.destinigo.talktoastro.view.PayOnline
import tta.destinigo.talktoastro.viewmodel.MainViewModel
import tta.destinigo.talktoastro.volley.VollyResponseListener
import tta.destinigo.talktoastro.volley.ext.RequestType

/**

 * Created by Vivek Singh on 2019-06-10.

 */

abstract class  BaseActivity : AppCompatActivity(), tta.destinigo.talktoastro.interfaces.IScreen,
    VollyResponseListener, ConnectivityReceiver.ConnectivityReceiverListener,
    PaymentResultListener {

    lateinit var mHelperViewModel: MainViewModel
    private lateinit var dialogInstance: Dialog
    var appVersionReq = MutableLiveData<String>()
    // private var mFirebaseAnalytics: FirebaseAnalytics? = null
    /**
     * container id for fragment
     *
     * @return id of the root layout to render a fragment
     */
    abstract val fragmentContainerId: Int
    private lateinit var mToolbar: Toolbar

    protected abstract val layoutResId: Int

    lateinit var mBaseApplication: tta.destinigo.talktoastro.BaseApplication
    private var hud: KProgressHUD? = null

    private lateinit var connectivityReceiverObj: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // registering the receiver to get the internet status inside application
        try {
            isNetworkConnected.checkNetworkConnected(applicationContext)
            connectivityReceiverObj = ConnectivityReceiver()
            registerReceiver(
                connectivityReceiverObj,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (layoutResId != 0)
            setContentView(layoutResId)
        tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager = supportFragmentManager
        val application = this.application
        if (application is tta.destinigo.talktoastro.BaseApplication) {
            mBaseApplication = application
        }

        Thread.setDefaultUncaughtExceptionHandler(
            tta.destinigo.talktoastro.util.ExceptionHandler(
                this
            )
        )

        if (getToolbarId() != 0) {
            mToolbar = findViewById<View>(getToolbarId()) as Toolbar //setup toolbar if found
            setSupportActionBar(mToolbar)
        }

        hud = KProgressHUD(this)
        Checkout.preload(applicationContext)

    }



    override fun onDestroy() {
        super.onDestroy()
        tta.destinigo.talktoastro.util.LogUtils.d("base activity", "on destroy called ")
        try {
            unregisterReceiver(connectivityReceiverObj)
        } catch (e: Exception) {

        }
    }

    override val myApplication: tta.destinigo.talktoastro.BaseApplication
        get() {
            return mBaseApplication
        }

    override val myActivity: tta.destinigo.talktoastro.BaseActivity
        get() {
            return this
        }

    override fun hideProgressBar() {
        hud!!.dismiss()
    }

    protected abstract fun getToolbarId(): Int

    fun getToolbar(): Toolbar {
        return mToolbar
    }

    override fun onResume() {
        super.onResume()
        run {
            if (mBaseApplication.isAppInBackground) {
                onAppResumeFromBackground()
            }
            mBaseApplication.onActivityResumed()
        }

        ConnectivityReceiver.connectivityReceiverListener = this

        // check the internet and show dialog
        if (isNetworkConnected.checkNetworkConnected(applicationContext)) {
            LogUtils.d("base activity ", "internet is connected ignore")
        } else {
            LogUtils.d("base activity ", "internet is not connected show dialog")
        }
    }

    /**
     * This callback will be called after onResume if application is being
     * resumed from background. <br></br>
     *
     *
     * Subclasses can override this method to get this callback.
     */
    protected fun onAppResumeFromBackground() {
        tta.destinigo.talktoastro.util.LogUtils.i("internet onAppResumeFromBackground()")

    }

    override fun onPause() {
        super.onPause()
        tta.destinigo.talktoastro.util.LogUtils.i("onPause()")
        val application = this.application
        if (application is tta.destinigo.talktoastro.BaseApplication) {
            application.onActivityPaused()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        tta.destinigo.talktoastro.util.LogUtils.i("onNewIntent()")
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            super.onBackPressed()
           // tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager.popBackStackImmediate()
        }

    }


    fun showProgressBar(message: String, cancellable: Boolean) {

        hud!!.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(message)
            .setCancellable(cancellable)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
            .show()

    }

    override fun onPaymentError(code: Int, response: String?) {
        try {
            hideProgressBar()
            tta.destinigo.talktoastro.util.LogUtils.d("Razorpay payment error>>>>>>>:$code with message:$response")
            /*Toast.makeText(
                applicationContext, "Razorpay payment error>>>>>>>>>:$code with message:$response",
                Toast.LENGTH_LONG
            ).show();*/
            var fragment = supportFragmentManager.findFragmentById(R.id.frame_Recharge) as PayOnline
            fragment.sendRazorPayFailureResp()
        }catch (e:Exception) {
            Toast.makeText(applicationContext,"Main Activity: ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
            Log.d("Base Activity", "Base Activity: ${e.printStackTrace()}\"")
            tta.destinigo.talktoastro.util.LogUtils.d("Base Activity: ${e.printStackTrace()}")
        }
    }

    override fun onPaymentSuccess(razorPaymentId: String?) {
        try {
            hideProgressBar()
            //Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
            // Save razorpay success response on server side.
            LogUtils.d("onPaymentSuccess: razorPaymentId: $razorPaymentId")
            var fragment = supportFragmentManager.findFragmentById(R.id.frame_Recharge) as PayOnline
            fragment.sendRazorPaySuccessResp(razorPaymentId)
        } catch (e: Exception) {
          //  Toast.makeText(applicationContext,"Main Activity: ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
            Log.d("Base Activity", "Base Activity: ${e.printStackTrace()}\"")
            tta.destinigo.talktoastro.util.LogUtils.d("Base Activity: ${e.printStackTrace()}")
        }
    }


    //****************************************************************************************//
    //********************************API request/response handlers -start**************************//
    //****************************************************************************************//


    /**
     * Use this method for multipart request
     *
     * @param identifier identifier integer for this request
     * @param pUrl       request url
     * @param pParams    request params
     */

    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        showProgress: Boolean, offlineReq: Boolean
    ) {
//        if (showProgress)
//            showProgressBar()
        mBaseApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offlineReq
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
     * @param offlineReq   - true when caching is required, false otherwise
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?, offlineReq: Boolean
    ) {
        mBaseApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offlineReq
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
        mBaseApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq, latitude, longitude
        )

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
        // showProgressBar()
        mBaseApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, null, pParams, null, this, offlineReq
        )

    }

    override fun onApiResponse(
        identifier: Int,
        response: String,
        serverDate: Long,
        lastModified: Long,
        latitude: Double,
        longitude: Double
    ) {
        //hideProgressBar()
    }

    override fun onApiResponse(
        identifier: Int,
        response: String,
        serverDate: Long,
        lastModified: Long
    ) {
        // hideProgressBar()
    }

    override fun onApiResponse(
        identifier: Int,
        response: JSONObject,
        serverDate: Long,
        lastModified: Long
    ) {
        // hideProgressBar()
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {
        onApiError(identifier, error)
        if (errorCode == null) {
            return
        }
    }

    fun onApiError(identifier: Int, error: String?) {
        // hideProgressBar()
        //showError(error, MESSAGETYPE.TOAST)
    }


    companion object {
        lateinit var sFragmentManager: FragmentManager
        private val mCounter = 0
    }

    override fun showProgressBar() {

    }

    /**
     * Callback will be called when there is change
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        //showAlertDialog(isConnected)
    }

    fun checkForAppUpdate(mContext: FragmentActivity) {

        mHelperViewModel =
            ViewModelProviders.of(this, MainFragment.MyViewModelFactory(myApplication))
                .get(MainViewModel::class.java)

        try {
            if (mContext != null && mHelperViewModel != null) {

                checkAppVersionAndShowUpdateDialogue(object : OkValueCallback {
                    override fun onValueReceived(value: String?) {
                        if (value.equals("1", false)) {
                            AwesomeDialog.build(mContext)
                                .title("New Version available")
                                .body("There is newer version of this application available, click UPDATE to update now?")
                                .icon(R.drawable.ic_congrts)
                                .onPositive(
                                    "Update",
                                    buttonBackgroundColor = R.drawable.layout_rounded_cancel,
                                    textColor = ContextCompat.getColor(
                                        mContext,
                                        android.R.color.white
                                    )
                                )
                                {
                                    val appPackageName = packageName // package name of the app
                                    try {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("market://details?id=$appPackageName")
                                            )
                                        )
                                    } catch (anfe: android.content.ActivityNotFoundException) {
                                        startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                            )
                                        )
                                    }
                                }
                                .onNegative(
                                    "No, Thanks",
                                    buttonBackgroundColor = R.drawable.layout_rounded,
                                    textColor = ContextCompat.getColor(
                                        mContext,
                                        android.R.color.white
                                    )
                                )
                                {}
                                .position(AwesomeDialog.POSITIONS.CENTER)
                                .setCancelable(false)
                        }
                    }
                }, mContext)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun checkAppVersionAndShowUpdateDialogue(mCallback: OkValueCallback, mContext: Context?) {

        try {
            if (mContext != null) {
                if (mHelperViewModel != null) {
                    mHelperViewModel?.getAppVersion()
                    mHelperViewModel?.appVersionReq?.observe(this, Observer {
                        val mResponse = it
                        if (mResponse != null && mResponse.data != null) {
                            val versionOfAppOnServer = mResponse.data.get(0).user_version

                            if (ApplicationConstant.CURRENT_APP_VERSION < versionOfAppOnServer) {
                                mCallback.onValueReceived("1")
                            }

                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}