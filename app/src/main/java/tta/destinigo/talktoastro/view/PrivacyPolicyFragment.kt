package tta.destinigo.talktoastro.view

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.viewmodel.PrivacyPolicyViewModel
import kotlinx.android.synthetic.main.fragment_privacy_policy.*
import tta.destinigo.talktoastro.util.ApplicationUtil


class PrivacyPolicyFragment : tta.destinigo.talktoastro.BaseFragment() {

    private var privacyViewModel: PrivacyPolicyViewModel? = null

    override val layoutResId: Int
        get() = R.layout.fragment_privacy_policy

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
        privacyViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)
        )
            .get(PrivacyPolicyViewModel::class.java)
        privacyViewModel?.getPrivacyPolicyObserver?.observe(this, Observer {
            webView_privacy_policy.loadData(it.description, "text/html; charset=UTF-8", null)
            webView_privacy_policy.setWebViewClient(object : WebViewClient() {
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
        ApplicationUtil.setToolbarTitleAndRechargeButton("Privacy Policy", false, R.drawable.ic_hamburger)

//        val textView: TextView = myActivity.findViewById(R.id.text_privacy_policy)
//        textView.text = "Personal information\n" +
//                "\n" +
//                "In order to operate the Site in an efficient and effective manner and provide users with information on astrology services & products that may be of interest to them, Talktoastro.com may collect personal information, including contact information (such as an email address), from its users. In addition, this information allows us to provide users with relevant astrological details. We also automatically track certain information based upon your behaviour on the Site and use this information to do internal research on our users' demographics, interests, and behaviour to better understand, protect and serve users in general.\n" +
//                "\n" +
//                "You agree that Talktoastro.com may use your personal information to contact you and deliver information to you that, in some cases, is targeted to your interests or provides administrative notices or communications applicable to your use of the Site. By accepting this Privacy Policy, you expressly agree to receive this information. If you do not wish to receive these communications, we encourage you to opt out of any further receipt by following the opt out provisions provided in each such communication.\n" +
//                "\n" +
//                "We do not sell our users' personal information to anyone for any reason if the user has indicated a desire for us to keep the information private. When placing an order for an astrology service, our users decide for themselves how much contact information they wish to display\n" +
//                "\n" +
//                "All users should be aware, however, that when they voluntarily display or distribute personal information (such as their email address), that information can be collected and used by others. This may result in unsolicited messages from third parties for which Talktoastro.com is not responsible. In addition, you may have arrived at this Web site by following a link from any other affiliate. If so, please be aware that Talktoastro.com may share your information with that affiliate and the affiliate may use the information consistent with its privacy policy instead of this one.\n" +
//                "\n" +
//                "Talktoastro.com may also disclose specific user information when we determine that such disclosure is necessary to comply with the law, to cooperate with or seek assistance from law enforcement or to protect the interests or safety of Talktoastro.com or other users of the Site. In addition, personal information we have collected may be passed on to a third party in the event of a transfer of ownership or assets or a bankruptcy of Talktoastro .com\n" +
//                "\n" +
//                "Credit card security\n" +
//                "\n" +
//                "Talktoastro.com employs encryption for secure credit card transactions. At Talktoastro .com, we use 128-bit SSL encryption, the highest level of SSL encryption currently available to consumers. This ensures the safety and security of your credit card transactions. SSL (\"Secure Sockets Layer\") means that our server and your browser create and agree on an encryption key that will be used only for that particular session. Once established, this key encrypts all communication between our server and your browser.\n" +
//                "\n" +
//                "Advertising\n" +
//                "\n" +
//                "We use third-party advertising companies to serve ads when you visit our Web site. These companies may use information about you and your visits to this and other Web sites in order to provide advertisements on this site and other sites about goods and services of interest to you.\n" +
//                "\n" +
//                "What you should know\n" +
//                "\n" +
//                "Talktoastro.com cannot ensure that all of your private communications and other personal information will never be disclosed in ways not otherwise described in this Privacy Policy. Therefore, although we are committed to protecting your privacy, we do not promise, and you should not expect, that your personal information or private communications would always remain private. As a user of the Site, you understand and agree that you assume all responsibility and risk for your use of the Site, the internet generally, and the documents you post or access and for your conduct on and off the Site.\n" +
//                "\n" +
//                "Logging into Talktoastro.com is always an optional task for you to take, (although logging in does give you access to smarter astrology search tools such as my account, Kundli matcher, my horoscope, Vedic luck)\n" +
//                "\n" +
//                "Your general contact information\n" +
//                "\n" +
//                "Managing your privacy is as simple as selecting which pieces of your contact information are displayed. This is done in the section of the resume form called \"Contact Information.\" Using option 3 above requires that you select the Apply Online Only option from the \"Contact Information\" section at the bottom of the edit form, as shown below.\n" +
//                "\n" +
//                "If you think you have been a victim of fraud, immediately report the committed fraud to your local police and contact us.\n" +
//                "\n" +
//                "The information user provides us\n" +
//                "\n" +
//                "Many of our services require users to sign up for a Talktoastro Account. We would require some personal information, like user name, email address, phone number, Country, GPS Location and Birth Date for the same.\n" +
//                "\n" +
//                "For ensuring an enhanced and customised user experience, we also ask users to update their Gender, Birth Location, Astrology topics (Love, Relationship etc.) and Current Location.\n" +
//                "\n" +
//                "Usage information\n" +
//                "\n" +
//                "We also track and collect information about user interaction, like when an app is installed, a Recharge is done to Wallet, a Consultation is made with Astrologer, a rating is provided to Astrologer. This information includes:\n" +
//                "\n" +
//                "Device information\n" +
//                "\n" +
//                "We collect device-specific information (such as unique device identifiers, and mobile network information). Talktoastro may associate user device identifiers or phone number with their Talktoastro Account.\n" +
//                "\n" +
//                "Location information\n" +
//                "\n" +
//                "When a user avail Talktoastro services, we may collect and process information about their actual location. We use various technologies to determine location, including IP address, GPS, and other sensors that may.\n" +
//                "\n" +
//                "Local storage\n" +
//                "\n" +
//                "We may collect and store information (including personal information) locally on user’s device using mechanisms such as browser web storage (including HTML 5) and application data caches.\n" +
//                "\n" +
//                "Cookies and similar technologies\n" +
//                "\n" +
//                "We use various technologies to collect and store information when the user browse the website or app, and this may include using cookies or similar technologies to identify their browser or device and for marketing purposes. Our product Talk to Astrologer keep track of user’s session to retain their information for multiple days without being logged out.\n" +
//                "\n" +
//                "Browser notification preference\n" +
//                "\n" +
//                "We ask for browser notification preference for delivering the browser-based notification directly to the device.  The user is free to enable or disable this preference and permission at any point of time.\n" +
//                "\n" +
//                "How we use the information, which we collect\n" +
//                "\n" +
//                "We use the information we collect for: research and development, marketing, product analysis and to ensure the best user experience. We also use this information to offer users tailored Astrology content – like giving them relevant Horoscopes, Astrology content, Astrologers to Talk, Vastu and Spirituality domain results.\n" +
//                "\n" +
//                "We may use the name user provide in their Talktoastro  Profile across all of the services we offer that require a Talktoastro  Account. In addition, we may replace multiple names associated with user’s Talktoastro Account so that they are represented consistently across all our services. Other users of Astrologers cannot view or have a name, email, photo or other information that has already been claimed by a user.\n" +
//                "\n" +
//                "User’s First Name, Birth Detail and Interests will be shared with Astrologers for consultation.\n" +
//                "\n" +
//                "When a user sign In Talktoastro, we keep a record of their communication with Astrologers to help, they reach most relevant Astrologers who can provide consultation for specific issues or concerns. We may use user’s email address to inform them about our services, such as letting them know about upcoming changes or improvements.\n" +
//                "\n" +
//                "We use information collected from cookies and other technologies, like IP Address and Location to localize the overall quality of our services. We use Current Location of the user in Talk to Astrologer services to display our pricing in their preferred currency. For example, by saving user’s current Location or country preferences, we will be able to have shown their preferred payment gateway.\n"
//        textView.makeLinks(
//            Pair("Personal information", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Credit card security", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Advertising", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("What you should know", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Your general contact information", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("The information user provides us", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Usage information", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Device information", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Location information", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Local storage", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Cookies and similar technologies", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("Browser notification preference", android.view.View.OnClickListener {
//            })
//        )
//        textView.makeLinks(
//            Pair("How we use the information, which we collect", android.view.View.OnClickListener {
//            })
//        )
    }

    private fun TextView.makeLinks(vararg links: Pair<String, android.view.View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: android.view.View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PrivacyPolicyViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
            get() = PrivacyPolicyFragment::class.java.name

        fun newInstance(bundle: Bundle?): PrivacyPolicyFragment {
            val fragment = PrivacyPolicyFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
