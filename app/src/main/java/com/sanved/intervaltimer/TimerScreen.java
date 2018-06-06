package com.sanved.intervaltimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sanved on 05-06-2018.
 */

public class TimerScreen extends AppCompatActivity {

    TextView tvTime, laps;
    FloatingActionButton pause, stop;
    private int mins, secs, lapsctr = 0;
    private String minsStr, secsStr;
    boolean flag = false;
    int seconds=0, hours=0, minutes=0;
    Timer timer;
    CustomTask cust1;
    private boolean goingToService = true;
    private boolean isRunning = true;
    private boolean serviceRun = false;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_screen);

        prefs = getSharedPreferences("data", MODE_PRIVATE);
        edit = prefs.edit();

        serviceRun = prefs.getBoolean("serviceRunning", false);

        if(serviceRun) {
            mins = prefs.getInt("mins", 0);
            secs = prefs.getInt("secs", 0);
            edit.putBoolean("serviceRunning", false).commit();
        }
        else{
            if(savedInstanceState == null){

                Bundle extras = getIntent().getExtras();

                mins = Integer.parseInt(extras.getString("mins"));
                secs = Integer.parseInt(extras.getString("secs"));

            }else{

                mins = (int) savedInstanceState.getSerializable("mins");
                secs = (int) savedInstanceState.getSerializable("secs");

            }
        }

        Typeface newFont = Typeface.createFromAsset(getAssets(), "noodle.ttf");

        tvTime = findViewById(R.id.tvTime);
        tvTime.setTypeface(newFont);

        laps = findViewById(R.id.tvLaps);
        laps.setTypeface(newFont);

        if (mins <= 9) {
            minsStr = "0" + mins;
        }else{
            minsStr = "" + mins;
        }

        if (secs <= 9) {
            secsStr = "0" + secs;
        }else{
            secsStr = "" + secs;
        }

        seconds = secs;
        minutes = mins;

        tvTime.setText(minsStr + " : " + secsStr);

        pause = findViewById(R.id.fabPause);
        stop = findViewById(R.id.fabStop);

        timer = new Timer();
        cust1 = new CustomTask();
        timer.schedule(cust1, 1000, 1000);
        isRunning = true;

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning){
                    pause.setImageResource(R.drawable.baseline_pause_black_24);
                    stop.setVisibility(View.INVISIBLE);
                    if(timer != null){
                        timer.cancel();
                    }
                    timer = new Timer();
                    cust1 = new CustomTask();
                    timer.schedule(cust1, 1000, 1000);
                    isRunning = true;
                }else{
                    pause.setImageResource(R.drawable.baseline_play_arrow_black_24);
                    stop.setVisibility(View.VISIBLE);
                    if (timer != null){
                        timer.cancel();
                        timer = null;
                    }
                    isRunning = false;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goingToService = false;
                setResult(5); // 5 for stop
                finish();
            }
        });

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
                    lapsctr++;

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

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
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
                    tvTime.setText(minsStr+ " : " + secsStr);
                    laps.setText("Laps : " + lapsctr);
                }});

        }
    }

    @Override
    public void onBackPressed() {

        if(isRunning) {
            setResult(6);
            edit.putInt("mins", minutes);
            edit.putInt("secs", seconds);
            edit.commit();
            Intent i = new Intent(TimerScreen.this, TimerService.class);
            startService(i);
        }else{
            setResult(5);
        }

        super.onBackPressed();

    }
}
