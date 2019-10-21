package com.halcyon.channelbridgebs;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by srinath1983 on 9/25/2014.
 */
public class UploadGPSTask extends IntentService {
    public static final String ALARM_WAKEUP_INTERVAL = "alarm_wake_up_interval";
    public static final String ALARM_REPEAT_START_DATE = "alarm_repeat_start_date";
    public static final int REQUEST_CODE = 1000;
    public static final String BACKUP_NAME = "backup_name";
    public static final String BACKUP_REQUIRED = "backup_required";
    Location location;
    double lat, lng;
    private LocationManager locationManager;

    public UploadGPSTask() {
        super(UploadGPSTask.class.getSimpleName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        new GetGps().execute();
        Log.e(">>>>>>>>>>>>>>>>>", "onHandleIntent");
    }

    private class GetGps extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {
            int returnValue = 1;
            try {

                 /*GpsLocation gpsLocation=new GpsLocation();
                 String gps=    gpsLocation.GetGps();
                 Rep_GPS ob=new Rep_GPS(UploadGPSTask.this);
                 String timeStamp = new SimpleDateFormat(
                         "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                 ob.openWritableDatabase();
                 Long result = ob.insertGPS(gps,gps,timeStamp,"123");
                 ob.closeDatabase();
                 returnValue=2;*/
            } catch (Exception e) {
                Log.w("Log", "Download  error: "
                        + e.toString());

            }


            return returnValue;
        }

        @Override
        protected void onPostExecute(Integer returnCode) {
            if (returnCode == 1)
                // Toast.makeText(UploadGPSTask.this, "Error Gps Upload!", Toast.LENGTH_SHORT).show();
            if (returnCode == 2)
                Toast.makeText(UploadGPSTask.this, "Gps Upload Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
