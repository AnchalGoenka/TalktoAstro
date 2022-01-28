package tta.destinigo.talktoastro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.NotificationList
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationMainAdapter(val notificationList: ArrayList<NotificationList>): RecyclerView.Adapter<NotificationMainAdapter.ViewHolder>() {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val mTitle=itemView.findViewById(R.id.tv_title) as TextView
        val mText=itemView.findViewById(R.id.tv_text) as TextView
        val mTime=itemView.findViewById(R.id.tv_time) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.notification_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list: NotificationList = notificationList.get(position)
        holder.mTitle.text = list.title
        holder.mText.text = list.text

        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output = SimpleDateFormat("dd MMM yyyy ")
        var d: Date? = null
        try {
            if(list.time!=null){
                d = input.parse(list.time)

            }else{
                d = input.parse("2018-02-05 00:00:00.503")

            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val time: String = output.format(d)

        holder.mTime.text = time

    }
}