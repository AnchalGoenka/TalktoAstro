package tta.destinigo.talktoastro.view

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.app_bar_main.*

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.UserProfile
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.activity.MainActivity


class UserProfileFragment : tta.destinigo.talktoastro.BaseFragment() {

    var userProfileViewModel: UserProfileViewModel? = null
    var originalUserProfile: UserProfile? = null

    override val layoutResId: Int
        get() = R.layout.fragment_user_profile

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

        (activity as MainActivity).bottom_navigation.menu.getItem(1).isChecked = true
        userProfileViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(UserProfileViewModel::class.java)
        var jsonObj = JSONObject()
        val userId = SharedPreferenceUtils.readString(
            ApplicationConstant.USERID,
            "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        jsonObj.put("userID", userId)
        userProfileViewModel?.getUserProfile(jsonObj)
        userProfileViewModel?.arrayMutableUserProfile?.observe(this, Observer {
            originalUserProfile = it
            val edit_first_name: EditText = myActivity.findViewById(R.id.edit_first_name)
            val edit_last_name: EditText = myActivity.findViewById(R.id.edit_last_name)
            val txv_edit_mobilenumber: EditText =
                myActivity.findViewById(R.id.txv_edit_mobilenumber)
            val txv_edit_email: EditText = myActivity.findViewById(R.id.txv_edit_email)
            val ccp_edit: CountryCodePicker = myActivity.findViewById(R.id.ccp_edit)

            edit_first_name.setText(it!!.firstName)
            edit_last_name.setText(it!!.lastName)
            ccp_edit.fullNumber = it.phonecode
            txv_edit_mobilenumber.setText(it!!.mobile)
            txv_edit_email.setText(it!!.email)
        })
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarSetUp()
        val edit_first_name: EditText = myActivity.findViewById(R.id.edit_first_name)
        val edit_last_name: EditText = myActivity.findViewById(R.id.edit_last_name)
        val txv_edit_mobilenumber: EditText = myActivity.findViewById(R.id.txv_edit_mobilenumber)
        val txv_edit_email: EditText = myActivity.findViewById(R.id.txv_edit_email)
        val ccp_edit: CountryCodePicker = myActivity.findViewById(R.id.ccp_edit)
        val chk_edit: CheckBox = myActivity.findViewById(R.id.chk_edit)
        val btn_signup: Button = myActivity.findViewById(R.id.btn_signup)

        edit_first_name.isEnabled = false
        edit_last_name.isEnabled = false
        txv_edit_mobilenumber.isEnabled = false
        txv_edit_email.isEnabled = false

        chk_edit.setOnClickListener {
            if (chk_edit.isChecked) {
                edit_first_name.isEnabled = true
                edit_last_name.isEnabled = true
                ccp_edit.isEnabled = true
                ccp_edit.isClickable = true
                txv_edit_mobilenumber.isEnabled = true
            } else {
                edit_first_name.isEnabled = false
                edit_last_name.isEnabled = false
                ccp_edit.isEnabled = false
                ccp_edit.isClickable = false
                txv_edit_mobilenumber.isEnabled = false
            }
        }

        btn_signup.setOnClickListener {
            val oldCurrency = SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            if (originalUserProfile?.firstName != edit_first_name.text.toString() || originalUserProfile?.lastName != edit_last_name.text.toString() ||
                (originalUserProfile?.phonecode != ccp_edit.fullNumber && ((oldCurrency == "91" && ccp_edit.fullNumber != "91") || !(oldCurrency != "91" && ccp_edit.fullNumber != "91") ||
                        (oldCurrency != "91" && ccp_edit.fullNumber == "91"))) || originalUserProfile?.mobile != txv_edit_mobilenumber.text.toString() ||
                originalUserProfile?.email != txv_edit_email.text.toString()
            ) {
                var mapParam = HashMap<String, String>()
                mapParam["first_name"] = edit_first_name.text.toString()
                mapParam["last_name"] = edit_last_name.text.toString()
                mapParam["email"] = txv_edit_email.text.toString()
                mapParam["phonecode"] = ccp_edit.fullNumber
                mapParam["mobile"] = txv_edit_mobilenumber.text.toString()
                mapParam["userID"] = SharedPreferenceUtils.readString(
                    ApplicationConstant.USERID,
                    "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                ).toString()

                showProgressBar("Please wait...", true)
                userProfileViewModel?.updateProfile(mapParam)
                userProfileViewModel?.arrayEditMutable?.observe(this, Observer {
                    hideProgressBar()
                    if (it!!) {
                        originalUserProfile = UserProfile(
                            originalUserProfile!!.balance,
                            txv_edit_email.text.toString(),
                            edit_first_name.text.toString(),
                            originalUserProfile!!.id,
                            edit_last_name.text.toString(),
                            txv_edit_mobilenumber.text.toString(),
                            ccp_edit.fullNumber,
                            originalUserProfile!!.verified
                        )

                        // Updating the balance and currency on toolbar

                        SharedPreferenceUtils.put(
                            ApplicationConstant.PHONECODE,
                            ccp_edit.fullNumber,
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        val name = edit_first_name.text.toString() + edit_last_name.text.toString()
                        SharedPreferenceUtils.put(
                            ApplicationConstant.NAME, name,
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        SharedPreferenceUtils.put(
                            ApplicationConstant.MOBILE, txv_edit_mobilenumber.text.toString(),
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        SharedPreferenceUtils.put(
                            ApplicationConstant.USERNAME, txv_edit_email.text.toString(),
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        val balance = SharedPreferenceUtils.readString(
                            ApplicationConstant.BALANCE,
                            "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
//                        if (ccp_edit.fullNumber == "91") {
//                            val newBal = balance!!.toInt() * ApplicationConstant.constConverterVal
//                            SharedPreferenceUtils.put(
//                                ApplicationConstant.BALANCE,
//                                newBal.toString(),
//                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
//                            )
//                        } else {
//                            val newBal: Int =
//                                balance!!.toInt() / ApplicationConstant.constConverterVal
//                            SharedPreferenceUtils.put(
//                                ApplicationConstant.BALANCE,
//                                newBal.toString(),
//                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
//                            )
//                        }
                        toolBarSetUp()
                        Toast.makeText(
                            ApplicationUtil.getContext(),
                            "Profile updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            ApplicationUtil.getContext(),
                            "Error while updating. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "No change detected.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun toolBarSetUp() {
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Edit Profile",
            false,
            R.drawable.ic_hamburger
        )
        toolbar!!.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserProfileViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = UserProfileFragment::class.java.name

        fun newInstance(bundle: Bundle?): UserProfileFragment {
            val fragment = UserProfileFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
