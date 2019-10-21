package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadCustomersTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;

    public DownloadCustomersTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {


    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Customers synchronised with the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Download Customer data from server.", Toast.LENGTH_SHORT);
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

                    Customers customerObject = new Customers(
                            context);
                    customerObject.openReadableDatabase();

                    String lastProductId = customerObject
                            .getMaxCustomerId();
                    customerObject.closeDatabase();
                    Log.w("Log", "lastCustId:  " + lastProductId);

                    if (lastProductId != "") {

                        maxRowID = lastProductId;
                    }


                    ArrayList<String[]> repStoreDataResponse = null;
                    try {
                            WebService webService = new WebService();
                            repStoreDataResponse = webService.getCustomerList(params[0], params[1], maxRowID);

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

                        Customers customers = new Customers(context);

                        String timeStamp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                        for (int i = 0; i < repStoreDataResponse.size(); i++) {

                            String[] custDetails = repStoreDataResponse.get(i);

                            customers.openReadableDatabase();
                            boolean isAvailable = customers
                                    .isCustomerDownloaded(custDetails[0]);
                            customers.closeDatabase();

                            if (isAvailable) {
                                Log.w("AVAILABLE", "Customer AVAILABLE");
                                customers.openWritableDatabase();
                                Long result = customers.updateCustomerDetails(
                                        custDetails[0], // pharmacyId
                                        custDetails[1], // pharmacyCode,
                                        custDetails[2], // dealerId,
                                        custDetails[3], // companyCode,
                                        custDetails[4], // customerName,
                                        custDetails[5], // address,
                                        custDetails[7], // area,
                                        custDetails[8], // town,
                                        custDetails[6], // district,
                                        custDetails[9], // telephone,
                                        custDetails[10], // fax,
                                        custDetails[11], // email,
                                        custDetails[12], // customerStatus,
                                        custDetails[13], // creditLimit,
                                        custDetails[33], // currentCredit,
                                        custDetails[14], // creditExpiryDate,
                                        custDetails[15], // creditDuration,
                                        custDetails[16], // vatNo,
                                        custDetails[17], // status,
                                        timeStamp, // timeStamp,
                                        custDetails[28], // latitude,
                                        custDetails[29], // longitude,
                                        custDetails[20], // web,
                                        custDetails[21], // brNo,
                                        custDetails[22], // ownerContact,
                                        custDetails[24], // ownerWifeBday,
                                        custDetails[23], // pharmacyRegNo,
                                        custDetails[25], // pharmacistName,
                                        custDetails[26], // purchasingOfficer,
                                        custDetails[27], // noStaff,
                                        custDetails[19], // customerCode
                                        custDetails[30],
                                        custDetails[31],
                                        android.util.Base64.decode(custDetails[32], Base64.DEFAULT),
                                        custDetails[34],
                                        custDetails[35],
                                        custDetails[36]
                                        // Byte.parseByte(custDetails[32])//image skk
                                );
                                customers.closeDatabase();

                                if (result == -1) {
                                    returnValue = 7;
                                    break;
                                }

                                returnValue = 5;

                            } else {
                                Log.w("UNAVAILABLE", "Customer UNAVAILABLE");
                                customers.openWritableDatabase();
                                Long result = customers.insertCustomer(custDetails[0], // pharmacyId
                                        custDetails[1], // pharmacyCode,
                                        custDetails[2], // dealerId,
                                        custDetails[3], // companyCode,
                                        custDetails[4], // customerName,
                                        custDetails[5], // address,
                                        custDetails[7], // area,
                                        custDetails[8], // town,
                                        custDetails[6], // district,
                                        custDetails[9], // telephone,
                                        custDetails[10], // fax,
                                        custDetails[11], // email,
                                        custDetails[12], // customerStatus,
                                        custDetails[13], // creditLimit,
                                        "0", // currentCredit,
                                        custDetails[14], // creditExpiryDate,
                                        custDetails[15], // creditDuration,
                                        custDetails[16], // vatNo,
                                        custDetails[17], // status,
                                        timeStamp, // timeStamp,
                                        custDetails[28], // latitude,
                                        custDetails[29], // longitude,
                                        custDetails[20], // web,
                                        custDetails[21], // brNo,
                                        custDetails[22], // ownerContact,
                                        custDetails[24], // ownerWifeBday,
                                        custDetails[23], // pharmacyRegNo,
                                        custDetails[25], // pharmacistName,
                                        custDetails[26], // purchasingOfficer,
                                        custDetails[27], // noStaff,
                                        custDetails[19], // customerCode
                                        custDetails[30], custDetails[31],
                                        android.util.Base64.decode(custDetails[32], Base64.DEFAULT),
                                        custDetails[33],
                                        custDetails[34],
                                        custDetails[35]

                                );
                                customers.closeDatabase();

                                if (result == -1) {
                                    returnValue = 1;
                                    break;
                                }

                                returnValue = 2;
                            }
                        }
                    } else {

                        returnValue = 3;

                    }

                } catch (Exception e) {
                    Log.w("Log", "Download customers error : "
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
