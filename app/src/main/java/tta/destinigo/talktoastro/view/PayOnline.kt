package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.fragment_pay_online.*
import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.RazorPayOrderIdModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.PayOnlineViewModel
import tta.destinigo.talktoastro.viewmodel.ProcessReportViewModel
import java.io.Serializable


class PayOnline : tta.destinigo.talktoastro.BaseFragment() {

    lateinit var payOnlineViewModel: PayOnlineViewModel
    var processReportViewModel: ProcessReportViewModel? = null
    var ttaOrderID: Long = 0
    var razorPayOrderID: String = ""
    val orderID = generateOrderIDRandom()
    var reportData: Serializable? = null

    var isComingFromReport: Boolean = SharedPreferenceUtils.readBoolean(
        ApplicationConstant.ISCOMINGFROMREPORT,
        false,
        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
    )

    val phoneNumber = SharedPreferenceUtils.readString(
        ApplicationConstant.MOBILE,
        "",
        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
    )

    val emailID = SharedPreferenceUtils.readString(
        ApplicationConstant.USERNAME,
        "",
        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
    )

    val userID: String? = SharedPreferenceUtils.readString(
        ApplicationConstant.USERID,
        "",
        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
    )
    var amount: String = ""


    override val layoutResId: Int
        get() = R.layout.fragment_pay_online

    override fun getToolbarId(): Int {
        return 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   /* override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //your code which you want to refresh
        (getActivity() as RechargeActivity?)?.refreshMyData2()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        payOnlineViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(PayOnlineViewModel::class.java)
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* val toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Pay online through",
            false,
            R.drawable.ic_arrow_back_24dp
        )

        toolbar.setNavigationOnClickListener {
            tta.destinigo.talktoastro.BaseFragment.popBackStack(myActivity)
        }*/
        if (arguments?.getSerializable("report") != null && isComingFromReport) {
            reportData = arguments?.getSerializable("report") as Serializable
            tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: Report Data =  $reportData")
        }


        val razorPayModel = arguments?.getParcelable<RazorPayOrderIdModel>("razorPayModel") as RazorPayOrderIdModel
        razorPayOrderID = razorPayModel.razorPayOrderId.toString()
        LogUtils.d("Payment Online: razorpayModel razorPayOrderId =  ${razorPayModel.razorPayOrderId}")
        amount = razorPayModel.billingAmount.toString()
        val phoneCode = SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )

        var billingAmount: Int = 0
        if (razorPayModel.billingAmount?.contains(".")!!) {
            billingAmount =
                razorPayModel.billingAmount.substring(0, razorPayModel.billingAmount.indexOf("."))
                    .toInt() * 100
        } else {
            billingAmount = razorPayModel.billingAmount.toInt() * 100
        }


        ttaOrderID = razorPayModel.ttaOrderId
        tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: razorpayModel ttaorderid = $ttaOrderID")
        var currency: String
        if (phoneCode == "91")
            currency = "INR"
        else {
            currency = "USD"
            txv_paytm.visibility = View.GONE
            check_paytm.visibility=View.GONE
            view_paytm.visibility=View.GONE
            view_razorPay.visibility=View.GONE
            check_razorpay.toggle()
            showProgressBar("Loading", true)
            myActivity.runOnUiThread(Runnable() {
                try {
                    val checkOut = Checkout()
                    checkOut.setImage(R.mipmap.ic_launcher)
                    checkOut.setFullScreenDisable(true)

                    val jsonObj = JSONObject()
                    jsonObj.put("amount", billingAmount.toString())
                    jsonObj.put("order_id", razorPayModel.razorPayOrderId)
                    jsonObj.put("currency", currency)
                    tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: razorpay setOnClickListener =  $jsonObj")
                    checkOut.open(myActivity, jsonObj)

                } catch (e: Exception) {
                    Toast.makeText(applicationContext,"Main Activity: ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
                    tta.destinigo.talktoastro.util.LogUtils.d("Main Activity: ${e.printStackTrace()}")
                }


            })
        }



        check_razorpay.setOnClickListener {
            if (check_paytm.isChecked) {
                check_paytm.toggle()
            }
            if (!check_razorpay.isChecked) {
                return@setOnClickListener
            }
            showProgressBar("Loading", true)
            myActivity.runOnUiThread(Runnable() {
                try {
                    val checkOut = Checkout()
                    checkOut.setImage(R.mipmap.ic_launcher)
                    checkOut.setFullScreenDisable(true)

                    val jsonObj = JSONObject()
                    jsonObj.put("amount", billingAmount.toString())
                    jsonObj.put("order_id", razorPayModel.razorPayOrderId)
                    jsonObj.put("currency", currency)
                    tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: razorpay setOnClickListener =  $jsonObj")
                    checkOut.open(myActivity, jsonObj)

                } catch (e: Exception) {
                    Toast.makeText(applicationContext,"Main Activity: ${e.printStackTrace()}",Toast.LENGTH_LONG).show()
                    tta.destinigo.talktoastro.util.LogUtils.d("Main Activity: ${e.printStackTrace()}")
                }


            })
        }
        check_paytm.setOnClickListener {
            if (check_razorpay.isChecked) {
                check_razorpay.toggle()
            }
            if (!check_paytm.isChecked) {
                return@setOnClickListener
            }
            showProgressBar("Loading", true)
            var jsonObj = JSONObject()
            //jsonObj.put("MID", ApplicationConstant.PAYTM_MERCHANTID)
            jsonObj.put("paytmOrderID", orderID.toString())
            jsonObj.put("ttaOrderID", ttaOrderID.toString())
            jsonObj.put("CUST_ID", userID!!)
            jsonObj.put("MOBILE_NO", phoneNumber!!)
            jsonObj.put("EMAIL", emailID!!)
            jsonObj.put("CHANNEL_ID", "WAP")
            jsonObj.put("TXN_AMOUNT", amount)
            jsonObj.put("WEBSITE", "DEFAULT")
            jsonObj.put("INDUSTRY_TYPE_ID", "Retail")
            jsonObj.put(
                "CALLBACK_URL",
                "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${orderID}"
            )
            tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: paytm setOnClickListener =  $jsonObj")
            payOnlineViewModel.getPayTMCheckSumHash(jsonObj)
            payOnlineViewModel?.arrayMutablePaytmOrderIdModel?.observe(
                viewLifecycleOwner,
                Observer {
                    tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: arrayMutablePaytmOrderIdModel response =  $it")
                    processPayTMPayment(it!!.payTMCheckSum)
                })
        }
    }

