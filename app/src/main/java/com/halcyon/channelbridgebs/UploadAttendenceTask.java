package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.Attendence;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.List;

/**
 * Created by srinath1983 on 9/30/2014.
 */
public class UploadAttendenceTask extends AsyncTask<String, Integer, Integer> {

    Context context;

    public UploadAttendenceTask(Context cntxt) {
        context = cntxt;
    }

    @Override
    protected Integer doInBackground(String... strings) {


        int returnValue = 1;
        publishProgress(1);
        if (isNetworkAvailable() == true) {
            Attendence att = new Attendence(context);
            //  Looper.prepare();

            att.openReadableDatabase();
            List<String[]> rtnGPS = att
                    .getAttendence("0");
            att.closeDatabase();
            Log.w("Log", "rtnGPS size :  " + rtnGPS.size());

            for (String[] rtnData : rtnGPS) {
                String[] GPSDetails = new String[9];
                GPSDetails[0] = rtnData[0];
                GPSDetails[1] = rtnData[1];
                GPSDetails[2] = rtnData[2];
                GPSDetails[3] = rtnData[3];
                GPSDetails[4] = rtnData[4];
                GPSDetails[5] = rtnData[5];
                GPSDetails[6] = rtnData[6];
                GPSDetails[7] = rtnData[7];
                GPSDetails[8] = rtnData[8];

                publishProgress(2);
                String responseArr = null;
                    try {
                        WebService webService = new WebService();
                        responseArr = webService.uploadAttendence(GPSDetails);
                        String[] separated = responseArr.split("-");
                        String error = separated[0].trim();
                        if (error.equals("1")) {
                            Attendence att1 = new Attendence(context);
                            att1.openReadableDatabase();
                            att1.UpdateAttendence(GPSDetails[0], GPSDetails[8], "1");
                            att1.closeDatabase();

                        }
                        returnValue = 2;
                        //  Thread.sleep(100);

                        // } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        //     e.printStackTrace();
                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



            }
            if (rtnGPS.size() <= 0)
                returnValue = 3;
        }

        return returnValue;
    }

    @Override
    protected void onPostExecute(Integer result) {
        // TODO Auto-generated method stub

        if (result == 2) {
            Toast notificationToast = Toast
                    .makeText(context, "New attendence uploaded to the server.",
                            Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (result == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "Unable to upload new attendence to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (result == 3) {
            Toast notificationToast = Toast.makeText(context,
                    "No new attendence upload to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
    }


    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
