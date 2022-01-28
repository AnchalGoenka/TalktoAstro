package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_forgot_password.*

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.ForgotPasswordViewModel
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject

class ForgotPasswordFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var forgotPasswordViewModel: ForgotPasswordViewModel? = null
    private var dialog: AlertDialog? = null
    override val layoutResId: Int
        get() = R.layout.fragment_forgot_password

    override fun getToolbarId(): Int {
        return 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        toolBarSetup()
    }

    private fun toolBarSetup() {
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        toolbar.title = "Forgot password"
        toolbar.btn_recharge.visibility = View.GONE
        toolbar.ib_notification.visibility=View.GONE
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forgotPasswordViewModel?.mutableForgotPasswordData?.removeObservers(this)
        forgotPasswordViewModel?.mutableForgotPasswordData?.value = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
createDialog()
        forgotPasswordViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(ForgotPasswordViewModel::class.java)
            btn_forgot_password.setOnClickListener {
            if (txv_email_forgot_password.text.isNotEmpty()) {
                showProgressBar("Please wait...")
                var json = JSONObject()
                json.put("email", txv_email_forgot_password.text)
                forgotPasswordViewModel?.changePassword(json)
                forgotPasswordViewModel?.mutableForgotPasswordData?.observe(this, Observer {
                    hideProgressBar()
                    if (it != null) {
                        if (it.success == 1) {
                            SharedPreferenceUtils.put(
                                ApplicationConstant.USERID, it.userID,
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                            )
                            var bundle = Bundle()
                            bundle.putString("otp", it.otp.toString())
                            bundle.putString("email", txv_email_forgot_password.text.toString())
                            var transaction: FragmentTransaction =
                                myActivity.supportFragmentManager.beginTransaction()
                            transaction.replace(
                                R.id.frame_layout,
                                NewPasswordFragment.newInstance(bundle),
                                NewPasswordFragment.tagName
                            )
                            transaction.addToBackStack(NewPasswordFragment.tagName)
                            transaction.commit()
                        } else {
                            if (dialog != null) {
                                hideProgressBar()
                                dialog!!.dismiss()
                                // Display the alert dialog on app interface
                                if (!dialog!!.isShowing) {
                                    dialog!!.show()
                                }
                            }
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
            } else {
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please enter your email-id",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(myActivity)
        // Display a message on alert dialog
        builder.setMessage("Email id does not exists")
        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK"){_,_ ->

        }
        // Finally, make the alert dialog using builder
        dialog = builder.create()
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ForgotPasswordViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName: String
            get() = ForgotPasswordFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ForgotPasswordFragment {
            val fragment = ForgotPasswordFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
