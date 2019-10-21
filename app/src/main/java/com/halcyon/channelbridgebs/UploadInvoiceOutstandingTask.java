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
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class UploadInvoiceOutstandingTask extends AsyncTask<String, Integer, Integer> {

    private Context context;

    public UploadInvoiceOutstandingTask(Context context) {
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
            Toast notificationToast = Toast.makeText(context, "Invoice Outstanding data uploaded to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Upload invoice Outstanding data to the server.", Toast.LENGTH_SHORT);
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


                Log.w("Log", "param result : " + params[0]);

                Log.w("Log", "loadProductRepStoreData result : starting ");

                publishProgress(1);

                Invoice invoiceObject = new Invoice(context);
                invoiceObject.openReadableDatabase();

                List<String[]> invoice = invoiceObject
                        .getInvoicesByOutstandingUploadStatus("false");
                invoiceObject.closeDatabase();

                Log.w("Log", "invoice size :  " + invoice.size());

                for (String[] invoiceData : invoice) {

                    Log.w("Log", "invoice id :  " + invoiceData[0]);
                    Log.w("Log", "invoice date :  " + invoiceData[10]);

                    Itinerary itinerary = new Itinerary(
                            context);
                    itinerary.openReadableDatabase();

                    String tempCust = itinerary
                            .getItineraryStatus(invoiceData[1]);
                    itinerary.closeDatabase();

                    String custNo = "";

                    Itinerary itineraryTwo = new Itinerary(
                            context);
                    itineraryTwo.openReadableDatabase();

                    if (tempCust.equals("true")) {

                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(context);
                        String deviceId = sharedPreferences.getString("DeviceId", "-1");

                        String[] itnDetails = itineraryTwo
                                .getItineraryDetailsForTemporaryCustomer(invoiceData[1]);
                        custNo = deviceId + "_" + itnDetails[7];// this is where yu have to change..!!

                    } else {
                        String[] itnDetails = itineraryTwo
                                .getItineraryDetailsById(invoiceData[1]);
                        custNo = itnDetails[4];
                    }

                    itineraryTwo.closeDatabase();

                    String[] invoiceOutstandingDetails = new String[6];

                    invoiceOutstandingDetails[0] = invoiceData[0]; // Invoice Id
                    invoiceOutstandingDetails[1] = custNo; // cust No
                    invoiceOutstandingDetails[2] = invoiceData[11].substring(0, 10); // invoice
                    // date
                    invoiceOutstandingDetails[3] = invoiceData[3]; // total
                    // amount
                    invoiceOutstandingDetails[4] = invoiceData[5]; // credit
                    // amount
                    invoiceOutstandingDetails[5] = invoiceData[13]; // credit
                    // duration

                    publishProgress(2);
                    String responseArr = null;
                    try {

                        WebService webService = new WebService();
                        responseArr = webService.uploadInvoiceOutstandingDetails(
                                params[0], params[1],
                                invoiceOutstandingDetails);


                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {
                        if (responseArr == null) {

                        } else {
                            if (responseArr.contains("No Error")) {

                                Log.w("Log", "Update the iternarary status");

                                Invoice invoiceObj = new Invoice(context);
                                invoiceObj.openReadableDatabase();
                                invoiceObj.setInvoiceOutstandingUpdatedStatus(
                                        invoiceData[0], "true");
                                invoiceObj.closeDatabase();

                                returnValue = 2;

                            } else {

                            }
                        }
                    } catch (Exception ex) {

                    }




                }

                if (invoice.size() < 1) {

                    returnValue = 4;
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

