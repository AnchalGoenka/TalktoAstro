package tta.destinigo.talktoastro.model


import com.google.gson.annotations.SerializedName

data class CallHistoryModel(
    @SerializedName("astro_id")
    val astroId: String,
    @SerializedName("astroName")
    val astroName: String,
    @SerializedName("call_id")
    val callId: String,
    @SerializedName("callTime")
    val callTime: String,
    @SerializedName("conversation_duration")
    val conversationDuration: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("status")
    val status: String
)