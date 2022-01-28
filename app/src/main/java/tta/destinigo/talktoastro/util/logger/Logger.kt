package tta.destinigo.talktoastro.util.logger

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.multidex.BuildConfig
import android.text.ClipboardManager
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**

 * Created by Vivek Singh on 2019-06-10.

 */

internal interface Printer {
    fun d(element: StackTraceElement, message: String, vararg args: Any)
    fun d(element: StackTraceElement, `object`: Any)

    fun e(element: StackTraceElement, message: String, vararg args: Any)
    fun e(element: StackTraceElement, `object`: Any)

    fun w(element: StackTraceElement, message: String, vararg args: Any)
    fun w(element: StackTraceElement, `object`: Any)

    fun i(element: StackTraceElement, message: String, vararg args: Any)
    fun i(element: StackTraceElement, `object`: Any)

    fun v(element: StackTraceElement, message: String, vararg args: Any)
    fun v(element: StackTraceElement, `object`: Any)

    fun wtf(element: StackTraceElement, message: String, vararg args: Any)
    fun wtf(element: StackTraceElement, `object`: Any)

    fun json(element: StackTraceElement, json: String)
}

internal enum class LogType {
    Verbose, Debug, Info, Warn, Error, Wtf
}

class Logger : Printer {

    private var dialog: AlertDialog? = null

    /**
     */
    private val listener = DialogInterface.OnClickListener { dialogInterface, which ->
        val context = dialog!!.context
        when (which) {
            DialogInterface.BUTTON_NEGATIVE -> {
                val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                Toast.makeText(context, tta.destinigo.talktoastro.util.LogUtils.COPY_SUCCESS, Toast.LENGTH_LONG).show()
            }
            DialogInterface.BUTTON_POSITIVE -> dialog!!.dismiss()
            else -> {
            }
        }
    }

    /**
     *
     * @param type
     * @param element
     * @param msg
     * @param args
     */
    private fun logString(type: LogType, element: StackTraceElement, msg: String, vararg args: Any) {
        var msg = msg

        val tag = generateTag(element)
        try {
            msg = String.format(msg, *args)
        } catch (e: Exception) {
            //e.printStackTrace();
        }

        when (type) {
            LogType.Verbose -> Log.v(tag, msg)
            LogType.Debug -> Log.d(tag, msg)
            LogType.Info -> Log.i(tag, msg)
            LogType.Warn -> Log.w(tag, msg)
            LogType.Error -> Log.e(tag, msg)
            LogType.Wtf -> Log.wtf(tag, msg)
            else -> {
            }
        }
    }

    /**
     *
     * @param type
     * @param element
     * @param object
     */
    private fun logObject(type: LogType, element: StackTraceElement, `object`: Any?) {
        if (!tta.destinigo.talktoastro.util.LogUtils.ENABLE_LOG) {
            return
        }
        if (`object` != null) {
            val simpleName = `object`.javaClass.simpleName
            if (`object` is Throwable) {
                val tag = generateTag(element)
                val msg = `object`.toString()
                val exception = `object` as Exception?
                when (type) {
                    LogType.Verbose -> Log.v(tag, msg, exception)
                    LogType.Debug -> Log.d(tag, msg, exception)
                    LogType.Info -> Log.i(tag, msg, exception)
                    LogType.Warn -> Log.w(tag, msg, exception)
                    LogType.Error -> Log.e(tag, msg, exception)
                    LogType.Wtf -> Log.wtf(tag, msg, exception)
                    else -> {
                    }
                }
            } else if (`object` is String) {
                try {
                    logString(type, element, `object`)
                } catch (e: MissingFormatArgumentException) {
                    e(element, e)
                }

            } else if (`object`.javaClass.isArray) {
                var msg = "Temporarily not support more than two dimensional Array!"
                val dim = tta.destinigo.talktoastro.util.logger.ArrayUtil.getArrayDimension(`object`)
                when (dim) {
                    1 -> {
                        val pair = tta.destinigo.talktoastro.util.logger.ArrayUtil.arrayToString(`object`)
                        msg = simpleName.replace("[]", "[" + pair.first + "] {\n")
                        msg += pair.second.toString() + "\n"
                    }
                    2 -> {
                        val pair1 = tta.destinigo.talktoastro.util.logger.ArrayUtil.arrayToObject(`object`)
                        val pair2 = pair1.first as Pair<*, *>
                        msg = simpleName.replace("[][]", "[" + pair2.first + "][" + pair2.second + "] {\n")
                        msg += pair1.second + "\n"
                    }
                    else -> {
                    }
                }
                logString(type, element, "$msg}")
            } else if (`object` is Collection<*>) {
                val collection = `object` as Collection<*>?
                var msg = "%s size = %d [\n"
                msg = String.format(msg, simpleName, collection!!.size)
                if (!collection.isEmpty()) {
                    val iterator = collection.iterator()
                    var flag = 0
                    while (iterator.hasNext()) {
                        val itemString = "[%d]:%s%s"
                        val item = iterator.next()
                        msg += String.format(
                            itemString, flag, tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString<Any>(item),
                            if (flag++ < collection.size - 1) ",\n" else "\n"
                        )
                    }
                }
                logString(type, element, "$msg\n]")
            } else if (`object` is Map<*, *>) {
                var msg = "$simpleName {\n"
                val map = `object` as Map<Any, Any>?
                val keys = map!!.keys
                for (key in keys) {
                    val itemString = "[%s -> %s]\n"
                    val value = map[key]
                    msg += String.format(
                        itemString, tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString(key),
                        tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString<Any>(value)
                    )
                }
                logString(type, element, "$msg}")
            } else {
                logString(type, element, tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString(`object`))
            }
        } else {
            logString(type, element, tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString<Any>(`object`))
        }
    }

