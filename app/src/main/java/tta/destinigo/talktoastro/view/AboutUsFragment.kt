package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.viewmodel.AboutUsViewModel
import kotlinx.android.synthetic.main.fragment_about_us.*


class AboutUsFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var aboutUsViewModel: AboutUsViewModel? = null
    override val layoutResId: Int
        get() = R.layout. fragment_about_us

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        aboutUsViewModel = ViewModelProviders.of(this,
            MyViewModelFactory(myApplication)
        )
            .get(AboutUsViewModel::class.java)
        aboutUsViewModel?.getPrivacyPolicyObserver?.observe(this, Observer {
            webView_aboutus.loadData(it.description, "text/html; charset=UTF-8", null)
//            webView_aboutus.setWebViewClient(object : WebViewClient() {
//                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    return true
//                }
//            })
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "About Us"
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutUsViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = AboutUsFragment::class.java.name

        fun newInstance(bundle: Bundle?): AboutUsFragment {
            val fragment = AboutUsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
