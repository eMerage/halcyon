package com.halcyon.channelbridge;

import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.DealerSales;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgews.WebService;

public class ReturnInvoiceActivity extends Activity implements LocationListener,AdapterView.OnItemSelectedListener{

    TableLayout tblInvoicedItems;
    TextView tViewCustomerName, tViewDate, tViewInvoiceNumber,
            tViewInvoiceValue;
    AutoCompleteTextView autoTvSearchProduct;
    ImageButton iBtnClearSearchString;
    Spinner  spInvoice;
    Button btnClearAll, btnSaveReturns, btnCancel, btnResalableAll, btnPrint;
    String pharmacyId, itineraryId, custName, custAddress, invoiceNo;
    ArrayList<String[]> invoiceData;
    HashMap<String, String[]> returnQuantities;
    DEL_Outstandiing outstandiing;
    ArrayList<String> invoiceNumberList;

    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();

    String testVar = "";
    boolean saveBtnFlag = false;
    private LocationManager locationManager;
    double lat,lng;
    Location location;
    private String  onTimeOrNot;
    private Boolean iswebApprovalActive = true;
    private DealerSales dealerSalesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_invoice);

        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewDate = (TextView) findViewById(R.id.labelDate);
        tViewInvoiceNumber = (TextView) findViewById(R.id.tvInvoiceNumber);
        tViewInvoiceValue = (TextView) findViewById(R.id.tvInvoiceValue);
        iBtnClearSearchString = (ImageButton) findViewById(R.id.bRemoveSearchString);
        autoTvSearchProduct = (AutoCompleteTextView) findViewById(R.id.etProductSearch);
        tblInvoicedItems = (TableLayout) findViewById(R.id.tlInvoicedItems);
        btnClearAll = (Button) findViewById(R.id.bClearAll);
        btnSaveReturns = (Button) findViewById(R.id.bSaveReturns);
        btnCancel = (Button) findViewById(R.id.bCancel);
        btnResalableAll = (Button) findViewById(R.id.bResalableAll);
        btnPrint = (Button) findViewById(R.id.bPrint);
        btnPrint.setEnabled(false);
        spInvoice = (Spinner)findViewById(R.id.labelInvoiceValue);

        returnQuantities = new HashMap<String, String[]>();
        String lastInvoiceId = getLastInvoiceForCustomer(itineraryId);
        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval",true));
        dealerSalesController = new DealerSales(ReturnInvoiceActivity.this);
        setInitialData();

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        }

        if (saveBtnFlag) {
            btnSaveReturns.setEnabled(false);
            btnResalableAll.setEnabled(false);
            btnCancel.setText("Done");
            btnPrint.setEnabled(true);

        }



