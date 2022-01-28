package tta.destinigo.talktoastro.agora.chat

import android.os.Build
import android.util.Log
import io.agora.rtm.RtmMessage
import tta.destinigo.talktoastro.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object MessageUtils  {

    val MAX_INPUT_NAME_LENGTH = 64

    val ACTIVITY_RESULT_CONN_ABORTED = 1

    val INTENT_EXTRA_IS_PEER_MODE = "chatMode"
    val INTENT_EXTRA_USER_ID = "userId"
    val INTENT_EXTRA_TARGET_NAME = "targetName"

    var RANDOM = Random()
    val COLOR_ARRAY = arrayListOf(
      //  R.drawable.shape_circle_black,
      //  R.drawable.shape_circle_blue,
       // R.drawable.shape_circle_pink,
      //  R.drawable.shape_circle_pink_dark,
      //  R.drawable.shape_circle_yellow,
        R.drawable.shape_circle_red,
        R.drawable.shape_circle_red)


    private val messageListBeanList: MutableList<MessageListBean> =
        ArrayList<MessageListBean>()

    fun addMessageListBeanList(messageListBean: MessageListBean) {
        messageListBeanList.add(messageListBean)
    }

    // clean up list on logout
    fun cleanMessageListBeanList() {
        messageListBeanList.clear()
    }

    fun getExistMessageListBean(accountOther: String): MessageListBean? {
        val ret = existMessageListBean(accountOther)
        return if (ret > -1) {
            messageListBeanList.removeAt(ret)
        } else null
    }

    // return existing list position
    private fun existMessageListBean(userId: String): Int {
        val size = messageListBeanList.size
        for (i in 0 until size) {
            if (messageListBeanList[i].accountOther.equals(userId)) {
                return i
            }
        }
        return -1
    }

    fun addMessageBean(account: String, msg: RtmMessage?) {
        var time: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            time =  current.format(formatter)
            Log.d("answer",time)
        } else {
            var date = Date()
            val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
            time = formatter.format(date)
            Log.d("answer",time)
        }

        val messageBean = MessageBean(account, msg, false,time)
        val ret = existMessageListBean(account)
        if (ret == -1) {
            // account not exist new messagelistbean
            messageBean.background =
                MessageUtils.COLOR_ARRAY.get(RANDOM.nextInt(MessageUtils.COLOR_ARRAY.size))
            val messageBeanList: MutableList<MessageBean> =
                ArrayList()
            messageBeanList.add(messageBean)
            messageListBeanList.add(MessageListBean(account, messageBeanList))
        } else {
            // account exist get messagelistbean
            val bean: MessageListBean = messageListBeanList.removeAt(ret)
            val messageBeanList: MutableList<MessageBean> =
                bean.getMessageBeanList() as MutableList<MessageBean>
            if (messageBeanList.size > 0) {
                messageBean.background = messageBeanList[0].background
            } else {
                messageBean.background =
                    MessageUtils.COLOR_ARRAY.get(RANDOM.nextInt(MessageUtils.COLOR_ARRAY.size))
            }
            messageBeanList.add(messageBean)
            bean.setMessageBeanList(messageBeanList)
            messageListBeanList.add(bean)
        }
    }
}