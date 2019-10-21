package com.halcyon.channelbridgebs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridge.ImageHandler;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.ImageGallery;
import com.halcyon.channelbridgews.WebService;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UploadNewCustomersTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;
    String deviceId;
    String repId;

    public UploadNewCustomersTask(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        deviceId = sharedPreferences.getString("DeviceId", "-1");
        repId = sharedPreferences.getString("RepId", "-1");
    }

    @Override
    protected void onPreExecute() {

    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast
                    .makeText(context, "New customers uploaded to the server.",
                            Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "Unable to Upload new Customers to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context, "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
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


                Log.w("Log", "param result : " + params[0]);

                Log.w("Log", "loadProductRepStoreData result : starting ");

                publishProgress(1);

                CustomersPendingApproval rtnProdObject = new CustomersPendingApproval(
                        context);
                rtnProdObject.openReadableDatabase();

                List<String[]> rtnProducts = rtnProdObject
                        .getCustomersByUploadStatus("false");
                rtnProdObject.closeDatabase();

                Log.w("Log", "rtnProducts size :  " + rtnProducts.size());

                ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();

//                    SharedPreferences sharedPreferences = PreferenceManager
//                            .getDefaultSharedPreferences(context);
//                    String deviceId = sharedPreferences.getString("DeviceId", "-1");

                for (String[] rtnProdData : rtnProducts) {

                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                    // Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);

                    String[] invoiceDetails = new String[24];

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
                    invoiceDetails[10] = rtnProdData[11];
                    invoiceDetails[11] = rtnProdData[12];
                    invoiceDetails[12] = rtnProdData[13];
                    invoiceDetails[13] = rtnProdData[15];
                    invoiceDetails[14] = rtnProdData[14];
                    invoiceDetails[15] = rtnProdData[16];
                    invoiceDetails[16] = rtnProdData[17];
                    invoiceDetails[17] = rtnProdData[18];
                    invoiceDetails[18] = rtnProdData[20];
                    invoiceDetails[19] = rtnProdData[21];
                    invoiceDetails[20] = rtnProdData[22];
                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);

                    ImageGallery imageGallery = new ImageGallery(
                            context);
                    imageGallery.openReadableDatabase();
                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                    String primaryImage = imageGallery.getPrimaryImageforCustomerId(rtnProdData[0]);
                    imageGallery.closeDatabase();
                    Log.w("Primary Image", primaryImage + "");
                    File customerImageFile = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images" + File.separator + primaryImage);

                    if (customerImageFile.exists()) {
                        try {
                            Bitmap bm = ImageHandler.decodeSampledBitmapFromResource(String.valueOf(customerImageFile), 500, 650);
                            rtnProdData[24] = ImageHandler.encodeTobase64(bm);
                        } catch (IllegalArgumentException e) {
                            Log.w("Illegal argument exception", e.toString());
                        } catch (OutOfMemoryError e) {
                            Log.w("Out of memory error :(", e.toString());
                        }

                    }
                    Log.w("Log", "rtnProducts id :  " + primaryImage);


                    invoiceDetails[20] = primaryImage;
                    invoiceDetails[21] = rtnProdData[10];
                    invoiceDetails[22] = rtnProdData[23];
                    invoiceDetails[23] = rtnProdData[24];
                    Log.w("Log", "rtnProducts id :  " + primaryImage);

                    publishProgress(2);
                    String responseArr = null;
                        try {

                            WebService webService = new WebService();
                            responseArr = webService.uploadNewCustomerDetails(deviceId, repId, invoiceDetails);


                        } catch (SocketException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }



                    Log.w("Log", "update data result : " + responseArr);

                    Log.w("Log",
                            "update data result : "
                                    + responseArr.contains("Successfully")
                    );
                    if (responseArr.contains("Ok")) {

                        Log.w("Log", "Update the iternarary status");

                        CustomersPendingApproval rtnProdObj = new CustomersPendingApproval(context);
                        rtnProdObj.openReadableDatabase();
                        rtnProdObj.setCustomerUploadedStatus(rtnProdData[0], "true");
                        rtnProdObj.closeDatabase();

                        returnValue = 2;

                    }

                    Log.w("Log", "loadProductRepStoreData result : "
                            + responseArr);

                }

                Log.w("Log", "invoicedProductDetailList size :  "
                        + invoicedProductDetailList.size());

                if (rtnProducts.size() < 1) {

                    returnValue = 3;
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
