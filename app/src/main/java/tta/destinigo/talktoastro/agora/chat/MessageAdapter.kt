package tta.destinigo.talktoastro.agora.chat

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmMessage
import io.agora.rtm.RtmMessageType
import tta.destinigo.talktoastro.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MessageAdapter(val messagelist: ArrayList<MessageBean>, val itemclick: (MessageBean, Int) -> Unit):
                     RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.agora_msg_list,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  messagelist.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
       val bean = messagelist.get(position) as MessageBean
        val localTime: LocalTime = LocalTime.now()
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val time: String = localTime.format(dateTimeFormatter)
        if(bean.beSelf){
             holder.mUserName.text=bean.account
            holder.mUserName.visibility=View.GONE
            holder.mUserTime.text = time

        }else{
            holder.mAstroName.text=bean.account
            holder.mAstroName.visibility=View.GONE
            holder.mAstroTime.text = time
            if(bean.background!=0){
               holder.mAstroName.setBackgroundResource(bean.background)
                holder.mAstroName.visibility=View.GONE
            }
        }

        val rtmMessage: RtmMessage? = bean.message
        when(rtmMessage!!.messageType){

            RtmMessageType.TEXT->{
                val msg = Html.fromHtml(rtmMessage.text)
                if(bean.beSelf){
                      holder.mUserMsg.visibility=View.VISIBLE
                      holder.mUserMsg.text=msg
                } 
                else{
                    val msg = Html.fromHtml(rtmMessage.text)
                      holder.mAstroMsg.visibility=View.VISIBLE
                      holder.mAstroMsg.text=rtmMessage.text
                }
                holder.mImageAstro.visibility=View.GONE
                holder.mImageUser.visibility=View.GONE
            }
            RtmMessageType.IMAGE->{
                val rtmImageMessage = rtmMessage as RtmImageMessage
                val builder =Glide.with(holder.itemView).load(rtmImageMessage.thumbnail)
                    .override(rtmImageMessage.thumbnailWidth,rtmImageMessage.thumbnailHeight)
                if (bean.beSelf){
                    holder.mImageUser.visibility=View.VISIBLE
                    builder.into(holder.mImageUser)
                }else{
                    holder.mImageAstro.visibility=View.VISIBLE
                    builder.into(holder.mImageAstro)
                }
                holder.mUserMsg.visibility=View.GONE
                holder.mAstroMsg.visibility=View.GONE
            }

        }

        holder.layoutUser.setVisibility(if (bean.beSelf) View.VISIBLE else View.GONE)
        holder.layoutAStro.setVisibility(if (bean.beSelf) View.GONE else View.VISIBLE)
        holder.itemView.setOnClickListener {
            itemclick(bean,position)
        }
    }
}