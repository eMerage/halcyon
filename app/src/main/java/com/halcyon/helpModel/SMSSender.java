package com.halcyon.helpModel;

import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by susantha on 4/23/2015.
 */
public class SMSSender extends Activity {

    private static  SmsManager mSmsManager;

    public static boolean sendMessage(String telNumber, String message){
        boolean bb=false;
        try {
            mSmsManager = SmsManager.getDefault();
            mSmsManager.sendTextMessage(telNumber,null,message,null,null);
            bb=true;
        }catch (Exception e){
           e.printStackTrace();
        }

        Log.i("msg called", telNumber + " : " + message);
        return bb;
    }
}
