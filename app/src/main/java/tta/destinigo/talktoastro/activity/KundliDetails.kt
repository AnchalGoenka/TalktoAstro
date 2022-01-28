package tta.destinigo.talktoastro.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_kundli_details.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.KDViewPagerAdapter
import tta.destinigo.talktoastro.view.BasicDetail
import tta.destinigo.talktoastro.view.BirthDetails
import tta.destinigo.talktoastro.view.ChartFragment
import tta.destinigo.talktoastro.view.GeneAscendantReport

/**
 * Created by Anchal
 */

class KundliDetails : BaseActivity() {
    override val fragmentContainerId: Int
        get() = R.id.frame_Recharge
    override val layoutResId: Int
        get() = R.layout.activity_kundli_details


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kundli_details)
        showProgressBar("loading",true)
        val intent = this.intent
        val bundle:Bundle = intent.extras!!
        setToolbar()
        setupViewPager(bundle)
        hideProgressBar()
    }

    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }
    override fun getToolbarId(): Int {
      return 0
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

    private fun setupViewPager(bundle: Bundle){
       // Toast.makeText(ApplicationUtil.getContext(),""+bundle.getString("AscendantReport"), Toast.LENGTH_SHORT).show()
        val birthDetails: Fragment = BirthDetails()
        birthDetails.setArguments(bundle)
        val basicDetails: Fragment = BasicDetail()
        basicDetails.setArguments(bundle)
        val chart: Fragment = ChartFragment()
        chart.setArguments(bundle)
        val geneAscendantReport: Fragment = GeneAscendantReport()
        geneAscendantReport.setArguments(bundle)

        val adapter = KDViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(birthDetails , "Birth Details")
        adapter.addFragment(basicDetails , "Basic Details")
        adapter.addFragment(chart, "Brith Chart")
        adapter.addFragment(geneAscendantReport, "Ascendant Report")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

    }




}


