package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.Entity.DealerSaleEntity;
import com.halcyon.Entity.ReturnHeaderEntity;
import com.halcyon.channelbridgeaddapters.ManualSyncList;
import com.halcyon.channelbridgeaddapters.ManualSyncRecyAdapter;
import com.halcyon.channelbridgebs.DownloadCustomerImagesTask;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.CreditPeriod;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.DealerSales;
import com.halcyon.channelbridgedb.DiscountStructures;
import com.halcyon.channelbridgedb.ImageGallery;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.ProductUnload;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.ReturnHeader;
import com.halcyon.channelbridgedb.ShelfQuantity;
import com.halcyon.channelbridgedb.Target;
import com.halcyon.channelbridgews.WebService;


import java.io.File;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 3/6/2017.
 */

public class ManualSync extends Activity {

    ArrayList<String> invoicedIds;
    ArrayList<String> ReturninvoicedIds;
    RecyclerView recyclerViewManualsync;
    ManualSyncRecyAdapter manualSyncAdapter;
    ArrayList<String> customrIDs;
    ArrayList<ManualSyncList> manualsynclist = new ArrayList<ManualSyncList>();

    RecyclerView.LayoutManager mLayoutManager;
    int recyclerPosition;

    String deviceId, repId;
    String globalResultProducts, globalResultCustomers, globalResultInventory, globalResultItinerary, globalResultInvoices,
            globalResultUpCustomers, globalResultReturns, globalResultShelf, globalResultupOutstandings, globalResultProductUnload, globalResultDwnOutstanding, globalResultCredit, globalResultDealerSales, globalResultFree;
    boolean chequeEnabled;

    Products productsDB;
    int productsDBDownCount, productsProgressStatus = 0,productsProgressColor = 0;

    Customers customersDB;
    int customersDBDownCount, customersProgressStatus = 0,customersProgressColor = 0;

    int repStoreProgressStatus = 0,repStoreProgressColor = 0;

    Itinerary itineraryDB;
    int itineraryDBDownCount, itineraryProgressStatus = 0,itineraryProgressColor = 0;

    Invoice invoiceDB;
    int invoiceDBDownCount, invoiceDBProgressStatus = 0,invoiceProgressColor = 0;

    CustomersPendingApproval customersPendingDB;
    int customersPendingDBupCount, customersPendingDBtoUpCount, customersPendingProgressStatus = 0,customersPendingProgressColor = 0;

    ReturnHeader returnDB;
    int returnDBupCount, returnDBtoUpCount, returnProgressStatus = 0,returnProgressColor = 0;

    ShelfQuantity shelfQtyDB;
    int shelfQtyDBupCount, shelfQtyDBtoUpCount, shelfQtyProgressStatus = 0,shelfQtyProgressColor = 0;

    int outstandingDBupCount, outstandingDBtoUpCount, outstandingUploadProgressStatus = 0,outstandingUploadProgressColor = 0, outstandingDownProgressStatus = 0;

    ProductUnload productUnloadDB;
    int productUnloadDBupCount, productUnloadDBtoUpCount, productUnloadProgressStatus = 0,productUnloadProgressColor = 0;


    DEL_Outstandiing dELOutstandDB;
    int dELOutstandDBDownCount, dELOutstandProgressStatus = 0,dELOutstandProgressColor = 0;

    CreditPeriod creditPeriodDB;
    int creditPeriodDBDownCount, creditPeriodProgressStatus = 0,creditPeriodProgressColor = 0;


    DealerSales dealerSalesDB;
    int dealerSalesDBDownCount, dealerSalesProgressStatus = 0,dealerSalesProgressColor = 0;

