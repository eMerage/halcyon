package com.halcyon.channelbridgebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;


 public class Download_DEL_Outstanding extends AsyncTask<String, Integer, Integer> {
    private final Context context;
    ProgressDialog dialog;


    public Download_DEL_Outstanding(Context context) {

        this.context = context;

    }


    @Override
    protected void onPreExecute() {
/**
 *   need to find a solution  for this dialog..because this dialog able to crash the app
 */
   /* dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Outstanding  Data from Server...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
*/
    }

    protected Integer doInBackground(String... strings) {
//        Looper.prepare();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");

        DEL_Outstandiing Outstandiing = new DEL_Outstandiing(
                context);

        int returnValue = 1;

        if (isNetworkAvailable() == true) {
            String dbStatus = "0";

            if (dbStatus == "0") {
                try {
                    ArrayList<String[]> repStoreDataResponse = null;

                        WebService webService = new WebService();
                        repStoreDataResponse = webService
                                .Download_DEL_Outstanding(
                                        deviceId,
                                        repId);


                    Log.w("Log", "Customer list.size() :  "
                            + repStoreDataResponse.size());
                    if (repStoreDataResponse.size() > 0) {


                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] DEL_Sales
                                    = repStoreDataResponse.get(i);
                            Outstandiing.openWritableDatabase();
                            boolean exitancy = Outstandiing.isExistOutstandingRow(DEL_Sales[0]);
                            Long result;
                            if (exitancy == false) {
                                result = Outstandiing.insertDEL_Out_Standiing(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3], DEL_Sales[4], DEL_Sales[5],
                                        DEL_Sales[6], DEL_Sales[7], DEL_Sales[8], DEL_Sales[9], DEL_Sales[10], DEL_Sales[11], DEL_Sales[12],
                                        DEL_Sales[13], DEL_Sales[14]
                                );
                            } else {
                                result = Outstandiing.updateCreditAmountByCusNOAndInvoNo(DEL_Sales[10], DEL_Sales[5], DEL_Sales[7]);
                            }

                            // Long result = Outstandiing.updateCreditAmountByCusNOAndInvoNo(DEL_Sales[10],DEL_Sales[5],DEL_Sales[7]);
                            Outstandiing.closeDatabase();
                            returnValue = 2;
                        }

                    } else {

                        returnValue = 3;

                    }

                } catch (Exception e) {
                    Log.w("Log", "Download Products error: "
                            + e.toString());
                }

            }
        }
        return returnValue;
    }

    @Override
    protected void onPostExecute(Integer returnCode) {
        super.onPostExecute(returnCode);
    /*   if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
        */
        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(this.context, "Download  Outstanding from the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
            // StaticValue.responce = 2;
        }
        if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(this.context, "Unable to Download Outstanding from the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
        if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(this.context, "O size Outstanding Download from the server", Toast.LENGTH_SHORT);
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
