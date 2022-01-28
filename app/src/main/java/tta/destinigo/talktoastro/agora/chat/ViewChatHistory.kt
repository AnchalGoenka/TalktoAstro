package tta.destinigo.talktoastro.agora.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.BaseActivity
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator

class ViewChatHistory : BaseActivity() {
    override val fragmentContainerId: Int
        get() = R.id.frame_Recharge
    override val layoutResId: Int
        get() = R.layout.activity_view_chat_history

    override fun getToolbarId(): Int {
        return 0
    }

    private var ViewChatHistoryViewModel: ViewChatHistoryViewModel? = null
    private var ViewChatHistoryAdapter: ViewChatAdapter? = null
    lateinit var userName :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_chat_history)
        val intent = intent
        val  chatId = intent.getStringExtra("ChatID")
        callRecyclerview(chatId.toString())
    }

    fun callRecyclerview(chatId:String){

        ViewChatHistoryViewModel = ViewModelProvider(this, MyViewModelFactory(myApplication)).get(
            tta.destinigo.talktoastro.agora.chat.ViewChatHistoryViewModel::class.java)

        ViewChatHistoryViewModel?.ViewChat(chatId)
        ViewChatHistoryViewModel?.ViewChatResponse?.observe(this, Observer {
            userName = it.chat.astro.first_name +" " + it.chat.astro.last_name
            setToolbar()
            val viewchat_list: RecyclerView = findViewById(R.id.rv_viewChat)
            viewchat_list.layoutManager = LinearLayoutManager(ApplicationUtil.getContext())
            ViewChatHistoryAdapter = ViewChatAdapter(it.chat.message_list)

            viewchat_list.adapter = ViewChatHistoryAdapter
            if (viewchat_list.layoutManager == null){
                viewchat_list.addItemDecoration(MarginItemDecorator(1))
            }
            //notifi_list.hasFixedSize()
        })

        /* notificationViewModel?.notificationDidFailed?.observe(this, Observer {

             ApplicationUtil.showDialog(this, it)
         })*/
    }
    fun setToolbar(){
        val toolbar_main_activity= myActivity.findViewById<Toolbar?>(R.id.toolbar_ChatHistory)
        //  toolbar_main_activity.tv_header_refresh.visibility= View.INVISIBLE
        //    toolbar_main_activity.iv_notofication.visibility=View.INVISIBLE
        toolbar_main_activity?.title = userName
        toolbar_main_activity?.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar_main_activity?.setNavigationOnClickListener {
            finish()
        }
    }

    class MyViewModelFactory(private val mApplication: BaseApplication) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ViewChatHistoryViewModel(mApplication) as T
        }
    }
}