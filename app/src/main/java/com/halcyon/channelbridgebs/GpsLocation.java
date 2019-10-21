package com.halcyon.channelbridgebs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by srinath1983 on 9/25/2014.
 */
public class GpsLocation extends Activity {


    Location location;
    double lat, lng;
    Context context;
    private LocationManager locationManager;


    public GpsLocation(Context context) {

    }

    public String GetGps() {
        String gps = "";
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, (android.location.LocationListener) this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location == null) {


            showGPSDisabledAlertToUser();
        } else {

            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());

            String la = Double.toString(lat);
            String lo = Double.toString(lng);
            gps = la + "," + lo;
            //Toast.makeText(this, gps + "New GPS Upload", Toast.LENGTH_SHORT).show();

        }

        return gps;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                // GetGPS();
                                // dialog.cancel();


                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}
