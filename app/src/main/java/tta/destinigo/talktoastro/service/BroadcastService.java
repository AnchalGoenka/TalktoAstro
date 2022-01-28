package tta.destinigo.talktoastro.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import tta.destinigo.talktoastro.model.TimerValueUpdate;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "tta.destinigo.talktoastro.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    Long timer = 0L;
    Long startTime = 0L;


    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting timer...");
        startTimer();

    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = intent.getLongExtra("timer", 0L);
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void startTimer() {
        if (timer > 0L) {
            startTime = timer;
            cdt = new CountDownTimer(timer, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                    TimerValueUpdate update = new TimerValueUpdate();
                    update.setTime(millisUntilFinished);
                    update.setStartTime(startTime);
                    EventBus.getDefault().post(update);
                }

                @Override
                public void onFinish() {
                    TimerValueUpdate update = new TimerValueUpdate();
                    update.setTime(0);
                    update.setStartTime(startTime);
                    EventBus.getDefault().post(update);
                }
            };

            cdt.start();
        }
    }
}
