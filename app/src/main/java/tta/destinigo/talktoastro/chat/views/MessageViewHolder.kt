package tta.destinigo.talktoastro.views

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.twilio.chat.CallbackListener
import com.twilio.chat.Member
import com.twilio.chat.User
import eu.inloop.simplerecycleradapter.SettableViewHolder
import kotterknife.bindView
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activities.MessageActivity
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.SharedPreferenceUtils
import java.text.SimpleDateFormat
import java.util.*


class MessageViewHolder(val context: Context, parent: ViewGroup)
    : SettableViewHolder<MessageActivity.MessageItem>(context, R.layout.message_item_layout, parent)
{
    val body: TextView by bindView(R.id.body)
    val bodyRight: TextView by bindView(R.id.bodyRight)
    val date: TextView by bindView(R.id.date)
    val dateRight: TextView by bindView(R.id.dateRight)

    override fun setData(message: MessageActivity.MessageItem) {
        val msg = message.message
        val name = SharedPreferenceUtils.readString(
            ApplicationConstant.NAME, "",
            SharedPreferenceUtils.getSharedPref(
                ApplicationUtil.getContext())
        )
        val bodyParams =
            body.getLayoutParams() as RelativeLayout.LayoutParams
        val bodyRightParams = bodyRight.getLayoutParams() as RelativeLayout.LayoutParams
        val timeParam = date.getLayoutParams() as RelativeLayout.LayoutParams
        val timeRightParam = dateRight.getLayoutParams() as RelativeLayout.LayoutParams
        if (name == msg.author) {
            body.visibility = View.GONE
            date.visibility = View.GONE
            bodyRight.visibility = View.VISIBLE
            dateRight.visibility = View.VISIBLE
            bodyRight.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            bodyRight.setPadding(20, 10, 80, 10)
            bodyRight.setLayoutParams(bodyRightParams)
            timeRightParam.setMargins(0,0,20, 0)
            dateRight.setLayoutParams(timeRightParam)
            bodyRight.text = msg.messageBody
            var dateCreation = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(msg.dateCreated)
            dateRight.text = toSimpleString(dateCreation)
        } else {
            bodyRight.visibility = View.GONE
            dateRight.visibility = View.GONE
            body.visibility = View.VISIBLE
            date.visibility = View.VISIBLE
            body.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            body.setPadding(80, 10, 20, 10)
            body.setLayoutParams(bodyParams)
            timeParam.setMargins(20,0,0, 0)
            date.setLayoutParams(timeParam)
            body.text = msg.messageBody
            var dateCreation = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(msg.dateCreated)
            date.text = toSimpleString(dateCreation)
        }




//        for (member in message.members.membersList) {
//            if (msg.author.contentEquals(member.identity)) {
//                fillUserAvatar(avatarView, member)
//                fillUserReachability(reachabilityView, member)
//            }
//
//            if (member.lastConsumedMessageIndex != null && member.lastConsumedMessageIndex == message.message.messageIndex) {
//                drawConsumptionHorizon(member)
//            }
//        }

//        if (msg.hasMedia()) {
//            body.visibility = View.GONE
//            mediaView.visibility = View.VISIBLE
//            mediaView.setImageURI(Uri.fromFile(File(context.cacheDir, msg.media.sid)))
//        }
    }

    fun toSimpleString(date: Date) : String {
        val format = SimpleDateFormat("HH:mm a", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT")
        return format.format(date)
    }

//    fun toggleDateVisibility() {
//        date.visibility = if (date.visibility == View.GONE) View.VISIBLE else View.GONE
//    }

//    private fun drawConsumptionHorizon(member: Member) {
//        val ident = member.identity
//        val color = getMemberRgb(ident)
//
//        val identity = TextView(itemView.context)
//        identity.text = ident
//        identity.textSize = 8f
//        identity.setTextColor(color)
//
//        // Layout
//        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//        val cc = identities.childCount
//        if (cc > 0) {
//            params.addRule(RelativeLayout.RIGHT_OF, identities.getChildAt(cc - 1).id)
//        }
//        identity.layoutParams = params
//
//        val line = View(itemView.context)
//        line.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5)
//        line.setBackgroundColor(color)
//
//        identities.addView(identity)
//        lines.addView(line)
//    }
//
//    private fun fillUserAvatar(avatarView: ImageView, member: Member) {
//        BaseApplication.instance.basicClient.chatClient?.users?.getAndSubscribeUser(member.identity, object : CallbackListener<User>() {
//            override fun onSuccess(user: User) {
//                val attributes = user.attributes
//                val avatar = attributes.opt("avatar") as String?
//                if (avatar != null) {
//                    val data = Base64.decode(avatar, Base64.NO_WRAP)
//                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
//                    avatarView.setImageBitmap(bitmap)
//                } else {
//                    avatarView.setImageResource(R.drawable.avatar2)
//                }
//            }
//        })
//    }

    private fun fillUserReachability(reachabilityView: ImageView, member: Member) {
        if (!BaseApplication.instance.basicClient.chatClient?.isReachabilityEnabled!!) {
            reachabilityView.setImageResource(R.drawable.reachability_disabled)
        } else {
            member.getAndSubscribeUser(object : CallbackListener<User>() {
                override fun onSuccess(user: User) {
                    if (user.isOnline) {
                        reachabilityView.setImageResource(R.drawable.reachability_online)
                    } else if (user.isNotifiable) {
                        reachabilityView.setImageResource(R.drawable.reachability_notifiable)
                    } else {
                        reachabilityView.setImageResource(R.drawable.reachability_offline)
                    }
                }
            })
        }
    }

    fun getMemberRgb(identity: String): Int {
        return HORIZON_COLORS[Math.abs(identity.hashCode()) % HORIZON_COLORS.size]
    }

    companion object {
        private val HORIZON_COLORS = intArrayOf(Color.GRAY, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA)
    }
}
