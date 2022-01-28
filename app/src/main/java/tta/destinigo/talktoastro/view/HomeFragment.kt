package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.RechargeActivity
import tta.destinigo.talktoastro.model.AstrologerListModel
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.viewmodel.HomeViewModel


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : tta.destinigo.talktoastro.BaseFragment(), SearchView.OnQueryTextListener,
    android.widget.SearchView.OnQueryTextListener {

    private var homeViewModel: HomeViewModel? = null
    private var homeAdapter: tta.destinigo.talktoastro.adapter.HomeAdapter? = null
    private var changeStatus: Boolean = false
    private var adapterPosition: Int = 0

    override val layoutResId: Int
        get() = R.layout.fragment_home

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onResume() {
        super.onResume()
        if (changeStatus) {
            changeStatus = false
            homeAdapter!!.notifyItemChanged(adapterPosition)
        }
        refreshList()
    }

    fun refreshList() {
        if (homeAdapter != null) {
            homeViewModel?.arrayListMutableLiveData?.value?.clear()
            homeAdapter!!.notifyDataSetChanged()
            showProgressBar("Loading")
            if (!SharedPreferenceUtils.readBoolean(ApplicationConstant.RELOADHOMEFRAGMENT, false, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))) {
                if (typeButtonPressed == ApplicationConstant.CALL_STRING) {
                    homeViewModel!!.getAstrologersList()
                } else {
                    homeViewModel!!.getReports()
                }
            }
            SharedPreferenceUtils.put(ApplicationConstant.RELOADHOMEFRAGMENT, false,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Astrologers", false, R.drawable.ic_arrow_back_24dp)
        if (typeButtonPressed == ApplicationConstant.REPORT_STRING) {
            toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Reports", false, R.drawable.ic_arrow_back_24dp)
        }
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
        //toolbar.ib_notification.visibility=View.INVISIBLE
        pull_to_refresh.setOnRefreshListener {
            refreshList()
            pull_to_refresh.isRefreshing = false
        }
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        searchView.setSearchableInfo(searchManager!!.getSearchableInfo(activity.componentName))
        searchView.setOnQueryTextListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showProgressBar("Loading")
        // Inflate the layout for this fragment
        homeViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(HomeViewModel::class.java)
        if (typeButtonPressed == ApplicationConstant.CALL_STRING) {
            homeViewModel!!.getAstrologersList()
        } else {
            homeViewModel!!.getReports()
        }
        homeViewModel?.arrayListMutableLiveData?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()
            homeAdapter = tta.destinigo.talktoastro.adapter.HomeAdapter(
                it,
                ApplicationConstant.CALL_STRING,
                { homeModel, position ->
                    val bundle = Bundle()
                    bundle.putParcelable("profile", homeModel)
                    bundle.putString("extra", "Call")
                    val profileFragment = ProfileFragment.newInstance(bundle)
                    var transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, profileFragment, ProfileFragment.tagName)
                    transaction.addToBackStack(ProfileFragment.tagName)
                    transaction.commit()

                },
                { homeModel, position ->
                    if (homeModel.phoneStatus != "online") {
                        return@HomeAdapter
                    }
                    if (!ApplicationUtil.checkLogin()) {
                        val intent = Intent(
                            ApplicationUtil.getContext(),
                            tta.destinigo.talktoastro.activity.LoginActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        val balance = SharedPreferenceUtils.readString(ApplicationConstant.BALANCE, "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                        val phoneCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                        if ( (phoneCode == "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_RUPEE) ||
                            (phoneCode != "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_DOLLAR) ) {
                            val intent =
                                Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
                            startActivity(intent)
                        } else {
                            var json = JSONObject()
                            json.put("astroID", homeModel.id)
                            json.put(
                                "userID", SharedPreferenceUtils.readString(
                                    ApplicationConstant.USERID, "",
                                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                                )
                            )
                            homeViewModel!!.callAstrologer(json)
                            homeViewModel!!.MutableCallData.observe(viewLifecycleOwner, Observer {
                                if (it!!.success == 1) {
                                    changeStatus = true
                                    Toast.makeText(
                                        ApplicationUtil.getContext(),
                                        it.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                    homeModel.phoneStatus = "busy"
                                    homeAdapter!!.notifyItemChanged(position)
                                    val bundle = Bundle()
                                    bundle.putString("ThankYou", ApplicationConstant.CALL_STRING)
                                    bundle.putString(
                                        "ThankYouName",
                                        "${homeModel.firstName} ${homeModel.lastName}"

                                    )
                                    var transaction: FragmentTransaction =
                                        myActivity.supportFragmentManager.beginTransaction()
                                    transaction.replace(
                                        R.id.frame_layout,
                                        ThankYouFragment.newInstance(bundle),
                                        ThankYouFragment.tagName
                                    )
                                    transaction.addToBackStack(ThankYouFragment.tagName)
                                    transaction.commit()
                                }
                            })
                        }
                    }
                }
            ) { homeModel, position ->
                if (ApplicationUtil.checkLogin()) {
                    val notifyCount = SharedPreferenceUtils.readString(
                        ApplicationConstant.NOTIFYCOUNT,
                        "0",
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    )
                    val notifyAstroID = SharedPreferenceUtils.readString(ApplicationConstant.CHECKNOTIFYASTROID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                    if (notifyCount == "1" && homeModel.id != notifyAstroID) {
                        return@HomeAdapter
                    }
                    var json = JSONObject();
                    var msg = ""
                    if (notifyAstroID == homeModel.id) {
                        msg = "Do you want to remove notification?"
                    } else {
                        msg = "Do you want to set notification?"
                    }
                    val alertDialogBuilder =
                        AlertDialog.Builder(
                            context
                        )
                    alertDialogBuilder.setNeutralButton("OK"){_,_ ->
                        if (notifyAstroID == homeModel.id) {
                            json.put("astroID", notifyAstroID)
                            json.put(
                                "userID", SharedPreferenceUtils.readString(
                                    ApplicationConstant.USERID, "",
                                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                                )
                            )
                            homeViewModel!!.removeNotification(json)
                        } else {
                            json.put("astroID", homeModel.id)
                            json.put(
                                "userID", SharedPreferenceUtils.readString(
                                    ApplicationConstant.USERID, "",
                                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                                )
                            )
                            homeViewModel!!.setNotifyIcon(json)
                        }
                    }
                    // set dialog message
                    alertDialogBuilder
                        .setMessage(msg)
                        .setCancelable(true)
                    alertDialogBuilder.show()
                } else {
                    val intent = Intent(ApplicationUtil.getContext(), tta.destinigo.talktoastro.activity.LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            val astrologer_list =
                myActivity.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.astrolgerList)
            astrologer_list.layoutManager =
                tta.destinigo.talktoastro.util.LinearLayoutManagerWrapper(
                    ApplicationUtil.getContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            /*val resId: Int = R.anim.layout_anim_right_to_left
            val animation: LayoutAnimationController =
                AnimationUtils.loadLayoutAnimation(context, resId)
            astrologer_list.setLayoutAnimation(animation)*/
            astrologer_list.adapter = homeAdapter
            if (astrologer_list.layoutManager == null) {
                astrologer_list.addItemDecoration(MarginItemDecorator(1))
            }
            astrologer_list.hasFixedSize()
            if (ApplicationUtil.checkLogin()) {
                homeViewModel!!.checkNotification()
            }
        })
        homeViewModel?.notificationRespone?.observe(viewLifecycleOwner, Observer {
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "1",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            if (homeAdapter != null){
                homeAdapter!!.notifyDataSetChanged()
            }
            ApplicationUtil.showDialog(myActivity, it)
        })
        homeViewModel?.checkNotification?.observe(viewLifecycleOwner, Observer {
            val astroID = it.get("astroID")
            if (astroID != null && homeAdapter != null){
                SharedPreferenceUtils.put(ApplicationConstant.CHECKNOTIFYASTROID, astroID, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                homeAdapter!!.notifyDataSetChanged()
            }
        })
        homeViewModel?.removeNotification?.observe(viewLifecycleOwner, Observer {
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "0",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            if (homeAdapter != null){
                SharedPreferenceUtils.put(ApplicationConstant.CHECKNOTIFYASTROID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                homeAdapter!!.notifyDataSetChanged()
            }
        })
        homeViewModel?.notificationResponeFailed?.observe(viewLifecycleOwner, Observer {
            ApplicationUtil.showDialog(myActivity, it)
        })
        val balance = SharedPreferenceUtils.readString(
            ApplicationConstant.BALANCE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        homeViewModel?.arrayListMutableReportData?.observe(viewLifecycleOwner, Observer {
            hideProgressBar()
            homeAdapter = tta.destinigo.talktoastro.adapter.HomeAdapter(
                it,
                ApplicationConstant.REPORT_STRING,
                { homeViewModel, position ->
                    val bundle = Bundle()
                    bundle.putParcelable("profile", homeViewModel)
                    bundle.putString("extra", "Report")
                    val profileFragment = ProfileFragment.newInstance(bundle)
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, profileFragment, ProfileFragment.tagName)
                    transaction.addToBackStack(ProfileFragment.tagName)
                    transaction.commit()
                },
                { homeViewModel, position ->
                     if (!ApplicationUtil.checkLogin()){
                         val intent = Intent(
                             ApplicationUtil.getContext(),
                             tta.destinigo.talktoastro.activity.LoginActivity::class.java
                         )
                         startActivity(intent)
                     } /*else if(balance=="null"){ val intent = Intent(
                        ApplicationUtil.getContext(),
                        tta.destinigo.talktoastro.activity.RechargeActivity::class.java
                    )
                        startActivity(intent)
                    } else if (balance!!.toInt() < resources.getInteger(R.integer.min_user_balance)) {
                    val intent = Intent(
                        ApplicationUtil.getContext(),
                        tta.destinigo.talktoastro.activity.RechargeActivity::class.java
                    )
                    startActivity(intent)
                }*/ else if (ApplicationUtil.checkLogin()) {
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    val bundle = Bundle()
                    bundle.putString("astroID", homeViewModel.id)
                    bundle.putString(
                        "ThankYouName",
                        "${homeViewModel.firstName} ${homeViewModel.lastName}"
                    )
                    transaction.replace(
                        R.id.frame_layout,
                        ReportFragment.newInstance(bundle),
                        ReportFragment.tagName
                    )
                    transaction.addToBackStack(ReportFragment.tagName)
                    transaction.commit()
                }
                },
                {_, _ -> })
            val astrologer_list =
                myActivity.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.astrolgerList)
            astrologer_list.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            astrologer_list.adapter = homeAdapter
            if (astrologer_list.layoutManager == null) {
                astrologer_list.addItemDecoration(MarginItemDecorator(1))
            }
            astrologer_list.hasFixedSize()
        })
        return inflater.inflate(layoutResId, container, false)
    }

    fun checkUserIfLoggedIn(): Boolean {
        val username = SharedPreferenceUtils.readString(
            ApplicationConstant.USERNAME, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        val password = SharedPreferenceUtils.readString(
            ApplicationConstant.PASSWORD, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        return !(username.isNullOrEmpty())
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        try {
            val originalArray = homeViewModel!!.arrayParentModelData
            var filteredArray = ArrayList<AstrologerListModel>()
            originalArray.forEach {
                if (it.firstName!!.contains(
                        query.toString(),
                        true
                    ) || it.lastName!!.contains(query.toString(), true) ||
                    it.expertise!!.contains(query.toString(), true)
                ) {
                    filteredArray!!.add(it)
                }
                if (filteredArray!!.isNotEmpty()) {
                    homeAdapter!!.filteredList(filteredArray)
                }
            }

        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onQueryTextChange")
        }

        return true
    }

    fun handleVoiceSearch(query: String?) {
        val searchView: SearchView = myActivity.findViewById(R.id.searchView)
        searchView.setQuery(query, true)
        searchView.clearFocus()
        KeypadUtils.hideSoftKeypad(myActivity)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(mApplication) as T
        }
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        internal val tagName: String
            get() = HomeFragment::class.java.name

        internal var typeButtonPressed: String = ""

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?, buttonType: String?): HomeFragment {
            val fragment = HomeFragment()
            fragment.arguments = bundle
            typeButtonPressed = buttonType.toString()
            return fragment
        }
    }
}

