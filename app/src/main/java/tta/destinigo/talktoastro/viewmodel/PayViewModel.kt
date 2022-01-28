package tta.destinigo.talktoastro.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.model.PayPackageModel
import tta.destinigo.talktoastro.model.RazorPayOrderIdModel
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.util.*


/**

 * Created by Vivek Singh on 2019-07-03.

 */
class PayViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app),PassDataInterface {
    val arrayListMutablePackageData = MutableLiveData<PayPackageModel>()
    var arrayMutableRazorPayOrderID = MutableLiveData<RazorPayOrderIdModel>()
    var passDataInterface: PassDataInterface? = null
    init {
        ApplicationConstant.SELECTPACKAGE=1
    }

     fun getPackages(jsonObj: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.PAYPACKAGES.ordinal,
            ApplicationConstant.PAYPACKAGES, jsonObj, null, null, false)
    }

    fun getRazorPayOrderID(jsonObj: JSONObject){
        Log.d("json ","json>>>>>"+jsonObj)
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.RAZORPAYORDERID.ordinal,
            ApplicationConstant.GENERATE_RAZORPAY_ORDERID, jsonObj, null, null, false)
    }

//    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
//        super.onApiResponse(identifier, response, serverDate, lastModified)
//        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PAYPACKAGES.ordinal) {
//            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response, PayPackageModel::class.java,
//                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PayPackageModel>() {
//                    override fun onParseComplete(data: PayPackageModel) {
//                        arrayListMutablePackageData.postValue(data)
//                    }
//                    override fun onParseFailure(data: PayPackageModel) {
//                        BaseApplication.instance.showToast(""+ data)
//                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
//                    }
//                })
//        }
//    }
    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        LogUtils.d("RAZORPAYORDERID: $response")
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.RAZORPAYORDERID.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), RazorPayOrderIdModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<RazorPayOrderIdModel>() {
                    override fun onParseComplete(data: RazorPayOrderIdModel) {
                        arrayMutableRazorPayOrderID.postValue(data)
                    }
                    override fun onParseFailure(data: RazorPayOrderIdModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }else if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PAYPACKAGES.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), PayPackageModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PayPackageModel>() {
                    override fun onParseComplete(data: PayPackageModel) {
                        arrayListMutablePackageData.postValue(data)
                    }
                    override fun onParseFailure(data: PayPackageModel) {
                        BaseApplication.instance.showToast(""+ data)
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
    }

    override fun onDataReceived(data: String?) {
        passDataInterface?.onDataReceived("Payment Details");
    }
}