//        if (!saveBtnFlag) {
//
//            try {
//                InvoicedProducts invoiceProductsObject = new InvoicedProducts(
//                        this);
//                invoiceProductsObject.openReadableDatabase();
//                invoiceData = invoiceProductsObject
//                        .getInvoiceDetailsForReturnsByInvoiceId(lastInvoiceId);
//                invoiceProductsObject.closeDatabase();
//            } catch (Exception e) {
//                Log.w("Error getting invoiced Item Data", e.toString());
//            }
//        }
        GetGPS();
      //  populateInvoicedItemsTable(invoiceData, returnQuantities, true);

        InvoicedProducts invoiceProductsObject = new InvoicedProducts(this);
        invoiceProductsObject.openReadableDatabase();
       // List<String[]> allinv = invoiceProductsObject.getAllInvoicedProducts();
        invoiceProductsObject.closeDatabase();



        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent startItinerary = new Intent(getApplication(),
                        ItineraryList.class);
                finish();
                startActivity(startItinerary);

            }
        });

        btnResalableAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (String[] invoicedProduct : invoiceData) {
                    String[] returnDetails = new String[11];
                    returnDetails[0] = invoicedProduct[5];
                    returnDetails[1] = invoicedProduct[0];
                    returnDetails[2] = invoicedProduct[1];
                    returnDetails[3] = String.valueOf(Integer.parseInt(invoicedProduct[1]));
                    returnDetails[4] = "0";
                    returnDetails[5] = invoicedProduct[3];
                    returnDetails[6] = invoicedProduct[2];
                    returnDetails[7] = invoicedProduct[7];
                    returnDetails[8] = invoicedProduct[6];
                    returnDetails[9] = invoicedProduct[8];
                    returnDetails[10] = invoicedProduct[9];

                    if (returnQuantities.containsKey(returnDetails[1])) {
                        returnQuantities.remove(returnDetails[1]);
                        returnQuantities.put(returnDetails[1], returnDetails);

                    } else {
                        returnQuantities.put(returnDetails[1], returnDetails);
                        Log.w("returnQuantities.size()",
                                returnQuantities.size() + "");
                    }
                }
                tblInvoicedItems.removeAllViews();
                populateInvoicedItemsTable(invoiceData, returnQuantities, true);
            }
        });

        btnSaveReturns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!returnQuantities.isEmpty()) {

                    if (validateSave()) {

                        saveBtnFlag = true;

                        // Sequence sequence = new
                        // Sequence(ReturnInvoiceActivity.this);
                        // sequence.openReadableDatabase();
                        // String inv = sequence.getLastRowId("invoice");
                        // sequence.closeDatabase();
                        //
                        // final String invoiceId =
                        // String.valueOf(Integer.parseInt(inv)+1);

                        // returnDetails[0] - Product Description
                        // returnDetails[1] - Batch
                        // returnDetails[2] = Qty
                        // returnDetails[3] = resalable
                        // returnDetails[4] = company ret
                        // returnDetails[5] = total
                        // returnDetails[6] = invoice id

                        Invoice invoice = new Invoice(ReturnInvoiceActivity.this);
                        invoice.openWritableDatabase();
                        invoice.setIsReturnedStatus(true, invoiceNo);
                        invoice.closeDatabase();

                        String timeStamp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                        double total = 0;

                        for (String returnProductKey : returnQuantities.keySet()) {
                            String[] returnDetails = returnQuantities
                                    .get(returnProductKey);

                            ProductReturns productReturnObject = new ProductReturns(
                                    ReturnInvoiceActivity.this);

                            if (!((returnDetails[3].contentEquals("0")) || (returnDetails[3]
                                    .isEmpty()))) {
                                productReturnObject.openWritableDatabase();
                                // productReturnObject.insertProductReturn(productCode,
                                // batchNo, invoiceNo, issueMode, normal, free,
                                // returnDate, customerNo, uploadedStatus,
                                // unitPrice, discount, returnInvoice,
                                // invoiceValidated)
                                productReturnObject.insertProductReturn(
                                        returnDetails[7], returnDetails[1],
                                        returnDetails[6], "SR",
                                        returnDetails[3], returnDetails[9],
                                        timeStamp, pharmacyId, "false",
                                        returnDetails[8], returnDetails[10],
                                        invoiceNo, "true",Double.toString(lat),Double.toString(lng),onTimeOrNot);
                                productReturnObject.closeDatabase();
                                Log.w("RETURN DISCOUNT SAVE", returnDetails[10]);

                                ProductRepStore productRepStoreObject = new ProductRepStore(
                                        ReturnInvoiceActivity.this);
                                productRepStoreObject.openWritableDatabase();

                                int freeQty = 0;

                                if (returnDetails[9] != null && returnDetails[9].length() > 0) {
                                    freeQty = Integer.parseInt(returnDetails[9]);
                                }


                                int rtnProdQty = Integer.parseInt(returnDetails[3]) + freeQty;

                                productRepStoreObject.updateProductRepStoreReturns(
                                        returnDetails[1], String.valueOf(rtnProdQty));
                                productRepStoreObject.closeDatabase();

                                if (Integer.parseInt(returnDetails[3]) <= Integer
                                        .parseInt(returnDetails[2])) {
                                    double temp = Double
                                            .parseDouble(returnDetails[3])
                                            * Double.parseDouble(returnDetails[8]);
                                    total = total + temp;
                                } else {
                                    double temp = Double
                                            .parseDouble(returnDetails[2])
                                            * Double.parseDouble(returnDetails[8]);
                                    total = total + temp;
                                }

                                // invoicedProductsObject.openWritableDatabase();
                                // //invoicedProductsObject.insertInvoicedProducts(invoiceId,productCode,
                                // batchNo, requestQty, free, discount, normal,
                                // date)
                                // invoicedProductsObject.insertInvoicedProducts(invoiceId,
                                // returnDetails[7], returnDetails[1], "0", "0",
                                // "0", returnDetails[3], timeStamp, "true");
                                // invoicedProductsObject.closeDatabase();

                                // invoicedProductsObject.openWritableDatabase();
                                // invoicedProductsObject.setReturnedTrue(returnDetails[6],
                                // returnDetails[7]);
                                // invoicedProductsObject.closeDatabase();

                                Log.w("return 8", returnDetails[8]);
                            }

                            if (!((returnDetails[4].contentEquals("0")) || (returnDetails[4]
                                    .isEmpty()))) {
                                productReturnObject.openWritableDatabase();
                                // productReturnObject.insertProductReturn(productCode,
                                // batchNo, invoiceNo, issue mode, normal, free,
                                // returnDate, customerNo, uploadedStatus)
                                productReturnObject.insertProductReturn(
                                        returnDetails[7], returnDetails[1],
                                        returnDetails[6], "CR",
                                        returnDetails[4], "0", timeStamp,
                                        pharmacyId, "false", returnDetails[8],
                                        returnDetails[10], invoiceNo, "true",Double.toString(lat),Double.toString(lng),onTimeOrNot);
                                productReturnObject.closeDatabase();

                                if (Integer.parseInt(returnDetails[4]) <= Integer
                                        .parseInt(returnDetails[2])) {
                                    double temp = Double
                                            .parseDouble(returnDetails[4])
                                            * Double.parseDouble(returnDetails[8]);
                                    total = total + temp;
                                } else {
                                    double temp = Double
                                            .parseDouble(returnDetails[2])
                                            * Double.parseDouble(returnDetails[8]);
                                    total = total + temp;
                                }

                                // invoicedProductsObject.openWritableDatabase();
                                // //invoicedProductsObject.insertInvoicedProducts(invoiceId,
                                // productCode, batchNo, requestQty, free, discount,
                                // normal, date)
                                // invoicedProductsObject.insertInvoicedProducts(invoiceId,
                                // returnDetails[7], returnDetails[1], "0", "0",
                                // "0", returnDetails[4], timeStamp, "true");
                                // invoicedProductsObject.closeDatabase();

                                // invoicedProductsObject.openWritableDatabase();
                                // invoicedProductsObject.setReturnedTrue(returnDetails[6],
                                // returnDetails[7]);
                                // invoicedProductsObject.closeDatabase();
                                Log.w("return 8", returnDetails[8]);
                            }
                        }
                        // invoiceObject.openWritableDatabase();
                        // // invoiceObject.insertInvoice(itineraryid, paymentType,
                        // totalAmount, paidAmount, creditAmount, cheque,
                        // markekReturn, discount, uploadedStatus, timeStamp)
                        // invoiceObject.insertInvoice(invoiceId, "R",
                        // String.format("%.2f", total), "0",
                        // "-"+String.format("%.2f", total), "0",
                        // String.format("%.2f", total), "0", "false", timeStamp);
                        // invoiceObject.closeDatabase();

                        // Intent itineraryList = new
                        // Intent(ReturnInvoiceActivity.this, ItineraryList.class);
                        // finish();
                        // startActivity(itineraryList);
                        btnSaveReturns.setEnabled(false);
                        btnResalableAll.setEnabled(false);
                        btnCancel.setText("Done");
                        btnPrint.setEnabled(true);

//					tblInvoicedItems.
                        new UploadProductReturnsTask(ReturnInvoiceActivity.this).execute("1");
                        tblInvoicedItems.removeAllViews();
                        populateInvoicedItemsTable(invoiceData, returnQuantities, false);

                    } else {
                        Toast returnProductsEmpty = Toast.makeText(ReturnInvoiceActivity.this, "Enter quantities for all the Products To Return!", Toast.LENGTH_SHORT);
                        returnProductsEmpty.setGravity(Gravity.TOP, 100, 100);
                        returnProductsEmpty.show();
                    }

                }
            }
        });

        btnClearAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (String[] invoicedProduct : invoiceData) {
                    String[] returnDetails = new String[11];
                    returnDetails[0] = invoicedProduct[5];
                    returnDetails[1] = invoicedProduct[0];
                    returnDetails[2] = invoicedProduct[1];
                    returnDetails[3] = "0";
                    returnDetails[4] = "0";
                    returnDetails[5] = invoicedProduct[3];
                    returnDetails[6] = invoicedProduct[2];
                    returnDetails[7] = invoicedProduct[7];
                    returnDetails[8] = invoicedProduct[6];
                    returnDetails[9] = invoicedProduct[8];
                    returnDetails[10] = invoicedProduct[9];

                    if (returnQuantities.containsKey(returnDetails[1])) {
                        returnQuantities.remove(returnDetails[1]);
                        returnQuantities.put(returnDetails[1], returnDetails);

                    } else {
                        returnQuantities.put(returnDetails[1], returnDetails);
                        Log.w("returnQuantities.size()",
                                returnQuantities.size() + "");
                    }
                }
                tblInvoicedItems.removeAllViews();
                populateInvoicedItemsTable(invoiceData, returnQuantities, true);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Builder alertPrint = new AlertDialog.Builder(
                        ReturnInvoiceActivity.this)
                        .setTitle("Print Returns")
                        .setMessage("Are you sure you want to print?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        try {

                                            SharedPreferences sharedPreferences = PreferenceManager
                                                    .getDefaultSharedPreferences(getBaseContext());
                                            boolean prePrintInvoiceFormatEnabled = sharedPreferences
                                                    .getBoolean(
                                                            "cbPrefPrePrintInvoice",
                                                            true);
                                            String repId = sharedPreferences
                                                    .getString("RepId", "-1");

                                            if (prePrintInvoiceFormatEnabled) {

                                                Date dateObj = new Date();

                                                String date = new SimpleDateFormat(
                                                        "yyyy-MM-dd")
                                                        .format(dateObj);
                                                String time = new SimpleDateFormat(
                                                        "hh:mm:ss a")
                                                        .format(dateObj);

                                                String printDateTime = new SimpleDateFormat(
                                                        "yyyy-MM-dd hh:mm:ss a")
                                                        .format(new Date());

                                                boolean flag = true;

                                                Reps reps = new Reps(
                                                        ReturnInvoiceActivity.this);
                                                reps.openReadableDatabase();
                                                ArrayList<String> delearDetails = reps
                                                        .getRepDetailsForPrinting(repId);
                                                reps.closeDatabase();
                                                int customerNameRemain = 0;
                                                int addressRemain = 0;
                                                String dealerName = delearDetails
                                                        .get(1).trim();
                                                String dealerCity = delearDetails
                                                        .get(2).trim();

                                                if (dealerName.length() > 18) {
                                                    dealerName = dealerName
                                                            .substring(0, 18);
                                                }

                                                if (dealerCity.length() > 18) {
                                                    dealerCity = dealerCity
                                                            .substring(0, 18);
                                                }
                                                if (custName.length() > 24) {
                                                    custName = custName.substring(
                                                            0, 25);
                                                } else {
                                                    customerNameRemain = 25 - custName
                                                            .length();
                                                }
                                                customerNameRemain = customerNameRemain + 1;
                                                for (int i = 0; i <= customerNameRemain; i++) {
                                                    custName = custName + " ";
                                                }

                                                if (custAddress.length() > 24) {
                                                    custAddress = custAddress
                                                            .substring(0, 25);
                                                } else {
                                                    addressRemain = 25 - custAddress
                                                            .length();
                                                }
                                                addressRemain = addressRemain + 1;
                                                for (int i = 0; i <= addressRemain; i++) {
                                                    custAddress = custAddress + " ";
                                                }

                                                String headerData = "\n";
                                                headerData = headerData
                                                        + "                                  "
                                                        + invoiceNo + "\n";
                                                headerData = headerData
                                                        + "                                  "
                                                        + date + "\n";
                                                headerData = headerData + "\n";
                                                headerData = headerData + custName
                                                        + dealerName + "\n";
                                                headerData = headerData
                                                        + custAddress + dealerCity
                                                        + "\n";
                                                headerData = headerData + "\n\n\n";

                                                String printData = "";

                                                double totalPrice = 0.0;
                                                int count = 0;
                                                int totalItems = 0;
                                                // returnDetails[0] =
                                                // tvProductDescription.getText().toString();
                                                // returnDetails[1] =
                                                // tvBatch.getText().toString();
                                                // returnDetails[2] =
                                                // tvQuantity.getText().toString();
                                                // returnDetails[3] = resalable;
                                                // returnDetails[4] =
                                                // txtCompanyReturns.getText().toString();
                                                // returnDetails[5] =
                                                // tvTotal.getText().toString();
                                                // returnDetails[6] =
                                                // invoicedProducts[2];
                                                // returnDetails[7] =
                                                // invoicedProducts[7];
                                                // returnDetails[8] =
                                                // invoicedProducts[6];
                                                // returnDetails[9] =
                                                // tvFree.getText().toString();

                                                for (String returnProductKey : returnQuantities
                                                        .keySet()) {
                                                    String[] returnDetails = returnQuantities
                                                            .get(returnProductKey);

                                                    if (flag) {

                                                        if (printData.length() > 1) {
                                                            printData = printData
                                                                    + "\n\n\n\n\n\n\n";
                                                        }

                                                        printData = printData
                                                                + headerData;
                                                        flag = false;
                                                    }

                                                    String productDescription = returnDetails[0];
                                                    String batch = returnDetails[1];

                                                    int totalQty = 0;

                                                    if (returnDetails[3] != null
                                                            && returnDetails[3] != "null") {
                                                        totalQty = totalQty
                                                                + Integer
                                                                .parseInt(returnDetails[3]);
                                                    }

                                                    if (returnDetails[9] != null
                                                            && returnDetails[9] != "null") {
                                                        totalQty = totalQty
                                                                + Integer
                                                                .parseInt(returnDetails[9]);
                                                    }

                                                    // String price =
                                                    // (String.format("%.2f",
                                                    // returnDetails[8]));
                                                    String price = (String.format(
                                                            "%.2f",
                                                            Double.parseDouble(returnDetails[8])));
                                                    String qty = String
                                                            .valueOf(totalQty);

                                                    double totalVal;
                                                    if (returnDetails[10].isEmpty()
                                                            || returnDetails[10]
                                                            .contentEquals("0")) {
                                                        totalVal = Double
                                                                .parseDouble(returnDetails[2])
                                                                * Double.parseDouble(returnDetails[8]);
                                                    } else {
                                                        totalVal = ((Double
                                                                .parseDouble(returnDetails[2]) * Double
                                                                .parseDouble(returnDetails[8])) - ((Double
                                                                .parseDouble(returnDetails[2]) * Double
                                                                .parseDouble(returnDetails[8])) * (Double
                                                                .parseDouble(returnDetails[10]) / 100)));
                                                    }

                                                    // String total =
                                                    // returnDetails[5];

                                                    String total = (String.format(
                                                            "%.2f", totalVal));

                                                    int batchRemain = 0;
                                                    int quantityRemain = 0;
                                                    int unitPriceRemain = 0;
                                                    int valueRemain = 0;

                                                    if (productDescription.length() > 44) {
                                                        productDescription = productDescription
                                                                .substring(0, 44);
                                                    }

                                                    if (batch.length() > 13) {
                                                        batch = batch.substring(0,
                                                                13);
                                                    } else {
                                                        batchRemain = 13 - batch
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= batchRemain; i++) {
                                                        batch = batch + " ";
                                                    }

                                                    if (qty.length() > 7) {
                                                        qty = qty.substring(0, 7);
                                                    } else if (qty.length() < 7) {
                                                        quantityRemain = 6 - qty
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= quantityRemain; i++) {
                                                        qty = " " + qty;
                                                    }

                                                    if (price.length() > 9) {
                                                        price = price.substring(0,
                                                                9);
                                                    } else if (price.length() < 9) {
                                                        unitPriceRemain = 8 - price
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= unitPriceRemain; i++) {
                                                        price = " " + price;
                                                    }

                                                    if (total.length() > 11) {
                                                        total = total.substring(0,
                                                                11);
                                                    } else if (total.length() < 11) {
                                                        valueRemain = 10 - total
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= valueRemain; i++) {
                                                        total = " " + total;
                                                    }

                                                    printData = printData
                                                            + productDescription
                                                            + "\n";
                                                    printData = printData
                                                            + (batch + qty + " "
                                                            + price + " " + total)
                                                            + "\n";

                                                    totalPrice = totalPrice
                                                            + totalVal;
                                                    totalItems = totalItems
                                                            + totalQty;

                                                    count++;
                                                    Log.w("COUNT", count + "lines");

                                                    if (count == 25) {
                                                        flag = true;
                                                        count = 0;
                                                    }

                                                }

                                                printData = printData
                                                        + "--------------------------------------------";
                                                printData = printData + "\n\n";

                                                String totalPriceValue = String
                                                        .format("%.2f", totalPrice);

                                                String footerData = "";

                                                if ((count + 6) < 30) {

                                                    footerData = footerData
                                                            + "Total Ret : "
                                                            + totalPriceValue
                                                            + "\n";
                                                    footerData = footerData
                                                            + "Items     : "
                                                            + totalItems + "\n";

                                                    footerData = footerData
                                                            + "\n\n";
                                                    footerData = footerData
                                                            + "-----------------------------\n";
                                                    footerData = footerData
                                                            + "  Customer Signature & Seal\n\n";

                                                    footerData = footerData
                                                            + "Print Date & Time : "
                                                            + "  " + printDateTime
                                                            + "\n\n";

                                                    footerData = footerData
                                                            + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                                                    printData = printData
                                                            + footerData;

                                                } else {

                                                    int k = 30 - count;

                                                    footerData = footerData
                                                            + "Total Ret : "
                                                            + totalPriceValue
                                                            + "\n";
                                                    footerData = footerData
                                                            + "Items     : "
                                                            + totalItems + "\n";

                                                    footerData = footerData
                                                            + "\n\n";
                                                    footerData = footerData
                                                            + "-----------------------------\n";
                                                    footerData = footerData
                                                            + "  Customer Signature & Seal\n\n";

                                                    footerData = footerData
                                                            + "Print Date & Time : "
                                                            + printDateTime
                                                            + "\n\n";

                                                    footerData = footerData
                                                            + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                                                    for (int i = 0; i <= k; i++) {
                                                        printData = printData
                                                                + "\n";
                                                    }

                                                    printData = printData
                                                            + headerData;
                                                    printData = printData
                                                            + footerData;

                                                }

                                                Log.w("printData : ", printData);

                                                Bundle bundleToView = new Bundle();
                                                bundleToView.putString("PrintData",
                                                        printData);

                                                Intent activityIntent = new Intent(
                                                        getApplicationContext(),
                                                        PrintUtility.class);
                                                activityIntent
                                                        .putExtras(bundleToView);
                                                startActivityForResult(
                                                        activityIntent, 0);

                                            } else {

                                                Date dateObj = new Date();

                                                String date = new SimpleDateFormat(
                                                        "yyyy-MM-dd")
                                                        .format(dateObj);
                                                String time = new SimpleDateFormat(
                                                        "hh:mm:ss a")
                                                        .format(dateObj);

                                                String printDateTime = new SimpleDateFormat(
                                                        "yyyy-MM-dd hh:mm:ss a")
                                                        .format(new Date());

                                                // boolean flag = true;

                                                Reps reps = new Reps(
                                                        ReturnInvoiceActivity.this);
                                                reps.openReadableDatabase();
                                                ArrayList<String> delearDetails = reps
                                                        .getRepDetailsForPrinting(repId);
                                                reps.closeDatabase();
                                                int customerNameRemain = 0;
                                                int addressRemain = 0;
                                                String dealerName = delearDetails
                                                        .get(1).trim();
                                                String dealerCity = delearDetails
                                                        .get(2).trim();
                                                String dealerTel = delearDetails
                                                        .get(3).trim();

                                                if (dealerName.length() > 18) {
                                                    dealerName = dealerName
                                                            .substring(0, 18);
                                                }

                                                if (dealerCity.length() > 18) {
                                                    dealerCity = dealerCity
                                                            .substring(0, 18);
                                                }
                                                if (custName.length() > 24) {
                                                    custName = custName.substring(
                                                            0, 25);
                                                } else {
                                                    customerNameRemain = 25 - custName
                                                            .length();
                                                }
                                                customerNameRemain = customerNameRemain + 1;
                                                for (int i = 0; i <= customerNameRemain; i++) {
                                                    custName = custName + " ";
                                                }

                                                if (custAddress.length() > 24) {
                                                    custAddress = custAddress
                                                            .substring(0, 25);
                                                } else {
                                                    addressRemain = 25 - custAddress
                                                            .length();
                                                }
                                                addressRemain = addressRemain + 1;
                                                for (int i = 0; i <= addressRemain; i++) {
                                                    custAddress = custAddress + " ";
                                                }

                                                String headerData = "";
                                                headerData = headerData
                                                        + dealerName + "\n";
                                                headerData = headerData
                                                        + dealerCity + "\n";
                                                headerData = headerData + "Tel: "
                                                        + dealerTel + "\n";
                                                headerData = headerData
                                                        + "Authorized Distributor for Indoscan Private Limited.";
                                                headerData = headerData + "\n\n";

                                                headerData = headerData
                                                        + "Invoice To\n";
                                                headerData = headerData + custName
                                                        + "Invoice No: "
                                                        + invoiceNo + "\n";
                                                headerData = headerData
                                                        + custAddress + "Date :"
                                                        + date + "\n";
                                                headerData = headerData + "\n\n";

                                                String printData = "";
                                                printData = printData + headerData;

                                                printData = printData
                                                        + "Description       Qty     Price       Value\n";
                                                printData = printData
                                                        + "--------------------------------------------";
                                                printData = printData + "\n";

                                                double totalPrice = 0.0;
                                                int count = 14;
                                                int totalItems = 0;
                                                int invoicePageCount = 1;
                                                // returnDetails[0] =
                                                // tvProductDescription.getText().toString();
                                                // returnDetails[1] =
                                                // tvBatch.getText().toString();
                                                // returnDetails[2] =
                                                // tvQuantity.getText().toString();
                                                // returnDetails[3] = resalable;
                                                // returnDetails[4] =
                                                // txtCompanyReturns.getText().toString();
                                                // returnDetails[5] =
                                                // tvTotal.getText().toString();
                                                // returnDetails[6] =
                                                // invoicedProducts[2];
                                                // returnDetails[7] =
                                                // invoicedProducts[7];
                                                // returnDetails[8] =
                                                // invoicedProducts[6];
                                                // returnDetails[9] =
                                                // tvFree.getText().toString();

                                                for (String returnProductKey : returnQuantities
                                                        .keySet()) {
                                                    String[] returnDetails = returnQuantities
                                                            .get(returnProductKey);

                                                    if (count == 60) {

                                                        printData = printData
                                                                + "\nPage "
                                                                + invoicePageCount
                                                                + "\n\n\n\n\n";
                                                        invoicePageCount++;
                                                        count = 0;
                                                    }

                                                    String productDescription = returnDetails[0];
                                                    String batch = returnDetails[1];

                                                    int totalQty = 0;

                                                    if (returnDetails[3] != null
                                                            && returnDetails[3] != "null") {
                                                        totalQty = totalQty
                                                                + Integer
                                                                .parseInt(returnDetails[3]);
                                                    }

                                                    if (returnDetails[9] != null
                                                            && returnDetails[9] != "null") {
                                                        totalQty = totalQty
                                                                + Integer
                                                                .parseInt(returnDetails[9]);
                                                    }

                                                    String price = (String.format(
                                                            "%.2f",
                                                            Double.parseDouble(returnDetails[8])));
                                                    String qty = String
                                                            .valueOf(totalQty);
                                                    // String total =
                                                    // returnDetails[5];

                                                    double totalVal;
                                                    if (returnDetails[10].isEmpty()
                                                            || returnDetails[10]
                                                            .contentEquals("0")) {
                                                        totalVal = Double
                                                                .parseDouble(returnDetails[2])
                                                                * Double.parseDouble(returnDetails[8]);
                                                    } else {
                                                        totalVal = ((Double
                                                                .parseDouble(returnDetails[2]) * Double
                                                                .parseDouble(returnDetails[8])) - ((Double
                                                                .parseDouble(returnDetails[2]) * Double
                                                                .parseDouble(returnDetails[8])) * (Double
                                                                .parseDouble(returnDetails[10]) / 100)));
                                                    }

                                                    // String total =
                                                    // returnDetails[5];

                                                    String total = (String.format(
                                                            "%.2f", totalVal));

                                                    int batchRemain = 0;
                                                    int quantityRemain = 0;
                                                    int unitPriceRemain = 0;
                                                    int valueRemain = 0;

                                                    if (productDescription.length() > 44) {
                                                        productDescription = productDescription
                                                                .substring(0, 44);
                                                    }

                                                    if (batch.length() > 13) {
                                                        batch = batch.substring(0,
                                                                13);
                                                    } else {
                                                        batchRemain = 13 - batch
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= batchRemain; i++) {
                                                        batch = batch + " ";
                                                    }

                                                    if (qty.length() > 7) {
                                                        qty = qty.substring(0, 7);
                                                    } else if (qty.length() < 7) {
                                                        quantityRemain = 6 - qty
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= quantityRemain; i++) {
                                                        qty = " " + qty;
                                                    }

                                                    if (price.length() > 9) {
                                                        price = price.substring(0,
                                                                9);
                                                    } else if (price.length() < 9) {
                                                        unitPriceRemain = 8 - price
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= unitPriceRemain; i++) {
                                                        price = " " + price;
                                                    }

                                                    if (total.length() > 11) {
                                                        total = total.substring(0,
                                                                11);
                                                    } else if (total.length() < 11) {
                                                        valueRemain = 10 - total
                                                                .length();
                                                    }
                                                    for (int i = 0; i <= valueRemain; i++) {
                                                        total = " " + total;
                                                    }

                                                    printData = printData
                                                            + productDescription
                                                            + "\n";
                                                    printData = printData
                                                            + (batch + qty + " "
                                                            + price + " " + total)
                                                            + "\n";

                                                    totalPrice = totalPrice
                                                            + Double.parseDouble(returnDetails[5]);
                                                    totalItems = totalItems
                                                            + Integer
                                                            .parseInt(returnDetails[4])
                                                            + Integer
                                                            .parseInt(returnDetails[3]);

                                                    count = count + 2;
                                                    Log.w("COUNT", count + "lines");

                                                }

                                                printData = printData
                                                        + "--------------------------------------------";
                                                printData = printData + "\n\n";

                                                String totalPriceValue = String
                                                        .format("%.2f", totalPrice);

                                                String footerData = "";

                                                if ((count + 12) < 60) {

                                                    footerData = footerData
                                                            + "Total Ret : "
                                                            + totalPriceValue
                                                            + "\n";
                                                    footerData = footerData
                                                            + "Items     : "
                                                            + totalItems + "\n";

                                                    footerData = footerData
                                                            + "\n\n";
                                                    footerData = footerData
                                                            + "-----------------------------\n";
                                                    footerData = footerData
                                                            + "  Customer Signature & Seal\n\n";

                                                    footerData = footerData
                                                            + "Print Date & Time : "
                                                            + "  " + printDateTime
                                                            + "\n\n";

                                                    footerData = footerData
                                                            + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                                                    printData = printData
                                                            + footerData;

                                                } else {

                                                    int k = 60 - count;

                                                    footerData = footerData
                                                            + "Total Ret : "
                                                            + totalPriceValue
                                                            + "\n";
                                                    footerData = footerData
                                                            + "Items     : "
                                                            + totalItems + "\n";

                                                    footerData = footerData
                                                            + "\n\n";
                                                    footerData = footerData
                                                            + "-----------------------------\n";
                                                    footerData = footerData
                                                            + "  Customer Signature & Seal\n\n";

                                                    footerData = footerData
                                                            + "Print Date & Time : "
                                                            + printDateTime
                                                            + "\n\n";

                                                    footerData = footerData
                                                            + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                                                    for (int i = 0; i <= k; i++) {
                                                        printData = printData
                                                                + "\n";
                                                    }

                                                    printData = printData
                                                            + "\nPage "
                                                            + invoicePageCount
                                                            + "\n\n\n\n\n";
                                                    invoicePageCount++;

                                                    printData = printData
                                                            + footerData;

                                                }

                                                Log.w("printData : ", printData);

                                                Bundle bundleToView = new Bundle();
                                                bundleToView.putString("PrintData",
                                                        printData);

                                                Intent activityIntent = new Intent(
                                                        getApplicationContext(),
                                                        PrintUtility.class);
                                                activityIntent
                                                        .putExtras(bundleToView);
                                                startActivityForResult(
                                                        activityIntent, 0);

                                            }

                                        } catch (Exception e) {
                                            Log.w("EROOR printing",
                                                    e.toString());
                                        }

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        return;
                                    }
                                });
                if (!returnQuantities.isEmpty()) {
                    alertPrint.show();
                }
            }
        });

    }

    private String getLastInvoiceForCustomer(String itnId) {
        // TODO Auto-generated method stub
        Invoice invoiceObject = new Invoice(this);
        invoiceObject.openReadableDatabase();
        String invoiceId = invoiceObject
                .getlastInvoiceForCustomerItinerary(itnId);
        invoiceObject.closeDatabase();

        return invoiceId;
    }

    private void populateInvoicedItemsTable(ArrayList<String[]> invoice,
                                            HashMap<String, String[]> retQty, boolean flagActive) {
        // TODO Auto-generated method stub
        String invoiceId = "";

        for (String[] invoiceDetails : invoice) {
            invoiceId = invoiceDetails[2];
        }

        boolean isProductInInvoiceReturned = false;

        if (flagActive) {
            Invoice invoiceObject = new Invoice(this);
            invoiceObject.openReadableDatabase();
            isProductInInvoiceReturned = invoiceObject
                    .getInvoiceReturnStatus(invoiceId);
            invoiceObject.closeDatabase();

        }


        TableRow tr;
        tr = new TableRow(this);
        tr.setId(0);
        tr.setPadding(0, 3, 0, 3);
        tr.setBackgroundColor(Color.parseColor("#d3d3d3"));
        tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        tblInvoicedItems.setShrinkAllColumns(true);

        TextView labelProductDescription = new TextView(this);
        labelProductDescription.setId(1);
        labelProductDescription.setText("Pr. Desc.");
        labelProductDescription.setGravity(Gravity.LEFT);
        labelProductDescription.setTextColor(Color.BLACK);
        labelProductDescription.setTypeface(null, Typeface.BOLD);
        tr.addView(labelProductDescription);

        TextView labelBatch = new TextView(this);
        labelBatch.setId(2);
        labelBatch.setText("Batch");
        labelBatch.setGravity(Gravity.LEFT);
        labelBatch.setTextColor(Color.BLACK);
        labelBatch.setTypeface(null, Typeface.BOLD);
        tr.addView(labelBatch);

        TextView labelQuantity = new TextView(this);
        labelQuantity.setId(3);
        labelQuantity.setText("Quantity");
        labelQuantity.setGravity(Gravity.LEFT);
        labelQuantity.setTextColor(Color.BLACK);
        labelQuantity.setTypeface(null, Typeface.BOLD);
        tr.addView(labelQuantity);

        TextView labelFree = new TextView(this);
        labelFree.setId(3);
        labelFree.setText("Free");
        labelFree.setGravity(Gravity.LEFT);
        labelFree.setTextColor(Color.BLACK);
        labelFree.setTypeface(null, Typeface.BOLD);
        tr.addView(labelFree);

        TextView labelResalable = new TextView(this);
        labelResalable.setId(4);
        labelResalable.setText("Resalable");
        labelResalable.setGravity(Gravity.LEFT);
        labelResalable.setTextColor(Color.BLACK);
        labelResalable.setTypeface(null, Typeface.BOLD);
        tr.addView(labelResalable);

        TextView labelCompanyReturns = new TextView(this);
        labelCompanyReturns.setId(5);
        labelCompanyReturns.setText("Company Returns");
        labelCompanyReturns.setGravity(Gravity.LEFT);
        labelCompanyReturns.setTextColor(Color.BLACK);
        labelCompanyReturns.setTypeface(null, Typeface.BOLD);
        tr.addView(labelCompanyReturns);

        TextView labelTotal = new TextView(this);
        labelTotal.setId(5);
        labelTotal.setText("Total");
        labelTotal.setGravity(Gravity.LEFT);
        labelTotal.setTextColor(Color.BLACK);
        labelTotal.setTypeface(null, Typeface.BOLD);
        tr.addView(labelTotal);
        tblInvoicedItems.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        try {
            int count = 1;

            if (!isProductInInvoiceReturned) {

                for (final String[] invoicedProducts : invoice) {
                    Log.w("returned", invoicedProducts[8]);
                    // if (invoicedProducts[8].contentEquals("false")) {

                    boolean flag = false;
                    for (String retDet : returnQuantities.keySet()) {
                        if (retDet.contentEquals(invoicedProducts[0])) {
                            flag = true;
                        }
                    }

                    invoiceNo = invoicedProducts[2];

                    tViewInvoiceNumber.setText(invoicedProducts[2]);
                    tViewInvoiceValue.setText(invoicedProducts[3]);

                    tr = new TableRow(this);
                    tr.setId(1000 + count);
                    tr.setPadding(0, 3, 0, 3);
                    tr.setLayoutParams(new LayoutParams(
                            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                    if (count % 2 != 0) {
                        tr.setBackgroundColor(Color.DKGRAY);

                    }

                    // 0 - batch
                    // 1 - normal
                    // 2 - invoiceId
                    // 3 - total amount
                    // 4 - itineraryId
                    // 5 - product description
                    // 6 - product price
                    // 7 - product code

                    final TextView tvProductDescription = new TextView(this);
                    tvProductDescription.setId(200 + count);
                    tvProductDescription.setText(invoicedProducts[5]);
                    tvProductDescription.setGravity(Gravity.LEFT);
                    tvProductDescription.setTextColor(Color.WHITE);
                    tr.addView(tvProductDescription);

                    final TextView tvBatch = new TextView(this);
                    tvBatch.setId(200 + count);
                    tvBatch.setText(invoicedProducts[0]);
                    tvBatch.setGravity(Gravity.LEFT);
                    tvBatch.setTextColor(Color.WHITE);
                    tr.addView(tvBatch);

                    final TextView tvQuantity = new TextView(this);
                    tvQuantity.setId(200 + count);
                    tvQuantity.setText(invoicedProducts[1]);
                    tvQuantity.setGravity(Gravity.LEFT);
                    tvQuantity.setTextColor(Color.WHITE);
                    tr.addView(tvQuantity);

                    final TextView tvFree = new TextView(this);
                    tvFree.setId(200 + count);
                    tvFree.setText(invoicedProducts[8]);
                    tvFree.setGravity(Gravity.LEFT);
                    tvFree.setTextColor(Color.WHITE);
                    tr.addView(tvFree);
                    Log.w("free", invoicedProducts[8]);

                    final EditText txtResalable = new EditText(this);
                    txtResalable.setId(200 + count);
                    txtResalable.setText("0");
                    // txtResalable.setBackgroundColor(Color.parseColor("#ffffff "));
                    txtResalable.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtResalable.setTextColor(Color.parseColor("#CC0000"));
                    txtResalable.setEnabled(flagActive);
                    tr.addView(txtResalable);

                    final EditText txtCompanyReturns = new EditText(this);
                    txtCompanyReturns.setId(200 + count);
                    txtCompanyReturns.setText("0");
                    txtCompanyReturns.setInputType(InputType.TYPE_CLASS_NUMBER);
                    txtCompanyReturns.setTextColor(Color.parseColor("#d3d3d3"));
                    txtCompanyReturns.setEnabled(flagActive);
                    tr.addView(txtCompanyReturns);

                    final TextView tvTotal = new TextView(this);
                    tvTotal.setId(200 + count);
                    double total;
                    if (invoicedProducts[9].isEmpty()
                            || invoicedProducts[9].contentEquals("0")) {
                        total = Double.parseDouble(invoicedProducts[1])
                                * Double.parseDouble(invoicedProducts[6]);
                    } else {
                        total = ((Double.parseDouble(invoicedProducts[1]) * Double
                                .parseDouble(invoicedProducts[6])) - ((Double
                                .parseDouble(invoicedProducts[1]) * Double
                                .parseDouble(invoicedProducts[6])) * (Double
                                .parseDouble(invoicedProducts[9]) / 100)));
                    }

                    tvTotal.setText(String.format("%.2f", total));
                    tvTotal.setGravity(Gravity.LEFT);
                    tvTotal.setTextColor(Color.WHITE);
                    tr.addView(tvTotal);

                    if (flag) {
                        String[] temp = returnQuantities
                                .get(invoicedProducts[0]);
                        txtResalable.setText(temp[3]);
                        txtCompanyReturns.setText(temp[4]);
                    }

                    txtResalable.addTextChangedListener(new TextWatcher() {

                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            // TODO Auto-generated method stub
                            try {
                                String resalable = s.toString().trim();
                                if (!((resalable.isEmpty()) && (resalable
                                        .contentEquals("0")))) {

                                    int qty = Integer.parseInt(tvQuantity
                                            .getText().toString());
                                    int free = Integer.parseInt(tvFree
                                            .getText().toString());
                                    int total = qty + free;
                                    int res = Integer.parseInt(resalable);
                                    Log.w("the total", total + "");
                                    if (res <= total) {

                                        String[] returnDetails = new String[11];
                                        returnDetails[0] = tvProductDescription
                                                .getText().toString();
                                        returnDetails[1] = tvBatch.getText()
                                                .toString();
                                        returnDetails[2] = tvQuantity.getText()
                                                .toString();
                                        returnDetails[3] = resalable;
                                        returnDetails[4] = txtCompanyReturns
                                                .getText().toString();
                                        returnDetails[5] = tvTotal.getText()
                                                .toString();
                                        returnDetails[6] = invoicedProducts[2];
                                        returnDetails[7] = invoicedProducts[7];
                                        returnDetails[8] = invoicedProducts[6];
                                        returnDetails[9] = tvFree.getText()
                                                .toString();
                                        returnDetails[10] = invoicedProducts[9];

                                        if (returnQuantities
                                                .containsKey(returnDetails[1])) {
                                            returnQuantities
                                                    .remove(returnDetails[1]);
                                            returnQuantities.put(
                                                    returnDetails[1],
                                                    returnDetails);
                                        } else {
                                            returnQuantities.put(
                                                    returnDetails[1],
                                                    returnDetails);
                                        }

                                    } else {
                                        Toast numberTooLarge = Toast
                                                .makeText(
                                                        ReturnInvoiceActivity.this,
                                                        "Please enter a number less than the total qty!",
                                                        Toast.LENGTH_SHORT);
                                        numberTooLarge.setGravity(Gravity.TOP,
                                                100, 100);
                                        numberTooLarge.show();
                                        txtResalable.setText("0");
                                    }
                                }
                            } catch (NumberFormatException numEx) {
                                Log.w("Invalid number", numEx.toString());
                                Toast invalidNumber = Toast.makeText(
                                        ReturnInvoiceActivity.this,
                                        "Invalid number!", Toast.LENGTH_SHORT);
                                invalidNumber.setGravity(Gravity.TOP, 100, 100);
                                invalidNumber.show();
                            }

                        }

                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                            // TODO Auto-generated method stub

                        }

                        public void afterTextChanged(Editable s) {
                            // TODO Auto-generated method stub

                        }
                    });

                    txtCompanyReturns.addTextChangedListener(new TextWatcher() {

                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            // TODO Auto-generated method stub
                            try {
                                String companyReturn = s.toString().trim();
                                if ((!companyReturn.isEmpty() && (!companyReturn
                                        .contentEquals("0")))) {

                                    int qty = Integer.parseInt(tvQuantity
                                            .getText().toString());
                                    int free = Integer.parseInt(tvFree
                                            .getText().toString());
                                    int total = qty + free;
                                    int cmp = Integer.parseInt(companyReturn);
                                    Log.w("the total", total + "");
                                    if (cmp <= total) {

                                        String[] returnDetails = new String[11];
                                        returnDetails[0] = tvProductDescription
                                                .getText().toString();
                                        returnDetails[1] = tvBatch.getText()
                                                .toString();
                                        returnDetails[2] = tvQuantity.getText()
                                                .toString();
                                        returnDetails[3] = txtResalable
                                                .getText().toString();
                                        returnDetails[4] = companyReturn;
                                        returnDetails[5] = tvTotal.getText()
                                                .toString();
                                        returnDetails[6] = invoicedProducts[2];
                                        returnDetails[7] = invoicedProducts[7];
                                        returnDetails[8] = invoicedProducts[6];
                                        returnDetails[9] = tvFree.getText()
                                                .toString();
                                        returnDetails[10] = invoicedProducts[9];

                                        if (returnQuantities
                                                .containsKey(returnDetails[1])) {
                                            returnQuantities
                                                    .remove(returnDetails[1]);
                                            returnQuantities.put(
                                                    returnDetails[1],
                                                    returnDetails);
                                        } else {
                                            returnQuantities.put(
                                                    returnDetails[1],
                                                    returnDetails);
                                        }

                                    } else {
                                        Toast numberTooLarge = Toast
                                                .makeText(
                                                        ReturnInvoiceActivity.this,
                                                        "Please enter a number less than the total qty!",
                                                        Toast.LENGTH_SHORT);
                                        numberTooLarge.setGravity(Gravity.TOP,
                                                100, 100);
                                        numberTooLarge.show();
                                        txtCompanyReturns.setText("0");
                                    }
                                }
                            } catch (NumberFormatException numEx) {
                                Log.w("Invalid number", numEx.toString());
                                Toast invalidNumber = Toast.makeText(
                                        ReturnInvoiceActivity.this,
                                        "Invalid number!", Toast.LENGTH_SHORT);
                                invalidNumber.setGravity(Gravity.TOP, 100, 100);
                                invalidNumber.show();
                            }
                        }

                        public void beforeTextChanged(CharSequence s,
                                                      int start, int count, int after) {
                            // TODO Auto-generated method stub

                        }

                        public void afterTextChanged(Editable s) {
                            // TODO Auto-generated method stub

                        }
                    });

                    count++;

                    tblInvoicedItems.addView(tr,
                            new TableLayout.LayoutParams(
                                    LayoutParams.FILL_PARENT,
                                    LayoutParams.WRAP_CONTENT));
                    // }
                }
             }

            if (count == 1) {
                Toast allProductsReturned = Toast.makeText(this,
                        "Products for this invoice have been returned",
                        Toast.LENGTH_SHORT);
                allProductsReturned.setGravity(Gravity.TOP, 100, 100);
                allProductsReturned.show();
            }
        } catch (Exception e) {
            Log.w("fuck una", "while populating table " + e.toString());
        }
    }

    private void setInitialData() {
        // TODO Auto-generated method stub


        Bundle extras = getIntent().getExtras();
        itineraryId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
        onTimeOrNot = extras.getString("onTimeOrNot");
        Itinerary itinerary = new Itinerary(this);

        outstandiing  = new DEL_Outstandiing(ReturnInvoiceActivity.this);
        invoiceNumberList = new ArrayList<>();
        invoiceNumberList =    outstandiing.loadOutSatingInvoiceNumber(pharmacyId);
        ArrayAdapter<String> invoiceListAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_list_item,invoiceNumberList);
        spInvoice.setAdapter(invoiceListAdapter);
        spInvoice.setOnItemSelectedListener(this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(itineraryId);
        itinerary.closeDatabase();

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary
                    .getItineraryDetailsForTemporaryCustomer(itineraryId);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", "
                    + itnDetails[4] + ", " + itnDetails[5];

            custName = itnDetails[0];
            tViewCustomerName.setText(itnDetails[0]);
            custAddress = address;
        } else {
            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();
            String[] customerDetails = customersObject
                    .getCustomerDetailsByPharmacyId(pharmacyId);
            customersObject.closeDatabase();
            tViewCustomerName.setText(customerDetails[5]);

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
            custName = customerDetails[5];
            custAddress = customerDetails[6];
            tViewCustomerName.setText(customerDetails[5]);
        }

        String systemDate = DateFormat.getDateInstance().format(new Date());

        tViewDate.setText(systemDate);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            finish();
            Intent itineraryListIntent = new Intent(
                    "com.halcyon.channelbridge.ITINERARYLIST");
            startActivity(itineraryListIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);
        outState.putSerializable("returnQuantities", returnQuantities);
        outState.putString("itineraryId", itineraryId);
        outState.putString("pharmacyId", pharmacyId);
        outState.putString("custName", custName);
        outState.putString("custAddress", custAddress);
        outState.putString("invoiceNo", invoiceNo);

        outState.putBoolean("saveBtnFlag", saveBtnFlag);


        selectedProductsArray = new ArrayList<SelectedProduct>();

        for (String[] returnProduct : invoiceData) {
            SelectedProduct product = new SelectedProduct();

            product.setProductId(returnProduct[0]);
            product.setQuantity(Integer.parseInt(returnProduct[1]));
            product.setProductCode(returnProduct[2]);
            product.setTotal(Double.parseDouble(returnProduct[3]));
            product.setProductBatch(returnProduct[4]);
            product.setExpiryDate(returnProduct[5]);
            product.setPrice(Double.parseDouble(returnProduct[6]));
            product.setTimeStamp(returnProduct[7]);
            product.setFree(Integer.parseInt(returnProduct[8]));
            product.setDiscount(Double.parseDouble(returnProduct[9]));
            //
            // Log.w("known Validate","################ :"+
            // product.getReturnValidated() + "");

            selectedProductsArray.add(product);
        }
        invoiceData = new ArrayList<String[]>();

        outState.putParcelableArrayList("selectedProductsArray", selectedProductsArray);

        Log.w("selectedProductsArray", "################ :" + selectedProductsArray.size() + "");
        Log.w("returnQuantities", "################ :" + returnQuantities.size() + "");
    }

    @SuppressWarnings("unchecked")
    private void setBundleData(Bundle bundlData) {

        returnQuantities = (HashMap<String, String[]>) bundlData
                .getSerializable("returnQuantities");
        itineraryId = bundlData.getString("itineraryId");
        pharmacyId = bundlData.getString("pharmacyId");
        custName = bundlData.getString("custName");
        custAddress = bundlData.getString("custAddress");
        invoiceNo = bundlData.getString("invoiceNo");

        saveBtnFlag = bundlData.getBoolean("saveBtnFlag");
        selectedProductsArray = bundlData.getParcelableArrayList("selectedProductsArray");

        Log.w("selectedProductsArray", "************* :" + selectedProductsArray.size() + "");
        Log.w("returnQuantities", "************* :" + returnQuantities.size() + "");

        invoiceData = new ArrayList<String[]>();
        for (SelectedProduct returns : selectedProductsArray) {
            String[] returnDetails = new String[10];
            returnDetails[0] = returns.getProductId();
            returnDetails[1] = String.valueOf(returns.getQuantity());
            returnDetails[2] = returns.getProductCode();
            returnDetails[3] = String.valueOf(returns.getTotal());
            returnDetails[4] = returns.getProductBatch();
            returnDetails[5] = returns.getExpiryDate();
            returnDetails[6] = String.valueOf(returns.getPrice());
            returnDetails[7] = returns.getTimeStamp();
            returnDetails[8] = String.valueOf(returns.getFree());
            returnDetails[9] = String.valueOf(returns.getDiscount());
            invoiceData.add(returnDetails);
        }

        selectedProductsArray = new ArrayList<SelectedProduct>();


    }


    private Boolean validateSave() {

        boolean flag = true;

        for (String[] element : invoiceData) {

            String[] returnQty = returnQuantities.get(element[0]);

            if (returnQty != null && returnQty.length > 0) {

                int normalQty = 0;
                int freeQty = 0;
                int reselQty = 0;
                int compQty = 0;


                if (element[1] != null && element[1].length() > 0) {
                    normalQty = Integer.parseInt(element[1]);
                }
                if (element[8] != null && element[8].length() > 0) {
                    freeQty = Integer.parseInt(element[8]);
                }
                if (returnQty[3] != null && returnQty[3].length() > 0) {
                    reselQty = Integer.parseInt(returnQty[3]);
                }
                if (returnQty[4] != null && returnQty[4].length() > 0) {
                    compQty = Integer.parseInt(returnQty[4]);
                }

                Log.w("flag %%%%%%%%%", "normalQty : " + normalQty);
                Log.w("flag %%%%%%%%%", "freeQty : " + freeQty);
                Log.w("flag %%%%%%%%%", "reselQty : " + reselQty);
                Log.w("flag %%%%%%%%%", "compQty : " + compQty);

                if (normalQty != reselQty + compQty) {
                    flag = false;
                }

            }
        }
        Log.w("flag %%%%%%%%%", "flag : " + flag);
        return flag;
    }

    private String GetGPS() {

        String GPS = "";

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            GPS = la + "-" + lo;

        }
        return GPS;
    }

    public void onLocationChanged(Location location) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String inVoNo = spInvoice.getSelectedItem().toString();

        if(iswebApprovalActive == false) {
            if (!saveBtnFlag) {

                try {
                    InvoicedProducts invoiceProductsObject = new InvoicedProducts(
                            this);
                    invoiceProductsObject.openReadableDatabase();
                    invoiceData = invoiceProductsObject
                            .getInvoiceDetailsForReturnsByInvoiceId(inVoNo);
                    invoiceProductsObject.closeDatabase();
                } catch (Exception e) {
                    Log.w("Error getting invoiced Item Data", e.toString());
                }
            }
        }else{

            invoiceData = dealerSalesController.getProductListForInvoice(inVoNo);

        }
       tblInvoicedItems.removeAllViews();
       populateInvoicedItemsTable(invoiceData, returnQuantities, true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class UploadProductReturnsTask extends AsyncTask<String, Integer, Integer> {

        private final Context context;

        public UploadProductReturnsTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {


        }

        protected void onProgressUpdate(Integer... progress) {
            setProgress(progress[0]);

        }

        protected void onPostExecute(Integer returnCode) {
            showDialog(returnCode);

        }

        @Override
        protected Integer doInBackground(String... params) {
            int returnValue = 1;

            Log.w("Log", "param result : " + params[0]);

            Log.w("Log", "loadProductRepStoreData result : starting ");

            if (isOnline()) {

                publishProgress(1);

                String timeStamp = new SimpleDateFormat("yyyy")
                        .format(new Date());

                // int year = new Date().getYear();

                ProductReturns rtnProdObject = new ProductReturns(ReturnInvoiceActivity.this);
                rtnProdObject.openReadableDatabase();

                List<String[]> rtnProducts = rtnProdObject
                        .getProductReturnsByStatus("false");
                rtnProdObject.closeDatabase();

                Log.w("Log", "rtnProducts size :  " + rtnProducts.size());

                for (String[] rtnProdData : rtnProducts) {

                    Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);
                    // Log.w("Log", "rtnProducts date :  " + rtnProdData[10]);

                    Products product = new Products(ReturnInvoiceActivity.this);
                    product.openReadableDatabase();
                    String[] productData = product
                            .getProductDetailsByProductCode(rtnProdData[1]);
                    product.closeDatabase();

                    ProductRepStore productRepStore = new ProductRepStore(
                            ReturnInvoiceActivity.this);
                    productRepStore.openReadableDatabase();
                    String[] productRepStor = productRepStore
                            .getProductDetailsByProductBatchAndProductCode(
                                    rtnProdData[2], rtnProdData[1]);
                    productRepStore.closeDatabase();

                    ArrayList<String[]> returnedProductList = new ArrayList<String[]>();

                    String[] invoiceDetails = new String[14];

                    invoiceDetails[0] = rtnProdData[1]; // Product
                    // code

                    Log.w("Log", "rtnProducts validated :  " + rtnProdData[13]);

                    Log.w("Log123", "rtnProducts Status :  " + rtnProdData[13]
                            + rtnProdData[13].equals("false") + "  "
                            + timeStamp);

                    // if (rtnProdData[13].equals("false")) {
                    // invoiceDetails[1] = timeStamp+rtnProdData[3]; // Invoice
                    // // Id
                    // }else{
                    invoiceDetails[1] = rtnProdData[3]; // Invoice
                    // Id
                    // }

                    String issueMode = rtnProdData[4];

//                    if (rtnProdData[4].equalsIgnoreCase("resalable")) {
//                        issueMode = "RS";
//                    } else if (rtnProdData[4].equalsIgnoreCase("company_returns")) {
//                        issueMode = "CR";
//                    }

                    invoiceDetails[2] = issueMode; // Issue mode
//					invoiceDetails[2] = "R"; // Issue mode
                    invoiceDetails[3] = rtnProdData[5]; // Normal
                    // qty
                    invoiceDetails[4] = changeDateFormat(rtnProdData[7]);
                    ; // Rtn date

                    Log.w("Log", "productRepStor[5] 3@@$@ :  "
                            + productRepStor[5]);

                    if (productRepStor[5] == null || productRepStor[5] == "") {
                        invoiceDetails[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
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

                    Log.w("Log", "Test ##### 11:  " + rtnProdData[11]);

                    Log.w("Log", "Test ##### :  " + rtnProdData[6]);

                    Log.w("Log",
                            "Test #####  bool :  "
                                    + (rtnProdData[6] != null && Integer
                                    .parseInt(rtnProdData[6]) > 0));

                    if (rtnProdData[6] != null
                            && Integer.parseInt(rtnProdData[6]) > 0) {

                        String[] invoiceDetailsFree = new String[14];

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
                        if(issueMode.equals("SR")){
                            invoiceDetailsFree[2] = "SF"; //
                        }else {
                            invoiceDetailsFree[2] = "CF"; //
                        }
                        // Issue mode
                        invoiceDetailsFree[3] = rtnProdData[6]; // Free qty
                        invoiceDetailsFree[4] = changeDateFormat(rtnProdData[7]); // Rtn
                        // date

                        Log.w("Log", "productRepStor[5] 3### :  "
                                + productRepStor[5]);

                        if (productRepStor[5] == null
                                || productRepStor[5] == "") {
                            invoiceDetailsFree[5] = changeDateFormat("2015-01-01 10:13:59.790"); // expire
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
                        Log.w("Log", "Test ##### 11:  " + rtnProdData[11]);

                        returnedProductList.add(invoiceDetailsFree);
                    }

                    publishProgress(2);
                    String responseArr = null;
                    while (responseArr == null) {
                        try {

                            SharedPreferences sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());
                            String deviceId = sharedPreferences.getString("DeviceId", "-1");
                            String repId = sharedPreferences.getString("RepId", "-1");

                            WebService webService = new WebService();
                            responseArr = webService
                                    .uploadProductReturnsDetails(
                                            deviceId,
                                            repId,
                                            returnedProductList);

                            Thread.sleep(100);

                        } catch (SocketException e) {
                            e.printStackTrace();
                            return 0;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return 0;
                        }

                    }

                    Log.w("Log",
                            "update data result : "
                                    + responseArr.contains("No Error"));
                    if (responseArr.contains("No Error")) {

                        Log.w("Log", "Update the iternarary status");

                        ProductReturns rtnProdObj = new ProductReturns(
                                ReturnInvoiceActivity.this);
                        rtnProdObj.openReadableDatabase();
                        rtnProdObj.setRtnProductsUploadedStatus(rtnProdData[0],
                                "true");
                        rtnProdObj.closeDatabase();

                        returnValue = 2;

                    }

                    Log.w("Log", "loadProductRepStoreData result : "
                            + responseArr);

                }

                if (rtnProducts.size() < 1) {

                    returnValue = 4;
                }

            } else {

                returnValue = 3;
            }
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(ReturnInvoiceActivity.this);
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

        Log.w("Date in Change date format: ", date);

        date = date.substring(0, 10);

        Log.w("Date in Change date format: ", date);

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String reformattedStr = "";
        Log.w("Date in Change date format: ", date);
        try {

            reformattedStr = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            Log.w("Date in Change date format: ParseException ", e.toString());
            e.printStackTrace();
        }
        Log.w("Date in Change date format: ", date);
        return reformattedStr;
    }


}
