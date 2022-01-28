package tta.destinigo.talktoastro.view

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var mainViewModel: MainViewModel? = null
    override val layoutResId: Int
        get() = R.layout.fragment_settings

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
        mainViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(MainViewModel::class.java)
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "Settings"
        val txv_user_profile: TextView = myActivity.findViewById(R.id.txv_user_profile)
        switch_status.isChecked = SharedPreferenceUtils.readBoolean(ApplicationConstant.ISDEVICETOKENENABLED, true,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
//        txv_user_profile.setOnClickListener {
//            var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.frame_layout, UserProfileFragment.newInstance(null), UserProfileFragment.tagName)
//            transaction.addToBackStack(UserProfileFragment.tagName)
//            transaction.commit()
//        }

        switch_status.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                SharedPreferenceUtils.put(ApplicationConstant.ISDEVICETOKENENABLED, true, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            } else {
                SharedPreferenceUtils.put(ApplicationConstant.ISDEVICETOKENENABLED, false, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            }
            mainViewModel?.passDeviceTokenToServer()
        }

        val versionCode = tta.destinigo.talktoastro.BuildConfig.VERSION_CODE
        val versionName = tta.destinigo.talktoastro.BuildConfig.VERSION_NAME

        textView_version.text = "Version: ${versionCode.toString()}.$versionName"
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = SettingsFragment::class.java.name

        fun newInstance(bundle: Bundle?): SettingsFragment {
            val fragment = SettingsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
