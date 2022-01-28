package tta.destinigo.talktoastro.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.hubQuestionList


class AskFreeQuesAdapter(val hubQuestionList: ArrayList<hubQuestionList>,val itemclick: (hubQuestionList, Int) -> Unit)
    : RecyclerView.Adapter<AskFreeQuesAdapter.ViewHolder>() {

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val mQuestion=itemView.findViewById(R.id.tv_question) as TextView
        val mQuestionNo=itemView.findViewById(R.id.tv_question_no) as TextView
        val  mlinearLayoutClickItem = itemView.findViewById(R.id.ll_question_list) as LinearLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.ask_free_question_list,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  hubQuestionList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list=hubQuestionList.get(position)
        holder.mQuestion.text=list.question
        holder.mQuestionNo.text="Q." + ((hubQuestionList.size - (position + 1)) +1)
       // holder.mlinearLayoutClickItem.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.rotate2)
        holder.mlinearLayoutClickItem.setOnClickListener {
            itemclick(list,position)
        }

    }
}