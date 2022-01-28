package tta.destinigo.talktoastro.chat

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.awesomedialog.*
import com.google.gson.JsonObject
import com.twilio.chat.Channel
import com.twilio.chat.ChannelListener
import com.twilio.chat.Member
import com.twilio.chat.Message
import kotlinx.android.synthetic.main.activity_chat_thank_you.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activities.MessageActivity
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.agora.chat.AgoraMessageActivity
import tta.destinigo.talktoastro.chat.views.ChatThankYouViewModel
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.Constants

class ChatThankYouActivity : BaseActivity(), ChannelListener, AnkoLogger {
    var timer: CountDownTimer? = null
    private var channel: Channel? = null
    private val _instance: ChatThankYouActivity = this
    var check : Int  = 0
    var timerVal: Long = 0L
    private var chatThankYouViewModel: ChatThankYouViewModel? = null
    override val fragmentContainerId: Int
        get() =  R.id.frame_layout
    override val layoutResId: Int
        get() = R.layout.activity_chat_thank_you
    override fun getToolbarId(): Int {
        return  0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_thank_you)
        chatThankYouViewModel =ViewModelProvider(this, MyViewModelFactory(myApplication)).get(ChatThankYouViewModel::class.java)
        val JsonObject =JSONObject()
        JsonObject.put("chatStatus","online")
        JsonObject.put("astroID",intent.getStringExtra(Constants.CHAT_ASTRO_ID))

        chatThankYouViewModel?.joinStatusCheck?.observe(this, Observer {status->
            if(status==true){
                check = 0
                ApplicationUtil.resetTimer()
                finish()
                startActivity<AgoraMessageActivity>(
                    Constants.CHAT_ASTRO_ID to intent.getStringExtra(Constants.CHAT_ASTRO_ID) as Any,
                    Constants.CHAT_ASTRO_NAME to intent.getStringExtra(Constants.CHAT_ASTRO_NAME) as Any,
                    Constants.CHAT_DURATION to intent.getStringExtra(Constants.CHAT_DURATION) as Any,
                    Constants.CHAT_ID to intent.getStringExtra(Constants.CHAT_ID) as Any
                )

            }
            else{

                check = 0
               // ApplicationUtil.resetTimer()
                chatThankYouViewModel?.changeChatStatus(JsonObject)
                AwesomeDialog.build(this)
                    .title("Astrologer not reachable " )
                    .body("Please try after sometime!! or You can chat with another Astrologer!!\n" )
                    .icon(R.mipmap.ic_launcher)
                    .onPositive("Okay", buttonBackgroundColor = R.drawable.button_rounded_corners_green, textColor = ContextCompat.getColor(
                        this,
                        android.R.color.white
                    )) {
                        Log.d("TAG", "positive ")

                        val intent =
                            Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                        startActivity(intent)

                    }
                    .position(AwesomeDialog.POSITIONS.CENTER)
            }

        })

