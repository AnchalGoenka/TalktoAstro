package tta.destinigo.talktoastro.NewUI.Adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.NotificationMainAdapter
import tta.destinigo.talktoastro.databinding.NewHomeArticleListBinding
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.model.NotificationList
import tta.destinigo.talktoastro.util.ApplicationUtil

class NewHomeArticle(private var arrayList: ArrayList<ArticleList>?, private val itemClick: (ArticleList, Int) -> Unit)
    : RecyclerView.Adapter<NewHomeArticle.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    fun filteredList(filteredArray: ArrayList<ArticleList>){
        this.arrayList = filteredArray
        notifyDataSetChanged()
    }




    override fun onBindViewHolder(holder: NewHomeArticle.ViewHolder, position: Int) {
        val list:ArticleList = arrayList!!.get(position)
        holder.mTitle.text = list.title
        Glide.with(ApplicationUtil.getContext()).load(Uri.parse(list.image)).placeholder(R.drawable.ic_banner_default).diskCacheStrategy(
            DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(false).into(holder.img)
        holder.mlinearLayoutClickItem.setOnClickListener {
            itemClick(list,position)
        }
       // holder.mText.text = list.text
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val mTitle=itemView.findViewById(R.id.tv_home_article) as TextView
        val img=itemView.findViewById(R.id.iv_article) as ImageView
        val mlinearLayoutClickItem=itemView.findViewById(R.id.ll_article_home) as LinearLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.item_home_article,parent,false)
        return ViewHolder(v)
    }
}