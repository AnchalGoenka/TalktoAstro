package tta.destinigo.talktoastro.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_kundli_details.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.KDViewPagerAdapter
import tta.destinigo.talktoastro.view.BasicDetail
/**
 * Created by Anchal
 */
class FreeKundliActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setToolbar()
        setupViewPager()
    }


    private fun setToolbar(){
        val toolbar_main_activity = findViewById<Toolbar>(R.id.toolbar_recharge)

        toolbar_main_activity.title = "Kundli Details"
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar_main_activity.setNavigationOnClickListener {
            finish()
        }
        toolbar_main_activity.layout_btn_recharge.visibility = View.GONE
        toolbar_main_activity.ib_notification.visibility = View.GONE
    }

    private fun setupViewPager(){
        val bundle = intent.extras
        val adapter = KDViewPagerAdapter(supportFragmentManager)
        val basicDetails: Fragment = BasicDetail()
        // basicDetails.setArguments(bundle)
        adapter.addFragment(basicDetails , "Basic Details")
        adapter.addFragment(BasicDetail() , "Chart")
        adapter.addFragment(BasicDetail() , "Dasha")

        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

    }

}