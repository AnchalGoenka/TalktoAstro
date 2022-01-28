package tta. destinigo.talktoastro.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_free_horoscope.*
import kotlinx.android.synthetic.main.fragment_freekundli.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.KundliDetails
import tta.destinigo.talktoastro.adapter.GeoDetailAdapter
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.viewmodel.FreeHoroscopeViewModel
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
/**
 * Created by Anchal
 */

class Freekundli : BaseFragment() {

    var freeHoroscopeViewModel: FreeHoroscopeViewModel? = null
    private lateinit var GeoDetailAdapter: GeoDetailAdapter
    private lateinit var current: Freekundli
    private  var seq =""
    var geo_list: androidx.recyclerview.widget.RecyclerView? = null
    var bundle = Bundle()
    var isclick:Boolean=false
    val json = JSONObject()
    var year_: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 0
    var min: Int = 0
    var latitude : Float= 0.0F
    var longitude : Float =0.0F

    lateinit var birthPlace: EditText
    override val layoutResId: Int
        get() = R.layout.fragment_freekundli


    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_freekundli, container, false)
        toolBarSetup()
        val btngetKundliDetails: Button = view.findViewById(R.id.btn_getKundli)
        val ivCalendar: ImageView = view.findViewById(R.id.imageCalendar)
        val ivtime: ImageView = view.findViewById(R.id.imagetime)
        val date:TextView = view.findViewById(R.id.txv_date)
        val time:TextView =view.findViewById(R.id.tv_time)
        birthPlace=view.findViewById(R.id.ed_birth_place)
        geo_list = view.findViewById(R.id.rv_placename)

        ivCalendar.setOnClickListener { datePicker() }
        ivtime.setOnClickListener { showTimePicker() }
        date.setOnClickListener { datePicker() }
        time.setOnClickListener { showTimePicker() }


        freeHoroscopeViewModel  = ViewModelProviders.of(this, FreeHoroscopeFragment.MyViewModelFactory(myApplication)).get(FreeHoroscopeViewModel::class.java)
        btngetKundliDetails.setOnClickListener {

            if (ed_name_process_report.text.isEmpty()||txv_date.text == "dd/mm/yyyy" || tv_time.text == "00:00AM" || ed_birth_place.text.isEmpty()){

                Toast.makeText(ApplicationUtil.getContext(), "All fields are mandatory.", Toast.LENGTH_LONG)
                    .show()
            }

            else{
                showProgressBar("Loading")
                val timezonejson=JSONObject()
                timezonejson.put("latitude",latitude)
                timezonejson.put("longitude",longitude)
                timezonejson.put("date",txv_date.text)
                freeHoroscopeViewModel?.getTimezone(timezonejson)
               // showProgressBar("Loading...")
                json.put("day", day)
                json.put("month", month)
                json.put("year", year_)
                json.put("hour", hour)
                json.put("min", min)
                json.put("lat",latitude)
                json.put("lon",longitude)

                freeHoroscopeViewModel?.getLatLongWithTimezone(json, ed_birth_place.text.toString(), txv_date.text.toString())
                freeHoroscopeViewModel?.birthDetails?.observe(this, androidx.lifecycle.Observer {
                    bundle.putString("birthDetails",it)
                    bundle.putString("name",ed_name_process_report.text.toString())
                })
                freeHoroscopeViewModel?.geneAscendantReport?.observe(this, androidx.lifecycle.Observer {
                    bundle.putString("AscendantReport",it)
                })
                freeHoroscopeViewModel?.chart?.observe(this, androidx.lifecycle.Observer {
                    bundle.putString("svg",it)
                })
                freeHoroscopeViewModel?.arrayMutableFreeHoroscope?.observe(this, androidx.lifecycle.Observer {
                    hideProgressBar()

                    bundle.putParcelable("freehoroscopemodel", it)
                    val intent =
                        Intent(ApplicationUtil.getContext(), KundliDetails::class.java)
                   // intent.put
                       intent.putExtras(bundle);
                       startActivity(intent)
                })

            }

        }
        current = this
        birthPlace.setOnTouchListener(OnTouchListener { v, event ->
            birthPlace.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                   // geo_list?.visibility=View.GONE
                }else{
                   // Toast.makeText(ApplicationUtil.getContext(),"length", Toast.LENGTH_SHORT).show()
                    geo_list?.visibility=View.VISIBLE
                    birthPlace.addTextChangedListener(textWatcher)

                }
            })
            false
        })


        return view
    }


    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            if(s.length < 3){
                geo_list?.visibility=View.GONE
            } else if(s.length>=3){
                geo_list?.visibility=View.VISIBLE
                geo()
            }

        }
    }

    fun geo(){

            freeHoroscopeViewModel?.getgeo(ed_birth_place.text.toString())
            freeHoroscopeViewModel?.arrayGeoDetails?.observe(this, androidx.lifecycle.Observer {

                geo_list?.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
                GeoDetailAdapter = GeoDetailAdapter(it) { geoname, i ->
                    birthPlace.text =
                        Editable.Factory.getInstance().newEditable(geoname.placeName.toString())
                    val lat = geoname.latitude.toString()
                    latitude = lat.toFloat()
                    longitude = geoname.longitude.toString().toFloat()
                    geo_list?.visibility = View.GONE

                  //  input.removeTextChangedListener(textWatcher);
                    //input.hideKeyboard()
                }
                geo_list?.adapter = GeoDetailAdapter
                if (geo_list?.layoutManager == null) {
                    geo_list?.addItemDecoration(MarginItemDecorator(1))
                }
                geo_list?.hasFixedSize()
                GeoDetailAdapter.notifyDataSetChanged()
            })
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun toolBarSetup() {

        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "Free kundli"
        myActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
        toolbar.btn_recharge.visibility=View.GONE
        toolbar.ib_notification.visibility=View.GONE
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
            txv_date.text = "${monthOfYear + 1}-$dayOfMonth-$year"
        }, year_, month, day)
        dpd.show()
    }
    private fun showTimePicker() {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
         hour = c.get(Calendar.HOUR_OF_DAY)
        min = c.get(Calendar.MINUTE)
        // date picker dialog
        /* val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
             c.set(Calendar.HOUR_OF_DAY, hour)
             c.set(Calendar.MINUTE, minute)
          val    mSelectedTime = SimpleDateFormat("HH:mm").format(c.time)
             tv_time.text = mSelectedTime
         }
         val timePickerDialog = TimePickerDialog(requireContext(),TimePickerDialog.THEME_HOLO_DARK, timeSetListener, hour, minute,
             false)
         timePickerDialog.show()*/
        /*val nTimePickerDialog = TimePickerDialog(
            myActivity, 2,
            OnTimeSetListener { pTimePicker, hr, min ->
                tv_time.text= MessageFormat.format("{0}:{1}", hr, min)
                TextView.setText(
                    MessageFormat.format(
                        "{0}:{1}",
                        hr,
                        min
                    )
                )
            },
            hour, minute, false
        )
        nTimePickerDialog.setTitle("Select Intervals")
        nTimePickerDialog.show()*/
        val picker =
            TimePickerDialog(
                myActivity, android.R.style.Theme_Holo_Light_Dialog,
                OnTimeSetListener { tp, sHour, sMinute ->
                    //eText.setText(sHour + ":" + sMinute);
                    c.set(Calendar.HOUR_OF_DAY, sHour)
                    c.set(Calendar.MINUTE, sMinute)
                    hour = sHour
                    min = sMinute
                   /* val    mSelectedTime = SimpleDateFormat("HH:mm").format(c.time)
                    tv_time.text = mSelectedTime*/
                    tv_time.text= MessageFormat.format("{0}:{1}", sHour, sMinute)
                  //  tv_time.text="$sHour :  $sMinute"
                }, hour, min, false
            )
        picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        picker.show()
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
                tv_time.setText(
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Freekundli.
         */

        internal val tagName
            get() = Freekundli::class.java.name

        fun newInstance(bundle: Bundle?): Freekundli {
            val fragment = Freekundli()
            fragment.arguments = bundle
            return fragment
        }
    }
}