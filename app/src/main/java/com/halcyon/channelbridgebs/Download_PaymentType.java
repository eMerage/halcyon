package com.halcyon.channelbridgebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.InvoicePaymentType;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Puritha Dev on 12/5/2014.
 */
public class Download_PaymentType extends AsyncTask<String, Integer, Integer> {

    private final Context context;
    ProgressDialog dialog;

    public Download_PaymentType(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Fetching Payment Type Data from Server...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();

    }


    @Override
    protected Integer doInBackground(String... strings) {
        String deviceId = strings[0];
        String repId = strings[1];
        InvoicePaymentType PaymentType = new InvoicePaymentType(context);
        int returnValue = 1;
        if (isNetworkAvailable() == true) {
            String dbStatus = "0";

            if (dbStatus == "0") {
                try {
                    ArrayList<String[]> repStoreDataResponse = null;
                        try {
                            WebService webService = new WebService();
                            repStoreDataResponse = webService
                                    .Download_Payment_Type(
                                            deviceId,
                                            repId);

                        } catch (SocketException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    Log.w("Log", "Customer list.size() :  "
                            + repStoreDataResponse.size());
                    if (repStoreDataResponse.size() > 0) {

                        PaymentType.openWritableDatabase();
                        PaymentType.Deletedata();
                        PaymentType.closeDatabase();

                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] DEL_Sales
                                    = repStoreDataResponse.get(i);
                            PaymentType.openWritableDatabase();
                            Long result = PaymentType.insertInvoicePaymentType(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3]
                            );
                            PaymentType.closeDatabase();

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
        dialog.dismiss();

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(this.context, "Download  payment type from the server.", Toast.LENGTH_SHORT);
            notificationToast.show();


        }
        if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(this.context, "Unable to Download  data from the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }
        if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(this.context,
                    "O size Data Download from the server", Toast.LENGTH_SHORT);
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
