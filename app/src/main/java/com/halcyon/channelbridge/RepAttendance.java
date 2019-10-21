package com.halcyon.channelbridge;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgebs.UploadAttendenceTask;
import com.halcyon.channelbridgedb.Attendence;
import com.halcyon.channelbridgedb.UserLogin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by srinath1983 on 9/29/2014.
 */
public class RepAttendance extends Activity implements LocationListener {

    Spinner spnLocale;
    Location location;
    double lat, lng;
    Button btnIN, btnOut, btnback;
    Builder alertExit;
    AlertDialog alertDialog;
    private LocationManager locationManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.halcyon.channelbridge.R.layout.rep_addendence);

        final EditText txtcomments = (EditText) findViewById(R.id.txCom);
        btnIN = (Button) findViewById(R.id.btIn);
        btnOut = (Button) findViewById(R.id.btout);
        spnLocale = (Spinner) findViewById(R.id.etLocation);
        btnback = (Button) findViewById(R.id.btback);

        // checkINAttendence();
        // checkOutAttendence();

        //  btnOut.setEnabled(false);
        // checkINAttendence();
        //  checkOutAttendence();
        spnLocale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                checkINAttendence();
                checkOutAttendence();


            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnIN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String time = new SimpleDateFormat(
                        "HH:mm").format(new Date());

                String timeStamp = new SimpleDateFormat(
                        "MM/dd/yyyy").format(new Date());

                String deviceId = "";

                UserLogin login = new UserLogin(RepAttendance.this);
                login.openReadableDatabase();
                ArrayList<String[]> users = login.getAllUsersDetails();
                login.closeDatabase();

                if (users.size() > 0) {
                    deviceId = users.get(0)[6];
                }
                if (!checkGPSEnable())
                    showGPSDisabledAlertToUser();
                else {
                    String[] GPS = GetGPS().split("-");
                    Attendence attendence = new Attendence(RepAttendance.this);
                    attendence.openWritableDatabase();
                    attendence.insertRepAttendence(deviceId, time, GPS[0], GPS[1], spnLocale.getSelectedItem().toString(), txtcomments.getText().toString(), timeStamp, "1");

                    attendence.closeDatabase();
                    txtcomments.setText("");
                    checkINAttendence();
                    Toast.makeText(RepAttendance.this, "Rep attendence save successfully.", Toast.LENGTH_SHORT).show();
                    new UploadAttendenceTask(RepAttendance.this).execute();
                }


            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String time = new SimpleDateFormat(
                        "HH:mm").format(new Date());

                String timeStamp = new SimpleDateFormat(
                        "MM/dd/yyyy").format(new Date());

                String deviceId = "";

                UserLogin login = new UserLogin(RepAttendance.this);
                login.openReadableDatabase();
                ArrayList<String[]> users = login.getAllUsersDetails();
                login.closeDatabase();

                if (users.size() > 0) {
                    deviceId = users.get(0)[6];
                }
                if (!checkGPSEnable())
                    showGPSDisabledAlertToUser();
                else {
                    String[] GPS = GetGPS().split("-");
                    Attendence attendence = new Attendence(RepAttendance.this);
                    attendence.openWritableDatabase();
                    attendence.insertRepAttendence(deviceId, time, GPS[0], GPS[1], spnLocale.getSelectedItem().toString(), txtcomments.getText().toString(), timeStamp, "0");

                    attendence.closeDatabase();
                    txtcomments.setText("");
                    checkOutAttendence();
                    Toast.makeText(RepAttendance.this, "Rep attendence save successfully.", Toast.LENGTH_SHORT).show();
                    new UploadAttendenceTask(RepAttendance.this).execute();
                }


            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // finish();
                //  Intent iternaryListActivity = new Intent(
                // "com.halcyon.channelbridge.ITINERARYLIST");
                // startActivity(iternaryListActivity);

                exitByBackKey();

            }
        });


    }

    private String GetGPS() {

        String GPS = "";
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location == null)

            GPS = "0" + "-" + "0";
            //showGPSDisabledAlertToUser();

        else {

            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());

            String la = Double.toString(lat);
            String lo = Double.toString(lng);
            GPS = la + "-" + lo;

        }
        return GPS;
    }

    public void onLocationChanged(Location location) {
        this.location = location;
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }

    private boolean checkGPSEnable() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location == null)
            return false;
        else
            return true;


    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent iternaryListActivity = new Intent(
                                        "com.halcyon.channelbridge.ITINERARYLIST");
                                startActivity(iternaryListActivity);
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                // GetGPS();
                                // dialog.cancel();

                                finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  dialog.cancel();
//                        Intent iternaryListActivity = new Intent(
//                                "com.halcyon.channelbridge.ITINERARYLIST");
//                        startActivity(iternaryListActivity);
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void checkINAttendence() {

        String timeStamp = new SimpleDateFormat(
                "MM/dd/yyyy").format(new Date());


        Attendence attendence = new Attendence(RepAttendance.this);
        attendence.openWritableDatabase();
        boolean isActive = attendence.isInActive(spnLocale.getSelectedItem().toString(), timeStamp);
        attendence.closeDatabase();

        if (isActive) {

            btnOut.setEnabled(true);
            btnIN.setEnabled(false);
        } else {
            btnIN.setEnabled(true);
            btnOut.setEnabled(false);
        }


    }

    private void checkOutAttendence() {

        String timeStamp = new SimpleDateFormat(
                "MM/dd/yyyy").format(new Date());
        Attendence attendence1 = new Attendence(RepAttendance.this);
        attendence1.openWritableDatabase();
        boolean isActive1 = attendence1.isOutActive(spnLocale.getSelectedItem().toString(), timeStamp);
        attendence1.closeDatabase();
        if (isActive1) {
            btnOut.setEnabled(false);
            btnIN.setEnabled(false);
        }

        //   else
        //   {
        //     btnIN.setEnabled(false);
        //     btnOut.setEnabled(true);

        //  }


    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }


    private void exitByBackKey() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RepAttendance.this);
        builder.setTitle("Warning");
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                finish();
                Intent iternaryListActivity = new Intent(
                        "com.halcyon.channelbridge.ITINERARYLIST");
                startActivity(iternaryListActivity);
            }
        });
        builder.show();

    }
}
