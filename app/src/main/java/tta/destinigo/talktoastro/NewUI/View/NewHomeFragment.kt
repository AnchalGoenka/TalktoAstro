package tta.destinigo.talktoastro.NewUI.View

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.asksira.loopingviewpager.LoopingViewPager
import kotlinx.android.synthetic.main.fragment_main.*
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.NewUI.Adapter.NewHomeArticle
import tta.destinigo.talktoastro.NewUI.ViewModel.NewHomeFragmentViewModel
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.activity.NotificationActivity
import tta.destinigo.talktoastro.activity.RechargeActivity
import tta.destinigo.talktoastro.adapter.DemoInfiniteAdapter
import tta.destinigo.talktoastro.chat.ChatAstrologerListFragment
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.view.*
import tta.destinigo.talktoastro.viewmodel.MainViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewHomeFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


   /* private var navHumber: ImageView? = null
    private var title: TextView? = null
    private var wallet: ImageView? = null
    private var notification : FrameLayout? = null*/
    private var call :CardView? = null
    private var oderReport :CardView? = null
    private var chat :CardView? = null
    private var ll_astrology_article :LinearLayout? = null
    private var ll_free_kundli :LinearLayout? = null
    private var ll_tarot_card_reading :LinearLayout? = null
    private var ll_ask_free_question :LinearLayout?= null
    private var ll_birth_time :LinearLayout?= null
    private var ll_life :LinearLayout?= null
    private var ll_Free_astrology_services :LinearLayout? = null
    private var ll_Financial_report :LinearLayout? = null
    private var ll_love :LinearLayout? = null

    private var newHomeFragmentViewModel: NewHomeFragmentViewModel? = null
    private var articleAdapter: NewHomeArticle? = null



    override val layoutResId: Int
        get() = R.layout.fragment_new_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_new_home, container, false)
        initViewToolbar(view)
        setOnClicks()
        onclick(view)
        newHomeFragmentViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
            .get(NewHomeFragmentViewModel::class.java)
        getBanner()
        getArticle()
        return  view
    }

    fun initViewToolbar(view: View?){
         /*navHumber = view?.findViewById<ImageView>(R.id.iv_customtoolbar_backHumber)
         title = view?.findViewById<TextView>(R.id.tv_customtoolbar_title)
         wallet =view?.findViewById<ImageView>(R.id.iv_customtoolbar_home_wallet)
         notification = view?.findViewById<FrameLayout>(R.id.frame_layout_notification)*/
         call = view?.findViewById(R.id.cv_talk_with_astrologer)
         oderReport = view?.findViewById(R.id.cv_oder_your_report)
         chat = view?.findViewById(R.id.cv_chat_with_astrologer)
        ll_astrology_article = view?.findViewById(R.id.ll_astrology_article)
        ll_free_kundli= view?.findViewById(R.id.ll_free_kundli)
        ll_tarot_card_reading =view?.findViewById(R.id.ll_tarot_card_reading)
        ll_Free_astrology_services = view?.findViewById(R.id.ll_Free_astrology_services)

        ll_ask_free_question = view?.findViewById(R.id.ll_ask_free_question)
        ll_birth_time = view?.findViewById(R.id.ll_birth_time)
        ll_life = view?.findViewById(R.id.ll_life)
        ll_Financial_report = view?.findViewById(R.id.ll_Financial)
        ll_love = view?.findViewById(R.id.ll_love)


    }

    fun setOnClicks(){

       /* navHumber!!.setOnClickListener{
            Toast.makeText(context, "Humber", Toast.LENGTH_SHORT).show()
        }
        title!!.setOnClickListener{
            Toast.makeText(context, "Title", Toast.LENGTH_SHORT).show()
        }

        wallet!!.setOnClickListener{
            Toast.makeText(context, "Wallet", Toast.LENGTH_SHORT).show()
            val intent =
                Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
            startActivity(intent)
        }
        notification!!.setOnClickListener{
            Toast.makeText(context, "Notification", Toast.LENGTH_SHORT).show()
            val intent =
                Intent(ApplicationUtil.getContext(), NotificationActivity::class.java)
            startActivity(intent)
        }*/
        ll_Free_astrology_services!!.setOnClickListener{
            Toast.makeText(context, "Free_astrology_services", Toast.LENGTH_SHORT).show()
        }
        ll_astrology_article!!.setOnClickListener {

            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 7)
            startActivity(intent)
        }
        ll_free_kundli!!.setOnClickListener {
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 8)
            startActivity(intent)
        }
        ll_tarot_card_reading!!.setOnClickListener {
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 9)
            startActivity(intent)
        }
        ll_ask_free_question!!.setOnClickListener {
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 10)
            startActivity(intent)
        }
        ll_birth_time!!.setOnClickListener {
            Toast.makeText(context, "Birth Time Rectification", Toast.LENGTH_SHORT).show()
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.REPORT_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()
        }
        ll_life!!.setOnClickListener {
            Toast.makeText(context, "Life Prediction Report", Toast.LENGTH_SHORT).show()
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.REPORT_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()
        }
        ll_Financial_report!!.setOnClickListener {
            Toast.makeText(context, "Finanical astrology Report", Toast.LENGTH_SHORT).show()
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.REPORT_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()
        }
        ll_love!!.setOnClickListener {
            Toast.makeText(context, "Love Astrology Report", Toast.LENGTH_SHORT).show()
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.REPORT_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()
        }

        call!!.setOnClickListener {
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.CALL_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()

        }
        oderReport!!.setOnClickListener {
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("type",ApplicationConstant.REPORT_STRING)
            intent.putExtra("fragmentNumber", 5)
            startActivity(intent)
            hideProgressBar()
        }
        chat!!.setOnClickListener {
            showProgressBar("Loading")
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            intent.putExtra("fragmentNumber", 6)
            startActivity(intent)
            hideProgressBar()
        }

    }
    fun getBanner() {
        newHomeFragmentViewModel?.arrayListMutableBannerData?.observe(
            viewLifecycleOwner,
            Observer { banner_model_list ->
                if (banner_model_list.isNullOrEmpty() || banner_model_list.first().marquee.isNullOrEmpty()) {
                    return@Observer
                }

                try {
                    myActivity.findViewById<FrameLayout>(R.id.header_layoutt).setBackgroundColor(
                        ContextCompat.getColor(context!!,R.color.white))
                } catch (e: Exception) {
                }

                var arrOfImages = ArrayList<String>()
                val pager: LoopingViewPager =
                    myActivity.findViewById(R.id.pagerr)
                val indicator: ScrollingPagerIndicator = myActivity.findViewById(R.id.indicatorr)
                if (banner_model_list!!.size > 0) {
                    banner_model_list.forEach { banner_model ->
                        banner_model.bannerList.forEach {
                            arrOfImages.add(it.image)
                        }
                    }
                    val adapter = DemoInfiniteAdapter(context!!, arrOfImages, true)
                    pager.adapter = adapter
                    indicator.attachToPager(pager)
                    hideProgressBar()
                }

            })
    }
    fun getArticle(){
        newHomeFragmentViewModel?.arrayArticleMutableLiveData?.observe(viewLifecycleOwner, Observer {

            articleAdapter =
               NewHomeArticle(it) { articleList, position ->
                    //  val p=position
                    val bundle = Bundle()
                    bundle.putParcelable("articleList", articleList)
                    bundle.putInt("position",position)
                    bundle.putSerializable("article",it)
                    // bundle.putParcelable("position",p)
                    val articleDetail = SwipeArticalFragment.newInstance(bundle)
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.framelayout_newHomeContainer,
                        articleDetail,
                        SwipeArticalFragment.tagName
                    )
                    transaction.addToBackStack(SwipeArticalFragment.tagName)
                    transaction.commit()
                }
            val article_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.rv_home_article)
            article_list.layoutManager = LinearLayoutManager(myActivity, LinearLayoutManager.HORIZONTAL,false)
           // article_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            article_list.adapter = articleAdapter
            if (article_list.layoutManager == null) {
                article_list.addItemDecoration(MarginItemDecorator(1))
            }
            article_list.hasFixedSize()
            hideProgressBar()
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewHomeFragment.
         */
        internal val tagName
            get() = NewHomeFragment::class.java.name

        fun newInstance(bundle: Bundle?): NewHomeFragment {
            val fragment = NewHomeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewHomeFragmentViewModel(mApplication) as T
        }
    }

      fun onclick(v: View?) {
        val item_id = v?.id
        when (item_id) {
            R.id.iv_customtoolbar_backHumber ->{ Toast.makeText(context, "Humber", Toast.LENGTH_SHORT).show()}
            R.id.tv_customtoolbar_title ->{
                Toast.makeText(context, "Title", Toast.LENGTH_SHORT).show()

            }
            R.id.iv_customtoolbar_home_wallet -> {
                Toast.makeText(context, "Wallet", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
                startActivity(intent)
            }
            R.id.frame_layout_notification -> {
                Toast.makeText(context, "Notification", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(ApplicationUtil.getContext(), NotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.cv_oder_your_report ->{

            }
            R.id.cv_chat_with_astrologer ->{

            }
            R.id.cv_talk_with_astrologer ->{
                val transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.framelayout_newHomeContainer, HomeFragment.newInstance(null, ApplicationConstant.CALL_STRING),
                    HomeFragment.tagName)
                transaction.addToBackStack(HomeFragment.tagName)
                transaction.commit()

            }
        }
    }
}