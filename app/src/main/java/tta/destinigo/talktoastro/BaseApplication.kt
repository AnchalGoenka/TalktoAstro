package tta.destinigo.talktoastro

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.android.volley.VolleyLog
import com.google.firebase.FirebaseApp
import com.twilio.chat.Channel
import com.twilio.chat.ErrorInfo
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.salesiqembed.ZohoSalesIQ
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import org.json.JSONObject
import tta.destinigo.talktoastro.activities.MessageActivity
import tta.destinigo.talktoastro.agora.chat.ChatManager
import tta.destinigo.talktoastro.chat.BasicChatClient
import tta.destinigo.talktoastro.chat.services.ChatFormViewModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.Constants
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.volley.VollyController
import tta.destinigo.talktoastro.volley.VollyRequestManager
import java.util.*


/**

 * Created by Vivek Singh on 2019-06-10.

 */

abstract class BaseApplication : MultiDexApplication(), AnkoLogger, Application.ActivityLifecycleCallbacks {
    private var mActivityTransitionTimer: Timer? = null
    private var mActivityTransitionTimerTask: TimerTask? = null
    var mAppInBackground: Boolean = false
    private val MAX_ACTIVITY_TRANSITION_TIME_MS: Long = 2000
    lateinit var vollyController: VollyController
    var p0: Channel? = null
    var chatManager: ChatManager? = null
    var numRunningActivities: Int = 0

    var astroID = ""
    var chatID = ""
    var sessionID = ""

    lateinit var basicClient: BasicChatClient
        private set

    // check weather app is in background
    var isAppInBackground: Boolean
        get() = mAppInBackground
        set(isAppInBackground) {
            mAppInBackground = isAppInBackground
        }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
        /*You may wish to enable Thread policy while in debug mode. always place this call after setDebuggable()*/
        //enableStrictPolicy(BuildConfig.DEBUG)
        FirebaseApp.initializeApp(this)
        basicClient = BasicChatClient(applicationContext)
        instance = this
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        registerActivityLifecycleCallbacks(this)
        startChat()
        chatManager = ChatManager(this)
        chatManager!!.init()
    }

    @JvmOverloads
    fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        debug { text }
        Handler(Looper.getMainLooper()).post {
            val toast = Toast.makeText(applicationContext, text, duration)
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
            if (!TextUtils.isEmpty(text))
                toast.show()
        }
    }

    fun startChat(){
        ZohoSalesIQ.init(this, ApplicationConstant.ZohoSalesIQ_Appkey, ApplicationConstant.ZohoSalesIQ_AccessKey);
        ZohoLiveChat.Chat.setTheme(R.style.AppTheme)
       // ZohoLiveChat.Chat.setTheme(android.R.style.AppTh)
        // ZohoSalesIQ.clearData(this);
       // ZohoLiveChat.Chat.setTheme(android.R.style.TextAppearance_Theme)
      //  ZohoLiveChat.Chat.setThemeColor("PrimaryColor", com.zoho.commons.Color(164, 188, 189));
    }

    fun showError(error: ErrorInfo) {
       // showError("Something went wrong", error)
    }

    fun showError(message: String, error: ErrorInfo) {
        showToast(formatted(message, error), Toast.LENGTH_LONG)
        logErrorInfo(message, error)
    }

    fun logErrorInfo(message: String, error: ErrorInfo) {
        error { formatted(message, error) }
    }

    private fun formatted(message: String, error: ErrorInfo): String {
        return String.format("%s. %s", message, error.toString())
    }

    /**
     * should be called from onResume of each activity of application
     */
    fun onActivityResumed() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask!!.cancel()
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer!!.cancel()
        }
        isAppInBackground = false
    }

    /**
     * should be called from onPause of each activity of app
     */
    fun onActivityPaused() {
        this.mActivityTransitionTimer = Timer()
        this.mActivityTransitionTimerTask = object : TimerTask() {
            override fun run() {
                isAppInBackground = true
            }
        }

        this.mActivityTransitionTimer!!.schedule(
            mActivityTransitionTimerTask!!,
            MAX_ACTIVITY_TRANSITION_TIME_MS
        )
    }

    /**
     * initializeVolly volly for the application
     */
    protected fun initializeVolly(
        appName: String,
        defaultDiskUsageByte: Int,
        threadPoolSizes: Int
    ) {
        var defaultDiskUsageBytes = defaultDiskUsageByte
        var threadPoolSize = threadPoolSizes

        if (defaultDiskUsageBytes <= 0)
        //set default to 5 MB
            defaultDiskUsageBytes = 5242880
        if (threadPoolSize <= 1)
            threadPoolSize = 4

        vollyController = VollyController.initialize(
            this.applicationContext, VollyRequestManager.Config(
                "data/data/" +
                        appName +
                        "/imageCache", defaultDiskUsageBytes, threadPoolSize
            )
        )

        VolleyLog.DEBUG = BuildConfig.DEBUG //if Volly is used
    }


    /*
           Enable  Thread and VM policy to detect and avoid avoid performing
            long running operations on the UI thread.
        */
    protected fun enableStrictPolicy(enable: Boolean) {
        //----------------------------Strict Policy-----------------------------//

        if (enable) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    //                    .penaltyDropBox()
                    .penaltyDialog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    //	                 .detectLeakedRegistrationObjects()
                    .penaltyDropBox()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }
        //----------------------------Strict Policy-----------------------------//

    }


    protected abstract fun initializeComponents()

    companion object {
        lateinit var instance: tta.destinigo.talktoastro.BaseApplication public set
    }

    fun rejoinChannel(ctx:Activity) {
        try {
            val p0 = this.p0
            if (p0 != null)
            {
                val msgIntent = Intent(this,MessageActivity::class.java)
                msgIntent.putExtra(Constants.EXTRA_CHANNEL,p0)
                msgIntent.putExtra(Constants.EXTRA_CHANNEL_SID,p0.sid)
                msgIntent.putExtra(Constants.EXTRA_UNIQUE_NAME,p0.uniqueName)
                ctx.startActivity(msgIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun saveSessionData(intent: Intent) {
        if (intent != null) {
            this.astroID = intent.getStringExtra(Constants.CHAT_ASTRO_ID).toString();
            this.chatID = intent.getStringExtra(Constants.CHAT_ID).toString()
            this.sessionID = intent.getStringExtra(Constants.CHAT_ID).toString()
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
        numRunningActivities++;
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        numRunningActivities--;
        if (numRunningActivities == 0) {
            Log.i("TAG", "No running activities left, app has likely entered the background.");
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    fun sendChatHistory(act: FragmentActivity) {

        var chatFormViewModel: ChatFormViewModel = ViewModelProviders.of(
            act,
            MessageActivity.MyViewModelFactory(instance)
        ).get(
            ChatFormViewModel::class.java
        )


        var jsonObj = JSONObject()

        jsonObj.put(
            "userID",
            SharedPreferenceUtils.readString(
                ApplicationConstant.USERID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )!!
        )
        jsonObj.put("astroID", this.astroID)
        jsonObj.put("duration", "0:00")
        jsonObj.put("status", "success")
        jsonObj.put("chatID", this.chatID)
        jsonObj.put("sessionID", this.sessionID)

        chatFormViewModel.sendChatLog(jsonObj)
        jsonObj.put("chatStatus", "online")
        chatFormViewModel.changeChatStatus(jsonObj)

        BaseApplication.instance.p0 = null
    }
}