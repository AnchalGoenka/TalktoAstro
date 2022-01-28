package tta.destinigo.talktoastro.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.view.VerifyOtp
import tta.destinigo.talktoastro.viewmodel.LoginViewModel

class LoginActivity : tta.destinigo.talktoastro.BaseActivity() {
    private var dialog: AlertDialog? = null
    private var loginWithPassword: Boolean = false
    override val fragmentContainerId: Int
        get() = R.id.frame_layout
    override val layoutResId: Int
        get() = R.layout.activity_login_new

    override fun getToolbarId(): Int {
        return 0
    }

    private var loginViewModel: LoginViewModel? = null
    // Initialize a new instance of
    var builder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = AlertDialog.Builder(this)
        setContentView(layoutResId)
        loginViewModel = ViewModelProviders.of(this, MyViewModelFactory(tta.destinigo.talktoastro.BaseApplication.instance)).get(LoginViewModel::class.java)
        //createDialog()
//        img_cancel.setOnClickListener {
//            this.finish()
//        }

        loginViewModel?.arrayListMutableLiveData?.observe(this, Observer {
            if (loginWithPassword) {
                hideProgressBar()
                val name = it!!.firstName + " " + it!!.lastName
                SharedPreferenceUtils.put(
                    ApplicationConstant.USERNAME, it!!.email,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.PASSWORD, txv_password.text.toString(),
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.USERID, it!!.userID,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.PHONECODE, it!!.phonecode,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.MOBILE, it!!.mobile,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.BALANCE, it!!.balance,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                SharedPreferenceUtils.put(
                    ApplicationConstant.NAME, name,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                it.notifyCount?.notify?.let { it1 ->
                    SharedPreferenceUtils.put(
                        ApplicationConstant.NOTIFYCOUNT, it1,
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    )
                }

                this.finish()
                val intent = Intent(
                    ApplicationUtil.getContext(),
                    tta.destinigo.talktoastro.activity.MainActivity::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else {
                it.otp?.let { it1 ->
                    SharedPreferenceUtils.put(ApplicationConstant.OTP, it1, SharedPreferenceUtils.getSharedPref(
                        ApplicationUtil.getContext()))
                }
                var hashMap = HashMap<String, String>()
                hashMap["mobile"] = txv_username.text.toString()
                hashMap["viasms"] = "1"
                hashMap["phonecode"] = ccp_login.fullNumber
//                var bundle = Bundle()
//                bundle.putSerializable("login credentials", hashMap)
//                var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.frame_layout, VerifyOtp.newInstance(bundle), VerifyOtp.tagName)
//                transaction.addToBackStack(VerifyOtp.tagName)
//                transaction.commit()

                val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("login credentials", hashMap)
                intent.putExtra("fragmentNumber", 3)
                startActivity(intent)
            }
           deviceToken()
        })




        loginViewModel?.loginViaSMs?.observe(this, Observer {
            SharedPreferenceUtils.put(
                ApplicationConstant.Details, it,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            hideProgressBar()
            deviceToken()
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
          //  intent.putExtra("login credentials", hashMap)
            intent.putExtra("fragmentNumber", 3)
            startActivity(intent)
        })


        loginViewModel?.loginDidFailed?.observe(this, Observer {
            hideProgressBar()
            ApplicationUtil.showDialog(this, it)
        })
        btnLoginViaSMS.setOnClickListener {
            loginWithPassword = false
            if (ApplicationUtil.isNetworkAvailable(ApplicationUtil.getContext())) {
                val phoneNumber = txv_username.text.toString()
                if (!phoneNumber.isEmpty()) {
                    SharedPreferenceUtils.put(
                        ApplicationConstant.MOBILE, phoneNumber,
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    )
                   /* var jsonObj = JSONObject()
                    jsonObj.put("mobile", phoneNumber)
                    jsonObj.put("viasms", "1")
                    jsonObj.put("phonecode", ccp_login.fullNumber)*/
                    showProgressBar("Loading", true)
                    loginViewModel?.loginViaSMS(phoneNumber)
                } else {
                    hideProgressBar()
                    if (txv_username.text.isEmpty()) {
                        txv_username.setError("Field cannot be blank")
                    }
                    Toast.makeText(ApplicationUtil.getContext(),"Please enter phone number", Toast.LENGTH_SHORT).show()
                }

            }
        }

        btn_login.setOnClickListener {
            loginWithPassword = true
            if (ApplicationUtil.isNetworkAvailable(ApplicationUtil.getContext())) {
                val phoneNumber = txv_username.text.toString()
                val password = txv_password.text.toString()
                if (phoneNumber.isNotEmpty() && password.isNotEmpty()) {
                    var jsonObj = JSONObject()
                    jsonObj.put("mobile", phoneNumber)
                    jsonObj.put("password", password)
                    showProgressBar("Loading", true)
                    loginViewModel?.loginUser(jsonObj)
                } else {
                    if (phoneNumber.isEmpty() && password.isEmpty()){
                        ApplicationUtil.showDialog(this, "Please enter your mobile number and password")
                    } else {
                        if (phoneNumber.isEmpty() && password.isNotEmpty()) {
                            txv_username.setError("Field cannot be blank")
                            ApplicationUtil.showDialog(this, "Please enter your mobile number")
                        } else if (password.isEmpty()){
                            txv_password.setError("Field cannot be blank")
                            ApplicationUtil.showDialog(this, "Please enter your password")
                        }
                    }
                }
            } else {
                hideProgressBar()
                ApplicationUtil.showDialog(this, "Please check your internet connectivity")
            }
        }

        /*txt_forgot_password.makeLinks(
           Pair("Forgot Password?", android.view.View.OnClickListener {
                this.finish()
               val intent = Intent(
                   ApplicationUtil.getContext(),
                    tta.destinigo.talktoastro.activity.MainActivity::class.java
               )
               intent.putExtra("fragmentNumber", 2)
              startActivity(intent)
           })
       )*/

        txv_dont_have_account.makeLinks(
            Pair("Create Account", android.view.View.OnClickListener {
                this.finish()
                val intent = Intent(
                    ApplicationUtil.getContext(),
                    tta.destinigo.talktoastro.activity.MainActivity::class.java
                )
                intent.putExtra("fragmentNumber", 1)
                startActivity(intent)
            })
        )

    }

    fun deviceToken(){
      FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

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

        loginViewModel?.passDeviceTokenToServer()
    }



    private fun createDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(myActivity)
        // Set the alert dialog title
        builder.setTitle("Login failed")

        // Display a message on alert dialog
        builder.setMessage("Please check your username and password")
        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK"){_,_ ->

        }
        // Finally, make the alert dialog using builder
        dialog = builder.create()
    }

    private fun TextView.makeLinks(vararg links: Pair<String, android.view.View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: android.view.View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.movementMethod =
            LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(mApplication) as T
        }
    }

}