    DiscountStructures discountStructuresDB;
    int discountStructuresDBDownCount, discountStructuresProgressStatus = 0,discountStructuresProgressColor = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_manual_sync);


        recyclerViewManualsync = (RecyclerView) findViewById(R.id.recyclerView_manualsync);
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerViewManualsync.setLayoutManager(mLayoutManager);
        recyclerViewManualsync.setItemAnimator(new DefaultItemAnimator());

        manualSyncAdapter = new ManualSyncRecyAdapter(this, manualsynclist);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        deviceId = sharedPreferences.getString("DeviceId", "-1");
        repId = sharedPreferences.getString("RepId", "-1");
        chequeEnabled = sharedPreferences.getBoolean("cbPrefEnableCheckDetails", true);


        productsDB = new Products(ManualSync.this);
        customersDB = new Customers(ManualSync.this);
        itineraryDB = new Itinerary(ManualSync.this);
        invoiceDB = new Invoice(ManualSync.this);
        customersPendingDB = new CustomersPendingApproval(ManualSync.this);
        returnDB = new ReturnHeader(ManualSync.this);
        shelfQtyDB = new ShelfQuantity(ManualSync.this);
        productUnloadDB = new ProductUnload(ManualSync.this);
        dELOutstandDB = new DEL_Outstandiing(ManualSync.this);
        creditPeriodDB = new CreditPeriod(ManualSync.this);
        dealerSalesDB = new DealerSales(ManualSync.this);
        discountStructuresDB = new DiscountStructures(ManualSync.this);


        getProductsCount();
        getCustomersCount();

        getItineraryCount();
        getInvoiceCount();
        getCustomersPendingCount();
        getReturnsCount();
        getshelfQtyCount();
        getUploadOutstandingsCount();
        getProductUnloadCount();
        getdELOutstandCount();
        getCreditPeriodCount();
        getDealerSalesCount();
        getDiscountStructuresCount();


        setAllSyncOptions();


    }

    public void RecyclerViewOnItemClick(int position) {
        globalResultProducts = null;
        globalResultCustomers = null;
        globalResultInventory = null;
        globalResultItinerary = null;
        globalResultInvoices = null;
        globalResultUpCustomers = null;
        globalResultReturns = null;
        globalResultShelf = null;
        globalResultupOutstandings = null;
        globalResultProductUnload = null;
        globalResultDwnOutstanding = null;
        globalResultCredit = null;
        globalResultDealerSales = null;
        globalResultFree = null;
        if (isOnline()) {
            if (position == 0) {
                recyclerPosition = position;
                productsProgressStatus = 1;
                setAllSyncOptions();
                new DownloadProductsTask(ManualSync.this).execute("1");
                new TargetSync(ManualSync.this).execute();
            } else if (position == 1) {
                recyclerPosition = position;
                customersProgressStatus = 1;
                setAllSyncOptions();
                new DownloadCustomersTask(ManualSync.this).execute("1");
            } else if (position == 2) {
                recyclerPosition = position;
                new TransferAuditForStock(ManualSync.this).execute();

                repStoreProgressStatus = 1;
                setAllSyncOptions();
                Toast.makeText(this, "Please wait, this may take some time. You'll be notified when it's complete.", Toast.LENGTH_LONG).show();
            } else if (position == 3) {
                recyclerPosition = position;
                itineraryProgressStatus = 1;
                setAllSyncOptions();
                new DownloadItineraryTask(ManualSync.this).execute("1");
            } else if (position == 4) {
                recyclerPosition = position;
                invoiceDBProgressStatus = 1;
                setAllSyncOptions();

                Reps repController = new Reps(ManualSync.this);
                repController.openReadableDatabase();
                String[] reps = repController.getRepDetails();
                repController.closeDatabase();

                new UploadInvoiceHeaderTask(ManualSync.this, repId, deviceId, reps[8]).execute();
                new UploadInvoiceTask(ManualSync.this).execute("1");
            } else if (position == 5) {
                recyclerPosition = position;
                customersPendingProgressStatus = 1;
                setAllSyncOptions();
                new UploadNewCustomersTask(ManualSync.this).execute("1");
            } else if (position == 6) {
                recyclerPosition = position;
                returnProgressStatus = 1;
                setAllSyncOptions();
                new UploadRetunHeaderTask(ManualSync.this, repId, deviceId).execute();
                new UploadProductReturnsTask(ManualSync.this).execute("1");
            } else if (position == 7) {
                recyclerPosition = position;
                shelfQtyProgressStatus = 1;
                setAllSyncOptions();
                new UploadShelfQtyTask(ManualSync.this).execute("1");
            } else if (position == 8) {
                recyclerPosition = position;
                if (chequeEnabled) {
                    outstandingUploadProgressStatus = 1;
                    setAllSyncOptions();
                    new UploadInvoiceOutstandingTask(ManualSync.this).execute("1");
                } else {
                    Toast.makeText(getApplication(), "This function is not available on your version.", Toast.LENGTH_SHORT);
                }
            } else if (position == 9) {
                recyclerPosition = position;
                productUnloadProgressStatus = 1;
                setAllSyncOptions();
                new UploadProductUnloadTask(ManualSync.this).execute("1");
            } else if (position == 10) {
                recyclerPosition = position;
                dELOutstandProgressStatus = 1;
                setAllSyncOptions();
                new Download_DEL_Outstanding(ManualSync.this).execute();
            } else if (position == 11) {
            } else if (position == 12) {

            } else if (position == 13) {
                recyclerPosition = position;
                creditPeriodProgressStatus = 1;
                setAllSyncOptions();
                new DownloadCreditPeriods(ManualSync.this).execute();
            } else if (position == 14) {
                recyclerPosition = position;
                dealerSalesProgressStatus = 1;
                setAllSyncOptions();
                new DownloadDealerSalesTask(ManualSync.this).execute();
            } else if (position == 15) {
            } else if (position == 16) {
                recyclerPosition = position;
                discountStructuresProgressStatus = 1;
                setAllSyncOptions();
                new DownloadFreeIsues(ManualSync.this).execute();
            }

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(ManualSync.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("No internet access");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertDialog.show();
        }


    }

    public void getProductsCount() {
        productsDBDownCount = productsDB.getRowCount();
    }

    public void getCustomersCount() {
        customersDBDownCount = customersDB.getrowcount();
    }

    public void getItineraryCount() {
        itineraryDBDownCount = itineraryDB.getItineraryCount();
    }

    public void getInvoiceCount() {
        invoiceDBDownCount = invoiceDB.getToBeUploadeInvice();
    }

    public void getCustomersPendingCount() {
        customersPendingDBupCount = customersPendingDB.getUploadedCustomers();
        customersPendingDBtoUpCount = customersPendingDB.getToBeUploadeCustomers();
    }

    public void getReturnsCount() {
        returnDBupCount = returnDB.getUploadedReturn();
        returnDBtoUpCount = returnDB.getToBeUploadeReturn();
    }

    public void getshelfQtyCount() {
        shelfQtyDBupCount = shelfQtyDB.getUploadedShelf();
        shelfQtyDBtoUpCount = shelfQtyDB.getToBeUploadeShelf();
    }

    public void getUploadOutstandingsCount() {
        outstandingDBupCount = invoiceDB.getUploadedOutstanding();
        outstandingDBtoUpCount = invoiceDB.getToBeUploadeOutstanding();
    }

    public void getProductUnloadCount() {
        productUnloadDBupCount = productUnloadDB.getUploadedProdUnload();
        productUnloadDBtoUpCount = productUnloadDB.getToBeUploadeProdUnload();
    }

    public void getdELOutstandCount() {
        dELOutstandDBDownCount = dELOutstandDB.getRowCount();
    }

    public void getCreditPeriodCount() {
        creditPeriodDBDownCount = creditPeriodDB.getCreditPeriodCount();
    }

    public void getDealerSalesCount() {
        dealerSalesDBDownCount = dealerSalesDB.getDealerSalesCount();
    }

    public void getDiscountStructuresCount() {
        discountStructuresDBDownCount = discountStructuresDB.getDiscountStructuresCount();
    }

    public void setAllSyncOptions() {

        manualsynclist.clear();

        manualsynclist.add(new ManualSyncList("Syncronize Products", "Update your tablet with the newest products available", productsDBDownCount,0,productsProgressStatus,0,globalResultProducts,productsProgressColor));
        manualsynclist.add(new ManualSyncList("Syncronize Customers", "Udpdate all customer data on your tablet", customersDBDownCount, 5, customersProgressStatus, 0, globalResultCustomers,customersProgressColor));
        manualsynclist.add(new ManualSyncList("Syncronize Inventory", "Update your inventory", 0, 0, repStoreProgressStatus, 0, globalResultInventory,repStoreProgressColor));
        manualsynclist.add(new ManualSyncList("Syncronize Itinerary", "Update your itinerary", itineraryDBDownCount, 5, itineraryProgressStatus, 0, globalResultItinerary,itineraryProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Invoices", "Upload your invoices", 0, invoiceDBDownCount, invoiceDBProgressStatus, 1, globalResultInvoices,invoiceProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Customers", "Upload all new customers", customersPendingDBupCount, customersPendingDBtoUpCount, customersPendingProgressStatus, 1, globalResultUpCustomers,customersPendingProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Returns", "Upload all sales returns", returnDBupCount, returnDBtoUpCount, returnProgressStatus, 1, globalResultReturns,returnProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Shelf Quantities", "Upload the current stocks that the customers have", shelfQtyDBupCount, shelfQtyDBtoUpCount, shelfQtyProgressStatus, 1, globalResultShelf,shelfQtyProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Invoice Outstandings", "Upload your invoice outstanding details", outstandingDBupCount, outstandingDBtoUpCount, outstandingUploadProgressStatus, 1, globalResultupOutstandings,outstandingUploadProgressColor));
        manualsynclist.add(new ManualSyncList("Upload Product Unload Details", "Upload product unload details and check the status", productUnloadDBupCount, productUnloadDBtoUpCount, productUnloadProgressStatus, 1, globalResultProductUnload,productUnloadProgressColor));
        manualsynclist.add(new ManualSyncList("Download Outstanding", "Download Outstanding", dELOutstandDBDownCount, 5, dELOutstandProgressStatus, 0, globalResultDwnOutstanding,dELOutstandProgressColor));
        manualsynclist.add(new ManualSyncList("Download collection Note", "Download collection Note", 0, 0, 0, 0, "",0));
        manualsynclist.add(new ManualSyncList("Upload Collection Note", "Upload Collection Note", 0, 0, 0, 1, "",0));
        manualsynclist.add(new ManualSyncList("Download Credit Periods", "Download Credit Periods", creditPeriodDBDownCount, 0, creditPeriodProgressStatus, 0, globalResultCredit,creditPeriodProgressColor));
        manualsynclist.add(new ManualSyncList("Download Dealer Sales", "Download Dealer Sales", dealerSalesDBDownCount, 0, dealerSalesProgressStatus, 0, globalResultDealerSales,dealerSalesProgressColor));
        manualsynclist.add(new ManualSyncList("Download Approved Person", "Download Approved Person", 0, 0, 0, 0, "",0));
        manualsynclist.add(new ManualSyncList("Download Free Issues", "Download Free Issues", discountStructuresDBDownCount, 0, discountStructuresProgressStatus, 0, globalResultFree,discountStructuresProgressColor));

        recyclerViewManualsync.setAdapter(manualSyncAdapter);
        mLayoutManager.scrollToPosition(recyclerPosition);

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



    private class DownloadProductsTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public DownloadProductsTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getProductsCount();
            productsProgressStatus = 0;
            globalResultProducts = getResultType(returnCode);
            setAllSyncOptions();
        }
        protected void onProgressUpdate(Integer... progress) {
            productsProgressColor = 1;
            setAllSyncOptions();
        }
        @Override
        protected Integer doInBackground(String... params) {

            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                String maxRowID = "0";
                Products prodObject = new Products(ManualSync.this);
                prodObject.openReadableDatabase();
                String lastProductId = prodObject.getMaxProductId();
                prodObject.closeDatabase();
                Log.w("Log", "lastProductId:  " + lastProductId);
                if (lastProductId != "") {
                    maxRowID = lastProductId;
                }

                ArrayList<String[]> repStoreDataResponse = null;
                try {
                    WebService webService = new WebService();
                    repStoreDataResponse = webService.getProductList(deviceId, repId, maxRowID);
                } catch (SocketException e) {
                    e.printStackTrace();
                    repStoreDataResponse = new ArrayList<String[]>();
                    return 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    repStoreDataResponse = new ArrayList<String[]>();
                    return 0;
                }


                if (repStoreDataResponse != null) {
                    if (repStoreDataResponse.size() > 0) {
                        publishProgress(1);
                        Products products = new Products(ManualSync.this);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] custDetails = repStoreDataResponse.get(i);
                            Log.w("Log", "prod id  " + custDetails[0].trim());
                            products.openWritableDatabase();
                            boolean flag = products.isProductAvailable(custDetails[0].trim());
                            products.closeDatabase();
                            if (flag) {
                                Log.w("Log", " inside flag true  ");
                                products.openWritableDatabase();
                                Long result = products.updateProduct(
                                        custDetails[0], custDetails[1],
                                        custDetails[2], custDetails[3],
                                        custDetails[4], custDetails[5],
                                        custDetails[6], custDetails[7],
                                        custDetails[8], "", custDetails[9],
                                        custDetails[10], custDetails[11],
                                        custDetails[12], custDetails[13],
                                        custDetails[14], custDetails[15],
                                        custDetails[16], timeStamp,
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
                                Long result = products.insertProduct(
                                        custDetails[0].trim(), custDetails[1],
                                        custDetails[2], custDetails[3],
                                        custDetails[4], custDetails[5],
                                        custDetails[6], custDetails[7],
                                        custDetails[8], "", custDetails[9],
                                        custDetails[10], custDetails[11],
                                        custDetails[12], custDetails[13],
                                        custDetails[14], custDetails[15],
                                        custDetails[16], timeStamp,
                                        custDetails[17].trim());
                                if (result == -1) {
                                    returnValue = 7;
                                    products.closeDatabase();
                                    break;
                                }
                                products.closeDatabase();
                            }
                            returnValue = 5;
                        }
                        products.closeDatabase();
                    } else {
                        returnValue = 6;
                    }
                } else {
                    returnValue = 0;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    private class DownloadCustomersTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public DownloadCustomersTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getCustomersCount();
            customersProgressStatus = 0;
            globalResultCustomers = getResultType(returnCode);
            setAllSyncOptions();
            new DownloadCustomerImagesTask(context).execute("1");
            new AduitCustomerCount(context).execute();

        }
        protected void onProgressUpdate(Integer... progress) {
            customersProgressColor  = 1;
            setAllSyncOptions();

        }

        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                String maxRowID = "0";
                Customers customerObject = new Customers(context);
                customerObject.openReadableDatabase();

                String lastProductId = customerObject.getMaxCustomerId();
                customerObject.closeDatabase();
                Log.w("Log", "lastCustId:  " + lastProductId);
                if (lastProductId != "") {
                    if (lastProductId != null) {
                        maxRowID = lastProductId;
                    }
                }
                ArrayList<String[]> repStoreDataResponse = null;
                //  while (repStoreDataResponse == null) {
                try {
                    WebService webService = new WebService();
                    repStoreDataResponse = webService.getCustomerList(deviceId, repId, maxRowID);
                } catch (SocketException e) {
                    e.printStackTrace();
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                //  }

                if (repStoreDataResponse != null) {
                    if (repStoreDataResponse.size() > 0) {
                        publishProgress(1);
                        Customers customers = new Customers(ManualSync.this);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] custDetails = repStoreDataResponse.get(i);
                            customers.openReadableDatabase();
                            boolean isAvailable = customers.isCustomerDownloaded(custDetails[0]);
                            customers.closeDatabase();
                            if (isAvailable) {
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
                                        custDetails[33], // currentCredit,//test
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

                                );
                                customers.closeDatabase();
                                if (result == -1) {
                                    returnValue = 7;
                                    break;
                                }
                                returnValue = 5;

                            } else {
                                customers.openWritableDatabase();
                                Long result = customers.insertCustomer(
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
                                        custDetails[30],
                                        custDetails[31],
                                        android.util.Base64.decode(custDetails[32], Base64.DEFAULT),
                                        custDetails[34],
                                        custDetails[35],
                                        custDetails[36]
                                );
                                customers.closeDatabase();
                                if (result == -1) {
                                    returnValue = 7;
                                    break;
                                }
                                returnValue = 5;
                            }
                        }
                    } else {
                        returnValue = 6;
                    }
                } else {
                    returnValue = 0;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

    private class DownloadProductRepStoreTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public DownloadProductRepStoreTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {

            repStoreProgressStatus = 0;
            globalResultInventory = getResultType(returnCode);
            setAllSyncOptions();
            notification("Syncronize Inventory","Inventory downloaded successfully");

        }
        protected void onProgressUpdate(Integer... progress) {
            repStoreProgressColor  = 1;
            setAllSyncOptions();

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                int maxRowID = 0;
                ProductRepStore repStoreObject = new ProductRepStore(ManualSync.this);
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
                    repStoreDataResponse = webService.getProductRepStoreList(deviceId, repId, maxRowID);

                } catch (SocketException e) {
                    e.printStackTrace();
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

                if (repStoreDataResponse != null) {
                    if (repStoreDataResponse.size() > 0) {
                        publishProgress(1);
                        ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] custDetails = repStoreDataResponse.get(i);
                            productRepStore.openReadableDatabase();
                            Long result = productRepStore.insertProductRepStore(custDetails[0],
                                    custDetails[2], custDetails[5],
                                    custDetails[3], custDetails[4], custDetails[6], custDetails[7], custDetails[8],
                                    timeStamp);
                            if (result == -1) {
                                returnValue = 7;
                                productRepStore.closeDatabase();
                                break;
                            }
                            productRepStore.closeDatabase();
                            returnValue = 5;
                        }

                    } else {
                        returnValue = 6;
                    }

                } else {
                    returnValue = 0;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    private class DownloadItineraryTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public DownloadItineraryTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {

            getItineraryCount();
            itineraryProgressStatus = 0;
            globalResultItinerary = getResultType(returnCode);
            setAllSyncOptions();
            new TransferAuditIternery(ManualSync.this).execute();
        }
        protected void onProgressUpdate(Integer... progress) {
            itineraryProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "DownloadItineraryTask result : starting ");
            if (isOnline()) {
                String maxRowID = "0";
                Itinerary itineraryObj = new Itinerary(ManualSync.this);
                itineraryObj.openReadableDatabase();
                String itineraryId = itineraryObj.getMaxItnId();
                itineraryObj.closeDatabase();
                Log.w("Log", "lastProductId:  " + itineraryId);
                if (itineraryId != "" && itineraryId != null) {
                    maxRowID = itineraryId;
                }
                ArrayList<String[]> repStoreDataResponse = null;

                try {
                    WebService webService = new WebService();
                    repStoreDataResponse = webService.getItineraryListForRep(repId, deviceId, maxRowID);

                } catch (SocketException e) {
                    e.printStackTrace();
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

                if (repStoreDataResponse != null) {
                    if (repStoreDataResponse.size() > 0) {
                        publishProgress(1);
                        Itinerary itinerary = new Itinerary(ManualSync.this);
                        itinerary.openWritableDatabase();
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] itnDetails = repStoreDataResponse.get(i);
                            Long result = itinerary.insertItinerary(itnDetails[8],
                                    itnDetails[0], itnDetails[1], itnDetails[2],
                                    itnDetails[3], itnDetails[4], itnDetails[5],
                                    itnDetails[6], itnDetails[7], timeStamp,
                                    "false", "false", "false");
                            if (result == -1) {
                                returnValue = 7;
                                break;
                            }
                            returnValue = 5;
                        }
                        itinerary.closeDatabase();
                    } else {
                        returnValue = 6;
                    }
                } else {
                    returnValue = 0;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

    private class UploadInvoiceTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadInvoiceTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getInvoiceCount();
            invoiceDBProgressStatus = 0;
            globalResultInvoices = getResultType(11);
            setAllSyncOptions();
            new TransferAudit(ManualSync.this).execute();
        }
        protected void onProgressUpdate(Integer... progress) {
            invoiceProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                Invoice invoiceObject = new Invoice(ManualSync.this);
                invoiceObject.openReadableDatabase();
                List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
                invoiceObject.closeDatabase();
                Log.w("Log", "invoice size :  " + invoice.size());
                for (String[] invoiceData : invoice) {
                    Log.w("Log", "invoice id :  " + invoiceData[0]);
                    Log.w("Log", "invoice date :  " + invoiceData[10]);
                    ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
                    InvoicedProducts invoicedProductsObject = new InvoicedProducts(ManualSync.this);
                    invoicedProductsObject.openReadableDatabase();
                    List<String[]> invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceData[0]);
                    invoicedProductsObject.closeDatabase();
                    Log.w("Log", "invoicedProducts size :  " + invoicedProducts.size());
                    for (String[] invoicedProduct : invoicedProducts) {
                        ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                        productRepStore.openReadableDatabase();
                        String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(invoicedProduct[3], invoicedProduct[2]);
                        productRepStore.closeDatabase();
                        Log.w("Log", "batch :  " + invoicedProduct[3]);
                        Log.w("Log", "exp :  " + productRepStor[5]);
                        Products product = new Products(ManualSync.this);
                        product.openReadableDatabase();
                        String[] productData = product.getProductDetailsByProductCode(invoicedProduct[2]);
                        product.closeDatabase();
                        Itinerary itinerary = new Itinerary(ManualSync.this);
                        itinerary.openReadableDatabase();
                        String tempCust = itinerary.getItineraryStatus(invoiceData[1]);
                        itinerary.closeDatabase();
                        String custNo = "";
                        Itinerary itineraryTwo = new Itinerary(ManualSync.this);
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
                            Log.w("Log", "profit :  " + profit);
                            invoiceDetails[0] = invoicedProduct[2]; // Product
                            invoiceDetails[1] = invoicedProduct[1]; // Invoice
                            invoiceDetails[2] = "N"; // Issue mode
                            invoiceDetails[3] = invoicedProduct[7]; // Normal
                            invoiceDetails[5] = invoiceData[2]; // Payment type

                            System.out.println("zzzzzzzzzzzzzzzz : "+productRepStor[5]);
                            System.out.println("pppppppppppppppppppp : "+invoicedProduct[2]);
                            System.out.println("bbbbbbbbbbbbbbbb : "+invoicedProduct[3]);
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
                          //  invoiceDetails[4] = changeDateFormat(invoiceData[10]);
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
                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadInvoiceDetails(deviceId, repId, invoicedProductDetailList);
                    } catch (SocketException e) {
                        returnValue = 0;
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        returnValue = 0;
                        e.printStackTrace();
                        return 0;
                    }
                    if (responseArr != null) {
                        if (responseArr.contains("No Error")) {
                            Invoice invoiceObj = new Invoice(ManualSync.this);
                            invoiceObj.openReadableDatabase();
                            invoiceObj.setInvoiceUpdatedStatus(invoiceData[0], "true");
                            invoiceObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                if (invoice.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

    private class UploadNewCustomersTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadNewCustomersTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getCustomersPendingCount();
            globalResultUpCustomers = getResultType(returnCode);
            customersPendingProgressStatus = 0;
            setAllSyncOptions();
        }
        protected void onProgressUpdate(Integer... progress) {
            customersPendingProgressColor = 1;
            setAllSyncOptions();
        }


        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                CustomersPendingApproval rtnProdObject = new CustomersPendingApproval(ManualSync.this);
                rtnProdObject.openReadableDatabase();
                List<String[]> rtnProducts = rtnProdObject.getCustomersByUploadStatus("false");
                rtnProdObject.closeDatabase();
                ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
                for (String[] rtnProdData : rtnProducts) {
                    String[] invoiceDetails = new String[24];
                    invoiceDetails[0] = rtnProdData[0];
                    invoiceDetails[1] = rtnProdData[1];
                    invoiceDetails[2] = rtnProdData[2];
                    invoiceDetails[3] = rtnProdData[3];
                    invoiceDetails[4] = rtnProdData[4];
                    invoiceDetails[5] = rtnProdData[5];
                    invoiceDetails[6] = rtnProdData[6];
                    invoiceDetails[7] = rtnProdData[7];
                    invoiceDetails[8] = rtnProdData[8];
                    invoiceDetails[9] = rtnProdData[9];
                    invoiceDetails[10] = rtnProdData[11];
                    invoiceDetails[11] = rtnProdData[12];
                    invoiceDetails[12] = rtnProdData[13];
                    invoiceDetails[13] = rtnProdData[15];
                    invoiceDetails[14] = rtnProdData[14];
                    invoiceDetails[15] = rtnProdData[16];
                    invoiceDetails[16] = rtnProdData[17];
                    invoiceDetails[17] = rtnProdData[18];
                    invoiceDetails[18] = rtnProdData[20];
                    invoiceDetails[19] = rtnProdData[21];
                    invoiceDetails[20] = rtnProdData[22];
                    ImageGallery imageGallery = new ImageGallery(ManualSync.this);
                    imageGallery.openReadableDatabase();
                    String primaryImage = imageGallery.getPrimaryImageforCustomerId(rtnProdData[0]);
                    imageGallery.closeDatabase();
                    File customerImageFile = new File(Environment.getExternalStorageDirectory() + File.separator
                            + "DCIM" + File.separator + "Channel_Bridge_Images"
                            + File.separator + primaryImage);
                    if (customerImageFile.exists()) {
                        try {

                            Bitmap bm = ImageHandler.decodeSampledBitmapFromResource(String.valueOf(customerImageFile), 400, 550);
                            rtnProdData[24] = ImageHandler.encodeTobase64(bm);
                        } catch (IllegalArgumentException e) {
                            Log.w("Illegal argument exception", e.toString());
                        } catch (OutOfMemoryError e) {
                            Log.w("Out of memory error :(", e.toString());
                        }

                    }
                    invoiceDetails[20] = primaryImage;
                    invoiceDetails[21] = rtnProdData[10];
                    invoiceDetails[22] = rtnProdData[23];
                    invoiceDetails[23] = rtnProdData[24];
                    String responseArr = null;

                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadNewCustomerDetails(deviceId, repId, invoiceDetails);
                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }

                    if (responseArr != null) {
                        if (responseArr.contains("Ok")) {
                            Log.w("Log", "Update the iternarary status");
                            CustomersPendingApproval rtnProdObj = new CustomersPendingApproval(ManualSync.this);
                            rtnProdObj.openReadableDatabase();
                            rtnProdObj.setCustomerUploadedStatus(rtnProdData[0], "true");
                            rtnProdObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }

                    } else {
                        returnValue = 0;
                    }
                }
                Log.w("Log", "invoicedProductDetailList size :  " + invoicedProductDetailList.size());
                if (rtnProducts.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    public class UploadRetunHeaderTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ReturnHeader returnHeaderController;
        private ArrayList<ReturnHeaderEntity> headerArrayList;
        WebService webService;

        public UploadRetunHeaderTask(Context context, String repId, String deviceId) {
            this.context = context;
            returnHeaderController = new ReturnHeader(context);
            headerArrayList = new ArrayList<>();
            webService = new WebService();
        }

        @Override
        protected void onPreExecute() {
            headerArrayList = returnHeaderController.getNotUploadedHeaders();
            super.onPreExecute();
        }

        protected void onPostExecute(Integer returnCode) {

        }

        @Override
        protected Void doInBackground(Void... params) {
            returnHeaderController.openWritableDatabase();
            for (ReturnHeaderEntity entity : headerArrayList) {
                String response = webService.uploadReturnHeader(repId, deviceId, entity);
                String[] resArray = response.split("-");
                String status = resArray[0].toString();
                if (status.trim().equals("OK")) {
                    returnHeaderController.updateStatus(resArray[1]);
                }
            }
            returnHeaderController.closeDatabase();
            return null;
        }
    }

    private class UploadProductReturnsTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadProductReturnsTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getReturnsCount();
            returnProgressStatus = 0;
            globalResultReturns = getResultType(11);
            setAllSyncOptions();
            new TransferAuditForReturn(ManualSync.this).execute();
        }
        protected void onProgressUpdate(Integer... progress) {
            returnProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;
            if (isOnline()) {
                String timeStamp = new SimpleDateFormat("yyyy").format(new Date());
                ProductReturns rtnProdObject = new ProductReturns(ManualSync.this);
                rtnProdObject.openReadableDatabase();
                List<String[]> rtnProducts = rtnProdObject.getProductReturnsByStatus("false");
                rtnProdObject.closeDatabase();
                for (String[] rtnProdData : rtnProducts) {
                    Products product = new Products(ManualSync.this);
                    product.openReadableDatabase();
                    String[] productData = product.getProductDetailsByProductCode(rtnProdData[1]);
                    product.closeDatabase();
                    ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                    productRepStore.openReadableDatabase();
                    String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(rtnProdData[2], rtnProdData[1]);
                    productRepStore.closeDatabase();
                    ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                    String[] invoiceDetails = new String[14];

                    invoiceDetails[0] = rtnProdData[1]; // Product
                    invoiceDetails[1] = rtnProdData[3]; // Invoice
                    String issueMode = rtnProdData[4];
                    invoiceDetails[2] = issueMode; // Issue mode
                    invoiceDetails[3] = rtnProdData[5]; // Normal
                    invoiceDetails[4] = changeDateFormatForReturnDate(rtnProdData[7]);
                    if (productRepStor[5] == null || productRepStor[5] == "") {
                        invoiceDetails[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                    } else {
                        invoiceDetails[5] = changeDateFormat(productRepStor[5]); // expire
                    }
                    invoiceDetails[6] = rtnProdData[2]; // batch no
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
                        String[] invoiceDetailsFree = new String[14];
                        invoiceDetailsFree[0] = rtnProdData[1]; // Product
                        invoiceDetailsFree[1] = rtnProdData[3]; // Invoice
                        if (issueMode.equals("SR")) {
                            invoiceDetailsFree[2] = "SF"; //
                        } else {
                            invoiceDetailsFree[2] = "CF"; //
                        }
                        invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
                        invoiceDetailsFree[4] = changeDateFormatForReturnDate(rtnProdData[7]); // Rtn
                        if (productRepStor[5] == null || productRepStor[5] == "") {
                            invoiceDetailsFree[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                        } else {
                            invoiceDetailsFree[5] = changeDateFormat(productRepStor[5]); // expire
                        }
                        invoiceDetailsFree[6] = rtnProdData[2]; // batch no
                        invoiceDetailsFree[7] = rtnProdData[8]; // cust no
                        invoiceDetailsFree[8] = "0"; // Unit price
                        invoiceDetailsFree[9] = rtnProdData[0]; // Id
                        invoiceDetailsFree[10] = rtnProdData[11]; // Discount
                        invoiceDetailsFree[11] = rtnProdData[14];
                        invoiceDetailsFree[12] = rtnProdData[15];
                        invoiceDetailsFree[13] = rtnProdData[16];
                        returnedProductList.add(invoiceDetailsFree);
                    }
                    String responseArr = null;
                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadProductReturnsDetails(deviceId, repId, returnedProductList);

                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                    if (responseArr != null || !responseArr.equals("null")) {
                        if (responseArr.contains("No Error")) {
                            ProductReturns rtnProdObj = new ProductReturns(ManualSync.this);
                            rtnProdObj.openReadableDatabase();
                            rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0], "true");
                            rtnProdObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                if (rtnProducts.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    private class UploadShelfQtyTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadShelfQtyTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getshelfQtyCount();
            shelfQtyProgressStatus = 0;
            globalResultShelf = getResultType(returnCode);
            setAllSyncOptions();
        }
        protected void onProgressUpdate(Integer... progress) {
            shelfQtyProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                ShelfQuantity rtnProdObject = new ShelfQuantity(ManualSync.this);
                rtnProdObject.openReadableDatabase();
                List<String[]> rtnProducts = rtnProdObject.getShelfQuantitiesByStatus("false");
                rtnProdObject.closeDatabase();
                ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
                for (String[] rtnProdData : rtnProducts) {
                    String[] invoiceDetails = new String[13];
                    invoiceDetails[0] = repId; // rep id
                    invoiceDetails[1] = rtnProdData[1]; // Invoice no
                    invoiceDetails[2] = rtnProdData[2]; // Invoice date
                    invoiceDetails[3] = rtnProdData[3]; // customer id
                    invoiceDetails[4] = rtnProdData[4]; // item code
                    invoiceDetails[5] = rtnProdData[6]; // item code
                    invoiceDetails[6] = rtnProdData[5]; // item code

                    String responseArr = null;
                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadShelfQuantityDetails(deviceId, repId, invoiceDetails);
                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                    if (responseArr != null || !responseArr.equals("null")) {
                        if (responseArr.contains("Record Inserted Successfully")) {
                            ShelfQuantity rtnProdObj = new ShelfQuantity(ManualSync.this);
                            rtnProdObj.openReadableDatabase();
                            rtnProdObj.setShelfQtyUploadedStatus(rtnProdData[0], "true");
                            rtnProdObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                Log.w("Log", "invoicedProductDetailList size :  " + invoicedProductDetailList.size());
                if (rtnProducts.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

    private class UploadInvoiceOutstandingTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadInvoiceOutstandingTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getUploadOutstandingsCount();
            outstandingUploadProgressStatus = 0;
            globalResultupOutstandings = getResultType(returnCode);
            setAllSyncOptions();
        }
        protected void onProgressUpdate(Integer... progress) {
            outstandingUploadProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                Invoice invoiceObject = new Invoice(ManualSync.this);
                invoiceObject.openReadableDatabase();
                List<String[]> invoice = invoiceObject.getInvoicesByOutstandingUploadStatus("false");
                invoiceObject.closeDatabase();
                for (String[] invoiceData : invoice) {
                    Itinerary itinerary = new Itinerary(ManualSync.this);
                    itinerary.openReadableDatabase();
                    String tempCust = itinerary.getItineraryStatus(invoiceData[1]);
                    itinerary.closeDatabase();
                    String custNo = "";
                    Itinerary itineraryTwo = new Itinerary(ManualSync.this);
                    itineraryTwo.openReadableDatabase();
                    if (tempCust.equals("true")) {
                        String[] itnDetails = itineraryTwo.getItineraryDetailsForTemporaryCustomer(invoiceData[1]);
                        custNo = deviceId + "_" + itnDetails[7];// this
                    } else {
                        String[] itnDetails = itineraryTwo.getItineraryDetailsById(invoiceData[1]);
                        custNo = itnDetails[4];
                    }
                    itineraryTwo.closeDatabase();
                    String[] invoiceOutstandingDetails = new String[6];
                    invoiceOutstandingDetails[0] = invoiceData[0]; // Invoice Id
                    invoiceOutstandingDetails[1] = custNo; // cust No
                    invoiceOutstandingDetails[2] = invoiceData[11].substring(0, 10); // invoice
                    invoiceOutstandingDetails[3] = invoiceData[3]; // total
                    invoiceOutstandingDetails[4] = invoiceData[5]; // credit
                    invoiceOutstandingDetails[5] = invoiceData[13]; // credit
                    String responseArr = null;

                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadInvoiceOutstandingDetails(deviceId, repId, invoiceOutstandingDetails);

                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }

                    if (responseArr != null || !responseArr.equals("null")) {
                        if (responseArr.contains("No Error")) {
                            Invoice invoiceObj = new Invoice(ManualSync.this);
                            invoiceObj.openReadableDatabase();
                            invoiceObj.setInvoiceOutstandingUpdatedStatus(invoiceData[0], "true");
                            invoiceObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                if (invoice.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

    private class UploadProductUnloadTask extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadProductUnloadTask(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getProductUnloadCount();
            productUnloadProgressStatus = 0;
            globalResultProductUnload = getResultType(returnCode);
            setAllSyncOptions();
        }
        protected void onProgressUpdate(Integer... progress) {
            productUnloadProgressColor = 1;
            setAllSyncOptions();

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                ProductUnload productUnloadObject = new ProductUnload(ManualSync.this);
                productUnloadObject.openReadableDatabase();
                List<String[]> productUnload = productUnloadObject.getProdUnloadsByUploadStatus("1");
                productUnloadObject.closeDatabase();
                for (String[] invoiceChequeData : productUnload) {
                    String[] unloadProdDetails = new String[5];
                    unloadProdDetails[0] = invoiceChequeData[0];
                    unloadProdDetails[1] = invoiceChequeData[1];
                    unloadProdDetails[2] = invoiceChequeData[4];
                    unloadProdDetails[3] = changeDateFormat(invoiceChequeData[3].substring(0, 10));
                    unloadProdDetails[4] = invoiceChequeData[2];
                    String responseArr = null;

                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.SetUnloadingDetails(deviceId, repId, unloadProdDetails);
                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                    if (responseArr != null || !responseArr.equals("null")) {
                        if (responseArr.contains("No Error")) {
                            ProductUnload invoiceChequeObj = new ProductUnload(ManualSync.this);
                            invoiceChequeObj.openReadableDatabase();
                            invoiceChequeObj.setProdUnloadStatus(invoiceChequeData[0], "0");
                            invoiceChequeObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                if (productUnload.size() < 1) {
                    returnValue = 4;
                }
                ProductUnload proUnloadObject = new ProductUnload(ManualSync.this);
                proUnloadObject.openReadableDatabase();
                List<String[]> prodUnload = proUnloadObject.getProdUnloadsByUploadStatus("0");
                proUnloadObject.closeDatabase();
                String unloadIds = "";
                boolean flag = true;
                for (String[] invoiceChequeData : prodUnload) {
                    if (flag) {
                        unloadIds = unloadIds + invoiceChequeData[0];
                        flag = false;
                    } else {
                        unloadIds = unloadIds + "," + invoiceChequeData[0];
                    }
                }
                ArrayList<String[]> responseArr = null;
                while (responseArr == null) {
                    try {
                        WebService webService = new WebService();
                        responseArr = webService.getGetUnloadingStatus(deviceId, repId, unloadIds);
                        Thread.sleep(100);

                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
                Log.w("Log", "update data result : " + responseArr.contains("No Error"));
                boolean responseFlag = false;
                if (responseArr.size() > 0) {
                    for (String[] details : responseArr) {
                        String idNo = details[0];
                        String unloadStatus = "0";
                        Long result = new Long(-1);
                        if (details[5].trim().equalsIgnoreCase("a")) {
                            unloadStatus = "2";
                            result = (long) 0;
                        } else if (details[5].trim().equalsIgnoreCase("r")) {
                            unloadStatus = "3";
                            int unloadQty = Integer.parseInt(details[2]);
                            ProductRepStore ProductRepStore = new ProductRepStore(getApplication());
                            ProductRepStore.openReadableDatabase();
                            result = ProductRepStore.updateRepStoreQtyAdd(details[4], unloadQty, details[1]);
                            ProductRepStore.closeDatabase();
                        }
                        if (result != -1) {
                            ProductUnload invoiceChequeObj = new ProductUnload(ManualSync.this);
                            invoiceChequeObj.openReadableDatabase();
                            invoiceChequeObj.setProdUnloadStatus(idNo, unloadStatus);
                            invoiceChequeObj.closeDatabase();
                            responseFlag = true;
                        }
                    }
                }

                if (responseFlag && returnValue == 2) {
                    returnValue = 8;
                } else if (responseFlag && returnValue == 4) {
                    returnValue = 9;
                } else if (!responseFlag && returnValue == 2) {
                    returnValue = 2;
                } else if (!responseFlag && returnValue == 4) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    public class Download_DEL_Outstanding extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public Download_DEL_Outstanding(Context context) {
            this.context = context;
        }
        protected void onProgressUpdate(Integer... progress) {
            dELOutstandProgressColor = 1;
            setAllSyncOptions();

        }
        protected Integer doInBackground(String... strings) {
            DEL_Outstandiing Outstandiing = new DEL_Outstandiing(context);
            int returnValue = 1;
            if (isOnline()) {
                String dbStatus = "0";
                if (dbStatus == "0") {
                    try {
                        ArrayList<String[]> repStoreDataResponse = null;
                        WebService webService = new WebService();
                        repStoreDataResponse = webService.Download_DEL_Outstanding(deviceId, repId);
                        if (repStoreDataResponse != null || !repStoreDataResponse.equals("null")) {
                            if (repStoreDataResponse.size() > 0) {
                                for (int i = 0; i < repStoreDataResponse.size(); i++) {
                                    publishProgress(1);
                                    String[] DEL_Sales = repStoreDataResponse.get(i);
                                    Outstandiing.openWritableDatabase();
                                    boolean exitancy = Outstandiing.isExistOutstandingRow(DEL_Sales[0]);
                                    Long result;
                                    if (exitancy == false) {
                                        result = Outstandiing.insertDEL_Out_Standiing(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3], DEL_Sales[4], DEL_Sales[5],
                                                DEL_Sales[6], DEL_Sales[7], DEL_Sales[8], DEL_Sales[9], DEL_Sales[10], DEL_Sales[11], DEL_Sales[12],
                                                DEL_Sales[13], DEL_Sales[14]
                                        );
                                    } else {
                                        result = Outstandiing.updateCreditAmountByCusNOAndInvoNo(DEL_Sales[10], DEL_Sales[5], DEL_Sales[7]);
                                    }
                                    Outstandiing.closeDatabase();
                                    returnValue = 2;
                                }

                            } else {
                                returnValue = 6;
                            }
                        } else {
                            returnValue = 0;
                        }
                    } catch (Exception e) {
                        Log.w("Log", "Download Products error: " + e.toString());
                        returnValue = 0;
                    }
                }
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(Integer returnCode) {
            getdELOutstandCount();
            dELOutstandProgressStatus = 0;
            globalResultDwnOutstanding = getResultType(returnCode);
            setAllSyncOptions();
        }
    }

    private class DownloadCreditPeriods extends AsyncTask<Void, Void, Void> {
        Context context;
        ArrayList<com.halcyon.Entity.CreditPeriod> responseCreditList;
        int returnValue = 1;

        public DownloadCreditPeriods(Context context) {
            this.context = context;
            responseCreditList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                WebService webService = new WebService();
                responseCreditList = webService.getCreditPeriods(deviceId, Integer.parseInt(repId));
            } catch (Exception e) {
                returnValue = 0;
                Log.e("cr web error", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (responseCreditList != null) {
                if (responseCreditList.size() != 0) {
                    creditPeriodProgressColor = 1;
                    setAllSyncOptions();
                    for (com.halcyon.Entity.CreditPeriod period : responseCreditList) {
                        creditPeriodDB.addCreditPeriods(period);
                    }
                    returnValue = 2;
                } else {
                    returnValue = 6;
                }
            } else {
                returnValue = 0;
            }
            getCreditPeriodCount();
            creditPeriodProgressStatus = 0;
            globalResultCredit = getResultType(returnValue);
            setAllSyncOptions();
        }
    }

    public class DownloadDealerSalesTask extends AsyncTask<Void, Integer, Integer> {
        Context context;
        WebService webService;
        ArrayList<DealerSaleEntity> salesList = null;
        int returnValue = 1;

        public DownloadDealerSalesTask(Context context) {
            this.context = context;
            webService = new WebService();
            salesList = new ArrayList<>();
        }
        protected void onProgressUpdate(Integer... progress) {
            dealerSalesProgressColor = 1;
            setAllSyncOptions();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            int a = 5;
            try {
                salesList = webService.getDealerSalesFromServer(deviceId, repId);
            } catch (Exception e) {
                returnValue = 0;
            }

            if (salesList != null) {
                if (salesList.size() != 0) {
                    publishProgress(1);
                    for (DealerSaleEntity entity : salesList) {
                        dealerSalesDB.insertDealerSales(entity);
                    }
                    returnValue = 2;
                } else {
                    returnValue = 6;
                }
            } else {
                returnValue = 0;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer returnCode) {
            getDealerSalesCount();
            dealerSalesProgressStatus = 0;
            globalResultDealerSales = getResultType(returnValue);
            setAllSyncOptions();
        }
    }

    private class DownloadFreeIsues extends AsyncTask<String, Integer, Integer> {
        private final Context context;
        ArrayList<String[]> responseArr = null;
        int returnValue = 1;

        public DownloadFreeIsues(Context context) {
            this.context = context;
        }

        protected void onProgressUpdate(Integer... progress) {
            discountStructuresProgressColor = 1;
            setAllSyncOptions();

        }

        protected void onPostExecute(Integer returnCode) {

            getDiscountStructuresCount();
            discountStructuresProgressStatus = 0;
            globalResultFree = getResultType(returnCode);
            setAllSyncOptions();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                try {
                    WebService webService = new WebService();
                    responseArr = webService.getDiscountStructures(repId);
                } catch (Exception e) {
                    e.printStackTrace();
                    returnValue = 0;
                }

                if (responseArr != null) {
                    if (responseArr.size() != 0) {
                        try {
                            publishProgress(1);
                            DiscountStructures ds = new DiscountStructures(ManualSync.this);
                            ds.openReadableDatabase();
                            for (int i = 0; i < responseArr.size(); i++) {
                                String[] disDetails = responseArr.get(i);
                                ds.insertDiscountStructures(disDetails[0], disDetails[1], disDetails[2], disDetails[3], disDetails[4], disDetails[5], disDetails[6], disDetails[7], disDetails[8]);
                            }
                            returnValue = 2;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        returnValue = 6;
                    }
                } else {
                    returnValue = 0;
                }
            } else {
                returnValue = 3;
            }
            return returnValue;

        }
    }


    private class TransferAuditIternery extends AsyncTask<Void, Void, Void> {
        ArrayList<String[]> responseArr;
        private final Context context;

        private TransferAuditIternery(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseArr = null;
            WebService webService = new WebService();
            Itinerary in = new Itinerary(ManualSync.this);
            in.openReadableDatabase();
            String count = in.getCount();
            try {
                responseArr = webService.checkTransferAuditIternery(repId, count);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (responseArr == null) {
            } else {
                if (responseArr.size() > 0) {
                    Itinerary itinerary = new Itinerary(ManualSync.this);
                    itinerary.openWritableDatabase();
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                    for (int i = 0; i < responseArr.size(); i++) {
                        String[] itnDetails = responseArr.get(i);
                        Long resultIn = itinerary.insertItinerary(
                                itnDetails[0],//itnRowId
                                itnDetails[1],//itnId
                                itnDetails[3],//date
                                "",//visitNo
                                itnDetails[4],//pharmacyId
                                itnDetails[5],//pharmacyCode
                                itnDetails[6],//name
                                itnDetails[7],//target
                                itnDetails[2],//repId
                                timeStamp,//timeStamp
                                "false",//isTemporaryCustomer
                                "false",//isInvoiced
                                "false"//isActive
                        );
                        if (resultIn == -1) {
                            break;
                        }
                    }
                    itinerary.closeDatabase();
                } else {

                }
            }

        }

    }
    private class TransferAuditForStock extends AsyncTask<Void, Void, Void> {
        ArrayList<String> responseArr;
        private final Context context;

        private TransferAuditForStock(Context context) {
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
            repStoreProgressStatus = 0;
            globalResultInventory = getResultType(10);
            setAllSyncOptions();
            if (responseArr == null) {
                new CheckDayTasksForStock(ManualSync.this).execute();
            } else {
                for (int i = 0; i < responseArr.size(); i++) {
                    Invoice invoiceObj = new Invoice(ManualSync.this);
                    invoiceObj.openReadableDatabase();
                    invoiceObj.setInvoiceUpdatedStatus(responseArr.get(i), "false");
                    invoiceObj.closeDatabase();

                }
                Reps repController = new Reps(ManualSync.this);
                repController.openReadableDatabase();
                String[] reps = repController.getRepDetails();
                repController.closeDatabase();

                new UploadInvoiceHeaderTask(ManualSync.this, repId, deviceId, reps[8]).execute();
                new UploadInvoiceTaskAudit(ManualSync.this).execute("1");
            }


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
            System.out.println("20/20 TransferAudit start ");
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
                new CheckDayTasks(ManualSync.this).execute();
                System.out.println("20/20 CheckDayTasks ");
            } else {
                System.out.println("20/20 responseArr ");
                for (int i = 0; i < responseArr.size(); i++) {
                    Invoice invoiceObj = new Invoice(ManualSync.this);
                    invoiceObj.openReadableDatabase();
                    invoiceObj.setInvoiceUpdatedStatus(responseArr.get(i), "false");
                    String intest=responseArr.get(i);
                    System.out.println("Invooo :"+responseArr.get(i));
                    invoiceObj.closeDatabase();


                }
                Reps repController = new Reps(ManualSync.this);
                repController.openReadableDatabase();
                String[] reps = repController.getRepDetails();
                repController.closeDatabase();

                new UploadInvoiceHeaderTask(ManualSync.this, repId, deviceId, reps[8]).execute();
                new UploadInvoiceTaskAudit(ManualSync.this).execute("1");
            }


        }

    }
    private class CheckDayTasksForStock extends AsyncTask<Void, Void, Void> {
        String responseInvoiceCount;
        private final Context context;

        private CheckDayTasksForStock(Context context) {
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
            Invoice invoiceObject = new Invoice(ManualSync.this);
            invoiceObject.openReadableDatabase();
            invoicedIds = invoiceObject.getInvoiceCountByDate();
            invoiceObject.closeDatabase();
            try {
                if (Integer.parseInt(responseInvoiceCount) == invoicedIds.size()) {
                    new DownloadProductRepStoreTask(ManualSync.this).execute("1");
                } else {
                    new getCheckDayTasksInvoiceIDForStock(ManualSync.this).execute();
                }
            } catch (NumberFormatException e) {
                new getCheckDayTasksInvoiceIDForStock(ManualSync.this).execute();
            } catch (Exception e) {
                new getCheckDayTasksInvoiceIDForStock(ManualSync.this).execute();
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
            System.out.println("20/20 CheckDayTasks start ");
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
            Invoice invoiceObject = new Invoice(ManualSync.this);
            invoiceObject.openReadableDatabase();
            invoicedIds = invoiceObject.getInvoiceCountByDate();
            invoiceObject.closeDatabase();
            try {
                if (Integer.parseInt(responseInvoiceCount) == invoicedIds.size()) {
                    notification("Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();
                    invoiceDBProgressStatus = 0;
                    globalResultInvoices = getResultType(13);
                    setAllSyncOptions();

                } else {
                    System.out.println("20/20 getCheckDayTasksInvoiceID start ");
                    new getCheckDayTasksInvoiceID(ManualSync.this).execute();
                }
            } catch (NumberFormatException e) {
                System.out.println("20/20 getCheckDayTasksInvoiceID start ");
                new getCheckDayTasksInvoiceID(ManualSync.this).execute();
            } catch (Exception e) {
                System.out.println("20/20 getCheckDayTasksInvoiceID start ");
                new getCheckDayTasksInvoiceID(ManualSync.this).execute();
            }

        }

    }


    private class getCheckDayTasksInvoiceIDForStock extends AsyncTask<Void, Void, Void> {
        private final Context context;
        ArrayList<String> checkDayTasksInvoiceID;
        ArrayList<String> allInvoicedIds;
        ArrayList<String> allReturnInvoicedIds;

        private getCheckDayTasksInvoiceIDForStock(Context context) {
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
                    new DownloadProductRepStoreTask(ManualSync.this).execute("1");
                }else if(checkDayTasksInvoiceID.isEmpty()){
                    new DownloadProductRepStoreTask(ManualSync.this).execute("1");
                } else {
                    for (int i = 0; i < checkDayTasksInvoiceID.size(); i++) {
                        Invoice invoiceObj = new Invoice(ManualSync.this);
                        invoiceObj.openReadableDatabase();
                        invoiceObj.setInvoiceUpdatedStatus(checkDayTasksInvoiceID.get(i), "false");
                        invoiceObj.closeDatabase();


                    }
                    Reps repController = new Reps(ManualSync.this);
                    repController.openReadableDatabase();
                    String[] reps = repController.getRepDetails();
                    repController.closeDatabase();

                    new UploadInvoiceHeaderTask(ManualSync.this, repId, deviceId, reps[8]).execute();
                    new UploadInvoiceTaskAudit(ManualSync.this).execute("1");
                }
            } catch (Exception e) {

            }
        }
    }


    private class getCheckDayTasksInvoiceID extends AsyncTask<Void, Void, Void> {
        private final Context context;
        ArrayList<String> checkDayTasksInvoiceID;
        ArrayList<String> allInvoicedIds;
        ArrayList<String> allReturnInvoicedIds;

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
                    notification("Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();

                    invoiceDBProgressStatus = 0;
                    globalResultInvoices = getResultType(13);
                    setAllSyncOptions();

                }else if(checkDayTasksInvoiceID.isEmpty()){
                    notification("Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();


                    invoiceDBProgressStatus = 0;
                    globalResultInvoices = getResultType(13);
                    setAllSyncOptions();

                } else {
                    for (int i = 0; i < checkDayTasksInvoiceID.size(); i++) {
                        Invoice invoiceObj = new Invoice(ManualSync.this);
                        invoiceObj.openReadableDatabase();
                        invoiceObj.setInvoiceUpdatedStatus(checkDayTasksInvoiceID.get(i), "false");
                        invoiceObj.closeDatabase();


                    }
                    Reps repController = new Reps(ManualSync.this);
                    repController.openReadableDatabase();
                    String[] reps = repController.getRepDetails();
                    repController.closeDatabase();

                    new UploadInvoiceHeaderTask(ManualSync.this, repId, deviceId, reps[8]).execute();
                    new UploadInvoiceTaskAudit(ManualSync.this).execute("1");

                }
           } catch (Exception e) {
                notification("Audit","Audit not successful,please do manually sync to invoice");
                System.out.println("20/20 getCheckDayTasksInvoiceID Exception ");
                invoiceDBProgressStatus = 0;
                globalResultInvoices = getResultType(13);
                setAllSyncOptions();
           }
        }
    }

    public class UploadInvoiceHeaderTask extends AsyncTask<Void, Void, Void> {
        Invoice invoiceObject;
        Context context;
        private String dealerId;
        private WebService webService;

        public UploadInvoiceHeaderTask(Context context, String repId, String deviceId, String dealerId) {
            this.context = context;
            invoiceObject = new Invoice(context);
            webService = new WebService();
            this.dealerId = dealerId;
        }

        @Override
        public Void doInBackground(Void... params) {
            invoiceObject.openReadableDatabase();
            List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
            invoiceObject.closeDatabase();
            Log.i("Called -->", "web service");
            if(invoice.size()==0){

            }else {
                webService.uploadInvoiceHeader(deviceId, repId, dealerId, invoice);
            }


            return null;
        }
    }

    private class UploadInvoiceTaskAudit extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadInvoiceTaskAudit(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            new TransferAudit(ManualSync.this).execute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;
            if (isOnline()) {
                Invoice invoiceObject = new Invoice(ManualSync.this);
                invoiceObject.openReadableDatabase();
                List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
                invoiceObject.closeDatabase();
                for (String[] invoiceData : invoice) {
                    ArrayList<String[]> invoicedProductDetailList = new ArrayList<String[]>();
                    InvoicedProducts invoicedProductsObject = new InvoicedProducts(ManualSync.this);
                    invoicedProductsObject.openReadableDatabase();
                    List<String[]> invoicedProducts = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceData[0]);
                    invoicedProductsObject.closeDatabase();
                    for (String[] invoicedProduct : invoicedProducts) {
                        ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                        productRepStore.openReadableDatabase();
                        String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(invoicedProduct[3], invoicedProduct[2]);
                        productRepStore.closeDatabase();
                        Products product = new Products(ManualSync.this);
                        product.openReadableDatabase();
                        String[] productData = product.getProductDetailsByProductCode(invoicedProduct[2]);
                        product.closeDatabase();
                        Itinerary itinerary = new Itinerary(ManualSync.this);
                        itinerary.openReadableDatabase();
                        String tempCust = itinerary.getItineraryStatus(invoiceData[1]);
                        itinerary.closeDatabase();
                        String custNo = "";
                        Itinerary itineraryTwo = new Itinerary(ManualSync.this);
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
                        Invoice invoiceObj = new Invoice(ManualSync.this);
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
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }

    private class UploadProductReturnsTaskAduit extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadProductReturnsTaskAduit(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            new TransferAudit(ManualSync.this).execute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;
            Log.w("Log", "param result : " + params[0]);
            Log.w("Log", "loadProductRepStoreData result : starting ");
            if (isOnline()) {
                String timeStamp = new SimpleDateFormat("yyyy").format(new Date());
                ProductReturns rtnProdObject = new ProductReturns(ManualSync.this);
                rtnProdObject.openReadableDatabase();
                List<String[]> rtnProducts = rtnProdObject.getProductReturnsByStatus("false");
                rtnProdObject.closeDatabase();
                Log.w("Log", "rtnProducts size :  " + rtnProducts.size());
                for (String[] rtnProdData : rtnProducts) {
                    Products product = new Products(ManualSync.this);
                    product.openReadableDatabase();
                    String[] productData = product.getProductDetailsByProductCode(rtnProdData[1]);
                    product.closeDatabase();
                    ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                    productRepStore.openReadableDatabase();
                    String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(rtnProdData[2], rtnProdData[1]);
                    productRepStore.closeDatabase();

                    ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                    String[] invoiceDetails = new String[14];
                    invoiceDetails[0] = rtnProdData[1]; // Product
                    invoiceDetails[1] = rtnProdData[3]; // Invoice
                    String issueMode = rtnProdData[4];
                    invoiceDetails[2] = issueMode; // Issue mode
                    invoiceDetails[3] = rtnProdData[5]; // Normal
                    invoiceDetails[4] = changeDateFormatForReturnDate(rtnProdData[7]);
                    if (productRepStor[5] == null || productRepStor[5] == "") {
                        invoiceDetails[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                    } else {
                        invoiceDetails[5] = changeDateFormat(productRepStor[5]); // expire

                    }
                    invoiceDetails[6] = rtnProdData[2]; // batch no
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
                        String[] invoiceDetailsFree = new String[14];
                        invoiceDetailsFree[0] = rtnProdData[1]; // Product
                        invoiceDetailsFree[1] = rtnProdData[3]; // Invoice
                        invoiceDetailsFree[2] = "RF"; // Issue mode
                        invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
                        invoiceDetailsFree[4] = changeDateFormat(rtnProdData[7]); // Rtn
                        if (productRepStor[5] == null || productRepStor[5] == "") {
                            invoiceDetailsFree[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                        } else {
                            invoiceDetailsFree[5] = changeDateFormat(productRepStor[5]); // expire
                        }
                        invoiceDetailsFree[6] = rtnProdData[2]; // batch no
                        invoiceDetailsFree[7] = rtnProdData[8]; // cust no
                        invoiceDetailsFree[8] = "0"; // Unit price
                        invoiceDetailsFree[9] = rtnProdData[0]; // Id
                        invoiceDetailsFree[10] = rtnProdData[11]; // Discount
                        invoiceDetailsFree[11] = rtnProdData[14];
                        invoiceDetailsFree[12] = rtnProdData[15];
                        invoiceDetailsFree[13] = rtnProdData[16];
                        returnedProductList.add(invoiceDetailsFree);
                    }
                    String responseArr = null;
                    while (responseArr == null) {
                        try {
                            WebService webService = new WebService();
                            responseArr = webService.uploadProductReturnsDetails(deviceId, repId, returnedProductList);
                            Thread.sleep(100);
                        } catch (SocketException e) {
                            e.printStackTrace();
                            return 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return 0;
                        }

                    }
                    Log.w("Log", "update data result : " + responseArr.contains("No Error"));
                    if (responseArr.contains("No Error")) {
                        Log.w("Log", "Update the iternarary status");
                        ProductReturns rtnProdObj = new ProductReturns(ManualSync.this);
                        rtnProdObj.openReadableDatabase();
                        rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0], "true");
                        rtnProdObj.closeDatabase();
                        returnValue = 2;
                    }
                    Log.w("Log", "loadProductRepStoreData result : " + responseArr);
                }
                if (rtnProducts.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;

        }

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

    public String changeDateFormat(String date) {

        System.out.println("xxxxxxxxxxxxxxxxx :"+date);
        try {
            date = date.substring(0, 10);
        }catch (NullPointerException nulpointer){

        }

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


    public String changeDateFormatForReturnDate(String date) {
        try {
            date = date.substring(0, 10);
        }catch (NullPointerException nulpointer){

        }

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy");
        String reformattedStr = "";
        try {
            reformattedStr = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }


    public String getResultType(int res) {
        String result = null;

        if (res == 1) {
            result = "Unable to Upload data,Please try again in a few minutes";
        } else if (res == 2) {
            result = "Data uploaded successfully";
        } else if (res == 3) {
            result = "There is no Internet Connectivity, Please check network connectivity";
        } else if (res == 4) {
            result = "Theres no data to upload";
        } else if (res == 5) {
            result = "Data downloaded successfully";
        } else if (res == 6) {
            result = "Theres no data to download";
        } else if (res == 7) {
            result = "Unable to save data,Please try again in a few minutes";
        } else if (res == 8) {
            result = "Data uploaded and sync with server successfully";
        } else if (res == 9) {
            result = "Theres no data to upload but sync with server successfully";
        } else if (res == 10) {
            result = "Please wait,You'll be notified when it's complete.";
        }else if (res == 11) {
            result = "Audit Started";
        }else if (res == 12) {
            result = "Audit not successful,Please try again";
        }else if (res == 13) {
            result = "Audit successful";
        } else if (res == 0) {
            result = "Server is busy,Please try again in a few minutes";
        }

        return result;
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void notification(String titel,String message){
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cb_logo_icon)
                .setContentTitle(titel)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(message);
        Intent resultIntent = null;
        resultIntent = new Intent(this, LoginActivity.class);

        TaskStackBuilder stackBuilder = null;
        PendingIntent resultPendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        }

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

    private class TransferAuditForReturn extends AsyncTask<Void, Void, Void> {
        ArrayList<String> responseArr;
        private final Context context;

        private TransferAuditForReturn(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseArr = null;
            WebService webService = new WebService();
            try {
                responseArr = webService.checkTransferAuditReuturn(repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (responseArr == null) {
                new CheckDayTasksForReturns(ManualSync.this).execute();
            } else {
                for (int i = 0; i < responseArr.size(); i++) {
                    ReturnHeader returnHeader = new ReturnHeader(ManualSync.this);
                    returnHeader.openReadableDatabase();
                    returnHeader.SetupdateStatusFalse(responseArr.get(i));
                    System.out.println("Re Header Invooo Num:"+responseArr.get(i));
                    returnHeader.closeDatabase();
                    ProductReturns prodReturn = new ProductReturns(ManualSync.this);
                    prodReturn.openReadableDatabase();
                    prodReturn.setRtnProductsUploadedStatusToAudit(responseArr.get(i), "false");
                    prodReturn.closeDatabase();

                }
                new UploadRetunHeaderTask(ManualSync.this, repId, deviceId).execute();
                new UploadProductReturnsTaskforAduit(ManualSync.this).execute("1");
            }


        }

    }
    private class CheckDayTasksForReturns extends AsyncTask<Void, Void, Void> {
        String responseInvoiceCount;
        private final Context context;

        private CheckDayTasksForReturns(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            responseInvoiceCount = null;
            WebService webService = new WebService();
            try {
                responseInvoiceCount = webService.checkInvoiceCountReturn(repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ReturnHeader returnHeader = new ReturnHeader(ManualSync.this);
            returnHeader.openReadableDatabase();
            ReturninvoicedIds = returnHeader.getReturnInvoiceCountByDate();
            returnHeader.closeDatabase();

            try {
                if (Integer.parseInt(responseInvoiceCount) == ReturninvoicedIds.size()) {
                    notification("Return Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();

                    returnProgressStatus = 0;
                    globalResultReturns = getResultType(13);
                    setAllSyncOptions();


                } else {
                    new getCheckDayTasksInvoiceIDForReturn(ManualSync.this).execute();
                }
            } catch (NumberFormatException e) {
                new getCheckDayTasksInvoiceIDForReturn(ManualSync.this).execute();
            } catch (Exception e) {
                new getCheckDayTasksInvoiceIDForReturn(ManualSync.this).execute();
            }

        }

    }

    private class getCheckDayTasksInvoiceIDForReturn extends AsyncTask<Void, Void, Void> {
        private final Context context;
        ArrayList<String> checkDayTasksInvoiceID;
        ArrayList<String> allInvoicedIds;
        ArrayList<String> allReturnInvoicedIds;

        private getCheckDayTasksInvoiceIDForReturn(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            checkDayTasksInvoiceID = null;
            WebService webService = new WebService();
            try {
                checkDayTasksInvoiceID = webService.getCheckInvoiceIdReturn(repId, ReturninvoicedIds);
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
                    notification("Return Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();
                    returnProgressStatus = 0;
                    globalResultReturns = getResultType(13);
                    setAllSyncOptions();

                }else if(checkDayTasksInvoiceID.isEmpty()){
                    notification("Return Audit","Successfully audited");
                    Toast.makeText(ManualSync.this, "Successfully audited", Toast.LENGTH_SHORT).show();
                    returnProgressStatus = 0;
                    globalResultReturns = getResultType(13);
                    setAllSyncOptions();

                } else {
                    for (int i = 0; i < checkDayTasksInvoiceID.size(); i++) {

                        ReturnHeader returnHeader = new ReturnHeader(ManualSync.this);
                        returnHeader.openReadableDatabase();
                        returnHeader.SetupdateStatusFalse(checkDayTasksInvoiceID.get(i));
                        System.out.println("Re Header Invooo Num:"+checkDayTasksInvoiceID.get(i));
                        returnHeader.closeDatabase();
                        ProductReturns prodReturn = new ProductReturns(ManualSync.this);
                        prodReturn.openReadableDatabase();
                        prodReturn.setRtnProductsUploadedStatusToAudit(checkDayTasksInvoiceID.get(i), "false");
                        prodReturn.closeDatabase();

                    }
                    new UploadRetunHeaderTask(ManualSync.this, repId, deviceId).execute();
                    new UploadProductReturnsTaskforAduit(ManualSync.this).execute(deviceId, repId);
                }
            } catch (Exception e) {
                notification("Return Audit","Audit not successful,please do manually sync to invoice");
                returnProgressStatus = 0;
                globalResultReturns = getResultType(12);
                setAllSyncOptions();
            }
        }
    }
    private class UploadProductReturnsTaskforAduit extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public UploadProductReturnsTaskforAduit(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            new TransferAuditForReturn(ManualSync.this).execute();
        }
        protected void onProgressUpdate(Integer... progress) {
            returnProgressColor = 1;
            setAllSyncOptions();

        }
        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;
            if (isOnline()) {
                String timeStamp = new SimpleDateFormat("yyyy").format(new Date());
                ProductReturns rtnProdObject = new ProductReturns(ManualSync.this);
                rtnProdObject.openReadableDatabase();
                List<String[]> rtnProducts = rtnProdObject.getProductReturnsByStatus("false");
                rtnProdObject.closeDatabase();
                for (String[] rtnProdData : rtnProducts) {
                    Products product = new Products(ManualSync.this);
                    product.openReadableDatabase();
                    String[] productData = product.getProductDetailsByProductCode(rtnProdData[1]);
                    product.closeDatabase();
                    ProductRepStore productRepStore = new ProductRepStore(ManualSync.this);
                    productRepStore.openReadableDatabase();
                    String[] productRepStor = productRepStore.getProductDetailsByProductBatchAndProductCode(rtnProdData[2], rtnProdData[1]);
                    productRepStore.closeDatabase();
                    ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                    String[] invoiceDetails = new String[14];

                    invoiceDetails[0] = rtnProdData[1]; // Product
                    invoiceDetails[1] = rtnProdData[3]; // Invoice
                    String issueMode = rtnProdData[4];
                    invoiceDetails[2] = issueMode; // Issue mode
                    invoiceDetails[3] = rtnProdData[5]; // Normal
                    invoiceDetails[4] = changeDateFormatForReturnDate(rtnProdData[7]);
                    if (productRepStor[5] == null || productRepStor[5] == "") {
                        invoiceDetails[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                    } else {
                        invoiceDetails[5] = changeDateFormat(productRepStor[5]); // expire
                    }
                    invoiceDetails[6] = rtnProdData[2]; // batch no
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
                        String[] invoiceDetailsFree = new String[14];
                        invoiceDetailsFree[0] = rtnProdData[1]; // Product
                        invoiceDetailsFree[1] = rtnProdData[3]; // Invoice
                        if (issueMode.equals("SR")) {
                            invoiceDetailsFree[2] = "SF"; //
                        } else {
                            invoiceDetailsFree[2] = "CF"; //
                        }
                        invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
                        invoiceDetailsFree[4] = changeDateFormatForReturnDate(rtnProdData[7]); // Rtn
                        if (productRepStor[5] == null || productRepStor[5] == "") {
                            invoiceDetailsFree[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
                        } else {
                            invoiceDetailsFree[5] = changeDateFormat(productRepStor[5]); // expire
                        }
                        invoiceDetailsFree[6] = rtnProdData[2]; // batch no
                        invoiceDetailsFree[7] = rtnProdData[8]; // cust no
                        invoiceDetailsFree[8] = "0"; // Unit price
                        invoiceDetailsFree[9] = rtnProdData[0]; // Id
                        invoiceDetailsFree[10] = rtnProdData[11]; // Discount
                        invoiceDetailsFree[11] = rtnProdData[14];
                        invoiceDetailsFree[12] = rtnProdData[15];
                        invoiceDetailsFree[13] = rtnProdData[16];
                        returnedProductList.add(invoiceDetailsFree);
                    }
                    String responseArr = null;
                    try {
                        publishProgress(1);
                        WebService webService = new WebService();
                        responseArr = webService.uploadProductReturnsDetails(deviceId, repId, returnedProductList);

                    } catch (SocketException e) {
                        e.printStackTrace();
                        return 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                    if (responseArr != null || !responseArr.equals("null")) {
                        if (responseArr.contains("No Error")) {
                            ProductReturns rtnProdObj = new ProductReturns(ManualSync.this);
                            rtnProdObj.openReadableDatabase();
                            rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0], "true");
                            rtnProdObj.closeDatabase();
                            returnValue = 2;
                        } else {
                            returnValue = 0;
                        }
                    } else {
                        returnValue = 0;
                    }
                }
                if (rtnProducts.size() < 1) {
                    returnValue = 4;
                }
            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }

    }


    private class AduitCustomerCount extends AsyncTask<Void, Void, Void> {
        int responseCustomerCount = 0;
        private final Context context;

        private AduitCustomerCount(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebService webService = new WebService();
            try {
                responseCustomerCount = webService.AuditCustomerCount(repId,deviceId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            int customerCount;
            Customers cus = new Customers(ManualSync.this);
            customerCount =cus.getrowcount();

            customrIDs = cus.getCustomerpharmacyID();
            System.out.println("vdfgffgbfgfgg    :"+customrIDs.size());

            if(customerCount==responseCustomerCount){
                notification("Customers Audit","Customers Successfully audited");
                Toast.makeText(ManualSync.this, "Customers Successfully audited", Toast.LENGTH_SHORT).show();
            }else {
                System.out.println("DownloadCustomersTaskAudit   start  :");

              new DownloadCustomersTaskAudit(ManualSync.this).execute();

            }


        }

    }
    private class DownloadCustomersTaskAudit extends AsyncTask<String, Integer, Integer> {
        private final Context context;

        public DownloadCustomersTaskAudit(Context context) {
            this.context = context;
        }

        protected void onPostExecute(Integer returnCode) {
            getCustomersCount();
            customersProgressStatus = 0;
            globalResultCustomers = getResultType(returnCode);
            setAllSyncOptions();


        }
        protected void onProgressUpdate(Integer... progress) {
            customersProgressColor  = 1;
            setAllSyncOptions();

        }

        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;
            if (isOnline()) {
                ArrayList<String[]> repStoreDataResponse = null;
                try {
                    WebService webService = new WebService();
                    repStoreDataResponse = webService.GetAuditCustomerList(deviceId, repId, customrIDs);
                } catch (SocketException e) {
                    e.printStackTrace();
                    return 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                if (repStoreDataResponse != null) {
                    if (repStoreDataResponse.size() > 0) {
                        publishProgress(1);
                        Customers customers = new Customers(ManualSync.this);
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                        for (int i = 0; i < repStoreDataResponse.size(); i++) {
                            String[] custDetails = repStoreDataResponse.get(i);
                            customers.openReadableDatabase();
                            boolean isAvailable = customers.isCustomerDownloaded(custDetails[0]);
                            customers.closeDatabase();
                            if (isAvailable) {
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
                                        custDetails[33], // currentCredit,//test
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

                                );
                                customers.closeDatabase();
                                if (result == -1) {
                                    returnValue = 7;
                                    break;
                                }
                                returnValue = 5;

                            } else {
                                customers.openWritableDatabase();
                                Long result = customers.insertCustomer(
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
                                        custDetails[30],
                                        custDetails[31],
                                        android.util.Base64.decode(custDetails[32], Base64.DEFAULT),
                                        custDetails[34],
                                        custDetails[35],
                                        custDetails[36]
                                );
                                customers.closeDatabase();
                                if (result == -1) {
                                    returnValue = 7;
                                    break;
                                }
                                returnValue = 5;
                            }
                        }
                    } else {
                        returnValue = 6;
                    }
                } else {
                    returnValue = 0;
                }

            } else {
                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ManualSync.this);
            autoSyncOnOffFlag.openReadableDatabase();
            autoSyncOnOffFlag.AutoSyncActive(0);
            autoSyncOnOffFlag.closeDatabase();
            return returnValue;
        }
    }

}
