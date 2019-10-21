package com.halcyon.channelbridge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.halcyon.channelbridgebs.GPSTask;
import com.halcyon.channelbridgebs.UploadAttendenceTask;
import com.halcyon.channelbridgebs.UploadNewGPS;

/**
 * Created by srinath1983 on 9/25/2014.
 */
public class GPSAutoSynchronize extends Service

{

    private static final String TAG = "GPSAUTOSYNCHRONIZE";
    private final IBinder mBinder = new MyBinder();
    Context context;
    Location location;
    double lat, lng;
    private LocationManager locationManager;
    private String provider;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        context = getApplicationContext();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // use this method to call upload web service

        super.onStartCommand(intent, flags, startId);

        System.out.println("onStartCommand ");

        Log.e(TAG, "onStartCommand");

        System.out.println("inside UploadInvoiceTask Task ");
        new GPSTask(GPSAutoSynchronize.this).execute(
                "0", "0");

        if (isNetworkAvailable() == true) {

            System.out.println(" UploadGPS Task ");
            new UploadNewGPS(GPSAutoSynchronize.this).execute(
                    "0", "0");
            System.out.println(" UploadAttendence Task ");
            new UploadAttendenceTask(GPSAutoSynchronize.this).execute(
                    "0", "0");

        }


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("onBind ");
        return mBinder;
    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public class MyBinder extends Binder {
        GPSAutoSynchronize getService() {
            System.out.println("getService ");
            return GPSAutoSynchronize.this;
        }
    }

}
