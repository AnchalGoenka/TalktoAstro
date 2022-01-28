package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.Adapter

import tta.destinigo.talktoastro.model.ArticleList

/**
 * Created by Anchal
 */


class SwipeArticalFragment : tta.destinigo.talktoastro.BaseFragment()  {

    private var articleViewModel: ArticleViewModel? = null
    override val layoutResId: Int
        get() = R.layout.fragment_swipe_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

           val view=inflater.inflate(R.layout.fragment_swipe_profile, container, false)
        showProgressBar("Loading")
       // viewPager = view?.findViewById(R.id.vp_swipe_Profile) as ViewPager2
        val viewPager = view.findViewById(R.id.vp_swipe_Profile) as ViewPager
        val position= arguments!!.getInt("position")
        val a=arguments!!.getSerializable("article")
        val adapter = Adapter(a as ArrayList<ArticleList>)
        viewPager.adapter = adapter
        viewPager.currentItem = position
        hideProgressBar()
        /*articleViewModel =
            ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ArticleViewModel::class.java)
        articleViewModel?.arrayArticleMutableLiveData?.observe(this, Observer {
            val viewPager = view.findViewById(R.id.vp_swipe_Profile) as ViewPager




            val article = arguments?.getParcelable("articleList") as ArticleList
            val position= arguments!!.getInt("position")
            val a=arguments!!.getSerializable("article")
            val adapter = Adapter(a as ArrayList<ArticleList>)
            viewPager.adapter = adapter
            viewPager.currentItem = position
            hideProgressBar()
        })*/


        return view
    }


    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticleViewModel(mApplication) as T
        }
    }




    companion object {


        internal val tagName: String
            get() = SwipeArticalFragment::class.java.name



        fun newInstance(bundle: Bundle?): SwipeArticalFragment {
            val fragment = SwipeArticalFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


}