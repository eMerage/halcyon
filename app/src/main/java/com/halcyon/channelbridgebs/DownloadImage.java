package com.halcyon.channelbridgebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by susantha on 6/4/2015.
 */
public class DownloadImage extends AsyncTask<String, Integer, String[]> {
    private final Context context;
    ProgressDialog dialog;

    public DownloadImage(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Download customer Image from Server...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();

    }

    protected void onProgressUpdate(Integer... progress) {
        switch (progress[0]) {
            case 1:
                Log.w("Log", "Download customer Image  ");
                dialog.setMessage("Download customer Image from Server...");
                break;
            default:
                break;
        }
    }

    @Override
    protected String[] doInBackground(String... params) {
        String repId = params[0];
        String pharmacyId = params[1];
        String imageWithImageId[] = new String[2];


        if (isNetworkAvailable() == true) {

            publishProgress(1);
            ArrayList<String[]> repStoreDataResponse = null;

            WebService webService = new WebService();

            try {
                repStoreDataResponse = webService.GetCustomerImage(repId, pharmacyId);
                if (repStoreDataResponse.size() > 0) {

                    for (int i = 0; i < repStoreDataResponse.size(); i++) {

                        imageWithImageId = repStoreDataResponse.get(i);

                    }

                } else {
                    Log.d("no response data", "" + repStoreDataResponse.size());
                }
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }


        return imageWithImageId;


    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        dialog.dismiss();
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
