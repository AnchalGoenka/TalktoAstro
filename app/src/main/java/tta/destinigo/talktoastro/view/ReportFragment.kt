package tta.destinigo.talktoastro.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.main_toolbar.view.*

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ReportList
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.report_layout_child.*
import org.json.JSONObject
import tta.destinigo.talktoastro.adapter.ReportListAdapter

class ReportFragment : tta.destinigo.talktoastro.BaseFragment() {

    private var reportViewModel: ReportViewModel? = null
    private var reportAdapter: tta.destinigo.talktoastro.adapter.ReportListAdapter? = null
    private var astroID: String = ""
    private var astroName: String = ""

    override val layoutResId: Int
        get() = R.layout.fragment_report

    override fun getToolbarId(): Int {
        return 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        if (checkBoxReport != null) {
            checkBoxReport.isChecked = false
            SharedPreferenceUtils.put(
                ApplicationConstant.REPORTCHECKED,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        }
        if (reportViewModel != null && reportAdapter != null) {
            reportViewModel?.arrayListMutableReportData?.value?.clear()
            reportAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showProgressBar("Please wait...", true)
        reportViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ReportViewModel::class.java)
        reportViewModel?.arrayListMutableReportData?.observe(this, Observer {
            hideProgressBar()
            reportAdapter= ReportListAdapter(it!!){reportList,position->
                var bundle = Bundle()
                val phoneCode = SharedPreferenceUtils.readString(
                    ApplicationConstant.PHONECODE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                astroID = arguments?.getString("astroID") as String
                astroName = arguments?.getString("ThankYouName") as String
                bundle.putString("id", reportList.serviceId)
                bundle.putString("astroID", astroID)
                bundle.putString("seviceName",reportList.name)
                bundle.putString("ThankYouName", astroName)
                if (phoneCode == "91") {
                    val intVal = reportList.reportInrPrice.toInt()
                    bundle.putString("price", intVal.toString())

                } else {
                    val intVal = reportList.reportUsdPrice.toInt()
                    bundle.putString("price", intVal.toString())
                }
                var transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.frame_layout,
                    MyReportFragment.newInstance(bundle),
                    MyReportFragment.tagName
                )
                transaction.addToBackStack(MyReportFragment.tagName)
                transaction.commit()
            }
          /*  reportAdapter = ReportListAdapter(it!!) { reportList, _ ->
                    try {
                        reportList.name?.let { it1 ->
                            SharedPreferenceUtils.put(
                                ApplicationConstant.REPORTCHECKED,
                                it1,
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                            )
                        }

                        var bundle = Bundle()
                        val phoneCode = SharedPreferenceUtils.readString(
                            ApplicationConstant.PHONECODE, "",
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )
                        bundle.putString("id", reportList.serviceId)
                        bundle.putString("astroID", astroID)
                        bundle.putString("seviceName",reportList.name)
                        bundle.putString("ThankYouName", astroName)
                        if (phoneCode == "91") {
                            val intVal = reportList.reportInrPrice.toInt()
                            bundle.putString("price", intVal.toString())

                        } else {
                            val intVal = reportList.reportUsdPrice.toInt()
                            bundle.putString("price", intVal.toString())
                        }
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            MyReportFragment.newInstance(bundle),
                            MyReportFragment.tagName
                        )
                        transaction.addToBackStack(MyReportFragment.tagName)
                        transaction.commit()
                    } catch (e: Exception) {
                        LogUtils.d("error")
                    }
                }*/
            val report_list_recycler: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.report_list_recycler)
            report_list_recycler.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            report_list_recycler.adapter = reportAdapter
            if (report_list_recycler.layoutManager == null){
                report_list_recycler.addItemDecoration(MarginItemDecorator(1))
            }
            report_list_recycler.hasFixedSize()
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = "Select Report Type"
        toolbar.ib_notification.visibility=View.GONE
        toolbar.layout_btn_recharge.visibility=View.GONE
        astroID = arguments?.getString("astroID") as String
        astroName = arguments?.getString("ThankYouName") as String
        var json = JSONObject()
        json.put("astroID", astroID)
        reportViewModel?.getReports(json)
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(mApplication) as T
        }
    }

    companion object {

        internal val tagName: String
            get() = ReportFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ReportFragment {
            val fragment = ReportFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
