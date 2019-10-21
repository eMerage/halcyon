package com.halcyon;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Telephony;

import com.halcyon.channelbridge.BuildConfig;
import com.halcyon.channelbridgehelp.SmsBroadcastReceiver;

public class Halcyon extends Application {


    SmsBroadcastReceiver smsBroadcastReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
       // smsBroadcastReceiver = new SmsBroadcastReceiver(BuildConfig.SERVICE_NUMBER, BuildConfig.SERVICE_CONDITION);
       // registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

    }


    @Override
    public void onTerminate() {

        super.onTerminate();
    }



}
