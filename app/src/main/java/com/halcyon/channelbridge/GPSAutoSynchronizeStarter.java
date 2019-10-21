package com.halcyon.channelbridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by srinath1983 on 9/25/2014.
 */
public class GPSAutoSynchronizeStarter extends BroadcastReceiver

{

    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, GPSAutoSynchronize.class);
        System.out.println("MyStartGPSServiceReceiver ");
        context.startService(service);
    }
}
