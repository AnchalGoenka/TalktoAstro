package tta.destinigo.talktoastro.view

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.main_toolbar.view.*

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ReportHistoryModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.ReportHistoryViewModel


class ReportHistoryFragment : tta.destinigo.talktoastro.BaseFragment() {

    var reportHistoryViewModel: ReportHistoryViewModel? = null
    var reportHistoryAdapter: tta.destinigo.talktoastro.adapter.ReportHistoryAdapter? = null
    override fun getToolbarId(): Int {
        return 0
    }

    override val layoutResId: Int
        get() = R.layout.fragment_report_history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reportHistoryViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ReportHistoryViewModel::class.java)
        reportHistoryViewModel?.arrayMutableReportHistory?.observe(this, Observer {
            reportHistoryAdapter =
                tta.destinigo.talktoastro.adapter.ReportHistoryAdapter(it!!) { reportHistoryModel: ReportHistoryModel, i: Int ->
                    var bundle = Bundle()
                    bundle.putParcelable("reportHistoryModel", reportHistoryModel)
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.frame_layout,
                        ReportDetailFragment.newInstance(bundle),
                        ReportDetailFragment.tagName
                    )
                    transaction.addToBackStack(ReportDetailFragment.tagName)
                    transaction.commit()
                }
            val report_history_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.report_history_list)
            report_history_list.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            report_history_list.adapter = reportHistoryAdapter
            if (report_history_list.layoutManager == null){
                report_history_list.addItemDecoration(MarginItemDecorator(1))
            }
            report_history_list.hasFixedSize()
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "Report History"
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        if (toolbar != null) {
            toolbar!!.btn_recharge.visibility = View.VISIBLE
            toolbar!!.setNavigationOnClickListener {
                val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
                drawer.openDrawer(Gravity.START)
            }
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ReportHistoryViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = ReportHistoryFragment::class.java.name

        fun newInstance(bundle: Bundle?): ReportHistoryFragment {
            val fragment = ReportHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
