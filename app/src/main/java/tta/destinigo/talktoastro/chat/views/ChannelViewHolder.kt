package tta.destinigo.talktoastro.views

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.twilio.chat.CallbackListener
import eu.inloop.simplerecycleradapter.SettableViewHolder
import kotterknife.bindView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.chat.ChannelModel
import tta.destinigo.talktoastro.chat.models.ChatHistoryModel
import tta.destinigo.talktoastro.util.LogUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChannelViewHolder : SettableViewHolder<ChatHistoryModel>, AnkoLogger {

    var chatHistoryList = ArrayList<ChatHistoryModel>()

    val friendlyName: TextView by bindView(R.id.call_history_astro_name_val)
    val chatDuration: TextView by bindView(R.id.call_history_conversatio_dur_val)
    val status: TextView by bindView(R.id.call_history_status_val)
    val price: TextView by bindView(R.id.call_history_price_val)
    val view: ConstraintLayout by bindView(R.id.constraintLayout)
    var count: Int = 0
    //val channelSid: TextView by bindView(R.id.channel_sid)
//    val updatedDate: TextView by bindView(R.id.channel_updated_date)
//    val createdDate: TextView by bindView(R.id.channel_created_date)
//    val usersCount: TextView by bindView(R.id.channel_users_count)
//    val totalMessagesCount: TextView by bindView(R.id.channel_total_messages_count)
//    val unconsumedMessagesCount: TextView by bindView(R.id.channel_unconsumed_messages_count)
//    val lastMessageDate: TextView by bindView(R.id.channel_last_message_date)
//    val pushesLevel: TextView by bindView(R.id.channel_pushes_level)

    constructor(context: Context, parent: ViewGroup, chatHistory: ArrayList<ChatHistoryModel>?)
        : super(context, R.layout.channel_item_layout, parent)
    {
        chatHistoryList.clear()
        chatHistoryList = chatHistory!!
    }

    fun toSimpleString(date: Date) : String {
        val format = SimpleDateFormat("MMM dd, yyyy")
        return format.format(date)
    }

    override fun setData(channel: ChatHistoryModel) {
//        count = count + 1
//        if (count > 1) {
//            return
//        }
//        LogUtils.d("printing the count for channel model list: $count")
//        LogUtils.d("printing the count for chat history model list: ${chatHistoryList.size}")
//        chatHistoryList.forEach {
//                if (it.status == "Falied") {
//                    view.setBackgroundResource(R.color.gray_light)
//                } else {
//                    view.setBackgroundResource(R.color.colorPrimaryLight_1)
//                }
//                val simpleDateFormat =
//                    SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//
//                try {
//                    val date1 = channel.channelModel?.dateCreatedAsDate
//                    val date2 = channel.channelModel?.lastMessageDate
//                    if (date1 == null || date2 == null) {
//                        chatDuration.text = "00:00"
//                    } else {
//                        chatDuration.text = printDifference(date1!!, date2!!)
//                    }
//                } catch (e: ParseException) {
//                    e.printStackTrace()
//                }
//                friendlyName.text = it.astroName
//                status.text = it.status
//                price.text = it.amount
//
//                if (channel.channelModel?.dateUpdatedAsDate != null){
//                    val dateFormated = toSimpleString(channel.channelModel?.dateUpdatedAsDate!!)
//                    //updatedDate.text = "Last Updated On: $dateFormated"
//                } else
//                    "<no updated date>"
//
//
//
//                if (channel.channelModel?.dateCreatedAsDate != null) {
//                    val dateFormated = toSimpleString(channel.channelModel?.dateUpdatedAsDate!!)
//                    //createdDate.text = "Created on: $dateFormated"
//                }
//                else
//                    "<no created date>"

//        pushesLevel.text = if (channel.notificationLevel == NotificationLevel.MUTED)
//            "Pushes: Muted"
//        else
//            "Pushes: Default"

//                channel.channelModel?.getUnconsumedMessagesCount(object : CallbackListener<Long>() {
//                    override fun onSuccess(value: Long?) {
//                        Log.d("ChannelViewHolder", "getUnconsumedMessagesCount callback")
//                        //unconsumedMessagesCount.text = "Unread " + value!!.toString()
//                    }
//                })
//
//                channel.channelModel?.getMessagesCount(object : CallbackListener<Long>() {
//                    override fun onSuccess(value: Long?) {
//                        Log.d("ChannelViewHolder", "getMessagesCount callback")
//                        //totalMessagesCount.text = "Messages " + value!!.toString()
//                    }
//                })
//
//                channel.channelModel?.getMembersCount(object : CallbackListener<Long>() {
//                    override fun onSuccess(value: Long?) {
//                        Log.d("ChannelViewHolder", "getMembersCount callback")
//                        //usersCount.text = "Members " + value!!.toString()
//                    }
//                })
//
//                val lastmsg = channel.channelModel?.lastMessageDate;
//                if (lastmsg != null) {
//                    //lastMessageDate.text = lastmsg.toString()
//                }
//
//                itemView.setBackgroundColor(
//                    Color.WHITE
//            if (channel.status == ChannelStatus.JOINED) {
//                Color.WHITE
////                if (channel.type == ChannelType.PRIVATE)
////                    Color.BLUE
////                else
////                    Color.WHITE
//            } else {
//                if (channel.status == ChannelStatus.INVITED)
//                    Color.YELLOW
//                else
//                    Color.GRAY
//            }
//                )
//        }

    }

    fun printDifference(startDate: Date, endDate: Date): String {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        return "${elapsedHours.toString()}:${elapsedMinutes.toString()}:${elapsedSeconds.toString()}"

    }
}
