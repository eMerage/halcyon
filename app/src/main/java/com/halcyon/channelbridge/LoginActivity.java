package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.UserLogin;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends Activity {
    //
    EditText txtUserName, txtPassword;
    Button btnSignIn, btnCancel;
    AlertDialog alertDialog;
    Builder alertExit;
    Location location;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        txtUserName = (EditText) findViewById(R.id.etUserName);
        txtPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.bSignIn);
        btnCancel = (Button) findViewById(R.id.bCancel);

        txtUserName.setFocusable(true);
        txtUserName.requestFocus();


        alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alertExit = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(
                        "Are you sure you want to exit?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });


        txtUserName.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!txtUserName.getText().toString().isEmpty()) {
                    txtPassword.setFocusable(true);
                    txtPassword.requestFocus();
                }
            }
        });

        txtPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                login();
            }
        });


        btnSignIn.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                login();

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {


            public void onClick(View v) {
                // TODO Auto-generated method stub
                alertExit.show();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            alertExit.show();
        }


        return super.onKeyDown(keyCode, event);
    }

    public void login() {
        UserLogin userLoginObject = new UserLogin(LoginActivity.this);
        // userLoginObject.op
        // enWritableDatabase();

        if (txtUserName.getText().toString().contentEquals("Admin")) {
            if (txtPassword.getText().toString().contentEquals("Admin@")) {
//			if (txtPassword.getText().toString().contentEquals("a")) {
                Intent adminIntent = new Intent(LoginActivity.this, AdministratorPreference.class);
                finish();
                startActivity(adminIntent);

            }

        } else {
            userLoginObject.openReadableDatabase();
            int status = userLoginObject.isUseractive(txtUserName.getText().toString());
            userLoginObject.closeDatabase();

            switch (status) {

                case 0:

                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Username is not valid");
                    alertDialog.show();
                    break;

                case 1:
                    try {
                        UserLogin userLogin = new UserLogin(LoginActivity.this);
                        userLogin.openReadableDatabase();
                        String[] userDetails = userLogin.getUserDetailsByUserName(txtUserName.getText().toString());
                        userLogin.closeDatabase();

                        //	Products p = new Products(this);
                        //	p.deleteDuplicates();


                        if (userDetails[1] != null) {
                            String enteredPassword = new Utility(LoginActivity.this).encryptString(txtPassword.getText()
                                    .toString());

                            if (enteredPassword.contentEquals(userDetails[1])) {


                                AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(LoginActivity.this);
                                autoSyncOnOffFlag.openReadableDatabase();
                                autoSyncOnOffFlag.AutoSyncActive(0);
                                // String  dbStatus=autoSyncOnOffFlag.GetAutoSyncStatus();
                                autoSyncOnOffFlag.closeDatabase();




                                new DownloadRepDetails(LoginActivity.this).execute("1");


                                // turnGPSOn();
                              //  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            //    loadRepData(sharedPreferences.getString("DeviceId", "-1"));


                                Intent iternaryListActivity = new Intent(
                                        "com.halcyon.channelbridge.ITINERARYLIST");
                                Bundle bundleToView = new Bundle();
                                bundleToView.putString("DeviceId", userDetails[2]);
                                bundleToView.putString("RepId", userDetails[3]);
                                bundleToView.putString("UserLogin", userDetails[4]);
                                iternaryListActivity.putExtras(bundleToView);
                                finish();
                                startActivity(iternaryListActivity);

                            } else {
                                alertDialog.setTitle("Warning");
                                alertDialog
                                        .setMessage("Password and Username do not match!");
                                alertDialog.show();
                            }
                        } else {
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("Cannot retrive your password please contact Administrator");
                            alertDialog.show();

                        }

                    } catch (Exception e) {
                        alertDialog.setTitle(R.string.error);
                        alertDialog.setMessage(e.toString());
                        alertDialog.show();
                        e.printStackTrace();
                    }

                    break;

                case 2:

                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("User Locked!");
                    alertDialog.show();
                    break;

                case 3:

                    alertDialog.setTitle("Warning");
                    alertDialog
                            .setMessage("Cannot Identify the user status please contact Administrator!");
                    alertDialog.show();
                    break;
                case 4:

                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("Oops! Looks Like something went wrong. Please try again or contact System Administrator");
                    alertDialog.show();
                    break;

            }
        }
    }

    private String saveUserData(ArrayList<String> repData, String dId) {

        String rtnStr = "";
        try {

            UserLogin userLogin = new UserLogin(LoginActivity.this);
            userLogin.openWritableDatabase();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());


            userLogin.closeDatabase();


            Reps reps = new Reps(LoginActivity.this);
            reps.openWritableDatabase();


            reps.insertRep(repData.get(0), repData.get(2),
                    repData.get(3), repData.get(4), repData.get(5),
                    repData.get(6), repData.get(7), repData.get(1),
                    0, timeStamp, repData.get(9), repData.get(10), repData.get(11), repData.get(18));

            reps.closeDatabase();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

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

    private class DownloadRepDetails extends AsyncTask<String, Integer, Integer> {
        private final Context context;
        ArrayList<String> response = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");

        public DownloadRepDetails(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            saveUserData(response, deviceId);

        }

        @Override
        protected Integer doInBackground(String... params) {

            if (isOnline()) {
                ArrayList<String[]> repStoreDataResponse = null;

                try {
                    WebService webService = new WebService();
                    response = webService.getRepForDevice(deviceId, getApplicationContext());

                } catch (SocketException e) {
                    e.printStackTrace();
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

            } else {

            }

            return null;
        }

    }


    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.locationserices.UseGpssatellites");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);


        /*    Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            sendBroadcast(intent);*/


        }
    }


}
