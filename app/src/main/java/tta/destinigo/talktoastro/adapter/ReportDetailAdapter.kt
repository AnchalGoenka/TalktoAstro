package tta.destinigo.talktoastro.adapter

import androidx.databinding.DataBindingUtil
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.ReportDetailListBinding
import tta.destinigo.talktoastro.model.ReportDetailModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils


/**

 * Created by Vivek Singh on 2019-09-19.

 */
class ReportDetailAdapter(private val arrayList: ArrayList<ReportDetailModel>):
    androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.ReportDetailAdapter.customView>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.ReportDetailAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val reportListBinding: ReportDetailListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.report_detail_child_list, parent, false)
        return customView(reportListBinding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.ReportDetailAdapter.customView, position: Int) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.ReportDetailAdapter.customView
            val reportListModel = arrayList!![position]
            h1.bind(reportListModel)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(
        val reportListBinding: ReportDetailListBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(reportListBinding.root) {

        fun bind(reportListModel: ReportDetailModel) {
            reportListBinding.reportDetailList = reportListModel
            reportListBinding.executePendingBindings()
            tta.destinigo.talktoastro.util.LogUtils.d("reportDetail: ${reportListModel}")
            reportListBinding.txvReportDetailMsg.text = Html.fromHtml(reportListModel.comment)
            if (reportListModel.userId == SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))) {
                reportListBinding.txvReportDetailName.text = "You:"
            } else {
                reportListBinding.txvReportDetailName.text = "Astrologer:"
            }

            reportListBinding.txvReportDetailUpdatedAt.text = reportListModel.updatedAt
        }
    }
}