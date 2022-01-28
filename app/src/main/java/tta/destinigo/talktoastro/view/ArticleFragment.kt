package tta.destinigo.talktoastro.view

import android.app.SearchManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.app_bar_main.*

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.util.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.activity.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ArticleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ArticleFragment : tta.destinigo.talktoastro.BaseFragment(), SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {

    private var articleViewModel: ArticleViewModel? = null
    private var articleAdapter: tta.destinigo.talktoastro.adapter.ArticleAdapter? = null

    override val layoutResId: Int
        get() = R.layout.fragment_article

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        toolBarSetup()
    }
    private fun toolBarSetup() {
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton("Articles", false, R.drawable.ic_hamburger)
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        showProgressBar("Loading")
        articleViewModel =
            ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(ArticleViewModel::class.java)
        articleViewModel?.arrayArticleMutableLiveData?.observe(this, Observer {

            articleAdapter =
                tta.destinigo.talktoastro.adapter.ArticleAdapter(it) { articleList, position ->
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
                        R.id.frame_layout,
                        articleDetail,
                        SwipeArticalFragment.tagName
                    )
                    transaction.addToBackStack(SwipeArticalFragment.tagName)
                    transaction.commit()
                }
            val article_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.article_list)
            article_list.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            article_list.adapter = articleAdapter
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
        val searchView_articles: SearchView = myActivity.findViewById(R.id.searchView_articles)
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        searchView_articles.setSearchableInfo(searchManager!!.getSearchableInfo(activity.componentName))
        searchView_articles.setOnQueryTextListener(this)
        (activity as MainActivity).bottom_navigation.menu.getItem(1).isChecked = true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        try {
            val originalArray = articleViewModel!!.arrayArticleModel
            var filteredArray = ArrayList<ArticleList>()
            originalArray.forEach {
                if (it.title?.contains(query.toString(),true)!!){
                    filteredArray!!.add(it)
                }
                if (filteredArray!!.isNotEmpty()){
                    articleAdapter!!.filteredList(filteredArray)
                }
            }
        }catch (e: Exception){
            tta.destinigo.talktoastro.util.LogUtils.d("onQueryTextChange")
        }

        return true
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ArticleViewModel(mApplication) as T
        }
    }

    companion object {
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        internal val tagName: String
            get() = ArticleFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ArticleFragment {
            val fragment = ArticleFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