        createUI()
    }

    override fun onDestroy() {
        channel?.removeListener(this@ChatThankYouActivity);
        check = 0
        finish()
        super.onDestroy()
    }

   fun checkjoinStatus(){
       val chatId =intent.getStringExtra(Constants.CHAT_ID) as Any
       chatThankYouViewModel?.checkAstroChatJoinStatus(chatId.toString())

   }

    override fun onResume() {
        super.onResume()
        if (intent != null) {
            channel = intent.getParcelableExtra<Channel>(Constants.EXTRA_CHANNEL)
        }

        if (timer == null) {
            val timeInMilliSeconds = ApplicationUtil.getTimerValue()
            setTimer(timeInMilliSeconds)
        }
       check= 1
        val ha = Handler()
        ha.postDelayed(object : Runnable {
            override fun run() {
                //call function
                if(check==1 ){
                checkjoinStatus()
                ha.postDelayed(this, 5000)}
            }
        }, 5000)
    }

    fun setTimer(timeInMilliSeconds: Long) {
        runOnUiThread {
            timer = object : CountDownTimer(timeInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerVal = millisUntilFinished

                val Mmin: Long = millisUntilFinished / 1000 / 60
                val Ssec: Long = millisUntilFinished / 1000 % 60
                if (Ssec < 10) {
                    textView_chat_timer.setText("$Mmin:0$Ssec")
                } else textView_chat_timer.setText("$Mmin:$Ssec")

            }

            override fun onFinish() {
                textView_chat_timer.setText("00:00")
                timerVal = 0L

                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(this@ChatThankYouActivity)


                // set dialog message
                alertDialogBuilder
                    .setMessage("Astrologer seems unavailable currently, please try with another astrologer from list")
                    .setCancelable(true)
                    .setPositiveButton("OK") { _, _ ->
                        val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                        startActivity(intent)
                        ApplicationUtil.resetTimer()

                    }
                alertDialogBuilder.show()
            }
        }
            timer?.start() }

    }

    override fun onPause() {
        if (timerVal != 0L) {
            ApplicationUtil.setTimerValue(timerVal)
        }
        timer?.cancel()
        timer = null
        super.onPause()
    }

    public fun getInstance(): ChatThankYouActivity {
        return _instance
    }

    private fun createUI() {
        if (intent != null) {
            try {
               // val basicClient = BaseApplication.instance.basicClient
               // val channelSid = intent.getStringExtra(Constants.EXTRA_CHANNEL_SID)
                val astroName = intent.getStringExtra(Constants.CHAT_ASTRO_NAME)
               // val channelsObject = basicClient.chatClient!!.channels
                val res = getResources()
             /*   channelsObject.getChannel(channelSid, ChatCallbackListener<Channel>() {
                    channel = it
                    channel!!.addListener(this@ChatThankYouActivity)
                })*/
                toolbarThankYouActivity.title = "Thank You"
                toolbarThankYouActivity.setNavigationIcon(resources.getDrawable(R.drawable.ic_arrow_back_24dp))
                toolbarThankYouActivity.setNavigationOnClickListener {
                    val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                    intent.putExtra("fragmentNumber", 4)
                    startActivity(intent)
                }
                textView_chat_timer.visibility = View.VISIBLE
                textView_for_initiating_chat.text =
                    res.getString(R.string.for_consulting_with_astrologer)
                textView_time_thankyou.text =
                    String.format(res.getString(R.string.answer_chat_call), astroName)

                setTimer(90000)
            } catch (e: Exception) {
            }
        }
    }

    override fun onMessageAdded(message: Message) {
    }

    override fun onMessageUpdated(message: Message?, reason: Message.UpdateReason) {
    }

    override fun onMessageDeleted(message: Message?) {
    }

    override fun onMemberAdded(member: Member?) {
        if (member != null) {
            ApplicationUtil.resetTimer()
            startActivity<MessageActivity>(
                Constants.EXTRA_CHANNEL to intent.getParcelableExtra<Channel>(Constants.EXTRA_CHANNEL),
                Constants.EXTRA_CHANNEL_SID to intent.getStringExtra(Constants.EXTRA_CHANNEL_SID),
                Constants.CHAT_ASTRO_ID to intent.getStringExtra(Constants.CHAT_ASTRO_ID) as Any,
                Constants.CHAT_ASTRO_NAME to intent.getStringExtra(Constants.CHAT_ASTRO_NAME) as Any,
                Constants.CHAT_DURATION to intent.getStringExtra(Constants.CHAT_DURATION) as Any,
                Constants.CHAT_ID to intent.getStringExtra(Constants.CHAT_ID) as Any
            )
            finish()
        }
    }

    override fun onMemberUpdated(member: Member?, reason: Member.UpdateReason) {
    }

    override fun onMemberDeleted(member: Member?) {

    }

    override fun onTypingStarted(channel: Channel?, member: Member?) {

    }

    override fun onTypingEnded(channel: Channel?, member: Member?) {

    }

    override fun onSynchronizationChanged(channel: Channel) {
    }

    companion object {

    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatThankYouViewModel(mApplication) as T
        }
    }
}
