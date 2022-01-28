package tta.destinigo.talktoastro.chat

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.LoginActivity
import tta.destinigo.talktoastro.activity.RechargeActivity
import tta.destinigo.talktoastro.adapter.HomeAdapter
import tta.destinigo.talktoastro.agora.chat.AGApplication
import tta.destinigo.talktoastro.agora.chat.AGApplication.Companion.the
import tta.destinigo.talktoastro.agora.chat.AgoraMessageActivity
import tta.destinigo.talktoastro.agora.chat.ChatManager
import tta.destinigo.talktoastro.model.AstrologerListModel
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.view.ProfileFragment
import tta.destinigo.talktoastro.viewmodel.HomeViewModel

/**
 * A simple [Fragment] subclass.
 */
class ChatAstrologerListFragment : BaseFragment(), SearchView.OnQueryTextListener,
    android.widget.SearchView.OnQueryTextListener {
     var mrtmclient: RtmClient? = null
    private var homeViewModel: HomeViewModel? = null
    private var homeAdapter: tta.destinigo.talktoastro.adapter.HomeAdapter? = null
    private var changeStatus: Boolean = false
    var isfromchatform :Boolean = false
    private var adapterPosition: Int = 0
    var token :String ?= null

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
            if (!SharedPreferenceUtils.readBoolean(
                    ApplicationConstant.RELOADHOMEFRAGMENT,
                    false,
                    SharedPreferenceUtils.getSharedPref(
                        ApplicationUtil.getContext()
                    )
                )
            ) {
                homeViewModel!!.getChatList()
            }
            SharedPreferenceUtils.put(
                ApplicationConstant.RELOADHOMEFRAGMENT, false,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        updateUserWallet()
        pull_to_refresh.setOnRefreshListener {
            refreshList()
            pull_to_refresh.isRefreshing = false
        }
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        searchView.setSearchableInfo(searchManager!!.getSearchableInfo(activity.componentName))
        searchView.setOnQueryTextListener(this)
    }

    fun setToolBar(){
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Astrologers",
            false,
            R.drawable.ic_arrow_back_24dp
        )
        toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Chat",
            false,
            R.drawable.ic_arrow_back_24dp
        )
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showProgressBar("Loading")
        homeViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(HomeViewModel::class.java)
        homeViewModel!!.rtmToken()
        if(homeAdapter == null){
            homeViewModel!!.getChatList()
        }else{
            if(isfromchatform){
                isfromchatform=false
            }else{
            refreshList()}
        }
       // homeViewModel!!.getChatList()
        homeViewModel?.rtmToken?.observe(viewLifecycleOwner, Observer {
         // BaseApplication.instance.showToast("$it")
            token =it
            SharedPreferenceUtils.put(ApplicationConstant.AgoraToken,"$token",SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        })
        homeViewModel?.arrayListMutableLiveData?.observe(viewLifecycleOwner, Observer {
            changeStatus = true
            hideProgressBar()
            homeAdapter = HomeAdapter(
                it,
                ApplicationConstant.CHAT_STRING,
                { homeModel, position ->
                    val bundle = Bundle()
                    bundle.putParcelable("profile", homeModel)
                    bundle.putString("extra", "Chat")
                    val profileFragment = ProfileFragment.newInstance(bundle)
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_layout, profileFragment, ProfileFragment.tagName)
                    transaction.addToBackStack(ProfileFragment.tagName)
                    transaction.commit()
                },
                { homeModel, position ->
//                    if (homeModel.phoneStatus != "online") {
//                        return@HomeAdapter
//                    }
                    if (!ApplicationUtil.checkLogin()) {
                        val intent = Intent(ApplicationUtil.getContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else {

                        val balance = SharedPreferenceUtils.readString(
                            ApplicationConstant.BALANCE, "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        val phoneCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))

                        //if balance less then 50 show error popup

                        if ( (phoneCode == "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_RUPEE) ||
                            (phoneCode != "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_DOLLAR) ) {


                            try {
                                if (phoneCode == "91") {
                                    ApplicationUtil.showDialog(
                                        myActivity,
                                        "Minimum balance should not be less than Rs 50. Please recharge."
                                    )
                                    return@HomeAdapter
                                }else{
                                    ApplicationUtil.showDialog(
                                        myActivity,
                                        "Minimum balance should not be less than $7. Please recharge."
                                    )
                                    return@HomeAdapter
                                }
                            } catch (e: Exception) {

                            }
                        }


                        /*if (ApplicationUtil.seconds != 0L) {
                            ApplicationUtil.showDialog(
                                myActivity,
                                "Please wait for astrologer to pickup/reject the earlier request"
                            )
                            return@HomeAdapter
                        }*/
                        changeStatus = true
                        isfromchatform = true
                        homeAdapter!!.notifyItemChanged(position)
                        var bundle = Bundle()
                        bundle.putString("astroID", homeModel.id)
                        bundle.putString(
                            "astroName",
                            homeModel.firstName + " " + homeModel.lastName
                        )
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            ChatFormFragment.newInstance(bundle),
                            ChatFormFragment.tagName
                        )
                        transaction.addToBackStack(ChatFormFragment.tagName)
                        transaction.commit()
                    }
                }, { _, _ -> })
            val astrologer_list =
                myActivity.findViewById<RecyclerView>(R.id.astrolgerList)
            astrologer_list.layoutManager =
                LinearLayoutManagerWrapper(ApplicationUtil.getContext(), LinearLayoutManager.VERTICAL, false)
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

    fun updateUserWallet () {
        val userID = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val json = JSONObject()
        json.put("userID",userID)
        if (homeViewModel != null){
            homeViewModel?.getWalletTransactions(json)
            homeViewModel?.balanceUpdateResp?.observe(viewLifecycleOwner, Observer {
                SharedPreferenceUtils.put(
                    ApplicationConstant.BALANCE, it,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                setToolBar()
            })
        }
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
            get() = ChatAstrologerListFragment::class.java.name

        internal var typeButtonPressed: String = ""


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?, buttonType: String?): ChatAstrologerListFragment {
            val fragment = ChatAstrologerListFragment()
            fragment.arguments = bundle
            typeButtonPressed = buttonType.toString()
            return fragment
        }
    }

}
