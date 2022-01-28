package tta.destinigo.talktoastro.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import kotlinx.android.synthetic.main.fragment_thank_you.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.viewmodel.MainViewModel

class ThankYouFragment : BaseFragment() {

    var timer: CountDownTimer? = null
    private var mainViewModel: MainViewModel? = null

    override val layoutResId: Int
        get() = R.layout.fragment_thank_you

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(MainViewModel::class.java)
        updateUserWallet()
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        val valueFrom = arguments?.getString("ThankYou")
        val astroName = arguments?.getString("ThankYouName")
        val userPhone = "+"+"${SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))}"+ " " + "${SharedPreferenceUtils.readString(
            ApplicationConstant.MOBILE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))}"
        val res = getResources()
        if (valueFrom == ApplicationConstant.CALL_STRING) {
            textView_call_timer.visibility = View.VISIBLE
            textView_for_ordering_thankyou.text = res.getString(R.string.for_consulting_with_astrologer)
            textView_time_thankyou.text = String.format(res.getString(R.string.answer_time_call), ApplicationConstant.TALKTOASTRO_PHONE_NUMBER, userPhone)
            textView_time_thankyou.makeLinks(
                Pair(ApplicationConstant.TALKTOASTRO_PHONE_NUMBER, View.OnClickListener {
                }),
                Pair(userPhone, View.OnClickListener {
                })
            )
            timer = object: CountDownTimer(90000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //textView_call_timer.setText(""+ (millisUntilFinished/1000) + "s")
                    if ((millisUntilFinished/1000) > 60) {
                        val sec = (millisUntilFinished/1000) - 60
                        if (sec < 10) {
                            textView_call_timer.setText("1:0"+ sec)
                        } else {
                            textView_call_timer.setText("1:"+ sec)
                        }

                    } else {
                        if ((millisUntilFinished/1000) < 10) {
                            textView_call_timer.setText("0:0"+ (millisUntilFinished/1000))
                        } else {
                            textView_call_timer.setText("0:"+ (millisUntilFinished/1000))
                        }

                    }
                }

                override fun onFinish() {
                    textView_call_timer.setText("00")
                }
            }
            timer?.start()
        } else {
            textView_for_ordering_thankyou.text = res.getString(R.string.for_ordering_your_report_to)
            textView_call_timer.visibility = View.GONE

            textView_time_thankyou.text = getResources().getString(R.string.answer_time_sms)
        }
        textView_name_thankyou.text = astroName
    }

    fun setToolBar() {
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Thank You", false, R.drawable.ic_hamburger)
        toolbar!!.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    private fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as TextView).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = getColor(R.color.colorPrimary)
                    ds.isUnderlineText = false
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun updateUserWallet () {
        val userID = SharedPreferenceUtils.readString(ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val json = JSONObject()
        json.put("userID",userID)
        if (mainViewModel != null){
            mainViewModel?.getWalletTransactions(json)
            mainViewModel?.balanceUpdateResp?.observe(this, Observer {
                SharedPreferenceUtils.put(
                    ApplicationConstant.BALANCE, it,
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                setToolBar()
            })
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(mApplication) as T
        }
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        internal val tagName: String
            get() = ThankYouFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ThankYouFragment {
            val fragment = ThankYouFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
