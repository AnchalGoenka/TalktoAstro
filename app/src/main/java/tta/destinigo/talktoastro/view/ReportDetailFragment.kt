package tta.destinigo.talktoastro.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ReportHistoryModel
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.viewmodel.ReportDetailViewModel
import kotlinx.android.synthetic.main.fragment_report_detail.*
import org.json.JSONObject

class ReportDetailFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var reportDetailViewModel: ReportDetailViewModel? = null
    private var reportDetailAdapter: tta.destinigo.talktoastro.adapter.ReportDetailAdapter? = null
    override val layoutResId: Int
        get() = R.layout.fragment_report_detail

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
        reportDetailViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ReportDetailViewModel::class.java)
        val reportData = arguments?.getParcelable<ReportHistoryModel>("reportHistoryModel") as ReportHistoryModel
        var json = JSONObject()
        json.put("reportID", reportData.id)
        reportDetailViewModel?.getReportDetails(json)
        reportDetailViewModel?.arrayMutableReportDetail?.observe(this, Observer {
            val recycler_report_detail: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.recycler_report_detail)
            reportDetailAdapter = tta.destinigo.talktoastro.adapter.ReportDetailAdapter(it!!)
            recycler_report_detail.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            recycler_report_detail.adapter = reportDetailAdapter
            if (recycler_report_detail.layoutManager == null){
                recycler_report_detail.addItemDecoration(MarginItemDecorator(1))
            }
            recycler_report_detail.hasFixedSize()
        })
        reportDetailViewModel?.reportBirthDetail?.observe(this, Observer {
            textView_name.text = it.name
            textView_location.text = it.place
            textView_date.text = it.date
            textView_time.text = it.time
            textView_requirement.text = it.requirement.replace("\n"," ")
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    fun setUptoolBar(title: String) {
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = title
        toolbar.setNavigationOnClickListener {
            tta.destinigo.talktoastro.BaseFragment.popBackStack(myActivity)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reportHistoryModel = arguments?.getParcelable<ReportHistoryModel>("reportHistoryModel") as ReportHistoryModel
        val serviceID = reportHistoryModel.serviceId
        setUptoolBar(serviceID!!)
        if (reportHistoryModel.status == "completed"){
            textInputLayout_report_comments.visibility=View.GONE
            button_submit_comment_report.visibility=View.GONE
            textInputLayout_report_comments.isEnabled = false
            button_submit_comment_report.isEnabled = false
        } else {
            textInputLayout_report_comments.isEnabled = true
            button_submit_comment_report.isEnabled = true
        }

        button_submit_comment_report.setOnClickListener {
            val userId = SharedPreferenceUtils.readString(
                ApplicationConstant.USERID, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())).toString()
            if (textInputLayout_report_comments.text.isNotEmpty()) {
                var json = JSONObject()
                json.put("reportID", reportHistoryModel.id)
                json.put("userID", userId)
                json.put("reportComment", textInputLayout_report_comments.text)
                reportDetailViewModel?.submitReportComments(json)
                reportDetailViewModel?.reportCommentResp?.observe(this, Observer {
                    textInputLayout_report_comments.text.clear()
                    reportDetailViewModel?.getReportDetails(json)
                })
            }
        }

    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReportDetailViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = ReportDetailFragment::class.java.name

        fun newInstance(bundle: Bundle?): ReportDetailFragment {
            val fragment = ReportDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
