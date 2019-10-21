package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.Rep_GPS;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.List;

/**
 * Created by srinath1983 on 9/26/2014.
 */
public class UploadNewGPS extends AsyncTask<String, Integer, Integer> {

    Context context;


    public UploadNewGPS(Context cntxt) {
        context = cntxt;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);




    }

    @Override
    protected Integer doInBackground(String... strings)

    {
        int returnValue = 1;
        publishProgress(1);

            Rep_GPS rtnRep_GPS = new Rep_GPS(context);
            rtnRep_GPS.openReadableDatabase();
            List<String[]> rtnGPS = rtnRep_GPS.getGPS("0");
            rtnRep_GPS.closeDatabase();
            Log.w("Log", "rtnGPS size :  " + rtnGPS.size());

            for (String[] rtnData : rtnGPS) {
                String[] GPSDetails = new String[5];
                GPSDetails[0] = rtnData[0];
                GPSDetails[1] = rtnData[1];
                GPSDetails[2] = rtnData[2];
                GPSDetails[3] = rtnData[3];
                GPSDetails[4] = rtnData[4];

                publishProgress(2);
                String responseArr = null;
                 try {

                        WebService webService = new WebService();
                        responseArr = webService.uploadGPS(GPSDetails);
                        String[] separated = responseArr.split("-");
                        String error = separated[0].trim();
                        if (error.equals("1")) {
                            Rep_GPS rtnRep_GPS1 = new Rep_GPS(context);
                            rtnRep_GPS1.openReadableDatabase();

                            rtnRep_GPS1.deleteGPSByRowId(GPSDetails[0]);
                            rtnRep_GPS1.closeDatabase();

                        }
                        returnValue = 2;


                    }catch (SocketException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                 }



            }


        return returnValue;
    }

    @Override
    protected void onPostExecute(Integer result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result == 2) {
            Toast notificationToast = Toast
                    .makeText(context, "New GPS uploaded to the server.",
                            Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (result == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "There is no new GPS data to upload.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
