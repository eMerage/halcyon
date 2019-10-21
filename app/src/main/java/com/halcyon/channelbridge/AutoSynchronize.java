package com.halcyon.channelbridge;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgebs.AutoBackupService;
import com.halcyon.channelbridgebs.DownloadCustomerImagesTask;
import com.halcyon.channelbridgebs.DownloadCustomersTask;
import com.halcyon.channelbridgebs.DownloadDealerSalesTask;
import com.halcyon.channelbridgebs.DownloadItineraryTask;
import com.halcyon.channelbridgebs.DownloadProductTask;
import com.halcyon.channelbridgebs.UploadBackupDatabase;
import com.halcyon.channelbridgebs.UploadCustomersImagesTask;
import com.halcyon.channelbridgebs.UploadInvoiceChequesDetailsTask;
import com.halcyon.channelbridgebs.UploadInvoiceHeaderTask;
import com.halcyon.channelbridgebs.UploadInvoiceOutstandingTask;
import com.halcyon.channelbridgebs.UploadInvoiceTask;
import com.halcyon.channelbridgebs.UploadNewCustomersTask;
import com.halcyon.channelbridgebs.UploadProductReturnsTask;
import com.halcyon.channelbridgebs.UploadRetunHeaderTask;
import com.halcyon.channelbridgebs.UploadShelfQtyTask;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.Target;
import com.halcyon.channelbridgedb.UserLogin;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgews.WebService;

public class AutoSynchronize extends Service {

    private static final String TAG = "AUTOSYNCHRONIZE";
    private final IBinder mBinder = new MyBinder();
    Context context;

