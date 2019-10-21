package com.halcyon.channelbridgehelp;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Hasitha on 5/21/15.
 */
public class ServiceChecker {

    private static  ServiceChecker instance;



    public   boolean isGPSServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("GPSAutoSynchronize".equals(service.service.getClassName())) {
                Log.w("GPS service running","=================> OK");
                return true;
            }
        }
        return false;
    }
}
