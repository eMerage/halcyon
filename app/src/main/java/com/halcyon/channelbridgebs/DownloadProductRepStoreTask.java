package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadProductRepStoreTask extends
        AsyncTask<String, Integer, Integer> {

    private final Context context;

    public DownloadProductRepStoreTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {


    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {
        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Inventory synchronised with the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Download Inventory data from the server.", Toast.LENGTH_SHORT);
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

                    int maxRowID = 0;


                    ProductRepStore repStoreObject = new ProductRepStore(
                            context);
                    repStoreObject.openReadableDatabase();

                    String lastProductId = repStoreObject.getMaxRepstoreId();
                    repStoreObject.closeDatabase();
                    Log.w("Log", "lastRepstoreId:  " + lastProductId);

                    if (lastProductId != null && (!lastProductId.equals("null"))) {
                        maxRowID = Integer.parseInt(lastProductId);
                    }

                    ArrayList<String[]> repStoreDataResponse = null;
                        try {

                            WebService webService = new WebService();
                            repStoreDataResponse = webService.getProductRepStoreList(
                                    params[0], params[1],
                                    maxRowID);

                        } catch (SocketException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    if (repStoreDataResponse.size() > 0) {

                        ProductRepStore productRepStore = new ProductRepStore(
                                context);

                        String timeStamp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] custDetails = repStoreDataResponse.get(i);
                            productRepStore.openReadableDatabase();
                            Long result = productRepStore.insertProductRepStore(
                                        custDetails[0], custDetails[2], custDetails[5],
                                        custDetails[3], custDetails[4],custDetails[6],custDetails[7],custDetails[8], timeStamp);

                                if (result == -1) {
                                    returnValue = 7;
                                    productRepStore.closeDatabase();
                                    break;
                                }
                                productRepStore.closeDatabase();

                            returnValue = 2;


                        }

                    } else {

                        returnValue = 3;

                    }

                } catch (Exception e) {
                    Log.w("Log", "Download Prod repstore error: "
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
