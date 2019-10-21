package com.halcyon.channelbridgehelp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridge.LoginActivity;
import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Approval_Details;
import com.halcyon.channelbridgedb.Reps;

public class SmsBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "SmsBroadcastReceiver";




    public SmsBroadcastReceiver() {
    }



    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            String cordeintroNumbre;

            Reps reps = new Reps(context);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final String repId = sharedPreferences.getString("RepId", "-1");

            cordeintroNumbre =normalizePhoneNumber(reps.getASM(repId));




            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }




            if(normalizePhoneNumber(smsSender).equals(cordeintroNumbre)){
                setApprovel(context,smsBody,smsSender);
            }else {

            }




        }

    }

    public void setApprovel(Context context,String code,String num){
        boolean updateResult=false;
        Approval_Details approval_details = new Approval_Details(context);
        updateResult= approval_details.updateAccessStstus(code);

        if(updateResult){
            notification(context,"Remark","Remark Approved");
            sendSMS(num,"Remark Approved Successfully");
        }else {
            sendSMS(num,"Remark Approved Not successfully");
        }

    }

    public boolean sendSMS(String number, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public String normalizePhoneNumber(String number) {

        number = number.replaceAll("[^+0-9]", ""); // All weird characters such as /, -, ...

        String country_code = "94";

        if (number.substring(0, 1).compareTo("0") == 0 && number.substring(1, 2).compareTo("0") != 0) {
            number = "+" + country_code + number.substring(1); // e.g. 0172 12 34 567 -> + (country_code) 172 12 34 567
        }

        number = number.replaceAll("^[0]{1,4}", "+"); // e.g. 004912345678 -> +4912345678

        return number;
    }

    public void notification(Context context,String titel,String message){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.cb_logo_icon)
                .setContentTitle(titel)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(message);
        Intent resultIntent = null;
        resultIntent = new Intent(context, LoginActivity.class);

        TaskStackBuilder stackBuilder = null;
        PendingIntent resultPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        }

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }
}
