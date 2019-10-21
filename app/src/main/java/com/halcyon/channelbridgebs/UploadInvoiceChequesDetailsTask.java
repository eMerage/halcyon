package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.InvoicedCheque;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class UploadInvoiceChequesDetailsTask extends AsyncTask<String, Integer, Integer> {

    private Context context;

    public UploadInvoiceChequesDetailsTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        Log.w("Log", "in UploadInvoiceTask ****");
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Invoice Cheque data uploaded to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Upload invoice Cheque data to the server.", Toast.LENGTH_SHORT);
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

                    InvoicedCheque invoiceChequeObject = new InvoicedCheque(
                            context);
                    invoiceChequeObject.openReadableDatabase();

                    List<String[]> invoiceCheque = invoiceChequeObject
                            .getInvoicedChequesByStatus("false");
                    invoiceChequeObject.closeDatabase();

                    Log.w("Log", "invoice size :  " + invoiceCheque.size());

                    for (String[] invoiceChequeData : invoiceCheque) {

                        Log.w("Log", "invoice id :  " + invoiceChequeData[0]);

                        String[] invoiceChequeDetails = new String[5];

                        invoiceChequeDetails[0] = invoiceChequeData[3]; // Cheque No
                        invoiceChequeDetails[1] = invoiceChequeData[2]; // cust No
                        invoiceChequeDetails[2] = invoiceChequeData[5]; // collected date
                        invoiceChequeDetails[3] = invoiceChequeData[6]; //release date
                        invoiceChequeDetails[4] = invoiceChequeData[4]; //Cheque Amount

                        publishProgress(2);
                        String responseArr = null;
                            try {

                                WebService webService = new WebService();
                                responseArr = webService.uploadInvoiceChequeDetails(
                                        params[0], params[1],
                                        invoiceChequeDetails);
                            } catch (SocketException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }



                        Log.w("Log",
                                "update data result : "
                                        + responseArr.contains("No Error")
                        );
                        if (responseArr.contains("No Error")) {

                            Log.w("Log", "Update the cheque status");


                            InvoicedCheque invoiceChequeObj = new InvoicedCheque(
                                    context);
                            invoiceChequeObj.openReadableDatabase();
                            invoiceChequeObj.setInvoicedChequesUploadedStatus(
                                    invoiceChequeData[0], "true");
                            invoiceChequeObj.closeDatabase();

                            returnValue = 2;

                        }

                        Log.w("Log", "loadProductRepStoreData result : "
                                + responseArr);

                    }

                    if (invoiceCheque.size() < 1) {

                        returnValue = 4;
                    }

                } catch (Exception e) {
                    Log.w("Log", "Upload Invoice error : "
                            + e.toString());
                }
            } else
                returnValue = 3;
        }
        return returnValue;

    }

    public String changeDateFormat(String date) {

        date = date.substring(0, 10);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        String reformattedStr = "";
        try {

            reformattedStr = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}

