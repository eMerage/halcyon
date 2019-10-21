package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridgebs.DownloadCustomersTask;
import com.halcyon.channelbridgebs.Download_DEL_Outstanding;
import com.halcyon.channelbridgebs.UploadCollectionNoteTask;
import com.halcyon.channelbridgebs.UploadInvoiceHeaderTask;
import com.halcyon.channelbridgebs.UploadInvoiceOutstandingTask;


import com.halcyon.channelbridgebs.UploadShelfQtyTask;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.CollectionNoteSendToApprovel;
import com.halcyon.channelbridgedb.CustomerProduct;
import com.halcyon.channelbridgedb.CustomerProductAvg;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedCheque;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Remarks;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.ShelfQuantity;
import com.halcyon.channelbridgedb.Target;
import com.halcyon.channelbridgedb.TemporaryInvoice;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import pl.droidsonroids.gif.GifImageView;

public class InvoiceGen3Activity extends Activity implements LocationListener {
    Location location;
    double lat, lng;
    String rowId, pharmacyId;
    TextView tViewCustomerName, tViewDate, tViewAddress, tViewTotalItems,
            tViewTotal, tViewCash, tViewCheque, tViewCredit, tViewRemain,
            tViewMarketReturns, tViewDiscount, tViewNeedToPay,
            tViewInvoiceNumber;
    TableLayout tblInvoice;
    Button btnPrint, btnSave, btnCancel;
    String cash, credit, marketReturns, discount, needToPay, paymentOption,
            totalPrice, totalQuantity, cheque, invoiceNumber, custName,
            repName, custAddress;
    String collectionDate = "", releaseDate = "", chequeNumber = "",
            creditDuration = "";
    Long timeStampMillies;
    String startTime = "";
    String creditAmount = "";
    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();
    long invoiceId;
    //	Dialog exportOptions;
    boolean flag = false;
    boolean chequeEnabled = false;
    boolean discountEntered = false;
    ArrayList<SelectedProduct> productList = new ArrayList<SelectedProduct>();
    ArrayList<SelectedProduct> shelfQuantityList = new ArrayList<SelectedProduct>();
    ArrayList<ReturnProduct> returnProductArray = new ArrayList<ReturnProduct>();
    Download_DEL_Outstanding out;
    private LocationManager locationManager;
    private String onTimeOrNot;
    private int selectedCreditPeriodIndex;
    private String selectedBank, selectedBranch;
    TemporaryInvoice tempInvoController;
    CollectionNoteSendToApprovel aprove;
    String collectNumber = "";
    String customerNumber = "";
    String referenceNumber = "";
    private Boolean isCheckEntered;
    private String selectedInvoOption = "";
    private String seletedPaymentOptionCode = "";
    private String branchCode = "";
    byte[] chequeimage;
    String dicountValue = "0";
    Invoice invoiceObject;

    ArrayList<String> invoicedIds;
    boolean manualFreeEnable;

    //  int totalQuantity = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_gen_3);

        Log.w("IG3####", "saveFlag val content @@@ : " + InvoiceGen2Activity.saveFlag);

        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        tViewAddress = (TextView) findViewById(R.id.tvAddress);
        tViewTotalItems = (TextView) findViewById(R.id.tvTotalQuantity);
        tViewTotal = (TextView) findViewById(R.id.tvTotal);
        tViewCash = (TextView) findViewById(R.id.tvCash);
        tViewCheque = (TextView) findViewById(R.id.tvCheque);
        tViewCredit = (TextView) findViewById(R.id.tvCredit);
        tViewRemain = (TextView) findViewById(R.id.tvRemain);
        tViewMarketReturns = (TextView) findViewById(R.id.tvMarketReturn);
        tViewDiscount = (TextView) findViewById(R.id.tvDiscount);
        tViewNeedToPay = (TextView) findViewById(R.id.tvNeedToPay);
        tViewInvoiceNumber = (TextView) findViewById(R.id.tvInvoiceNumber);
        tblInvoice = (TableLayout) findViewById(R.id.tlInvoice);
        btnPrint = (Button) findViewById(R.id.btnPrint);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);

        tempInvoController = new TemporaryInvoice(InvoiceGen3Activity.this);
        tempInvoController.openWritableDatabase();
        aprove = new CollectionNoteSendToApprovel(InvoiceGen3Activity.this);
        collectNumber = aprove.GenareCollectionNoteNumber();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//		btnPrint.setEnabled(false);
        if (savedInstanceState != null) {
            getDataFromPreviousActivity(savedInstanceState);
        } else {
            getDataFromPreviousActivity(getIntent().getExtras());
        }
        if (InvoiceGen2Activity.saveFlag) {

            btnPrint.setEnabled(false);
        } else {

            flag = true;

            btnCancel.setText("Close");
            btnPrint.setEnabled(true);
            btnSave.setEnabled(false);

        }

