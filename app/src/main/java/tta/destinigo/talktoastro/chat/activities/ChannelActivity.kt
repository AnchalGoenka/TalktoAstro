package tta.destinigo.talktoastro.activities

import ChatCallbackListener
import ToastStatusListener
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaopiz.kprogresshud.KProgressHUD
import com.twilio.chat.*
import com.twilio.chat.Channel.ChannelType
import eu.inloop.simplerecycleradapter.SimpleRecyclerAdapter
import kotlinx.android.synthetic.main.activity_channel.*
import org.jetbrains.anko.*
import org.json.JSONException
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.agora.chat.ChatManager
import tta.destinigo.talktoastro.agora.chat.ViewChatHistory
import tta.destinigo.talktoastro.chat.BasicChatClient
import tta.destinigo.talktoastro.chat.ChannelModel
import tta.destinigo.talktoastro.chat.models.ChatHistoryModel
import tta.destinigo.talktoastro.chat.services.ChatFormViewModel
import tta.destinigo.talktoastro.chat.views.ChannelViewAdapter
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.Constants
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChannelActivity : AppCompatActivity(), ChatClientListener, AnkoLogger {
    private lateinit var basicClient: BasicChatClient
    private val channels = HashMap<String, ChannelModel>()
    private lateinit var adapter: SimpleRecyclerAdapter<ChannelModel>
    private lateinit var chatFormViewModel: ChatFormViewModel
    private lateinit var listAdapter: ChannelViewAdapter
    private var listChatHistoryModel = ArrayList<ChatHistoryModel>()
    private var timerVal: Long = 0L
    var timer: CountDownTimer? = null
    private var hud: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        if (intent != null) {
            timerVal = intent.getLongExtra("Timer", 0L)
          //er5t6  view_chat_live.visibility = View.VISIBLE

        }
     toolbarChannel.title = "Chat history"
        toolbarChannel.setNavigationIcon(resources.getDrawable(R.drawable.ic_arrow_back_24dp))
        toolbarChannel.setNavigationOnClickListener {
            val intent = Intent(ApplicationUtil.getContext(), MainActivity::class.java)
            startActivity(intent)
        }
        chatFormViewModel = ViewModelProviders.of(
            this,
            MessageActivity.MyViewModelFactory(BaseApplication.instance)
        ).get(
            ChatFormViewModel::class.java
        )

        hud = KProgressHUD(this)

        getChatHistory()
        basicClient = BaseApplication.instance.basicClient
        basicClient.chatClient?.setListener(this@ChannelActivity)

        view_chat_live.setOnClickListener {
            startActivity<ChannelActivity>(
                Constants.EXTRA_CHANNEL to intent.getParcelableExtra<Channel>(Constants.EXTRA_CHANNEL),
                Constants.EXTRA_CHANNEL_SID to intent.getStringExtra(Constants.EXTRA_CHANNEL_SID),
                "Timer" to timerVal,
                "ContinueChat" to true
            )
        }

    }

    private fun setTimerForChat(timeVal: Long) {
        val durationMinutes = intent.getStringExtra(Constants.CHAT_DURATION)?.toInt()
        timer = object : CountDownTimer(timerVal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerVal = millisUntilFinished
            }

            override fun onFinish() {
                timerVal = 0L
            }
        }
        timer?.start()
    }

    fun showProgressBar(message: String, cancellable: Boolean) {

        hud!!.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(message)
            .setCancellable(cancellable)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
            .show()

    }

    fun getChatHistory() {
        showProgressBar("Loading", true)
        var jsonObj = JSONObject()
        jsonObj.put(
            "userID",
            SharedPreferenceUtils.readString(
                ApplicationConstant.USERID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )!!
        )
        chatFormViewModel.getChatHistory(jsonObj)
        chatFormViewModel.arrayMutableChatHistory.observe(this, androidx.lifecycle.Observer {
            listChatHistoryModel = it
            setupListView(it)
            getChannels(it)
            if (hud != null)
                hud!!.dismiss()
        })
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.channel, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_start_new_chat -> showCreateChannelDialog(ChannelType.PRIVATE)
//            R.id.action_create_public -> showCreateChannelDialog(ChannelType.PUBLIC)
//            R.id.action_create_private -> showCreateChannelDialog(ChannelType.PRIVATE)
//            R.id.action_create_public_withoptions -> createChannelWithType(ChannelType.PUBLIC)
//            R.id.action_create_private_withoptions -> createChannelWithType(ChannelType.PRIVATE)
//            R.id.action_search_by_unique_name -> showSearchChannelDialog()
//            R.id.action_user_info -> startActivity(Intent(applicationContext, UserActivity::class.java))
//            R.id.action_update_token -> basicClient.updateToken()
//            R.id.action_logout -> {
//                basicClient.shutdown()
//                finish()
//            }
//            R.id.action_unregistercm -> basicClient.unregisterFcmToken()
//            R.id.action_crash_in_chat_client -> basicClient.chatClient!!.simulateCrash(Where.CHAT_CLIENT_CPP)
//            R.id.action_crash_in_tm_client -> basicClient.chatClient!!.simulateCrash(Where.TM_CLIENT_CPP)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createChannelWithType(type: ChannelType) {
        val rand = Random()
        val value = rand.nextInt(50)

        val attrs = JSONObject()
        try {
            attrs.put("topic", "testing channel creation with options ${value}")
        } catch (xcp: JSONException) {
            error { "JSON exception: $xcp" }
        }

        val typ = if (type == ChannelType.PRIVATE) "Priv" else "Pub"

        val builder = basicClient.chatClient?.channels?.channelBuilder()

        builder?.withFriendlyName("${typ}_TestChannelF_${value}")
            ?.withUniqueName("${typ}_TestChannelU_${value}")
            ?.withType(type)
            ?.withAttributes(attrs)
            ?.build(object : CallbackListener<Channel>() {
                override fun onSuccess(newChannel: Channel) {
                    debug { "Successfully created a channel with options." }
                    channels.put(newChannel.sid, ChannelModel(newChannel))
                    refreshChannelList()
                }

                override fun onError(errorInfo: ErrorInfo?) {
                    error { "Error creating a channel" }
                }
            })
    }

    private fun showCreateChannelDialog(type: ChannelType) {
        alert("") {
            customView {
                verticalLayout {
                    textView {
                        text = "Enter chat header name"
                        padding = dip(10)
                    }.lparams(width = matchParent)
                    val channel_name = editText { padding = dip(10) }.lparams(width = matchParent)
                    positiveButton(R.string.create) {
                        val channelName = channel_name.text.toString()
                        debug { "Creating channel with friendly Name|$channelName|" }
                        basicClient.chatClient?.channels?.createChannel(
                            channelName,
                            type,
                            ChatCallbackListener<Channel>() {
                                debug { "Channel created with sid|${it.sid}| and type ${it.type}" }
                                channels.put(it.sid, ChannelModel(it))
                                it.join(
                                    ToastStatusListener(
                                        "Successfully joined channel",
                                        "Failed to join channel",
                                        {
                                            refreshChannelList()
                                        },
                                        {
                                            if (it.status == Channel.ChannelStatus.JOINED) {
                                                Handler().postDelayed({
                                                    ChannelModel(it).getChannel(ChatCallbackListener<Channel>() {
                                                        startActivity<MessageActivity>(
                                                            Constants.EXTRA_CHANNEL to it,
                                                            Constants.EXTRA_CHANNEL_SID to it.sid
                                                        )
                                                    })
                                                }, 0)
                                            }
                                        })
                                )
                                refreshChannelList()
                            })
                    }
                    negativeButton(R.string.cancel) {}
                }
            }
        }.show()
    }

    private fun showSearchChannelDialog() {
        alert(R.string.title_find_channel) {
            customView {
                verticalLayout {
                    textView {
                        text = "Enter unique channel name"
                        padding = dip(10)
                    }.lparams(width = matchParent)
                    val channel_name = editText { padding = dip(10) }.lparams(width = matchParent)
                    positiveButton(R.string.search) {
                        val channelSid = channel_name.text.toString()
                        debug { "Searching for ${channelSid}" }
                        basicClient.chatClient?.channels?.getChannel(
                            channelSid,
                            ChatCallbackListener<Channel?>() {
                                if (it != null) {
                                    BaseApplication.instance.showToast("${it.sid}: ${it.friendlyName}")
                                } else {
                                    BaseApplication.instance.showToast("Channel not found.")
                                }
                            })
                    }
                    negativeButton(R.string.cancel) {}
                }
            }
        }.show()
    }

    private fun setupListView(chatHistoryList: ArrayList<ChatHistoryModel>) {
        listAdapter = ChannelViewAdapter(chatHistoryList) { chathistory, position ->

         //  if(chathistory.status=="success"){
            if(chathistory.chat_duration?.toInt()!! >= 1){
                val intent = Intent(this, ViewChatHistory::class.java)
                intent.putExtra("ChatID",chathistory.chatID)
                startActivity(intent)
           }else{

                BaseApplication.instance.showToast("Failed chat, history not available")
           }
            /*for (chanObj in channels) {
                if (chathistory.sessionID == chanObj.value.sid) {
                    Handler().postDelayed({
                        chanObj.value.getChannel(ChatCallbackListener<Channel>() {
                            startActivity<MessageActivity>(
                                Constants.EXTRA_CHANNEL to it,
                                Constants.EXTRA_CHANNEL_SID to it.sid,
                                "Chat History" to "true"
                            )
                        })
                    }, 0)
                } else {
                    alert("Failed chat, history not available") {
                        positiveButton("Ok") { dialog ->
                            dialog.cancel()
                        }
                        negativeButton(R.string.cancel) {}
                    }.show()
                }
            }*/

        }
//        adapter = SimpleRecyclerAdapter(
//                ItemClickListener { channel: ChannelModel, _, _ ->
//                    if (channel.channelModel?.status == Channel.ChannelStatus.JOINED) {
//                        Handler().postDelayed({
//                            channel.channelModel?.getChannel(ChatCallbackListener<Channel>() {
//                                startActivity<MessageActivity>(
//                                    Constants.EXTRA_CHANNEL to it,
//                                    Constants.EXTRA_CHANNEL_SID to it.sid,
//                                    "Chat History" to "true"
//                                )
//                            })
//                        }, 0)
//                        return@ItemClickListener
//                    }
//                    alert("Failed chat, history not available") {
//                        positiveButton("Ok") { dialog ->
//                            channel.channelModel?.join(
//                                    ToastStatusListener("Successfully joined channel",
//                                            "Failed to join channel") {
//                                        refreshChannelList()
//                                    })
//                            dialog.cancel()
//                        }
//                        negativeButton(R.string.cancel) {}
//                    }.show()
//                },
//                object : SimpleRecyclerAdapter.CreateViewHolder<ChatHistoryModel>() {
//                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettableViewHolder<ChatHistoryModel> {
//                        return ChannelViewHolder(this@ChannelActivity, parent, chatHistoryList);
//                    }
//                })
//
//        adapter.setLongClickListener(
//            ItemLongClickListener { channel: ChatHistoryModel, _, _ ->
//                selector("Select an option", MESSAGE_OPTIONS) { dialog, which ->
//                    when (which) {
//                        REMOVE -> {
//                            dialog.cancel()
//                            Handler().postDelayed({
//                                channel.channelModel?.getChannel(ChatCallbackListener<Channel>() {
//                                    it.leave(ToastStatusListener("Successfully left chat","Error leaving chat"))
//                                })
//                            }, 0)
//                        }
//                        DELETE -> {
//                            dialog.cancel()
//                            Handler().postDelayed({
//                                channel.channelModel?.getChannel(ChatCallbackListener<Channel>() {
//                                    it.destroy(ToastStatusListener("Successfully deleted chat","Error deleting chat"))
//                                })
//                            }, 0)
//                        }
//                    }
//                }
//                true
//            }
//        )

        channel_list.adapter = listAdapter
        channel_list.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }

    private fun refreshChannelList() {
        listAdapter.updateChatHistoryList(listChatHistoryModel, channels)
    }

    private fun getChannelsPage(
        paginator: Paginator<ChannelDescriptor>,
        chatHistoryList: ArrayList<ChatHistoryModel>
    ) {
        for (cd in paginator.items) {
            error { "Adding channel descriptor for sid|${cd.sid}| friendlyName ${cd.friendlyName}" }
            channels.put(cd.sid, ChannelModel(cd))
        }
        //refreshChannelList()

        if (paginator.hasNextPage()) {
            paginator.requestNextPage(object : CallbackListener<Paginator<ChannelDescriptor>>() {
                override fun onSuccess(channelDescriptorPaginator: Paginator<ChannelDescriptor>) {
                    getChannelsPage(channelDescriptorPaginator, chatHistoryList)
                }
            })
        } else {
            // Get subscribed channels last - so their status will overwrite whatever we received
            // from public list. Ugly workaround for now.
            val chans = basicClient.chatClient?.channels?.subscribedChannels
            if (chans != null) {
                for (channel in chans) {
                    channels.put(channel.sid, ChannelModel(channel))
                }
                listAdapter.updateChatHistoryList(null, channels)
            }
            //refreshChannelList()
        }
    }

    // Initialize channels with channel list
    private fun getChannels(chatHistoryList: ArrayList<ChatHistoryModel>) {
        channels.clear()

        basicClient.chatClient?.channels?.getPublicChannelsList(object :
            CallbackListener<Paginator<ChannelDescriptor>>() {
            override fun onSuccess(channelDescriptorPaginator: Paginator<ChannelDescriptor>) {
                getChannelsPage(channelDescriptorPaginator, chatHistoryList)
            }
        })

        basicClient.chatClient?.channels?.getUserChannelsList(object :
            CallbackListener<Paginator<ChannelDescriptor>>() {
            override fun onSuccess(channelDescriptorPaginator: Paginator<ChannelDescriptor>) {
                getChannelsPage(channelDescriptorPaginator, chatHistoryList)
            }
        })
    }

    private fun showIncomingInvite(channel: Channel) {
        if (channel.status == Channel.ChannelStatus.JOINED) {
            return
        }
        alert(R.string.channel_invite_message, R.string.channel_invite) {
            customView {
                verticalLayout {
                    textView {
                        text = "You are invited to channel ${channel.friendlyName} |${channel.sid}|"
                        padding = dip(10)
                    }.lparams(width = matchParent)
                    positiveButton(R.string.join) {
                        channel.join(ToastStatusListener(
                            "Successfully joined channel",
                            "Failed to join channel"
                        ) {
                            channels.put(channel.sid, ChannelModel(channel))
                            refreshChannelList()
                        })
                    }
                    negativeButton(R.string.decline) {
                        channel.declineInvitation(
                            ToastStatusListener(
                                "Successfully declined channel invite",
                                "Failed to decline channel invite"
                            )
                        )
                    }
                }
            }
        }.show()
    }

    private inner class CustomChannelComparator : Comparator<ChannelModel> {
        override fun compare(lhs: ChannelModel, rhs: ChannelModel): Int {
            return lhs.friendlyName.compareTo(rhs.friendlyName)
        }
    }

    //=============================================================
    // ChatClientListener
    //=============================================================

    override fun onChannelJoined(channel: Channel) {
        debug { "Received onChannelJoined callback for channel |${channel.friendlyName}|" }
        channels.put(channel.sid, ChannelModel(channel))
        refreshChannelList()
    }

    override fun onChannelAdded(channel: Channel) {
        debug { "Received onChannelAdded callback for channel |${channel.friendlyName}|" }
        channels.put(channel.sid, ChannelModel(channel))
        refreshChannelList()
    }

    override fun onChannelUpdated(channel: Channel, reason: Channel.UpdateReason) {
        debug { "Received onChannelUpdated callback for channel |${channel.friendlyName}| with reason ${reason}" }
        channels.put(channel.sid, ChannelModel(channel))
        refreshChannelList()
    }

    override fun onChannelDeleted(channel: Channel) {
        debug { "Received onChannelDeleted callback for channel |${channel.friendlyName}|" }
        channels.remove(channel.sid)
        refreshChannelList()
    }

    override fun onChannelInvited(channel: Channel) {
        channels.put(channel.sid, ChannelModel(channel))
        refreshChannelList()
        showIncomingInvite(channel)
    }

    override fun onChannelSynchronizationChange(channel: Channel) {
        error { "Received onChannelSynchronizationChange callback for channel |${channel.friendlyName}| with new status ${channel.status}" }
        refreshChannelList()
    }

    override fun onClientSynchronization(status: ChatClient.SynchronizationStatus) {
        error { "Received onClientSynchronization callback ${status}" }
    }

    override fun onUserUpdated(user: User, reason: User.UpdateReason) {
        error { "Received onUserUpdated callback for ${reason}" }
    }

    override fun onUserSubscribed(user: User) {
        error { "Received onUserSubscribed callback" }
    }

    override fun onUserUnsubscribed(user: User) {
        error { "Received onUserUnsubscribed callback" }
    }

    override fun onNewMessageNotification(
        channelSid: String?,
        messageSid: String?,
        messageIndex: Long
    ) {
        BaseApplication.instance.showToast("Received onNewMessage push notification")
    }

    override fun onAddedToChannelNotification(channelSid: String?) {
        BaseApplication.instance.showToast("Received onAddedToChannel push notification")
    }

    override fun onInvitedToChannelNotification(channelSid: String?) {
        BaseApplication.instance.showToast("Received onInvitedToChannel push notification")
    }

    override fun onRemovedFromChannelNotification(channelSid: String?) {
        BaseApplication.instance.showToast("Received onRemovedFromChannel push notification")
    }

    override fun onNotificationSubscribed() {
        BaseApplication.instance.showToast("Subscribed to push notifications")
    }

    override fun onNotificationFailed(errorInfo: ErrorInfo) {
        BaseApplication.instance.showError("Failed to subscribe to push notifications", errorInfo)
    }

    override fun onError(errorInfo: ErrorInfo) {
        BaseApplication.instance.showError("Received error", errorInfo)
    }

    override fun onConnectionStateChange(connectionState: ChatClient.ConnectionState) {
        //  BaseApplication.instance.showToast("Transport state changed to ${connectionState}")
    }

    override fun onTokenExpired() {
        basicClient.onTokenExpired()
    }

    override fun onTokenAboutToExpire() {
        basicClient.onTokenAboutToExpire()
    }

    companion object {
        private val MESSAGE_OPTIONS = listOf("Leave chat", "Delete chat")
        private val REMOVE = 0
        private val DELETE = 1
        private val CHANNEL_OPTIONS = arrayOf("Join")
        private val JOIN = 2
    }
}
