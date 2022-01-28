package tta.destinigo.talktoastro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_birth_details.view.*
import org.json.JSONArray
import org.json.JSONObject
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil


/**
 * Created by Anchal
 */
class BirthDetails : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_birth_details, container, false)
       // var jssonobject=JsonParser.parseString(arguments?.getString("birthDetails"))
      //  val jsonObject =JsonObject()
        val array = JSONObject(arguments?.getString("birthDetails"))
        view.txv_year_val.text=array.get("year").toString()
        view.txv_Month_val.text=array.get("month").toString()
        view.txv_Day_val.text=array.get("day").toString()
        view.txv_Hour_val.text=array.get("hour").toString()
        view.txv_Minute_val.text=array.get("minute").toString()
        view.txv_Latitude_val.text=array.get("latitude").toString()
        view.txv_Longitude_val.text=array.get("longitude").toString()
        view.txv_Timezone_val.text=array.get("timezone").toString()
        view.txv_Sun_Rise_val.text=array.get("sunrise").toString()
        view.txv_Sunset_val.text=array.get("sunset").toString()
        view.txv_Ayanamsha_val.text=array.get("ayanamsha").toString()
        view.txv_Name_val.text=arguments?.getString("name")

        return view
    }

    companion object {

        internal val tagName: String
            get() = BasicDetail::class.java.name

        fun newInstance(bundle: Bundle?): BasicDetail {
            val fragment = BasicDetail()
            fragment.arguments = bundle
            return fragment
        }
    }
}