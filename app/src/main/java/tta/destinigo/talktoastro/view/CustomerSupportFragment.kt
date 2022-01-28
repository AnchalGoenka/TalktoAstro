package tta.destinigo.talktoastro.view

import android.content.Intent
import android.net.Uri
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
import android.widget.TextView

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant

class CustomerSupportFragment : tta.destinigo.talktoastro.BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_customer_support

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
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        val txv_phone_number = myActivity.findViewById<TextView>(R.id.txv_phone_number)
        val txv_whatsapp_number = myActivity.findViewById<TextView>(R.id.txv_whatsapp_number)
        val txv_email = myActivity.findViewById<TextView>(R.id.txv_email)
        val txv_website: TextView = myActivity.findViewById(R.id.txv_website)

        toolbar.title = "Customer Support"
        txv_phone_number.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL);
                intent.data = Uri.parse("tel:${txv_phone_number.text}");
                startActivity(intent);
            } catch (e: Exception) {
                print("exception:$e")
            }
        }
        txv_whatsapp_number.setOnClickListener {
            try {
                val url = "https://api.whatsapp.com/send?phone=${txv_whatsapp_number.text}"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: Exception) {
                print("exception:$e")
            }
        }
        txv_email.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, txv_email.text)
                startActivity(Intent.createChooser(intent, "Send Email"))
            } catch (e: Exception) {
                print("exception:$e")
            }
        }
        txv_website.makeLinks(
            Pair("www.talktoastro.com", android.view.View.OnClickListener {
                try {
                    val url = ApplicationConstant.talkToAstroURL
                    val i = Intent(Intent.ACTION_VIEW)
                    i.setDataAndType(Uri.parse(url), "text/html");
                    startActivity(i)
                } catch (e: Exception) {
                    print("exception:$e")
                }

            })
        )
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

    companion object {
        internal val tagName
            get() = CustomerSupportFragment::class.java.name

        fun newInstance(bundle: Bundle?): CustomerSupportFragment {
            val fragment = CustomerSupportFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
