package tta.destinigo.talktoastro.agora.chat

import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.MessageList
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewChatAdapter(val list : ArrayList<MessageList>): RecyclerView.Adapter<ViewChatAdapter.ViewHolder>() {

    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {

        val  layoutAStro= itemView.findViewById<RelativeLayout>(R.id.rl_astro)
        val  mAstroName=itemView.findViewById(R.id.tv_astro_name) as TextView
        val  mAstroMsg=itemView.findViewById(R.id.tv_astro_msg) as TextView
        val  mAstroTime=itemView.findViewById(R.id.tv_astro_TimeStamp) as TextView
        val  mImageAstro = itemView.findViewById<ImageView>(R.id.iv_img_astro)

        val  layoutUser= itemView.findViewById<RelativeLayout>(R.id.rl_user)
        val  mUserName=itemView.findViewById(R.id.tv_user_name) as TextView
        val  mUserMsg=itemView.findViewById(R.id.tv_user_msg) as TextView
        val  mUserTime=itemView.findViewById(R.id.tv_userTimeStamp) as TextView
        val  mImageUser = itemView.findViewById<ImageView>(R.id.iv_img_user)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewChatAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.agora_msg_list,parent,false)
        return ViewChatAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewChatAdapter.ViewHolder, position: Int) {
        val item = list[position]
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output = SimpleDateFormat("hh:mm aa")

        var d: Date? = null
        var d1: Date? = null
        try {
            if(item.sent_at!=null){
                d = input.parse(item.sent_at)
                d1 = input.parse("2018-02-05 17:06:55.372")
            }else{
                d = input.parse("2018-02-05 00:00:00.503")
                d1 = input.parse("2018-02-05 17:06:55.372")
            }

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted: String = output.format(d)
        val formatted1: String = output.format(d1)

        Log.i("DATE", "" + formatted)
        Log.i("DATE1", "" + formatted1)
        if(item.message==null){
            val msg :String? = Html.fromHtml(item.message.toString()).toString()
            if(item.type=="astrologer"){
                holder.mAstroMsg.text = msg
                holder.mAstroTime.text = formatted
                holder.mAstroMsg.visibility = View.VISIBLE
                holder.mUserTime.visibility = View.GONE
                holder.mAstroName.visibility = View.GONE
            }
            else if(item.type=="user"){
                holder.mUserMsg.text = msg
                holder.mUserTime.text = formatted
                holder.mUserMsg.visibility = View.VISIBLE
                holder.mAstroTime.visibility = View.GONE
                holder.mUserName.visibility = View.GONE
            }

        }else{
            val msg = Html.fromHtml(item.message.toString())
            if(item.type=="astrologer"){
                holder.mAstroMsg.text = msg
                holder.mAstroTime.text = formatted
                holder.mAstroMsg.visibility = View.VISIBLE
                holder.mUserTime.visibility = View.GONE
                holder.mAstroName.visibility = View.GONE
            }
            else if(item.type=="user"){
                holder.mUserMsg.text = msg
                holder.mUserTime.text = formatted
                holder.mUserMsg.visibility = View.VISIBLE
                holder.mAstroTime.visibility = View.GONE
                holder.mUserName.visibility = View.GONE
            }
        }

        holder.setIsRecyclable(false)
    }
}