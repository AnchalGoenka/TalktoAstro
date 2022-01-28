package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R


/**
 * A simple [Fragment] subclass.
 * Use the [TarotCardResult.newInstance] factory method to
 * create an instance of this fragment.
 */
class TarotCardResult : BaseFragment() {

    override val layoutResId: Int
        get() = R.layout.fragment_tarot_card_result

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
        val view = inflater.inflate(R.layout.fragment_tarot_card_result, container, false)
        toolBarSetup()
        return view
    }
    private fun toolBarSetup() {

        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        getActivity()!!.runOnUiThread { toolbar.title = "TarotCard Result" }

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
         * @return A new instance of fragment TarotCardResult.
         */
        internal val tagName
            get() = TarotCardResult::class.java.name

        fun newInstance(bundle: Bundle?): TarotCardResult {
            val fragment = TarotCardResult()
            fragment.arguments = bundle
            return fragment
        }
    }
}