    /**
     *
     * @return
     */
    private fun generateTag(caller: StackTraceElement): String {
        var stackTrace = caller.toString()
        stackTrace = stackTrace.substring(stackTrace.lastIndexOf('('), stackTrace.length)
        var tag = "%s%s.%s%s"
        var callerClazzName = caller.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        val Prefix =
            if (TextUtils.isEmpty(tta.destinigo.talktoastro.util.LogUtils.configTagPrefix)) "" else tta.destinigo.talktoastro.util.LogUtils.configTagPrefix
        tag = String.format(tag, Prefix, callerClazzName, caller.methodName, stackTrace)
        //        if(tag.length()>=23)
        //            tag=tag.substring(0,23);
        return tag
    }

    override fun d(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Debug, element, message, *args)
    }

    override fun d(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Debug, element, `object`)
    }

    override fun e(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Error, element, message, *args)
    }

    override fun e(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Error, element, `object`)
    }

    override fun w(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Warn, element, message, *args)
    }

    override fun w(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Warn, element, `object`)
    }

    override fun i(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Info, element, message, *args)
    }

    override fun i(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Info, element, `object`)
    }

    override fun v(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Verbose, element, message, *args)
    }

    override fun v(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Verbose, element, `object`)
    }

    override fun wtf(element: StackTraceElement, message: String, vararg args: Any) {
        logString(LogType.Wtf, element, message, *args)
    }

    override fun wtf(element: StackTraceElement, `object`: Any) {
        logObject(LogType.Wtf, element, `object`)
    }

    override fun json(element: StackTraceElement, json: String) {
        val indent = 4
        if (TextUtils.isEmpty(json)) {
            d(element, "JSON{json is null}")
            return
        }
        try {
            if (json.startsWith("{")) {
                val jsonObject = JSONObject(json)
                val msg = jsonObject.toString(indent)
                d(element, msg)
            } else if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                val msg = jsonArray.toString(indent)
                d(element, msg)
            }
        } catch (e: JSONException) {
            e(element, e)
        }

    }

    /**
     *
     * @param context
     * @param object
     */
    fun alert(context: Context, `object`: Any) {
        if (!BuildConfig.DEBUG) {
            return
        }
        if (null == dialog) {
            dialog = AlertDialog.Builder(context)
                .setNegativeButton(tta.destinigo.talktoastro.util.LogUtils.COPY, listener)
                .setPositiveButton(tta.destinigo.talktoastro.util.LogUtils.CANCEL, listener)
                .create()

        }
        dialog!!.setMessage(tta.destinigo.talktoastro.util.logger.SystemUtil.objectToString(`object`))
        dialog!!.show()
    }
}