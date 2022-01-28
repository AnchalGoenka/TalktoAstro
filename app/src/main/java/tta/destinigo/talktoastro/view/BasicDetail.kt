package tta.destinigo.talktoastro.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_basic_details.*
import kotlinx.android.synthetic.main.fragment_basic_details.view.*
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.FreeHoroscopeModel
import tta.destinigo.talktoastro.util.ApplicationUtil

/**
 * Created by Anchal
 */
class BasicDetail : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_basic_details

    override fun getToolbarId(): Int {
        return 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // val bundle = myActivity.intent.getBundleExtra("freehoroscopemodel")
        //  Toast.makeText(ApplicationUtil.getContext(),""+bundle, Toast.LENGTH_SHORT).show()
       // val bundle = intent.extras




    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_basic_details, container, false)
        val freeHoroscopeModel=arguments?.getParcelable<FreeHoroscopeModel>("freehoroscopemodel")

        view.txv_ascendant_val.text = freeHoroscopeModel?.ascendant
        view.txv_ascendant_lord_val.text = freeHoroscopeModel?.ascendant_lord
        view.txv_varna_val.text = freeHoroscopeModel?.Varna
        view.txv_vashya_val.text = freeHoroscopeModel?.Vashya
        view.txv_yoni_val.text = freeHoroscopeModel?.Yoni
        view.txv_gan_val.text = freeHoroscopeModel?.Gan
        view.txv_nadi_val.text = freeHoroscopeModel?.Nadi
        view.txv_signLord_val.text = freeHoroscopeModel?.SignLord
        view.txv_sign_val.text = freeHoroscopeModel?.sign
        view.txv_naksahtra_val.text = freeHoroscopeModel?.Naksahtra
        view.txv_NaksahtraLord_val.text = freeHoroscopeModel?.NaksahtraLord
        view.txv_charan_val.text = freeHoroscopeModel?.Charan.toString()
        view.txv_yog_val.text = freeHoroscopeModel?.Yog
        view.txv_karan_val.text = freeHoroscopeModel?.Karan
        view.txv_tithi_val.text = freeHoroscopeModel?.Tithi
        view.txv_yunja_val.text = freeHoroscopeModel?.yunja
        view.txv_tatva_val.text = freeHoroscopeModel?.tatva
        view.txv_name_alphabet_val.text = freeHoroscopeModel?.name_alphabet
        view.txv_paya_val.text = freeHoroscopeModel?.paya
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // Toast.makeText(myActivity,"abc"+arguments?.getParcelable("freehoroscopemodel"), Toast.LENGTH_LONG).show()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnswerFragment.
         */

        internal val tagName: String
            get() = BasicDetail::class.java.name



        fun newInstance(bundle: Bundle?): BasicDetail {
            val fragment = BasicDetail()
            fragment.arguments = bundle
            return fragment
        }
    }
}