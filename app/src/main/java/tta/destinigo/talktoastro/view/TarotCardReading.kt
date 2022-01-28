package tta.destinigo.talktoastro.view

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R


/**
 * A simple [Fragment] subclass.
 * Use the [TarotCardReading.newInstance] factory method to
 * create an instance of this fragment.
 */
class TarotCardReading : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_tarot_card_reading

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
        val view= inflater.inflate(R.layout.fragment_tarot_card_reading, container, false)

        val iv_first_card: ImageView = view.findViewById(R.id.iv_first_card)
        val iv_second_card: ImageView = view.findViewById(R.id.iv_second_card)
        val iv_Third_card: ImageView = view.findViewById(R.id.iv_Third_card)
        val btn_getReading: Button = view.findViewById(R.id.btn_getReading)
        val blinkanimation =
            AlphaAnimation(1F, 0F) // Change alpha from fully visible to invisible

        blinkanimation.duration = 300 // duration - half a second

        blinkanimation.interpolator = LinearInterpolator() // do not alter animation rate

        blinkanimation.repeatCount = 3 // Repeat animation infinitely

        blinkanimation.repeatMode = Animation.REVERSE
        /*val animation1 = AlphaAnimation(0.2f, 1.0f)
        animation1.duration = 1000
        animation1.startOffset = 5000
        animation1.fillAfter = true
        iv.startAnimation(animation1)*/
        iv_first_card.setOnClickListener {

            iv_first_card.setBackgroundResource(R.drawable.tarot_card_selector)
            iv_first_card.startAnimation(blinkanimation)

           // iv_first_card.setElevation(200F)
           // iv_first_card.translationZ =200F

        }
        iv_second_card.setOnClickListener {
            iv_second_card.setBackgroundResource(R.drawable.tarot_card_selector)


        }
        iv_Third_card.setOnClickListener {
            iv_Third_card.setBackgroundResource(R.drawable.tarot_card_selector)
            val animation1 = AlphaAnimation(0.2f, 1.0f)
            animation1.duration = 500
            iv_Third_card.setAlpha(1f)
            iv_Third_card.startAnimation(animation1)

        }
        btn_getReading.setOnClickListener {
            var transaction: FragmentTransaction =
                myActivity.supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.frame_layout,
                TarotCardResult.newInstance(null),
                TarotCardResult.tagName
            )
            transaction.addToBackStack(TarotCardResult.tagName)
            transaction.commit()
        }

        toolBarSetup()
        return view
    }

    private fun toolBarSetup() {

        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity!!.runOnUiThread { toolbar.title = "TarotCard Reading" }

        myActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
        myActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false);
        myActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true);
        toolbar.btn_recharge.visibility=View.GONE
        toolbar.ib_notification.visibility=View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TarotCardReading.
         */

        internal val tagName
            get() = TarotCardReading::class.java.name

        fun newInstance(bundle: Bundle?): TarotCardReading {
            val fragment = TarotCardReading()
            fragment.arguments = bundle
            return fragment
        }
    }
}