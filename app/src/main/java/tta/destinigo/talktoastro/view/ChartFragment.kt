package tta.destinigo.talktoastro.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_privacy_policy.*
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil
/**
 * Created by Anchal
 */

class ChartFragment : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_chart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // iv_svg.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

    }

    override fun getToolbarId(): Int {
       return  0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      val  view=inflater.inflate(R.layout.fragment_chart, container, false)
        val svgimage=arguments?.getString("svg")
        Log.d("svg", svgimage.toString())
        val ivchart=view?.findViewById<WebView>(R.id.iv_svg)
        ivchart?.loadData(svgimage.toString(), "text/html; charset=UTF-8", null)
        ivchart?.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }
        })

        return view
    }

    companion object {
        internal val tagName: String
            get() = BasicDetail::class.java.name

        fun newInstance(bundle: Bundle?): BasicDetail {
            val fragment = BasicDetail()
            fragment.arguments = bundle
            return fragment
        }
}}