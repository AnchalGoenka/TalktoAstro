package tta.destinigo.talktoastro.activity

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_razor_pay.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.PayPackageModel
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.view.OrderPreview
import tta.destinigo.talktoastro.viewmodel.PayViewModel


class PayActivityFragment  : tta.destinigo.talktoastro.BaseFragment() {
    private var payViewModel: PayViewModel? = null
    private var packageModel: PayPackageModel? = null
    private var packageAdapter: tta.destinigo.talktoastro.adapter.packageAdapter? = null
    var packageList: RecyclerView?  = null
    var packageId: String = ""
    var packageCode: String = ""

    override val layoutResId: Int
        get() = R.layout.activity_razor_pay
    var passDataInterface: PassDataInterface? = null
    private var back:Boolean=false
    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val isComingFromDrawer = arguments?.getString("isDrawer")
        if (isComingFromDrawer == "true") {
            val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
            toolbar.title = "Recharge"
        } else {
            /*var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Recharge", false, R.drawable.ic_arrow_back_24dp)
            toolbar.setNavigationOnClickListener {
                popBackStack(myActivity)
            }*/
        }

        val userId = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        var jsonObj = JSONObject()
        jsonObj.put("userID", userId)

        showProgressBar("Loading...")
        payViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(PayViewModel::class.java)

        payViewModel?.getPackages(jsonObj)

