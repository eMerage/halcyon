package com.halcyon.channelbridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AutoSynchronizeStarter extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AutoSynchronize.class);
        System.out.println("MyStartServiceReceiver ");
        context.startService(service);
    }
}