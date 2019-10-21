package com.halcyon.channelbridgebs;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.Rep_GPS;
import com.halcyon.channelbridgedb.UserLogin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by srinath1983 on 9/26/2014.
 */
public class GPSTask extends AsyncTask<String, Integer, Integer> implements android.location.LocationListener {
    private static final String TAG = "BOOMBOOMTESTGPS";

    private final Context context;
    Activity activity;
    Location location;
    double lat = 0, lng = 0;
    String la = "0", lo = "0";
    private LocationManager locationManager;
    private LocationManager mLocationManager = null;

    public GPSTask(Context context) {

        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);

    }


    protected void onPostExecute(Integer returnCode) {

        super.onPostExecute(returnCode);
        if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Device GPS not enable please enable GPS option.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "GPS Get Successfully.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            try {

                Toast notificationToast = Toast.makeText(context, "GPS not available in this place.", Toast.LENGTH_SHORT);
                notificationToast.show();

            } catch (Exception e) {
                Log.w("Log", "Download  error gps: "
                        + e.toString());

            }

        }


    }

    @Override
    protected Integer doInBackground(String... strings) {
        int returnValue = 1;

        try {
            Log.w("Log", " Called gps: "
            );
            returnValue = 1;
            //  showGPSDisabledAlertToUser();
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            try {
                lat = (double) (location.getLatitude());
                lng = (double) (location.getLongitude());
                la = Double.toString(lat);
                lo = Double.toString(lng);
            } catch (Exception e) {
                e.printStackTrace();
            }


            String deviceId = "";

            UserLogin login = new UserLogin(context);
            login.openReadableDatabase();
            ArrayList<String[]> users = login.getAllUsersDetails();
            login.closeDatabase();

            if (users.size() > 0) {
                deviceId = users.get(0)[6];
            }


            Rep_GPS ob = new Rep_GPS(context);
            String timeStamp = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm:ss").format(new Date());
            ob.openWritableDatabase();
            Long result = ob.insertGPS(la, lo, timeStamp, deviceId);
            ob.closeDatabase();
            returnValue = 2;
                /*    } else {

                        returnValue = 3;
                        showGPSDisabledAlertToUser();

                    }*/


            Log.w("Log", "gps called finish: "
            );

        } catch (Exception e) {
            Log.w("Log", "Download  error  gps: "
                    + e.toString());

        }


        return returnValue;
    }


    public void onLocationChanged(Location location) {

        try {
            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());
            la = Double.toString(lat);
            lo = Double.toString(lng);

           /* String deviceId = "";
            UserLogin login = new UserLogin(context);
            login.openReadableDatabase();
            ArrayList<String[]> users = login.getAllUsersDetails();
            login.closeDatabase();

            if (users.size() > 0) {
                deviceId = users.get(0)[6];
            }


            Rep_GPS ob = new Rep_GPS(context);
            String timeStamp = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm:ss").format(new Date());
            ob.openWritableDatabase();
            Long result = ob.insertGPS(la, lo, timeStamp, deviceId);
            ob.closeDatabase();*/


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onStatusChanged(String s, int i, Bundle bundle) {


    }

    public void onProviderEnabled(String s) {
      /*  try {
            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());
            la = Double.toString(lat);
            lo = Double.toString(lng);

            String deviceId = "";

            UserLogin login = new UserLogin(context);
            login.openReadableDatabase();
            ArrayList<String[]> users = login.getAllUsersDetails();
            login.closeDatabase();

            if (users.size() > 0) {
                deviceId = users.get(0)[6];
            }


            Rep_GPS ob = new Rep_GPS(context);
            String timeStamp = new SimpleDateFormat(
                    "MM/dd/yyyy HH:mm:ss").format(new Date());
            ob.openWritableDatabase();
            Long result = ob.insertGPS(la, lo, timeStamp, deviceId);
            ob.closeDatabase();

        }catch (Exception e){
            e.printStackTrace();
        }*/


    }

    public void onProviderDisabled(String s) {


    }


}
