package tta.destinigo.talktoastro.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recharge.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.adapter.NotificationMainAdapter
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.view.ArticleViewModel
import tta.destinigo.talktoastro.view.CallHistoryFragment
import tta.destinigo.talktoastro.viewmodel.CallHistoryViewModel
import tta.destinigo.talktoastro.viewmodel.LoginViewModel
import tta.destinigo.talktoastro.viewmodel.NotificationViewModel
/**
 * Created by Anchal
 */
class NotificationActivity: tta.destinigo.talktoastro.BaseActivity() {

    private var notificationViewModel: NotificationViewModel? = null
    private var notificationMainAdapter: NotificationMainAdapter? = null

    override val fragmentContainerId: Int
        get() = R.id.frame_Recharge
    override val layoutResId: Int
        get() = R.layout.activity_notification

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        setToolbar()
        callRecyclerview()
    }

    fun setToolbar(){
        val toolbar_main_activity = myActivity.findViewById<Toolbar>(R.id.notification_toolbar)
        toolbar_main_activity.layout_btn_recharge.visibility= View.GONE
        toolbar_main_activity.ib_notification.visibility=View.GONE
        toolbar_main_activity.title = "Notification"
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar_main_activity.setNavigationOnClickListener { finish()}
    }

    fun callRecyclerview(){

        notificationViewModel = ViewModelProvider(this, MyViewModelFactory(myApplication)).get(NotificationViewModel::class.java)

        notificationViewModel?.arrayNotificationMutableLiveData?.observe(this, Observer {

            notificationMainAdapter = NotificationMainAdapter(it!!)
            val notifi_list: RecyclerView = findViewById(R.id.rv_notification)
            notifi_list.layoutManager = LinearLayoutManager(ApplicationUtil.getContext())
            notifi_list.adapter = notificationMainAdapter
            if (notifi_list.layoutManager == null){
                notifi_list.addItemDecoration(MarginItemDecorator(1))
            }
            //notifi_list.hasFixedSize()

        })

        notificationViewModel?.notificationDidFailed?.observe(this, Observer {

            ApplicationUtil.showDialog(this, it)
        })
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(mApplication) as T
        }
    }

}