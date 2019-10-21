package com.halcyon.channelbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgebs.UploadMacAddresstask;
import com.halcyon.channelbridgedb.DatabaseHelper;
import com.halcyon.channelbridgedb.UserLogin;

public class Preferences extends PreferenceActivity {

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);
        findPreference("pChangePassword").setOnPreferenceClickListener(
                new PreferenceOnClickListener());
        findPreference("pBackupDatabase").setOnPreferenceClickListener(
                new PreferenceOnClickListener());
        findPreference("etPrefEnterMac").setOnPreferenceClickListener(
                new PreferenceOnClickListener());


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent itineraryIntent = new Intent(this, ItineraryList.class);
            finish();
            startActivity(itineraryIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {

            case 1:

                builder.setMessage("Unable to Upload data")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alert = builder.create();
                return alert;

            case 2:

                builder.setMessage("Data uploaded successfully")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertTwo = builder.create();
                return alertTwo;

            case 3:

                builder.setMessage(
                        "There is no Internet Connectivity, Please check network connectivity.")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertThree = builder.create();
                return alertThree;

            case 4:

                builder.setMessage("Theres no data to upload")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertFour = builder.create();
                return alertFour;

            case 5:

                builder.setMessage("Data downloaded successfully")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertFive = builder.create();
                return alertFive;

            case 6:

                builder.setMessage("Theres no data to download")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertSix = builder.create();
                return alertSix;

            case 7:

                builder.setMessage("Unable to save data")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alertSeven = builder.create();
                return alertSeven;

        }

        return null;
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

    private class PreferenceOnClickListener implements
            Preference.OnPreferenceClickListener {
        public boolean onPreferenceClick(Preference preference) {
            // Do something...

            if (preference.getKey().equals("pChangePassword")) {

                Log.w("Log", "PreferenceOnClickListener pChangePassword");
                final Dialog changePasswordDialog = new Dialog(Preferences.this);
                changePasswordDialog
                        .setContentView(R.layout.password_change_popup);
                changePasswordDialog.setTitle("Change Password");
                final EditText txtOldPassword = (EditText) changePasswordDialog
                        .findViewById(R.id.etOldPassWord);
                final EditText txtNewPassword = (EditText) changePasswordDialog
                        .findViewById(R.id.etNewPassword);
                final EditText txtRetypeNewPassword = (EditText) changePasswordDialog
                        .findViewById(R.id.etRetypeNewPassword);
                final CheckBox cBoxShowPassword = (CheckBox) changePasswordDialog
                        .findViewById(R.id.cbShowPassword);
                Button btnChangePassword = (Button) changePasswordDialog
                        .findViewById(R.id.bChangePassword);
                Button btnCancel = (Button) changePasswordDialog
                        .findViewById(R.id.bCancel);

                changePasswordDialog.show();

                cBoxShowPassword.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (cBoxShowPassword.isChecked()) {
                            txtOldPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            txtNewPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            txtRetypeNewPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else if (!cBoxShowPassword.isChecked()) {
                            // txtOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // txtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // txtRetypeNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            txtOldPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txtNewPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txtRetypeNewPassword
                                    .setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }

                    }
                });
                // cBoxShowPassword.setOnCheckedChangeListener(new
                // OnCheckedChangeListener() {
                //
                // public void onCheckedChanged(CompoundButton buttonView,
                // boolean isChecked) {
                // // TODO Auto-generated method stub
                //
                //
                // }
                // });

                btnCancel.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        changePasswordDialog.dismiss();
                    }
                });

                btnChangePassword
                        .setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                try {
                                    String oldPassword = new Utility(
                                            Preferences.this)
                                            .encryptString(txtOldPassword
                                                    .getText().toString()
                                                    .trim());
                                    String newPassword = txtNewPassword
                                            .getText().toString().trim();
                                    String retypePassword = txtRetypeNewPassword
                                            .getText().toString().trim();
                                    ArrayList<String> userDetails = new ArrayList<String>();
                                    UserLogin userLogin = new UserLogin(
                                            Preferences.this);

                                    if (!oldPassword.isEmpty()) {
                                        if (!newPassword.isEmpty()) {
                                            if (!retypePassword.isEmpty()) {

                                                SharedPreferences sharedPreferences = PreferenceManager
                                                        .getDefaultSharedPreferences(getBaseContext());
                                                String usrLogin = sharedPreferences.getString("UserLogin", "-1");

                                                userLogin
                                                        .openReadableDatabase();
                                                userDetails = userLogin
                                                        .getUserDetailsFromRowId(usrLogin);
                                                userLogin.closeDatabase();

                                                if (userDetails.get(3)
                                                        .contentEquals(
                                                                oldPassword)) {
                                                    if (newPassword
                                                            .contentEquals(retypePassword)) {
                                                        String newEncryptedPassword = new Utility(
                                                                Preferences.this)
                                                                .encryptString(newPassword);
                                                        userLogin
                                                                .openWritableDatabase();
                                                        userLogin
                                                                .changeUserPassword(
                                                                        userDetails
                                                                                .get(0),
                                                                        newEncryptedPassword);
                                                        userLogin
                                                                .closeDatabase();
                                                        txtOldPassword
                                                                .setText(null);
                                                        txtNewPassword
                                                                .setText(null);
                                                        txtRetypeNewPassword
                                                                .setText(null);
                                                        changePasswordDialog
                                                                .dismiss();

                                                    } else {
                                                        Toast passwordsDoNotMatch = Toast
                                                                .makeText(
                                                                        Preferences.this,
                                                                        "Passwords do not match!",
                                                                        Toast.LENGTH_SHORT);
                                                        passwordsDoNotMatch
                                                                .setGravity(
                                                                        Gravity.TOP,
                                                                        100,
                                                                        100);
                                                        passwordsDoNotMatch
                                                                .show();
                                                        txtNewPassword
                                                                .setText(null);
                                                        txtRetypeNewPassword
                                                                .setText(null);
                                                        txtNewPassword
                                                                .setFocusable(true);
                                                        txtNewPassword
                                                                .requestFocus();
                                                    }
                                                } else {
                                                    Toast invalidOldPassword = Toast
                                                            .makeText(
                                                                    Preferences.this,
                                                                    "Invalid old password!",
                                                                    Toast.LENGTH_SHORT);
                                                    invalidOldPassword
                                                            .setGravity(
                                                                    Gravity.TOP,
                                                                    100, 100);
                                                    invalidOldPassword.show();
                                                    txtOldPassword
                                                            .setText(null);
                                                    txtNewPassword
                                                            .setText(null);
                                                    txtRetypeNewPassword
                                                            .setText(null);
                                                    txtOldPassword
                                                            .setFocusable(true);
                                                    txtOldPassword
                                                            .requestFocus();
                                                }
                                            } else {
                                                Toast retypePasswordFieldEmpty = Toast
                                                        .makeText(
                                                                Preferences.this,
                                                                "The Retype password field cannot be left blank!",
                                                                Toast.LENGTH_SHORT);
                                                retypePasswordFieldEmpty
                                                        .setGravity(
                                                                Gravity.TOP,
                                                                100, 100);
                                                retypePasswordFieldEmpty.show();
                                                txtNewPassword.setText(null);
                                                txtRetypeNewPassword
                                                        .setText(null);
                                                txtNewPassword
                                                        .setFocusable(true);
                                                txtNewPassword.requestFocus();
                                            }
                                        } else {
                                            Toast newPasswordFieldEmpty = Toast
                                                    .makeText(
                                                            Preferences.this,
                                                            "Type in the new password",
                                                            Toast.LENGTH_SHORT);
                                            newPasswordFieldEmpty.setGravity(
                                                    Gravity.TOP, 100, 100);
                                            newPasswordFieldEmpty.show();
                                            txtOldPassword.setText(null);
                                            txtNewPassword.setText(null);
                                            txtRetypeNewPassword.setText(null);
                                            txtOldPassword.setFocusable(true);
                                            txtOldPassword.requestFocus();
                                        }
                                    } else {
                                        Toast oldPasswordEmpty = Toast
                                                .makeText(
                                                        Preferences.this,
                                                        "You must specify the old password!",
                                                        Toast.LENGTH_SHORT);
                                        oldPasswordEmpty.setGravity(
                                                Gravity.TOP, 100, 100);
                                        oldPasswordEmpty.show();
                                        txtNewPassword.setText(null);
                                        txtRetypeNewPassword.setText(null);
                                        txtNewPassword.setFocusable(true);
                                        txtNewPassword.requestFocus();
                                    }
                                } catch (Exception e) {
                                    Log.w("Preferences",
                                            "Unable to change password"
                                                    + e.toString());
                                }

                            }
                        });

            } else if (preference.getKey().equals("pBackupDatabase")) {
                Log.w("Log", "PreferenceOnClickListener pBackupDatabase");

//				File file = getBaseContext().getDatabasePath(DatabaseName);
//				FileInputStream fis = new FileInputStream(file);
//				String outputDB = Environment.getExternalStorageDirectory() +"/<your directory>/<backup database name>";
//				OutputStream os = new FileOutputStream(outputDB);
//
//				// Transfer bytes from the inputfile to the outputfile
//				byte[] buffer = new byte[1024];
//				int length;
//				while ((length = fis.read(buffer))>0) {
//				       os.write(buffer, 0, length);
//				}
//				// Close the streams
//				os.flush();
//				os.close();
//				fis.close();
//
                try {
                    File file = getBaseContext().getDatabasePath("channel_bridge_db"); //"/data/data/com.marina.channelbridge/databases/channel_bridge_db.db";
//				    File dbFile = new File(inFileName);
                    FileInputStream fis;

                    fis = new FileInputStream(file);

                    String version = "0";

                    PackageInfo pInfo;
                    try {
                        pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = pInfo.versionName;
                    } catch (NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Integer databaseVersion = DatabaseHelper.DATABASE_VERSION;
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());

                    String outFileName = Environment.getExternalStorageDirectory() + "/Version-" + version + "_DBVersion-" + databaseVersion + "_Date-" + timeStamp + ".db";

                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(outFileName);

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fis.close();


                    AlertDialog alertDialog = new AlertDialog.Builder(Preferences.this).create();
                    alertDialog.setTitle("Successful");
                    alertDialog
                            .setMessage("File Saved to the location : " + outFileName);
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    alertDialog.show();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }else if(preference.getKey().equals("etPrefEnterMac")){


                final Dialog macDialog = new Dialog(Preferences.this);
                macDialog
                        .setContentView(R.layout.mac_address_popup);
                macDialog.setTitle("Enter Printer mac Address");
                final EditText edMac= (EditText) macDialog
                        .findViewById(R.id.edmacAd);

                macDialog.show();

                Button btnEnter = (Button) macDialog
                        .findViewById(R.id.btEnter);
                btnEnter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(edMac.getText().toString().isEmpty()){
                            Toast.makeText(Preferences.this,"Please enter mac address",Toast.LENGTH_LONG).show();
                        }else{
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            preferences.edit().putString("etPrefEnterMac",edMac.getText().toString()).commit();


                            String deviceId = preferences.getString("DeviceId", "-1");
                            String repId = preferences.getString("RepId", "-1");
                            new UploadMacAddresstask(Preferences.this,edMac.getText().toString()).execute();
                            macDialog.dismiss();
                        }

                    }
                });


            }
//			else if (preference.getKey().equals("pUploadImages")) {
//				Log.w("Log", "PreferenceOnClickListener pUploadImages");
//				new UploadCustomerImageTask(Preferences.this).execute("1");
//
//			}

            return false;
        }
    }

}
