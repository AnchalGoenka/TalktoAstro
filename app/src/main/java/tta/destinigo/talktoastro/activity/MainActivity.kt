package tta.destinigo.talktoastro.activity

import ChatCallbackListener
import ToastStatusListener
import android.app.AlertDialog
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.razorpay.PaymentResultListener
import com.twilio.chat.CallbackListener
import com.twilio.chat.Channel
import com.twilio.chat.ErrorInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.BuildConfig
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activities.ChannelActivity

import tta.destinigo.talktoastro.chat.BasicChatClient
import tta.destinigo.talktoastro.chat.ChannelModel
import tta.destinigo.talktoastro.chat.ChatAstrologerListFragment
import tta.destinigo.talktoastro.chat.ChatThankYouActivity
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.view.*
import tta.destinigo.talktoastro.viewmodel.HomeViewModel
import tta.destinigo.talktoastro.viewmodel.NotificationViewModel
import java.util.*


class MainActivity : tta.destinigo.talktoastro.BaseActivity(), PaymentResultListener,
    ConnectivityReceiver.ConnectivityReceiverListener, BasicChatClient.LoginListener {

    //    private var dl: DrawerLayout? = null
    private var actionBarToggle: ActionBarDrawerToggle? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var dialog: AlertDialog? = null
    private var isTokenUpdatedCalled = false
    private lateinit var basicClient: BasicChatClient
    private val channels = HashMap<String, ChannelModel>()
    private var channelSID = ""
    var timer: CountDownTimer? = null
    private var homeViewModel: HomeViewModel? = null
//    private var nv: NavigationView? = null

    override val fragmentContainerId: Int
        get() = R.id.frame_layout
    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun getToolbarId(): Int {
        return R.id.toolbar_main
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showAlertDialog(isConnected)
    }

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(layoutResId)
        setupToolbar()
        checkLogin()
        setUpFireBase()
        createDialog()
        checkAppUpdate()
       // checkForAppUpdate(this)
        homeViewModel = ViewModelProvider(this, MyViewModelFactory(myApplication)).get(
            HomeViewModel::class.java)
        fragmentManager = supportFragmentManager
//        view_return_to_chat.visibility = View.GONE

        var transaction: FragmentTransaction = fragmentManager.beginTransaction()
        SharedPreferenceUtils.put(
            ApplicationConstant.REPORTCHECKED,
            "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        if (getIntent().getIntExtra("fragmentNumber", 0) == 1) {
            transaction.replace(
                R.id.frame_layout,
                RegistrationFragment.newInstance(null),
                RegistrationFragment.tagName
            )
            transaction.addToBackStack(null)
            transaction.commit()
            bottom_navigation.selectedItemId = R.id.action_setting
        } else if (getIntent().getIntExtra("fragmentNumber", 0) == 2) {
            transaction.replace(
                R.id.frame_layout,
                ForgotPasswordFragment.newInstance(null),
                ForgotPasswordFragment.tagName
            )
            transaction.addToBackStack(null)
            transaction.commit()
        } else if (getIntent().getIntExtra("fragmentNumber", 0) == 3) {
         //   val hashVal = getIntent().getSerializableExtra("login credentials")
            var bundle = Bundle()
            bundle.putString("Register", "login")
          //  bundle.putSerializable("login credential", hashVal)
            transaction.replace(
                R.id.frame_layout,
                VerifyOtp.newInstance(bundle),
                VerifyOtp.tagName
            )
            transaction.addToBackStack(null)
            transaction.commit()
        } else if (getIntent().getIntExtra("fragmentNumber", 0) == 4) {
            transaction.replace(
                R.id.frame_layout,
                ChatAstrologerListFragment.newInstance(null, ApplicationConstant.CHAT_STRING),
                ChatAstrologerListFragment.tagName
            )
           // transaction.addToBackStack(ChatAstrologerListFragment.tagName)
            transaction.commit()
            updateUserWallet()
        }  else if (getIntent().getIntExtra("fragmentNumber", 0) == 5) {

            transaction.replace(
                R.id.frame_layout,
                HomeFragment.newInstance(null, getIntent().getStringExtra("type")),
                HomeFragment.tagName
            )
           // transaction.addToBackStack(HomeFragment.tagName)
            transaction.commit()
            updateUserWallet()

        } else if (getIntent().getIntExtra("fragmentNumber", 0) == 6){

            transaction.replace(
                R.id.frame_layout,
                ChatAstrologerListFragment.newInstance(null, ApplicationConstant.CHAT_STRING),
                ChatAstrologerListFragment.tagName
            )
           // transaction.addToBackStack(ChatAstrologerListFragment.tagName)
            transaction.commit()
            updateUserWallet()
        }else if (getIntent().getIntExtra("fragmentNumber", 0) == 7){
            transaction.replace(
                R.id.frame_layout,
                ArticleFragment.newInstance(null),
                ArticleFragment.tagName
            )
           // transaction.addToBackStack(null)
            transaction.commit()
            updateUserWallet()
        }
        else if (getIntent().getIntExtra("fragmentNumber", 0) == 8){

            transaction.replace(
                R.id.frame_layout,
                Freekundli.newInstance(null),
                Freekundli.tagName
            )
          //  transaction.addToBackStack(Freekundli.tagName)
            transaction.commit()
            updateUserWallet()
        }
        else if(getIntent().getIntExtra("fragmentNumber", 0) == 9){

            transaction.replace(
                R.id.frame_layout,
                TarotCardReading.newInstance(null),
                TarotCardReading.tagName
            )
           // transaction.addToBackStack(TarotCardReading.tagName)
            transaction.commit()
            updateUserWallet()
        }
        else if(getIntent().getIntExtra("fragmentNumber", 0) == 10){

            transaction.replace(
                R.id.frame_layout,
                AskFreeQuestionFragment.newInstance(null),
                AskFreeQuestionFragment.tagName
            )
            //transaction.addToBackStack(AskFreeQuestionFragment.tagName)
            transaction.commit()
            updateUserWallet()
        }

        else {
            transaction.replace(
                R.id.frame_layout,
                MainFragment.newInstance(null),
                MainFragment.tagName
            )
            transaction.commit()
            updateUserWallet()
        }
        if (ApplicationUtil.checkLogin()){
            bottom_navigation.menu.findItem(R.id.action_setting).title = "MY profile"
        }
        else {
            bottom_navigation.menu.findItem(R.id.action_setting).title = "Sign Up"
        }

        bottom_navigation.selectedItemId
        bottom_navigation.setOnNavigationItemSelectedListener {
            transaction = fragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.action_home -> {
                    val mainFragment=MainFragment()
                    //setCurrentFragment(mainFragment)
                    val fragment = fragmentManager.findFragmentByTag(MainFragment.tagName)
                    /*if (fragment != null) {
                        transaction.replace(R.id.frame_layout, fragment, fragment.getTag());
                        transaction.commit();
                    } else {
                        transaction.replace(
                            R.id.frame_layout,
                            MainFragment.newInstance(null),
                            MainFragment.tagName
                        )
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                    true*/
                    transaction.replace(
                        R.id.frame_layout,
                        MainFragment.newInstance(null),
                        MainFragment.tagName
                    )
                    transaction.addToBackStack(null)
                    transaction.commit()
                    updateUserWallet()

                }
                R.id.action_read -> {
                    val articleFragment=ArticleFragment()
                   // setCurrentFragment(articleFragment)
                   val fragment = fragmentManager.findFragmentByTag(ArticleFragment.tagName)
                    /*  if (fragment != null) {
                         transaction.replace(R.id.frame_layout, fragment, fragment.getTag())
                         transaction.commit();
                     } else {
                         transaction.replace(
                             R.id.frame_layout,
                             ArticleFragment.newInstance(null),
                             ArticleFragment.tagName
                         )
                         transaction.addToBackStack(null)
                         transaction.commit()
                     }
                     true*/
                    transaction.replace(
                        R.id.frame_layout,
                        ArticleFragment.newInstance(null),
                        ArticleFragment.tagName
                    )
                    transaction.addToBackStack(null)
                    transaction.commit()
                    updateUserWallet()
                }
                R.id.action_setting -> {
                    if (ApplicationUtil.checkLogin()) {
                        val fragment =
                            fragmentManager.findFragmentByTag(UserProfileFragment.tagName)
                      /*  if (fragment != null) {
                            transaction.replace(R.id.frame_layout, fragment, fragment.getTag());
                            transaction.commit();
                        } else {
                            transaction.replace(
                                R.id.frame_layout,
                                UserProfileFragment.newInstance(null),
                                UserProfileFragment.tagName
                            )
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }*/
                        transaction.replace(
                            R.id.frame_layout,
                            UserProfileFragment.newInstance(null),
                            UserProfileFragment.tagName
                        )
                        transaction.addToBackStack(null)
                        transaction.commit()
                        updateUserWallet()
                    } else {
                        val fragment =
                            fragmentManager.findFragmentByTag(RegistrationFragment.tagName)
                        /*if (fragment != null) {
                            transaction.replace(R.id.frame_layout, fragment, fragment.getTag());
                            transaction.commit();
                        } else {
                            transaction.replace(
                                R.id.frame_layout,
                                RegistrationFragment.newInstance(null),
                                RegistrationFragment.tagName
                            )
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }*/

                        transaction.replace(
                            R.id.frame_layout,
                            RegistrationFragment.newInstance(null),
                            RegistrationFragment.tagName
                        )
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }

                    true
                }
                else -> true
            }
            true
        }

