package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

 public class UploadInvoiceTask extends AsyncTask<String, Integer, Integer> {

    private Context context;


    public UploadInvoiceTask(Context context) {
        this.context = context;

    }


    protected void onPreExecute() {

        Log.w("Log", "in UploadInvoiceTask ****");
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {

        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Invoice data uploaded to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();

        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to Upload invoice data to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context, "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }


    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub
        WebService webService = new WebService();
        int returnValue = 1;

        if (isNetworkAvailable() == true) {
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
            autoSyncOnOffFlag.openReadableDatabase();
            String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
            autoSyncOnOffFlag.closeDatabase();

            int id = Integer.parseInt(dbStatus);
            if (id == 0) {
            //    try {

                    Log.w("Log", "param result : " + params[0]);

                    Log.w("Log", "loadProductRepStoreData result : starting ");

                    publishProgress(1);

                    Invoice invoiceObject = new Invoice(context);
                    invoiceObject.openReadableDatabase();

                    List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
                    invoiceObject.closeDatabase();

                            Log.w("Log", "invoice size :  " + invoice.size());

                    for (String[] invoiceData : invoice) {

                        Log.w("Log", "invoice id :  " + invoiceData[0]);
                        Log.w("Log", "invoice date :  " + invoiceData[10]);
                        Log.w("Log", "Lat :  " + invoiceData[13]);
                        Log.w("Log", "Lon:  " + invoiceData[14]);
                        ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();

                        InvoicedProducts invoicedProductsObject = new InvoicedProducts(context);
                        invoicedProductsObject.openReadableDatabase();
                        List<String[]> invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceData[0]);

                        invoicedProductsObject.closeDatabase();




                        for (String[] invoicedProduct : invoicedProducts) {

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
                                custNo =itnDetails[8];// this is where yu have to
                                // change..!!
                            } else {
                                String[] itnDetails = itineraryTwo.getItineraryDetailsById(invoiceData[1]);
                                custNo = itnDetails[4];
                            }

                            itineraryTwo.closeDatabase();

                            System.out.println("invoicedProduct[7] :" + invoicedProduct[7]);

                            if (invoicedProduct[7] != "" && Integer.parseInt(invoicedProduct[7]) > 0) {

                                String[] invoiceDetails = new String[18];

                                int qty = Integer.parseInt(invoicedProduct[7]);
                                double purchasePrice = 0;
                                double selleingPrice = 0;
                                if (productData[12] != null && productData[12].length() > 0) {
                                    purchasePrice = Double.parseDouble(productData[12]);
                                }
                                if (productData[13] != null && productData[13].length() > 0) {
                                    selleingPrice = Double.parseDouble(productData[14]);
                                }

                                double profit = (selleingPrice * qty) - (purchasePrice * qty);

                                Log.w("Log", "profit :  " + profit);

                                invoiceDetails[0] = invoicedProduct[2]; // Product
                                // code
                                invoiceDetails[1] = invoicedProduct[1]; // Invoice
                                // Id
                                invoiceDetails[2] = "N"; // Issue mode
                                invoiceDetails[3] = invoicedProduct[7]; // Normal
                                // qty
         				//		invoiceDetails[4] = changeDateFormat(invoiceData[10]); // Invoice date
                                invoiceDetails[5] = invoiceData[2]; // Payment type

                                invoiceDetails[6] = changeDateFormat(invoicedProduct[11]); // Expire
                                // date
                                invoiceDetails[7] = invoicedProduct[3]; // Batch no
                                invoiceDetails[8] = custNo; // Customer no
                                invoiceDetails[9] = String.valueOf(profit); // Profit
                                invoiceDetails[10] = productData[13]; // Unit price
                                invoiceDetails[11] = invoicedProduct[6]; // Discount
                                invoiceDetails[12] = invoicedProduct[0]; // Id
                                invoiceDetails[13] = invoiceData[11]; // Invoice time
                                invoiceDetails[14] = invoiceData[16];
                                invoiceDetails[15] = invoiceData[15];
                                invoiceDetails[16] = invoicedProduct[4];
                                invoiceDetails[17] = invoicedProduct[10];

                                invoicedProductDetailList.add(invoiceDetails);

                            }

                            if (invoicedProduct[5] != "" && Integer.parseInt(invoicedProduct[5]) > 0) {

                                String[] invoiceDetails = new String[18];

                                invoiceDetails[0] = invoicedProduct[2]; // Product
                                // code
                                invoiceDetails[1] = invoicedProduct[1]; // Invoice
                                // Id
                                invoiceDetails[2] = "F"; // Issue mode
                                invoiceDetails[3] = invoicedProduct[5]; // Normal
                                // qty
//						invoiceDetails[4] = changeDateFormat(invoiceData[10]); // Invoice date
                                invoiceDetails[5] = invoiceData[2]; // Payment type

                                invoiceDetails[6] = changeDateFormat(invoicedProduct[11]);
                                ; // Expire
                                // date
                                invoiceDetails[7] = invoicedProduct[3]; // Batch no
                                invoiceDetails[8] = custNo; // Customer no
                                invoiceDetails[9] = "0.00"; // Profit
                                invoiceDetails[10] = "0"; // Unit price
                                invoiceDetails[11] = invoicedProduct[6]; // Discount
                                invoiceDetails[12] = invoicedProduct[0]; // Id
                                invoiceDetails[13] = invoiceData[11]; // Invoice time
                                invoiceDetails[14] = invoiceData[16];
                                invoiceDetails[15] = invoiceData[15];
                                invoiceDetails[16] = invoicedProduct[4];
                                invoiceDetails[17] = invoicedProduct[10];


                                invoicedProductDetailList.add(invoiceDetails);

                            }

                        }

                        Log.w("Log", "invoicedProductDetailList size :  "
                                + invoicedProductDetailList.size());

                        publishProgress(2);
                        String responseArr = null;
                            try {
                                responseArr = webService.uploadInvoiceDetails(params[0], params[1], invoicedProductDetailList);
                                if (responseArr.contains("No Error")) {

                                    Log.w("Log", "Update the iternarary status");

                                    Invoice invoiceObj = new Invoice(context);
                                    invoiceObj.openReadableDatabase();
                                    invoiceObj.setInvoiceUpdatedStatus(invoiceData[0], "true");
                                    invoiceObj.closeDatabase();

                                    returnValue = 2;

                                }


                            } catch (SocketException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                    }

                    if (invoice.size() < 1) {

                        returnValue = 4;
                    }

//                } catch (Exception e) {
//                    Log.w("Log", "Upload Invoice error : "
//                            + e.toString());
//                }
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

