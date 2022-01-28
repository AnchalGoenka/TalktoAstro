package tta.destinigo.talktoastro

import tta.destinigo.talktoastro.util.ApplicationConstant



/**

 * Created by Vivek Singh on 2019-06-11.

 */
open class TTABaseAppplication : tta.destinigo.talktoastro.BaseApplication() {

    override fun initializeComponents() {

        initializeVolly(getString(tta.destinigo.talktoastro.R.string.app_name), 0, 0)
        tta.destinigo.talktoastro.util.LogUtils.ENABLE_LOG = ApplicationConstant.SHOW_LOGS//todo:set it to false before release
    }



}