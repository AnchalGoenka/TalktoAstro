package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-10-18.

 */
class CoupanCodeModel (
    @SerializedName("success")
    val success: Int,
    @SerializedName("newTalkValue")
    val newTalkValue: Double,
    @SerializedName("message")
    val message: String
)