    String deviceId, repId;
    ArrayList<String> invoicedIds;

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        context = getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        deviceId = sharedPreferences.getString("DeviceId", "-1");
        repId = sharedPreferences.getString("RepId", "-1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // use this method to call upload web service

        super.onStartCommand(intent, flags, startId);

        System.out.println("onStartCommand ");

        Log.e(TAG, "onStartCommand");
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean autoSyncActivate = preferences.getBoolean("AutoSyncRun", true);
        AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(AutoSynchronize.this);
        autoSyncOnOffFlag.openReadableDatabase();
        String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
        autoSyncOnOffFlag.closeDatabase();
        if (dbStatus.isEmpty())
            dbStatus = "0";
        Log.w("AUTOSYNC ACTIVATE?  ####### ", String.valueOf(autoSyncActivate) + "");

        if (isOnline() && autoSyncActivate) {

            String deviceId = "", repId = "";

            UserLogin login = new UserLogin(AutoSynchronize.this);
            login.openReadableDatabase();
            ArrayList<String[]> users = login.getAllUsersDetails();
            login.closeDatabase();

            if (users.size() > 0) {
                deviceId = users.get(0)[6];
            }

            Reps reps = new Reps(AutoSynchronize.this);
            reps.openReadableDatabase();
            ArrayList<String[]> repsDetails = reps.getAllRepsDetails();
            reps.closeDatabase();

            if (repsDetails.size() > 0) {
                repId = repsDetails.get(0)[1];
            }
            int flag = Integer.parseInt(dbStatus);
            if (deviceId != "" && repId != "" && flag == 0) {

                try {

                    Toast.makeText(context, "Autooooooo suncccc", Toast.LENGTH_LONG).show();


                    System.out.println("inside DownloadCustomerImagesTask task ");
                    new DownloadCustomerImagesTask(AutoSynchronize.this).execute("1");
                    Reps repController = new Reps(AutoSynchronize.this);
                    repController.openReadableDatabase();
                    String[] repks = repController.getRepDetails();
                    repController.closeDatabase();
                    System.out.println("inside UploadInvoiceTask Task ");

                    new TargetSync(AutoSynchronize.this).execute();

                    new UploadInvoiceHeaderTask(AutoSynchronize.this,repId,deviceId,repks[8]).execute();
                    new UploadInvoiceTaskAudit(AutoSynchronize.this).execute(deviceId, repId);

                    System.out.println("inside UploadNewCustomersTask Task ");
                    new UploadNewCustomersTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    System.out.println("inside UploadProductReturnsTask Task ");
                    new UploadRetunHeaderTask(AutoSynchronize.this,repId,deviceId).execute();

                    new UploadProductReturnsTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    System.out.println("inside UploadShelfQtyTask Task ");
                    new UploadShelfQtyTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    System.out.println("inside DownloadCustomersTask Task ");
                    new DownloadCustomersTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    System.out.println("inside DownloadItineraryTask Task ");
                    new DownloadItineraryTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    System.out
                            .println("inside DownloadProductRepStoreTask Task ");
                 /*   new DownloadProductRepStoreTask(AutoSynchronize.this)
                            .execute(deviceId, repId);*/

                    System.out.println("inside DownloadProductTask Task ");
                    new DownloadProductTask(AutoSynchronize.this).execute(
                            deviceId, repId);

                    new DownloadDealerSalesTask(AutoSynchronize.this).execute();

                    System.out
                            .println("inside UploadCustomersImagesTask Task ");
                    new UploadCustomersImagesTask(AutoSynchronize.this).execute("1");



                    if (preferences.getBoolean(AutoBackupService.BACKUP_REQUIRED, false)) {
                       // new UploadBackupDatabase(preferences.getString(AutoBackupService.BACKUP_NAME, ""), getApplicationContext()).execute();
                    }

                    SharedPreferences preferencesTwo = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    boolean chequeEnabled = preferencesTwo.getBoolean("cbPrefEnableCheckDetails", false);

                    if (chequeEnabled) {

                        System.out.println("inside UploadInvoiceOutstandingTask Task ");
                        new UploadInvoiceOutstandingTask(AutoSynchronize.this).execute(
                                deviceId, repId);

                        System.out.println("inside UploadInvoiceChequesDetailsTask Task ");
                        new UploadInvoiceChequesDetailsTask(AutoSynchronize.this).execute(
                                deviceId, repId);
                    }


                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e(TAG, "Background service exception : " + e.toString());

                }
            } else
                System.out.println("Auto Synchronized disable when using manual Upload");

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println("onBind ");
        return mBinder;
    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }

    public class MyBinder extends Binder {
        AutoSynchronize getService() {
            System.out.println("getService ");
            return AutoSynchronize.this;
        }
    }

    private class TransferAudit extends AsyncTask<Void, Void, Void> {
        ArrayList<String> responseArr;
        private final Context context;

        private TransferAudit(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseArr = null;
            WebService webService = new WebService();
            try {
                responseArr = webService.checkTransferAudit(repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (responseArr == null) {
                new CheckDayTasks(context).execute();
            } else {
                for (int i = 0; i < responseArr.size(); i++) {
                    Invoice invoiceObj = new Invoice(context);
                    invoiceObj.openReadableDatabase();
                    invoiceObj.setInvoiceUpdatedStatus(responseArr.get(i), "false");
                    invoiceObj.closeDatabase();
                }
                Reps repController = new Reps(context);
                repController.openReadableDatabase();
                String[] reps = repController.getRepDetails();
                repController.closeDatabase();

                new UploadInvoiceHeaderTask(context, repId, deviceId, reps[8]).execute();
                new UploadInvoiceTask(context).execute("1");
            }


        }

    }



    private class TargetSync extends AsyncTask<Void, Void, Void> {
        ArrayList<String[]> responseArr ;
        private final Context context;

        private TargetSync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseArr = null;
            WebService webService = new WebService();
            try {
                responseArr = webService.getProductTarget(deviceId, repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Target targetDB = new Target(context);
            for (int i = 0; i < responseArr.size(); i++) {
                String[] custDetails = responseArr.get(i);

                int target ;
                int achievement;

                try {
                    target =Integer.parseInt(custDetails[2]);
                }catch (NumberFormatException numEx){
                    target=0;
                }

                try {
                    achievement =Integer.parseInt(custDetails[3]);
                }catch (NumberFormatException numEx){
                    achievement=0;
                }
                targetDB.insertTarget(custDetails[0],custDetails[1],target,achievement,custDetails[4]);

            }


        }

    }





    private class CheckDayTasks extends AsyncTask<Void, Void, Void> {
        String responseInvoiceCount;
        private final Context context;

        private CheckDayTasks(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseInvoiceCount = null;
            WebService webService = new WebService();
            try {
                responseInvoiceCount = webService.checkInvoiceCount(repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Invoice invoiceObject = new Invoice(context);
            invoiceObject.openReadableDatabase();
            invoicedIds = invoiceObject.getInvoiceCountByDate();
            invoiceObject.closeDatabase();
            try {
                if (Integer.parseInt(responseInvoiceCount) == invoicedIds.size()) {

                } else {
                    new getCheckDayTasksInvoiceID(context).execute();
                }
            } catch (NumberFormatException e) {
                new getCheckDayTasksInvoiceID(context).execute();
            } catch (Exception e) {
                new getCheckDayTasksInvoiceID(context).execute();
            }

        }

    }
    private class getCheckDayTasksInvoiceID extends AsyncTask<Void, Void, Void> {
        private final Context context;
        ArrayList<String> checkDayTasksInvoiceID;


        private getCheckDayTasksInvoiceID(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            checkDayTasksInvoiceID = null;
            WebService webService = new WebService();
            try {
                checkDayTasksInvoiceID = webService.getCheckInvoiceId(repId, invoicedIds);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (checkDayTasksInvoiceID == null) {

                }else if(checkDayTasksInvoiceID.isEmpty()){
                } else {
                    for (int i = 0; i < checkDayTasksInvoiceID.size(); i++) {
                        Invoice invoiceObj = new Invoice(context);
                        invoiceObj.openReadableDatabase();
                        invoiceObj.setInvoiceUpdatedStatus(checkDayTasksInvoiceID.get(i), "false");
                        invoiceObj.closeDatabase();


                    }
                    Reps repController = new Reps(context);
                    repController.openReadableDatabase();
                    String[] reps = repController.getRepDetails();
                    repController.closeDatabase();

                    new UploadInvoiceHeaderTask(context, repId, deviceId, reps[8]).execute();
                    new UploadInvoiceTask(context).execute("1");
                }
            } catch (Exception e) {

            }
        }
    }

    private class UploadInvoiceTaskAudit extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadInvoiceTaskAudit(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            new TransferAudit(context).execute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                Invoice invoiceObject = new Invoice(context);
                invoiceObject.openReadableDatabase();
                List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
                invoiceObject.closeDatabase();
                for (String[] invoiceData : invoice) {
                    ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
                    InvoicedProducts invoicedProductsObject = new InvoicedProducts(context);
                    invoicedProductsObject.openReadableDatabase();
                    List<String[]> invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceData[0]);
                    invoicedProductsObject.closeDatabase();
                    for (String[] invoicedProduct : invoicedProducts) {
                        ProductRepStore productRepStore = new ProductRepStore(context);
                        productRepStore.openReadableDatabase();
                        String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(invoicedProduct[3], invoicedProduct[2]);
                        productRepStore.closeDatabase();
                        Products product = new Products(context);
                        product.openReadableDatabase();
                        String[] productData = product.getProductDetailsByProductCode(invoicedProduct[2]);
                        product.closeDatabase();
                        Itinerary itinerary = new Itinerary(context);
                        itinerary.openReadableDatabase();
                        String tempCust = itinerary.getItineraryStatus(invoiceData[1]);
                        itinerary.closeDatabase();
                        String custNo = "";
                        Itinerary itineraryTwo = new Itinerary(context);
                        itineraryTwo.openReadableDatabase();
                        if (tempCust.equals("true")) {
                            String[] itnDetails = itineraryTwo.getItineraryDetailsForTemporaryCustomer(invoiceData[1]);
                            custNo = itnDetails[8];// this is where yu have to
                        } else {
                            String[] itnDetails = itineraryTwo.getItineraryDetailsById(invoiceData[1]);
                            custNo = itnDetails[4];
                        }
                        itineraryTwo.closeDatabase();
                        if (invoicedProduct[7] != "" && Integer.parseInt(invoicedProduct[7]) > 0) {
                            String[] invoiceDetails = new String[18];
                            int qty = Integer.parseInt(invoicedProduct[7]);
                            double purchasePrice = 0;
                            double selleingPrice = 0;
                            if (productData[12] != null && productData[12].length() > 0) {
                                purchasePrice = Double.parseDouble(productData[12]);
                            }
                            if (productData[13] != null && productData[13].length() > 0) {
                                selleingPrice = Double.parseDouble(productData[13]);
                            }
                            double profit = (selleingPrice * qty) - (purchasePrice * qty);
                            invoiceDetails[0] = invoicedProduct[2]; // Product
                            invoiceDetails[1] = invoicedProduct[1]; // Invoice
                            invoiceDetails[2] = "N"; // Issue mode
                            invoiceDetails[3] = invoicedProduct[7]; // Normal
                            invoiceDetails[5] = invoiceData[2]; // Payment type
                            invoiceDetails[6] = changeDateFormat(productRepStor[5]); // Expire
                            invoiceDetails[7] = invoicedProduct[3]; // Batch no
                            invoiceDetails[8] = custNo; // Customer no
                            invoiceDetails[9] = String.valueOf(profit); // Profit
                            invoiceDetails[10] = productData[13]; // Unit price
                            invoiceDetails[11] = invoicedProduct[6]; // Discount
                            invoiceDetails[12] = invoicedProduct[0]; // Id
                            invoiceDetails[13] = invoiceData[11]; // Invoice
                            invoiceDetails[14] = invoiceData[16];
                            invoiceDetails[15] = invoiceData[15];                                    // time
                            invoiceDetails[16] = invoicedProduct[4];
                            invoiceDetails[17] = invoicedProduct[10];
                            invoicedProductDetailList.add(invoiceDetails);
                        }
                        if (invoicedProduct[5] != "" && Integer.parseInt(invoicedProduct[5]) > 0) {
                            String[] invoiceDetails = new String[18];
                            invoiceDetails[0] = invoicedProduct[2]; // Product
                            invoiceDetails[1] = invoicedProduct[1]; // Invoice
                            invoiceDetails[2] = "F"; // Issue mode
                            invoiceDetails[3] = invoicedProduct[5]; // Normal
                            invoiceDetails[5] = invoiceData[2]; // Payment type
                            invoiceDetails[6] = changeDateFormat(productRepStor[5]);
                            invoiceDetails[7] = invoicedProduct[3]; // Batch no
                            invoiceDetails[8] = custNo; // Customer no
                            invoiceDetails[9] = "0.00"; // Profit
                            invoiceDetails[10] = "0"; // Unit price
                            invoiceDetails[11] = invoicedProduct[6]; // Discount
                            invoiceDetails[12] = invoicedProduct[0]; // Id
                            invoiceDetails[13] = invoiceData[11]; // Invoice
                            invoiceDetails[14] = invoiceData[16];
                            invoiceDetails[15] = invoiceData[15];                                    // time
                            invoiceDetails[16] = invoicedProduct[4];
                            invoiceDetails[17] = invoicedProduct[10];
                            invoicedProductDetailList.add(invoiceDetails);
                        }
                    }
                    String responseArr = null;
                    while (responseArr == null) {
                        try {
                            WebService webService = new WebService();
                            responseArr = webService.uploadInvoiceDetails(deviceId, repId, invoicedProductDetailList);
                            Thread.sleep(100);
                        } catch (SocketException e) {
                            e.printStackTrace();
                            return 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return 0;
                        }

                    }
                    if (responseArr.contains("No Error")) {
                        Invoice invoiceObj = new Invoice(context);
                        invoiceObj.openReadableDatabase();
                        invoiceObj.setInvoiceUpdatedStatus(invoiceData[0], "true");
                        invoiceObj.closeDatabase();
                        returnValue = 2;

                    }
                }
                if (invoice.size() < 1) {
                    returnValue = 4;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

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

}
