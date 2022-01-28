package tta.destinigo.talktoastro.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.jetbrains.anko.textColor
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.BuildConfig
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.LoginActivity
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.activity.PayActivityFragment
import tta.destinigo.talktoastro.adapter.PostAdapter
import tta.destinigo.talktoastro.adapter.ReviewListAdapter
import tta.destinigo.talktoastro.chat.ChatFormFragment
import tta.destinigo.talktoastro.model.AstrologerListModel
import tta.destinigo.talktoastro.model.AstrologerModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.AstroProfileViewModel
import tta.destinigo.talktoastro.viewmodel.HomeViewModel
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.submitReview
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var astroProfileViewModel: AstroProfileViewModel? = null
    private var reviewAdapter: ReviewListAdapter? = null
    private var postAdapter:PostAdapter?= null
    private var homeViewModelInProfile: HomeViewModel? = null
    private var astroID: String = ""
    private var ratingVal: String = "5"
    private lateinit var mp: MediaPlayer
    private var pause:Boolean = false
    private var extra:String?=""

    override val layoutResId: Int
        get() = R.layout.fragment_profile

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layoutResId, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        mp.release()
        SharedPreferenceUtils.put(ApplicationConstant.RELOADHOMEFRAGMENT, true,
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar("Loading")
        astroProfileViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(AstroProfileViewModel::class.java)
        val profile = arguments?.getParcelable<AstrologerListModel>("profile") as AstrologerListModel
         extra = arguments?.getString("extra")
        txv_person_name.text = profile.firstName + " " + profile.lastName
      //  textView.setText(Html.fromHtml(htmlString));
        val string = Html.fromHtml(profile.about)

       // Html.fromHtml(profile.about, Html.FROM_HTML_MODE_LEGACY), about.BufferType.SPANNABLE);
        tv_about.text=string

        /*txv_detail.settings.setJavaScriptEnabled(true)
        txv_detail.loadData(profile.about, "text/html; charset=UTF-8", null)
        txv_detail.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }
        })*/



       /* val basicDetails: Fragment = BasicDetail()
       // basicDetails.setArguments(bundle)
        val chart: Fragment = ChartFragment()
        //chart.setArguments(bundle)

        val adapter = ProfileviewpagerAdapter(childFragmentManager)
        adapter.addFragment(basicDetails , "About Me")
        adapter.addFragment(chart, "Postsnxbxn")

        viewPager_profile.adapter = adapter
        tabs_profile.setupWithViewPager(viewPager)*/

       // astroProfileViewModel?.getPost(astroID)

        astroProfileViewModel?.postListMutableLiveData?.observe(viewLifecycleOwner, Observer {
            postAdapter = PostAdapter(it)

            val post_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.rv_post)
            post_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())

           // post_list.setItemAnimator(DefaultItemAnimator())

            /*post_list.addOnScrollListener(object : RecyclerViewScrollListener() {
                fun onScrollUp() {}
                fun onScrollDown() {}
                fun onLoadMore() {
                    loadMoreData()
                }
            })*/
            post_list.adapter =postAdapter

            if (list_reviews_profile.layoutManager == null){
                list_reviews_profile.addItemDecoration(MarginItemDecorator(1))
            }

            list_reviews_profile.hasFixedSize()

        })
        btn_posts.setOnClickListener {
            tv_about.visibility=View.GONE
            rv_post.visibility=View.VISIBLE
            btn_posts.setBackgroundColor(ContextCompat.getColor(myActivity, R.color.colorPrimary));
            btn_about_me.setBackgroundColor(ContextCompat.getColor(myActivity, R.color.colorPrimaryLight_2));

        }
        btn_about_me.setOnClickListener {
            rv_post.visibility=View.GONE
            tv_about.visibility=View.VISIBLE
            btn_about_me.setBackgroundColor(ContextCompat.getColor(myActivity, R.color.colorPrimary));
            btn_posts.setBackgroundColor(ContextCompat.getColor(myActivity, R.color.colorPrimaryLight_2));
        }




        txv_language.text = profile.languages
        txv_experience.text = profile.experience
        // txv_experience.text="AAAA"
        txv_expertise_profile.text = profile.expertise
        if(profile.justComing=="new"){
            tv_StarRatingProfile.text = "New"
        }else{
        tv_StarRatingProfile.text = profile.ratingAvg + " "
        }
        tv_call_min.text = profile.callMin
        tv_reports.text = profile.reportNum
        tv_review.text = profile.totalRatings
        if(profile.justComing=="new"){
            iv_new.visibility=View.VISIBLE
        }

        mp = MediaPlayer()
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {

            mp.setDataSource(ApplicationConstant.audiourl + profile.audio)
           // mp.prepare()
            mp.prepareAsync()


        } catch (e: IllegalArgumentException) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (e: SecurityException) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (e: IllegalStateException) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        relativeLayout_play.setOnClickListener {

if(profile.audio==null){
    AwesomeDialog.build(myActivity)
        .body("Audio is not Available")
        .onPositive("Okay",buttonBackgroundColor = R.drawable.layout_rounded,
            textColor = ContextCompat.getColor(
                myActivity,
                android.R.color.white)) {
            Log.d("TAG", "positive ")
        }
        .position(AwesomeDialog.POSITIONS.CENTER)
}
else{
    if (pause) {
        iv_play.visibility = View.INVISIBLE
        iv_pause.visibility = View.VISIBLE
        mp.start()
        pause = false
        // Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()
    } else {


        iv_play.visibility = View.INVISIBLE
        iv_pause.visibility = View.VISIBLE
        mp.start()
        // Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()

    }
    mp.setOnCompletionListener {
        iv_play.visibility = View.VISIBLE
        iv_pause.visibility = View.INVISIBLE
        //pauseBtn.isEnabled = false
        // stopBtn.isEnabled = false
        //Toast.makeText(this,"end",Toast.LENGTH_SHORT).show()
    }
}
        }
        iv_pause.setOnClickListener {
            if(mp.isPlaying){
                mp.pause()
                pause = true
                iv_play.visibility = View.VISIBLE
                iv_pause.visibility=View.INVISIBLE
               // Toast.makeText(this,"media pause",Toast.LENGTH_SHORT).show()
            }
        }
        layout_flash_sale.visibility  = View.GONE
       /* if (profile.isflash == "1") {
           // layout_flash_sale.visibility  = View.VISIBLE
            val value = "${profile.flashdisplayprice}/Min"
            val text = "${profile.price}/Min  "
           // val text = "Flash Offer Price"
            val text2 = text + " " + value

            val spannable = SpannableString(text2)

            spannable.setSpan(
                ForegroundColorSpan(Color.LTGRAY),
                0,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(StrikethroughSpan(),0,text.length,0)
            spannable.setSpan(RelativeSizeSpan(1.3f), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val context: Context = ApplicationUtil.getContext()
            ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);
            //  val color: Int = getColor(R.color.colorPrimary)
            spannable.setSpan(
                ForegroundColorSpan(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null)),
                text.length + 1, text2.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(StyleSpan(Typeface.BOLD), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            txv_rate_profile.setText(spannable, TextView.BufferType.SPANNABLE)
           // txv_flash_sale.setText(spannable, TextView.BufferType.SPANNABLE)
        } else if (profile.isFreeMin == "1") {
            layout_flash_sale.visibility  = View.VISIBLE
            txv_flash_sale.text = "Introductory Free minutes*"
            txv_flash_sale.textColor = Color.WHITE
        }
*/
        val phoneCode = SharedPreferenceUtils.readString(
            ApplicationConstant.PHONECODE, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        val notifyCount = SharedPreferenceUtils.readString(ApplicationConstant.NOTIFYCOUNT, "0", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        var notifyAstroID = SharedPreferenceUtils.readString(ApplicationConstant.CHECKNOTIFYASTROID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
        if (profile.phoneStatus == "offline" || profile.phoneStatus == "busy") {
            imageViewNotification.visibility = View.VISIBLE
        } else {
            imageViewNotification.visibility = View.INVISIBLE
        }
        if (notifyCount == "0") {
            imageViewNotification.setBackgroundResource(R.drawable.ic_notification_unselected)
        } else {
            if (notifyAstroID == profile.id) {
                imageViewNotification.setBackgroundResource(R.drawable.ic_notification_filled)
            } else {
                imageViewNotification.setBackgroundResource(R.drawable.ic_notification_selected)
            }
        }
        if (phoneCode == "91") {


            if (profile.isflash == "1") {
                layout_flash_sale.visibility  = View.VISIBLE
                val value = "${profile.flashdisplayprice}/Min"
                val text = "${profile.price}/Min  "
                 val text3 = "Flash Offer Price"
                val text2 = text + " " + value

                val spannable = SpannableString(text2)
                val spannable1 = SpannableString(text3)

                spannable1.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0,
                    text3.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    ForegroundColorSpan(Color.LTGRAY),
                    0,
                    text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(StrikethroughSpan(),0,text.length,0)
                spannable.setSpan(RelativeSizeSpan(1.3f), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val context: Context = ApplicationUtil.getContext()
                ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);
                //  val color: Int = getColor(R.color.colorPrimary)
                spannable.setSpan(
                    ForegroundColorSpan(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null)),
                    text.length + 1, text2.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(StyleSpan(Typeface.BOLD), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                txv_rate_profile.setText(spannable, TextView.BufferType.SPANNABLE)
                 txv_flash_sale.setText(spannable1, TextView.BufferType.SPANNABLE)
            } else if (profile.isFreeMin == "1") {
                layout_flash_sale.visibility  = View.VISIBLE
                txv_flash_sale.text = "Introductory Free minutes*"
                txv_flash_sale.textColor = Color.WHITE

                val s = "${profile.price} / Min"
                val ss1 = SpannableString(s)
                if (profile.price!!.length == 2){
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 2, 0) // set size
                } else if (profile.price.length == 3) {
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 3, 0) // set size
                } else {
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 1, 0) // set size
                }
                txv_rate_profile.text = ss1
            }else {txv_rate_profile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee, 0, 0, 0)
                val s = "${profile.price} / Min"
                val ss1 = SpannableString(s)
                if (profile.price!!.length == 2){
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 2, 0) // set size
                } else if (profile.price.length == 3) {
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 3, 0) // set size
                } else {
                    ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 1, 0) // set size
                }
                txv_rate_profile.text = ss1}

        } else {
            txv_rate_profile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dollar_orange, 0, 0, 0)
            val s = "${profile.foreigndisplayprice} / Min"
            val ss1 = SpannableString(s)
            if (profile.foreigndisplayprice?.length == 2){
                ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 2, 0) // set size
            } else if (profile.foreigndisplayprice?.length == 3) {
                ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 3, 0) // set size
            } else {
                ss1.setSpan(RelativeSizeSpan(ApplicationConstant.constPriceSize), 0, 1, 0) // set size
            }
            txv_rate_profile.text = ss1
        }
        astroID = profile.id!!
        buttonTitle(extra!!, profile.phoneStatus!!, profile.chatStatus!!)



        var json = JSONObject()
        json.put("astroID", astroID)

        astroProfileViewModel?.getPost(astroID)
        astroProfileViewModel?.getAstroProfile(json)
        val bundle = Bundle()


        astroProfileViewModel?.arrayListMutableLiveData?.observe(viewLifecycleOwner, Observer {
          //  txv_availability_profile.text = it!!.availability.first().day+"\n"+it!!.availability.first().time
            bundle.putParcelableArrayList("reviews", it!!.reviews)

            reviewAdapter = tta.destinigo.talktoastro.adapter.ReviewListAdapter(it!!.reviews,"review")

            val list_reviews_profile: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.list_reviews_profile)
            list_reviews_profile.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            list_reviews_profile.adapter =reviewAdapter
            list_reviews_profile.adapter = reviewAdapter
            if (list_reviews_profile.layoutManager == null){
                list_reviews_profile.addItemDecoration(MarginItemDecorator(1))
            }
          val count =  list_reviews_profile.adapter?.itemCount
            if(it!!.reviews.size <=25){
                tv_loadmore.visibility=View.GONE
            }
            list_reviews_profile.hasFixedSize()
            hideProgressBar()
        })

        tv_loadmore.setOnClickListener {
            var offset=10
            offset=offset+10
            reviewAdapter?.setDisplayCount(offset)
            reviewAdapter?.notifyDataSetChanged()
            /*showProgressBar("Loading")
            var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, WriteReviewFragment.newInstance(bundle, astroID), WriteReviewFragment.tagName)
            transaction.addToBackStack(WriteReviewFragment.tagName)
            transaction.commit()*/
        }
        ratingVal = "5"
        astroProfileViewModel?.reviewListMutableLiveData?.observe(viewLifecycleOwner, Observer {
            Toast.makeText(ApplicationUtil.getContext(), it, Toast.LENGTH_LONG)
                .show()
            write_review_box.text.clear()
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise)
            var json = JSONObject()
            json.put("astroID", astroID)
            astroProfileViewModel?.getAstroProfile(json)
        })

        homeViewModelInProfile = ViewModelProviders.of(this, MyHomeViewModelFactory(myApplication)).get(HomeViewModel::class.java)
        homeViewModelInProfile?.notificationRespone?.observe(viewLifecycleOwner, Observer{
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "1",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            imageViewNotification.setBackgroundResource(R.drawable.ic_notification_filled)
            ApplicationUtil.showDialog(myActivity, it)
        })

        homeViewModelInProfile?.checkNotification?.observe(viewLifecycleOwner, Observer {
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "1",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            val astroID = it.get("astroID")
            if (astroID != null){
                notifyAstroID = astroID
                SharedPreferenceUtils.put(ApplicationConstant.CHECKNOTIFYASTROID, astroID, SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            }
        })

        homeViewModelInProfile?.removeNotification?.observe(viewLifecycleOwner, Observer {
            SharedPreferenceUtils.put(
                ApplicationConstant.NOTIFYCOUNT, "0",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            notifyAstroID = ""
            SharedPreferenceUtils.put(ApplicationConstant.CHECKNOTIFYASTROID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            imageViewNotification.setBackgroundResource(R.drawable.ic_notification_unselected)
        })

        homeViewModelInProfile?.notificationResponeFailed?.observe(viewLifecycleOwner, Observer {
            ApplicationUtil.showDialog(myActivity, it)
        })



        Glide.with(ApplicationUtil.getContext()).load(Uri.parse(profile.image)).placeholder(R.mipmap.default_profile).diskCacheStrategy(
            DiskCacheStrategy.RESOURCE)

            .skipMemoryCache(false).into(imageProfile)

        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(txv_person_name.text.toString(), true, R.drawable.ic_arrow_back_24dp)
        toolbar.layout_btn_recharge.visibility = View.GONE
        toolbar.ib_notification.visibility=View.GONE
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }

        imageViewNotification.setOnClickListener {
            if (ApplicationUtil.checkLogin()) {
                val notifyCount = SharedPreferenceUtils.readString(
                    ApplicationConstant.NOTIFYCOUNT,
                    "0",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                if (notifyCount == "1"  && profile.id != notifyAstroID) {
                    return@setOnClickListener
                }
                var json = JSONObject();
                var msg = ""
                if (notifyAstroID == profile.id) {
                    msg = "Do you want to remove notification?"
                } else {
                    msg = "Do you want to set notification?"
                }
                val alertDialogBuilder =
                    AlertDialog.Builder(
                        context
                    )
                alertDialogBuilder.setNeutralButton("OK"){_,_ ->
                    if (notifyAstroID == profile.id) {
                        json.put("astroID", notifyAstroID)
                        json.put(
                            "userID", SharedPreferenceUtils.readString(
                                ApplicationConstant.USERID, "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                            )
                        )
                        homeViewModelInProfile!!.removeNotification(json)
                    } else {
                        json.put("astroID", profile.id)
                        json.put(
                            "userID", SharedPreferenceUtils.readString(
                                ApplicationConstant.USERID, "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                            )
                        )
                        homeViewModelInProfile!!.setNotifyIcon(json)
                    }
                }
                // set dialog message
                alertDialogBuilder
                    .setMessage(msg)
                    .setCancelable(true)
                alertDialogBuilder.show()
            } else {
                val intent = Intent(ApplicationUtil.getContext(), tta.destinigo.talktoastro.activity.LoginActivity::class.java)
                startActivity(intent)
            }
        }

        btn_submit_review.setOnClickListener {
            if (ApplicationUtil.checkLogin()) {
                var json = JSONObject()
                json.put("rating", ratingVal)
                BaseApplication.instance.showToast(ratingVal)
                json.put("review", write_review_box.text)
                json.put("astroID", astroID)
                json.put(
                    "userID",
                    SharedPreferenceUtils.readString(
                        ApplicationConstant.USERID,
                        "",
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    )
                )

                astroProfileViewModel?.submitReview(json)
            } else {
                val intent = Intent(ApplicationUtil.getContext(), tta.destinigo.talktoastro.activity.LoginActivity::class.java)
                startActivity(intent)
            }
        }

        imgStar1_submit_review.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "1"
            BaseApplication.instance.showToast(ratingVal)
        }
        imgStar2_submit_review.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "2"
            BaseApplication.instance.showToast(ratingVal)
        }
        imgStar3_submit_review.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "3"
            BaseApplication.instance.showToast(ratingVal)
        }
        imgStar4_submit_review.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular)
            ratingVal = "4"
            BaseApplication.instance.showToast(ratingVal)
        }
        imgStar5_submit_review.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise)
            ratingVal = "5"
            BaseApplication.instance.showToast(ratingVal)
        }

    }

    fun buttonTitle(type: String, status: String, chatStatus: String){
        val actionButton: Button = myActivity.findViewById(R.id.actionButton)
            if (type == "Call") {
                if (status == "online") {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_green)
                    actionButton.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    actionButton.text = ApplicationConstant.CALLNOW
                    actionButton.setOnClickListener {
                        if (ApplicationUtil.checkLogin()) {
                            val balance = SharedPreferenceUtils.readString(ApplicationConstant.BALANCE, "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                            val phoneCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                            if ( (phoneCode == "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_RUPEE) ||
                                (phoneCode != "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_DOLLAR) ) {
                                var transaction: FragmentTransaction =
                                    myActivity.supportFragmentManager.beginTransaction()
                                transaction.replace(
                                    R.id.frame_layout,
                                    PayActivityFragment.newInstance(null),
                                    PayActivityFragment.tagName
                                )
                                transaction.addToBackStack(PayActivityFragment.tagName)
                                transaction.commit()
                            } else {
                                var json = JSONObject()
                                json.put("astroID", astroID)
                                json.put(
                                    "userID", SharedPreferenceUtils.readString(
                                        ApplicationConstant.USERID, "",
                                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                                    )
                                )

                                homeViewModelInProfile!!.callAstrologer(json)
                                homeViewModelInProfile!!.MutableCallData.observe(viewLifecycleOwner, Observer {
                                    if (it!!.success == 1) {
                                        actionButton.setBackgroundResource(R.drawable.button_rounded_corners_red)
                                        actionButton.setTextColor(Color.parseColor("#A82E2A"))
                                        actionButton.text = "Busy"

                                        val bundle = Bundle()
                                        bundle.putString(
                                            "ThankYou",
                                            ApplicationConstant.CALL_STRING
                                        )
                                        bundle.putString(
                                            "ThankYouName",
                                            "${txv_person_name.text}"
                                        )
                                        var transaction: FragmentTransaction =
                                            myActivity.supportFragmentManager.beginTransaction()
                                        transaction.replace(
                                            R.id.frame_layout,
                                            ThankYouFragment.newInstance(bundle),
                                            ThankYouFragment.tagName
                                        )
                                        transaction.addToBackStack(ThankYouFragment.tagName)
                                        transaction.commit()
                                    }
                                })
                            }
                        }  else {
                            val intent = Intent(ApplicationUtil.getContext(), tta.destinigo.talktoastro.activity.LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else if (status == "offline") {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_gray)
                    actionButton.setTextColor(Color.DKGRAY)
                    actionButton.text = "Offline"
                } else if (status == "busy") {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_red)
                    actionButton.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    actionButton.text = "Busy"
                }
            }
            else if(type == "Chat") {
                if (chatStatus == "online") {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_green)
                    actionButton.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    actionButton.text = ApplicationConstant.CHAT
                    actionButton.setOnClickListener {
                        //                    if (homeModel.phoneStatus != "online") {
//                        return@HomeAdapter
//                    }
                        if (!ApplicationUtil.checkLogin()) {
                            val intent = Intent(ApplicationUtil.getContext(), LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            val balance = SharedPreferenceUtils.readString(
                                ApplicationConstant.BALANCE, "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                            if (balance!!.toInt() <= resources.getInteger(R.integer.min_user_balance)) {
                                ApplicationUtil.showDialog(myActivity, "Minimum balance should not be less than Rs 50. Please recharge.")
                                return@setOnClickListener
                            }
                            var bundle = Bundle()
                            bundle.putString("astroID", astroID)
                            bundle.putString("astroName",txv_person_name.text.toString())
                            var transaction: FragmentTransaction =
                                myActivity.supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.frame_layout, ChatFormFragment.newInstance(bundle), ChatFormFragment.tagName)
                            transaction.addToBackStack(ChatFormFragment.tagName)
                            transaction.commit()
                        }
                    }
                } else if (chatStatus == "busy") {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_red)
                    actionButton.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    actionButton.text = "Busy"
                } else {
                    actionButton.setBackgroundResource(R.drawable.button_rounded_corners_gray)
                    actionButton.setTextColor(Color.DKGRAY)
                    actionButton.text = "Offline"
                }
            }
            else {
                actionButton.setBackgroundResource(R.drawable.button_rounded_corners_green)
                actionButton.setTextColor(
                    ContextCompat.getColor(
                        ApplicationUtil.getContext(),
                        R.color.white
                    )
                )
                txv_rate_profile.visibility = View.INVISIBLE
                actionButton.text = "Order Report"
                actionButton.setOnClickListener {
                    val balance = SharedPreferenceUtils.readString(
                        ApplicationConstant.BALANCE, "",
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                    if (!ApplicationUtil.checkLogin()) {
                        val intent = Intent(
                            ApplicationUtil.getContext(),
                            tta.destinigo.talktoastro.activity.LoginActivity::class.java
                        )
                        startActivity(intent)
                    }/*else if(balance=="null"){ val intent = Intent(
                        ApplicationUtil.getContext(),
                        tta.destinigo.talktoastro.activity.RechargeActivity::class.java
                    )
                        startActivity(intent)
                    } else if (balance!!.toInt() <= resources.getInteger(R.integer.min_user_balance)) {
                        ApplicationUtil.showDialog(myActivity, "Minimum balance should not be less than Rs 250. Please recharge.")
                        //return@setOnClickListener
                    }*/ else{
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    val bundle = Bundle()
                    bundle.putString("astroID", astroID)
                    bundle.putString(
                        "ThankYouName",
                        "${txv_person_name.text}"
                    )
                    transaction.replace(
                        R.id.frame_layout,
                        ReportFragment.newInstance(bundle),
                        ReportFragment.tagName
                    )
                    transaction.addToBackStack(ReportFragment.tagName)
                    transaction.commit()}
                }
            }
    }

    fun setImagesAsPerRating(img1: Int, img2: Int, img3: Int, img4: Int, img5: Int){
        imgStar1_submit_review.setImageResource(img1)
        imgStar2_submit_review.setImageResource(img2)
        imgStar3_submit_review.setImageResource(img3)
        imgStar4_submit_review.setImageResource(img4)
        imgStar5_submit_review.setImageResource(img5)
    }

    override fun onDestroyView() {
        super.onDestroyView()
       /* if(extra == "Chat"){
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 4)
            startActivity(intent)
        }*/
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AstroProfileViewModel(mApplication) as T
        }
    }

    class MyHomeViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(mApplication) as T
        }
    }
    companion object {
        internal val tagName: String
            get() = ProfileFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ProfileFragment {
            val fragment = ProfileFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance1(movie: AstrologerModel): ProfileFragment {

            // Store the movie data in a Bundle object
            val args = Bundle()
            /*  args.putString(MovieHelper.KEY_TITLE, movie.title)
              args.putInt(MovieHelper.KEY_RATING, movie.rating)
              args.putString(MovieHelper.KEY_POSTER_URI, movie.posterUri)
              args.putString(MovieHelper.KEY_OVERVIEW, movie.overview)*/

            // Create a new MovieFragment and set the Bundle as the arguments
            // to be retrieved and displayed when the view is created
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
