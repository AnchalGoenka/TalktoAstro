package tta.destinigo.talktoastro.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil
import java.util.*
/**
 * Created by Anchal
 */
class RechargeActivity  : BaseActivity()  {

    override val fragmentContainerId: Int
        get() = R.id.frame_Recharge
    override val layoutResId: Int
        get() = R.layout.activity_recharge
    var authkey:String = ""
    private lateinit var fragmentManager: FragmentManager
    private var actionBarToggle: ActionBarDrawerToggle? = null
    override fun getToolbarId(): Int {
        return 0
    }

    private var currentStep = 0
    private val currentStep2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
        instance = this
        val toolbar_main_activity = myActivity.findViewById<Toolbar>(R.id.toolbar_recharge)

        toolbar_main_activity.title = "Recharge Wallet"
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar_main_activity.setNavigationOnClickListener {
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            startActivity(intent)
        }

        toolbar_main_activity.layout_btn_recharge.visibility = View.GONE
        toolbar_main_activity.ib_notification.visibility = View.GONE


        val steps: MutableList<String> = ArrayList()
            steps.add("Select Package ")
            steps.add("Payment Details ")
            steps.add("Pay ")
            sv_rechargeActivity.setSteps(steps)

        val intent = this.intent
        val bundle = intent.extras

        var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_Recharge,
            PayActivityFragment.newInstance(bundle),
            PayActivityFragment.Companion.tagName)
         // transaction.addToBackStack(null)
            transaction.commit()
         Log.d("RechargeActivity Bundle",""+bundle)


    }

    fun refreshMyData() {
        //Code to refresh Activity Data hear
        if (currentStep < sv_rechargeActivity.getStepCount() - 1) {
            currentStep++
            sv_rechargeActivity.go(currentStep, true)
        } else {
            sv_rechargeActivity.done(true)
        }
    }

    fun refreshMyData2() {
        //Code to refresh Activity Data hear
        if (currentStep > 0) {
            currentStep--
        }
        sv_rechargeActivity.done(false)
        sv_rechargeActivity.go(currentStep, true)
    }


    companion object {
        lateinit var instance: RechargeActivity public set

    }
}