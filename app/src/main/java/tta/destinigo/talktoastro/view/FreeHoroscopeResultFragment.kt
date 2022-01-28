package tta.destinigo.talktoastro.view


import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.FreeHoroscopeModel


class FreeHoroscopeResultFragment : tta.destinigo.talktoastro.BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_free_horoscope_result

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
        return inflater.inflate(R.layout.fragment_free_horoscope_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = "Your horoscope"
        toolbar.setNavigationOnClickListener {
            tta.destinigo.talktoastro.BaseFragment.popBackStack(myActivity)
        }
        val freeHoroscopeModel = arguments?.getParcelable<FreeHoroscopeModel>("freehoroscopemodel") as FreeHoroscopeModel
//        txv_ascendant_val.text = freeHoroscopeModel.ascendant
//        txv_ascendant_lord_val.text = freeHoroscopeModel.ascendant_lord
//        txv_varna_val.text = freeHoroscopeModel.Varna
//        txv_vashya_val.text = freeHoroscopeModel.Vashya
//        txv_yoni_val.text = freeHoroscopeModel.Yoni
//        txv_gan_val.text = freeHoroscopeModel.Gan
//        txv_nadi_val.text = freeHoroscopeModel.Nadi
//        txv_signLord_val.text = freeHoroscopeModel.SignLord
//        txv_sign_val.text = freeHoroscopeModel.sign
//        txv_naksahtra_val.text = freeHoroscopeModel.Naksahtra
//        txv_NaksahtraLord_val.text = freeHoroscopeModel.NaksahtraLord
//        txv_charan_val.text = freeHoroscopeModel.Charan.toString()
//        txv_yog_val.text = freeHoroscopeModel.Yog
//        txv_karan_val.text = freeHoroscopeModel.Karan
//        txv_tithi_val.text = freeHoroscopeModel.Tithi
//        txv_yunja_val.text = freeHoroscopeModel.yunja
//        txv_tatva_val.text = freeHoroscopeModel.tatva
//        txv_name_alphabet_val.text = freeHoroscopeModel.name_alphabet
//        txv_paya_val.text = freeHoroscopeModel.paya

    }

    companion object {
        internal val tagName
        get() = FreeHoroscopeResultFragment::class.java.name
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle?): FreeHoroscopeResultFragment {
                val fragment = FreeHoroscopeResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
