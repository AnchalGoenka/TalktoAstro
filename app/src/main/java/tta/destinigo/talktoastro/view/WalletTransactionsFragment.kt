package tta.destinigo.talktoastro.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.WalletTransactionViewModel
import org.json.JSONObject


class WalletTransactionsFragment : tta.destinigo.talktoastro.BaseFragment() {
    var walletViewModel: WalletTransactionViewModel? = null
    var walletAdapter: tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter? = null
    override fun getToolbarId(): Int {
        return 0
    }

    override val layoutResId: Int
        get() = R.layout.fragment_wallet_transactions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        walletViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(WalletTransactionViewModel::class.java)
        val userId = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        var json = JSONObject()
        json.put("userID", userId)
        walletViewModel?.getWalletTransactions(json)
        showProgressBar("Loading...")
        walletViewModel?.arrayMutableWalletViewModel?.observe(this, Observer {
            hideProgressBar()
            walletAdapter =
                tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter(it!!.walletHistoy)
            SharedPreferenceUtils.put(ApplicationConstant.BALANCE, it!!.walletBalance,
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            val wallet_history_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.wallet_history_list)
            wallet_history_list.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            wallet_history_list.adapter = walletAdapter
            if (wallet_history_list.layoutManager == null){
                wallet_history_list.addItemDecoration(MarginItemDecorator(1))
            }
            wallet_history_list.hasFixedSize()
        })
        walletViewModel?.errorMutableViewModel?.observe(this, Observer {
            hideProgressBar()
            Toast.makeText(
                ApplicationUtil.getContext(),"No history available.",
                Toast.LENGTH_SHORT).show()
        })
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ApplicationUtil.setToolbarTitleAndRechargeButton("Transaction history", false, R.drawable.ic_hamburger)
    }


    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WalletTransactionViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
        get() = WalletTransactionsFragment::class.java.name

        fun newInstance(bundle: Bundle?): WalletTransactionsFragment {
            val fragment = WalletTransactionsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
