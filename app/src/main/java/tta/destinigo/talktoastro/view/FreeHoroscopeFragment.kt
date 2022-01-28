package tta.destinigo.talktoastro.view

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.viewmodel.FreeHoroscopeViewModel
import org.json.JSONObject
import java.util.*
import android.app.TimePickerDialog
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.widget.Toast
import tta.destinigo.talktoastro.util.ApplicationUtil
import kotlinx.android.synthetic.main.fragment_free_horoscope.*

class FreeHoroscopeFragment : tta.destinigo.talktoastro.BaseFragment() {

    var freeHoroscopeViewModel: FreeHoroscopeViewModel? = null
    val json = JSONObject()
    var year_: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 0
    var min: Int = 0

    override val layoutResId: Int
        get() = R.layout.fragment_free_horoscope

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
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = "Check horoscope"
        toolbar.setNavigationOnClickListener { popBackStack(myActivity) }


        freeHoroscopeViewModel  = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(FreeHoroscopeViewModel::class.java)
        btn_horoscope_submit.setOnClickListener {
            if (txv_horoscope_birth_date_val.text == "dd/mm/yyyy" || txv_horoscope_birth_time_val.text == "hh:mm" || txv_place_horoscope.text.isEmpty()){
                Toast.makeText(ApplicationUtil.getContext(), "All fields are mandatory.", Toast.LENGTH_LONG)
                    .show()
            }
            else {
                showProgressBar("Loading...")
                json.put("day", day.toString())
                json.put("month", month.toString())
                json.put("year", year_.toString())
                json.put("hour", hour.toString())
                json.put("min", min.toString())

                
                freeHoroscopeViewModel?.getLatLongWithTimezone(json, ed_place.text.toString(),
                    txv_horoscope_birth_date_val.text.toString())
                freeHoroscopeViewModel?.arrayMutableFreeHoroscope!!.observe(this, Observer {
                    hideProgressBar()
                    var bundle = Bundle()
                    bundle.putParcelable("freehoroscopemodel", it)
                    var transaction: FragmentTransaction = myActivity.supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.frame_layout,
                        FreeHoroscopeResultFragment.newInstance(bundle),
                        FreeHoroscopeResultFragment.tagName
                    )
                    transaction.addToBackStack(FreeHoroscopeResultFragment.tagName)
                    transaction.commit()
                })
            }
        }

    }

    fun datePicker(){
        val c = Calendar.getInstance()
        year_ = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(myActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in Toast
            year_ = year
            month = monthOfYear+1
            day = dayOfMonth
            txv_horoscope_birth_date_val.text = "$dayOfMonth / ${monthOfYear + 1} / $year"
        }, year_, month, day)
        dpd.show()
    }

    fun timePicker(){
        val mcurrentTime = Calendar.getInstance()
        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        min = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(myActivity,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                min = selectedMinute
                var AM_PM = "AM"
                if(selectedHour < 12) {
                    AM_PM = "AM"
                } else {
                    AM_PM = "PM"
                }
                txv_horoscope_birth_time_val.setText(
                    "$selectedHour:$selectedMinute $AM_PM"
                )
            }, hour, min, false
        )//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FreeHoroscopeViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName
        get() = FreeHoroscopeFragment::class.java.name
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(bundle: Bundle?): FreeHoroscopeFragment{
            val fragment = FreeHoroscopeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
