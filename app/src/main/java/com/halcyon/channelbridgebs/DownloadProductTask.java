package com.halcyon.channelbridgebs;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadProductTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;

    public DownloadProductTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Products synchronised with the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Download Product data from the server.", Toast.LENGTH_SHORT);
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

                    Log.w("Log", "loadProductRepStoreData result : starting ");

                    publishProgress(1);

                    String maxRowID = "0";

                    Products prodObject = new Products(
                            context);
                    prodObject.openReadableDatabase();

                    String lastProductId = prodObject
                            .getMaxProductId();
                    prodObject.closeDatabase();
                    Log.w("Log", "lastProductId:  " + lastProductId);

                    if (lastProductId != "") {
                        if (lastProductId != null) {

                            maxRowID = lastProductId;
                        }

                    }


                    ArrayList<String[]> repStoreDataResponse = null;
                        try {

                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(context);
                            String deviceId = sharedPreferences.getString("DeviceId", "-1");
                            String repId = sharedPreferences.getString("RepId", "-1");

                            WebService webService = new WebService();
                            repStoreDataResponse = webService.getProductList(
                                    deviceId, repId,
                                    maxRowID);


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

                        Products products = new Products(context);


                        String timeStamp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                        for (int i = 0; i < repStoreDataResponse.size(); i++) {

                            String[] custDetails = repStoreDataResponse.get(i);

                            Log.w("Log", "prod id  " + custDetails[0].trim());

                            products.openWritableDatabase();
                            boolean flag = products.isProductAvailable(custDetails[0].trim());
                            products.closeDatabase();

                            if (flag) {

                                Log.w("Log", " inside flag true  ");

                                products.openWritableDatabase();
                                Long result = products.updateProduct(custDetails[0],
                                        custDetails[1], custDetails[2], custDetails[3],
                                        custDetails[4], custDetails[5], custDetails[6],
                                        custDetails[7], custDetails[8], "",
                                        custDetails[9], custDetails[10],
                                        custDetails[11], custDetails[12],
                                        custDetails[13], custDetails[14],
                                        custDetails[15], custDetails[16], timeStamp,
                                        custDetails[17].trim());

                                Log.w("Log", " inside flag true  " + custDetails[17] + " result :" + result);

                                if (result == -1) {
                                    returnValue = 7;
                                    products.closeDatabase();
                                    break;
                                }
                                Log.w("Log", " inside flag true  " + result);
                                products.closeDatabase();
                            } else {

                                Log.w("Log", " inside flag false ");

                                products.openWritableDatabase();
                                Long result = products.insertProduct(custDetails[0].trim(),
                                        custDetails[1], custDetails[2], custDetails[3],
                                        custDetails[4], custDetails[5], custDetails[6],
                                        custDetails[7], custDetails[8], "",
                                        custDetails[9], custDetails[10],
                                        custDetails[11], custDetails[12],
                                        custDetails[13], custDetails[14],
                                        custDetails[15], custDetails[16], timeStamp,
                                        custDetails[17].trim());

                                if (result == -1) {
                                    returnValue = 1;
                                    products.closeDatabase();
                                    break;
                                }
                                products.closeDatabase();
                            }


                            returnValue = 2;
                        }

                        products.closeDatabase();

                    } else {

                        returnValue = 3;

                    }


                } catch (Exception e) {
                    Log.w("Log", "Download Products error: "
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
