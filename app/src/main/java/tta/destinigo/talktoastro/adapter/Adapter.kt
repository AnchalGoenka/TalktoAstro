package tta.destinigo.talktoastro.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.util.ApplicationUtil

class Adapter(private  val list:ArrayList<ArticleList>):PagerAdapter() {



    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }


    override fun getCount(): Int {
        // Count the items and return it
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.fragment_article_detail,container,false)

        // Get the widgets reference from layout
        val constraintLayout:ConstraintLayout = view.findViewById(R.id.constraintLayout)
        val imageView: ImageView = view.findViewById(R.id.imageView_article_detail)
        val tvLUpper: TextView = view.findViewById(R.id.txv_article_heading_detail)
        val webView: WebView = view.findViewById(R.id.webView_article_detail_fragment)

        // Set the text views text
        //for (n in list.indices) {
            Glide.with(ApplicationUtil.getContext())
                .load(Uri.parse(list.get(position).image.toString()))
                .placeholder(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE
            )

                .skipMemoryCache(false).into(imageView)
            tvLUpper.text = list.get(position).title.toString()

        val string=list.get(position).description?.replace("&#39;","'")
        webView.loadData(string!!, "text/html; charset=utf-8", "UTF-8");
            /*webView.loadData(
                list.get(position).description.toString(),
                "text/html; charset=UTF-8",
                null
            )
            webView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return true
                }
            })*/

            // Add the view to the parent

        //}
        container.addView(view,0)
        // Return the view
        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

}

