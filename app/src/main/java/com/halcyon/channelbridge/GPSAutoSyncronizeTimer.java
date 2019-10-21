package com.halcyon.channelbridge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by srinath1983 on 9/25/2014.
 */
public class GPSAutoSyncronizeTimer extends BroadcastReceiver {
    // Restart service every 30 seconds
    // for 1 min 60000
    // for 15 mins 900000
    private static final long REPEAT_TIME = 180000;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, GPSAutoSynchronizeStarter.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        // Start 30 seconds after boot completed
        cal.add(Calendar.SECOND, 10);
        //
        // Fetch every 30 seconds
        // InexactRepeating allows Android to optimize the energy consumption

        System.out.println("GPS Auto sync this shit ");

//	    service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//	        cal.getTimeInMillis(), REPEAT_TIME, pending);

        service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                REPEAT_TIME, pending);

        System.out.println("GPS Auto sync this shit 2");

    }
}
