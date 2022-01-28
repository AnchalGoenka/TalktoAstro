package tta.destinigo.talktoastro.interfaces


/**

 * Created by Vivek Singh on 2019-06-10.

 */

interface IScreen {
    val myApplication: tta.destinigo.talktoastro.BaseApplication
    val myActivity: tta.destinigo.talktoastro.BaseActivity
    fun showProgressBar()
    fun hideProgressBar()
}