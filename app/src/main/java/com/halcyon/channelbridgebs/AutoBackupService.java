package com.halcyon.channelbridgebs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.DatabaseHelper;

public class AutoBackupService extends IntentService {

	
	public static final String ALARM_WAKEUP_INTERVAL= "alarm_wake_up_interval";
	public static final String ALARM_REPEAT_START_DATE = "alarm_repeat_start_date";
	public static final int REQUEST_CODE = 1000;
	public static final String BACKUP_NAME = "backup_name";
	public static final String BACKUP_REQUIRED = "backup_required";
	
	public AutoBackupService() {
		super(AutoBackupService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		new BackupDatabase().execute();
		Log.e(">>>>>>>>>>>>>>>>>", "onHandleIntent");
		

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

    private class BackupDatabase extends AsyncTask<Void, Void, Boolean> {

        private String dbName;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.e(">>>>>>>>>>>>>>>>>", "BackupDatabase = doInBackground ");
                File file = getBaseContext().getDatabasePath("channel_bridge_db"); //"/data/data/com.marina.channelbridge/databases/channel_bridge_db.db";
                FileInputStream fis;
                fis = new FileInputStream(file);
                String version = "0";

                PackageInfo pInfo;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoBackupService.this);
                String deviceId = sp.getString("DeviceId", "default_device_id");

                Integer databaseVersion = DatabaseHelper.DATABASE_VERSION;
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());

                dbName = "Device-" + deviceId + "_Version-" + version + "_DBVersion-" + databaseVersion + "_Date-" + timeStamp + ".db";
                String dbFile = Environment.getExternalStorageDirectory() + "/Device-" + deviceId + "_Version-" + version + "_DBVersion-" + databaseVersion + "_Date-" + timeStamp + ".db";
                // Open the empty db as the output stream
                OutputStream output = new FileOutputStream(dbFile);

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
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(AutoBackupService.this, "Database successfully backed up!", Toast.LENGTH_SHORT).show();
                if (isOnline()) {
                    Log.e(">>>>>>>>>>>>>>>>>", "BackupDatabase = onPostExecute = isOnline");
                    new UploadBackupDatabase(dbName, AutoBackupService.this).execute();
                } else {
                    Log.e(">>>>>>>>>>>>>>>>>", "BackupDatabase = onPostExecute = NOT ONLINE");
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AutoBackupService.this);
                    sp.edit().putString(BACKUP_NAME, dbName).putBoolean(BACKUP_REQUIRED, true).commit();
                }
            }
        }
    }
}
