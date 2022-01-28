package tta.destinigo.talktoastro.NewUI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.NewUI.View.MenuFragment
import tta.destinigo.talktoastro.NewUI.View.NewHomeFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.activity.NotificationActivity
import tta.destinigo.talktoastro.activity.RechargeActivity
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.view.MainFragment
import tta.destinigo.talktoastro.view.RegistrationFragment

class HomeActivity : BaseActivity(), View.OnClickListener {

    private var wallet: ImageView? = null
    private var wallet1: TextView? = null
    private var notification : FrameLayout? = null
    private var home : LinearLayout? = null
    private var menu : LinearLayout? = null
    private var talk : LinearLayout? = null
    private var report : LinearLayout? = null
    private var chat : LinearLayout? = null
    private var more : LinearLayout? = null


    private lateinit var fragmentManager: FragmentManager
    override val fragmentContainerId: Int
        get() = R.id.framelayout_newHomeContainer
    override val layoutResId: Int
        get() = R.layout.activity_home
    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initViewToolbar()
        setOnClicks()
        fragmentManager = supportFragmentManager
        var transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(
            R.id.framelayout_newHomeContainer,
            NewHomeFragment.newInstance(null),
            NewHomeFragment.tagName
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun initViewToolbar(){

       // title = view?.findViewById<TextView>(R.id.tv_customtoolbar_title)
        wallet =findViewById<ImageView>(R.id.iv_customtoolbar_home_wallet)
        wallet1 = findViewById<TextView>(R.id.tv_customtoolbar_home_wallet)
        notification = findViewById<FrameLayout>(R.id.frame_layout_notification)
        home = findViewById<LinearLayout>(R.id.ll_home)
        menu = findViewById<LinearLayout>(R.id.ll_menu)
        talk = findViewById<LinearLayout>(R.id.ll_talk)
        chat = findViewById<LinearLayout>(R.id.ll_chat)
        report = findViewById<LinearLayout>(R.id.ll_report)
        more = findViewById<LinearLayout>(R.id.ll_more)

    }
    fun setOnClicks(){

        wallet!!.setOnClickListener(this)
        wallet1!!.setOnClickListener(this)
        notification!!.setOnClickListener(this)
        home!!.setOnClickListener(this)
        menu!!.setOnClickListener(this)
        talk!!.setOnClickListener(this)
        chat!!.setOnClickListener(this)
        report!!.setOnClickListener(this)
        more!!.setOnClickListener (this)

    }
    override fun onClick(p0: View?) {
        var transaction: FragmentTransaction = fragmentManager.beginTransaction()
        when(p0?.id){
            R.id.iv_customtoolbar_home_wallet->{
                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_customtoolbar_home_wallet->{
                Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(ApplicationUtil.getContext(), RechargeActivity::class.java)
                startActivity(intent)
            }
            R.id.frame_layout_notification ->{
                Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(ApplicationUtil.getContext(), NotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.ll_home ->{
                transaction.replace(
                    R.id.framelayout_newHomeContainer,
                    NewHomeFragment.newInstance(null),
                    NewHomeFragment.tagName
                )
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.ll_menu ->{
                transaction.replace(
                    R.id.framelayout_newHomeContainer,
                    MenuFragment.newInstance(null),
                    MenuFragment.tagName
                )
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.ll_talk ->{
                val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                intent.putExtra("type",ApplicationConstant.CALL_STRING)
                intent.putExtra("fragmentNumber", 5)
                startActivity(intent)
            }
            R.id.ll_report ->{
                val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                intent.putExtra("type",ApplicationConstant.REPORT_STRING)
                intent.putExtra("fragmentNumber", 5)
                startActivity(intent)
            }
            R.id.ll_chat ->{
                val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                intent.putExtra("fragmentNumber", 6)
                startActivity(intent)
            }
            R.id.ll_more ->{

            }
                // do some work here
            }
    }


}