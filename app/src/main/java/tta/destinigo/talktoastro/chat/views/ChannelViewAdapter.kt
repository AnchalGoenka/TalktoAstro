package tta.destinigo.talktoastro.chat.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.chat.ChannelModel
import tta.destinigo.talktoastro.chat.models.ChatHistoryModel
import tta.destinigo.talktoastro.databinding.ChatHistoryListBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/**

 * Created by Vivek Singh on 7/18/20.

 */
class ChannelViewAdapter(
    private var chatHistoryModelList: ArrayList<ChatHistoryModel>,
    private val itemClick: (ChatHistoryModel, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mItems = chatHistoryModelList
    private var channelsList = HashMap<String, ChannelModel>()

    fun updateChatHistoryList(
        newChatHistoryList: List<ChatHistoryModel>?,
        channels: HashMap<String, ChannelModel>
    ) {
        if (newChatHistoryList != null) {
            mItems.clear()
            mItems.addAll(newChatHistoryList)
        }
        channelsList = channels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        val chatHistoryListBinding: ChatHistoryListBinding =
            DataBindingUtil.inflate(inflator, R.layout.channel_item_layout, parent, false)
        return customView(chatHistoryListBinding, itemClick)
        //return ChannelHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            val h1 = holder as ChannelViewAdapter.customView
            val chatHistoryListModel = mItems!![position]
            h1.bind(chatHistoryListModel, position)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
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

    fun clear() {
        mItems.clear()
    }

    fun addItems(items: List<ChatHistoryModel>) {
        addItems(items, false)
    }

    fun addItems(
        items: List<ChatHistoryModel>,
        notifyInserted: Boolean
    ) {
        mItems.addAll(items)
        if (notifyInserted) notifyItemRangeInserted(mItems.size - items.size - 1, items.size)
    }

    //class ChannelHistoryViewHolder(var view: View): RecyclerView.ViewHolder(view)

    inner class customView(
        val chatHistoryListBinding: ChatHistoryListBinding,
        val itemClick: (ChatHistoryModel, Int) -> Unit
    ) : RecyclerView.ViewHolder(chatHistoryListBinding.root) {
        fun bind(chatHistory: ChatHistoryModel, position: Int) {
            /* if (chatHistory.status.equals("Falied",true)) {
                 chatHistoryListBinding.constraintLayout.setBackgroundResource(R.color.gray_light)
             } else {
                 chatHistoryListBinding.constraintLayout.setBackgroundResource(R.color.colorPrimaryLight_1)
             }

             chatHistoryListBinding.chatHistoryConversatioDurVal.text = chatHistory.chatTime

             val simpleDateFormat =
                 SimpleDateFormat("dd/M/yyyy hh:mm:ss")

             try {
                 for (chanObj in channelsList) {
                     if (chatHistory.sessionID == chanObj.value.sid) {
                         val date1 = chanObj.value.dateCreatedAsDate
                         val date2 = chanObj.value.lastMessageDate
                         if (date1 == null || date2 == null) {
                             chatHistoryListBinding.chatHistoryConversatioDurVal.text = "00:00"
                         } else {
                             chatHistoryListBinding.chatHistoryConversatioDurVal.text = printDifference(date1!!, date2!!)
                         }
                     }
                 }
             } catch (e: ParseException) {
                 e.printStackTrace()
             }
             chatHistoryListBinding.chatHistoryAstroNameVal.text = chatHistory.astroName
             chatHistoryListBinding.chatHistoryStatusVal.text = chatHistory.status
             chatHistoryListBinding.chatHistoryPriceVal.text = chatHistory.amount*/


            val data = chatHistoryModelList.get(position)

            var chatDuration = 0
            try {
                chatDuration = chatHistory!!.chat_duration!!.toInt()
            } catch (e: Exception) {
            }

            if (chatDuration > 1) {
                chatHistoryListBinding.constraintInternal.setBackgroundResource(R.color.colorPrimaryLight_1)
                chatHistoryListBinding.callHistoryConversatioDur.visibility = View.VISIBLE
                chatHistoryListBinding.callHistoryPrice.visibility = View.VISIBLE
                chatHistoryListBinding.view4.visibility = View.VISIBLE
                chatHistoryListBinding.viewPaymentTime.visibility = View.VISIBLE
                chatHistoryListBinding.callHistoryConversatioDurVal.visibility = View.VISIBLE
                chatHistoryListBinding.callHistoryPriceVal.visibility = View.VISIBLE
                chatHistoryListBinding.callHistoryPriceVal.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_rupee,
                    0,
                    0,
                    0
                )
                chatHistoryListBinding.callHistoryConversatioDurVal.text =
                    "${chatHistory.chat_duration} mins"
                chatHistoryListBinding.callHistoryStatusVal.text = "Success"
            } else {
                chatHistoryListBinding.constraintInternal.setBackgroundResource(R.color.gray_light)
                chatHistoryListBinding.callHistoryConversatioDur.visibility = View.GONE
                chatHistoryListBinding.callHistoryPrice.visibility = View.GONE
                chatHistoryListBinding.view4.visibility = View.GONE
                chatHistoryListBinding.viewPaymentTime.visibility = View.GONE
                chatHistoryListBinding.callHistoryConversatioDurVal.visibility = View.GONE
                chatHistoryListBinding.callHistoryPriceVal.visibility = View.GONE
                chatHistoryListBinding.callHistoryStatusVal.text = "Failed"
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("GMT")
            val result = formatter.parse(data.chatTime)

            val outputFmt =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val time = outputFmt.format(result)
            chatHistoryListBinding.callHistoryAstroNameVal.text = data.astroName
            chatHistoryListBinding.callHistoryCallTimeVal.text = "" + time
            chatHistoryListBinding.callHistoryCallTime.text = "Chat time:"
            chatHistoryListBinding.callHistoryConversatioDur.text="Chat Duration:"
            chatHistoryListBinding.callHistoryPriceVal.text = data.price
            itemView.setOnClickListener { itemClick(chatHistory, adapterPosition) }

        }
    }

}