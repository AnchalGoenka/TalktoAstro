package tta.destinigo.talktoastro.adapter

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.UserWalletListBinding
import tta.destinigo.talktoastro.model.WalletHistoryData
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils


/**

 * Created by Vivek Singh on 2019-08-05.

 */
class UserWalletHistoryAdapter(private val arrayList: ArrayList<WalletHistoryData>): androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter.customView>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val walletListBinding: UserWalletListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.wallet_history_child_items, parent, false)
        return customView(walletListBinding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter.customView, position: Int) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.UserWalletHistoryAdapter.customView
            val walletListModel = arrayList!![position]
            h1.bind(walletListModel)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(
        val walletListBinding: UserWalletListBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(walletListBinding.root) {

        fun bind(walletListModel: WalletHistoryData) {
            walletListBinding.walletTransactionList = walletListModel
            walletListBinding.executePendingBindings()
            walletListBinding.walletTxvTalkValueVal.text = walletListModel.talkValue.replace(".00","")
            walletListBinding.walletTxvPaidAmtVal.text = walletListModel.paidAmount.replace(".00","")
            val phoneCode = SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if (phoneCode == "91") {
                walletListBinding.walletTxvTalkValueVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
                walletListBinding.walletTxvPaidAmtVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
            }
            else {
                walletListBinding.walletTxvTalkValueVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
                walletListBinding.walletTxvPaidAmtVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
            }
        }
    }
}