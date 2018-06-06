package com.sanved.intervaltimer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{

    TextView tv1;
    EditText mins, secs;
    int minsInt = 5, secsInt = 2;
    ImageButton minus, plus;
    FloatingActionButton enter;
    CoordinatorLayout cola1;
    Timer timer, timer2;
    IncreaseTask increase;
    DecreaseTask decrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        initVals();

        getWindow().getDecorView().clearFocus();

    }

    public void initVals(){

        tv1 = findViewById(R.id.tv1);
        Typeface newFont = Typeface.createFromAsset(getAssets(), "noodle.ttf");
        tv1.setTypeface(newFont);

        mins = findViewById(R.id.etMins);
        mins.setTypeface(newFont);

        secs = findViewById(R.id.etSecs);
        secs.setTypeface(newFont);

        cola1 = findViewById(R.id.cola1);

        mins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    Toast.makeText(StartScreen.this, "Enter a value", Toast.LENGTH_SHORT).show();
                    minsInt = 0;
                    mins.setText("0");
                }else{
                    int temp = Integer.parseInt(s.toString());
                    if (temp > 59) {
                        Toast.makeText(StartScreen.this, "Enter a value less than 59", Toast.LENGTH_SHORT).show();
                        mins.setText("" + minsInt);
                    } else {
                        minsInt = temp;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        secs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()) {
                    Toast.makeText(StartScreen.this, "Enter a value", Toast.LENGTH_SHORT).show();
                    secsInt = 0;
                    secs.setText("0");
                }else{
                    int temp = Integer.parseInt(s.toString());
                    if (temp > 59) {
                        Toast.makeText(StartScreen.this, "Enter a value less than 59", Toast.LENGTH_SHORT).show();
                        secs.setText("" + secsInt);
                    } else {
                        secsInt = temp;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        secs.setText("0" + secsInt);
        mins.setText("0" + minsInt);

        plus = findViewById(R.id.ibAdd);
        minus = findViewById(R.id.ibRemove);
        enter = findViewById(R.id.fabEnter);

        enter.setOnClickListener(this);

        plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(timer != null){
                            timer.cancel();
                        }
                        timer = new Timer();
                        increase = new IncreaseTask();
                        timer.schedule(increase, 30, 100);
                        break;
                    case  MotionEvent.ACTION_UP:
                        if (timer != null){
                            timer.cancel();
                            timer = null;
                        }
                        break;
                }
                return false;
            }
        });

        minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(timer2 != null){
                            timer2.cancel();
                        }
                        timer2 = new Timer();
                        decrease = new DecreaseTask();
                        timer2.schedule(decrease, 30, 100);
                        break;
                    case  MotionEvent.ACTION_UP:
                        if (timer2 != null){
                            timer2.cancel();
                            timer2 = null;
                        }
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.fabEnter:

                Intent i = new Intent(StartScreen.this, TimerScreen.class);
                i.putExtra("mins", "" + minsInt);
                i.putExtra("secs", "" + secsInt);
                startActivityForResult(i, 69);

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 69){

            if(resultCode == 5){

            }else if(resultCode == 6){
                finish();
            }

        }

    }

    class IncreaseTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    if (secsInt == 59) {
                        secsInt = 0;
                        minsInt++;
                        if (minsInt <= 9) {
                            mins.setText("0" + minsInt);
                        } else {
                            mins.setText("" + minsInt);
                        }
                        secs.setText("00");
                    } else {
                        secsInt++;
                        if (secsInt <= 9) {
                            secs.setText("0" + secsInt);
                        } else {
                            secs.setText("" + secsInt);
                        }
                    }
                }});

        }
    }


    class DecreaseTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    if(secsInt == 0){
                        secsInt = 59;
                        minsInt--;
                        if (minsInt <= 9) {
                            mins.setText("0" + minsInt);
                        }else{
                            mins.setText("" + minsInt);
                        }
                        secs.setText("59");
                    }else {
                        secsInt--;
                        if (secsInt <= 9) {
                            secs.setText("0" + secsInt);
                        }else{
                            secs.setText("" + secsInt);
                        }
                    }
                }});

        }
    }

}
