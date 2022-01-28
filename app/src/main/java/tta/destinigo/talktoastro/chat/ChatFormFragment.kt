package tta.destinigo.talktoastro.chat

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.awesomedialog.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.fragment_chat_form.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.jetbrains.anko.startService
import org.json.JSONObject
import tour.traveling.travel.ui.product.ComounViewModel
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.chat.services.ChatFormViewModel
import tta.destinigo.talktoastro.chat.services.RegistrationIntentService
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.model.request.SendNotiRequest
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.LogUtils
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import tta.destinigo.talktoastro.view.WriteReviewFragment.Companion.astroId
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ChatFormFragment : BaseFragment() {

    private lateinit var chatFormViewModel: ChatFormViewModel
    val mViewModel by lazy { ViewModelProviders.of(this).get(ComounViewModel::class.java) }
    var chatId = ""
    var duration = ""
    var astroID = ""
    lateinit var jsonObj :JSONObject

    override val layoutResId: Int
        get() = R.layout.fragment_chat_form

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onPause() {
        super.onPause()
        btn_submit.isEnabled = true
        hideProgressBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (checkPlayServices()) {
//            fcmAvailable.isChecked = true
            // Start IntentService to register this application with GCM
            ApplicationUtil.getContext().startService<RegistrationIntentService>()
        }
        return inflater.inflate(R.layout.fragment_chat_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatFormViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(
            ChatFormViewModel::class.java
        )
        var toolbar = ApplicationUtil.setToolbarTitleAndRechargeButton(
            "Chat Form",
            true,
            R.drawable.ic_arrow_back_24dp
        )
        toolbar.layout_btn_recharge.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            popBackStack(myActivity)
        }
//        initializeBirthDateList(spinner_minutes, spinner_am_pm, spinner_hours)
        val astroID = arguments?.getString("astroID")
        astroId= astroID.toString()
        val astroName = arguments?.getString("astroName")

        checkUserForm()
        chatFormViewModel.chatFormSubmitSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          hideProgressBar()

            chatId = it.chatID.toString()
            duration = it.duration.toString()
            Log.d("chatFormApi","Duration: ${it.duration}")
            callFcmNotification(chatId)
            // callSendNotificationApi(astroID.toString())
            startChat(astroID!!, astroName!!, duration, chatId)
         // BaseApplication.instance.showToast(it.chatID.toString())

            })
        chatFormViewModel.astrochatStatus.observe(viewLifecycleOwner, Observer {
            myActivity.runOnUiThread {
                if (it == ApplicationConstant.ONLINE) {
                    Log.d("jsonObject",jsonObj.toString())
                    chatFormViewModel.submitChatForm(jsonObj)
                }
                else {
                    AwesomeDialog.build(myActivity)
                        .title("Astrologer is Busy " )
                        .body("Please try after sometime!! or You can chat with another Astrologer!!\n" )
                        .icon(R.mipmap.ic_launcher)
                        .onPositive("Okay", buttonBackgroundColor = R.drawable.button_rounded_corners_green, textColor = ContextCompat.getColor(
                            myActivity,
                            android.R.color.white
                        )) {
                            Log.d("TAG", "positive ")

                            val intent =
                                Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                            startActivity(intent)

                        }
                        .position(AwesomeDialog.POSITIONS.CENTER)
                }
            }
        })
        chatFormViewModel.chatFormSubmitFailure.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                btn_submit.isEnabled = true
                ApplicationUtil.showDialog(myActivity, it)
            })

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
            dpd.show()
        }

        btn_submit.setOnClickListener {

            val balance = SharedPreferenceUtils.readString(
                ApplicationConstant.BALANCE, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))

            try {
               /* if (balance!!.toInt() <= resources.getInteger(R.integer.min_user_balance)) {
                    ApplicationUtil.showDialog(
                        myActivity,
                        "Minimum balance should not be less than Rs 50. Please recharge."
                    )

                }*/
                val phoneCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
                if ( (phoneCode == "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_RUPEE) ||
                    (phoneCode != "91" && balance!!.toInt() < ApplicationConstant.MINIMUM_BALANCE_DOLLAR) ) {


                    try {
                        if (phoneCode == "91") {
                            ApplicationUtil.showDialog(
                                myActivity,
                                "Minimum balance should not be less than Rs 50. Please recharge."
                            )

                        }else{
                            ApplicationUtil.showDialog(
                                myActivity,
                                "Minimum balance should not be less than $7. Please recharge."
                            )

                        }
                    } catch (e: Exception) {

                    }
                }else {
                    btn_submit.isEnabled = false
                    showProgressBar("Loading")
                    if (txv_name_process_report.text.isNotEmpty() && radioGroup_process_report.checkedRadioButtonId.toString()
                            .isNotEmpty() &&
                        txv_birth_place_process_report.text.isNotEmpty() && txv_date.text != "dd/mm/yyyy" && txv_question.text.isNotEmpty()
                        && !time_dob.equals("00:00 AM")
//                        spinner_minutes.selectedItem.toString() != "MM" && spinner_hours.selectedItem.toString() != "HH"
                    ) {

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
                        if (option_male.isChecked) {
                            gender = "male"
                        } else {
                            gender = "female"
                        }
//comment
                        jsonObj = JSONObject()

                        jsonObj.put(
                            "userID",
                            SharedPreferenceUtils.readString(
                                ApplicationConstant.USERID,
                                "",
                                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                            )!!
                        )
                        jsonObj.put("astroID", astroID)
                        jsonObj.put("name", txv_name_process_report.text.toString())
                        jsonObj.put("gender", gender)
                        jsonObj.put("dateofbirth", txv_date.text.toString())
                        jsonObj.put("timeofbirth", time.toString())
                        jsonObj.put("placeofbirth", txv_birth_place_process_report.text.toString())
                        jsonObj.put("countryofbirth", "India")
                        jsonObj.put("question", editText3.text.toString())

                        val chatMessage ="<b>Name:</b>${txv_name_process_report.text.toString()}<br/><b>Place : </b>${txv_birth_place_process_report.text.toString()}<br/><b>Country :</b>India<br/><b>Gender :</b>${gender}<br/><b>DOB: </b> ${txv_date.text.toString()}<br/><b>TOB :</b>${time.toString()}<br/><b>Question : </b>${editText3.text.toString()}"
                          //  "Name: ${txv_name_process_report.text.toString()}\nGender: ${gender}\nDate of birth: ${txv_date.text.toString()}\nTime of birth: ${time.toString()}\nPlace of birth: ${txv_birth_place_process_report.text.toString()}\nQuestion: ${editText3.text.toString()}"
                        SharedPreferenceUtils.put(
                            ApplicationConstant.CHATMESSAGE,
                            chatMessage,
                            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                        )

                        //chatFormViewModel.submitChatForm(jsonObj)


                        var json = JSONObject()
                        json.put("astroID",astroId)

                        chatFormViewModel.getAstroChatStatus(json)


                       /* jsonObj = JSONObject()
                        jsonObj.put("astroID", astroID)
                        jsonObj.put("chatStatus", "online")
*/
                       // chatFormViewModel.changeChatStatus(jsonObj)


                    } else {
                        Toast.makeText(
                            ApplicationUtil.getContext(),
                            "All field are mandatory.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
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

    private fun callSendNotificationApi(astroID: String) {
        val req = SendNotiRequest("" + astroID)
        mViewModel.callSendNotification(req)
            .observe(viewLifecycleOwner, object : Observer<TokenModel> {
                override fun onChanged(resp: TokenModel?) {
//                    showLoadingView(false, binding.loadingView.loadingIndicator, binding.loadingView.container)
                    if (resp != null) {

                    } else {

                    }
                }

            })

    }

    fun callFcmNotification(chatID: String){
        chatFormViewModel.fcmNotification(chatID)
        chatFormViewModel.notificationsend.observe(viewLifecycleOwner, Observer {
           // BaseApplication.instance.showToast("$it")
        })
    }

    fun checkUserForm() {
        var json = JSONObject()
        json.put(
            "userID",
            SharedPreferenceUtils.readString(
                ApplicationConstant.USERID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        )
        chatFormViewModel.getChatForm(json)
        chatFormViewModel.arrayListMutableLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                if (it.success != 0) {
                    txv_name_process_report.setText(it.name)
                    txv_birth_place_process_report.setText(it.placeOfBirth)
                    if (it.gender == "male") {
                        option_male.isChecked = true
                    } else {
                        option_female.isChecked = true
                    }
                    txv_date.setText(it.dateOfBirth)
                    editText3.setText(it.question)
                    val index = it.timeOfBirth!!.indexOf(":")
                    val hrs = it.timeOfBirth!!.subSequence(0, index)
                    val min = it.timeOfBirth!!.subSequence(index + 1, index + 3)
                    var AM_PM = ""
                    var intam_pm = 0
                    if (it.timeOfBirth.contains("AM")) {
                        AM_PM = "AM"
                        intam_pm = 0
                    } else {
                        AM_PM = "PM"
                        intam_pm = 1
                    }
//                    spinner_hours.setSelection(intHrs)
//                    spinner_minutes.setSelection(intMins)
//                    spinner_am_pm.setSelection(intam_pm)

//                    time_dob.setText("" + intHrs + " : " + intMins + " " + AM_PM)

                    try {

                        val intHrs = hrs.toString().trim().toInt()
                        val intMins = min.toString().trim().toInt() + 1

                        val f: NumberFormat = DecimalFormat("00")
                        val formattedHours = f.format(intHrs)
                        val formattedMinutes = f.format(intMins)
                        var AM_PM = ""
                        if (intHrs < 12)
                            AM_PM = "AM"
                        else
                            AM_PM = "PM"

                        time_dob.setText("" + formattedHours + " : " + formattedMinutes + " " + AM_PM)
                    } catch (e: Exception) {
                        var AM_PM = ""
                        /*if (intHrs < 12)
                            AM_PM = "AM"
                        else
                            AM_PM = "PM"*/
                        time_dob.setText("" + hrs.toString() + " : " + min.toString() + " " + AM_PM)
                    }

                }
            })
    }

    fun initializeBirthDateList(
        spinner_minutes: Spinner,
        spinner_am_pm: Spinner,
        spinner_hours: Spinner
    ) {
        val listHours = listOf("HH", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val listMinutes = listOf(
            "MM",
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "38",
            "39",
            "40",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "47",
            "48",
            "49",
            "50",
            "51",
            "52",
            "53",
            "54",
            "55",
            "56",
            "57",
            "58",
            "59"
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

    fun startChat(astroId: String, astroName: String, duration: String, chatID: String) {
        MainActivity.instance.updateTokenForChatClient(astroId, astroName, duration, chatID)
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(ApplicationUtil.getContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    myActivity, resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )
                    .show()
            } else {
                LogUtils.d("This device is not supported.")
            }
            return false
        }
        return true
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatFormViewModel(mApplication) as T
        }
    }

    companion object {
        internal val tagName: String
            get() = ChatFormFragment::class.java.name

        private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment SearchPlacesFragment.
         */
        fun newInstance(bundle: Bundle?): ChatFormFragment {
            val fragment = ChatFormFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}
