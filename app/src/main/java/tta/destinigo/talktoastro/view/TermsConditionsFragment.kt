package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.viewmodel.TermsConditionsViewModel
import kotlinx.android.synthetic.main.fragment_terms_conditions.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.util.ApplicationUtil


class TermsConditionsFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var termsAndConditionViewModel: TermsConditionsViewModel? = null
    override val layoutResId: Int
        get() = R.layout.fragment_terms_conditions

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
        termsAndConditionViewModel = ViewModelProviders.of(this,
            MyViewModelFactory(myApplication)
        )
            .get(TermsConditionsViewModel::class.java)
        termsAndConditionViewModel?.getPrivacyPolicyObserver?.observe(this, Observer {
            webView_termsandconditions.loadData(it.description, "text/html; charset=UTF-8", null)
            webView_termsandconditions.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return true
                }
            })
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ApplicationUtil.setToolbarTitleAndRechargeButton("Terms and conditions", false, R.drawable.ic_hamburger)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TermsConditionsViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = TermsConditionsFragment::class.java.name

        fun newInstance(bundle: Bundle?): TermsConditionsFragment {
            val fragment = TermsConditionsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
