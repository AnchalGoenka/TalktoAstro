package tta.destinigo.talktoastro.agora.chat

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtm.*
import kotlinx.android.synthetic.main.activity_agora_message.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONArray
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activities.ChannelActivity
import tta.destinigo.talktoastro.activities.MessageActivity
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.chat.ChatThankYouActivity
import tta.destinigo.talktoastro.chat.services.ChatFormViewModel
import tta.destinigo.talktoastro.chat.views.ChatThankYouViewModel
import tta.destinigo.talktoastro.service.BroadcastService
import tta.destinigo.talktoastro.util.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AgoraMessageActivity : BaseActivity() {

    private var mRtmClient: RtmClient? = null
    private var a :PeerOnlineState ? =null
    private var mClientListener: RtmClientListener? = null
    private var mMessageBeanList: ArrayList<MessageBean> = ArrayList<MessageBean>()
    private var adapter: MessageAdapter? = null
    private var  mRvMsgList: RecyclerView?=null
    private var mUserId:String? = ""
    private var mPeerId = ""
    private  var chatId = ""
    var time: String ?=""
    var mChatManager: ChatManager? =null
    var timer: CountDownTimer? = null
    var milliSecondsFinished: Long = 0L
    var milliSeconds: Long = 0L
    private lateinit var chatFormViewModel: ChatFormViewModel
    private lateinit var chatThankYouViewModel: ChatThankYouViewModel
    private var agoraMsgViewModel: AgoraMessageActivityViewModel? = null
    var send =false
    override val fragmentContainerId: Int
        get() = R.id.frame_layout
    override val layoutResId: Int
        get() = R.layout.activity_agora_message

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_message)

        showProgressBar("Loading...",true)
         mRvMsgList = myActivity.findViewById(R.id.rv_message_list)
        val intent = intent
       // mUserId = intent.getStringExtra("userId")
        mUserId =  SharedPreferenceUtils.readString(
            ApplicationConstant.USERID,
            "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        mPeerId = intent.getStringExtra(Constants.CHAT_ASTRO_ID)!!.toInt().toString()
        chatId = intent.getStringExtra(Constants.CHAT_ID)!!
     //   BaseApplication.instance.showToast(mUserId.toString())
      //  mPeerId= "330"
        mChatManager = BaseApplication.instance.chatManager



        mRtmClient = mChatManager!!.mrtmClient
        mClientListener = MyRtmClientListener()
        mChatManager!!.registerListener(mClientListener as MyRtmClientListener)
        agoraMsgViewModel = ViewModelProvider(this, MyViewModelFactory(myApplication)).get(AgoraMessageActivityViewModel::class.java)
        chatFormViewModel = ViewModelProviders.of(this, MessageActivity.MyViewModelFactory(BaseApplication.instance)).get(ChatFormViewModel::class.java)
        chatThankYouViewModel= ViewModelProvider(this, ChatThankYouActivity.MyViewModelFactory(myApplication)).get(ChatThankYouViewModel::class.java)

        chatThankYouViewModel.mesageCheck.observe(this, Observer {it ->
             if(it == "ended"){
                 endChat("Astrologer not Reachable please click On Okay Button To End Chat ")
             }

        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            time = current.format(formatter)
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            time = formatter.format(date)

        }
        setToolbar()
        agoraLogin()

        setRecyclerView()

        btn_send.setOnClickListener {

            sendPeerMessage()
            ed_msg.setText("")

        }
        btn_endChat.setOnClickListener {
           endChat("Do you want to end chat?")
        }
     // cancelAllNotifications()

    }
    private fun cancelAllNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun checkjoinStatus(){
        val chatId =intent.getStringExtra(Constants.CHAT_ID) as Any
        chatThankYouViewModel?.checkAstroChatJoinStatus(chatId.toString())

    }

    fun endChat(message :String){
        runOnUiThread {
            val alertDialogBuilder = android.app.AlertDialog.Builder(this)
            alertDialogBuilder.setMessage(message)
                .setCancelable(true)
            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                timer?.cancel()
                val timeSpentInChat = milliSeconds - milliSecondsFinished
                val min = TimeUnit.MILLISECONDS.toMinutes(timeSpentInChat)
                val sec = TimeUnit.MILLISECONDS.toSeconds(timeSpentInChat) - (min * 60)
                savechat()
                mRtmClient!!.logout(null)
                //  sendChatLogOnChatEnd("$min:$sec")
                val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
                intent.putExtra("fragmentNumber", 4)
                startActivity(intent)
                finish()
            }
            alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
              if(!send){
                  formData()
              }
            }
            alertDialogBuilder.show() }

    }

    fun savechat(){
        val jsonArray = JSONArray()
        for(message in mMessageBeanList) {

            val json1 = JSONObject()
            if(message.beSelf==true){
                json1.put("type", "user")
            }
            else{
                json1.put("type", "astrologer")
            }
            val msg: String = message.message?.text ?: "" ;
            Log.d("Message Array", "message====>"+msg)
            json1.put("text",msg)
            json1.put("sent_at",message.sentAt)
            jsonArray.put(json1)
        }


        val json2 = JSONObject()
        json2.put("messages", jsonArray)

        agoraMsgViewModel?.saveChat(json2,chatId.toString())
        agoraMsgViewModel?.endchat(chatId.toString())
    }

    override fun onResume() {
        super.onResume()
       /* val ha = Handler()
        ha.postDelayed(object : Runnable {
            override fun run() {
                //call function
                checkjoinStatus()
                    ha.postDelayed(this, 5000)
            }
        }, 5000)
*/
    }


    fun setToolbar(){
        val toolbar_main_activity = myActivity.findViewById<Toolbar>(R.id.message_toolbar)
        toolbar_main_activity.layout_btn_recharge.visibility= View.GONE
        toolbar_main_activity.ib_notification.visibility= View.GONE
        toolbar_main_activity.title = intent.getStringExtra(Constants.CHAT_ASTRO_NAME)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar_main_activity.setNavigationOnClickListener {
            endChat("Do you want to end chat?")
        }
    }

    fun formData(){

        val firstChatMsgUserForm = SharedPreferenceUtils.readString(
            ApplicationConstant.CHATMESSAGE,
            "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
        )
        if (!firstChatMsgUserForm.isNullOrEmpty()) {
            sendchatFormMessage(firstChatMsgUserForm)
            SharedPreferenceUtils.put(
                ApplicationConstant.CHATMESSAGE,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
        }
        val isComingFromChatHistory = intent.getBooleanExtra("ContinueChat", false)
        if (isComingFromChatHistory) {
            val timerRemainingVal = intent.getLongExtra("Timer", 0L)
            setTimerForChat(timerRemainingVal)
        } else {
            if (intent.getStringExtra(Constants.CHAT_DURATION) != null) {
                val durationMinutes = intent.getStringExtra(Constants.CHAT_DURATION)!!.toInt()
                milliSeconds = (durationMinutes * 60 * 1000).toLong()
                setTimerForChat(milliSeconds)
            }
        }
    }

    private fun sendchatFormMessage(text: String) {

        val message = mRtmClient!!.createMessage()
        message.text = "$text  "
        val messageBean = MessageBean(mUserId, message, true,time)
        mMessageBeanList.add(messageBean)
        adapter?.notifyItemRangeChanged(mMessageBeanList.size, 1)
        mRvMsgList?.scrollToPosition(mMessageBeanList.size - 1)
        sendmessage(message)
    }

    private fun setTimerForChat(timerVal: Long) {
       runOnUiThread { milliSeconds = timerVal
                LogUtils.d("Chat timer in seconds: $milliSeconds")

                /*val intent = Intent(this, BroadcastService::class.java)
                intent.putExtra("timer", milliSeconds)
                startService(intent)*/

                timer = object : CountDownTimer(timerVal, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        milliSecondsFinished = millisUntilFinished
                        tv_chatTimeValue.text = String.format(
                            "%02d:%02d",
                            (millisUntilFinished / 60000),
                            (millisUntilFinished % 60000 / 1000)
                        )
                    }

                    override fun onFinish() {
                        tv_chatTimeValue.text = "00"
                        milliSecondsFinished = 0L
                        val alertDialogBuilder =
                            android.app.AlertDialog.Builder(
                                this@AgoraMessageActivity
                            )

                        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                            timer?.cancel()
                            val timeSpentInChat = milliSeconds - milliSecondsFinished
                            val min = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
                            val sec = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - (min * 60)
                            savechat()
                            finish()
                            mRtmClient!!.logout(null)
                            //  sendChatLogOnChatEnd("$min:$sec")
                        }
                        // set dialog message
                        alertDialogBuilder
                            .setMessage("Please recharge to chat. Your wallet is empty.")
                            .setCancelable(false)
                        alertDialogBuilder.show()
                    }
                }

       }
        timer?.start()

    }



    private fun sendChatLogOnChatEnd(duration: String) {
        var jsonObj = JSONObject()

        jsonObj.put(
            "userID",
            SharedPreferenceUtils.readString(
                ApplicationConstant.USERID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )!!
        )
        jsonObj.put("astroID", intent.getStringExtra(Constants.CHAT_ASTRO_ID))
        jsonObj.put("duration", duration)
        jsonObj.put("status", "success")
        jsonObj.put("chatID", intent.getStringExtra(Constants.CHAT_ID))
        jsonObj.put("sessionID", "0")

        chatFormViewModel.sendChatLog(jsonObj)
        jsonObj.put("chatStatus", "online")
                jsonObj.put("chatStatus", "ended")
        chatFormViewModel.changeChatStatus(jsonObj)

        BaseApplication.instance.p0 = null

        val intent = Intent(ApplicationUtil.getContext(), ChannelActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        val timeSpentInChat = milliSeconds - milliSecondsFinished
        val min = TimeUnit.MILLISECONDS.toMinutes(timeSpentInChat)
        val sec = TimeUnit.MILLISECONDS.toSeconds(timeSpentInChat) - (min * 60)
        mRtmClient!!.logout(null)
        finish()
        savechat()
      //  mRtmClient!!.release()
      //  sendChatLogOnChatEnd("$min:$sec")
    }

    //Login in agoraServer
    fun agoraLogin(){

        val userId = SharedPreferenceUtils.readString(
            ApplicationConstant.USERID, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))

        val agoraToken = SharedPreferenceUtils.readString(
            ApplicationConstant.AgoraToken, "",
            SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))

        mRtmClient?.login(agoraToken,userId,object :ResultCallback<Void?>{
            override fun onSuccess(p0: Void?) {
                myActivity.runOnUiThread {
                    hideProgressBar()

                    Toast.makeText(myActivity, "Start Chat  " , Toast.LENGTH_LONG).show()
                    formData()
                     val ha = Handler()
       ha.postDelayed(object : Runnable {
           override fun run() {
               //call function
              // checkjoinStatus()
               formData()
               Log.d("Formdata>>","aaaaaaaaaa")
                  // ha.postDelayed(this, 5000)
           }
       }, 500)
                }

            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                myActivity.runOnUiThread {
                    hideProgressBar()
                    Toast.makeText(myActivity, "Chat Failure  " + errorInfo?.errorDescription, Toast.LENGTH_LONG).show()

                }
            }

        }
        )


    }

    private  fun sendPeerMessage(){
        val message = mRtmClient!!.createMessage()
        message.text = ed_msg.getText().toString()

        val messageBean = MessageBean(mUserId, message, true,time)
        mMessageBeanList.add(messageBean)
        adapter?.notifyItemRangeChanged(mMessageBeanList.size, 1)
        mRvMsgList?.scrollToPosition(mMessageBeanList.size - 1)


        sendmessage(message)


    }


 private  fun  sendmessage(message: RtmMessage){
   //  val options = mChatManager!!.sendMessageOptions
   //  options!!.enableOfflineMessaging = true

     mRtmClient!!.sendMessageToPeer(mPeerId, message, mChatManager!!.sendMessageOptions, object : ResultCallback<Void?> {

         override fun onSuccess(aVoid: Void?) {
             // do nothing
           //  Toast.makeText(baseContext, "onSuccess, Toast.LENGTH_LONG).show()
             if(!send){
                 send = true
             }
         }

         override fun onFailure(errorInfo: ErrorInfo) {
             // refer to RtmStatusCode.PeerMessageState for the message state
             val errorCode = errorInfo.errorCode
             runOnUiThread {

                 when (errorCode) {
                     RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT, RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE ->{
                         timer!!.cancel()
                         endChat("Astrologer not Reachable\n Please click On Ok Button To End Chat ")
                         BaseApplication.instance.showToast(getString(
                             R.string.send_msg_failed))
                     }
                     RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE ->{
                     //    BaseApplication.instance.showToast(getString(R.string.peer_offline))
                         timer!!.cancel()
                         endChat("Astrologer not Reachable\n Please click On Ok Button To End Chat ")

                     }
                     RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER -> BaseApplication.instance.showToast(getString(
                        R.string.message_cached))
                 }
             }
         }

     }
     )

 }

    private fun setRecyclerView(){
        adapter = MessageAdapter(mMessageBeanList) {mMessageBean, i ->
         BaseApplication.instance.showToast("hh")

        }
        mRvMsgList?.layoutManager = LinearLayoutManager(ApplicationUtil.getContext())
        mRvMsgList?.scheduleLayoutAnimation()
        mRvMsgList?.adapter = adapter
        if (mRvMsgList?.layoutManager == null){
            mRvMsgList?.addItemDecoration(MarginItemDecorator(1))
        }
        mRvMsgList?.hasFixedSize()
    }


    /**
     * API CALLBACK: rtm event listener
     */

      inner  class MyRtmClientListener : RtmClientListener{

            override fun onConnectionStateChanged(state: Int, p1: Int) {
                runOnUiThread {
                    when (state) {
                        RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING -> BaseApplication.instance.showToast(
                            getString(R.string.reconnecting)
                        )
                        RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED -> {
                            BaseApplication.instance.showToast(getString(R.string.account_offline))
                            setResult(MessageUtils.ACTIVITY_RESULT_CONN_ABORTED)
                            finish()
                        }
                        RtmStatusCode.ConnectionState.CONNECTION_STATE_DISCONNECTED -> BaseApplication.instance.showToast(
                            getString(R.string.Disconnect)
                        )
                    }
                }
            }

            override fun onMessageReceived(message: RtmMessage, peerId: String) {

                runOnUiThread {

                    if (peerId == mPeerId) {

                        val messageBean = MessageBean(peerId, message, false,time)
                        messageBean.background = getMessageColor(peerId)
                        mMessageBeanList.add(messageBean)
                        adapter?.notifyItemRangeChanged(mMessageBeanList.size, 1)
                        mRvMsgList?.scrollToPosition(mMessageBeanList.size - 1)
                    } else {
                        MessageUtils.addMessageBean(peerId, message)
                    }
                }
            }
            override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {

            }

            override fun onTokenExpired() {

            }

            override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {

            }

            override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {

            }

            override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {

            }



            override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {

            }

        }

            private fun getMessageColor(account: String): Int {
                for (i in mMessageBeanList.indices) {
                    if (account == mMessageBeanList[i].account) {
                        return mMessageBeanList[i].background
                    }
                }
                return MessageUtils.COLOR_ARRAY[MessageUtils.RANDOM.nextInt(MessageUtils.COLOR_ARRAY.size)]
            }


            override fun onBackPressed() {
               // super.onBackPressed()
                endChat("Do you want to end chat?")

            }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AgoraMessageActivityViewModel(mApplication) as T
        }
    }
}