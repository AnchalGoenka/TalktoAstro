package tta.destinigo.talktoastro.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import tta.destinigo.talktoastro.BaseFragment

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil

class HowItWorksFragment : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_how_it_works

    override fun getToolbarId(): Int {
        return 0
    }
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val toolbar =  ApplicationUtil.setToolbarTitleAndRechargeButton("How it works", false, R.drawable.ic_hamburger)
        toolbar!!.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
        return inflater.inflate(layoutResId, container, false)
    }

    companion object {
        internal val tagName
            get() = HowItWorksFragment::class.java.name

        fun newInstance(bundle: Bundle?): HowItWorksFragment{
            val fragment = HowItWorksFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
