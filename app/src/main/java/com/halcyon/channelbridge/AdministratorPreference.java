package com.halcyon.channelbridge;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridgebs.AutoBackupService;
import com.halcyon.channelbridgedb.Sequence;

public class AdministratorPreference extends PreferenceActivity implements OnPreferenceClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(com.halcyon.channelbridge.R.xml.administrator_preference);

        findPreference("etPrefLastInvoiceNumber").setOnPreferenceClickListener(this);
        findPreference("etPrefLastCustomerNumber").setOnPreferenceClickListener(this);
        findPreference("prefBackupDays").setOnPreferenceClickListener(this);
        //findPreference("cbPrefEnableCheckDetails").setOnPreferenceClickListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            Intent itineraryIntent = new Intent(this, LoginActivity.class);
            startActivity(itineraryIntent);
        }
        return super.onKeyDown(keyCode, event);
    }


    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("etPrefLastInvoiceNumber")) {
            final Dialog reason = new Dialog(AdministratorPreference.this);
            reason.setContentView(com.halcyon.channelbridge.R.layout.extra_customer_reason_popup);
            reason.setTitle("Alert");
            TextView tViewTitle = (TextView) reason.findViewById(com.halcyon.channelbridge.R.id.tvMessage);
            final EditText txtReason = (EditText) reason.findViewById(com.halcyon.channelbridge.R.id.etReason);
            Button btnSaveReason = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bSave);
            Button btnCancelPopup = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bCancel);
            tViewTitle.setText("Enter the last Invoice Number");
            reason.setCanceledOnTouchOutside(false);

            btnCancelPopup.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    reason.dismiss();
                }
            });

            btnSaveReason.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (!txtReason.getText().toString().isEmpty()) {
                        if (isNumeric(txtReason.getText().toString())) {
                            Sequence sequence = new Sequence(AdministratorPreference.this);
                            sequence.openReadableDatabase();
                            String lrid = sequence.getLastRowId("invoice");
                            sequence.closeDatabase();

                            if (lrid.contentEquals("0")) {
                                Log.w("INSERT", "INSERT");
                                sequence.openWritableDatabase();
                                long result = sequence.insertSequence(txtReason.getText().toString(), "invoice");
                                Log.w("number = ", txtReason.getText().toString());
                                sequence.closeDatabase();
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("ReturnNumber", txtReason.getText().toString());
                                editor.commit();
                                if (result != -1) {
                                    Toast.makeText(AdministratorPreference.this, "Success!", Toast.LENGTH_SHORT).show();
                                    reason.dismiss();
                                    sequence.openReadableDatabase();
                                    String lr = sequence.getLastRowId("invoice");
                                    sequence.closeDatabase();
                                    Log.w("LAST ROW ID", lr);
                                } else {
                                    Toast.makeText(AdministratorPreference.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.w("UPDATE", "UPDATE");
                                sequence.openWritableDatabase();
                                long result = sequence.setlastInvoiceNumber(txtReason.getText().toString(), "invoice");
                                Log.w("number = ", txtReason.getText().toString());
                                sequence.closeDatabase();
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("ReturnNumber", txtReason.getText().toString());
                                editor.commit();
                                if (result != -1) {
                                    Toast.makeText(AdministratorPreference.this, "Success!", Toast.LENGTH_SHORT).show();
                                    reason.dismiss();
                                    sequence.openReadableDatabase();
                                    String lr = sequence.getLastRowId("invoice");
                                    sequence.closeDatabase();
                                    Log.w("LAST ROW ID", lr);
                                } else {
                                    Toast.makeText(AdministratorPreference.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                                }
                            }


                        } else {
                            Toast.makeText(AdministratorPreference.this, "Should be numeric!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AdministratorPreference.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            reason.show();
            return true;
        } else if (preference.getKey().equals("etPrefLastCustomerNumber")) {
            final Dialog reason = new Dialog(AdministratorPreference.this);
            reason.setContentView(com.halcyon.channelbridge.R.layout.extra_customer_reason_popup);
            reason.setTitle("Alert");
            TextView tViewTitle = (TextView) reason.findViewById(com.halcyon.channelbridge.R.id.tvMessage);
            final EditText txtReason = (EditText) reason.findViewById(com.halcyon.channelbridge.R.id.etReason);
            Button btnSaveReason = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bSave);
            Button btnCancelPopup = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bCancel);
            tViewTitle.setText("Enter the last Customer Number");
            reason.setCanceledOnTouchOutside(false);

            btnCancelPopup.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    reason.dismiss();
                }
            });

            btnSaveReason.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (!txtReason.getText().toString().isEmpty()) {
                        if (isNumeric(txtReason.getText().toString())) {
                            Sequence sequence = new Sequence(AdministratorPreference.this);
                            sequence.openReadableDatabase();
                            String lrid = sequence.getLastRowId("customers_pending_approval");
                            sequence.closeDatabase();

                            if (lrid.contentEquals("0")) {
                                Log.w("INSERT", "INSERT");
                                sequence.openWritableDatabase();
                                long result = sequence.insertSequence(txtReason.getText().toString(), "customers_pending_approval");
                                Log.w("number = ", txtReason.getText().toString());
                                sequence.closeDatabase();
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("CustomerNumber", txtReason.getText().toString());
                                editor.commit();
                                if (result != -1) {
                                    Toast.makeText(AdministratorPreference.this, "Success!", Toast.LENGTH_SHORT).show();
                                    reason.dismiss();
                                    sequence.openReadableDatabase();
                                    String lr = sequence.getLastRowId("customers_pending_approval");
                                    sequence.closeDatabase();
                                    Log.w("LAST ROW ID", lr);
                                } else {
                                    Toast.makeText(AdministratorPreference.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.w("UPDATE", "UPDATE");
                                sequence.openWritableDatabase();
                                long result = sequence.setlastInvoiceNumber(txtReason.getText().toString(), "customers_pending_approval");
                                Log.w("number = ", txtReason.getText().toString());
                                sequence.closeDatabase();
                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("CustomerNumber", txtReason.getText().toString());
                                editor.commit();
                                if (result != -1) {
                                    Toast.makeText(AdministratorPreference.this, "Success!", Toast.LENGTH_SHORT).show();
                                    reason.dismiss();
                                    sequence.openReadableDatabase();
                                    String lr = sequence.getLastRowId("customers_pending_approval");
                                    sequence.closeDatabase();
                                    Log.w("LAST ROW ID", lr);
                                } else {
                                    Toast.makeText(AdministratorPreference.this, "Error Saving!", Toast.LENGTH_SHORT).show();
                                }
                            }


                        } else {
                            Toast.makeText(AdministratorPreference.this, "Should be numeric!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AdministratorPreference.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            reason.show();
            return true;
        } else if (preference.getKey().equals("prefBackupDays")) {
            final Dialog reason = new Dialog(AdministratorPreference.this);
            reason.setContentView(com.halcyon.channelbridge.R.layout.extra_customer_reason_popup);
            reason.setTitle("Alert");
            TextView tViewTitle = (TextView) reason.findViewById(com.halcyon.channelbridge.R.id.tvMessage);
            final EditText txtReason = (EditText) reason.findViewById(com.halcyon.channelbridge.R.id.etReason);
            Button btnSaveReason = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bSave);
            Button btnCancelPopup = (Button) reason.findViewById(com.halcyon.channelbridge.R.id.bCancel);
            tViewTitle.setText("Enter the number of days to backup");
            reason.setCanceledOnTouchOutside(false);

            btnSaveReason.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    if (isNumeric(txtReason.getText().toString())) {
                        Log.e(">>>>>>>>>>>>>>>>>", "SAVing");
                        saveTime(Integer.parseInt(txtReason.getText().toString()));
                    } else {
                        Toast.makeText(AdministratorPreference.this, "Enter a valid number! ", Toast.LENGTH_SHORT).show();
                    }
                    reason.dismiss();
                }
            });
            btnCancelPopup.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    reason.dismiss();
                }
            });
            reason.show();
            return true;
        } else if (preference.getKey().equals("cbPrefEnableCheckDetails")) {
            String s = "sdfdsf";
            return true;
        }

        return false;
    }

    private void saveTime(int days) {
        long current = Calendar.getInstance().getTimeInMillis();
        long interval = TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        Log.e(">>>>>>>>>>>>>>>>>", "saveTime");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Editor editor = preferences.edit();
        editor.putLong(AutoBackupService.ALARM_REPEAT_START_DATE, current);
        editor.putLong(AutoBackupService.ALARM_WAKEUP_INTERVAL, interval);
        editor.commit();
        setAlarm(current, interval);
    }

    private void setAlarm(long startDate, long interval) {
        Log.e(">>>>>>>>>>>>>>>>>", "setAlarm");
        Intent backupIntent = new Intent(AdministratorPreference.this.getApplicationContext(), AutoBackupService.class);
        PendingIntent pendingIntent = PendingIntent.getService(AdministratorPreference.this.getApplicationContext(), AutoBackupService.REQUEST_CODE, backupIntent, IntentService.START_FLAG_RETRY);
        AlarmManager alarmManager = (AlarmManager) AdministratorPreference.this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startDate, interval, pendingIntent);
    }

}
