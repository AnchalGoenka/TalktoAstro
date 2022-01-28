package tta.destinigo.talktoastro.agora.chat

import android.content.Context
import android.util.Log
import io.agora.rtm.*
import tta.destinigo.talktoastro.BaseApplication
import tta.destinigo.talktoastro.BuildConfig
import tta.destinigo.talktoastro.util.ApplicationConstant
import java.util.*

class ChatManager(var mContext: Context?) {
    private val TAG = ChatManager::class.java.simpleName
    private val mListenerList: MutableList<RtmClientListener> = ArrayList()
     var mrtmClient: RtmClient? = null
    private set
    var sendMessageOptions: SendMessageOptions? = null
        private set

    fun init(){

       val appId  =ApplicationConstant.AppId
        try {


            mrtmClient = RtmClient.createInstance(mContext, appId, object : RtmClientListener{

            override fun onConnectionStateChanged(p0: Int, p1: Int) {

              //  BaseApplication.instance.showToast("onConnectionStateChanged"+ p0)
            }

            override fun onMessageReceived(rtmMessage: RtmMessage?, peerId: String?) {
                for (listener in mListenerList) {
                    listener.onMessageReceived(rtmMessage, peerId)
                }
               // BaseApplication.instance.showToast("onMessageReceived chat manager")
            }
            override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
              //  BaseApplication.instance.showToast("onMediaDownloadingProgress")
            }

            override fun onTokenExpired() {
             //   BaseApplication.instance.showToast("onTokenExpired")
            }

            override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                //BaseApplication.instance.showToast("onPeersOnlineStatusChanged")
            }

            override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {
              //  BaseApplication.instance.showToast("onMediaUploadingProgress")
            }

            override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
               // BaseApplication.instance.showToast("onImageMessageReceivedFromPeer")
            }

            override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
               // BaseApplication.instance.showToast("onFileMessageReceivedFromPeer")
            }
        })

        }catch  (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RuntimeException(
             //   BaseApplication.instance.showToast("onFileMessageReceivedFromPeer")
                "NEED TO check rtm sdk init fatal error\n" + Log.getStackTraceString(e)
            )}
        if (BuildConfig.DEBUG) {
            mrtmClient?.setParameters("{\"rtm.log_filter\": 65535}")
        }
        sendMessageOptions = SendMessageOptions()
    }


    /*fun getRtmClient(): RtmClient? {
        return mrtmClient
    }*/
    fun registerListener(listener: RtmClientListener) {
        mListenerList.add(listener)
    }

    fun unregisterListener(listener: RtmClientListener?) {
        mListenerList.remove(listener)
    }

}