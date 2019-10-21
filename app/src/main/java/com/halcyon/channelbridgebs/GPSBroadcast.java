package com.halcyon.channelbridgebs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by srinath1983 on 9/26/2014.
 */
public class GPSBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent backupIntent = new Intent(context.getApplicationContext(), UploadGPSTask.class);
        PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), UploadGPSTask.REQUEST_CODE, backupIntent, IntentService.START_FLAG_RETRY);

        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getStartRepeatFromDate1(context.getApplicationContext()), getTimeIntervalfromPreference1(context), pendingIntent);

    }


    private long getTimeIntervalfromPreference1(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(UploadGPSTask.ALARM_WAKEUP_INTERVAL, getDefaultWakeupTime());
    }

    private long getStartRepeatFromDate1(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getLong(UploadGPSTask.ALARM_REPEAT_START_DATE, Calendar.getInstance().getTimeInMillis());
    }

    private long getDefaultWakeupTime() {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.MONTH, getNextMonth());
        return calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
    }

    private int getNextMonth() {
        int nextMonth = 0;
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth == Calendar.DECEMBER) {
            nextMonth = Calendar.JANUARY;
        } else {
            nextMonth = currentMonth + 1;
        }
        return nextMonth;
    }
}
