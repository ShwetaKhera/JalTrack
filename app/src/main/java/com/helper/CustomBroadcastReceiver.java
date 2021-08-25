package com.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomBroadcastReceiver extends BroadcastReceiver {

    public CustomBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Notification onReceiver Called");
        Intent intent1 = new Intent(context, CustomIntentService.class);
        context.startService(intent1);
    }
}
