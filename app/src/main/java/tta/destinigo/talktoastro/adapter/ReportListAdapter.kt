package tta.destinigo.talktoastro.adapter

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.ReportListBinding
import tta.destinigo.talktoastro.model.ReportList
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import kotlin.collections.ArrayList


/**

 * Created by Vivek Singh on 2019-07-18.

 */
class ReportListAdapter(
    private var arrayList: ArrayList<ReportList>?,
    private val itemClick: (ReportList, Int) -> Unit
): androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.ReportListAdapter.customView>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.ReportListAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val reportListBinding: ReportListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.report_layout_child, parent, false)
        return customView(reportListBinding, itemClick)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.ReportListAdapter.customView, position: Int) {
        try {
//            val checkBoxCheckedValue = SharedPreferenceUtils.readString(ApplicationConstant.REPORTCHECKED, "",
//                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
//            var isChecked = false
//            if (checkBoxCheckedValue == getRow["name"]){
//                isChecked = true
//            }

//            val reportListModel = ReportList(getRow["id"]!! as String, getRow["name"]!! as String, getRow["reportInrPrice"]!! as Double,
//                getRow["reportInrPriceWithTax"]!! as Double, getRow["reportUsdPrice"]!! as Double, getRow["reportUsdPriceWithTax"]!! as Double,
//                getRow["taxName"]!!.toString(), getRow["taxPercent"]!!.toString(), false)
//            holder.bind(reportListModel, position)

            val h1 = holder as tta.destinigo.talktoastro.adapter.ReportListAdapter.customView
            val reportListModel = arrayList!![position]
            h1.bind(reportListModel, position)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(val reportListBinding: ReportListBinding,
                           val itemClick: (ReportList, Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.ViewHolder(reportListBinding.root) {

        fun bind(reportListModel: ReportList, position: Int) {
            reportListBinding.reportList = reportListModel
            reportListBinding.executePendingBindings()
            tta.destinigo.talktoastro.util.LogUtils.d("reportListModel: $reportListModel")
            val phoneCode = SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if (phoneCode == "91"){
                val intVal = reportListModel.reportInrPrice.toInt()
                reportListBinding.txvReportPrice.text = intVal.toString()
                reportListBinding.txvReportPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black, 0, 0, 0)
            }else{
                val intVal = reportListModel.reportUsdPrice.toInt()
                reportListBinding.txvReportPrice.text = intVal.toString()
                reportListBinding.txvReportPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar, 0, 0, 0)
            }
            reportListBinding.checkBoxReport.isChecked = reportListModel.checkBox


            reportListBinding.checkBoxReport.setOnClickListener { itemClick(reportListModel, adapterPosition) }


        }

    }

}