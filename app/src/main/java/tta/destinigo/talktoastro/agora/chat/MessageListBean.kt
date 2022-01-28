package tta.destinigo.talktoastro.agora.chat

import java.util.ArrayList

class MessageListBean {

    var accountOther: String
    private var messageBeanList: MutableList<MessageBean>

    constructor(account: String, messageBeanList: MutableList<MessageBean>) {
        accountOther = account
        this.messageBeanList = messageBeanList
    }

    /**
     * Create message list bean from offline messages
     *
     * @param account     peer user id to find offline messages from
     * @param chatManager chat manager that managers offline message pool
     */
    /*constructor(account: String, chatManager: ChatManager) {
        accountOther = account
        messageBeanList = ArrayList()
        val messageList = chatManager.getAllOfflineMessages(account)
        for (m in messageList!!) {
            // All offline messages are from peer users
            val bean = MessageBean(account, m, false)
            messageBeanList.add(bean)
        }
    }*/

    fun getMessageBeanList(): List<MessageBean> {
        return messageBeanList
    }

    fun setMessageBeanList(messageBeanList: MutableList<MessageBean>) {
        this.messageBeanList = messageBeanList
    }
}