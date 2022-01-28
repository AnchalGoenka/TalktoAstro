package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.NotificationAdapter
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator

class NotificationListFragment : tta.destinigo.talktoastro.BaseFragment() {

    private var articleViewModel: NotificationArticalViewModel? = null
    private var NotificationAdapter: tta.destinigo.talktoastro.adapter.NotificationAdapter? = null

    override val layoutResId: Int
        get() = R.layout.fragment_article

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
        showProgressBar("Loading")
        articleViewModel =
            ViewModelProviders.of(this, MyViewModelFactory(myApplication))
                .get(NotificationArticalViewModel::class.java)
        articleViewModel?.arrayArticleMutableLiveData?.observe(this, Observer {
            NotificationAdapter =
                NotificationAdapter(it) { articleList, position ->
                    val bundle = Bundle()
                    bundle.putParcelable("articleList", articleList)
                    val articleDetail = ArticleDetailFragment.newInstance(bundle)
                    var transaction: FragmentTransaction =
                        myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.frame_layout,
                        articleDetail,
                        ArticleDetailFragment.tagName
                    )
                    transaction.addToBackStack(ArticleDetailFragment.tagName)
                    transaction.commit()
                }
            val article_list: RecyclerView = myActivity.findViewById(R.id.article_list)
            article_list.layoutManager =
                LinearLayoutManager(ApplicationUtil.getContext())
            article_list.adapter = NotificationAdapter
            if (article_list.layoutManager == null) {
                article_list.addItemDecoration(MarginItemDecorator(1))
            }
            article_list.hasFixedSize()
            hideProgressBar()
        })
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val relativeLayout = view!!.findViewById(R.id.relative) as RelativeLayout
        relativeLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        toolBarSetup()
    }

    private fun toolBarSetup() {
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Notifications",
            false,
            R.drawable.ic_hamburger
        )
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationArticalViewModel(mApplication) as T
        }
    }

    companion object {

        internal val tagName: String
            get() = NotificationListFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): NotificationListFragment {
            val fragment = NotificationListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}