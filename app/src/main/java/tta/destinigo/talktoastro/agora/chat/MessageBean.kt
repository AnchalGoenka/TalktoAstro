package tta.destinigo.talktoastro.agora.chat

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import io.agora.rtm.RtmMessage
import kotlinx.android.parcel.Parcelize
import tta.destinigo.talktoastro.model.AnswerList
import tta.destinigo.talktoastro.model.ReportHistoryModel

class MessageBean (

    @SerializedName("account")
    val account: String?,
    @SerializedName("message")
    var message: RtmMessage?,
    @SerializedName("beSelf")
    val beSelf: Boolean= false,
    @SerializedName("sent_at")
    val sentAt: String?
){
    var background: Int =0
    var cacheFile: String? = null
}


