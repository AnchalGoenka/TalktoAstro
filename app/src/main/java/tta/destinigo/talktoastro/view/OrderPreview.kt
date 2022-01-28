package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_order_preview.*
import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.RechargeActivity
import tta.destinigo.talktoastro.model.RazorPayOrderIdModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.OrderPreviewViewModel


class OrderPreview : tta.destinigo.talktoastro.BaseFragment() {

    lateinit var orderViewModel: OrderPreviewViewModel

    override val layoutResId: Int
        get() = R.layout.fragment_order_preview

    override fun getToolbarId(): Int {
        return 0
    }
   private var back:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    /*  override fun onActivityCreated(savedInstanceState: Bundle?) {
         super.onActivityCreated(savedInstanceState)
         //your code which you want to refresh
         (getActivity() as RechargeActivity?)?.refreshMyData2()
     }
      override fun onStart() {
           super.onStart()
           //update your fragment
           (getActivity() as RechargeActivity?)?.refreshMyData2()
       }*/
    override fun onResume() {
        super.onResume()
        if(back==true){
        (getActivity() as RechargeActivity?)?.refreshMyData2()}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ApplicationConstant.SELECTPACKAGE=1
        orderViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(OrderPreviewViewModel::class.java)
        return inflater.inflate(R.layout.fragment_order_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* val toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Payment Details", false, R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener {
            tta.destinigo.talktoastro.BaseFragment.popBackStack(myActivity)
        }*/

        val phoneCode = SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        if (phoneCode == "91") {
            txv_amount_to_be_paid_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
            txv_amount_value_order_preview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
            txv_talktime_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
            txv_service_charge_value_order_preview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black_small, 0, 0, 0)
        }
        else {
            txv_amount_to_be_paid_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
            txv_amount_value_order_preview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
            txv_talktime_value.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
            txv_service_charge_value_order_preview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_black_small, 0, 0, 0)
        }

        val razorPayModel = arguments?.getParcelable<RazorPayOrderIdModel>("razorPayModel") as RazorPayOrderIdModel
        val reportJson = arguments?.getSerializable("report")

        txv_recharge_value.text = razorPayModel.packageName
        txv_amount_value_order_preview.text = razorPayModel.talkValue
        txv_service_charge_value_order_preview.text = razorPayModel.taxPercent
        txv_amount_to_be_paid_value.text = razorPayModel.billingAmount
        txv_talktime_value.text = razorPayModel.talkValue

        txv_service_charge_order_preview.text = String.format(resources.getString(R.string.service_charge),ApplicationConstant.SERVICECHARGE)

        val code = arguments?.getString("code")
        if(!code.isNullOrEmpty()) {
            txv_coupan_code.text = Editable.Factory.getInstance().newEditable(code)
        }
        btn_proceed_for_payment.setOnClickListener {

            var bundle = Bundle()
            bundle.putParcelable("razorPayModel", razorPayModel)
            if (reportJson != null){
                bundle.putSerializable("report", reportJson)
            }
            if (arguments?.getString("ThankYouName") != null) {
                val astroName = arguments?.getString("ThankYouName") as String
                bundle.putString("ThankYouName","${astroName}")
            }
            (getActivity() as RechargeActivity?)?.refreshMyData()
            var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_Recharge, PayOnline.newInstance(bundle), PayOnline.tagName)
            transaction.addToBackStack(PayOnline.tagName)
            transaction.commit()
            back=true




        }



        btn_apply_coupan_code.setOnClickListener{
            var jsonObj = JSONObject()
            jsonObj.put("couponCode", txv_coupan_code.text)
            jsonObj.put("ttaOrderID", razorPayModel.ttaOrderId)
            showProgressBar("Please wait...")
            orderViewModel.applyCoupanCode(jsonObj)
            orderViewModel.coupanDidAvailed.observe(viewLifecycleOwner, Observer {
                btn_apply_coupan_code.isEnabled = false
                btn_apply_coupan_code?.setTextColor(ContextCompat.getColor(myActivity, R.color.white))
                btn_apply_coupan_code?.setBackgroundColor(ContextCompat.getColor(myActivity, R.color.gray))
                txv_talktime_value.text = it.newTalkValue.toString()
                hideProgressBar()
                // Initialize a new instance of
                val builder = AlertDialog.Builder(myActivity)
                // Set the alert dialog title
                builder.setTitle("Coupon Applied")

                // Display a message on alert dialog
                builder.setMessage(it.message)
                // Display a neutral button on alert dialog
                builder.setNeutralButton("OK") { _, _ ->

                }
                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()
                // Display the alert dialog on app interface
                dialog.show()
            })
            orderViewModel.coupanDidFailed.observe(viewLifecycleOwner, Observer {
                hideProgressBar()
                btn_apply_coupan_code.isEnabled = true
                // Initialize a new instance of
                val builder = AlertDialog.Builder(myActivity)
                // Set the alert dialog title
                builder.setTitle("Coupon Failed")

                // Display a message on alert dialog
                builder.setMessage(it)
                // Display a neutral button on alert dialog
                builder.setNeutralButton("OK") { _, _ ->

                }
                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()
                // Display the alert dialog on app interface
                dialog.show()
            })
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderPreviewViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = OrderPreview::class.java.name

        fun newInstance(bundle: Bundle?): OrderPreview {
            val fragment = OrderPreview()
            fragment.arguments = bundle
            return fragment
        }
    }
}
