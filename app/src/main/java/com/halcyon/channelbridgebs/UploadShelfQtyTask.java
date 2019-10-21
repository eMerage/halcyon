package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.ShelfQuantity;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UploadShelfQtyTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;

    public UploadShelfQtyTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context,
                    "Shelf quantities uploaded to the server.",
                    Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "Unable to Upload Shelf Quantites to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context,
                    "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }

    }

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

                    Log.w("Log", "loadProductRepStoreData result : starting ");

                    publishProgress(1);


                    ShelfQuantity rtnProdObject = new ShelfQuantity(
                            context);
                    rtnProdObject.openReadableDatabase();

                    List<String[]> rtnProducts = rtnProdObject
                            .getShelfQuantitiesByStatus("false");
                    rtnProdObject.closeDatabase();

                    Log.w("Log", "rtnProducts size :  " + rtnProducts.size());

                    ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();

                    for (String[] invoicedProduct : rtnProducts) {
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[0]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[1]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[2]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[3]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[4]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[5]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[6]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[7]);
                        Log.w("Log", "rtnProducts :  " + invoicedProduct[8]);

                    }

                    for (String[] rtnProdData : rtnProducts) {

                        Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);

                        String[] invoiceDetails = new String[13];

                        invoiceDetails[0] = params[1]; // rep id

                        invoiceDetails[1] = rtnProdData[1]; // Invoice no
                        invoiceDetails[2] = rtnProdData[2]; // Invoice date

                        invoiceDetails[3] = rtnProdData[3]; // customer id

                        invoiceDetails[4] = rtnProdData[4]; // item code
                        invoiceDetails[5] = rtnProdData[6]; // item code
                        invoiceDetails[6] = rtnProdData[5]; // item code

                        publishProgress(2);
                        String responseArr = null;
                            try {
                                WebService webService = new WebService();
                                responseArr = webService.uploadShelfQuantityDetails(params[0], params[1], invoiceDetails);
                            } catch (SocketException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }



                        Log.w("Log",
                                "update data result : "
                                        + responseArr
                                        .contains("Record Inserted Successfully")
                        );
                        if (responseArr.contains("Record Inserted Successfully")) {

                            Log.w("Log", "Update the iternarary status");

                            ShelfQuantity rtnProdObj = new ShelfQuantity(
                                    context);
                            rtnProdObj.openReadableDatabase();
                            rtnProdObj.setShelfQtyUploadedStatus(rtnProdData[0],
                                    "true");
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

                } catch (Exception e) {
                    Log.w("Log", "Upload shelf qty error: "
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
