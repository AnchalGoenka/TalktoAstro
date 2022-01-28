package tta.destinigo.talktoastro.adapter

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.ReportHistoryListBinding
import tta.destinigo.talktoastro.model.ReportHistoryModel


/**

 * Created by Vivek Singh on 2019-08-29.

 */
class ReportHistoryAdapter(private val arrayList: ArrayList<ReportHistoryModel>,
                           private val itemClick: (ReportHistoryModel, Int) -> Unit): androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.ReportHistoryAdapter.customView>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.ReportHistoryAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val reportListBinding: ReportHistoryListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.report_history_child_list, parent, false)
        return customView(reportListBinding, itemClick)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.ReportHistoryAdapter.customView, position: Int) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.ReportHistoryAdapter.customView
            val reportListModel = arrayList!![position]
            h1.bind(reportListModel)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(
        val reportListBinding: ReportHistoryListBinding, val itemClick: (ReportHistoryModel, Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(reportListBinding.root) {

        fun bind(reportListModel: ReportHistoryModel) {
            reportListBinding.reportHistoryList = reportListModel
            reportListBinding.executePendingBindings()
            itemView.setOnClickListener{itemClick(reportListModel, adapterPosition)}
        }
    }
}