//		exportOptions = new Dialog(InvoiceGen3Activity.this);
//		exportOptions.setContentView(R.layout.export_options);
//		exportOptions.setTitle("Select how you want to export:");
//		ImageButton iBtnPdf = (ImageButton) exportOptions
//				.findViewById(R.id.ibPdf);
//		ImageButton iBtnXls = (ImageButton) exportOptions
//				.findViewById(R.id.ibXls);
//		ImageButton iBtnPrint = (ImageButton) exportOptions
//				.findViewById(R.id.ibPrint);
//		Button btnCancelPopup = (Button) exportOptions
//				.findViewById(R.id.bCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Null?")
                .setCancelable(false)
                .setPositiveButton("Print",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Itinerary List",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                startActivity(startItinerary);
                                finish();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        setInitialData();
        seperateShelfQuantity();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean invoiceQtySuggestion = preferences.getBoolean("cbPrefProductAvg", true);

        if (invoiceQtySuggestion) {
            saveCustomerAverage();
        } else {
            saveCustomerProductQuantity();
        }

        invoiceObject = new Invoice(this);
        populateinvoices(productList);

//		btnCancelPopup.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				exportOptions.dismiss();
//			}
//		});

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                locationManager.removeUpdates(InvoiceGen3Activity.this);
                String repName = getRepName();
                printFunction(custName, custAddress, repName, String.valueOf(invoiceId));

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                locationManager.removeUpdates(InvoiceGen3Activity.this);
                if (flag) {
                    finish();
                    Intent itineraryListIntent = new Intent(
                            "com.halcyon.channelbridge.ITINERARYLIST");
                    startActivity(itineraryListIntent);

                } else {
                    Intent invoiceGen2ActivityIntent = new Intent(
                            getApplicationContext(), InvoiceGen2Activity.class);
                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("Id", rowId);
                    bundleToView.putString("PharmacyId", pharmacyId);
                    bundleToView.putString("Discount", discount);
                    bundleToView.putString("Cheque", cheque);
                    bundleToView.putString("Cash", cash);
                    bundleToView.putString("startTime", startTime);
                    bundleToView.putString("referenceNumber", referenceNumber);
                    bundleToView.putBoolean("isCheckEntered", isCheckEntered);
                    bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                    if (chequeEnabled) {
                        bundleToView.putString("ChequeNumber", chequeNumber);
                        bundleToView
                                .putString("CollectionDate", collectionDate);
                        bundleToView.putString("ReleaseDate", releaseDate);
                    }
                    bundleToView.putString("CreditDuration", creditDuration);
                    bundleToView.putParcelableArrayList("SelectedProducts",
                            selectedProductsArray);
                    bundleToView.putParcelableArrayList("ReturnProducts",
                            returnProductArray);
                    bundleToView.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
                    bundleToView.putString("TotalPrice", totalPrice);
                    bundleToView.putString("selectedInvoOption", selectedInvoOption);
                    invoiceGen2ActivityIntent.putExtras(bundleToView);
                    startActivity(invoiceGen2ActivityIntent);
                    finish();
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                flag = true;
                timeStampMillies = new Date().getTime();
                if (flag) {
                    btnCancel.setText("Close");
                }
                btnPrint.setEnabled(true);
                btnSave.setEnabled(false);

                if (InvoiceGen2Activity.saveFlag) {

                    for (SelectedProduct sp : selectedProductsArray) {

                    }
                    //                   Log.w("##### discount: ", discount);
                    if (discount != "" && (Double.parseDouble(discount) > 0)) {
                        for (SelectedProduct sp : selectedProductsArray) {

                            sp.setDiscount(Double.parseDouble(discount));

                        }
                    }

                    for (SelectedProduct sp : selectedProductsArray) {
                        Log.w("Individual DISCOUNT", sp.getDiscount() + "");
                    }

                    updateInvoiceDb();
                    updateInvoicedProductDb();
                    updateRepsStore();
                    updateItinerary();
                    updateShelfQuantityDB();
                    saveReturns();
                    updateInvoicedCheque();

                    upadateRemark(pharmacyId);

                    Target target = new Target(InvoiceGen3Activity.this);

                    target.UpdateAchievement(selectedProductsArray);


                    if (Double.parseDouble(cash) > 0 || Double.parseDouble(cheque) > 0) {
                        saveChequeToCollectionNote();
                    }
                    tempInvoController.deleteAllRecords();


                    if (isOnline()) {

                        Toast noMatchesFound = Toast.makeText(getApplication(),
                                "Data upload started.", Toast.LENGTH_SHORT);
                        noMatchesFound.show();

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        String deviceId = sharedPreferences.getString("DeviceId", "-1");
                        String repId = sharedPreferences.getString("RepId", "-1");

                        Reps repController = new Reps(InvoiceGen3Activity.this);
                        repController.openReadableDatabase();
                        String[] reps = repController.getRepDetails();
                        repController.closeDatabase();

                        new UploadInvoiceHeaderTask(InvoiceGen3Activity.this, repId, deviceId, reps[8]).execute();
                        new UploadInvoiceTask(InvoiceGen3Activity.this).execute(deviceId, repId);

                        new UploadProductReturnsTask(InvoiceGen3Activity.this)
                                .execute(deviceId,
                                        repId);

                        new UploadShelfQtyTask(InvoiceGen3Activity.this)
                                .execute(deviceId,
                                        repId);

                        SharedPreferences preferencesTwo = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());
                        boolean chequeEnabled = preferencesTwo.getBoolean("cbPrefEnableCheckDetails", false);
                        Log.i("My Boolean Value====>", "" + chequeEnabled);

                        // if (chequeEnabled) {
                        new UploadInvoiceOutstandingTask(InvoiceGen3Activity.this).execute(
                                deviceId, repId);

                        // }
                        new UploadCollectionNoteTask(InvoiceGen3Activity.this).execute();
//InvoiceGen3Activity.this
                        out = new Download_DEL_Outstanding(InvoiceGen3Activity.this);
                        out.execute(deviceId,
                                repId);

                        new DownloadCustomersTask(InvoiceGen3Activity.this)
                                .execute(deviceId,
                                        repId);

                        new TargetSync(InvoiceGen3Activity.this).execute();


                    } else {
                        Toast noMatchesFound = Toast.makeText(getApplication(),
                                "Please check the internet conectivity!",
                                Toast.LENGTH_SHORT);
                        noMatchesFound.show();
                    }

                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(InvoiceGen3Activity.this
                                    .getBaseContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("AutoSyncRun", true);
                    editor.commit();

                    AlertDialog alertDialog = new AlertDialog.Builder(InvoiceGen3Activity.this).create();
                    alertDialog.setMessage("Your invoice saved!");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            showAchievement();
                        }
                    });
                    alertDialog.show();

                    InvoiceGen2Activity.saveFlag = false;
                }
            }

        });

    }

    private void saveChequeToCollectionNote() {
        //credit set to empty (request by frank)
        Log.i("referenceNumber ->", "" + referenceNumber);
        aprove.openWritableDatabase();
        aprove.insertCollectionNoteSendToApprovel(collectNumber, referenceNumber, custName, creditAmount,
                invoiceNumber, credit, selectedInvoOption, cash, cheque, chequeNumber, selectedBank, selectedBranch, collectionDate, releaseDate, chequeimage, seletedPaymentOptionCode, branchCode, customerNumber, "WO", credit);
        aprove.closeDatabase();
    }


    /* (non-Javadoc)
         * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
         */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putString("Id", rowId);
        outState.putString("PharmacyId", pharmacyId);
        outState.putString("Cash", cash);
        outState.putString("Credit", credit);
        outState.putString("Cheque", cheque);
        outState.putString("CreditDuration", creditDuration);
        outState.putString("MarketReturns", marketReturns);
        outState.putString("Discount", discount);
        outState.putString("NeedToPay", needToPay);
        outState.putString("TotalPrice", totalPrice);
        outState.putString("TotalQuantity", totalQuantity);
        outState.putString("InvoiceNumber", invoiceNumber);
        outState.putString("startTime", startTime);
        outState.putLong("invoiceId", invoiceId);
        outState.putBoolean("ManualFreeEnable", manualFreeEnable);


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails",
                true);

        if (chequeEnabled) {
            outState.putString("ChequeNumber", chequeNumber);
            outState.putString("CollectionDate", collectionDate);
            outState.putString("ReleaseDate", releaseDate);
        }
        outState.putParcelableArrayList("SelectedProducts", selectedProductsArray);
        outState.putParcelableArrayList("ReturnProducts", returnProductArray);


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

    private String GetGPS() {

        String GPS = "";

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location == null)

            GPS = "0" + "-" + "0";
            //showGPSDisabledAlertToUser();

        else {

            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());

            String la = Double.toString(lat);
            String lo = Double.toString(lng);
            locationManager.removeUpdates(InvoiceGen3Activity.this);
            GPS = la + "-" + lo;

        }
        return GPS;
    }


    protected void updateShelfQuantityDB() {
        // TODO Auto-generated method stub
        ShelfQuantity shelfQuantity = new ShelfQuantity(this);
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new Date());
        ArrayList<SelectedProduct> mergeList = tempInvoController.getShelfQuantityTempList();
        shelfQuantityList.addAll(mergeList);
        shelfQuantity.openWritableDatabase();
        for (SelectedProduct shelfQuantityDetails : shelfQuantityList) {

            // shelfQuantity.insertShelfQuantity(invoiceNo, invoiceDate,
            // customerId, productId, batch, availableStock, timeStamp,
            // isUploaded)
            shelfQuantity.insertShelfQuantity(invoiceNumber, timeStamp,
                    pharmacyId, shelfQuantityDetails.getProductCode(),
                    shelfQuantityDetails.getProductBatch(),
                    String.valueOf(shelfQuantityDetails.getShelfQuantity()),
                    timeStamp, "false");


        }
        shelfQuantity.closeDatabase();
    }

    private void seperateShelfQuantity() {
        for (SelectedProduct selectedProduct : selectedProductsArray) {
            Log.w("selectedproductarraysize", selectedProductsArray.size() + "");
            if (selectedProduct.getNormal() != 0) {
                productList.add(selectedProduct);
            }
        }

        for (SelectedProduct selectedProduct : selectedProductsArray) {
            if (selectedProduct.getShelfQuantity() != 0) {
                shelfQuantityList.add(selectedProduct);
            }
        }
    }

    protected void updateItinerary() {
        // TODO Auto-generated method stub

        Itinerary itineraryObject = new Itinerary(this);

        itineraryObject.openReadableDatabase();
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd",
                Locale.US);
        String currentDate = sdfDateTime.format(new Date(System
                .currentTimeMillis()));
        List<String[]> result = itineraryObject
                .getAllItinerariesForADay(currentDate);
        itineraryObject.closeDatabase();

        int nxtItn = 0;
        for (int i = 0; i < result.size(); i++) {
            String[] temp = result.get(i);
            if (temp[0].contentEquals(rowId)) {
                nxtItn = i + 1;
            }
        }

        itineraryObject.openWritableDatabase();
        if (nxtItn < result.size()) {
            String[] temp = result.get(nxtItn);
            itineraryObject.setIsActiveTrue(temp[0]);
        }
        itineraryObject.closeDatabase();
        itineraryObject.openWritableDatabase();
        itineraryObject.setIsActiveFalse(rowId);
        itineraryObject.closeDatabase();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (flag) {
                finish();
                Intent itineraryListIntent = new Intent(
                        "com.halcyon.channelbridge.ITINERARYLIST");
                startActivity(itineraryListIntent);

            } else {
                Intent invoiceGen2ActivityIntent = new Intent(
                        getApplicationContext(), InvoiceGen2Activity.class);
                Bundle bundleToView = new Bundle();

                if (chequeEnabled) {
                    bundleToView.putString("ChequeNumber", chequeNumber);
                    bundleToView.putString("CollectionDate", collectionDate);
                    bundleToView.putString("ReleaseDate", releaseDate);
                }
                bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                bundleToView.putString("CreditDuration", creditDuration);
                bundleToView.putString("Id", rowId);
                bundleToView.putString("PharmacyId", pharmacyId);
                bundleToView.putString("Discount", discount);
                bundleToView.putString("Cheque", cheque);
                bundleToView.putString("Cash", cash);
                bundleToView.putString("startTime", startTime);
                bundleToView.putBoolean("isCheckEntered", isCheckEntered);
                bundleToView.putParcelableArrayList("SelectedProducts",
                        selectedProductsArray);
                bundleToView.putParcelableArrayList("ReturnProducts",
                        returnProductArray);
                bundleToView.putString("referenceNumber", referenceNumber);
                bundleToView.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
                bundleToView.putString("selectedInvoOption", selectedInvoOption);
                invoiceGen2ActivityIntent.putExtras(bundleToView);
                startActivity(invoiceGen2ActivityIntent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setInitialData() {
        // TODO Auto-generated method stub
        Itinerary itinerary = new Itinerary(this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(rowId);
        itinerary.closeDatabase();

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary
                    .getItineraryDetailsForTemporaryCustomer(rowId);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", "
                    + itnDetails[4] + ", " + itnDetails[5];

            custName = itnDetails[0];
            tViewCustomerName.setText(itnDetails[0]);
            tViewAddress.setText(address);
            custAddress = address;
        } else {
            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();
            String[] customerDetails = customersObject
                    .getCustomerDetailsByPharmacyId(pharmacyId);
            customersObject.closeDatabase();
            tViewCustomerName.setText(customerDetails[5]);
            custName = customerDetails[5];
            tViewAddress.setText(customerDetails[6]);
            custAddress = customerDetails[6];
            creditAmount = customerDetails[15];
            customerNumber = customerDetails[4];
        }
        // 0 - rowid
        // 1 - pharmacyId
        // 2 - pharmacyCode
        // 3 - dealerId
        // 4 - companyCode
        // 5 - customerName
        // 6 - address
        // 7 - area
        // 8 - town
        // 9 - district
        // 10 - telephone
        // 11 - fax
        // 12 - email
        // 13 - customerStatus
        // 14 - creditLimit
        // 16 - currentCredit
        // 16 - creditExpiryDate
        // 17 - creditDuration
        // 18 - vatNo
        // 19 - status

        // PAYMENT OPTIONS
        // C - cash
        // R - Credit
        // Q - cheque


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();

        paymentOption = "";
        if (!cash.isEmpty()) {
            paymentOption = paymentOption + "C";

        }
        if (!cheque.isEmpty()) {
            paymentOption = paymentOption + "Q";

        }

        if (!credit.isEmpty()) {
            paymentOption = paymentOption + "R";
        }
        if (cash.isEmpty()) {
            cash = "0";
        }
        if (credit.isEmpty()) {
            credit = "0";
        }
        if (cheque.isEmpty()) {
            cheque = "0";
        }


        tViewCredit.setText(String.valueOf(credit));
        tViewCash.setText(String.valueOf(cash));
        tViewCheque.setText(String.valueOf(cheque));
        tViewMarketReturns.setText(marketReturns);
        tViewDiscount.setText(discount);
        tViewNeedToPay.setText(needToPay);
        tViewTotalItems.setText(totalQuantity);
        tViewInvoiceNumber.setText(" " + invoiceNumber);
        tViewRemain.setText("");

        String systemDate = DateFormat.getDateInstance().format(new Date());
        tViewDate.setText(systemDate);
        tViewTotal.setText(String.format("%.2f", Double.parseDouble(totalPrice)));

    }

    private void getDataFromPreviousActivity(Bundle extras) {
        try {


            rowId = extras.getString("Id");
            pharmacyId = extras.getString("PharmacyId");
            cash = extras.getString("Cash");
            credit = extras.getString("Credit");
            cheque = extras.getString("Cheque");
            creditDuration = extras.getString("CreditDuration");
            onTimeOrNot = extras.getString("onTimeOrNot");
            selectedCreditPeriodIndex = extras.getInt("selectedCreditIndex");
            startTime = extras.getString("startTime");
            referenceNumber = extras.getString("referenceNumber");
            manualFreeEnable = extras.getBoolean("ManualFreeEnable");
            isCheckEntered = extras.getBoolean("isCheckEntered");

            totalPrice = extras.getString("TotalPrice");
            selectedInvoOption = extras.getString("selectedInvoOption");
            seletedPaymentOptionCode = extras.getString("seletedPaymentOptionCode");

            branchCode = extras.getString("branchCode");
            chequeimage = extras.getByteArray("chequeimage");
            dicountValue = extras.getString("dicountValue");
            //  totalQuantity = extras.getInt("totalQuantity");
            if (extras.containsKey("invoiceId")) {
                invoiceId = extras.getLong("invoiceId");
            }

            Log.w("CHECK VALUE $$$$$$$$$$$###############", cheque);

            Log.w("IG3",
                    "MarketReturns 332 " + extras.getString("MarketReturns"));

            marketReturns = extras.getString("MarketReturns");

            discount = extras.getString("Discount");
            Log.w("Discount--->", "" + discount);
            needToPay = extras.getString("NeedToPay");

            totalQuantity = extras.getString("TotalQuantity");
            invoiceNumber = extras.getString("InvoiceNumber");
            returnProductArray = extras
                    .getParcelableArrayList("ReturnProducts");
            selectedProductsArray = extras
                    .getParcelableArrayList("SelectedProducts");

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails",
                    true);

            if (chequeEnabled) {
                if (extras.containsKey("ChequeNumber")) {
                    chequeNumber = extras.getString("ChequeNumber");
                }
                if (extras.containsKey("CollectionDate")) {
                    collectionDate = extras.getString("CollectionDate");
                }
                if (extras.containsKey("ReleaseDate")) {
                    releaseDate = extras.getString("ReleaseDate");
                }
                if (extras.containsKey("BankName")) {
                    selectedBank = extras.getString("BankName");
                }
                if (extras.containsKey("Branch")) {
                    selectedBranch = extras.getString("Branch");
                }

                Log.w("cheque details", chequeNumber + " # " + collectionDate
                        + " # " + releaseDate);
                Log.i("bann--->", selectedBank);

            }

            Log.w("IG3", "totalPrice 332 " + totalPrice);

            Log.w("IG3", "Pharmacy id " + pharmacyId);
            Log.w("IG3", "rowId " + rowId);

        } catch (Exception e) {
            Log.w("InvoiceGen3: ", e.toString());
        }

    }

    private void updateInvoiceDb() {

        invoiceObject.openWritableDatabase();
///add susantha
        String gps1 = GetGPS();
        String[] gps = gps1.split("-");
        //  GetGPS() ;

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                .format(new Date(timeStampMillies));

        Log.w("IG3", "timeStamp " + timeStamp);

        invoiceId = invoiceObject.insertInvoice(rowId, paymentOption,
                totalPrice, cash, credit, cheque, marketReturns, discount,
                "false", timeStamp, "false", creditDuration, gps[0].toString(), gps[1].toString(), startTime, dicountValue, totalQuantity, pharmacyId);//devAJ
        invoiceObject.closeDatabase();
        Log.w("IG3", "invoiceId " + invoiceId);
        Log.w("CREDIT DURATION", "#####" + creditDuration + "#####");
        Log.w("lat", "#####" + gps[0].toString() + "#####");
        Log.w("lng", "#####" + gps[1].toString() + "#####");

    }

    private void updateInvoicedProductDb() {
        // TODO Auto-generated method stub
        InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
        invoicedProductsObject.openWritableDatabase();
        for (SelectedProduct selectedProduct : productList) {
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")
                    .format(new Date(timeStampMillies));
            invoicedProductsObject.insertInvoicedProducts(
                    String.valueOf(invoiceId),
                    selectedProduct.getProductCode(),
                    selectedProduct.getProductBatch(),
                    String.valueOf(selectedProduct.getRequestedQuantity()),
                    String.valueOf(selectedProduct.getFree()),
                    String.valueOf(selectedProduct.getDiscount()),
                    String.valueOf(selectedProduct.getNormal()), timeStamp,
                    String.valueOf(selectedProduct.getPrice()),
                    String.valueOf(selectedProduct.getFreeSystem()),
                    String.valueOf(selectedProduct.getExpiryDate()),
                    String.valueOf(selectedProduct.getPrechesPrice()),
                    String.valueOf(selectedProduct.getRetailPrice())
            );
        }
        invoicedProductsObject.closeDatabase();
    }

    private void updateRepsStore() {
        ProductRepStore productRepStoreObject = new ProductRepStore(this);

        for (SelectedProduct selectedProduct : productList) {
            productRepStoreObject.openReadableDatabase();
            productRepStoreObject.closeDatabase();
            productRepStoreObject.openWritableDatabase();
            productRepStoreObject.updateRepStoreData(String.valueOf(selectedProduct.getRowId()),
                    selectedProduct.getNormal() + selectedProduct.getFree());

            productRepStoreObject.closeDatabase();
        }
    }

    private void updateInvoicedCheque() {
        InvoicedCheque invoicedCheque = new InvoicedCheque(this);
        if ((!chequeNumber.isEmpty()) && (chequeNumber != "")) {
            Log.w("Cheque saving... ########", "Methanata awa");
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS")
                    .format(new Date(timeStampMillies));
            invoicedCheque.openWritableDatabase();
            invoicedCheque.insertInvoicedCheque(String.valueOf(invoiceId),
                    pharmacyId, chequeNumber, cheque, collectionDate,
                    releaseDate, timeStamp, "false");
            invoicedCheque.closeDatabase();
        }
    }

    private void upadateRemark(String pharmacyId){

        Remarks remarks = new Remarks(this);
        remarks.openWritableDatabase();
        remarks.updateRemarks(pharmacyId);
        remarks.closeDatabase();

    }

    private void populateinvoices(ArrayList<SelectedProduct> productsArray) {
        // TODO Auto-generated method stub

        Log.w("IG3 populatetable", "inside");
        Log.w("IG3 populatetable",
                "productsArray.size() : " + productsArray.size());
        TableRow tr;
        tblInvoice.setShrinkAllColumns(true);

        try {

            int count = 1;
            for (SelectedProduct selectedProduct : productsArray) {
                Log.w("called", "inside populate for");

                tr = new TableRow(this);
                tr.setId(1000 + count);
                tr.setPadding(4, 4, 4, 4);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);

                }

                String proDescription = null;
                if (selectedProduct.getProductDescription() == null || selectedProduct.getProductDescription().equals("")) {
                    Products pr = new Products(InvoiceGen3Activity.this);
                    pr.openReadableDatabase();
                    proDescription = pr.getProductDis(selectedProduct.getProductCode(), selectedProduct.getProductBatch());
                } else {
                    proDescription = selectedProduct.getProductDescription();
                }


                TextView tvProductDescription = new TextView(this);
                tvProductDescription.setId(200 + count);
                tvProductDescription.setText(proDescription);
                tvProductDescription.setGravity(Gravity.LEFT);
                tvProductDescription.setPadding(3, 3, 3, 3);
                tvProductDescription.setTextColor(Color.WHITE);
                tr.addView(tvProductDescription);

                System.out.println(" Productc getProductDescription :" + selectedProduct.getProductDescription());
                System.out.println(" Productc getProductCode :" + selectedProduct.getProductCode());

                TextView tvPrice = new TextView(this);

                tvPrice.setId(200 + count);
                tvPrice.setText(String.valueOf(selectedProduct.getPrice()));
                tvPrice.setGravity(Gravity.LEFT);
                tvPrice.setPadding(3, 3, 3, 3);
                tvPrice.setTextColor(Color.WHITE);
                tr.addView(tvPrice);

                TextView tvExp = new TextView(this);
                tvExp.setId(200 + count);
                tvExp.setText(String.valueOf(selectedProduct.getExpiryDate().substring(0, 10)));
                tvExp.setGravity(Gravity.LEFT);
                tvExp.setPadding(3, 3, 3, 3);
                tvExp.setTextColor(Color.WHITE);
                tr.addView(tvExp);

                TextView tvNormal = new TextView(this);
                tvNormal.setId(200 + count);
                tvNormal.setText(String.valueOf(selectedProduct.getNormal()));
                tvNormal.setGravity(Gravity.LEFT);
                tvNormal.setPadding(3, 3, 3, 3);
                tvNormal.setTextColor(Color.WHITE);
                tr.addView(tvNormal);

                TextView tvFree = new TextView(this);
                tvFree.setId(200 + count);
                tvFree.setText(String.valueOf(selectedProduct.getFree()));
                tvFree.setGravity(Gravity.LEFT);
                tvFree.setPadding(3, 3, 3, 3);
                tvFree.setTextColor(Color.WHITE);
                tr.addView(tvFree);

                TextView tvDiscount = new TextView(this);
                tvDiscount.setId(200 + count);
                tvDiscount
                        .setText(String.valueOf(selectedProduct.getDiscount()));
                tvDiscount.setGravity(Gravity.LEFT);
                tvDiscount.setPadding(3, 3, 3, 3);
                tvDiscount.setTextColor(Color.WHITE);
                tr.addView(tvDiscount);

                TextView tvQuantity = new TextView(this);
                tvQuantity.setId(200 + count);
                tvQuantity.setText(String.valueOf(selectedProduct.getNormal()
                        + selectedProduct.getFree()));
                tvQuantity.setGravity(Gravity.LEFT);
                tvQuantity.setPadding(3, 3, 3, 3);
                tvQuantity.setTextColor(Color.WHITE);
                tr.addView(tvQuantity);

                TextView tvTotal = new TextView(this);
                tvTotal.setId(200 + count);
                double tempPrice = Double.parseDouble(String
                        .valueOf(selectedProduct.getNormal()
                                * selectedProduct.getPrice()))
                        * ((100 - selectedProduct.getDiscount()) / 100);
                tvTotal.setText(String.format("%.2f", tempPrice));
                tvTotal.setGravity(Gravity.LEFT);
                tvTotal.setPadding(3, 3, 3, 3);
                tvTotal.setTextColor(Color.WHITE);
                tr.addView(tvTotal);

                count++;

                tblInvoice.addView(tr, new TableLayout.LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
        } catch (Exception e) {
            Log.w("pop Table error", e.toString());
        }
    }

    public void saveReturns() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(new Date());

        ProductReturns productReturns = new ProductReturns(this);
        if (!returnProductArray.isEmpty()) {
            for (ReturnProduct returnProduct : returnProductArray) {
                productReturns.openWritableDatabase();

                Log.w("known Validate",
                        "################ :"
                                + returnProduct.getReturnValidated() + "");

                // productReturns.insertProductReturn(productCode, batchNo,
                // invoiceNo, issueMode, normal, free, returnDate, customerNo,
                // uploadedStatus, unitPrice, discount, returnInvoice)

                long ret = productReturns.insertProductReturn(
                        returnProduct.getProductId(), returnProduct.getBatch(),
                        String.valueOf(returnProduct.getInvoiceNumber()),
                        returnProduct.getIssueMode(),
                        String.valueOf(returnProduct.getQuantity()),
                        String.valueOf(returnProduct.getFree()), timeStamp,
                        pharmacyId, "false",
                        String.valueOf(returnProduct.getUnitPrice()),
                        String.valueOf(returnProduct.getDiscount()),
                        String.valueOf(invoiceId),
                        returnProduct.getReturnValidated(), Double.toString(lat), Double.toString(lng), onTimeOrNot);
                productReturns.closeDatabase();

                String a = returnProduct.getpPrice();
                String b = returnProduct.getRetailPrice();
                String c = returnProduct.getBatch();
                String ad = String.valueOf(returnProduct.getUnitPrice());
                String f = returnProduct.getExDate();
                String fd = returnProduct.getProductId();


                if (returnProduct.getIssueMode().contentEquals("SF") || returnProduct.getIssueMode().contentEquals("SR")) {

                    if (checkBatch(returnProduct.getProductId(), returnProduct.getBatch())) {
                        ProductRepStore productRepStoreObject = new ProductRepStore(InvoiceGen3Activity.this);
                        productRepStoreObject.openWritableDatabase();
                        productRepStoreObject.updateProductRepStoreReturns(returnProduct.getBatch(), String.valueOf(returnProduct.getQuantity() + returnProduct.getFree()));
                        productRepStoreObject.closeDatabase();
                        Log.w("known batch", returnProduct.getBatch() + "");
                    } else {
                        String expiryDate = "2015-01-01 00:00:00.000";
                        ProductRepStore productRepStore = new ProductRepStore(InvoiceGen3Activity.this);
                        productRepStore.openReadableDatabase();
                        long r = productRepStore.insertProductRepStore("0",
                                returnProduct.getProductId(),
                                returnProduct.getBatch(),
                                String.valueOf(returnProduct.getQuantity() + returnProduct.getFree()), expiryDate, "0", "0", "0",
                                timeStamp);
                        productRepStore.closeDatabase();

                    }
                }

                Log.w("returned row id", ret + "");
            }
        }
    }

    private boolean checkBatch(String pCode, String b) {
        boolean flag = false;
        ProductRepStore productRepStore = new ProductRepStore(this);
        productRepStore.openReadableDatabase();
        ArrayList<String> checkBatches = productRepStore.getBatchesByProductCode(pCode);
        productRepStore.closeDatabase();
        Log.w("check Batch size", checkBatches.size() + "");
        for (String batch : checkBatches) {
            if (batch.contentEquals(b)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    //sk  need to remove printFunction


    /**
     * new add print function
     *
     * @param custName
     * @param address
     * @param repName
     * @param invoiceId add new invoice id
     */
    protected void printFunction(String custName, String address, String repName, String invoiceId) {
        // TODO Auto-generated method stub
        try {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean prePrintInvoiceFormatEnabled = sharedPreferences.getBoolean("cbPrefPrePrintInvoice", true);
            String repId = sharedPreferences.getString("RepId", "-1");

            if (prePrintInvoiceFormatEnabled) {

                int count = 48;
                int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();

                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                List<String[]> invoicedProductsPrintDetails = new ArrayList<String[]>();

                Customers cus = new Customers(InvoiceGen3Activity.this);
                cus.openReadableDatabase();
                String phone = cus.getCustomerPhoneByPharmacyId(pharmacyId);
                if (phone.equals("0")) {
                    CustomersPendingApproval pen = new CustomersPendingApproval(InvoiceGen3Activity.this);
                    pen.openReadableDatabase();
                    phone = pen.getCustomerPhoneByPharmacyId(pharmacyId);

                } else {

                }

                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(InvoiceGen3Activity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns.getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();

                    InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
                    invoicedProductsObject.openReadableDatabase();
                    invoicedProductsPrintDetails = invoicedProductsObject.getInvoicedProductsByInvoiceId(invoiceId);
                    invoicedProductsObject.closeDatabase();
                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps.getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();


                String add[] = dealerCity.split("@");
                String addres[] = add[0].split(",");

                String telfax = add[1];
                String delerefax = null;
                String delereContactDetails[] = telfax.split("$");
                String delereNumber = delereContactDetails[0].trim();

                try {
                    delerefax = delereContactDetails[1].trim();
                } catch (Exception ex) {
                    delereNumber = delereNumber.substring(0, (delereNumber.length() - 1));
                    delerefax = "";
                }


                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT
                String teram = invoiceD.get(2);
                if (teram.equals("CQR")) {
                    teram = "Cheque";
                } else {
                    teram = "Cash";
                }

                Log.w("IG3", "invoiceD.get(11) 332 " + invoiceD.get(11));


                String date = invoiceD.get(11).substring(0, 10);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;


                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }


                String repTelNo = "";
                for (int i = 0; i <= 6; i++) {
                    repTelNo = repTelNo + " ";
                }
                repTelNo = repTelNo + dealerTel;

                int custnameLength = custName.length();
                int addressLength = address.length();
                String cusMargin = "";
                String addressMargin = "";
                String nameMargin = "";

                String delerAdrresMarginone = "";
                String delerAdrresMargintwo = "";

                String delerPhoneMargin = "";
                String delerFaxMargin = "";

                String finalAddress = addres[1].trim() + "," + addres[2].trim();

                for (int i = 0; i < 75 - delereNumber.length(); i++) {
                    delerPhoneMargin = delerPhoneMargin + " ";
                }


                for (int i = 0; i < 75 - finalAddress.length(); i++) {
                    delerAdrresMargintwo = delerAdrresMargintwo + " ";
                }

                for (int i = 0; i < 75 - addres[0].length(); i++) {
                    delerAdrresMarginone = delerAdrresMarginone + " ";
                }

                for (int i = 0; i < 75 - dealerName.length(); i++) {
                    nameMargin = nameMargin + " ";
                }

                for (int i = 0; i < 60 - custnameLength; i++) {
                    cusMargin = cusMargin + " ";
                }
                for (int i = 0; i < 60 - addressLength; i++) {
                    addressMargin = addressMargin + " ";
                }

                if (invoiceId.length() == 1) {
                    invoiceId = "00" + invoiceId;
                } else if (invoiceId.length() == 2) {
                    invoiceId = "0" + invoiceId;
                } else {
                    invoiceId = invoiceId;
                }


                String po = "";

                String headerData = "\n";
                headerData = headerData + "                                      INVOICE \n";
                headerData = headerData + nameMargin + dealerName + "\n";
                headerData = headerData + delerAdrresMarginone + addres[0] + "\n";
                headerData = headerData + delerAdrresMargintwo + finalAddress + "\n";
                headerData = headerData + delerPhoneMargin + delereNumber + "\n";
                headerData = headerData + delerFaxMargin + delerefax + "\n";
                headerData = headerData + "CUSTOMER ADDRESS                                            Invoice No:" + invoiceId + "\n";
                headerData = headerData + custName + cusMargin + "Date:" + date + "\n";
                headerData = headerData + address + addressMargin + "P.O     :" + po + "\n";
                headerData = headerData + "TEL : " + phone + "\n";
                headerData = headerData + "                                                            Tearm  :" + teram + "\n";
                headerData = headerData + "\n";
                headerData = headerData + "No Description               Batch  Expiry R.Pri W/Pric  Qty Fre Dis  Ammount  \n";
                headerData = headerData + "--------------------------------------------------------------------------------";
                headerData = headerData + "\n";

                String printData = "";

                int totalQty = 0;
                double totalProdsValue = 0;


                List<String[]> freeProducts = new ArrayList<String[]>();
                int itemcount = 1;
                for (String[] invoicedProduct : invoicedProductsPrintDetails) {

                    if (count == 48) {
                        if (printData.length() > 1) {

                            int k = spaceCount;

                            for (int i = 0; i <= k; i++) {
                                printData = printData + "\n";
                            }

                        }

                        printData = printData + headerData;
                        count = 0;
                    }

                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products.getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[8];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT


                    String expiry = invoicedProduct[11];


                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice = invoicedProduct[9];// IMPORTANT
                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT
                    String retailsPrice = productdetail[14];

                    if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double
                            .parseDouble(unitPrice))
                            * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;

                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);
                    String unitPriceString = String.valueOf(unitPrice);
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;

                    productDescription = productDescription.trim();
                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();


                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }

                    if (productDescription.length() > 44) {
                        productDescription = productDescription.substring(0, 44);
                    }

                    if (quantityString.length() < 7) {
                        quantityRemain = 6 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 9) {
                        unitPriceRemain = 8 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 11) {
                        valueRemain = 10 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    //Himanshu

                    String itemno;
                    if (String.valueOf(itemcount).length() == 1) {
                        itemno = "0" + String.valueOf(itemcount);
                    } else {
                        itemno = String.valueOf(itemcount);
                    }

                    if (batch.length() == 1) {
                        batch = batch.substring(0, 1) + "     ";
                    } else if (batch.length() == 2) {
                        batch = batch.substring(0, 2) + "    ";
                    } else if (batch.length() == 3) {
                        batch = batch.substring(0, 3) + "   ";
                    } else if (batch.length() == 4) {
                        batch = batch.substring(0, 4) + "  ";
                    } else if (batch.length() == 5) {
                        batch = batch.substring(0, 5) + " ";
                    } else {
                        batch = batch.substring(0, 6);
                    }

                    if (productDescription.length() == 26) {

                    } else if (productDescription.length() > 26) {
                        productDescription = productDescription.substring(0, 26);
                    } else {
                        int prodetails = 26 - productDescription.length();
                        String prodetailsspace = "";
                        for (int i = 0; i < prodetails; i++) {
                            prodetailsspace = prodetailsspace + " ";
                        }
                        productDescription = productDescription + prodetailsspace;
                    }

                    String quantity;
                    if (String.valueOf(qty).length() == 1) {
                        quantity = "00" + String.valueOf(qty);
                    } else if (String.valueOf(qty).length() == 2) {
                        quantity = "0" + String.valueOf(qty);
                    } else {
                        quantity = String.valueOf(qty);
                    }


                    String newRetailprice = retailsPrice.trim();
                    if (newRetailprice.length() == 1) {
                        newRetailprice = "      " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 2) {
                        newRetailprice = "     " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 3) {
                        newRetailprice = "    " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 4) {
                        newRetailprice = "   " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 5) {
                        newRetailprice = "  " + newRetailprice;
                    } else if (retailsPrice.trim().length() == 6) {
                        newRetailprice = " " + newRetailprice;
                    } else {
                        newRetailprice = newRetailprice.substring(0, 7);
                    }

                    String newunitprice = unitPriceString.trim();
                    if (newunitprice.length() == 1) {
                        newunitprice = "      " + newunitprice;
                    } else if (newunitprice.trim().length() == 2) {
                        newunitprice = "     " + newunitprice;
                    } else if (newunitprice.trim().length() == 3) {
                        newunitprice = "    " + newunitprice;
                    } else if (newunitprice.trim().length() == 4) {
                        newunitprice = "   " + newunitprice;
                    } else if (newunitprice.trim().length() == 5) {
                        newunitprice = "  " + newunitprice;
                    } else if (newunitprice.trim().length() == 6) {
                        newunitprice = " " + newunitprice;
                    } else {
                        newunitprice = newunitprice.substring(0, 7);
                    }

                    if (free.length() == 1) {
                        free = "  " + free;
                    } else if (free.length() == 2) {
                        free = " " + free;
                    } else if (free.length() == 3) {
                        free = free;
                    }


                    // printData = printData + itemno + " " + productDescription + " " + batch + " " + expiry.substring(2, 10) + " " + newRetailprice+ " " + newunitprice + " " + quantity + " "+free+" "+ discount + " " + valueString.trim() + "" + "\n";
                    printData = printData + itemno + " " + productDescription + "             " + newRetailprice + " " + newunitprice + " " + quantity + " " + free + " " + discount + "" + valueString + "" + "\n";
                    itemcount++;
                    count = count + 2;


                }
                double discountedPrice = (Float.parseFloat(invoiceValue) / 100)
                        * Double.parseDouble(invoiceD.get(8));


                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));


                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 10;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 45) {
                    printData = printData + "\n";
                    printData = printData + "--------------------------------------------------------------------------------\n";
                    printData = printData + "Total                                                " + totalQt + "          " + invoiceVal.trim() + "\n";

                    count = count + 3;
                } else {

                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + headerData;
                    count = 0;

                    printData = printData + "\n";
                    printData = printData + "--------------------------------------------------------------------------------\n";
                    printData = printData + "Total                                                " + totalQt + "          " + invoiceVal.trim() + "\n";


                    count = count + 3;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 45) {

                        printData = printData + "\n Returned Products\n";
                        printData = printData + "--------------------------------------------------------------------------------\n";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 48 - count;
                        k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + headerData;
                        count = 0;

                        printData = printData + "\n Returned Products\n";
                        printData = printData + "--------------------------------------------------------------------------------\n";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 48) {
                            if (printData.length() > 1) {

                                int k = spaceCount;

                                for (int i = 0; i <= k; i++) {
                                    printData = printData + "\n";
                                }
                            }

                            printData = printData + headerData;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 7) {
                            quantityReturnRemain = 6 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }

                        printData = printData + selectedProduct[9] + "\n";
                        printData = printData + "              " + (quantityReturnString) + " " + priceReturnString + " " + (valueReturnString) + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                String craditduration = invoiceD.get(13);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance(); // Get Calendar Instance
                c.setTime(sdf.parse(date));
                c.add(Calendar.DATE, Integer.parseInt(craditduration));  // add 45 days
                sdf = new SimpleDateFormat("dd/MM/yyyy");

                Date resultdate = new Date(c.getTimeInMillis());   // Get new time
                String calculateDuration = sdf.format(resultdate);
                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                if ((count + 17) < 48) {

                    footerData = footerData + "--------------------------------------------------------------------------------\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF " + dealerName + " \n";
                    footerData = footerData + "Short dated products must be returned before 4 months of expiary date\n";
                    footerData = footerData + "THIS INVOICE IS DUE FOR SETTLEMENT ON OR BEFORE : " + calculateDuration + " \n\n";
                    footerData = footerData + "                                                      Received Correct Products\n";
                    footerData = footerData + "                                                           & Quantities\n\n";
                    footerData = footerData + "                                                      --------------------------\n";
                    footerData = footerData + "Printed by :" + getRepName() + " Tel : " + repTelNo.trim() + "\n";
                    footerData = footerData + "Software Provided By Mobitel (Pvt) Ltd - +94(0)712755777.\n";

                    printData = printData + footerData;

                } else {

                    footerData = footerData + "--------------------------------------------------------------------------------\n";
                    footerData = footerData + "\n";
                    footerData = footerData + "PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF " + dealerName + " \n";
                    footerData = footerData + "Short dated products must be returned befor 4 months of expiary date\n";
                    footerData = footerData + "THIS INVOICE IS DUE FOR SETTLEMENT ON OR BEFORE : " + calculateDuration + " \n\n";
                    footerData = footerData + "                                                      Received Correct Products\n";
                    footerData = footerData + "                                                           & Quantities\n\n";
                    footerData = footerData + "                                                      --------------------------\n";
                    footerData = footerData + "Printed by :" + getRepName() + " Tel : " + repTelNo.trim() + "\n";
                    footerData = footerData + "Software Provided By Mobitel (Pvt) Ltd - +94(0)712755777.\n";


                    int k = 48 - count;
                    k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + headerData;
                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(), PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            } else {


                // boolean flag = true;
                int count = 14;
//				int spaceCount = 8;

                Invoice invoice = new Invoice(this);
                invoice.openReadableDatabase();
                ArrayList<String> invoiceD = invoice
                        .getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();

                ArrayList<String[]> returnedProductList = new ArrayList<String[]>();
                List<String[]> invoicedProductsPrintDetails = new ArrayList<String[]>();
                if (invoiceId != "") {

                    ProductReturns productReturns = new ProductReturns(
                            InvoiceGen3Activity.this);
                    productReturns.openReadableDatabase();
                    returnedProductList = productReturns
                            .getReturnDetailsByInvoiceId(invoiceId);
                    productReturns.closeDatabase();

                    InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
                    invoicedProductsObject.openReadableDatabase();
                    invoicedProductsPrintDetails = invoicedProductsObject
                            .getInvoicedProductsByInvoiceId(invoiceId);
                    invoicedProductsObject.closeDatabase();


                }

                Reps reps = new Reps(this);
                reps.openReadableDatabase();
                ArrayList<String> delearDetails = reps
                        .getRepDetailsForPrinting(repId);
                reps.closeDatabase();

                String dealerName = delearDetails.get(1).trim();
                String dealerCity = delearDetails.get(2).trim();
                String dealerTel = delearDetails.get(3).trim();

                if (dealerName.length() > 18) {
                    dealerName = dealerName.substring(0, 18);
                }

                if (dealerCity.length() > 18) {
                    dealerCity = dealerCity.substring(0, 18);
                }

                String invoiceValue = invoiceD.get(3);// IMPORTANT
                String returns = invoiceD.get(7);// IMPORTANT

                Log.w("IG3", "invoiceD.get(11) 332 " + invoiceD.get(11));

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");

                // Date dateObj = dateFormat.parse(invoiceD.get(11));

                // String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                String date = invoiceD.get(11).substring(0, 10);
                // String time =new SimpleDateFormat("hh:mm:ss a").format(dateObj);
                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")
                        .format(new Date());

                int customerNameRemain = 0;
                int addressRemain = 0;

                if (custName.length() > 24) {
                    custName = custName.substring(0, 25);
                } else {
                    customerNameRemain = 25 - custName.length();
                }
                customerNameRemain = customerNameRemain + 1;
                for (int i = 0; i <= customerNameRemain; i++) {
                    custName = custName + " ";
                }

                if (address.length() > 24) {
                    address = address.substring(0, 25);
                } else {
                    addressRemain = 25 - address.length();
                }
                addressRemain = addressRemain + 1;
                for (int i = 0; i <= addressRemain; i++) {
                    address = address + " ";
                }

                String headerData = "";
                headerData = headerData + dealerName + "\n";
                headerData = headerData + dealerCity + "\n";
                headerData = headerData + "Tel: " + dealerTel + "\n";
                headerData = headerData + "\n\n";


                headerData = headerData + "Invoice To\n";
                headerData = headerData + custName + "Invoice No: " + invoiceId + "\n";
                headerData = headerData + address + "Date :" + date + "\n";
                headerData = headerData + "\n\n";

                String printData = "";
                int totalQty = 0;
                double totalProdsValue = 0;

                int invoicePageCount = 1;

                printData = printData + headerData;

                printData = printData + "Description       Qty     Price       Value\n";
                printData = printData + "--------------------------------------------";
                printData = printData + "\n";

                List<String[]> freeProducts = new ArrayList<String[]>();

                for (String[] invoicedProduct : invoicedProductsPrintDetails) {

                    if (count == 60) {

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;
                    }

                    Products products = new Products(this);
                    products.openReadableDatabase();
                    String[] productdetail = products
                            .getProductDetailsById(invoicedProduct[2]);
                    products.closeDatabase();

                    String productCode = productdetail[2];// IMPORTANT
                    String productDescription = productdetail[8];// IMPORTANT
                    String batch = invoicedProduct[3];// IMPORTANT


                    String expiry = invoicedProduct[11];


                    String discount = invoicedProduct[6];// IMPORTANT
                    String unitPrice = invoicedProduct[9];// IMPORTANT
                    String normal = invoicedProduct[7];// IMPORTANT
                    String free = invoicedProduct[5];// IMPORTANT

                    if (free != "" && Integer.parseInt(free) > 0) {
                        freeProducts.add(invoicedProduct);
                    }

                    totalQty = totalQty + Integer.parseInt(normal);// IMPORTANT

                    int qty = Integer.parseInt(normal);// IMPORTANT

                    double prodValue = (Integer.parseInt(normal) * Double
                            .parseDouble(unitPrice))
                            * ((100 - Double.parseDouble(discount)) / 100);

                    totalProdsValue = totalProdsValue + prodValue;

                    String value = (String.format("%.2f", prodValue));
                    String quantityString = String.valueOf(qty);
                    String unitPriceString = String.valueOf(unitPrice);
                    String valueString = String.valueOf(value);

                    int quantityRemain = 0;
                    int unitPriceRemain = 0;
                    int valueRemain = 0;

                    productDescription = productDescription.trim();
                    quantityString = quantityString.trim();
                    unitPriceString = unitPriceString.trim();
                    valueString = valueString.trim();

                    Log.w("SIZE",
                            "quantityString size : " + quantityString.length());
                    Log.w("SIZE",
                            "unitPriceString size : " + unitPriceString.length());
                    Log.w("SIZE", "valueString size : " + valueString.length());

                    if (quantityString.length() > 7) {
                        quantityString = quantityString.substring(0, 7);
                    }
                    if (unitPriceString.length() > 9) {
                        unitPriceString = unitPriceString.substring(0, 9);
                    }
                    if (valueString.length() > 11) {
                        valueString = valueString.substring(0, 11);
                    }

                    if (productDescription.length() > 44) {
                        productDescription = productDescription.substring(0, 44);
                    }

                    if (quantityString.length() < 7) {
                        quantityRemain = 6 - quantityString.length();
                    }

                    for (int i = 0; i <= quantityRemain; i++) {
                        quantityString = " " + quantityString;
                    }

                    if (unitPriceString.length() < 9) {
                        unitPriceRemain = 8 - unitPriceString.length();
                    }

                    for (int i = 0; i <= unitPriceRemain; i++) {
                        unitPriceString = " " + unitPriceString;
                    }

                    if (valueString.length() < 11) {
                        valueRemain = 10 - valueString.length();
                    }

                    for (int i = 0; i <= valueRemain; i++) {
                        valueString = " " + valueString;
                    }

                    printData = printData + productDescription + "\n";
                    printData = printData + "              " + quantityString + " "
                            + unitPriceString + " " + valueString + "\n";

                    count = count + 2;
                    Log.w("COUNT", count + "lines");

                }

                // Log.w("IG3", "discount 332 " +
                // Integer.parseInt(invoiceD.get(8)));

                double discountedPrice = (Float.parseFloat(invoiceValue) / 100)
                        * Double.parseDouble(invoiceD.get(8));

                Log.w("IG3", "discountedPrice 332 " + discountedPrice);

                double totalDiscountedValue = (discountedPrice + Float
                        .parseFloat(returns));

                Log.w("IG3", "totalDiscountedValue 332 " + totalDiscountedValue);

                Log.w("IG3", "total 332 " + count);

                double needToPay = Float.parseFloat(invoiceValue)
                        - totalDiscountedValue;
                String needToPayString = String.format("%.2f", needToPay);

                Log.w("IG3", "needToPay 332 " + needToPay);

                String totalQt = String.valueOf(totalQty);
                String invoiceVal = String.format("%.2f", totalProdsValue);

                if (invoiceVal.length() > 9) {
                    invoiceVal = invoiceVal.substring(0, 9);
                }

                int invoiceValRemainRemain = 0;
                if (invoiceVal.length() < 11) {
                    invoiceValRemainRemain = 11 - invoiceVal.length();
                }

                int quantityRemain = 0;

                if (totalQt.length() < 7) {
                    quantityRemain = 6 - totalQt.length();
                }

                for (int i = 0; i <= quantityRemain; i++) {
                    totalQt = " " + totalQt;
                }

                invoiceValRemainRemain = invoiceValRemainRemain + 10;

                for (int i = 0; i <= invoiceValRemainRemain; i++) {
                    invoiceVal = " " + invoiceVal;
                }

                if (count < 57) {
                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total        " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";

                    count = count + 3;
                } else {

                    int k = 60 - count;
                    //k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                    invoicePageCount++;
                    count = 0;


                    printData = printData
                            + "--------------------------------------------\n";
                    printData = printData + "Total        " + " " + totalQt
                            + invoiceVal + "\n";
                    printData = printData
                            + "--------------------------------------------\n";
                    count = count + 3;
                }

                if (returnedProductList.size() > 0) {

                    if (count < 57) {

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 60 - count;
                        //k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData + "\n Returned Products\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] selectedProduct : returnedProductList) {

                        if (count == 60) {
                            printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        int quantityReturnRemain = 0;
                        int priceReturnRemain = 0;
                        int valueReturnRemain = 0;

                        int normal = 0, free = 0;
                        if (selectedProduct[4] != "") {
                            normal = Integer.parseInt(selectedProduct[4]);
                        }

                        if (selectedProduct[5] != "") {
                            free = Integer.parseInt(selectedProduct[5]);
                        }

                        double price = 0;
                        if (selectedProduct[8] != "") {
                            price = Double.parseDouble(selectedProduct[8]);
                        }

                        double discountVal = 0;
                        if (selectedProduct[10] != "") {
                            discountVal = Double.parseDouble(selectedProduct[10]);
                        }

                        String quantityReturnString = String.valueOf(normal + free);
                        String priceReturnString = String.valueOf(price);

                        double prodDiscountValue = 0;
                        if (discountVal > 0) {
                            prodDiscountValue = (normal * price) / 100
                                    * discountVal;
                        }

                        String valueReturnString = String.format("%.2f",
                                (normal * price) - prodDiscountValue);

                        if (quantityReturnString.length() < 7) {
                            quantityReturnRemain = 6 - quantityReturnString
                                    .length();
                        }
                        if (priceReturnString.length() < 9) {
                            priceReturnRemain = 8 - priceReturnString.length();
                        }
                        if (valueReturnString.length() < 10) {
                            valueReturnRemain = 10 - valueReturnString.length();
                        }

                        for (int i = 0; i <= quantityReturnRemain; i++) {
                            quantityReturnString = " " + quantityReturnString;
                        }
                        for (int i = 0; i <= priceReturnRemain; i++) {
                            priceReturnString = " " + priceReturnString;
                        }
                        for (int i = 0; i <= valueReturnRemain; i++) {
                            valueReturnString = " " + valueReturnString;
                        }

                        printData = printData + selectedProduct[9] + "\n";
                        printData = printData + "              "
                                + (quantityReturnString) + " " + priceReturnString
                                + " " + (valueReturnString) + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                if (freeProducts.size() > 0) {

                    if (count < 57) {

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    } else {

                        int k = 60 - count;
//						k = k + spaceCount;

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                        invoicePageCount++;
                        count = 0;

                        printData = printData
                                + "\n Free Issues or Special Discount\n";

                        printData = printData
                                + "--------------------------------------------";
                        printData = printData + "\n";

                        count = count + 3;

                    }

                    for (String[] invoicedProduct : freeProducts) {

                        if (count == 60) {
                            printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                            invoicePageCount++;
                            count = 0;
                        }

                        Products products = new Products(this);
                        products.openReadableDatabase();
                        String[] productdetail = products
                                .getProductDetailsById(invoicedProduct[2]);
                        products.closeDatabase();

                        String productCode = productdetail[2];// IMPORTANT
                        String productDescription = productdetail[8];// IMPORTANT
                        String batch = invoicedProduct[3];// IMPORTANT


                        String expiry = invoicedProduct[11];

                        String discount = invoicedProduct[6];// IMPORTANT
                        String unitPrice = invoicedProduct[9];// IMPORTANT
                        String normal = invoicedProduct[7];// IMPORTANT
                        String free = invoicedProduct[5];// IMPORTANT

                        int qty = Integer.parseInt(free);// IMPORTANT

                        String quantityString = String.valueOf(qty);
                        String unitPriceString = "0";
                        String valueString = "0";

                        int qtyRemain = 0;
                        int unitPriceRemain = 0;
                        int valueRemain = 0;

                        productDescription = productDescription.trim();
                        quantityString = quantityString.trim();
                        unitPriceString = unitPriceString.trim();
                        valueString = valueString.trim();

                        Log.w("SIZE",
                                "quantityString size : " + quantityString.length());
                        Log.w("SIZE",
                                "unitPriceString size : "
                                        + unitPriceString.length());
                        Log.w("SIZE", "valueString size : " + valueString.length());

                        if (quantityString.length() > 7) {
                            quantityString = quantityString.substring(0, 7);
                        }
                        if (unitPriceString.length() > 9) {
                            unitPriceString = unitPriceString.substring(0, 9);
                        }
                        if (valueString.length() > 11) {
                            valueString = valueString.substring(0, 11);
                        }

                        if (productDescription.length() > 44) {
                            productDescription = productDescription
                                    .substring(0, 44);
                        }

                        if (quantityString.length() < 7) {
                            qtyRemain = 6 - quantityString.length();
                        }

                        for (int i = 0; i <= qtyRemain; i++) {
                            quantityString = " " + quantityString;
                        }

                        if (unitPriceString.length() < 9) {
                            unitPriceRemain = 8 - unitPriceString.length();
                        }

                        for (int i = 0; i <= unitPriceRemain; i++) {
                            unitPriceString = " " + unitPriceString;
                        }

                        if (valueString.length() < 11) {
                            valueRemain = 10 - valueString.length();
                        }

                        for (int i = 0; i <= valueRemain; i++) {
                            valueString = " " + valueString;
                        }

                        printData = printData + productDescription + "\n";
                        printData = printData + "              " + quantityString
                                + " " + unitPriceString + " " + valueString + "\n";

                        count = count + 2;
                        Log.w("COUNT", count + "lines");

                    }

                    printData = printData
                            + "--------------------------------------------";
                    printData = printData + "\n";
                    count++;

                }

                printData = printData + "\n";
                count++;

                String footerData = "";

                double discountValue = 0;

                if (invoiceD.get(8) != ""
                        && Double.parseDouble(invoiceD.get(8)) > 0) {
                    double invoiceTotalVal = Double.parseDouble(invoiceD.get(3));
                    double invoiceDiscount = Double.parseDouble(invoiceD.get(8));
                    discountValue = (invoiceTotalVal / 100) * invoiceDiscount;
                }

                if ((count + 17) < 60) {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                    footerData = footerData + "Discount    : " + invoiceD.get(8)
                            + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";


                    printData = printData + footerData;

                } else {

                    footerData = footerData + "Gross Value : " + invoiceD.get(3)
                            + "\n";
                    footerData = footerData + "Discount    : " + invoiceD.get(8)
                            + "%  (" + String.format("%.2f", discountValue) + ")\n";
                    footerData = footerData + "Return Value: " + returns + "\n";
                    footerData = footerData + "Free/Special: 0\n";
                    footerData = footerData + "Need to pay : " + needToPayString
                            + "\n";

                    footerData = footerData + "\n\n";
                    footerData = footerData + "-----------------------------\n";
                    footerData = footerData + "  Customer Signature & Seal\n\n";

                    footerData = footerData + "Print Date & Time : "
                            + printDateTime + "\n\n";

                    footerData = footerData
                            + "Software By eMerge Solutions - 0115 960 960\n";

                    int k = 60 - count;
//					k = k + spaceCount;

                    for (int i = 0; i <= k; i++) {
                        printData = printData + "\n";
                    }

                    printData = printData + "\nPage " + invoicePageCount + "\n\n\n\n\n";
                    invoicePageCount++;


//					printData = printData + headerData;
                    printData = printData + footerData;

                }

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                // Print invoice

                Intent activityIntent = new Intent(getApplicationContext(),
                        PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getRepName() {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        String repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();
        return repName;
    }

    private boolean hasCustomerBoughtProduct(String pharmacyId, String productId) {
        boolean hasCustomerPurchased = false;
        CustomerProduct customerProduct = new CustomerProduct(this);
        customerProduct.openReadableDatabase();
        hasCustomerPurchased = customerProduct.hasCustomerBoughtProduct(pharmacyId, productId);
        customerProduct.closeDatabase();
        return hasCustomerPurchased;
    }

    private boolean hasCustomerIncreasedProductQuantity(String pharmacyId, String productId, int currentQty) {
        CustomerProduct customerProduct = new CustomerProduct(this);
        customerProduct.openReadableDatabase();
        int previousQty = customerProduct.getInvoicedQuantityForCustomer(pharmacyId, productId);
        customerProduct.closeDatabase();
        boolean increased = false;
        if (currentQty > previousQty) {
            increased = true;
        }
        return increased;
    }

    private void saveCustomerAverage() {
        CustomerProductAvg customerProductAvg = new CustomerProductAvg(InvoiceGen3Activity.this);

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (SelectedProduct sp : selectedProductsArray) {
            if (map.containsKey(sp.getProductCode())) {
                int total = map.get(sp.getProductCode()) + sp.getShelfQuantity() + sp.getRequestedQuantity();
                map.remove(sp.getProductCode());
                map.put(sp.getProductCode(), total);
                Log.e("SaveCustomerAverage: removed and added", sp.getProductCode().toString());
            } else {
                map.put(sp.getProductCode(), sp.getRequestedQuantity() + sp.getShelfQuantity());
                Log.e("SaveCustomerAverage: added", sp.getProductCode().toString());
            }
        }

        for (String key : map.keySet()) {
            customerProductAvg.openReadableDatabase();

            if (customerProductAvg.hasCustomerBoughtProduct(pharmacyId, key)) {
                Log.i(InvoiceGen3Activity.class.getSimpleName(), "CustomerProduct AVG: Customer has invoiced");
                ArrayList<Integer> lastInv = customerProductAvg.getInvoiceCount(pharmacyId, key);
                customerProductAvg.closeDatabase();

                customerProductAvg.openWritableDatabase();
                int result = customerProductAvg.updateCustomerProductAverage(pharmacyId, key, lastInv.get(0) + map.get(key), lastInv.get(1) + 1);
                if (result > 0) {
                    Log.i(InvoiceGen3Activity.class.getSimpleName(), "Successfully UPDATED customer product average");
                } else {
                    Log.e(InvoiceGen3Activity.class.getSimpleName(), "Failed to UPDATE customer product average");
                }
                customerProductAvg.closeDatabase();
            } else {
                Log.i(InvoiceGen3Activity.class.getSimpleName(), "CustomerProduct AVG: Customer has NOT invoiced");
                customerProductAvg.openWritableDatabase();
                long result = customerProductAvg.insertCustomerProductAvg(pharmacyId, key, map.get(key), 1);
                if (result > 0) {
                    Log.i(InvoiceGen3Activity.class.getSimpleName(), "Successfully INSERTED customer product average");
                } else {
                    Log.e(InvoiceGen3Activity.class.getSimpleName(), "Failed to INSERT customer product average");
                }
            }


        }

    }

    private void saveCustomerProductQuantity() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (SelectedProduct sp : selectedProductsArray) {
            if (map.containsKey(sp.getProductCode())) {
                int total = map.get(sp.getProductCode()) + sp.getShelfQuantity() + sp.getRequestedQuantity();
                map.remove(sp.getProductCode());
                map.put(sp.getProductCode(), total);
                Log.e("Seperate Method: removed and added", sp.getProductCode().toString());
            } else {
                map.put(sp.getProductCode(), sp.getRequestedQuantity() + sp.getShelfQuantity());
                Log.e("Seperate Method added", sp.getProductCode().toString());
            }
        }

        timeStampMillies = new Date().getTime();
        CustomerProduct customerProduct = new CustomerProduct(this);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date(timeStampMillies));


        for (String key : map.keySet()) {
            if (hasCustomerBoughtProduct(pharmacyId, key)) {
                if (hasCustomerIncreasedProductQuantity(pharmacyId, key, map.get(key))) {
                    customerProduct.openWritableDatabase();
                    long r = customerProduct.updateCustomerProduct(pharmacyId, key, String.valueOf(map.get(key)), timeStamp);
                    customerProduct.closeDatabase();
                    Log.e("seperateshelfqty", "update " + r + " " + pharmacyId + " " + " " + key + " " + map.get(key));
                } else {
                    Log.e("IG3", "Customer has not increased amount");
                }

            } else {
                customerProduct.openWritableDatabase();
                long r = customerProduct.insertCustomerProduct(pharmacyId, key, String.valueOf(map.get(key)), timeStamp);
                customerProduct.closeDatabase();
                Log.e("seperateshelfqty", "insert " + r);
            }
        }


    }

    public void onLocationChanged(Location location) {

    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {

    }

    public void onProviderDisabled(String s) {

    }


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
                new TransferAudit(InvoiceGen3Activity.this).execute();
            } else if (returnCode == 1) {
                Toast notificationToast = Toast.makeText(context, "Unable to Upload invoice data to the server.", Toast.LENGTH_SHORT);
                notificationToast.show();
                new TransferAudit(InvoiceGen3Activity.this).execute();
            } else if (returnCode == 3) {
                Toast notificationToast = Toast.makeText(context, "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
                notificationToast.show();
                new TransferAudit(InvoiceGen3Activity.this).execute();
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
                                custNo = itnDetails[8];// this is where yu have to
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
                                //						invoiceDetails[4] = changeDateFormat(invoiceData[10]); // Invoice date
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
                        while (responseArr == null) {
                            try {


                                responseArr = webService.uploadInvoiceDetails(params[0], params[1], invoicedProductDetailList);

                                Thread.sleep(100);
                            } catch (SocketException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();


                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                        Log.w("Log",
                                "update data result : "
                                        + responseArr.contains("No Error")
                        );
                        if (responseArr.contains("No Error")) {

                            Log.w("Log", "Update the iternarary status");

                            Invoice invoiceObj = new Invoice(
                                    context);
                            invoiceObj.openReadableDatabase();
                            invoiceObj.setInvoiceUpdatedStatus(invoiceData[0],
                                    "true");
                            invoiceObj.closeDatabase();

                            returnValue = 2;

                        }

                        Log.w("Log", "loadProductRepStoreData result : "
                                + responseArr);

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

    private class TransferAudit extends AsyncTask<Void, Void, Void> {

        SharedPreferences sharedPreferences;
        String repId;
        ArrayList<String> responseArr;
        private final Context context;
        String deviceId;


        private TransferAudit(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            repId = sharedPreferences.getString("RepId", "-1");
            deviceId = sharedPreferences.getString("DeviceId", "-1");
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
                new CheckDayTasks(InvoiceGen3Activity.this).execute();
            } else {
                for (int i = 0; i < responseArr.size(); i++) {
                    Invoice invoiceObj = new Invoice(InvoiceGen3Activity.this);
                    invoiceObj.openReadableDatabase();
                    invoiceObj.setInvoiceUpdatedStatus(responseArr.get(i), "false");
                    invoiceObj.closeDatabase();
                    System.out.println("Invooo :" + responseArr.get(i));
                }
                Reps repController = new Reps(InvoiceGen3Activity.this);
                repController.openReadableDatabase();
                String[] reps = repController.getRepDetails();
                repController.closeDatabase();

                new UploadInvoiceHeaderTask(InvoiceGen3Activity.this, repId, deviceId, reps[8]).execute();
                new UploadInvoiceTaskAudit(InvoiceGen3Activity.this).execute("1");
            }


        }

    }

    private class CheckDayTasks extends AsyncTask<Void, Void, Void> {

        SharedPreferences sharedPreferences;
        String repId;
        String responseInvoiceCount;
        private final Context context;


        private CheckDayTasks(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            repId = sharedPreferences.getString("RepId", "-1");
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

            Invoice invoiceObject = new Invoice(InvoiceGen3Activity.this);
            invoiceObject.openReadableDatabase();
            invoicedIds = invoiceObject.getInvoiceCountByDate();
            invoiceObject.closeDatabase();

            try {
                if (Integer.parseInt(responseInvoiceCount) == invoicedIds.size()) {
                    Toast.makeText(InvoiceGen3Activity.this, "Successfully audited", Toast.LENGTH_SHORT).show();

                } else {
                    new getCheckDayTasksInvoiceID(InvoiceGen3Activity.this).execute();
                }

            } catch (NumberFormatException e) {
                System.out.println("himaaaaa CheckDayTasks NumberFormatException" + e);
            } catch (Exception e) {
                System.out.println("himaaaaa CheckDayTasks tException" + e);
            }

        }

    }

    private class getCheckDayTasksInvoiceID extends AsyncTask<Void, Void, Void> {

        SharedPreferences sharedPreferences;
        String repId;
        private final Context context;
        ArrayList<String> checkDayTasksInvoiceID;
        String deviceId;

        ArrayList<String> allInvoicedIds;
        ArrayList<String> allReturnInvoicedIds;

        private getCheckDayTasksInvoiceID(Context context) {
            this.context = context;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            repId = sharedPreferences.getString("RepId", "-1");
            deviceId = sharedPreferences.getString("DeviceId", "-1");
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
                if (checkDayTasksInvoiceID.isEmpty() || checkDayTasksInvoiceID == null) {

                } else {
                    for (int i = 0; i < checkDayTasksInvoiceID.size(); i++) {
                        Invoice invoiceObj = new Invoice(InvoiceGen3Activity.this);
                        invoiceObj.openReadableDatabase();
                        invoiceObj.setInvoiceUpdatedStatus(checkDayTasksInvoiceID.get(i), "false");
                        invoiceObj.closeDatabase();


                    }
                    Reps repController = new Reps(InvoiceGen3Activity.this);
                    repController.openReadableDatabase();
                    String[] reps = repController.getRepDetails();
                    repController.closeDatabase();

                    new UploadInvoiceHeaderTask(InvoiceGen3Activity.this, repId, deviceId, reps[8]).execute();
                    new UploadInvoiceTaskAudit(InvoiceGen3Activity.this).execute("1");

                }
            } catch (Exception e) {

            }
        }
    }

    public class UploadInvoiceTaskAudit extends AsyncTask<String, Integer, Integer> {

        private Context context;
        SharedPreferences sharedPreferences;
        String repId;

        public UploadInvoiceTaskAudit(Context context) {
            this.context = context;

        }


        protected void onPreExecute() {

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Integer returnCode) {

            if (returnCode == 2) {
                new TransferAudit(InvoiceGen3Activity.this).execute();
            } else if (returnCode == 1) {
                new TransferAudit(InvoiceGen3Activity.this).execute();
            } else if (returnCode == 3) {
                new TransferAudit(InvoiceGen3Activity.this).execute();
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
                                custNo = itnDetails[8];// this is where yu have to
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
//						invoiceDetails[4] = changeDateFormat(invoiceData[10]); // Invoice date
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
                        while (responseArr == null) {
                            try {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                String deviceId = sharedPreferences.getString("DeviceId", "-1");
                                String repId = sharedPreferences.getString("RepId", "-1");

                                responseArr = webService.uploadInvoiceDetails(deviceId, repId, invoicedProductDetailList);

                                Thread.sleep(100);
                            } catch (SocketException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();


                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                        Log.w("Log",
                                "update data result : "
                                        + responseArr.contains("No Error")
                        );
                        if (responseArr.contains("No Error")) {

                            Log.w("Log", "Update the iternarary status");

                            Invoice invoiceObj = new Invoice(
                                    context);
                            invoiceObj.openReadableDatabase();
                            invoiceObj.setInvoiceUpdatedStatus(invoiceData[0],
                                    "true");
                            invoiceObj.closeDatabase();

                            returnValue = 2;

                        }

                        Log.w("Log", "loadProductRepStoreData result : "
                                + responseArr);

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

    private class TargetSync extends AsyncTask<Void, Void, Void> {
        ArrayList<String[]> responseArr ;
        private final Context context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");

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

            new TransferAudit(InvoiceGen3Activity.this).execute();

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
                    try {

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
                                invoiceDetailsFree[2] = invoiceDetails[2]; // Issue mode
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
                            while (responseArr == null) {
                                try {
                                    WebService webService = new WebService();
                                    responseArr = webService.uploadProductReturnsDetails(
                                            deviceId, repId, returnedProductList);

                                    Thread.sleep(100);

                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

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

                    } catch (SocketException e) {
                        Log.w("Log", "Upload prod returns error: "
                                + e.toString());
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




    public void showAchievement() {
        //pharmacyId
        Double targetValue, totalInvoiceValue;

        Itinerary in = new Itinerary(InvoiceGen3Activity.this);
        in.openWritableDatabase();
        targetValue = in.getCustomerTargetById(pharmacyId);
        in.closeDatabase();

        Invoice invoice = new Invoice(InvoiceGen3Activity.this);
        invoice.openWritableDatabase();
        totalInvoiceValue = invoice.getTotaleInvoiceValueOfACustome(pharmacyId);
        invoice.closeDatabase();


        final Dialog dialogBox = new Dialog(InvoiceGen3Activity.this);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBox.setContentView(R.layout.dialog_target_achievement);
        dialogBox.setCancelable(true);

        TextView txtAchievementStatus =(TextView)dialogBox.findViewById(R.id.textView45);
        TextView txtTarget =(TextView)dialogBox.findViewById(R.id.textView47);
        TextView txtAchievement =(TextView)dialogBox.findViewById(R.id.textView48);

        GifImageView gifImageView =(GifImageView)dialogBox.findViewById(R.id.gifImageView);
        RelativeLayout layoutMain = (RelativeLayout)dialogBox.findViewById(R.id.relativeLayout_main);

        Button btnOk =(Button)dialogBox.findViewById(R.id.button);

        gifImageView.setImageResource(R.drawable.tenor);


        if(targetValue<=totalInvoiceValue){
            //well done
            txtAchievementStatus.setText("Your target has been achieved !!");
            txtTarget.setText("Your Target :"+targetValue);
            txtAchievement.setText("Your Achievement :"+totalInvoiceValue);
            gifImageView.setImageResource(R.drawable.tenor);
            layoutMain.setBackgroundColor(getResources().getColor(R.color.Achievement_dialog_background));

        }else {
            txtAchievementStatus.setText("Your target has been not achieved !!");
            txtTarget.setText("Your Target :"+targetValue);
            txtAchievement.setText("Your Achievement :"+totalInvoiceValue);
            gifImageView.setImageResource(R.drawable.tenor_sad);
            layoutMain.setBackgroundColor(getResources().getColor(R.color.white));
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
            }
        });
        dialogBox.show();
    }


}
