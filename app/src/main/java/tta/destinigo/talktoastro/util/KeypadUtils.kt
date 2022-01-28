package tta.destinigo.talktoastro.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


/**

 * Created by Vivek Singh on 2019-06-10.

 */
object KeypadUtils {

    /**
     * hides the soft keypad from screen
     *
     * @param pActivity
     */
    fun hideSoftKeypad(pActivity: Activity) {
        if (pActivity.window != null && pActivity.window.currentFocus != null) {
            hideSoftKeypad(pActivity, pActivity.window.currentFocus)
        }
    }

    /**
     * hides the soft keypad from screen
     *
     * @param pActivity
     * @param view
     */
    fun hideSoftKeypad(pActivity: Activity, view: View?) {
        val imm = pActivity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}