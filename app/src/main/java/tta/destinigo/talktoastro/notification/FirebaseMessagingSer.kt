package tta.destinigo.talktoastro.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coloros.ocs.base.task.Tasks.call
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.MainActivity
import tta.destinigo.talktoastro.agora.chat.AgoraMessageActivity
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils

/**

 * Created by Vivek Singh on 2019-09-23.

 */
class FirebaseMessagingSer : FirebaseMessagingService() {
    val TAG = "Firebase Message"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("token>>>>>>>>","testing");


//        showNotification(ApplicationUtil.getContext(), remoteMessage)

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
       /* val title = remoteMessage.notification!!.title.toString() + "TiTle"
        val body = remoteMessage.notification!!.body.toString() + "Body"
        Log.d("token>>>>>>>>","title>>>>>"+title);
        Log.d("token>>>>>>>>","body>>>>>"+body);*/
       // show(title,remoteMessage.notification!!.body.toString())

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel =
                NotificationChannel("ID", "Name", importance)
            notificationManager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(
                applicationContext,
                notificationChannel.id
            )
        } else {
            NotificationCompat.Builder(applicationContext)
        }

        builder = builder

            .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight_Dark))
            .setContentTitle(remoteMessage.data["title"])
            .setSmallIcon(R.drawable.app_icon_chat)
            // .setTicker(title)
            .setContentText(remoteMessage.data["msg"])
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
           // .addAction(R.drawable.bubble_b,"ACcept",pendingIntent)
            //.addAction(R.drawable.ic_paytm_icon,"Deny",pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1, builder.build())

    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase id", "Refreshed token: $token")
        if (!TextUtils.isEmpty(token))
            SharedPreferenceUtils.put(
                ApplicationConstant.DEVICETOKEN, token.toString(),
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )

    }


    private fun show(title:String,msg:String){
        val intent = Intent(this, AgoraMessageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isFromChat", true);

        val acceptPendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )


        val builder = NotificationCompat.Builder(this,"tta_channel_id")
            .setSmallIcon(R.drawable.app_icon_chat)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 2000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
           // .addAction(R.drawable.shape_circle_red,"deny",denyPendingIntent)
            //.addAction(R.drawable.shape_circle_green,"Accept",acceptPendingIntent)
            .build()
                as Notification

        val notificationManagerCompat= NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(123,builder)
    }
    @SuppressLint("InvalidWakeLockTag")
    private fun showNotification(context: Context, p0: RemoteMessage) {

        var title = "Dear User, you have received a chat request"

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isFromChat", true);

        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "tta_channel_id"
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this,
            channelId
        )
            .setSmallIcon(R.drawable.app_icon_chat)
            .setContentTitle(title)
            .setContentText("To Join click here")
            .setAutoCancel(true)
            .setColor(Color.rgb(245, 124, 0))
            .setVibrate(longArrayOf(1000, 2000))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }


        try {
            val pm: PowerManager =
                applicationContext.getSystemService(POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isInteractive
            if (isScreenOn == false) {
                val wl: PowerManager.WakeLock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                    "MyLock"
                )
                wl.acquire(10000)
                val wl_cpu: PowerManager.WakeLock =
                    pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
                wl_cpu.acquire(10000)
            }
        } catch (e: Exception) {
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())

    }
}