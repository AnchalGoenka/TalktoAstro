package tta.destinigo.talktoastro.view

import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.util.ApplicationUtil

class ArticleDetailFragment : tta.destinigo.talktoastro.BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_article_detail

    override fun getToolbarId(): Int {
        return  0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val article = arguments?.getParcelable<ArticleList>("articleList") as ArticleList
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        val imageView_article_detail: ImageView = myActivity.findViewById(R.id.imageView_article_detail)
        val txv_article_heading_detail: TextView = myActivity.findViewById(R.id.txv_article_heading_detail)

        val webView_article_detail_fragment: WebView = myActivity.findViewById(R.id.webView_article_detail_fragment)

        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = article.title
        toolbar.setNavigationOnClickListener {
            tta.destinigo.talktoastro.BaseFragment.popBackStack(myActivity)
        }
        Glide.with(ApplicationUtil.getContext()).load(Uri.parse(article.image)).placeholder(R.drawable.ic_banner_default).diskCacheStrategy(
            DiskCacheStrategy.RESOURCE)

            .skipMemoryCache(false).into(imageView_article_detail)
        txv_article_heading_detail.text = article.title

        val string=article.description?.replace("&#39;","'")

        webView_article_detail_fragment.loadData(article.description?.replace("&#39;","'").toString(), "text/html; charset=utf-8", "UTF-8");
        /*webView_article_detail_fragment.loadData(article.description, "text/html; charset=UTF-8", null)
        webView_article_detail_fragment.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }
        })*/


    }

    companion object {
        internal val tagName: String
            get() = ArticleDetailFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ArticleDetailFragment {
            val fragment = ArticleDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
