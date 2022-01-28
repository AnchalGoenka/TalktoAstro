package tta.destinigo.talktoastro.adapter

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.CallHistoryListBinding
import tta.destinigo.talktoastro.model.CallHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils


/**

 * Created by Vivek Singh on 2019-08-29.

 */
class CallHistoryAdapter(private val arrayList: ArrayList<CallHistoryModel>): androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.CallHistoryAdapter.customView>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.CallHistoryAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val callListBinding: CallHistoryListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.call_history_child_list, parent, false)
        return customView(callListBinding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.CallHistoryAdapter.customView, position: Int) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.CallHistoryAdapter.customView
            val callListModel = arrayList!![position]
            h1.bind(callListModel)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(
        val callListBinding: CallHistoryListBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(callListBinding.root) {

        fun bind(callListModel: CallHistoryModel) {
            callListBinding.callHistoryList = callListModel
            callListBinding.executePendingBindings()
            val phoneCode = SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if (callListModel.conversationDuration.isNullOrBlank()){
                callListBinding.callHistoryConversatioDurVal.text = "0"
                callListBinding.callHistoryPriceVal.text = "NA"
            }
            if (callListModel.status == "Success") {
                callListBinding.constraintLayout.setBackgroundResource(R.color.colorPrimaryLight_1)
                callListBinding.callHistoryConversatioDur.visibility = View.VISIBLE
                callListBinding.callHistoryPrice.visibility = View.VISIBLE
                callListBinding.callHistoryConversatioDurVal.visibility = View.VISIBLE
                callListBinding.callHistoryPriceVal.visibility = View.VISIBLE
                callListBinding.view4.visibility = View.VISIBLE
                callListBinding.viewPaymentTime.visibility = View.VISIBLE
                if (phoneCode == "91") {
                    callListBinding.callHistoryPriceVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
                }
                else {
                    callListBinding.callHistoryPriceVal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
                }
                callListBinding.callHistoryConversatioDurVal.text = "${callListModel.conversationDuration} mins"
            } else {
                callListBinding.constraintLayout.setBackgroundResource(R.color.gray_light)
                callListBinding.callHistoryConversatioDur.visibility = View.GONE
                callListBinding.callHistoryPrice.visibility = View.GONE
                callListBinding.view4.visibility = View.GONE
                callListBinding.viewPaymentTime.visibility = View.GONE
                callListBinding.callHistoryConversatioDurVal.visibility = View.GONE
                callListBinding.callHistoryPriceVal.visibility = View.GONE
            }
        }
    }
}