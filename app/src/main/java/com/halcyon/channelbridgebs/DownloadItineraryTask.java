package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadItineraryTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;

    public DownloadItineraryTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        Log.w("Log", "in DownloadItineraryTask ****");

    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Integer returnCode) {
        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Itineraries synchronised with the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Download Itinerary data from the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context,
                    "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }


    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub

        int returnValue = 1;

        if (isNetworkAvailable() == true) {
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
            autoSyncOnOffFlag.openReadableDatabase();
            String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
            autoSyncOnOffFlag.closeDatabase();
            int id = Integer.parseInt(dbStatus);
            if (id == 0) {
                try {
                    Log.w("Log", "param result : " + params[0]);

                    Log.w("Log", "DownloadItineraryTask result : starting ");

                    publishProgress(1);

                    String maxRowID = "0";

                    Itinerary itineraryObj = new Itinerary(
                            context);
                    itineraryObj.openReadableDatabase();

                    String itineraryId = itineraryObj.getMaxItnId();
                    itineraryObj.closeDatabase();
                    Log.w("Log", "lastProductId:  " + itineraryId);

                    if (itineraryId != "") {
                        if (itineraryId != null) {

                            maxRowID = itineraryId;
                        }

                    }

                    ArrayList<String[]> repStoreDataResponse = null;
                        try {
                            WebService webService = new WebService();
                            repStoreDataResponse = webService.getItineraryListForRep(params[1], params[0], maxRowID);
                        } catch (SocketException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    Log.w("Log", "repStoreDataResponse.size() :  "
                            + repStoreDataResponse.size());

                    if (repStoreDataResponse.size() > 0) {

                        Itinerary itinerary = new Itinerary(context);
                        itinerary.openWritableDatabase();

                        String timeStamp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                        for (int i = 0; i < repStoreDataResponse.size(); i++) {

                            String[] itnDetails = repStoreDataResponse.get(i);

                            Long result = itinerary.insertItinerary(itnDetails[8],
                                    itnDetails[0], itnDetails[1], itnDetails[2],
                                    itnDetails[3], itnDetails[4], itnDetails[5],
                                    itnDetails[6], itnDetails[7], timeStamp, "false",
                                    "false", "false");

                            if (result == -1) {
                                returnValue = 1;
                                break;
                            }

                            returnValue = 2;
                        }

                        itinerary.closeDatabase();

                    } else {

                        returnValue = 4;

                    }

                } catch (Exception e) {
                    Log.w("Log", "Download Itinerary error: "
                            + e.toString());
                }
            } else
                returnValue = 3;
        }
        return returnValue;

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
