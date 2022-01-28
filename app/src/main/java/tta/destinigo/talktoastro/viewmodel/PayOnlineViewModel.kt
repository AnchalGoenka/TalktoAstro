package tta.destinigo.talktoastro.viewmodel

import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import tta.destinigo.talktoastro.model.PaymentStatusModel
import tta.destinigo.talktoastro.model.PaytmOrderIdModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONObject
import tta.destinigo.talktoastro.util.LogUtils


/**

 * Created by Vivek Singh on 2019-07-26.

 */
class PayOnlineViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app) {
//    var arrayListMutableLiveData = MutableLiveData<ArrayList<AstrologerListModel>>()

    val arrayMutablePaytmOrderIdModel = MutableLiveData<PaytmOrderIdModel>()
    var paymentResponseSuccess = MutableLiveData<PaymentStatusModel>()
    var paymentResponseFailure = MutableLiveData<String>()

    init {
    }

    fun saveRazorPaySuccessResponse(json: JSONObject?){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.RAZORPAYSUCCESSRESPONSE.ordinal,
            ApplicationConstant.PROCESS_RAZORPAY_ORDER, json, null, null, false)
    }

    fun savePaytmSuccessResponse(json: JSONObject?) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.PAYTMSUCCESSRESPONSE.ordinal,
            ApplicationConstant.PROCESS_PAYTM_ORDER, json, null, null, false)
    }

    fun getPayTMCheckSumHash(json: JSONObject?) {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.PAYTMCHECKSUMHASH.ordinal,
            ApplicationConstant.PAYTM_ORDER, json, null, null, false)
    }


    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        LogUtils.d("RAZORPAYSUCCESSRESPONSE: $response")
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.RAZORPAYSUCCESSRESPONSE.ordinal) {
            tta.destinigo.talktoastro.util.LogUtils.d("Payment Online view model: razorpay response $response")
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), PaymentStatusModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PaymentStatusModel>() {
                    override fun onParseComplete(data: PaymentStatusModel) {
                        when(data.success) {
                            1 -> {
                                paymentResponseSuccess.postValue(data)
                            }
                            0 -> {
                                paymentResponseFailure.postValue(ApplicationConstant.PAYMENTPROCESSINGFAILED)
                            }
                        }
                    }

                    override fun onParseFailure(data: PaymentStatusModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                        Toast.makeText(ApplicationUtil.getContext(),data.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PAYTMSUCCESSRESPONSE.ordinal) {
            LogUtils.d("PAYTMSUCCESSRESPONSE: $response")
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), PaymentStatusModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PaymentStatusModel>() {
                    override fun onParseComplete(data: PaymentStatusModel) {
                        when(data.success) {
                            1 -> {
                                paymentResponseSuccess.postValue(data)
                            }
                            0 -> {
                                paymentResponseFailure.postValue(ApplicationConstant.PAYMENTPROCESSINGFAILED)
                            }
                        }
                    }

                    override fun onParseFailure(data: PaymentStatusModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                        Toast.makeText(ApplicationUtil.getContext(),data.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PAYTMCHECKSUMHASH.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), PaytmOrderIdModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PaytmOrderIdModel>() {
                    override fun onParseComplete(data: PaytmOrderIdModel) {
                        when(data.success) {
                            1 -> {
                                arrayMutablePaytmOrderIdModel.postValue(data)
                            }
                        }
                    }

                    override fun onParseFailure(data: PaytmOrderIdModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
    }
    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        var endIndex = response.indexOf("}",0,true)
        var startIndex = response.indexOf("{", 0, true)
        val finalResp = response.substring(startIndex, endIndex+1)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.PAYTMCHECKSUMHASH.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(finalResp, PaytmOrderIdModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<PaytmOrderIdModel>() {
                    override fun onParseComplete(data: PaytmOrderIdModel) {
                        when(data.success) {
                            1 -> {
                                arrayMutablePaytmOrderIdModel.postValue(data)
                            }
                        }
                    }

                    override fun onParseFailure(data: PaytmOrderIdModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error " )
                    }
                })
        }
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {
        super.onApiError(identifier, error, errorCode)
        tta.destinigo.talktoastro.util.LogUtils.d(error)
    }
}