package tta.destinigo.talktoastro.util;

import android.app.Activity;

/**
 * Created by Vivek Singh on 2019-06-10.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity activity;

    public ExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.d("uncaughtException", ex);
        activity.finish();
        System.exit(2);
    }
}
