package tta.destinigo.talktoastro.model


import com.google.gson.annotations.SerializedName

data class ReportDetailModelList(
    val reportDetail: ArrayList<ReportDetailModel>
)

data class ReportDetailModel(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: String
)