//        return_to_chatFab.setOnClickListener {
//            if (BaseApplication.instance.p0 == null)
//                Toast.makeText(applicationContext, "No Active Chat", Toast.LENGTH_SHORT).show()
//            else
//                BaseApplication.instance.rejoinChannel(this)
//        }
    }

    fun updateUserWallet () {
        val userID = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val json = JSONObject()
        json.put("userID",userID)
        if (homeViewModel != null){
            homeViewModel?.getWalletTransactions(json)
            homeViewModel?.balanceUpdateResp?.observe(this, androidx.lifecycle.Observer {
                SharedPreferenceUtils.put(
                    ApplicationConstant.BALANCE, it,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                setupToolbar()
            })

        }
    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout,fragment,fragment.getTag())
            commit()
        }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("custom-event-name"))
        showAlertDialog(true)
        if (ApplicationUtil.checkLogin()) {
            startLoginToChat()
        }

    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            ApplicationUtil.showDialog(myActivity, message)
        }
    }

    fun setupToolbar() {
        actionBarToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.Open, R.string.Close)
        drawer_layout.addDrawerListener(actionBarToggle!!)
        actionBarToggle!!.syncState()
        val toolbar_main_activity = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar_main_activity)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (ApplicationUtil.checkLogin()) {
            toolbar_main_activity.ib_notification.visibility = View.VISIBLE
            toolbar_main_activity.layout_btn_recharge.visibility = View.VISIBLE
        } else {
            toolbar_main_activity.layout_btn_recharge.visibility = View.INVISIBLE
        }
        //toolbar_main_activity.ib_notification.visibility=View.VISIBLE
        nv.itemIconTintList = null
        nv.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.getItemId()
                val menuTitle = item.title
                when (id) {
                    R.id.nav_login -> {
                        if (menuTitle == getString(R.string.login)) {
                            val intent =
                                Intent(
                                    ApplicationUtil.getContext(),
                                    tta.destinigo.talktoastro.activity.LoginActivity::class.java
                                )
                            startActivity(intent)
                        } else {
                            logOut(item)
                        }

                    }
                    R.id.nav_recharge_wallet -> {
                        drawer_layout.closeDrawers()
                        openRechargePackagePage(true)
                        updateUserWallet()
                    }
                    R.id.nav_settings -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            SettingsFragment.newInstance(null),
                            SettingsFragment.tagName
                        )
                        transaction.addToBackStack(SettingsFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_wallet_transactions -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            WalletTransactionsFragment.newInstance(null),
                            WalletTransactionsFragment.tagName
                        )
                        transaction.addToBackStack(WalletTransactionsFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_chat_history -> {
                        drawer_layout.closeDrawers()
                        val intent =
                            Intent(ApplicationUtil.getContext(), ChannelActivity::class.java)
                        startActivity(intent)
                        updateUserWallet()
                    }
                    R.id.nav_call_history -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            CallHistoryFragment.newInstance(null),
                            CallHistoryFragment.tagName
                        )
                        transaction.addToBackStack(CallHistoryFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_report_history -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            ReportHistoryFragment.newInstance(null),
                            ReportHistoryFragment.tagName
                        )
                        transaction.addToBackStack(ReportHistoryFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_share -> {
                        drawer_layout.closeDrawers()
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("text/plain")
                        val text =
                            "https://play.google.com/store/apps/details?id=tta.destinigo.talktoastro&hl=en"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                        startActivity(Intent.createChooser(shareIntent, "Share using"))
                    }
                    R.id.nav_customer_support -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            CustomerSupportFragment.newInstance(null),
                            CustomerSupportFragment.tagName
                        )
                        transaction.addToBackStack(CustomerSupportFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_privacy_policy -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            PrivacyPolicyFragment.newInstance(null),
                            PrivacyPolicyFragment.tagName
                        )
                        transaction.addToBackStack(PrivacyPolicyFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_about_us -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            AboutUsFragment.newInstance(null),
                            AboutUsFragment.tagName
                        )
                        transaction.addToBackStack(AboutUsFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_terms_conditions -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            TermsConditionsFragment.newInstance(null),
                            TermsConditionsFragment.tagName
                        )
                        transaction.addToBackStack(TermsConditionsFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_ask_free_questions->{
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            AskFreeQuestionFragment.newInstance(null),
                            AskFreeQuestionFragment.tagName
                        )
                        transaction.addToBackStack(AskFreeQuestionFragment.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_free_kundli->{
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            Freekundli.newInstance(null),
                            Freekundli.tagName
                        )
                        transaction.addToBackStack(Freekundli.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_tarot_Reading->{
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            TarotCardReading.newInstance(null),
                            TarotCardReading.tagName
                        )
                        transaction.addToBackStack(TarotCardReading.tagName)
                        transaction.commit()
                        updateUserWallet()
                    }
                    R.id.nav_rate_us -> {
                        val appPackageName =
                            packageName // getPackageName() from Context or Activity object
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
                    R.id.nav_how_it_works -> {
                        drawer_layout.closeDrawers()
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            HowItWorksFragment.newInstance(null),
                            HowItWorksFragment.tagName
                        )
                        transaction.addToBackStack(HowItWorksFragment.tagName)
                        transaction.commit()
                    }
                    else -> return true
                }
                return true

            }
        })
        layout_btn_recharge.setOnClickListener {
            openRechargePackagePage(false)
        }

        ib_notification.setOnClickListener {

            val intent = Intent(ApplicationUtil.getContext(), NotificationActivity::class.java)
            startActivity(intent)

        }
        /*notification_icon.setOnClickListener {
            var transaction: FragmentTransaction =
                myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frame_layout,
                NotificationListFragment.newInstance(null),
                NotificationListFragment.tagName
            )
            transaction.addToBackStack(NotificationListFragment.tagName)
            transaction.commit()
        }*/
    }

    fun openRechargePackagePage(isComingFromDrawer: `Boolean`) {
        SharedPreferenceUtils.put(
            ApplicationConstant.ISCOMINGFROMREPORT,
            false,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        var bundle: Bundle?
        if (isComingFromDrawer) {
            bundle = Bundle()
            bundle!!.putString("isDrawer", "true")
        } else {
            bundle = null
        }

        /*var transaction: FragmentTransaction =
            myActivity.supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout,
            newInstance(bundle),
            PayActivityFragment.Companion.tagName
        )
        transaction.addToBackStack(PayActivityFragment.Companion.tagName)
        transaction.commit()*
        /

         */
        val intent =
            Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
        startActivity(intent)
    }


    fun setUpFireBase() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Talk to astro")
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

       /* FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                SharedPreferenceUtils.put(
                    ApplicationConstant.DEVICETOKEN, token.toString(),
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                LogUtils.d("Token: $token")
                //Toast.makeText(baseContext, "Token:$token", Toast.LENGTH_SHORT).show()
            })*/
    }

    fun checkLogin() {
        val username = SharedPreferenceUtils.readString(
            ApplicationConstant.USERNAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val password = SharedPreferenceUtils.readString(
            ApplicationConstant.PASSWORD, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val name = SharedPreferenceUtils.readString(
            ApplicationConstant.NAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val phoneNumber = "+" + "${
            SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        }" + " " + "${
            SharedPreferenceUtils.readString(
                ApplicationConstant.MOBILE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        }"
        val phCode = SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE,
            "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val menu = nv.menu
        val navlogin = menu.findItem(R.id.nav_login)
        val header = nv.getHeaderView(0)
        if (username.isNullOrEmpty()) {
            SharedPreferenceUtils.put(
                ApplicationConstant.PHONECODE,
                "91",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "0",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            SharedPreferenceUtils.put(
                ApplicationConstant.CHECKNOTIFYASTROID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            btn_recharge.visibility = View.INVISIBLE
          //  navlogin.setTitle(getString(R.string.login))
            menu.findItem(R.id.nav_wallet_transactions).isVisible = false
            menu.findItem(R.id.nav_call_history).isVisible = false
            menu.findItem(R.id.nav_report_history).isVisible = false
            menu.findItem(R.id.nav_recharge_wallet).isVisible = false
            menu.findItem(R.id.nav_chat_history).isVisible = false
           // menu.findItem(R.id.nav_free_questions).isVisible=false
            header.nav_login_email.visibility = View.INVISIBLE
            header.nav_login_user_name.visibility = View.INVISIBLE
            header.nav_login_phone.visibility = View.GONE
            header.nav_button_login.visibility = View.VISIBLE
            header.nav_button_login.setOnClickListener {
                val intent =
                    Intent(
                        ApplicationUtil.getContext(),
                        tta.destinigo.talktoastro.activity.LoginActivity::class.java
                    )
                startActivity(intent)
            }
         } else {
            if (phCode == "91") {
                btn_recharge.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_rupee,
                    0,
                    0,
                    0
                )
            } else {
                btn_recharge.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_dollar_orange,
                    0,
                    0,
                    0
                )
            }
            btn_recharge.visibility = View.VISIBLE
            navlogin.setTitle(getString(R.string.action_logout))
            menu.findItem(R.id.nav_wallet_transactions).isVisible = true
            menu.findItem(R.id.nav_call_history).isVisible = true
            menu.findItem(R.id.nav_report_history).isVisible = true
            menu.findItem(R.id.nav_recharge_wallet).isVisible = true
            menu.findItem(R.id.nav_chat_history).isVisible = true
            header.nav_login_email.visibility = View.VISIBLE
            header.nav_login_user_name.visibility = View.VISIBLE
            header.nav_login_phone.visibility = View.VISIBLE
            header.nav_button_login.visibility = View.GONE
            header.nav_login_email.text = username
            header.nav_login_email.setOnClickListener {
                openUserProfileFragment()
            }
            header.nav_login_user_name.text = name
            header.nav_login_user_name.setOnClickListener {
                openUserProfileFragment()
            }
            header.nav_login_phone.text = phoneNumber
            header.nav_login_phone.setOnClickListener {
                openUserProfileFragment()
            }
        }
        val versionCode = tta.destinigo.talktoastro.BuildConfig.VERSION_CODE
        val versionName = tta.destinigo.talktoastro.BuildConfig.VERSION_NAME
       // header.nav_ver.text = "Version: ${versionCode.toString()}.$versionName"
        header.nav_ver.text = "Version: $versionName"
    }

    fun openUserProfileFragment() {
        drawer_layout.closeDrawers()
        var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout,
            UserProfileFragment.newInstance(null),
            UserProfileFragment.tagName
        )
        transaction.addToBackStack(UserProfileFragment.tagName)
        transaction.commit()
    }

    fun logOut(item: MenuItem) {
        SharedPreferenceUtils.put(
            ApplicationConstant.USERNAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        SharedPreferenceUtils.put(
            ApplicationConstant.PASSWORD, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        item.setTitle(getString(R.string.login))
        startActivity(Intent(ApplicationUtil.getContext(), this::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (actionBarToggle!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(
            item
        )

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
     }

    private fun handleIntent(intent: Intent) {
        val fragmentManager = supportFragmentManager
        var currentFragment: tta.destinigo.talktoastro.BaseFragment?

        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (tta.destinigo.talktoastro.BaseFragment.getTopFragment(
                    fragmentManager,
                    fragmentContainerId
                ) is HomeFragment
            ) {
                currentFragment =
                    supportFragmentManager.findFragmentById(R.id.frame_layout) as HomeFragment
                currentFragment.handleVoiceSearch(query)
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
//        hideProgressBar()
//        tta.destinigo.talktoastro.util.LogUtils.d("Razorpay payment error>>>>>>>:$code with message:$response")
//        Toast.makeText(applicationContext, "Razorpay payment error>>>>>>>>>:$code with message:$response",
//            Toast.LENGTH_LONG).show();
//        var fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as PayOnline
//        fragment.sendRazorPayFailureResp()
    }

    override fun onPaymentSuccess(razorPaymentId: String?) {
        /*hideProgressBar()
        //Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show()
        // Save razorpay success response on server side.
        LogUtils.d("onPaymentSuccess: razorPaymentId: $razorPaymentId")
        var fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as PayOnline
        fragment.sendRazorPaySuccessResp(razorPaymentId)*/
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                if (currentFocus != null) {
                    val currentFocus:View = currentFocus as View
                    if (currentFocus is EditText) {
                        val outRect: Rect = Rect()
                        currentFocus.getGlobalVisibleRect(outRect)
                        if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                            currentFocus.clearFocus()
                            KeypadUtils.hideSoftKeypad(this)
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun checkAppUpdate() {
        // Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks whether the platform allows the specified type of update,
// and checks the update priority.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    500
                )
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 500) {
            if (resultCode != RESULT_OK) {
                LogUtils.d("Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    /**
     * Login to create chat client
     */


    fun startLoginToChat() {
        val userName = SharedPreferenceUtils.readString(
            ApplicationConstant.NAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val certPinningChosen = true
        val realm = "us1"
        val ttl = "3000"

        SharedPreferenceUtils.put(
            "pinCerts",
            certPinningChosen,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        SharedPreferenceUtils.put(
            "realm",
            realm,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        SharedPreferenceUtils.put(
            "ttl",
            ttl,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val url = Uri.parse(BuildConfig.ACCESS_TOKEN_SERVICE_URL)
            .buildUpon()
            .appendQueryParameter("identity", userName)
            .appendQueryParameter("realm", realm)
            .appendQueryParameter("ttl", ttl)
            .build()
            .toString()
        LogUtils.d("url string : $url")
        BaseApplication.instance.basicClient.login(userName!!, certPinningChosen, realm, url, this)

    }

    var astroId = ""
    var astroName = ""
    var chatDuration = ""
    var chatID = ""
    fun updateTokenForChatClient(astrologerId: String, astrologerName: String, duration: String, chatId: String) {
        astroId = astrologerId
        astroName = astrologerName
        chatDuration = duration
        chatID = chatId
       // isTokenUpdatedCalled = true
       /* if (BaseApplication.instance.basicClient.chatClient == null) {
            startLoginToChat()
        } else {
            BaseApplication.instance.basicClient.updateToken()
        }*/

        startActivity<ChatThankYouActivity>(
            /*Constants.EXTRA_CHANNEL to newChannel,
            Constants.EXTRA_CHANNEL_SID to newChannel.sid,*/
            Constants.CHAT_ASTRO_ID to astroId as Any,
            Constants.CHAT_ASTRO_NAME to astroName as Any,
            Constants.CHAT_DURATION to chatDuration as Any,
            Constants.CHAT_ID to chatID as Any
        )
    }

    override fun onLoginStarted() {
        LogUtils.d("Log in started")
    }

    override fun onLoginFinished() {
        LogUtils.d("Login finished")
        if (isTokenUpdatedCalled) {
            val channelName = astroName
            basicClient = BaseApplication.instance.basicClient

            /* basicClient.chatClient?.channels?.createChannel(channelName, Channel.ChannelType.PRIVATE, ChatCallbackListener<Channel>() {
                 //debug { "Channel created with sid|${it.sid}| and type ${it.type}" }
                 channelSID = it.sid
                 channels[it.sid] = ChannelModel(it)
 //                channels[it.uniqueName] =
 //                channels[it.uniqueName] = ChannelModel(it)
                 it.join(
                     ToastStatusListener("", "Failed to send chat request", {
                     }, {
                         if (it.status == Channel.ChannelStatus.JOINED) {
                             Handler().postDelayed({
                                 ChannelModel(it).getChannel(ChatCallbackListener<Channel>() { channel ->
                                     LogUtils.d("it name: $channel")
                                     LogUtils.d("astroName: $astroName")
                                     channel.members.inviteByIdentity(astroName, ToastStatusListener(
                                         "",
                                         "Error while sending chat request to astrologer",{},{
                                             startActivity<ChatThankYouActivity>(
                                                 Constants.EXTRA_CHANNEL to it,
                                                 Constants.EXTRA_CHANNEL_SID to it.sid,
                                                 Constants.CHAT_ASTRO_ID to astroId as Any,
                                                 Constants.CHAT_ASTRO_NAME to astroName as Any,
                                                 Constants.CHAT_DURATION to chatDuration as Any,
                                                 Constants.CHAT_ID to chatID as Any
                                             )
                                         }))
                                 })
                             }, 0)
                         }
                     })
                 )
             })*/
            try {
                val builder = basicClient.chatClient?.channels?.channelBuilder()

                val userId = SharedPreferenceUtils.readString(
                    ApplicationConstant.USERID, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                builder?.withFriendlyName(channelName)
                    ?.withUniqueName(userId + "_" + chatDuration+"_"+ System.currentTimeMillis())
                    ?.withType(Channel.ChannelType.PRIVATE)
                    ?.build(object : CallbackListener<Channel>() {
                        override fun onSuccess(newChannel: Channel) {
                            /*debug { "Successfully created a channel with options." }
                            channels.put(newChannel.sid, ChannelModel(newChannel))
                            refreshChannelList()*/

                            newChannel.join(
                                ToastStatusListener("", "Failed to send chat request", {
                                }, {
                                    if (newChannel.status == Channel.ChannelStatus.JOINED) {
                                        Handler().postDelayed({
                                            ChannelModel(newChannel).getChannel(ChatCallbackListener<Channel>() { channel ->
                                                LogUtils.d("it name: $channel")
                                                LogUtils.d("astroName: $astroName")
                                                channel.members.inviteByIdentity(
                                                    astroName,
                                                    ToastStatusListener(
                                                        "",
                                                        "Error while sending chat request to astrologer",
                                                        {},
                                                        {
                                                            startActivity<ChatThankYouActivity>(
                                                                Constants.EXTRA_CHANNEL to newChannel,
                                                                Constants.EXTRA_CHANNEL_SID to newChannel.sid,
                                                                Constants.CHAT_ASTRO_ID to astroId as Any,
                                                                Constants.CHAT_ASTRO_NAME to astroName as Any,
                                                                Constants.CHAT_DURATION to chatDuration as Any,
                                                                Constants.CHAT_ID to chatID as Any
                                                            )

                                                            BaseFragment.popBackStack(myActivity)
                                                        })
                                                )
                                            })
                                        }, 0)
                                    }
                                })
                            )
                        }

                        override fun onError(errorInfo: ErrorInfo?) {
//                            error { "Error creating a channel" }
//                            Toast.makeText(this!!,"Error creating a channel",Toast.LENGTH_SHORT)
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                "Error creating a channel",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isTokenUpdatedCalled = false
        }
    }

    override fun onLoginError(errorMessage: String) {
        BaseApplication.instance.showToast("Error logging in : " + errorMessage, Toast.LENGTH_LONG)
        isTokenUpdatedCalled = false
    }

    override fun onLogoutFinished() {
        BaseApplication.instance.showToast("Log out finished")
        isTokenUpdatedCalled = false
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(ApplicationUtil.getContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    myActivity,
                    resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )
                    .show()
            } else {
                LogUtils.d("This device is not supported.")
            }
            return false
        }
        return true
    }

    companion object {
        lateinit var instance: MainActivity public set
        private val DEFAULT_CLIENT_NAME = "TestUser"
        private val DEFAULT_REALM = "us1"
        private val DEFAULT_TTL = "3000"
        private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    }

    private fun createDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(myActivity)
        // Set the alert dialog title
        builder.setTitle("Internet not available")

        // Display a message on alert dialog
        builder.setMessage("Please check your internet connectivity")
        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK") { _, _ ->

        }
        // Finally, make the alert dialog using builder
        dialog = builder.create()
    }

    private fun showAlertDialog(isConnected: Boolean) {
        if (dialog != null && !isConnected && !dialog!!.isShowing) {
            hideProgressBar()
            // Display the alert dialog on app interface
            if (!dialog!!.isShowing) {
                dialog!!.show()
            }
        } else {
            if (dialog != null && isConnected && dialog!!.isShowing) {
                dialog!!.dismiss()
                dialog!!.cancel()
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        }

    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(mApplication) as T
        }
    }

    /*override fun onBackPressed() {
        super.onBackPressed()


        if(homeFragment.isVisible()){
            bottom_navigation.setSelectedItemId(R.id.nav_home);
        }
        if(searchFragment.isVisible()){
            bottom_navigation.setSelectedItemId(R.id.nav_search);
        }
        if(myProfileFragment.isVisible()){
            bottom_navigation.setSelectedItemId(R.id.nav_profile);
        }

    }*/
}
