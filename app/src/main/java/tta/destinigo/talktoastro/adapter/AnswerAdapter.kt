package tta.destinigo.talktoastro.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.AnswerList
import tta.destinigo.talktoastro.model.hubQuestionList
import tta.destinigo.talktoastro.util.ApplicationUtil
import kotlin.coroutines.coroutineContext

class AnswerAdapter(val answerList: ArrayList<AnswerList>?): RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val astroPic=itemView.findViewById(R.id.cv_pic) as CircleImageView
        val astroName = itemView.findViewById(R.id.tv_astrologer_name) as TextView
        val astroAnswer = itemView.findViewById(R.id.tv_answer) as TextView
        val llanswer =itemView.findViewById(R.id.ll_answer) as LinearLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=
            LayoutInflater.from(parent.context).inflate(R.layout.answer_list,parent,false)
        return AnswerAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return answerList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list= answerList?.get(position)
        holder.astroName.text=list?.name
        holder.astroAnswer.text=list?.answer
      //  holder.llanswer.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rotate2)
        if(list!!.userType=="astrologer") {
            Glide.with(ApplicationUtil.getContext()).load(Uri.parse(list?.image))
                .placeholder(R.mipmap.default_profile).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE
            )

            .skipMemoryCache(false).into(holder.astroPic)
        }
    }
}