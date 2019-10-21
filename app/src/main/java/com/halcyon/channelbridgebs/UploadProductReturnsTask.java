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
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

  public class UploadProductReturnsTask extends AsyncTask<String, Integer, Integer> {

    private final Context context;
    String deviceId;
    String repId;

    public UploadProductReturnsTask(Context context) {
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
            Toast notificationToast = Toast.makeText(context,
                    "Product returns uploaded to the server.",
                    Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context,
                    "Unable to Upload Product Returns to the server.", Toast.LENGTH_SHORT);
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

                Log.w("Log", "param result : " + params[0]);

                Log.w("Log", "loadProductRepStoreData result : starting ");

                publishProgress(1);

                String timeStamp = new SimpleDateFormat("yyyy").format(new Date());

                // int year = new Date().getYear();

                ProductReturns rtnProdObject = new ProductReturns(context);
                rtnProdObject.openReadableDatabase();

                List<String[]> rtnProducts = rtnProdObject
                        .getProductReturnsByStatus("false");
                rtnProdObject.closeDatabase();

                Log.w("Log", "rtnProducts size :  " + rtnProducts.size());

                for (String[] rtnProdData : rtnProducts) {

                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                    // Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);

                    Products product = new Products(context);
                    product.openReadableDatabase();
                    String[] productData = product
                            .getProductDetailsByProductCode(rtnProdData[1]);
                    product.closeDatabase();

                    ProductRepStore productRepStore = new ProductRepStore(context);
                    productRepStore.openReadableDatabase();
                    String[] productRepStor = productRepStore
                            .getProductDetailsByProductBatchAndProductCode(rtnProdData[2], rtnProdData[1]);
                    productRepStore.closeDatabase();

                    ArrayList<String[]> returnedProductList = new ArrayList<String[]>();

                    String[] invoiceDetails = new String[15];

                    invoiceDetails[0] = rtnProdData[1]; // Product
                    // code

                    Log.w("Log", "rtnProducts validated :  " + rtnProdData[13]);

                    Log.w("Log123", "rtnProducts Status :  " + rtnProdData[13]
                            + rtnProdData[13].equals("false") + "  " + timeStamp);

                    // if (rtnProdData[13].equals("false")) {
                    // invoiceDetails[1] = timeStamp+rtnProdData[3]; // Invoice
                    // // Id
                    // }else{
                    invoiceDetails[1] = rtnProdData[3]; // Invoice
                    // Id
                    // }

//			invoiceDetails[2] = "R"; // Issue mode
                    String issueMode = rtnProdData[4];


//                        if (rtnProdData[4].equalsIgnoreCase("resalable")) {
//                            issueMode = "RS";
//                        } else if (rtnProdData[4].equalsIgnoreCase("company_returns")) {
//                            issueMode = "CR";
//                        }

                    invoiceDetails[2] = issueMode; // Issue mode
                    invoiceDetails[3] = rtnProdData[5]; // Normal
                    // qty
                    invoiceDetails[4] = changeDateFormat(rtnProdData[7]); // Rtn date

                    Log.w("Log", "productRepStor[5] 3@@$@ :  " + productRepStor[5]);


                    if (productRepStor[5] == null || productRepStor[5] == "") {
                        invoiceDetails[5] = changeDateFormat("2030-01-01 10:13:59.790"); // expire
                        // date
                    } else {
                        invoiceDetails[5] = changeDateFormat(productRepStor[5]); // expire
                        // date
                    }

                    invoiceDetails[6] = rtnProdData[2]; // batch no
                    // date
                    invoiceDetails[7] = rtnProdData[8]; // Batch no

                    invoiceDetails[8] = rtnProdData[10]; // Unit price
                    if (invoiceDetails[8] == null || invoiceDetails[8] == "") {
                        invoiceDetails[8] = productData[14]; // Unit price
                    }

                    invoiceDetails[9] = rtnProdData[0]; // Id

                    invoiceDetails[10] = rtnProdData[11]; // Discount
                    invoiceDetails[11] = rtnProdData[14];
                    invoiceDetails[12] = rtnProdData[15];
                    invoiceDetails[13] = rtnProdData[16];
                    returnedProductList.add(invoiceDetails);

                    if (rtnProdData[6] != null && Integer.parseInt(rtnProdData[6]) > 0) {

                        String[] invoiceDetailsFree = new String[15];

                        invoiceDetailsFree[0] = rtnProdData[1]; // Product
                        // code
                        // if
                        // (rtnProdData[13].equals("false"))
                        // {
                        // invoiceDetailsFree[1] = timeStamp+rtnProdData[3]; //
                        // Invoice
                        // // Id
                        // }else{
                        invoiceDetailsFree[1] = rtnProdData[3]; // Invoice
                        // Id
                        // }
                        invoiceDetailsFree[2] =  invoiceDetails[2]; // Issue mode
                        invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
                        invoiceDetailsFree[4] = changeDateFormat(rtnProdData[7]); // Rtn
                        // date

                        Log.w("Log", "productRepStor[5] 3### :  " + productRepStor[5]);


                        if (productRepStor[5] == null
                                || productRepStor[5] == "") {
                            invoiceDetailsFree[5] = changeDateFormat("2030-01-01 10:13:59.790"); // expire
                            // date
                        } else {
                            invoiceDetailsFree[5] = changeDateFormat(productRepStor[5]); // expire
                            // date
                        }

                        invoiceDetailsFree[6] = rtnProdData[2]; // batch no
                        // date
                        invoiceDetailsFree[7] = rtnProdData[8]; // cust no

                        invoiceDetailsFree[8] = "0"; // Unit price

                        invoiceDetailsFree[9] = rtnProdData[0]; // Id
                        invoiceDetailsFree[10] = rtnProdData[11]; // Discount
                        invoiceDetailsFree[11] = rtnProdData[14];
                        invoiceDetailsFree[12] = rtnProdData[15];
                        invoiceDetailsFree[13] = rtnProdData[16];
                        returnedProductList.add(invoiceDetailsFree);
                    }

                    publishProgress(2);
                    String responseArr = null;
                        try {
                            WebService webService = new WebService();
                            responseArr = webService.uploadProductReturnsDetails(deviceId, repId, returnedProductList);
                        } catch (SocketException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    Log.w("Log",
                            "update data result : " + responseArr.contains("No Error"));
                    if (responseArr.contains("No Error")) {

                        Log.w("Log", "Update the iternarary status");

                        ProductReturns rtnProdObj = new ProductReturns(context);
                        rtnProdObj.openReadableDatabase();
                        rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0], "true");

                        rtnProdObj.closeDatabase();

                        returnValue = 2;

                    }

                    Log.w("Log", "loadProductRepStoreData result : " + responseArr);

                }

                if (rtnProducts.size() < 1) {

                    returnValue = 3;
                }

            } else
                returnValue = 3;
        }
        return returnValue;

    }

    public String changeDateFormat(String date) {

        date = date.substring(0, 10);

        SimpleDateFormat fromUser = new SimpleDateFormat(
                "yyyy-MM-dd");
        // SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
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
