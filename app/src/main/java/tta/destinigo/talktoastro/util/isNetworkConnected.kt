package tta.destinigo.talktoastro.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import tta.destinigo.talktoastro.util.ApplicationConstant.Companion.IS_INTERNET_AVAILABLE


/**

 * Created by Vivek Singh on 2019-06-10.

 */
class isNetworkConnected {
    companion object {
        @SuppressLint("MissingPermission")
        fun checkNetworkConnected(context: Context) : Boolean{

            var connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectionManager.activeNetworkInfo

            IS_INTERNET_AVAILABLE = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting

            return  IS_INTERNET_AVAILABLE
        }
    }
}