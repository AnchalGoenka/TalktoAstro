package tta.destinigo.talktoastro.adapter

import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.ArticleListBinding
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.util.ApplicationUtil


/**

 * Created by Vivek Singh on 2019-06-15.

 */
class NotificationAdapter(private var arrayList : ArrayList<ArticleList>?,
                          private val itemClick: (ArticleList, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val articleListBinding: ArticleListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.fragment_article_list, parent, false)
        return customView(articleListBinding, itemClick)
    }

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

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.NotificationAdapter.customView
            val articleListModel = arrayList!![position]
            h1.bind(articleListModel, position)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(
        val articleListBinding: ArticleListBinding,
        val itemClick: (ArticleList, Int) -> Unit
    ) : RecyclerView.ViewHolder(articleListBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(articleListModel: ArticleList, position: Int) {
            articleListBinding.articleListModel = articleListModel
            articleListBinding.executePendingBindings()
            Glide.with(ApplicationUtil.getContext()).load(Uri.parse(articleListModel.image)).placeholder(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE)

                .skipMemoryCache(false).into(articleListBinding.imgArticle)

            var stringDesc = Html.fromHtml(articleListModel.description).toString()
            stringDesc = stringDesc.replace("\n","")
            articleListBinding.txvDescription.text = stringDesc

            itemView.setOnClickListener{itemClick(articleListModel, adapterPosition)}

        }
    }
}