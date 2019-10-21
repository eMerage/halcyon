package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.Remarks;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.List;

/**
 * Created by Amila on 6/12/15.
 */
public class UploadRemarksTask extends AsyncTask<Void, Void, Integer> {

    Context context;


    public UploadRemarksTask(Context cntxt) {
        context = cntxt;
    }


    @Override
    protected Integer doInBackground(Void... params) {

        int returnValue = 1;
        if (isNetworkAvailable() == true) {
            Remarks repRemarks = new Remarks(context);
            repRemarks.openReadableDatabase();
            List<String[]> rtnRemarks = repRemarks.getRemarksZer0("0");

            repRemarks.closeDatabase();
            Log.w("Log", "rtnGPS size :  " + rtnRemarks.size());

            for (java.lang.String[] rtnData : rtnRemarks) {
                java.lang.String[] remarksDetails = new String[12];
                remarksDetails[0] = rtnData[0];
                remarksDetails[1] = rtnData[1];
                remarksDetails[2] = rtnData[2];
                remarksDetails[3] = rtnData[3];
                remarksDetails[4] = rtnData[4];
                remarksDetails[5] = rtnData[5];
                remarksDetails[6] = rtnData[6];
                remarksDetails[7] = rtnData[7];
                remarksDetails[8] = rtnData[8];
                remarksDetails[9] = rtnData[9];
                remarksDetails[10] = rtnData[10];
                remarksDetails[11] = rtnData[11];
                String responseArr = null;

                    try {

                        WebService webService = new WebService();
                        responseArr = webService.uploadRemarks(remarksDetails);
                        //java.lang.String[] separated = responseArr.split("-");
                        // java.lang.String error = separated[0].trim();
                        if (responseArr.equals("OK")) {
                            Remarks remarks = new Remarks(context);
                            remarks.openReadableDatabase();
                            remarks.updateRemarksUpload(remarksDetails[0]);
                            remarks.closeDatabase();

                        }
                        returnValue = 2;

                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


            }

        }
        return returnValue;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        if (result == 2) {
            Toast notificationToast = Toast
                    .makeText(context, "Remarks uploaded to the server.",
                            Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (result == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "No new remarks  to upload.", Toast.LENGTH_SHORT);
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

