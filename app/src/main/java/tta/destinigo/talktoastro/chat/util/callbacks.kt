import com.twilio.chat.CallbackListener
import com.twilio.chat.ErrorInfo
import com.twilio.chat.StatusListener
import tta.destinigo.talktoastro.BaseApplication

typealias SuccessStatus = () -> Unit
typealias SuccessCallback<T> = (T) -> Unit
typealias ErrorCallback = (error: ErrorInfo) -> Unit

class ChatCallbackListener<T>(val fail: ErrorCallback = {},
                              val success: SuccessCallback<T> = {}) : CallbackListener<T>() {

    override fun onSuccess(p0: T) = success(p0)

    override fun onError(err: ErrorInfo) {
        BaseApplication.instance.showError(err)
        fail(err)
    }
}

open class ChatStatusListener(val fail: ErrorCallback = {},
                              val success: SuccessStatus = {}) : StatusListener() {

    override fun onSuccess() = success()

    override fun onError(err: ErrorInfo) {
        BaseApplication.instance.showError(err)
        fail(err)
    }
}


/**
 * Status listener that shows a toast with operation results.
 */
class ToastStatusListener(val okText: String, val errorText: String, fail: ErrorCallback = {},
                          success: SuccessStatus = {}) : ChatStatusListener(fail, success) {

    override fun onSuccess() {
        BaseApplication.instance.showToast(okText)
        success()
    }
}
