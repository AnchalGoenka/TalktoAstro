package tta.destinigo.talktoastro.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_chat_form.*
import kotlinx.android.synthetic.main.fragment_process_report.btn_submit
import kotlinx.android.synthetic.main.fragment_process_report.editText3
import kotlinx.android.synthetic.main.fragment_process_report.radioGroup_process_report
import kotlinx.android.synthetic.main.fragment_process_report.txv_birth_place_process_report
import kotlinx.android.synthetic.main.fragment_process_report.txv_date
import kotlinx.android.synthetic.main.fragment_process_report.txv_name_process_report
import kotlinx.android.synthetic.main.fragment_process_report.txv_question
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.viewmodel.ProcessReportViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

class ProcessReportFragment : tta.destinigo.talktoastro.BaseFragment() {

    var processReportViewModel: ProcessReportViewModel? = null
    var abc = "";


    override val layoutResId: Int
        get() = R.layout.fragment_chat_form

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
        processReportViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(
            ProcessReportViewModel::class.java
        )
        /*var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Birth Details - ${
                SharedPreferenceUtils.readString(
                    ApplicationConstant.REPORTCHECKED, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
            }", true, R.drawable.ic_arrow_back_24dp
        )
        toolbar.layout_btn_recharge.visibility = View.GONE
        toolbar.ib_notification.visibility=View.GONE
        toolbar.setNavigationOnClickListener {
            SharedPreferenceUtils.put(
                ApplicationConstant.REPORTCHECKED,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            popBackStack(myActivity)
        }*/
        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        myActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
        toolbar.title = "Report Form"
        toolbar.ib_notification.visibility=View.GONE
        toolbar.layout_btn_recharge.visibility=View.GONE
//        initializeBirthDateList(spinner_minutes, spinner_am_pm, spinner_hours)
        val price = arguments?.getString("price")
        val serviceID = arguments?.getString("id")
        val astroID = arguments?.getString("astroID")
        val astroName = arguments?.getString("ThankYouName") as String
        val serviceName =arguments?.getString("seviceName") as String

        txv_date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                myActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in Toast
                    txv_date.text = "$dayOfMonth / ${monthOfYear + 1} / $year"
                },
                year,
                month,
                day
            )
            dpd.getDatePicker().setMaxDate(c.timeInMillis);
            dpd.show()
        }

        var hour: Int = 0
        var min: Int = 0
        var finalMin: String = ""
