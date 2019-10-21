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

import com.halcyon.channelbridgedb.ExpireWarning;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by susantha on 6/11/2015.
 */
public class UploadExpiredWarning extends
        AsyncTask<String, Integer, Integer> {

    private final Context context;
    ProgressDialog dialog;

    public UploadExpiredWarning(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("upload data to Server...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();

    }

    protected void onProgressUpdate(Integer... progress) {
      //  super.setProgress(progress[0]);
        switch (progress[0]) {
            case 1:
                Log.w("Log", "yyyyyyyyyy: ");
                dialog.setMessage("Loading data from Tab");
                break;
            case 2:
                dialog.setMessage("Uploading data to the server...");
                break;
            default:
                break;
        }
    }

    protected void onPostExecute(Integer returnCode) {
        dialog.dismiss();
        super.onPostExecute(returnCode);
        dialog.dismiss();

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(this.context, "Upload Success.", Toast.LENGTH_SHORT);
            notificationToast.show();


        }
        if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(this.context, "Unable to upload to server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
        if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(this.context,
                    "Unable to upload to server", Toast.LENGTH_SHORT);
            notificationToast.show();
        }

    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");
        int returnValue = 1;
            publishProgress(1);
        if (isNetworkAvailable() == true) {
            ExpireWarning expireWarning = new ExpireWarning(context);
            expireWarning.openReadableDatabase();
            List<String[]> warnings = expireWarning.getSendWarningByUploadStatus("false");
            expireWarning.closeDatabase();

            ArrayList<String[]> expireWarningDetailList = new ArrayList<String[]>();
            for (String[] rtnProdData : warnings) {

                Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);


                String[] invoiceDetails = new String[10];

                invoiceDetails[0] = rtnProdData[0];
                invoiceDetails[1] = rtnProdData[1];
                invoiceDetails[2] = rtnProdData[2];
                invoiceDetails[3] = rtnProdData[3];
                invoiceDetails[4] = rtnProdData[4];
                invoiceDetails[5] = rtnProdData[5];
                invoiceDetails[6] = rtnProdData[6];
                invoiceDetails[7] = rtnProdData[7];
                invoiceDetails[8] = rtnProdData[8];
                invoiceDetails[9] = rtnProdData[9];

                //  expireWarningDetailList.add(invoiceDetails);
                Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                publishProgress(2);
                String responseArr = null;
                //   while (responseArr == null) {
                try {

                    WebService webService = new WebService();
                    responseArr = webService.uploadExpirewarning(
                            deviceId,
                            repId, invoiceDetails);

                    //  Thread.sleep(100);

                }catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }/*catch (InterruptedException e) {
                        e.printStackTrace();

                        return 0;
                    }*/

                //   }

                Log.w("Log", "update data result : " + responseArr);

                Log.w("Log",
                        "update data result : "
                                + responseArr.contains("Successfully"));
                if (responseArr.contains("OK")) {

                    Log.w("Log", "Update the iternarary status");

                    expireWarning.openReadableDatabase();
                    expireWarning.setrUploadedStatus(rtnProdData[0], "true");
                    expireWarning.closeDatabase();


                    returnValue = 2;

                }

                Log.w("Log", "loadProductRepStoreData result : "
                        + responseArr);

            }

           /* Log.w("Log", "invoicedProductDetailList size :  "
                    + invoicedProductDetailList.size());*/

            if (warnings.size() < 1) {

                returnValue = 3;
            }
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
