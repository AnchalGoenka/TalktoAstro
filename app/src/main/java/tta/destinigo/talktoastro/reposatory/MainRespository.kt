package tour.traveling.travel.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import network.AppRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.model.request.SendNotiRequest

class MainRespository {

    fun callSendNotification(req: SendNotiRequest): LiveData<TokenModel> {
        val data = MutableLiveData<TokenModel>()
        AppRetrofit.instance.callSendNotification(req).enqueue(object : Callback<TokenModel> {
            override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<TokenModel>, response: Response<TokenModel>) {

                if (response.isSuccessful){
                    data.value = if (response != null && response.body() != null) response!!.body() else null
                }
                else {
                    val gson = Gson()
                    val adapter = gson.getAdapter(TokenModel::class.java)
                    if (response.errorBody() != null)
                        data.value = adapter.fromJson(response.errorBody()!!.string())
                }
            }
        })

        return data
    }




}