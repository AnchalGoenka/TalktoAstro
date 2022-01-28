package network

import retrofit2.Call
import retrofit2.http.*
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.model.request.SendNotiRequest
import tta.destinigo.talktoastro.util.ApplicationConstant


/**
 * Created by Avnish on 2/4/18.
 */
interface AppService {


    //    //OTP Generate API
//    @FormUrlEncoded
//    @POST(NetworkConstants.OTP_GENERATE)
//    fun callOTP_Api(
//        @Field("user_mobile") user_mobile: String
//    ): Call<OTPGenerateResponce>



    @POST(ApplicationConstant.SEND_NOTIFICATION)
    fun callSendNotification(@Body req: SendNotiRequest
    ): Call<TokenModel>

}

