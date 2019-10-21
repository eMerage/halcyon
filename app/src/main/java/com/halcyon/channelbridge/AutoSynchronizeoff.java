package com.halcyon.channelbridge;

/**
 * Created by srinath1983 on 9/12/2014.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoSynchronizeoff extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AutoSynchronize.class);
        System.out.println("MyStartServiceReceiver ");
        context.stopService(service);
    }
}
