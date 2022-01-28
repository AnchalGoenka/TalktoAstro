package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gene_ascendant_report.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.R

/**
 * Created by Anchal
 */
class GeneAscendantReport : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_gene_ascendant_report, container, false)
        val jsonObject=arguments?.getString("AscendantReport")
        Log.d("AscendantReport", jsonObject.toString())
        val geneAscendantReport = JSONObject(arguments?.getString("AscendantReport").toString())
        view.txv_ascendant.text="Your Ascendant Is : " + geneAscendantReport.getJSONObject("asc_report").get("ascendant").toString()
        view.txv_ascendant_report.text=geneAscendantReport.getJSONObject("asc_report").get("report").toString()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GeneAscendantReport.
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