    fun processPayTMPayment(checkSum: String?) {
        myActivity.runOnUiThread(Runnable {
            try {
                var Service = PaytmPGService.getProductionService()
                var paramMap = HashMap<String, String>();
                paramMap.put("MID", ApplicationConstant.PAYTM_MERCHANTID)
                paramMap.put("ORDER_ID", orderID.toString())
                paramMap.put("CUST_ID", userID!!)
                paramMap.put("MOBILE_NO", phoneNumber!!)
                paramMap.put("EMAIL", emailID!!)
                paramMap.put("CHANNEL_ID", "WAP")
                paramMap.put("TXN_AMOUNT", amount)
                paramMap.put("WEBSITE", "DEFAULT")
                paramMap.put("INDUSTRY_TYPE_ID", "Retail")
                paramMap.put(
                    "CALLBACK_URL",
                    "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=${orderID}"
                )
                paramMap.put(
                    "CHECKSUMHASH",
                    checkSum.toString()
                )
                var Order = PaytmOrder(paramMap)
                Service.initialize(Order, null)
                Service.startPaymentTransaction(
                    myActivity,
                    true,
                    true,
                    object : PaytmPaymentTransactionCallback {
                        /*Call Backs*/
                        override fun someUIErrorOccurred(inErrorMessage: String) {
                            hideProgressBar()
                        }

                        override fun onTransactionResponse(inResponse: Bundle) {
                            hideProgressBar()
                            tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: ProcessPaytmPayment onTransactionResponse =  $inResponse")
                            if (inResponse.get("STATUS") == "TXN_SUCCESS") {
                                Toast.makeText(
                                    ApplicationUtil.getContext(),
                                    "Payment Transaction successfull ",
                                    Toast.LENGTH_LONG
                                ).show()
                                processPayTmResponse()
                            } else {
                                Toast.makeText(
                                    ApplicationUtil.getContext(),
                                    "Payment Transaction failed ",
                                    Toast.LENGTH_LONG
                                ).show()
                                if (isComingFromReport) {
                                    processReport()
                                }
                            }
                        }

                        override fun networkNotAvailable() {
                            hideProgressBar()
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                "Network connection error: Check your internet connectivity",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun clientAuthenticationFailed(inErrorMessage: String) {
                            hideProgressBar()
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                "Authentication failed: Server error" + inErrorMessage.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorLoadingWebPage(
                            iniErrorCode: Int,
                            inErrorMessage: String,
                            inFailingUrl: String
                        ) {
                            hideProgressBar()
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                "Unable to load webpage " + inErrorMessage.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onBackPressedCancelTransaction() {
                            hideProgressBar()
                            Toast.makeText(
                                ApplicationUtil.getContext(),
                                "Transaction cancelled",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        override fun onTransactionCancel(
                            inErrorMessage: String,
                            inResponse: Bundle
                        ) {
                            hideProgressBar()
                        }
                    })
            } catch (e: Exception) {
                hideProgressBar()
                tta.destinigo.talktoastro.util.LogUtils.d("Main Activity: ${e.printStackTrace()}")
            }
        })
    }

    fun generateOrderIDRandom(): Long {
        val number = Math.floor(Math.random() * 9_000_000_000L).toLong() + 1_000_000_0000L
        return number
    }

    /**
     * Razorpay error and success callback methods are called from main activity: the parent activity for this fragment.
     */
    fun sendRazorPaySuccessResp(razorPaymentId: String?) {
        // Save razorpay success response on server side.
        var jsonObj = JSONObject()
        jsonObj.put("ttaOrderID", ttaOrderID)
        jsonObj.put("razorpayPaymentID", razorPaymentId)
        LogUtils.d("Payment Online: Json to save razorpay transaction response =  $jsonObj")
        payOnlineViewModel.saveRazorPaySuccessResponse(jsonObj)
        getPaymentResponseFromLocalServer()
    }

    fun sendRazorPayFailureResp() {
        // Save razorpay success response on server side.
       // goToMainFragmentOnPaymentSuccessAndFailureResponse()
        if (isComingFromReport) {
            processReport()
        }
    }

    fun processPayTmResponse() {
        var jsonObj = JSONObject()
        jsonObj.put("ttaOrderID", ttaOrderID)
        jsonObj.put("paytmOrderID", orderID)
        tta.destinigo.talktoastro.util.LogUtils.d("Payment Online: Json after paytm transaction response =  $jsonObj")
        payOnlineViewModel.savePaytmSuccessResponse(jsonObj)
        getPaymentResponseFromLocalServer()
    }

    fun getPaymentResponseFromLocalServer() {
        payOnlineViewModel?.paymentResponseSuccess.observe(this, Observer {
            Toast.makeText(
                ApplicationUtil.getContext(),
                it.message,
                Toast.LENGTH_LONG
            )
                .show()
            SharedPreferenceUtils.put(
                ApplicationConstant.BALANCE, it.walletNewBalance.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            goToMainFragmentOnPaymentSuccessAndFailureResponse()
        })
        payOnlineViewModel?.paymentResponseFailure?.observe(this, Observer {
            Toast.makeText(
                ApplicationUtil.getContext(),
                it,
                Toast.LENGTH_LONG
            )
                .show()
            goToMainFragmentOnPaymentSuccessAndFailureResponse()
        })
    }



    /**
     * Function to be called when response is recieved from local server after we send the response of paytm/razorpay
     * as request to the local server. If we are not coming from report section than we move to main fragment
     * inspite of sucess or failure from server.
     */
    fun goToMainFragmentOnPaymentSuccessAndFailureResponse() {
        if (!isComingFromReport) {
            Handler().postDelayed(
                {
                    if (!myActivity.isFinishing()) {
                        var transaction: FragmentTransaction =
                            myActivity.supportFragmentManager.beginTransaction()
                        transaction.replace(
                            R.id.frame_layout,
                            MainFragment.newInstance(null),
                            MainFragment.tagName
                        )
                        transaction.addToBackStack(MainFragment.tagName)
                        transaction.commit()
                    }
                },
                1000
            )
        } else {
            processReport()
        }
    }

    /**
     * @func processReport(): gets called when we come to payment page from report section. And then handling the response based on wallet balance
     */
    fun processReport() {
        if (reportData != null) {
            val reportHashMap = reportData as HashMap<String, String>
            val price = reportHashMap.get("price")
            processReportViewModel =
                ViewModelProviders.of(this, MyViewModelFactoryProcessReport(myApplication))
                    .get(ProcessReportViewModel::class.java)
            if (price!!.toInt() <= SharedPreferenceUtils.readString(
                    ApplicationConstant.BALANCE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )!!.toInt()
            ) {
                reportHashMap.remove("price")
                processReportViewModel?.getReports(reportHashMap)
                processReportViewModel?.respMutableData?.observe(this, androidx.lifecycle.Observer {
                    Toast.makeText(
                        ApplicationUtil.getContext(), "Report submitted succesfully.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({
                        if (!myActivity.isFinishing()) {
                            var transaction: FragmentTransaction =
                                myActivity.supportFragmentManager.beginTransaction()
                            transaction.replace(
                                R.id.frame_layout,
                                HomeFragment.newInstance(null, "Report"),
                                HomeFragment.tagName
                            )
                            transaction.addToBackStack(HomeFragment.tagName)
                            transaction.commit()
                        }
                    }, 1000)
                })
            } else {
                Handler().postDelayed(
                    {
                        if (!myActivity.isFinishing()) {
                            var bundle = Bundle()
                            bundle.putSerializable("report", reportData)
                            if (arguments?.getString("ThankYouName") != null) {
                                val astroName = arguments?.getString("ThankYouName") as String
                                bundle.putString("ThankYouName", "${astroName}")
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
                    },
                    2000
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
        hideProgressBar()
    }

    fun showDialog(msg: String) {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(myActivity)
        // Set the alert dialog title
        //builder.setTitle(msg)

        // Display a message on alert dialog
        builder.setMessage(msg)
        // Display a neutral button on alert dialog
        builder.setNeutralButton("OK") { _, _ ->

        }
        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PayOnlineViewModel(mApplication) as T
        }
    }

    class MyViewModelFactoryProcessReport(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProcessReportViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = PayOnline::class.java.name

        fun newInstance(bundle: Bundle?): PayOnline {
            val fragment = PayOnline()
            fragment.arguments = bundle
            return fragment
        }
    }
}