//        txv_process_report_birth_time_val.setOnClickListener {
//            val mcurrentTime = Calendar.getInstance()
//            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
//            min = mcurrentTime.get(Calendar.MINUTE)
//            val mTimePicker: TimePickerDialog
//            mTimePicker = TimePickerDialog(myActivity,
//                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
//                    var AM_PM = "AM"
//                    if(selectedHour < 12) {
//                        AM_PM = "AM"
//                    } else {
//                        var selectedHr = selectedHour - 12
//                        hour = selectedHr
//                        if (hour == 0){AM_PM = "AM"} else{AM_PM = "PM"}
//                    }
//                    if (selectedMinute < 10) {finalMin = "0$selectedMinute" } else {
//                        finalMin = "$selectedMinute"
//                    }
//
//                    txv_process_report_birth_time_val.setText(
//                        "$hour:$finalMin $AM_PM"
//                    )
//                }, hour, min, false
//            )//Yes 24 hour time
//            mTimePicker.setTitle("Select Time")
//            mTimePicker.show()
//        }

        btn_submit.setOnClickListener {
//            Log.d("validation", "birth time" + time_dob.text);
//            Log.d("validation", "edit text" + editText3.text);
            if (txv_name_process_report.text.isNotEmpty() && radioGroup_process_report.checkedRadioButtonId.toString()
                    .isNotEmpty() &&
                txv_birth_place_process_report.text.isNotEmpty() && txv_date.text != "dd/mm/yyyy" && editText3.text.isNotEmpty()
                && !time_dob.equals("00:00AM")/* &&
                spinner_minutes.selectedItem.toString() != "MM" && spinner_hours.selectedItem.toString() != "HH"*/) {

                var time = time_dob.text
                /*var minutes = spinner_minutes.selectedItem.toString()
                if (spinner_minutes.selectedItem.toString().toInt() < 10) {
                    minutes = "0${spinner_minutes.selectedItem}"
                }
                if (spinner_am_pm.selectedItem.toString() == "AM") {
                    time = spinner_hours.selectedItem.toString() + ":" + minutes + " AM"
                } else {
                    time = spinner_hours.selectedItem.toString() + ":" + minutes + " PM"
                }*/
                var gender: String = ""
                if (radioGroup_process_report.checkedRadioButtonId == 1) {
                    gender = "male"
                } else {
                    gender = "female"
                }

                var jsonObj = HashMap<String, String>()

                jsonObj.put("name", txv_name_process_report.text.toString())
                jsonObj.put("gender", gender)
                jsonObj.put("birthPlace", txv_birth_place_process_report.text.toString())
                jsonObj.put("birthDate", txv_date.text.toString())
                jsonObj.put("birthTime", time.toString())
                jsonObj.put("question", editText3.text.toString())
                jsonObj.put("price", price.toString())
                jsonObj.put("serviceName", serviceName.toString())
                jsonObj.put("serviceID", serviceID.toString())
                jsonObj.put(
                    "userID",
                    SharedPreferenceUtils.readString(
                        ApplicationConstant.USERID,
                        "",
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    ).toString()
                )
                jsonObj.put("astroID", astroID.toString())

                var bundle = Bundle()
                bundle.putSerializable("report", jsonObj)
                bundle.putString("ThankYouName", astroName)
                var transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.frame_layout,
                    MyReportFragment.newInstance(bundle),
                    MyReportFragment.tagName
                )
                transaction.addToBackStack(MyReportFragment.tagName)
                transaction.commit()
            }
            else if(txv_name_process_report.text.isEmpty() ){
                txv_name_process_report.setFocusable(true);
                txv_name_process_report.requestFocus();
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Enter Full Name",
                    Toast.LENGTH_SHORT
                ).show()}
            else if(txv_name_process_report.text.length<2){
                txv_name_process_report.setFocusable(true);
                txv_name_process_report.requestFocus();
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Enter Valid Full Name",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(txv_birth_place_process_report.text.isEmpty() ){
                txv_birth_place_process_report.setFocusable(true);
                txv_birth_place_process_report.requestFocus();
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Enter Birth Place",
                    Toast.LENGTH_SHORT
                ).show()}

            else if( radioGroup_process_report.checkedRadioButtonId == -1 ){

            Toast.makeText(
                ApplicationUtil.getContext(),
                "Please Select Gender",
                Toast.LENGTH_SHORT
            ).show()}
            else if( txv_date.text == "dd/mm/yyyy" ){

                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Enter Birth Date",
                    Toast.LENGTH_SHORT
                ).show()}

            else if( time_dob.text == "00:00AM" ){

                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Enter Birth Time",
                    Toast.LENGTH_SHORT
                ).show()}

           else if(editText3.text.isEmpty() ){
                editText3.requestFocus();
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "Please Write your Question",
                    Toast.LENGTH_SHORT
                ).show()}
            else {
                Toast.makeText(
                    ApplicationUtil.getContext(),
                    "All field are mandatory.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        time_dob.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                { timePicker, selectedHour, selectedMinute ->
                    try {
                        val f: NumberFormat = DecimalFormat("00")
                        val formattedHours = f.format(selectedHour)
                        val formattedMinutes = f.format(selectedMinute)
                        var AM_PM = ""
                        if (selectedHour < 12)
                            AM_PM = "AM"
                        else
                            AM_PM = "PM"

                        time_dob.setText("" + formattedHours + " : " + formattedMinutes + " " + AM_PM)
                    } catch (e: Exception) {
                        var AM_PM = ""
                        if (selectedHour < 12)
                            AM_PM = "AM"
                        else
                            AM_PM = "PM"
                        time_dob.setText("" + selectedHour + " : " + selectedMinute + " " + AM_PM)
                    }
                },
                hour,
                minute,
                true
            ) //Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }
    }

    fun initializeBirthDateList(
        spinner_minutes: Spinner,
        spinner_am_pm: Spinner,
        spinner_hours: Spinner
    ) {
        val listHours = listOf("HH", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val listMinutes = listOf(
            "MM", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37",
            "38",
            "39", "40", "41",
            "42", "43", "44", "45", "46",
            "47", "48", "49", "50", "51", "52",
            "53", "54", "55", "56", "57", "58", "59"
        )
        val adapterHours =
            ArrayAdapter(ApplicationUtil.getContext(), R.layout.spinner_item, listHours)
        adapterHours.setDropDownViewResource(R.layout.spinner_item)
        spinner_hours.adapter = adapterHours
        spinner_hours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // either one will work as well
                val item = parent.getItemAtPosition(position) as String
                spinner_hours.setSelection(position)
                //adapterHours.notifyDataSetChanged()
                //val item = adapterHours.getItem(position)
            }
        }

        val adapterMinutes =
            ArrayAdapter(ApplicationUtil.getContext(), R.layout.spinner_item, listMinutes)
        adapterMinutes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_minutes.adapter = adapterMinutes
        spinner_minutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // either one will work as well
                val item = parent.getItemAtPosition(position) as String
                adapterMinutes.notifyDataSetChanged()
            }
        }

        val adapter =
            ArrayAdapter(ApplicationUtil.getContext(), R.layout.spinner_item, listOf("AM", "PM"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_am_pm.adapter = adapter
        spinner_am_pm.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // either one will work as well
                val item = parent.getItemAtPosition(position) as String
                adapter.notifyDataSetChanged()
            }
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProcessReportViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName: String
            get() = ProcessReportFragment::class.java.name

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ProcessReportFragment {
            val fragment = ProcessReportFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