        payViewModel?.arrayListMutablePackageData?.observe(this, Observer {
            hideProgressBar()
            packageModel = it
            val phoneCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if (phoneCode == "91"){
                setValueBasedOnCurrency(phoneCode, R.drawable.ic_rupee_black, it)
                txv_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee_black, 0, 0, 0)
            } else{
                setValueBasedOnCurrency(phoneCode.toString(), R.drawable.ic_dollar, it)
                txv_amount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar, 0, 0, 0)
            }
        })

        return inflater.inflate(layoutResId, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        payViewModel?.arrayMutableRazorPayOrderID?.removeObservers(this)
        payViewModel?.arrayMutableRazorPayOrderID?.value = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       // val bundle = myActivity.intent.getBundleExtra("Bundle")
      //  Toast.makeText(ApplicationUtil.getContext(),""+bundle.getSerializable("report"), Toast.LENGTH_SHORT).show()
        packageList =myActivity.findViewById(R.id.rv_package)
       /* llInaugural.setOnClickListener {
            txv_amount.text = btn_inaugural.text
            packageId = btn_inaugural.id.toString()
            if(!txv_inaugural_cashback.text.isNullOrEmpty()){
            packageCode = txv_inaugural_code.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button_pressed)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_inaugural.setBackgroundResource(R.color.color_main_page_button_pressed)
          //  txv_inaugural_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button)
            txv_pearl_bronze.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_PearlBronze_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button)
            txv_emerald_silver.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_EmeraldSilver_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button)
            txv_ruby_gold.setBackgroundResource(R.color.colorPrimaryLight)
          //  txv_RubyGold_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button)
            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button)
            txv_sapphire_platinum.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button)
            txv_diamond_titanium.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_DiamondTitanium_cashback.setBackgroundResource(R.color.colorPrimaryLight)
        }
        llPearlBronze.setOnClickListener {
            txv_amount.text = btn_pearl_bronze.text
            packageId = btn_pearl_bronze.id.toString()
            if(!txv_PearlBronze_cashback.text.isNullOrEmpty()){
                packageCode = txv_PearlBronze_cashback.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button)
            txv_inaugural.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_inaugural_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button_pressed)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_pearl_bronze.setBackgroundResource(R.color.color_main_page_button_pressed)
           // txv_PearlBronze_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)
            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button)
            txv_emerald_silver.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_EmeraldSilver_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button)
            txv_ruby_gold.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_RubyGold_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button)
            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button)
            txv_sapphire_platinum.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button)
            txv_diamond_titanium.setBackgroundResource(R.color.colorPrimaryLight)
          //  txv_DiamondTitanium_cashback.setBackgroundResource(R.color.colorPrimaryLight)
        }
        llEmeraldSilver.setOnClickListener {
            txv_amount.text = btn_emerald_silver.text
            packageId = btn_emerald_silver.id.toString()
            if(!txv_EmeraldSilver_cashback.text.isNullOrEmpty()){
                packageCode = txv_EmeraldSilver_code.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button)
            txv_inaugural.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_inaugural_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button)
            txv_pearl_bronze.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_PearlBronze_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button_pressed)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_emerald_silver.setBackgroundResource(R.color.color_main_page_button_pressed)
           // txv_EmeraldSilver_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button)
            txv_ruby_gold.setBackgroundResource(R.color.colorPrimaryLight)
          //  txv_RubyGold_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button)
            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button)
            txv_sapphire_platinum.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button)
            txv_diamond_titanium.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_DiamondTitanium_cashback.setBackgroundResource(R.color.colorPrimaryLight)
        }
        llRubyGold.setOnClickListener {
            txv_amount.text = btn_ruby_gold.text
            packageId = btn_ruby_gold.id.toString()
            if(!txv_RubyGold_cashback.text.isNullOrEmpty()){
                packageCode = txv_RubyGold_code.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button)
            txv_inaugural.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_inaugural_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button)
            txv_pearl_bronze.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_PearlBronze_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button)
            txv_emerald_silver.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_EmeraldSilver_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button_pressed)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_ruby_gold.setBackgroundResource(R.color.color_main_page_button_pressed)
            //txv_RubyGold_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)

            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button)
            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button)
            txv_sapphire_platinum.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button)
            txv_diamond_titanium.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_DiamondTitanium_cashback.setBackgroundResource(R.color.colorPrimaryLight)
        }
        llSapphirePlatinum.setOnClickListener {
            txv_amount.text = btn_sapphire_platinum.text
            packageId = btn_sapphire_platinum.id.toString()
            if(!txv_SapphirePlatinum_cashback.text.isNullOrEmpty()){
                packageCode = txv_SapphirePlatinum_code.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button)
            txv_inaugural.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_inaugural_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button)
            txv_pearl_bronze.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_PearlBronze_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button)
            txv_emerald_silver.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_EmeraldSilver_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button)
            txv_ruby_gold.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_RubyGold_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button_pressed)
            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button_pressed)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button)
            txv_diamond_titanium.setBackgroundResource(R.color.colorPrimaryLight)
            //txv_DiamondTitanium_cashback.setBackgroundResource(R.color.colorPrimaryLight)
        }
        llDiamondTitanium.setOnClickListener {
            txv_amount.text = btn_diamond_titanium.text
            packageId = btn_diamond_titanium.id.toString()
            if(!txv_DiamondTitanium_cashback.text.isNullOrEmpty()){
                packageCode = txv_DiamondTitanium_code.text.toString()}
            //llInaugural.setBackgroundResource(R.color.color_main_page_button)
            ll1_internal_inaugural.setBackgroundResource(R.color.color_main_page_button)
            txv_inaugural.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_inaugural_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll1_internal_pearl_bronze.setBackgroundResource(R.color.color_main_page_button)
           // txv_inaugural_cashback.setBackgroundResource(R.color.color_main_page_button)
            //llPearlBronze.setBackgroundResource(R.color.color_main_page_button)
            txv_pearl_bronze.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_PearlBronze_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_emerald_silver.setBackgroundResource(R.color.color_main_page_button)
            //llEmeraldSilver.setBackgroundResource(R.color.color_main_page_button)
            txv_emerald_silver.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_EmeraldSilver_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_ruby_gold.setBackgroundResource(R.color.color_main_page_button)
            //txv_EmeraldSilver_cashback.setBackgroundResource(R.color.color_main_page_button)
            //llRubyGold.setBackgroundResource(R.color.color_main_page_button)
            txv_ruby_gold.setBackgroundResource(R.color.colorPrimaryLight)
            ll2_internal_sapphire_platinum.setBackgroundResource(R.color.color_main_page_button)
           // txv_RubyGold_cashback.setBackgroundResource(R.color.color_main_page_button)
           // txv_RubyGold_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            //llSapphirePlatinum.setBackgroundResource(R.color.color_main_page_button)
            txv_sapphire_platinum.setBackgroundResource(R.color.colorPrimaryLight)
           // txv_SapphirePlatinum_cashback.setBackgroundResource(R.color.colorPrimaryLight)

            ll2_internal_diamond_titanium.setBackgroundResource(R.color.color_main_page_button_pressed)
            //llDiamondTitanium.setBackgroundResource(R.color.color_main_page_button_pressed)
            txv_diamond_titanium.setBackgroundResource(R.color.color_main_page_button_pressed)
           // txv_DiamondTitanium_cashback.setBackgroundResource(R.color.color_main_page_button_pressed)
        }*/

        val userId = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val reportJson = arguments?.getSerializable("report")

      //  bundle.getSerializable("report")
        btn_proceed.setOnClickListener {
           /* if (currentStep < sv_recharge.getStepCount() - 1) {
                currentStep++
                sv_recharge.go(currentStep, true)
            } else {
                sv_recharge.done(true)
            }*/
        //    passDataInterface?.onDataReceived("Payment Details");
            //ApplicationConstant.SELECTPACKAGE=1

            if (!txv_amount.text.isNullOrEmpty()) {
                showProgressBar("Please wait", true)

                val url = "${ApplicationConstant.GENERATE_RAZORPAY_ORDERID}?packageID=$packageId&userID=$userId"
                var jsonObj = JSONObject()
                jsonObj.put("packageID", packageId)
                jsonObj.put("userID", userId)

                payViewModel?.getRazorPayOrderID(jsonObj)
                payViewModel?.arrayMutableRazorPayOrderID?.observe(this, Observer {
                    if (it != null){
                        (getActivity() as RechargeActivity?)?.refreshMyData()
                        val bundle = Bundle()
                        hideProgressBar()
                        bundle.putParcelable("razorPayModel", it)
                        if(packageCode.isNotEmpty()){
                            bundle.putString("code",packageCode)}
                        if (reportJson != null){
                            bundle.putSerializable("report", reportJson)
                          //  Toast.makeText(ApplicationUtil.getContext(),""+reportJson, Toast.LENGTH_SHORT).show()

                        }
                        if (arguments?.getString("ThankYouName") != null) {
                            val astroName = arguments?.getString("ThankYouName") as String
                            bundle.putString("ThankYouName","${astroName}")
                           // Toast.makeText(ApplicationUtil.getContext(),""+astroName, Toast.LENGTH_SHORT).show()
                        }
                        val orderPreview = OrderPreview.newInstance(bundle)
                        var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frame_Recharge, orderPreview, OrderPreview.tagName)
                        transaction.addToBackStack(OrderPreview.tagName)
                        transaction.commit()
                        back=true


                    }
                })
            } else {
                showAlertDialog()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if(back==true){
            (getActivity() as RechargeActivity?)?.refreshMyData2()}
    }
    /* override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //your code which you want to refresh
        (getActivity() as RechargeActivity?)?.refreshMyData2()
    }
   override fun onStart() {
        super.onStart()
        //update your fragment
        (getActivity() as RechargeActivity?)?.refreshMyData2()
    }*/

    private fun showAlertDialog() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(myActivity)
        // Set the alert dialog title
        builder.setTitle("Package not selected")

        // Display a message on alert dialog
        builder.setMessage("Please select a package.")
        // Display a neutral button on alert dialog
        builder.setNeutralButton("Cancel"){_,_ ->

        }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun setValueBasedOnCurrency(phoneCode: String, currencySymbol: Int, packages: PayPackageModel?) {
        /*btn_inaugural.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)
        btn_pearl_bronze.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)
        btn_emerald_silver.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)
        btn_ruby_gold.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)
        btn_sapphire_platinum.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)
        btn_diamond_titanium.setCompoundDrawablesWithIntrinsicBounds(currencySymbol, 0, 0, 0)*/
//        imgInaugural.setImageResource(currencySymbol)
//        img_pearl_bronze.setImageResource(currencySymbol)
//        img_emerald_silver.setImageResource(currencySymbol)
//        img_ruby_gold.setImageResource(currencySymbol)
//        img_sapphire_platinum.setImageResource(currencySymbol)
//        img_diamond_titanium.setImageResource(currencySymbol)

        /**
         * set amount based on currency value
         */
        if (phoneCode == "91") {
            val arrayOfRupeePackages = packages?.payPackages?.first()

            packageAdapter =
                tta.destinigo.talktoastro.adapter.packageAdapter(arrayOfRupeePackages) { list, i ->
                    txv_amount.text = list.amount
                    packageId = list.id.toString()
                    if (!list.cashback.isNullOrEmpty()) {
                        packageCode = list.code.toString()
                    }
                }

             /*arrayOfRupeePackages?.forEach {
                when(it.name){
                    ApplicationConstant.INAUGURAL -> {
                        btn_inaugural.text = it.amount
                        btn_inaugural.id = it.id.toInt()
                        txv_inaugural.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_inaugural.visibility=View.VISIBLE
                            txv_inaugural_cashback.visibility=View.VISIBLE
                        txv_inaugural_cashback.text=it.cashback + "% Extra"
                        txv_inaugural_code.text=it.code}
                    }
                    ApplicationConstant.PEARL -> {
                        btn_pearl_bronze.text = it.amount
                        btn_pearl_bronze.id = it.id.toInt()
                        txv_pearl_bronze.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_PearlBronze.visibility=View.VISIBLE
                            txv_PearlBronze_cashback.visibility=View.VISIBLE
                        txv_PearlBronze_cashback.text=it.cashback + "% Extra"
                        txv_PearlBronze_code.text=it.code}
                    }
                    ApplicationConstant.EMERALD -> {
                        btn_emerald_silver.text = it.amount
                        btn_emerald_silver.id = it.id.toInt()
                        txv_emerald_silver.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_EmeraldSilver.visibility=View.VISIBLE
                            txv_EmeraldSilver_cashback.visibility=View.VISIBLE
                        txv_EmeraldSilver_cashback.text =it.cashback + "% Extra"
                        txv_EmeraldSilver_code.text=it.code}
                    }
                    ApplicationConstant.RUBY -> {
                        btn_ruby_gold.text = it.amount
                        btn_ruby_gold.id = it.id.toInt()
                        txv_ruby_gold.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_RubyGold.visibility=View.VISIBLE
                            txv_RubyGold_cashback.visibility=View.VISIBLE
                        txv_RubyGold_cashback.text =it.cashback + "% Extra"
                        txv_RubyGold_code.text=it.code}
                    }
                    ApplicationConstant.SAPPHIRE -> {
                        btn_sapphire_platinum.text = it.amount
                        btn_sapphire_platinum.id = it.id.toInt()
                        txv_sapphire_platinum.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_SapphirePlatinum.visibility=View.VISIBLE
                            txv_SapphirePlatinum_cashback.visibility=View.VISIBLE
                        txv_SapphirePlatinum_cashback.text =it.cashback + "% Extra"
                        txv_SapphirePlatinum_code.text=it.code}
                    }
                    ApplicationConstant.DIAMOND -> {
                        btn_diamond_titanium.text = it.amount
                        btn_diamond_titanium.id = it.id.toInt()
                        txv_diamond_titanium.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_DiamondTitanium.visibility=View.VISIBLE
                            txv_DiamondTitanium_cashback.visibility=View.VISIBLE
                        txv_DiamondTitanium_cashback.text =it.cashback + "% Extra"
                        txv_DiamondTitanium_code.text=it.code}
                    }
                }
            }*/
        } else {
            val arrayOfDollarPackages = packages?.payPackages?.last()

            packageAdapter =
                tta.destinigo.talktoastro.adapter.packageAdapter(arrayOfDollarPackages) { list, i ->
                    txv_amount.text = list.amount
                    packageId = list.id.toString()
                    if (!list.cashback.isNullOrEmpty()) {
                        packageCode = list.code.toString()
                        BaseApplication.instance.showToast(list.toString())
                    }

                    /*arrayOfDollarPackages?.forEach {
                when(it.name){
                    ApplicationConstant.INAUGURAL -> {
                        btn_inaugural.text = it.amount
                        btn_inaugural.id = it.id.toInt()
                        txv_inaugural.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_inaugural.visibility=View.VISIBLE
                            txv_inaugural_cashback.visibility=View.VISIBLE
                        txv_inaugural_cashback.text=it.cashback + "% Extra"
                        txv_inaugural_code.text=it.code}
                    }
                    ApplicationConstant.BRONZE -> {
                        btn_pearl_bronze.text = it.amount
                        btn_pearl_bronze.id = it.id.toInt()
                        txv_pearl_bronze.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_PearlBronze.visibility=View.VISIBLE
                            txv_PearlBronze_cashback.visibility=View.VISIBLE
                        txv_PearlBronze_cashback.text=it.cashback + "% Extra"
                        txv_PearlBronze_code.text=it.code}
                    }
                    ApplicationConstant.SILVER -> {
                        btn_emerald_silver.text = it.amount
                        btn_emerald_silver.id = it.id.toInt()
                        txv_emerald_silver.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_EmeraldSilver.visibility=View.VISIBLE
                            txv_EmeraldSilver_cashback.visibility=View.VISIBLE
                        txv_EmeraldSilver_cashback.text =it.cashback + "% Extra"
                        txv_EmeraldSilver_code.text=it.code}
                    }
                    ApplicationConstant.GOLD -> {
                        btn_ruby_gold.text = it.amount
                        btn_ruby_gold.id = it.id.toInt()
                        txv_ruby_gold.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_RubyGold.visibility=View.VISIBLE
                            txv_RubyGold_cashback.visibility=View.VISIBLE
                        txv_RubyGold_cashback.text =it.cashback + "% Extra"
                        txv_RubyGold_code.text=it.code}
                    }
                    ApplicationConstant.PLATINUM -> {
                        btn_sapphire_platinum.text = it.amount
                        btn_sapphire_platinum.id = it.id.toInt()
                        txv_sapphire_platinum.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_SapphirePlatinum.visibility=View.VISIBLE
                            txv_SapphirePlatinum_cashback.visibility=View.VISIBLE
                        txv_SapphirePlatinum_cashback.text =it.cashback + "% Extra"
                        txv_SapphirePlatinum_code.text=it.code }
                    }
                    ApplicationConstant.TITANIUM -> {
                        btn_diamond_titanium.text = it.amount
                        btn_diamond_titanium.id = it.id.toInt()
                        txv_diamond_titanium.text = it.name
                        if(!it.code.isNullOrEmpty()){
                            iv_DiamondTitanium.visibility=View.VISIBLE
                            txv_DiamondTitanium_cashback.visibility=View.VISIBLE
                        txv_DiamondTitanium_cashback.text =it.cashback + "% Extra"
                        txv_DiamondTitanium_code.text=it.code}
                    }
                }
            }*/
                }

        }
      //  packageList?.layoutManager = LinearLayoutManager(ApplicationUtil.getContext())
        packageList?.layoutManager = GridLayoutManager(ApplicationUtil.getContext(),2)
        packageList?.scheduleLayoutAnimation()
        packageList!!.adapter = packageAdapter
        if (packageList?.layoutManager == null) {
            packageList?.addItemDecoration(MarginItemDecorator(1))
        }
        packageList?.hasFixedSize()
    }
    /*fun refreshActivity() {
        val mDashboardActivity: RechargeActivity? = getActivity() as RechargeActivity?
        if (RechargeActivity != null) {
            RechargeActivity.refreshMyData()
        }
    }*/

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PayViewModel(mApplication) as T
        }
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        internal val tagName: String
            get() = tta.destinigo.talktoastro.activity.PayActivityFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): tta.destinigo.talktoastro.activity.PayActivityFragment {
            val fragment = tta.destinigo.talktoastro.activity.PayActivityFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
