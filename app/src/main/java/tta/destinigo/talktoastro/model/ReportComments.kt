package tta.destinigo.talktoastro.model

import com.google.gson.annotations.SerializedName


/**

 * Created by Vivek Singh on 2019-07-28.

 */
data class ReportComments(
    val arrReportCommentsData: ArrayList<ReportCommentsData>
)

data class ReportCommentsData(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: String
)