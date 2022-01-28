package tta.destinigo.talktoastro.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbb20.CountryCodePicker
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.RegistrationViewModel
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.util.LogUtils


class RegistrationFragment : tta.destinigo.talktoastro.BaseFragment() {

    private var registrationModel: RegistrationViewModel? = null
    private var dialog: AlertDialog? = null
    override val layoutResId: Int
        get() = R.layout.fragment_registration

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivity.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(layoutResId, container, false)
    }

    override fun onResume() {
        super.onResume()
        toolBarSetup()
    }
    private fun toolBarSetup() {
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        toolbar.title = "Sign Up"
        toolbar.btn_recharge.visibility = View.INVISIBLE
        checkLogin(toolbar)
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //createDialog()
        (activity as MainActivity).bottom_navigation.menu.getItem(2).isChecked = true
        val txv_terms_and_condition: TextView = myActivity.findViewById(R.id.txv_terms_and_condition)
        val txv_login_registration: TextView = myActivity.findViewById(R.id.txv_login_registration)
        val btn_signup: Button = myActivity.findViewById(R.id.btn_signup)
        val txv_firstname: TextView = myActivity.findViewById(R.id.txv_firstname)
        val txv_lastname: TextView = myActivity.findViewById(R.id.txv_lastname)
        val txv_email: TextView = myActivity.findViewById(R.id.txv_email)
        val txv_mobilenumber: TextView = myActivity.findViewById(R.id.txv_mobilenumber)
        val txv_password: TextView = myActivity.findViewById(R.id.txv_password)

        registrationModel =
            ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(RegistrationViewModel::class.java)
        registrationModel?.arrayListMutableLiveData?.observe(this, Observer {
            hideProgressBar()
            val name = txv_firstname.text.toString() + " " + txv_lastname.text.toString()
            var ccp = myActivity.findViewById<CountryCodePicker>(R.id.ccp)
            SharedPreferenceUtils.put(
                ApplicationConstant.USERNAME, txv_email.text.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.PASSWORD, txv_password.text.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.USERID, it!!.userId,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.PHONECODE, ccp.fullNumber,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.MOBILE, txv_mobilenumber.text.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.BALANCE, "0",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            SharedPreferenceUtils.put(
                ApplicationConstant.NAME, name,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

            /*val hashMap = HashMap<String, String>()
            hashMap["mobile"] = txv_mobilenumber.text.toString()
            hashMap["viasms"] = "1"
            hashMap["phonecode"] = ccp.fullNumber*/
            deviceToken()
            val bundle = Bundle()
           // bundle.putSerializable("Register credential", hashMap)
            bundle.putString("Register", "register")
            registrationModel?.loginViaSMS(txv_mobilenumber.text.toString())
            /*var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, VerifyOtp.newInstance(bundle), VerifyOtp.tagName)
            transaction.addToBackStack(VerifyOtp.tagName)
            transaction.commit()*/
        })

        registrationModel?.loginViaSMs?.observe(this, Observer {
            val bundle = Bundle()
            bundle.putString("Register", "register")
            SharedPreferenceUtils.put(
                ApplicationConstant.Details, it,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            hideProgressBar()

            var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, VerifyOtp.newInstance(bundle), VerifyOtp.tagName)
            transaction.addToBackStack(null)
          //  transaction.addToBackStack(VerifyOtp.tagName)
            transaction.commit()
        })
        registrationModel?.registrationFailed?.observe(this, Observer {
            hideProgressBar()
            ApplicationUtil.showDialog(myActivity, it)
        })

        txv_terms_and_condition.makeLinks(
            Pair("Terms of Use", View.OnClickListener {
                var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, TermsConditionsFragment.newInstance(null), TermsConditionsFragment.tagName)
                transaction.addToBackStack(TermsConditionsFragment.tagName)
                transaction.commit()
            }),
            Pair("Privacy Policy", View.OnClickListener {
                var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, PrivacyPolicyFragment.newInstance(null), PrivacyPolicyFragment.tagName)
                transaction.addToBackStack(PrivacyPolicyFragment.tagName)
                transaction.commit()
            }))
        txv_login_registration.makeLinks(
            Pair("login", View.OnClickListener {
                if (checkLogin(myActivity.findViewById<Toolbar>(R.id.toolbar_main))) {
                    var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, HomeFragment.newInstance(null, "Call"), HomeFragment.tagName)
                    transaction.addToBackStack(HomeFragment.tagName)
                    transaction.commit()
                } else {
                    val intent = Intent(ApplicationUtil.getContext(), tta.destinigo.talktoastro.activity.LoginActivity::class.java)
                    startActivity(intent)
                }
            })
        )
        btn_signup.setOnClickListener {
            if (ApplicationUtil.isNetworkAvailable(ApplicationUtil.getContext())){
                var ccp = myActivity.findViewById<CountryCodePicker>(R.id.ccp)
                if (txv_firstname.text.isNotEmpty() && txv_lastname.text.isNotEmpty() && txv_email.text.isNotEmpty() && ccp.fullNumber.isNotEmpty() && txv_mobilenumber.text.isNotEmpty() && txv_password.text.isNotEmpty()) {
                    showProgressBar("Loading")
                    val deviceId = Settings.Secure.getString(
                        getContext()?.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                    )
                    val provider = "Android"
                    var json = JSONObject()
                    json.put("first_name", txv_firstname.text.toString())
                    json.put("last_name", txv_lastname.text.toString())
                    json.put("email", txv_email.text.toString())
                    json.put("phonecode", ccp.fullNumber)
                    json.put("mobile", txv_mobilenumber.text.toString())
                    json.put("password", txv_password.text.toString())
                    json.put("provider", provider)
                    json.put("provider_id", deviceId)

                    registrationModel?.registerUser(json)
                } else {
                    if (txv_firstname.text.isEmpty()) {
                        txv_firstname.setError("Field cannot be blank")
                    }
                    if (txv_lastname.text.isEmpty()) {
                        txv_lastname.setError("Field cannot be blank")
                    }
                    if (txv_email.text.isEmpty()) {
                        txv_email.setError("Field cannot be blank")
                    }
                    if (txv_mobilenumber.text.isEmpty()) {
                        txv_mobilenumber.setError("Field cannot be blank")
                    }
                    if (txv_password.text.isEmpty()) {
                        txv_password.setError("Field cannot be blank")
                    }
                    ApplicationUtil.showDialog(myActivity, "All fields are mandatory")
                }
            } else {
                hideProgressBar()
                ApplicationUtil.showDialog(ApplicationUtil.getContext(), "Please check your internet connectivity")
            }
        }
    }
    fun deviceToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener( OnCompleteListener {   task ->

            if (!task.isSuccessful) {
                Log.w("token failed", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            SharedPreferenceUtils.put(
                ApplicationConstant.DEVICETOKEN, token.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            LogUtils.d("Token: $token")
        })

        registrationModel?.passDeviceTokenToServer()
    }
    fun checkLogin(toolbar: Toolbar): Boolean {
        val username = SharedPreferenceUtils.readString(
            ApplicationConstant.USERNAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val password = SharedPreferenceUtils.readString(
            ApplicationConstant.PASSWORD, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        //val logout = myActivity.findViewById<View>(R.id.nav_logout)
        if (username.isNullOrEmpty()){
            toolbar.btn_recharge.visibility = View.INVISIBLE
            return false
            //logout.visibility = View.INVISIBLE
        }
        else{
            toolbar.btn_recharge.visibility = View.VISIBLE
            toolbar.btn_recharge.text = SharedPreferenceUtils.readString(ApplicationConstant.BALANCE, "0", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            return true
        }
    }
    /*

         */
    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (this.text.toString().contains("login")) {
                spannableString.setSpan(RelativeSizeSpan(1.5f), startIndexOfLink, startIndexOfLink + link.first.length, 0)
            }
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegistrationViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = RegistrationFragment::class.java.name

        fun newInstance(bundle: Bundle?): RegistrationFragment {
            val fragment = RegistrationFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
