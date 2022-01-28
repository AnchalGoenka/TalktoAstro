package tta.destinigo.talktoastro.activities

import ChatCallbackListener
import ChatStatusListener
import ToastStatusListener
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twilio.chat.*
import eu.inloop.simplerecycleradapter.ItemClickListener
import eu.inloop.simplerecycleradapter.ItemLongClickListener
import eu.inloop.simplerecycleradapter.SettableViewHolder
import eu.inloop.simplerecycleradapter.SimpleRecyclerAdapter
import kotlinx.android.synthetic.main.activity_message.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.json.JSONException
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.chat.services.ChatFormViewModel
import tta.destinigo.talktoastro.model.TimerValueUpdate
import tta.destinigo.talktoastro.service.BroadcastService
import tta.destinigo.talktoastro.services.MediaService
import tta.destinigo.talktoastro.util.*
import tta.destinigo.talktoastro.views.MemberViewHolder
import tta.destinigo.talktoastro.views.MessageViewHolder
import java.util.*
import java.util.concurrent.TimeUnit

// RecyclerView Anko
fun ViewManager.recyclerView() = recyclerView(theme = 0) {}

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit): RecyclerView {
    return ankoView({ RecyclerView(it) }, theme = 0, init = init)
}

fun ViewManager.recyclerView(theme: Int = 0) = recyclerView(theme) {}

inline fun ViewManager.recyclerView(theme: Int = 0, init: RecyclerView.() -> Unit): RecyclerView {
    return ankoView({ RecyclerView(it) }, theme, init)
}
// End RecyclerView Anko

class MessageActivity : AppCompatActivity(), ChannelListener, AnkoLogger {
    private lateinit var adapter: SimpleRecyclerAdapter<MessageItem>
    private var channel: Channel? = null
    private lateinit var chatFormViewModel: ChatFormViewModel

    private val messageItemList = ArrayList<MessageItem>()
    private lateinit var identity: String

    private val _instance: MessageActivity = this
    var timer: CountDownTimer? = null
    var milliSecondsFinished: Long = 0L
    var milliSeconds: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        createUI()
        ApplicationUtil.resetTimer()
//        registerReceiver(br, IntentFilter(BroadcastService.COUNTDOWN_BR));
        EventBus.getDefault().register(this);
    }

    override fun onDestroy() {
        channel?.removeListener(this@MessageActivity);
        stopService(Intent(this, BroadcastService::class.java))
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    override fun onResume() {
        super.onResume()
        if (intent != null) {
            channel = intent.getParcelableExtra<Channel>(Constants.EXTRA_CHANNEL)
//            val bundle = intent.extras
//            var hashMap = HashMap<String, Any>()
//            if (bundle != null){
//                hashMap = bundle.getSerializable("extra") as HashMap<String, Any>
//            }
//            channel = hashMap.get("Constants.EXTRA_CHANNEL") as Channel?

            if (channel != null) {
                setupListView(channel!!)
            }

        }

        if (timer != null)
            timer!!.start()

    }

    override fun onPause() {
        super.onPause()
//        timer?.cancel()
        try {
//            unregisterReceiver(br)
        } catch (e: Exception) {
            // Receiver was probably already stopped in onPause()
        }
    }

    public fun getInstance(): MessageActivity {
        return _instance
    }

    private fun setTimerForChat(timerVal: Long) {

        milliSeconds = timerVal
        LogUtils.d("Chat timer in seconds: $milliSeconds")

        val intent = Intent(this, BroadcastService::class.java)
        intent.putExtra("timer", milliSeconds)
        startService(intent)

        timer = object : CountDownTimer(timerVal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                milliSecondsFinished = millisUntilFinished
                /*  txtView_chatTimeValue.text = String.format(
                      "%02d:%02d",
                      (millisUntilFinished / 60000),
                      (millisUntilFinished % 60000 / 1000)
                  )*/
            }

            override fun onFinish() {
                txtView_chatTimeValue.text = "00"
                milliSecondsFinished = 0L
                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(
                        this@MessageActivity
                    )

                alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                    timer?.cancel()
                    val timeSpentInChat = milliSeconds - milliSecondsFinished
                    val min = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
                    val sec = TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - (min * 60)
                    channel!!.leave(ToastStatusListener(
                        "", ""
                    ) {
//                        sendChatLogOnChatEnd("0")
                        sendChatLogOnChatEnd("$min:$sec")
                    })
                }
                // set dialog message
                alertDialogBuilder
                    .setMessage("Please recharge to chat. Your wallet is empty.")
                    .setCancelable(true)
                alertDialogBuilder.show()
            }
        }
