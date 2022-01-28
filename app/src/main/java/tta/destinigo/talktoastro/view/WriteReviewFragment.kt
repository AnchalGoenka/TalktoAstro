package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_write_review.*
import kotlinx.android.synthetic.main.main_toolbar.view.*

import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.ReviewListAdapter
import tta.destinigo.talktoastro.model.Review
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.WriteReviewViewModel


class WriteReviewFragment : tta.destinigo.talktoastro.BaseFragment() {
    private var reviewAdapter: ReviewListAdapter? = null
    private var writeViewModel: WriteReviewViewModel? = null
    private var ratingVal: String = ""
    override val layoutResId: Int
        get() = R.layout.fragment_write_review

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
        showProgressBar()
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Reviews", true, R.drawable.ic_arrow_back_24dp)
        toolbar.layout_btn_recharge.visibility = View.GONE
        toolbar.ib_notification.visibility=View.GONE
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
        val reviewList: java.util.ArrayList<Review>? = arguments!!.getParcelableArrayList("reviews")


        /*imgStar1.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "1"
        }
        imgStar2.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "2"
        }
        imgStar3.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
            ratingVal = "3"
        }
        imgStar4.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular)
            ratingVal = "4"
        }
        imgStar5.setOnClickListener {
            setImagesAsPerRating(R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise)
            ratingVal = "5"
        }
        btn_submit_review.setOnClickListener {
           var json = JSONObject()
            //json.put("rating",spinner_rating.selectedItem.toString())
            json.put("rating", ratingVal)
           json.put("review",write_review_box.text)
            json.put("astroID", astroId)
            json.put("userID", SharedPreferenceUtils.readString(ApplicationConstant.USERID, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())))

            writeViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication))
                .get(WriteReviewViewModel::class.java)

            writeViewModel?.submitReview(json)
           writeViewModel?.arrayListMutableLiveData?.observe(this, Observer {
                Toast.makeText(ApplicationUtil.getContext(), it, Toast.LENGTH_LONG)
                    .show()
            })
        }*/


        reviewAdapter = tta.destinigo.talktoastro.adapter.ReviewListAdapter(reviewList,"ReviewLoadMore")
        val list_reviews_profile: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.list_reviews_profile)
        list_reviews_profile.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
        list_reviews_profile.adapter = reviewAdapter
        if (list_reviews_profile.layoutManager == null){
            list_reviews_profile.addItemDecoration(MarginItemDecorator(1))
        }
        list_reviews_profile.setItemViewCacheSize(20)
        list_reviews_profile.hasFixedSize()
        hideProgressBar()
    }

   /* fun setImagesAsPerRating(img1: Int, img2: Int, img3: Int, img4: Int, img5: Int){
        imgStar1.setImageResource(img1)
        imgStar2.setImageResource(img2)
        imgStar3.setImageResource(img3)
        imgStar4.setImageResource(img4)
        imgStar5.setImageResource(img5)
    }*/

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WriteReviewViewModel(mApplication) as T
        }
    }

    companion object {

        internal val tagName
        get() = WriteReviewFragment::class.java.name

        internal var astroId: String = ""

        @JvmStatic
        fun newInstance(bundle: Bundle?, astroID: String): WriteReviewFragment {
            val fragment = WriteReviewFragment()
            fragment.arguments = bundle
            astroId = astroID
            return fragment
        }
    }
}
