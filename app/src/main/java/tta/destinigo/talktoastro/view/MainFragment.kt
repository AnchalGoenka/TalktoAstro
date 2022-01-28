package tta.destinigo.talktoastro.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.razorpay.Checkout
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.salesiqembed.ZohoSalesIQ
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activities.ChannelActivity
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.adapter.DemoInfiniteAdapter
import tta.destinigo.talktoastro.chat.ChatAstrologerListFragment
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.MainViewModel


class MainFragment : tta.destinigo.talktoastro.BaseFragment() {

    private var checkOut = Checkout()

    private var mainViewModel: MainViewModel? = null
    private var toolbar: Toolbar? = null

    override val layoutResId: Int
        get() = R.layout.fragment_main

    override fun getToolbarId(): Int {
        return 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        toolBarSetup()
        updateUserWallet()
        val pager: com.asksira.loopingviewpager.LoopingViewPager =
            myActivity.findViewById(R.id.pager)
        pager.resumeAutoScroll()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showProgressBar("Loading")
        mainViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(MainViewModel::class.java)
        getBanner()
        updateUserWallet()
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    private fun toolBarSetup() {
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "TalktoAstro",
            false,
            R.drawable.ic_hamburger
        )
        toolbar!!.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarSetup()
        val view_call: ConstraintLayout = myActivity.findViewById(R.id.view_call)
        val view_report: ConstraintLayout = myActivity.findViewById(R.id.view_report)
        val mFab = myActivity.findViewById<FloatingActionButton>(R.id.mfab)
      //  val mFab:FloatingActionButton = myActivity.findViewById(R.id.mfab)
        (activity as MainActivity).bottom_navigation.menu.getItem(0).isChecked = true
        view_call.setOnClickListener {
            var transaction: FragmentTransaction =
                myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frame_layout,
                HomeFragment.newInstance(null, ApplicationConstant.CALL_STRING),
                HomeFragment.tagName
            )
            transaction.addToBackStack(HomeFragment.tagName)
            transaction.commit()
        }
        view_report.setOnClickListener {
            var transaction: FragmentTransaction =
                myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frame_layout,
                HomeFragment.newInstance(null, ApplicationConstant.REPORT_STRING),
                HomeFragment.tagName
            )
            transaction.addToBackStack(HomeFragment.tagName)
            transaction.commit()
        }
        mFab.setOnClickListener {

            ZohoLiveChat.Chat.open()
           // ZohoLiveChat.Chat.show()

           // showDialog("Dialog")
//            val intent = Intent(ApplicationUtil.getContext(), CustomerSuppportActivity::class.java)
//            startActivity(intent)


        }

        view_chat.setOnClickListener {
            var transaction: FragmentTransaction =
                myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frame_layout,
                ChatAstrologerListFragment.newInstance(null, ApplicationConstant.CHAT_STRING),
                ChatAstrologerListFragment.tagName
            )
            transaction.addToBackStack(ChatAstrologerListFragment.tagName)
            transaction.commit()
        }


        textView_starting_from_per_min.text =
            resources.getString(R.string.starting_from_per_min) + " "
        textView_starting_from_per_min_2.text =
            resources.getString(R.string.starting_from_per_min_2) + " "
       /* textView_starting_from_per_min_3.text =
            resources.getString(R.string.starting_from_per_min_3)
*/
        textView_starting_from_per_min_3.text =
            resources.getString(R.string.starting_from_only_3)
        textView_starting_only_report.text = resources.getString(R.string.starting_from_only) + " "
        textView_starting_only_report_2.text =
            resources.getString(R.string.starting_from_only_2) + " "
        textView_starting_only_report_3.text = resources.getString(R.string.starting_from_only_3)

        textView_starting_only_chat.text = resources.getString(R.string.starting_from_only) + " "
       textView_starting_only_chat_2.text =
            resources.getString(R.string.starting_from_only_chat) + " "
       textView_starting_only_chat_3.text = resources.getString(R.string.starting_from_only_3)
    }
    private fun showDialog(title: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.customersupport_dialog)
        /*val body = dialog.findViewById(R.id.body) as TextView
        body.text = "title"*/
        val window: Window? = dialog.getWindow()
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val yesBtn = dialog.findViewById(R.id.txv_Phone) as EditText
        val noBtn = dialog.findViewById(R.id.yesButton) as EditText
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
    fun getBanner() {
        mainViewModel?.arrayListMutableBannerData?.observe(
            viewLifecycleOwner,
            Observer { banner_model_list ->
                if (banner_model_list.isNullOrEmpty() || banner_model_list.first().marquee.isNullOrEmpty()) {
                    return@Observer
                }
                textView_marquee.setText(Html.fromHtml(banner_model_list.first().marquee.first().scope))
                textView_marquee.isSelected = true

//                textView_marquee.startAnimation(
//                    AnimationUtils.loadAnimation(
//                        context,
//                        R.anim.text_transslate
//                    ) as Animation
//                )

                try {
                    myActivity.findViewById<FrameLayout>(R.id.header_layout).setBackgroundColor(ContextCompat.getColor(context!!,R.color.white))
                } catch (e: Exception) {
                }

                var arrOfImages = ArrayList<String>()
                val pager: com.asksira.loopingviewpager.LoopingViewPager =
                    myActivity.findViewById(R.id.pager)
                val indicator: ScrollingPagerIndicator = myActivity.findViewById(R.id.indicator)
                if (banner_model_list!!.size > 0) {
                    banner_model_list.forEach { banner_model ->
                        banner_model.bannerList.forEach {
                            arrOfImages.add(it.image)
                        }
                    }

                    val adapter = DemoInfiniteAdapter(context!!, arrOfImages, true)
                    pager.adapter = adapter
                    indicator.attachToPager(pager)

                    hideProgressBar()
                }

            })
    }

    override fun onPause() {
        val pager: com.asksira.loopingviewpager.LoopingViewPager =
            myActivity.findViewById(R.id.pager)
        pager.pauseAutoScroll()
        super.onPause()
    }

    fun updateUserWallet() {
        val userID = SharedPreferenceUtils.readString(
            ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val json = JSONObject()
        json.put("userID", userID)
        if (mainViewModel != null) {
            mainViewModel?.getWalletTransactions(json)
            mainViewModel?.balanceUpdateResp?.observe(viewLifecycleOwner, Observer {
                SharedPreferenceUtils.put(
                    ApplicationConstant.BALANCE, it,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                toolBarSetup()
            })
        }
    }



    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = MainFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): MainFragment {
            val fragment = MainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
