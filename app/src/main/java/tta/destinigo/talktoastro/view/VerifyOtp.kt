package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.CalendarContract
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_thank_you.*
import kotlinx.android.synthetic.main.fragment_verify_otp.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.LoginActivity
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.viewmodel.LoginViewModel
import tta.destinigo.talktoastro.viewmodel.VerifyOtpViewModel

class VerifyOtp : tta.destinigo.talktoastro.BaseFragment() {

    private var verifyOtpModel: VerifyOtpViewModel? = null
    var timer: CountDownTimer? = null
    private var loginViewModel: LoginViewModel? = null
    var session_id:String? =null
    override val layoutResId: Int
        get() = R.layout.fragment_verify_otp

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Verify OTP", true, R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener {
            val type = arguments?.getString("Register")
            if (type == "login") {
                val intent =
                    Intent(ApplicationUtil.getContext(), LoginActivity::class.java)
                startActivity(intent)
            } else {
                popBackStack(myActivity)
            }
        }
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val num1: EditText = myActivity.findViewById(R.id.num1)
        val num2: EditText = myActivity.findViewById(R.id.num2)
        val num3: EditText = myActivity.findViewById(R.id.num3)
        val num4: EditText = myActivity.findViewById(R.id.num4)
        val num5: EditText = myActivity.findViewById(R.id.num5)
        val num6: EditText = myActivity.findViewById(R.id.num6)

        val btn_submit: Button = myActivity.findViewById(R.id.btn_submit)
        val type = arguments?.getString("Register")
        runTimer()
        verifyOtpModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(VerifyOtpViewModel::class.java)



        verifyOtpModel?.resendOTPMutableLiveData?.observe(this, Observer {
            if(it.otp==null){
                showProgressBar("Loading")
                val txv_otp_verified: TextView = myActivity.findViewById(R.id.txv_otp_verified)
                txv_otp_verified.visibility = View.VISIBLE
                if (type == "login") {
                    val name = it!!.firstName + " " + it!!.lastName
                    SharedPreferenceUtils.put(
                        ApplicationConstant.USERNAME, it!!.email,
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
                }
                Handler().postDelayed(Runnable {
                    hideProgressBar()
                    val fragment = VerifyOtp()
                    var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                    transaction.remove(fragment)
                    transaction.commit()
                    val intent = Intent(
                        ApplicationUtil.getContext(),
                        MainActivity::class.java
                    )
                    startActivity(intent)
                }, 500)
            }else{
                it.otp.let { it1 ->
                    SharedPreferenceUtils.put(ApplicationConstant.OTP, it1, SharedPreferenceUtils.getSharedPref(
                        ApplicationUtil.getContext()))
                }
            }
        })
        verifyOtpModel?.loginDidFailed?.observe(this, Observer {
            hideProgressBar()
            ApplicationUtil.showDialog(myActivity, it)
        })

        verifyOtpModel?.verifyOTPWithTWOFACTOR?.observe(viewLifecycleOwner, Observer {
            val mobile = SharedPreferenceUtils.readString(ApplicationConstant.MOBILE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            val otp = SharedPreferenceUtils.readString(ApplicationConstant.OTP, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                    var jsonObj =JSONObject()
                    // jsonObj.put("otp", otp)
                      jsonObj.put("otp", "5555")
                      jsonObj.put("mobile", mobile)
            verifyOtpModel?.verifyOtp(jsonObj)
        })


        verifyOtpModel?.resendVia2Factor?.observe(viewLifecycleOwner, Observer {
          //  session_id =it
            SharedPreferenceUtils.put(ApplicationConstant.Details, it, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))

        })
        verifyOtpModel?.arrayListMutableLiveData?.observe(viewLifecycleOwner, Observer {
            showProgressBar("Loading")
            val txv_otp_verified: TextView = myActivity.findViewById(R.id.txv_otp_verified)
            txv_otp_verified.visibility = View.VISIBLE
            if (type == "login") {
                val name = it!!.firstName + " " + it!!.lastName
                SharedPreferenceUtils.put(
                    ApplicationConstant.USERNAME, it!!.email,
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
            }
            Handler().postDelayed(Runnable {
                hideProgressBar()
                val intent = Intent(
                    ApplicationUtil.getContext(),
                    MainActivity::class.java
                )
                startActivity(intent)
            }, 500)
        })

        num1.requestFocus()
        num1.showSoftInputOnFocus = true
        num1.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num1.length() == 1)
                    num2.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num2.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num2.length() == 1)
                    num3.requestFocus()
                if (num2.length() == 0)
                    num1.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num3.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num3.length() == 1)
                    num4.requestFocus()
                if (num3.length() == 0)
                    num2.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num4.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num4.length() == 1)
                    num5.requestFocus()
                if (num4.length() == 0)
                    num3.requestFocus()

                /*if (num4.length() == 0)
                    num3.requestFocus()*/
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num5.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num5.length() == 1)
                    num6.requestFocus()
                if (num5.length() == 0)
                    num4.requestFocus()

                /*if (num4.length() == 0)
                    num3.requestFocus()*/
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num6.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (num6.length() == 0)
                    num5.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        btn_submit.setOnClickListener {
            showProgressBar("Loading")
            verifyOtp(num1, num2, num3, num4,num5,num6)
        }

        button_Resend.setOnClickListener{
            var type = arguments?.getString("Register")
            if (type == "login") {
             //   val hashVal = arguments?.getSerializable("login credential") as HashMap<String, String>
               // verifyOtpModel?.resendOTPRequest(JSONObject(hashVal as Map<*, *>))
                val mobile = SharedPreferenceUtils.readString(ApplicationConstant.MOBILE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                verifyOtpModel?.resendWith2Factor(mobile!!)
                num1.requestFocus()
                num1.text.clear()
                num2.text.clear()
                num3.text.clear()
                num4.text.clear()
                num5.text.clear()
                num6.text.clear()
                num1.requestFocus()
                runTimer()
            } else {
               /* val hashVal = arguments?.getSerializable("Register credential") as HashMap<String, String>
                verifyOtpModel?.resendOTPRequest(JSONObject(hashVal as Map<*, *>))
                runTimer()*/
                val mobile = SharedPreferenceUtils.readString(ApplicationConstant.MOBILE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                verifyOtpModel?.resendWith2Factor(mobile!!)
                num1.requestFocus()
                num1.text.clear()
                num2.text.clear()
                num3.text.clear()
                num4.text.clear()
                num5.text.clear()
                num6.text.clear()
                num1.requestFocus()
                runTimer()
            }
        }
    }

    fun verifyOtp(num1: EditText, num2: EditText, num3: EditText, num4: EditText,num5 :EditText ,num6 :EditText){
        val userId = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val mobile = SharedPreferenceUtils.readString(ApplicationConstant.MOBILE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val otp = SharedPreferenceUtils.readString(ApplicationConstant.OTP, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val userEnteredOTP = "${num1.text}${num2.text}${num3.text}${num4.text}${num5.text}${num6.text}"
        var jsonObj = JSONObject()

            val type = arguments?.getString("Register")
            //var url = ""
            if (type == "login") {
                //url = "${ApplicationConstant.VERIFYOTP}?verify=\"1\"&mobile=\"${mobile}\""
               // jsonObj.put("otp", otp)
              //  jsonObj.put("otp", userEnteredOTP)
              //  jsonObj.put("mobile", mobile)
                //verifyOtpModel?.resendOTPRequest(jsonObj)

                 session_id =SharedPreferenceUtils.readString(ApplicationConstant.Details, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                verifyOtpModel?.verifyOTPWITHTWOFACTOR(session_id!!,userEnteredOTP)

            } else if( type == "register") {
                //url = "${ApplicationConstant.VERIFYOTP}?verify=\"1\"&userID=\"${userId}\""
               /* jsonObj.put("verify", "1")
                jsonObj.put("userID", userId)
                verifyOtpModel?.verifyOtp(jsonObj)*/
                session_id =SharedPreferenceUtils.readString(ApplicationConstant.Details, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                verifyOtpModel?.verifyOTPWITHTWOFACTOR(session_id!!,userEnteredOTP)

        }
    }

    fun runTimer() {
        button_Resend.isEnabled = false
        button_Resend.setBackgroundResource(R.drawable.button_rounded_corners_gray)
        button_Resend.setTextColor(Color.DKGRAY)
        timer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished/1000) < 10) {
                    txv_timer_otp.setText("0:0"+ (millisUntilFinished/1000))
                } else {
                    txv_timer_otp.setText("0:"+ (millisUntilFinished/1000))
                }
            }

            override fun onFinish() {
                button_Resend.isEnabled = true
                button_Resend.setBackgroundResource(R.drawable.button_signup)
                button_Resend.setTextColor(Color.WHITE)
                txv_timer_otp.setText("00:00")
            }
        }
        timer?.start()
    }

    private fun showAlertDialog() {
            hideProgressBar()
            // Initialize a new instance of
            val builder = AlertDialog.Builder(myActivity)
            // Set the alert dialog title
            builder.setTitle("Incorrect pin")

            // Display a message on alert dialog
            builder.setMessage("Please enter correct pin")
            // Display a neutral button on alert dialog
            builder.setNeutralButton("OK"){_,_ ->

            }
            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            // Display the alert dialog on app interface
            dialog.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        popBackStack(myActivity)
        val fragment = VerifyOtp()
        var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.addToBackStack(null)
        //  transaction.addToBackStack(VerifyOtp.tagName)
        transaction.commit()

    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VerifyOtpViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName: String
        get() = VerifyOtp::class.java.name

        fun newInstance(bundle: Bundle?): VerifyOtp{
            val fragment = VerifyOtp()
            fragment.arguments = bundle
            return fragment
        }
    }
}
