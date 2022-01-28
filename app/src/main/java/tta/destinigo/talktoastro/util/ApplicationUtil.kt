package tta.destinigo.talktoastro.util

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.os.IBinder
import android.os.Message
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.main_toolbar.view.*
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity


/**

 * Created by Vivek Singh on 2019-06-09.

 */
class ApplicationUtil {

    companion object {

        var timer: CountDownTimer? = null
        var seconds: Long = 0L
        /**
         * Returns the status of the network
         */
        fun isNetworkAvailable(pContext: Context): Boolean {
            val conMngr = pContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = conMngr.activeNetworkInfo

            return activeNetwork != null && activeNetwork.isConnected
        }

        fun getContext(): Context {
            return tta.destinigo.talktoastro.BaseApplication.instance
        }

        fun checkLogin() : Boolean {
            val username = SharedPreferenceUtils.readString(
                ApplicationConstant.USERNAME, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            val password = SharedPreferenceUtils.readString(
                ApplicationConstant.PASSWORD, "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            return !(username.isNullOrEmpty())
        }

        fun setToolbarTitleAndRechargeButton(title: String, isHide: Boolean, drawableRes: Int): Toolbar{
            val myActivity = MainActivity.instance
            val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
            val phCode = SharedPreferenceUtils.readString(ApplicationConstant.PHONECODE, "", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            if (toolbar != null) {
                toolbar.title = title
                myActivity.supportActionBar?.setHomeAsUpIndicator(drawableRes)
                if (phCode == "91") {
                    toolbar.btn_recharge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rupee_orange_large, 0, 0, 0)
                } else{
                    toolbar.btn_recharge.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.dollar_orange_large,0,0,0)
                }
                toolbar.btn_recharge.text = SharedPreferenceUtils.readString(
                    ApplicationConstant.BALANCE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )

                if (isHide || !checkLogin()) {
                    toolbar.layout_btn_recharge.visibility = View.INVISIBLE
                    toolbar.btn_recharge.visibility = View.INVISIBLE
                    toolbar.ib_notification.visibility=View.INVISIBLE
                } else {
                    toolbar.layout_btn_recharge.visibility = View.VISIBLE
                    toolbar.btn_recharge.visibility = View.VISIBLE
                    toolbar.ib_notification.visibility = View.VISIBLE
                }
            }
            return toolbar
        }

        fun showDialog(
            context: Context?,
            message: String?
        ) {
            val alertDialogBuilder =
                AlertDialog.Builder(
                    context
                )

            alertDialogBuilder.setNeutralButton("OK"){_,_ ->

            }
            // set dialog message
            alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)


            // show it


            // show it
            alertDialogBuilder.show()
        }

        fun setTimerValue(timerVal: Long) {
            timer = object: CountDownTimer(timerVal, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    seconds = millisUntilFinished/1000
                }
                override fun onFinish() {
                    seconds = 0L
                    /*val msgService = MessageService()
                    msgService.sendMessage()*/
                }
            }
            timer?.start()
        }

        fun resetTimer() {
            timer?.cancel()
            seconds = 0L
        }

        fun getTimerValue(): Long {
            return this.seconds*1000
        }
    }

}

class MessageService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        // TODO Auto-generated method stub
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO Auto-generated method stub
        sendMessage()
        return super.onStartCommand(intent, flags, startId)
    }

    // Send an Intent with an action named "custom-event-name". The Intent
    // sent should
    // be received by the ReceiverActivity.
    fun sendMessage() {
        val intent = Intent("custom-event-name")
        // You can also include some extra data.
        intent.putExtra("message", "Astrologer seems unavailable currently, please try with another astrologer from list")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}