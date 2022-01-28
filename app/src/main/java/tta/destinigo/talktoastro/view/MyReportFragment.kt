package tta.destinigo.talktoastro.view

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.ProcessReportViewModel
import kotlinx.android.synthetic.main.fragment_my_report.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.activity.PayActivityFragment
import tta.destinigo.talktoastro.activity.RechargeActivity

class MyReportFragment : tta.destinigo.talktoastro.BaseFragment() {

    var processReportViewModel: ProcessReportViewModel? = null



    var  isclicked:Boolean=false
    override val layoutResId: Int
        get() = R.layout.fragment_my_report

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        if (textView_wallet_balance_val != null) {
            textView_wallet_balance_val.text = SharedPreferenceUtils.readString(
                ApplicationConstant.BALANCE,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = "Report Details"
        toolbar.ib_notification.visibility=View.GONE
        toolbar.layout_btn_recharge.visibility=View.GONE


        processReportViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ProcessReportViewModel::class.java)
        @Suppress("UNCHECKED_CAST")
        val reportJson = arguments?.getSerializable("report") as HashMap<String, String>?
        val price = reportJson?.get("price")
        val astroName = arguments?.getString("ThankYouName") as String?
        val servicename=arguments?.getString("seviceName") as String?
        val reportprice=arguments?.getString("price") as String?
        if(!servicename.isNullOrEmpty()){
            constraintLayout_myreport.visibility=View.GONE
            ll_myreport.visibility=View.GONE

            textView_service_type.text=servicename
            textView_total_amount_val.text = reportprice
            textView_wallet_balance_val.text = SharedPreferenceUtils.readString(ApplicationConstant.BALANCE,
                "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if(reportprice!!.toInt() >= SharedPreferenceUtils.readString( ApplicationConstant.BALANCE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))!!.toInt()){
                tv_message.visibility=View.VISIBLE
            }
                 isclicked=true
        }else{

            reportJson?.let { updateValuesFromReportJson(it) }
        }



        button_pay.setOnClickListener {
    if(isclicked==true){


            var bundle = Bundle()
            val phoneCode = SharedPreferenceUtils.readString(
                ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
   // astroID = arguments?.getString("astroID") as String
   /*var astroName1 = arguments?.getString("ThankYouName") as String
    bundle.putString("id", arguments?.getString("id") as String)
    bundle.putString("astroID", arguments?.getString("astroID") as String)
    bundle.putString("seviceName",arguments?.getString("seviceName") as String)
    bundle.putString("ThankYouName", astroName1)
    if (phoneCode == "91") {
        val pprice= arguments?.getString("price") as String
        val intVal = pprice.toInt()
        bundle.putString("price", intVal.toString())

    } else {
        val pprice= arguments?.getString("price") as String
        val intVal = pprice.toInt()
        bundle.putString("price", intVal.toString())
    }
    var transaction: FragmentTransaction =
        myActivity.supportFragmentManager.beginTransaction()
    transaction.replace(
        R.id.frame_layout,
        ProcessReportFragment.newInstance(bundle),
        ProcessReportFragment.tagName
    )
    transaction.addToBackStack(ProcessReportFragment.tagName)
    transaction.commit()*/

    if (reportprice!!.toInt() >= SharedPreferenceUtils.readString( ApplicationConstant.BALANCE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))!!.toInt()) {
        val intent = Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
        //intent.putExtras(bundle);
        startActivity(intent)
    }else{
        
        var astroName1 = arguments?.getString("ThankYouName") as String
        bundle.putString("id", arguments?.getString("id") as String)
        bundle.putString("astroID", arguments?.getString("astroID") as String)
        bundle.putString("seviceName",arguments?.getString("seviceName") as String)
        bundle.putString("ThankYouName", astroName1)
        if (phoneCode == "91") {
            val pprice= arguments?.getString("price") as String
            val intVal = pprice.toInt()
            bundle.putString("price", intVal.toString())

        } else {
            val pprice= arguments?.getString("price") as String
            val intVal = pprice.toInt()
            bundle.putString("price", intVal.toString())
        }
        var transaction: FragmentTransaction =
            myActivity.supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout,
            ProcessReportFragment.newInstance(bundle),
            ProcessReportFragment.tagName
        )
        transaction.addToBackStack(ProcessReportFragment.tagName)
        transaction.commit()
    }

    isclicked=false
}else {

           /*  if (price!!.toInt() <= SharedPreferenceUtils.readString( ApplicationConstant.BALANCE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))!!.toInt()) {
                button_pay.isEnabled = false
                reportJson!!.remove("price")
                processReportViewModel?.getReports(reportJson!!)
                processReportViewModel?.respMutableData?.observe(this, androidx.lifecycle.Observer {
                    button_pay.isEnabled = true
                    Toast.makeText(
                        ApplicationUtil.getContext(),"Report submitted succesfully.",
                        Toast.LENGTH_SHORT).show()
                    val bundle = Bundle()
                    bundle.putString("ThankYou",ApplicationConstant.REPORT_STRING)
                    bundle.putString("ThankYouName","${astroName}")
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.frame_layout,
                        ThankYouFragment.newInstance(bundle),
                        ThankYouFragment.tagName
                    )
                    transaction.addToBackStack(ThankYouFragment.tagName)
                    transaction.commit()
                })
                processReportViewModel?.respMutableError?.observe(this, Observer {
                    button_pay.isEnabled = true
                    Toast.makeText(
                        ApplicationUtil.getContext(),it,
                        Toast.LENGTH_SHORT).show()
                })
            } else {
                SharedPreferenceUtils.put(ApplicationConstant.ISCOMINGFROMREPORT, true, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                val bundle = Bundle()
                bundle.putSerializable("report", reportJson)
                bundle.putString("ThankYouName","${astroName}")
               *//* var transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.frame_layout,
                    PayActivityFragment.newInstance(bundle),
                    PayActivityFragment.tagName
                )
                transaction.addToBackStack(PayActivityFragment.tagName)
                transaction.commit()*//*
                val intent = Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
                intent.putExtras(bundle);

                startActivity(intent)
            }*/
    button_pay.isEnabled = false
    reportJson!!.remove("price")
    processReportViewModel?.getReports(reportJson!!)
    processReportViewModel?.respMutableData?.observe(this, androidx.lifecycle.Observer {
        button_pay.isEnabled = true
        Toast.makeText(
            ApplicationUtil.getContext(),"Report submitted succesfully.",
            Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putString("ThankYou",ApplicationConstant.REPORT_STRING)
        bundle.putString("ThankYouName","${astroName}")
        var transaction: FragmentTransaction =
            myActivity.supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.frame_layout,
            ThankYouFragment.newInstance(bundle),
            ThankYouFragment.tagName
        )
        transaction.addToBackStack(ThankYouFragment.tagName)
        transaction.commit()
    })
    processReportViewModel?.respMutableError?.observe(this, Observer {
        button_pay.isEnabled = true
        Toast.makeText(
            ApplicationUtil.getContext(),it,
            Toast.LENGTH_SHORT).show()
    })
        }


        }
    }

    fun updateValuesFromReportJson(reportData: HashMap<String, String>) {
        textView_name.text = reportData.get("name")
        textView_location.text = reportData.get("birthPlace")
        textView_date.text = reportData.get("birthDate")
        textView_time.text = reportData.get("birthTime")
        textView_question_val.movementMethod= ScrollingMovementMethod()
        textView_question_val.text = reportData.get("question")
        textView_total_amount_val.text = reportData.get("price")
        textView_service_type.text=reportData.get("serviceName")
        button_pay.text="Submitt"
        textView_wallet_balance_val.text = SharedPreferenceUtils.readString(ApplicationConstant.BALANCE,
            "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val phoneCode = SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        if (phoneCode == "91") {
            textView_total_amount_val.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black, 0, 0, 0)
            textView_wallet_balance_val.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black, 0, 0, 0)
        } else {
            textView_total_amount_val.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar, 0, 0, 0)
            textView_wallet_balance_val.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar, 0, 0, 0)
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProcessReportViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = MyReportFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): MyReportFragment {
            val fragment = MyReportFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
