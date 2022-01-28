package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_new_password.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.LoginActivity
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.viewmodel.NewPasswordViewModel


class NewPasswordFragment : BaseFragment() {
    private var newPasswordViewModel: NewPasswordViewModel? = null
    override val layoutResId: Int
        get() = R.layout.fragment_new_password

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    fun toolBarSetUp(toolbar: androidx.appcompat.widget.Toolbar) {
        toolbar.layout_btn_recharge.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password_container.visibility = View.GONE
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Verify otp",
            true,
            R.drawable.ic_arrow_back_24dp
        )
        toolBarSetUp(toolbar)
        val otp = arguments?.getString("otp")
        val email = arguments?.getString("email")
        txv_new_password_email.setText(email)
        newPasswordViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(NewPasswordViewModel::class.java)

        num1_pass.requestFocus()
        num1_pass.showSoftInputOnFocus = true
        num1_pass.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num1_pass.length() == 1)
                    num2_pass.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num2_pass.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num2_pass.length() == 1)
                    num3_pass.requestFocus()
                if (num2_pass.length() == 0)
                    num1_pass.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num3_pass.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num3_pass.length() == 1)
                    num4_pass.requestFocus()
                if (num3_pass.length() == 0)
                    num2_pass.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        num4_pass.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (num4_pass.length() == 0)
                    num3_pass.requestFocus()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        btn_submit_pass.setOnClickListener {
            verifyOtp(num1_pass, num2_pass, num3_pass, num4_pass, otp!!, toolbar)
        }

        btn_change_password.setOnClickListener {
            if (txv_confirm_password.text.isNotEmpty() &&
                    txv_new_password.text.isNotEmpty() &&
                    txv_new_password_email.text.isNotEmpty()) {
                if (txv_new_password.text.toString() == txv_confirm_password.text.toString()) {
                    showProgressBar("Please wait...")
                    var json = JSONObject()
                    json.put("email", txv_new_password_email.text)
                    json.put("Password", txv_new_password.text)
                    json.put("confirmPassword", txv_confirm_password.text)
                    newPasswordViewModel?.changePassword(json)
                    newPasswordViewModel?.mutableNewPasswordData?.observe(this, Observer {
                        hideProgressBar()
                        if (it.success == 1) {
                            val intent = Intent(
                                ApplicationUtil.getContext(),
                                LoginActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            Toast.makeText(ApplicationUtil.getContext(), it.message, Toast.LENGTH_SHORT)
                        }

                    })
                } else {
                    val builder = AlertDialog.Builder(myActivity)
                    // Set the alert dialog title
                    builder.setTitle("Password mismatch")

                    // Display a message on alert dialog
                    builder.setMessage("Password and confirm password does not matches")
                    // Display a neutral button on alert dialog
                    builder.setNeutralButton("OK"){_,_ ->

                    }
                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()
                    // Display the alert dialog on app interface
                    dialog.show()
                }

            } else {
                Toast.makeText(ApplicationUtil.getContext(), "All field are mandatory", Toast.LENGTH_SHORT)
            }

        }
    }

    fun verifyOtp(num1: EditText, num2: EditText, num3: EditText, num4: EditText, otp: String, toolbar: Toolbar){
        val userEnteredOTP = "${num1.text}${num2.text}${num3.text}${num4.text}"
        if (userEnteredOTP == otp){
            txv_otp_verified.visibility = View.VISIBLE
            toolbar.title = "Enter new password"
            Handler().postDelayed(Runnable {
                password_container.visibility = View.VISIBLE
                otp_container.visibility = View.GONE
            }, 1000)

        }else{
            showAlertDialog()
        }
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

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewPasswordViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName: String
            get() = NewPasswordFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): NewPasswordFragment {
            val fragment = NewPasswordFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
