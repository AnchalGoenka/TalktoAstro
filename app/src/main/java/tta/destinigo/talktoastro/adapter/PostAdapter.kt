package tta.destinigo.talktoastro.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.PostListModel


class PostAdapter(val list: ArrayList<PostListModel>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val text = itemView.findViewById(R.id.tv_para) as TextView
        val date = itemView.findViewById(R.id.tv_date) as TextView
        val  youTube = itemView.findViewById(R.id.wv_YouTube) as WebView




    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val v=
            LayoutInflater.from(parent.context).inflate(R.layout.post_list,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post_list= list?.get(position)


        holder.date.text=post_list.updatedAt
        val string = Html.fromHtml(post_list.textmsg)
        holder.text.text=string
        holder.youTube.loadData(post_list.ytLink.toString(), "text/html" , "utf-8" );
         holder.youTube.settings.javaScriptEnabled=true
       // holder.youTube.settings.setTextZoom(holder.youTube.settings.getTextZoom() - 75);
        holder.youTube.settings.loadWithOverviewMode = true
        holder.youTube.settings.useWideViewPort = true
        //holder.youTube.settings.textZoom=100
        holder.youTube.setHorizontalScrollBarEnabled(false)
        holder.youTube.setVerticalScrollBarEnabled(false)
       // holder.youTube.settings.builtInZoomControls=true

        holder.youTube.setWebViewClient(WebViewClient())
        holder.youTube.setWebChromeClient(WebChromeClient())
        holder.youTube.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(
                view: View?,
                requestedOrientation: Int,
                callback: CustomViewCallback?
            ) {
                super.onShowCustomView(view, requestedOrientation, callback)
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
            }
        }


    }

}