//        timer?.start()

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
        jsonObj.put("sessionID", intent.getStringExtra(Constants.EXTRA_CHANNEL_SID))

        chatFormViewModel.sendChatLog(jsonObj)
        jsonObj.put("chatStatus", "online")
        chatFormViewModel.changeChatStatus(jsonObj)

        BaseApplication.instance.p0 = null

        val intent = Intent(ApplicationUtil.getContext(), ChannelActivity::class.java)
        startActivity(intent)

        finish()
    }

    private fun createUI() {
        if (intent != null) {
            chatFormViewModel = ViewModelProviders.of(
                this,
                MyViewModelFactory(BaseApplication.instance)
            ).get(
                ChatFormViewModel::class.java
            )
            val basicClient = BaseApplication.instance.basicClient
            identity = basicClient.chatClient!!.myIdentity
            val channelSid = intent.getStringExtra(Constants.EXTRA_CHANNEL_SID)
            // astrologer id from whom the chat is initiated.
            val channelsObject = basicClient.chatClient!!.channels
            channelsObject.getChannel(channelSid, ChatCallbackListener<Channel>() {
                channel = it
                //save channel
                BaseApplication.instance.p0 = it

//                BaseApplication.instance.saveSessionData(intent)

                toolbarMessage.title = it.friendlyName
                toolbarMessage.setNavigationIcon(resources.getDrawable(R.drawable.ic_arrow_back_24dp))
                toolbarMessage.setNavigationOnClickListener {
                    startActivity<ChannelActivity>(
                        Constants.EXTRA_CHANNEL to channel,
                        Constants.EXTRA_CHANNEL_SID to channelSid,
                        "Timer" to milliSecondsFinished
                    )
                    finish()
                }
                channel!!.addListener(this@MessageActivity)
                this@MessageActivity.title = channel!!.friendlyName
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
                setupListView(channel!!)
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
                setupInput()
            })

            button_endChat.setOnClickListener {
                val alertDialogBuilder =
                    android.app.AlertDialog.Builder(
                        this
                    )

                alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                    timer?.cancel()
                    val timeSpentInChat = milliSeconds - milliSecondsFinished
                    val min = TimeUnit.MILLISECONDS.toMinutes(timeSpentInChat)
                    val sec = TimeUnit.MILLISECONDS.toSeconds(timeSpentInChat) - (min * 60)
                    channel!!.leave(ToastStatusListener(
                        "Successfully left channel", "Error leaving channel"
                    ) {
                        sendChatLogOnChatEnd("$min:$sec")
                    })
                }
                alertDialogBuilder.setNegativeButton("Cancel") { _, _ -> }
                // set dialog message
                alertDialogBuilder
                    .setMessage("Do you want to end chat?")
                    .setCancelable(true)
                alertDialogBuilder.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> showChannelSettingsDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChannelSettingsDialog() {

        selector("Select an option", EDIT_OPTIONS) { _, which ->
            when (which) {
                NAME_CHANGE -> showChangeNameDialog()
                TOPIC_CHANGE -> showChangeTopicDialog()
                LIST_MEMBERS -> {
                    val users = BaseApplication.instance.basicClient.chatClient!!.users
                    // Members.getMembersList() way
                    val members = channel!!.members.membersList
                    val name = StringBuffer()
                    for (i in members.indices) {
                        name.append(members[i].identity)
                        if (i + 1 < members.size) {
                            name.append(", ")
                        }
                        members[i].getUserDescriptor(ChatCallbackListener<UserDescriptor>() {
                            debug { "Got user descriptor from member: ${it.identity}" }
                        })
                        members[i].getAndSubscribeUser(ChatCallbackListener<User>() {
                            debug { "Got subscribed user from member: ${it.identity}" }
                        })
                    }
                    BaseApplication.instance.showToast(name.toString(), Toast.LENGTH_LONG)
                    // Users.getSubscribedUsers() everybody we subscribed to at the moment
                    val userList = users.subscribedUsers
                    val name2 = StringBuffer()
                    for (i in userList.indices) {
                        name2.append(userList[i].identity)
                        if (i + 1 < userList.size) {
                            name2.append(", ")
                        }
                    }
                    BaseApplication.instance.showToast(
                        "Subscribed users: ${name2.toString()}",
                        Toast.LENGTH_LONG
                    )

                    // Get user descriptor via identity
                    users.getUserDescriptor(
                        channel!!.members.membersList[0].identity,
                        ChatCallbackListener<UserDescriptor>() {
                            BaseApplication.instance.showToast(
                                "Random user descriptor: ${it.friendlyName}/${it.identity}",
                                Toast.LENGTH_SHORT
                            )
                        })

                    // Users.getChannelUserDescriptors() way - paginated
                    users.getChannelUserDescriptors(channel!!.sid,
                        object : CallbackListener<Paginator<UserDescriptor>>() {
                            override fun onSuccess(userDescriptorPaginator: Paginator<UserDescriptor>) {
                                getUsersPage(userDescriptorPaginator)
                            }
                        })

                    // Channel.getMemberByIdentity() for finding the user in all channels
                    val members2 =
                        BaseApplication.instance.basicClient.chatClient!!.channels.getMembersByIdentity(
                            channel!!.members.membersList[0].identity
                        )
                    val name3 = StringBuffer()
                    for (i in members2.indices) {
                        name3.append(members2[i].identity + " in " + members2[i].channel.friendlyName)
                        if (i + 1 < members2.size) {
                            name3.append(", ")
                        }
                    }
                    //BaseApplication.get().showToast("Random user in all channels: "+name3.toString(), Toast.LENGTH_LONG);
                }
                INVITE_MEMBER -> showInviteMemberDialog()
                ADD_MEMBER -> showAddMemberDialog()
                REMOVE_MEMBER -> showRemoveMemberDialog()
                LEAVE -> channel!!.leave(ToastStatusListener(
                    "Successfully left channel", "Error leaving channel"
                ) {
                    finish()
                })
                CHANNEL_DESTROY -> channel!!.destroy(ToastStatusListener(
                    "Successfully destroyed channel", "Error destroying channel"
                ) {
                    finish()
                })
                CHANNEL_ATTRIBUTE -> try {
                    BaseApplication.instance.showToast(channel!!.attributes.toString())
                } catch (e: JSONException) {
                    BaseApplication.instance.showToast("JSON exception in channel attributes")
                }
                SET_CHANNEL_UNIQUE_NAME -> showChangeUniqueNameDialog()
                GET_CHANNEL_UNIQUE_NAME -> BaseApplication.instance.showToast(channel!!.uniqueName)
                GET_MESSAGE_BY_INDEX -> channel!!.messages.getMessageByIndex(
                    channel!!.messages.lastConsumedMessageIndex!!,
                    ChatCallbackListener<Message>() {
                        BaseApplication.instance.showToast("SUCCESS GET MESSAGE BY IDX")
                        error { "MESSAGES ${it.messages.toString()}, CHANNEL ${it.channel.sid}" }
                    })
                SET_ALL_CONSUMED -> channel!!.messages.setAllMessagesConsumedWithResult(
                    ChatCallbackListener<Long>()
                    { unread -> BaseApplication.instance.showToast("$unread messages still unread") })
                SET_NONE_CONSUMED -> channel!!.messages.setNoMessagesConsumedWithResult(
                    ChatCallbackListener<Long>()
                    { unread -> BaseApplication.instance.showToast("$unread messages still unread") })
                DISABLE_PUSHES -> channel!!.setNotificationLevel(Channel.NotificationLevel.MUTED,
                    ToastStatusListener(
                        "Successfully disabled pushes", "Error disabling pushes"
                    ) {
                        finish()
                    })
                ENABLE_PUSHES -> channel!!.setNotificationLevel(Channel.NotificationLevel.DEFAULT,
                    ToastStatusListener(
                        "Successfully enabled pushes", "Error enabling pushes"
                    ) {
                        finish()
                    })
            }
        }
    }

    private fun getUsersPage(userDescriptorPaginator: Paginator<UserDescriptor>) {
        for (u in userDescriptorPaginator.items) {
            u.subscribe(ChatCallbackListener<User>() {
                debug { "${it.identity} is a subscribed user now" }
            })
        }
        if (userDescriptorPaginator.hasNextPage()) {
            userDescriptorPaginator.requestNextPage(ChatCallbackListener<Paginator<UserDescriptor>>() {
                getUsersPage(it)
            })
        }
    }

    private fun showChangeNameDialog() {
        alert(R.string.title_update_friendly_name) {
            customView {
                val friendly_name = editText { text.append(channel!!.friendlyName) }
                positiveButton(R.string.update) {
                    val friendlyName = friendly_name.text.toString()
                    debug { friendlyName }
                    channel!!.setFriendlyName(
                        friendlyName, ToastStatusListener(
                            "Successfully changed name", "Error changing name"
                        )
                    )
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showChangeTopicDialog() {
        alert(R.string.title_update_topic) {
            customView {
                val topic = editText { text.append(channel!!.attributes.toString()) }
                positiveButton(R.string.change_topic) {
                    val topicText = topic.text.toString()
                    debug { topicText }

                    try { // @todo Get attributes to update
                        JSONObject().apply {
                            put("Topic", topicText)
                            channel!!.setAttributes(
                                this, ToastStatusListener(
                                    "Attributes were set successfullly.",
                                    "Setting attributes failed"
                                )
                            )
                        }
                    } catch (ignored: JSONException) {
                        // whatever
                    }
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showInviteMemberDialog() {
        alert(R.string.title_invite_member) {
            customView {
                val member = editText { hint = "Enter user id" }
                positiveButton(R.string.invite_member) {
                    val memberName = member.text.toString()
                    debug { memberName }
                    channel!!.members.inviteByIdentity(
                        memberName, ToastStatusListener(
                            "Invited user to channel",
                            "Error in inviteByIdentity"
                        )
                    )
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showAddMemberDialog() {
        alert(R.string.title_add_member) {
            customView {
                val member = editText { hint = "Enter user id" }
                positiveButton(R.string.invite_member) {
                    val memberName = member.text.toString()
                    debug { memberName }
                    channel!!.members.addByIdentity(
                        memberName, ToastStatusListener(
                            "Successful addByIdentity",
                            "Error adding member"
                        )
                    )
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showRemoveMemberDialog() {
        alert("Remove members") {
            customView {
                verticalLayout {
                    val view = recyclerView {}.lparams(width = dip(250), height = matchParent)
                    negativeButton(R.string.cancel) {}

                    view.adapter = SimpleRecyclerAdapter<Member>(
                        ItemClickListener { member: Member, _, _ ->
                            channel!!.members.remove(
                                member, ToastStatusListener(
                                    "Successful removeMember operation",
                                    "Error in removeMember operation"
                                )
                            )
                            // @todo update memberList here
                        },
                        object : SimpleRecyclerAdapter.CreateViewHolder<Member>() {
                            override fun onCreateViewHolder(
                                parent: ViewGroup,
                                viewType: Int
                            ): SettableViewHolder<Member> {
                                return MemberViewHolder(this@MessageActivity, parent)
                            }
                        },
                        channel!!.members.membersList
                    )

                    view.layoutManager = LinearLayoutManager(this@MessageActivity).apply {
                        orientation = LinearLayoutManager.VERTICAL
                    }
                }
            }
        }.show()
    }

    private fun showUpdateMessageDialog(message: Message) {
        alert(R.string.title_update_message) {
            customView {
                val messageText = editText { text.append(message.messageBody) }
                positiveButton(R.string.update) {
                    val text = messageText.text.toString()
                    debug { text }
                    message.updateMessageBody(text, ToastStatusListener(
                        "Success updating message",
                        "Error updating message"
                    ) {
                        // @todo only need to update one message body
                        loadAndShowMessages()
                    })
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showUpdateMessageAttributesDialog(message: Message) {
        alert(R.string.title_update_attributes) {
            customView {
                val messageAttrText = editText { text.append(message.attributes.toString()) }
                positiveButton(R.string.update) {
                    val text = messageAttrText.text.toString()
                    debug { text }
                    try {
                        JSONObject(text).apply {
                            message.setAttributes(this, ToastStatusListener(
                                "Success updating message attributes",
                                "Error updating message attributes"
                            ) {
                                // @todo only need to update one message
                                loadAndShowMessages()
                            })
                        }
                    } catch (e: JSONException) {
                        error { "Invalid JSON attributes entered, using old value" }
                    }
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun showChangeUniqueNameDialog() {
        alert("Update channel unique name") {
            customView {
                val uniqueNameText = editText { text.append(channel!!.uniqueName) }
                positiveButton(R.string.update) {
                    val uniqueName = uniqueNameText.text.toString()
                    debug { uniqueName }
                    channel!!.setUniqueName(uniqueName, ChatStatusListener());
                }
                negativeButton(R.string.cancel) {}
            }
        }.show()
    }

    private fun loadAndShowMessages() {
        channel!!.messages?.getLastMessages(50, ChatCallbackListener<List<Message>>() {
            messageItemList.clear()
            val members = channel!!.members
            if (it.isNotEmpty()) {
                for (i in it.indices) {
                    messageItemList.add(MessageItem(it[i], members, identity))
                }
            }
            adapter.clear()
            adapter.addItems(messageItemList)
            adapter.notifyDataSetChanged()
            if (messageItemList.size > 1) {
                message_list_view.smoothScrollToPosition(messageItemList.size - 1)
            }
        })
    }

    private fun setupInput() {
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        messageInput.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (channel != null) {
                        channel!!.typing()
                    }
                }
            })

            setOnEditorActionListener { _, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_NULL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    sendMessage()
                }
                true
            }
        }

        sendButton.apply {
            setOnClickListener { sendMessage() }

            setOnLongClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                }
                this@MessageActivity.startActivityForResult(intent, FILE_REQUEST)
                true
            }
        }
    }

    private inner class CustomMessageComparator : Comparator<Message> {
        override fun compare(lhs: Message?, rhs: Message?): Int {
            if (lhs == null) {
                return if (rhs == null) 0 else -1
            }
            if (rhs == null) {
                return 1
            }
            return lhs.dateCreated.compareTo(rhs.dateCreated)
        }
    }

    private fun setupListView(channel: Channel) {
//        message_list_view.viewTreeObserver.addOnScrollChangedListener {
//            if (message_list_view.lastVisiblePosition >= 0 && message_list_view.lastVisiblePosition < adapter.itemCount) {
//                val item = adapter.getItem(message_list_view.lastVisiblePosition)
//                if (item != null && messagesObject != null)
//                    channel.messages.advanceLastConsumedMessageIndex(
//                            item.message.messageIndex)
//            }
//        }

        adapter = SimpleRecyclerAdapter<MessageItem>(
            ItemClickListener { _: MessageItem, viewHolder, _ ->

            },
            object : SimpleRecyclerAdapter.CreateViewHolder<MessageItem>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): SettableViewHolder<MessageItem> {
                    return MessageViewHolder(this@MessageActivity, parent);
                }
            })

        adapter.setLongClickListener(
            ItemLongClickListener { message: MessageItem, _, _ ->
                selector("Select an option", MESSAGE_OPTIONS) { dialog, which ->
                    when (which) {
                        REMOVE -> {
                            dialog.cancel()
                            channel.messages.removeMessage(
                                message.message, ToastStatusListener(
                                    "Successfully removed message. It should be GONE!!",
                                    "Error removing message"
                                ) {
                                    messageItemList.remove(message)
                                    adapter.notifyDataSetChanged()
                                })
                        }
                        EDIT -> showUpdateMessageDialog(message.message)
                        GET_ATTRIBUTES -> {
                            try {
                                BaseApplication.instance.showToast(message.message.attributes.toString())
                            } catch (e: JSONException) {
                                BaseApplication.instance.showToast("Error parsing message attributes")
                            }
                        }
                        SET_ATTRIBUTES -> showUpdateMessageAttributesDialog(message.message)
                    }
                }
                true
            }
        )

        message_list_view.adapter = adapter
        message_list_view.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        loadAndShowMessages()
    }

    private fun sendchatFormMessage(text: String) {
        channel!!.messages.sendMessage(
            Message.options().withBody(text),
            ChatCallbackListener<Message>() {
                //BaseApplication.instance.showToast("Successfully sent message");
                adapter.notifyDataSetChanged()
                messageInput.setText("")
            })
    }

    private fun sendMessage() {
        val input = messageInput.text.toString()
        if (input != "") {
            sendchatFormMessage(input)
        }

        messageInput.requestFocus()
    }

    /// Send media message
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            debug { "Uri: ${data?.data}" }

            startService<MediaService>(
                MediaService.EXTRA_ACTION to MediaService.EXTRA_ACTION_UPLOAD,
                MediaService.EXTRA_CHANNEL to channel as Parcelable,
                MediaService.EXTRA_MEDIA_URI to data?.data.toString()
            )
        }
    }

    override fun onMessageAdded(message: Message) {
        setupListView(channel!!)

        startService<MediaService>(
            MediaService.EXTRA_ACTION to MediaService.EXTRA_ACTION_DOWNLOAD,
            MediaService.EXTRA_CHANNEL to channel as Parcelable,
            MediaService.EXTRA_MESSAGE_INDEX to message.messageIndex
        )
    }

    override fun onMessageUpdated(message: Message?, reason: Message.UpdateReason) {
        if (message != null) {
            BaseApplication.instance.showToast("onMessageUpdated for ${message.sid}, changed because of ${reason}")
        } else {
            debug { "Received onMessageUpdated" }
        }
    }

    override fun onMessageDeleted(message: Message?) {
        if (message != null) {
            BaseApplication.instance.showToast("onMessageDeleted for ${message.sid}")
        } else {
            debug { "Received onMessageDeleted." }
        }
    }

    override fun onMemberAdded(member: Member?) {
        if (member != null) {
            BaseApplication.instance.showToast("${member.identity} joined")
        }
    }

    override fun onMemberUpdated(member: Member?, reason: Member.UpdateReason) {
        if (member != null) {
            BaseApplication.instance.showToast("${member.identity} changed because of ${reason}")
        }
    }

    override fun onMemberDeleted(member: Member?) {
        if (member != null) {
            //ApplicationUtil.showDialog(this@MessageActivity, "${member.identity} has left the chat")
            val intent = Intent(ApplicationUtil.getContext(), ChannelActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onTypingStarted(channel: Channel?, member: Member?) {
        if (member != null) {
            val text = "${member.identity} is typing ..."
            typingIndicator.text = text
            typingIndicator.setTextColor(Color.RED)
            debug { text }
        }
    }

    override fun onTypingEnded(channel: Channel?, member: Member?) {
        if (member != null) {
            typingIndicator.text = null
            debug { "${member.identity} finished typing" }
        }
    }

    override fun onSynchronizationChanged(channel: Channel) {
        debug { "Received onSynchronizationChanged callback for ${channel.friendlyName}" }
    }

    data class MessageItem(
        val message: Message,
        val members: Members,
        internal var currentUser: String
    )

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {


        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ChatFormViewModel(mApplication) as T
        }
    }

    companion object {
        private val MESSAGE_OPTIONS = listOf("Remove", "Edit", "Get Attributes", "Edit Attributes")
        private val REMOVE = 0
        private val EDIT = 1
        private val GET_ATTRIBUTES = 2
        private val SET_ATTRIBUTES = 3

        private val EDIT_OPTIONS = listOf(
            "Change Friendly Name",
            "Change Topic",
            "List Members",
            "Invite Member",
            "Add Member",
            "Remove Member",
            "Leave",
            "Destroy",
            "Get Attributes",
            "Change Unique Name",
            "Get Unique Name",
            "Get message index 0",
            "Set all consumed",
            "Set none consumed",
            "Disable Pushes",
            "Enable Pushes"
        )

        //private val EDIT_OPTIONS = listOf("Invite Member", "Add Member", "Remove Member", "Leave", "Destroy")
        private val NAME_CHANGE = 0
        private val TOPIC_CHANGE = 1
        private val LIST_MEMBERS = 2
        private val INVITE_MEMBER = 3
        private val ADD_MEMBER = 4
        private val REMOVE_MEMBER = 5
        private val LEAVE = 6
        private val CHANNEL_DESTROY = 7
        private val CHANNEL_ATTRIBUTE = 8
        private val SET_CHANNEL_UNIQUE_NAME = 9
        private val GET_CHANNEL_UNIQUE_NAME = 10
        private val GET_MESSAGE_BY_INDEX = 11
        private val SET_ALL_CONSUMED = 12
        private val SET_NONE_CONSUMED = 13
        private val DISABLE_PUSHES = 14
        private val ENABLE_PUSHES = 15

        private val FILE_REQUEST = 1000;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TimerValueUpdate?) {
        val millisUntilFinished = event!!.getTime();
        Log.i("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000)
//        Log.i("TAG", "Timer: " + millisUntilFinished)
        milliSecondsFinished = millisUntilFinished

        txtView_chatTimeValue.text = String.format(
            "%02d:%02d",
            (millisUntilFinished / 60000),
            (millisUntilFinished % 60000 / 1000)
        )

        if (millisUntilFinished == 0L) {
            val timeSpentInChat = milliSeconds - milliSecondsFinished
            val min = TimeUnit.MILLISECONDS.toMinutes(timeSpentInChat)
            val sec = TimeUnit.MILLISECONDS.toSeconds(timeSpentInChat) - (min * 60)
            channel!!.leave(ToastStatusListener(
                "Successfully left channel", "Error leaving channel"
            ) {
                sendChatLogOnChatEnd("$min:$sec")
                Toast.makeText(
                    applicationContext,
                    "Please recharge to chat. Your wallet is empty.",
                    Toast.LENGTH_SHORT
                ).show();
            })
        }
    }

}
