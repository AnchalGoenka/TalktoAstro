package tta.destinigo.talktoastro

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.json.JSONObject
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.KeypadUtils
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.volley.VollyResponseListener
import tta.destinigo.talktoastro.volley.ext.RequestType


/**

 * Created by Vivek Singh on 2019-06-10.

 */
abstract class BaseFragment : Fragment(), tta.destinigo.talktoastro.interfaces.IScreen, VollyResponseListener, View.OnClickListener {

    protected lateinit var activity: tta.destinigo.talktoastro.BaseActivity
    protected lateinit var parentLayout: ConstraintLayout//

    protected abstract val layoutResId: Int
    protected var applicationContext = ApplicationUtil.getContext()

    fun getColor(colorInt: Int): Int {
        return ContextCompat.getColor(applicationContext, colorInt)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (getActivity() !is tta.destinigo.talktoastro.BaseActivity)
            throw RuntimeException("Your Activity should extend from com.agreeya.android.baseframework.ui.activity.BaseActivity")
    }

    fun showProgressBar(message: String) {
        try {
            activity.showProgressBar(message, true)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun showProgressBar(message: String, cancellable: Boolean) {
        activity.showProgressBar(message, cancellable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = myActivity
        setHasOptionsMenu(true)
        tta.destinigo.talktoastro.BaseFragment.Companion.sChildFragmentManager = childFragmentManager
    }

    override val myActivity: tta.destinigo.talktoastro.BaseActivity
        @Throws(RuntimeException::class)
        get() {
            if (getActivity() !is tta.destinigo.talktoastro.BaseActivity)
                throw RuntimeException("Your Activity should extend from com.agreeya.android.baseframework.ui.activity.BaseActivity")
            return getActivity() as tta.destinigo.talktoastro.BaseActivity
        }

    override fun showProgressBar() {

    }

    override fun hideProgressBar() {
        activity.hideProgressBar()
    }

    override val  myApplication: tta.destinigo.talktoastro.BaseApplication
        get() {
            return activity.myApplication
        }

    fun setToolBarTitle(title: String?) {
        if (activity.supportActionBar != null)
            activity.supportActionBar!!.title = title
    }

    protected abstract fun getToolbarId(): Int

    private lateinit var toolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (layoutResId <= 0) {
            tta.destinigo.talktoastro.util.LogUtils.e("No layout resource provided. your fragment should override getLayoutResId method and return layout view")
            return super.onCreateView(inflater, container, savedInstanceState)
        }

        val viewINS = if (view != null) {
            view
        }
        else {
            inflater.inflate(layoutResId, container, false)
        }

        if(view != null) {
            toolbar = viewINS!!.findViewById<View>(getToolbarId()) as Toolbar
        }else
        {
            toolbar = if (getToolbarId() != -1) {
                myActivity.getToolbar()
            } else {
                viewINS!!.findViewById<View>(getToolbarId()) as Toolbar
            }
        }

        toolbar.setTitleTextColor(Color.WHITE)
        myActivity.setSupportActionBar(toolbar)
        setToolBarTitle(tag)
        return viewINS
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentLayout = myActivity.findViewById<View>(
            activity.fragmentContainerId
        ) as ConstraintLayout
    }


    //****************************************************************************************//
    //********************************API request/response handlers -start**************************//
    //****************************************************************************************//


    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        showProgress: Boolean,
        offileReq: Boolean
    ) {
        if (showProgress)
            showProgressBar()
        myApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq
        )

    }

    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request or null
     * @param pParams-     request params or null
     * @param pReqHeaders- request headers if any as key, value map. or null if no headers requied
     * @param offileReq    - true when caching is required, false otherwise
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        offileReq: Boolean
    ) {
        showProgressBar()
        myApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq
        )

    }

    /**
     * Use this method to make a network request with or without a request body
     *
     * @param pReqType     -request type get, put or post
     * @param identifier   - identifier integer for this request
     * @param pUrl         -url
     * @param pJsonReqBody - json object to be sent with request or null
     * @param pParams-     request params or null
     * @param pReqHeaders- request headers if any as key, value map. or null if no headers requied
     * @param offileReq    - true when caching is required, false otherwise
     * @param latitude - latitude from place view modal
     * @param longitude - longitude from place view modal
     */
    fun doApiRequest(
        pReqType: RequestType, identifier: Int, pUrl: String,
        pJsonReqBody: JSONObject?,
        pParams: Map<String, String>?, pReqHeaders: Map<String, String>?,
        offileReq: Boolean, latitude: Double, longitude: Double
    ) {
        myApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, pJsonReqBody, pParams,
            pReqHeaders, this, offileReq, latitude, longitude
        )

    }

    /**
     * Override for above method with least required params
     *
     * @param pReqType   -request type get, put or post
     * @param identifier - identifier integer for this request
     * @param pUrl       -url
     * @param pParams-   request params or null
     * @param offlineReq - true when caching is required, false otherwise
     */
    fun doApiRequest(
        pReqType: RequestType,
        identifier: Int,
        pUrl: String,
        pParams: Map<String, String>?,
        offlineReq: Boolean
    ) {
        showProgressBar()
        myApplication.vollyController.doApiRequest(
            pReqType, identifier,
            pUrl, null, pParams, null, this, offlineReq
        )

    }

    override fun onApiResponse(
        identifier: Int,
        response: String,
        serverDate: Long,
        lastModified: Long,
        latitude: Double,
        longitude: Double
    ) {
        //hideProgressBar()
    }

    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
        hideProgressBar()
    }

    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        hideProgressBar()
    }

    override fun onApiError(identifier: Int, error: String?, errorCode: String?) {

    }

    override fun onClick(v: View) {

    }


    open fun onBackPressed() {
        tta.destinigo.talktoastro.BaseFragment.Companion.popBackStack(myActivity)
    }

    companion object {

        var sChildFragmentManager: FragmentManager? = null


        fun removeFragment(activity: tta.destinigo.talktoastro.BaseActivity, fragment: Fragment){

            activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit()

        }

        /**
         * Call this method from anywhere to add new fragment in activity container layout
         *
         * @param activity         - Reference of  [BaseActivity].
         * @param containerResId   - Resource Id of the parent container layout of activity in which Fragment will be rendered.
         * - value 0 will use default parent  `android.R.id.content` as fragment container
         * @param fragment         - live object of the fragment to load
         * @param tag              - unique tag for fragment as identifier
         * @param isAddToBackStack - if  `true` add this fragment to backstack
         * @param animate          - if `true` show the fragment with default transition animation
         */
        fun replaceFragment(
            activity: Activity, containerResId: Int, fragment: Fragment,
            tag: String, isAddToBackStack: Boolean, animate: Boolean
        ) {
            //Later on Custom animation can be given here
            //        checkNotNull(BaseActivity.sFragmentManager);
            //        checkNotNull(fragment);
            val transaction = tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager.beginTransaction()
            tta.destinigo.talktoastro.BaseFragment.Companion.replace(
                transaction,
                containerResId,
                fragment,
                tag,
                isAddToBackStack,
                animate,
                0
            )
        }

        /**
         * Call this method from anywhere to add new fragment in activity container layout
         *
         * @param activity         - Reference of  [BaseActivity].
         * @param containerResId   - Resource Id of the parent container layout of activity in which Fragment will be rendered.
         * - value 0 will use default parent  `android.R.id.content` as fragment container
         * @param fragment         - live object of the fragment to load
         * @param tag              - unique tag for fragment as identifier
         * @param isAddToBackStack - if  `true` add this fragment to backstack
         * @param animate          - if `true` show the fragment with default transition animation
         */
        fun replaceFragment(
            transaction: FragmentTransaction, containerResId: Int, fragment: Fragment,
            tag: String, isAddToBackStack: Boolean, animate: Boolean, animType: Int
        ) {
            //Later on Custom animation can be given here
            //        checkNotNull(BaseActivity.sFragmentManager);
            //        checkNotNull(fragment);
            //val transaction = BaseActivity.sFragmentManager.beginTransaction()
            tta.destinigo.talktoastro.BaseFragment.Companion.replace(
                transaction,
                containerResId,
                fragment,
                tag,
                isAddToBackStack,
                animate,
                animType
            )
        }

        private fun replace(
            transaction: FragmentTransaction, containerResId: Int, fragment: Fragment,
            tag: String, isAddToBackStack: Boolean, animate: Boolean, animType: Int
        ) {
            var containerResId = containerResId

            if(animate) {

                when (animType) {
                    1 -> transaction.setCustomAnimations(
                        tta.destinigo.talktoastro.R.anim.fade_in_with_zoom,
                        tta.destinigo.talktoastro.R.anim.no_animation
                    )
                    2 -> transaction.setCustomAnimations(
                        tta.destinigo.talktoastro.R.anim.slide_in_left,
                        tta.destinigo.talktoastro.R.anim.slide_out_left
                    )
                    else -> transaction.setCustomAnimations(
                        tta.destinigo.talktoastro.R.anim.slide_in_up,
                        tta.destinigo.talktoastro.R.anim.slide_in_down,
                        tta.destinigo.talktoastro.R.anim.slide_out_up,
                        tta.destinigo.talktoastro.R.anim.slide_out_up
                    )
                }
            }
            else
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE)

            try {
                if (containerResId <= 0)
                    containerResId = android.R.id.content

                transaction.commit()
                // transaction.commitAllowingStateLoss();
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun replaceWithPOP(
            containerResId: Int, fragment: Fragment,
            tag: String, isAddToBackStack: Boolean, animate: Boolean, animType: Int
        ) {
            try {
                var containerResId = containerResId
                val manager = tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager
                val fragmentPopped = manager.findFragmentByTag(tag)
                tta.destinigo.talktoastro.util.LogUtils.d("base fragment ", "fragment inside by tag - $fragmentPopped")
                // val fragmentPopped = manager.popBackStackImmediate(tag, 0)
                if (fragmentPopped == null) {
                    tta.destinigo.talktoastro.util.LogUtils.d("base fragment", "fragment inside create new  $tag")
                    val transaction = tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager.beginTransaction()
                    if (animate) {

                        when (animType) {
                            1 -> transaction.setCustomAnimations(
                                tta.destinigo.talktoastro.R.anim.fade_in_with_zoom,
                                tta.destinigo.talktoastro.R.anim.no_animation,
                                tta.destinigo.talktoastro.R.anim.fade_in_with_zoom,
                                tta.destinigo.talktoastro.R.anim.no_animation
                            )
                            2 -> transaction.setCustomAnimations(
                                tta.destinigo.talktoastro.R.anim.slide_in_left,
                                tta.destinigo.talktoastro.R.anim.slide_out_left
                            )
                            else -> transaction.setCustomAnimations(
                                tta.destinigo.talktoastro.R.anim.fade_in_with_zoom,
                                tta.destinigo.talktoastro.R.anim.no_animation,
                                tta.destinigo.talktoastro.R.anim.fade_in_with_zoom,
                                tta.destinigo.talktoastro.R.anim.no_animation
                            )
                        }
                    } else
                        transaction.setTransition(FragmentTransaction.TRANSIT_NONE)

                    try {
                        if (containerResId <= 0)
                            containerResId = android.R.id.content

//                    if (fragment is TermsConditionFragment || fragment is AirQualityLevelInfo || fragment is SearchPlacesFragment || fragment is PlacesFragment)
//                        transaction.add(containerResId, fragment)
//                    else
                        transaction.replace(containerResId, fragment, tag)
                        if (isAddToBackStack) {
                            transaction.addToBackStack(tag)
                        }
                        transaction.commit()
                        // transaction.commitAllowingStateLoss();
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    tta.destinigo.talktoastro.util.LogUtils.d("base fragment", "fragment inside pop old")
                    // manager.popBackStack(tag, 0)
                    // also add fragment to back stack
                    val transaction = tta.destinigo.talktoastro.BaseActivity.Companion.sFragmentManager.beginTransaction()
                    transaction.replace(containerResId,fragmentPopped)
                    transaction.addToBackStack(tag)
                    transaction.commit()
                }
            }catch (e: Exception){
                tta.destinigo.talktoastro.util.LogUtils.d("replaceWithPOP:${e.printStackTrace()}")
            }
        }

        fun popBackStack(manager: FragmentManager?) {
            if (manager != null) {
                tta.destinigo.talktoastro.util.LogUtils.d("Pop the last fragment${manager.fragments}")
                manager.popBackStack()
            }
        }

        fun popBackStack(activity: tta.destinigo.talktoastro.BaseActivity?) {
            if (activity != null) {
                try {
                    LogUtils.d("Pop the last fragment activity")
                    KeypadUtils.hideSoftKeypad(activity)
                    activity.supportFragmentManager.popBackStack()
                } catch (e: Exception) {
                    LogUtils.d(e.localizedMessage)
                }

            }
        }

        fun getTopFragment(manager: FragmentManager?, containerId: Int): tta.destinigo.talktoastro.BaseFragment? {
            return if (manager != null) manager.findFragmentById(containerId) as tta.destinigo.talktoastro.BaseFragment? else null
        }

        //It will set visibility of view to VISIBLE with expand animation
        fun expand(v: View) {
            v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight
            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = 500
            v.startAnimation(a)
        }


        //It will set visibility of view to GONE with collapse animation
        fun collapse(v: View) {
            val initialHeight = v.measuredHeight
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            // 1dp/ms
            a.duration = 500
            v.startAnimation(a)
        }
    }

}