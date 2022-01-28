package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName

data class ReportBirthDetailModel(
    @SerializedName("date")
    val date: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("requirement")
    val requirement: String,
    @SerializedName("time")
    val time: String
)