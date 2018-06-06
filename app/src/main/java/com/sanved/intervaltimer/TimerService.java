package com.sanved.intervaltimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sanved on 06-06-2018.
 */

public class TimerService extends Service {

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    int mins, secs;
    int minutes, seconds;

    Timer timer;
    CustomTask cust1;

    boolean isRunning = true;

    String secsStr, minsStr;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction()!=null && intent.getAction().equals("STOP")){
            stopSelf();
            return START_NOT_STICKY;
        }

        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();

        edit.putBoolean("serviceRunning" , true).commit();

        mins = prefs.getInt("mins", 0);
        secs = prefs.getInt("secs", 0);

        //Toast.makeText(this, mins + "Running in background" + secs, Toast.LENGTH_SHORT).show();

        minutes = mins;
        seconds = secs;

        startForeground(69, createNotification(mins + " : " + secs));

        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        cust1 = new CustomTask();
        timer.schedule(cust1, 1000, 1000);
        isRunning = true;

        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification(String title) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "interval_service";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intentHide = new Intent(this, StopServiceReceiver.class);

        PendingIntent hide = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intentHide, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.drawable.baseline_play_arrow_black_18);
        builder.setContentTitle(title);
        builder.setContentText("Click to open App");
        builder.setOngoing(true);
        builder.setCategory("service");
        builder.addAction(R.drawable.baseline_play_arrow_black_18,"STOP", hide);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, TimerScreen.class), 0));
        return builder.build();
    }

    private void updateNotification(String text) {

        Notification notification = createNotification(text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(69, notification);
    }


    class CustomTask extends TimerTask {

        @Override
        public void run() {
            seconds--;
            if(seconds == 0){
                if(minutes == 0) {
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP2,150);
                    Log.e("tone end","tone end");
                    seconds = secs;
                    minutes = mins;
                }
                else {
                    seconds = 59;
                    minutes--;
                }
            }
            if(minutes == 0 && seconds <= 5){

                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
                Log.e("tone ","tone");
            }

            if (minutes <= 9) {
                minsStr = "0" + minutes;
            }else{
                minsStr = "" + minutes;
            }

            if (seconds <= 9) {
                secsStr = "0" + seconds;
            }else{
                secsStr = "" + seconds;
            }
            updateNotification(minsStr + " : " + secsStr);
            edit.putInt("mins", minutes);
            edit.putInt("secs", seconds);
            edit.commit();

        }
    }

    public class StopServiceReceiver extends BroadcastReceiver {
        public static final int REQUEST_CODE = 333;

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent service = new Intent(context, TimerService.class);
            context.stopService(service);
        }
    }
}
