package com.sanved.intervaltimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Sanved on 06-06-2018.
 */

public class StopServiceReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 333;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, TimerService.class);
        context.stopService(service);
    }
}
