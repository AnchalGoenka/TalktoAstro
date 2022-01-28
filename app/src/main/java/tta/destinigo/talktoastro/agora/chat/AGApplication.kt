package tta.destinigo.talktoastro.agora.chat

import android.app.Application
import android.content.Context
import com.zoho.salesiqembed.ZohoSalesIQ
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.util.ApplicationConstant

abstract class AGApplication : Application() {
    var chatManager: ChatManager? = null


    override fun onCreate() {
        super.onCreate()
        sInstance = this
        chatManager = ChatManager(this)
        chatManager!!.init()
    }



    companion object {
        private var sInstance: AGApplication? = null

        @JvmStatic
        fun the(): AGApplication? {
            return sInstance
        }

    }
}