package com.halcyon.channelbridge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.Entity.DealerSaleEntity;
import com.halcyon.Entity.ReturnHeaderEntity;
import com.halcyon.channelbridgebs.UploadProductReturnsTask;
import com.halcyon.channelbridgebs.UploadRetunHeaderTask;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.DealerSales;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.InvoicedProducts;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.ReturnHeader;
import com.halcyon.channelbridgedb.Sequence;

public class ReturnProductHistoryActivity extends Activity implements LocationListener, CompoundButton.OnCheckedChangeListener {
    Location location;
    double lat, lng;
    private LocationManager locationManager;
    String itineraryId, pharmacyId;
    TextView tViewDate, tViewCustomerName, tViewTotalReturns, tViewDiscount, tvCrediAmount, tvReturnNoH, tvHtotalAmount, tvHtotDiscount;
    EditText txtFree, edExpiryDateHistory, spinReturnQuantity, edDisNPercentage, edrPrice, tViewUnitPrice, edDisNvalue;
    Button btnAdd, btnCancel, btnSaveReturns, btnPrint, btnDeleteReturns;
    ImageButton iBtnClearSearch;
    private CheckBox cbHistory;
    AutoCompleteTextView txtProduct, spinBatches;
    TableLayout tblProductReturns;
    Spinner spinInvoiceNumber;
    ArrayList<String[]> products = new ArrayList<String[]>();
    String[] invoicedProductDetails = new String[10];
    String productId;
    boolean flagFromInvoiceGen = false;
    boolean isReturnSaved = false;
    String cash, credit, marketReturns, discount, needToPay, paymentOption, totalPrice, totalQuantity, cheque, invoiceNumber, issueMode;
    String returnDiscount;
    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();
    ArrayList<String[]> returnProducts = new ArrayList<String[]>();
    RadioGroup rGroupOptions;
    TableRow tr;
    String custName, custAddress;
    long maxFreeReturn = 0;
    boolean chequeEnabled = false;
    String collectionDate = "", releaseDate = "", chequeNumber = "", creditDuration = "";
    boolean saveBtnFlag = false;
    private String onTimeOrNot;
    DEL_Outstandiing outstandiing;
    ArrayList<String> invoiceNumberList;
    ArrayList<String> productList;
    Products productsObject;
    InvoicedProducts invoicedProducts;
    ArrayAdapter<String> batcAdapterList;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String sellingPrice = "0", purPrice = "0", retailPrice = "0";
    double retrunTotal = 0;
    double totalDiscount = 0;
    Calendar calendar;
    String pid = "", pPrice = "", totalValue;
    private String startTime = "";
    private String endTime = "";
    private Boolean iswebApprovalActive = true;
    ArrayList<ReturnProduct> returnProductsArray = new ArrayList<ReturnProduct>();
    private DealerSales dealerSalesController;

    boolean manualFreeEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_product_history);

		/*
         * Note to all Developers: The normal process is, when the user presses the add product button the products get added to the table
		 * After the products are added, when the user clicks on save, it is saved onto a parcellable array list
		 * The "Cancel" button becomes "Done".
		 * In the "done button onclick, the user checks if the returns are saved and then procedes the normal way :)"
		 * Cheers
		 */
        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval", true));
        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewDate = (TextView) findViewById(R.id.labelDate);
        tViewUnitPrice = (EditText) findViewById(R.id.edUnitPrice);
        tViewTotalReturns = (TextView) findViewById(R.id.tvReturnQty);
        //tViewDiscount = (TextView) findViewById(R.id.tvDiscount);
        txtProduct = (AutoCompleteTextView) findViewById(R.id.etProduct);
        btnCancel = (Button) findViewById(R.id.bCancel);
        btnAdd = (Button) findViewById(R.id.bAdd);
        btnSaveReturns = (Button) findViewById(R.id.bSaveReturn);
        btnPrint = (Button) findViewById(R.id.bPrint);
        btnDeleteReturns = (Button) findViewById(R.id.bDeleteReturns);
        rGroupOptions = (RadioGroup) findViewById(R.id.rgOptions);
        spinBatches = (AutoCompleteTextView) findViewById(R.id.sBatches);
        spinInvoiceNumber = (Spinner) findViewById(R.id.sInvoiceNumber);
        spinReturnQuantity = (EditText) findViewById(R.id.sReturnQty);
        txtFree = (EditText) findViewById(R.id.etFree);
        startTime = formatDate(new Date());
        edExpiryDateHistory = (EditText) findViewById(R.id.edExpiryDateHistory);
        //  iBtnClearSearch = (ImageButton) findViewById(R.id.ibClearSearch);
        tblProductReturns = (TableLayout) findViewById(R.id.tlItemsToRemove);
        tvCrediAmount = (TextView) findViewById(R.id.tvCrediAmountH);
        cbHistory = (CheckBox) findViewById(R.id.cbHistory);
        tvReturnNoH = (TextView) findViewById(R.id.tvReturnNoH);
        edDisNPercentage = (EditText) findViewById(R.id.edDisNPercentage);
        edrPrice = (EditText) findViewById(R.id.edrPrice);
        tvHtotalAmount = (TextView) findViewById(R.id.tvHtotalAmount);
        edDisNvalue = (EditText) findViewById(R.id.edDisNvalue);
        tvHtotDiscount = (TextView) findViewById(R.id.tvHtotDiscount);
        productsObject = new Products(this);
        txtFree.setText("0");
        tViewUnitPrice.setText("0");
        tViewTotalReturns.setText("0");
        btnPrint.setEnabled(false);
        btnDeleteReturns.setEnabled(false);
        spinReturnQuantity.setEnabled(false);
        productList = new ArrayList<>();
        invoicedProducts = new InvoicedProducts(ReturnProductHistoryActivity.this);
        dealerSalesController = new DealerSales(ReturnProductHistoryActivity.this);
        calendar = Calendar.getInstance();
        calendar.set(2020, 00, 01);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        edExpiryDateHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReturnProductHistoryActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();//2020,00,01
            }
        });
        cbHistory.setOnCheckedChangeListener(this);
        edDisNPercentage.setEnabled(false);
        edDisNvalue.setEnabled(false);
        spinBatches.setThreshold(1);
        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDataFromPreviousActivity();
        }

        updateLabel();
        if (saveBtnFlag) {
            btnSaveReturns.setEnabled(false);
            btnAdd.setEnabled(false);
            btnCancel.setText("Done");
            btnPrint.setEnabled(true);

        }


        // getDataForInvoiceSpinner();
        // getDataForProductsList();
        productsObject = new Products(this);
        productsObject.openReadableDatabase();

        Sequence sequence = new Sequence(ReturnProductHistoryActivity.this);
        sequence.openReadableDatabase();
        String inv = sequence.getLastRowId("return_header");
        sequence.closeDatabase();
        String year = new SimpleDateFormat("yyyy").format(new Date());
        int invNo = Integer.parseInt(inv) + 1;

        tvReturnNoH.setText(String.valueOf(invNo));

        List<String[]> tempArray = productsObject.getAllProducts();
        products.addAll(tempArray);
        productsObject.closeDatabase();


        GetGPS();
        outstandiing = new DEL_Outstandiing(ReturnProductHistoryActivity.this);
        invoiceNumberList = new ArrayList<>();
        invoiceNumberList = outstandiing.loadOutSatingInvoiceNumber(pharmacyId);
        ArrayAdapter<String> invoiceListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, invoiceNumberList);
        // invoiceListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinInvoiceNumber.setAdapter(invoiceListAdapter);
        setInitialData();

//        iBtnClearSearch.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                txtProduct.setText(null);
//            }
//        });

        edDisNPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtProduct.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                ArrayList<String> nullBatchList = new ArrayList<String>();
                ArrayList<String> nullInvoiceList = new ArrayList<String>();
                //setBatchListAdapter(nullBatchList);
                // setInvoiceNumberSpinnerAdapter(nullInvoiceList);
                //setInvoiceQuantitySpinnerAdapter("0");
                tViewUnitPrice.setText("0");
                //          try {
                if (cbHistory.isChecked() == false) {
                    for (String[] p : products) {
                        if (p[8].contentEquals(s.toString())) {
                            productId = p[2];
                            // getDataForBatchList(productId , pharmacyId);

                            pid = p[1];
                            sellingPrice = p[13];
                            purPrice = p[12];
                            retailPrice = p[14];
                            tViewUnitPrice.setText(sellingPrice);
                            edrPrice.setText(retailPrice);
                            spinReturnQuantity.setEnabled(true);
                            spinInvoiceNumber.setClickable(false);

                            setBatchList();
                        }
                    }
                } else {
                    String code = "";
                    code = productsObject.getProductCodeByName(s.toString());
                    productId = code;
                    if (iswebApprovalActive == false) {

                        String[] productDetails = new String[10];

                        if (!code.isEmpty()) {
                            try {
                                invoicedProducts.openReadableDatabase();
                                productDetails = invoicedProducts.getSelectedInvoiceProduct(spinInvoiceNumber.getSelectedItem().toString(), code);
                                invoicedProducts.closeDatabase();
                                spinBatches.setText(productDetails[2]);
                                tViewUnitPrice.setText(productDetails[6]);
                                spinReturnQuantity.setText(productDetails[3]);
                                spinReturnQuantity.setEnabled(true);
                                edDisNPercentage.setEnabled(true);
                                edDisNvalue.setEnabled(true);
                                // pid = productDetails[1];
                                spinInvoiceNumber.setClickable(false);
                            } catch (Exception e) {
                                Log.e("Error ->", e.toString());
                            }

                        }
                    } else {
                        if (!code.isEmpty()) {
                            DealerSaleEntity entity = dealerSalesController.getProductById(code, spinInvoiceNumber.getSelectedItem().toString());

                            spinBatches.setText(entity.getBatch());
                            tViewUnitPrice.setText(entity.getUnitPrice());
                            edrPrice.setText(entity.getrPrice());
                            spinReturnQuantity.setText("" + entity.getQty());
                            spinReturnQuantity.setEnabled(true);
                            edDisNPercentage.setEnabled(true);
                            edDisNvalue.setEnabled(true);
                            spinInvoiceNumber.setClickable(false);
                        }
                        // pid.
                    }


                }

//                } catch (Exception e) {
//                    Log.w("error getting product info", e.toString());
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        spinBatches.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                Object selectedBatch = spinBatches.getText().toString();
                // getDataForInvoiceSpinner(selectedBatch.toString(), pharmacyId);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spinInvoiceNumber.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                //  Object selectedInvoice = spinInvoiceNumber.getSelectedItem();
                boolean isChecked = cbHistory.isChecked();
                checkHistoryValidation(isChecked);
                double creditAmount = Double.parseDouble(loadCreditAmount(spinInvoiceNumber.getSelectedItem().toString()));
                tvCrediAmount.setText(String.format("%.2f", creditAmount));
                // getDataForInvoice(selectedInvoice.toString());

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        spinReturnQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String free = txtFree.getText().toString();
                String qty = s.toString();
                if (free.isEmpty()) {
                    free = "0";
                }
                if (qty.isEmpty()) {
                    qty = "0";
                }

                if (Integer.parseInt(qty) > 0) {
                    edDisNPercentage.setEnabled(true);
                    edDisNvalue.setEnabled(true);
                }
                int lineQty = Integer.parseInt(qty) + Integer.parseInt(free);
                tViewTotalReturns.setText("" + lineQty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        spinReturnQuantity.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//            public void onItemSelected(AdapterView<?> arg0, View arg1,
//                                       int arg2, long arg3) {
//                // TODO Auto-generated method stub
//                Object o = spinReturnQuantity.getSelectedItem();
//                int returnQty = Integer.parseInt(o.toString());
//
//                int free = 0;
//
//                try {
//                    free = Integer.parseInt(txtFree.getText().toString());
//                } catch (NumberFormatException e) {
//                    Log.w("ReturnPRoductHistory spinReturnQty", e.toString());
//                    free = 0;
//                }
//                int totalReturn = returnQty + free;
//                tViewTotalReturns.setText(String.valueOf(totalReturn));
//
//            }
//
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });

        txtFree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
//                int freeAmt = 0;
//                try {
//                    freeAmt = Integer.parseInt(s.toString());
//                } catch (NumberFormatException e) {
//                    Log.w("ReturnPRoductHistory spinReturnQty", e.toString());
//                    freeAmt = 0;
//                }
//                Object o = spinReturnQuantity.getText().toString();
//                int returnQty = Integer.parseInt(o.toString());
//
//                if (freeAmt <= maxFreeReturn) {
//                    tViewTotalReturns.setText(String.valueOf(returnQty + freeAmt));
//                } else {
//                    tViewTotalReturns.setText(String.valueOf(returnQty));
//                    txtFree.setText(String.valueOf(maxFreeReturn));
//                    Toast.makeText(ReturnProductHistoryActivity.this, "Free returns cannot be more than " + maxFreeReturn, Toast.LENGTH_SHORT).show();
//                }


                String qty = spinReturnQuantity.getText().toString();
                String free = s.toString();
                if (free.isEmpty()) {
                    free = "0";
                }
                if (qty.isEmpty()) {
                    qty = "0";
                }

                if (Integer.parseInt(free) > 0) {
                    edDisNPercentage.setEnabled(false);
                    edDisNvalue.setEnabled(false);
                }
                int lineQty = Integer.parseInt(qty) + Integer.parseInt(free);
                tViewTotalReturns.setText("" + lineQty);


            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        edDisNPercentage.addTextChangedListener(new TextWatcher() {
            double unitPrice = 0;
            int qty = 0;
            double total = 0;
            double dicountValue = 0;
            double discPercentage = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!tViewUnitPrice.getText().toString().isEmpty()) {
                    unitPrice = Double.parseDouble(tViewUnitPrice.getText().toString());
                }
                if (!spinReturnQuantity.getText().toString().isEmpty()) {
                    qty = Integer.parseInt(spinReturnQuantity.getText().toString());
                }
                if (!s.toString().isEmpty()) {
                    discPercentage = Double.parseDouble(s.toString());
                } else {
                    discPercentage = 0;
                }

                if (discPercentage > 0) {
                    txtFree.setText("0");
                    txtFree.setEnabled(false);
                }
                total = unitPrice * qty;
                dicountValue = (total * discPercentage) / 100;
                edDisNvalue.setText(String.format("%.2f", dicountValue));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edDisNvalue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String disValue = s.toString();
                double value = 0;
                if (disValue.isEmpty()) {
                    value = 0;
                } else {
                    value = Double.parseDouble(disValue);
                }

                if (value > 0) {
                    txtFree.setText("0");
                    txtFree.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!returnProducts.isEmpty()) {
                    if (isReturnSaved) {
                        if (flagFromInvoiceGen) {
                            Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                            Bundle bundleToView = new Bundle();
                            bundleToView.putString("Id", itineraryId);
                            bundleToView.putString("PharmacyId", pharmacyId);
                            bundleToView.putString("Cash", cash);
                            bundleToView.putString("Credit", credit);
                            bundleToView.putString("Cheque", cheque);
                            bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                            if (chequeEnabled) {
                                bundleToView.putString("ChequeNumber", chequeNumber);
                                bundleToView.putString("CollectionDate", collectionDate);
                                bundleToView.putString("ReleaseDate", releaseDate);
                            }
                            bundleToView.putString("MarketReturns", marketReturns);
                            bundleToView.putString("Discount", discount);
                            bundleToView.putString("NeedToPay", needToPay);
                            bundleToView.putString("PaymentOption", paymentOption);
                            bundleToView.putString("TotalPrice", totalPrice);
                            bundleToView.putString("TotalQuantity", totalQuantity);
                            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                            bundleToView.putString("TotalValue", totalValue);
                            startInvoiceGen2.putExtras(bundleToView);
                            startActivity(startInvoiceGen2);
                            finish();
                        } else {
                            Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                            finish();
                            startActivity(startItinerary);
                        }
                    } else {
                        Builder alertNotSaved = new AlertDialog.Builder(ReturnProductHistoryActivity.this)
                                .setTitle("Warning")
                                .setMessage("Changes have not been saved. Do you want to save and exit?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                isReturnSaved = saveReturns();
                                                if (isReturnSaved) {

                                                    btnPrint.setEnabled(true);
                                                    btnSaveReturns.setEnabled(false);
                                                    btnCancel.setText("Done");

                                                    if (flagFromInvoiceGen) {
                                                        Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                                                        Bundle bundleToView = new Bundle();
                                                        bundleToView.putString("Id", itineraryId);
                                                        bundleToView.putString("PharmacyId", pharmacyId);
                                                        bundleToView.putString("Cash", cash);
                                                        bundleToView.putString("Credit", credit);
                                                        bundleToView.putString("Cheque", cheque);
                                                        bundleToView.putString(" onTimeOrNot", onTimeOrNot);
                                                        bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                                                        if (chequeEnabled) {
                                                            bundleToView.putString("ChequeNumber", chequeNumber);
                                                            bundleToView.putString("CollectionDate", collectionDate);
                                                            bundleToView.putString("ReleaseDate", releaseDate);
                                                        }
                                                        bundleToView.putString("MarketReturns", marketReturns);
                                                        bundleToView.putString("Discount", discount);
                                                        bundleToView.putString("NeedToPay", needToPay);
                                                        bundleToView.putString("PaymentOption", paymentOption);
                                                        bundleToView.putString("TotalPrice", totalPrice);
                                                        bundleToView.putString("TotalQuantity", totalQuantity);
                                                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                        bundleToView.putString("TotalValue", totalValue);
                                                        startInvoiceGen2.putExtras(bundleToView);
                                                        startActivity(startInvoiceGen2);
                                                        finish();
                                                    } else {
                                                        Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                                        finish();
                                                        startActivity(startItinerary);
                                                    }
                                                }
                                            }
                                        })
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        return;

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        if (flagFromInvoiceGen) {
                                            Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                                            Bundle bundleToView = new Bundle();
                                            bundleToView.putString("Id", itineraryId);
                                            bundleToView.putString("PharmacyId", pharmacyId);
                                            bundleToView.putString("Cash", cash);
                                            bundleToView.putString("Credit", credit);
                                            bundleToView.putString("Cheque", cheque);
                                            bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                                            if (chequeEnabled) {
                                                bundleToView.putString("ChequeNumber", chequeNumber);
                                                bundleToView.putString("CollectionDate", collectionDate);
                                                bundleToView.putString("ReleaseDate", releaseDate);
                                            }
                                            bundleToView.putString("MarketReturns", marketReturns);
                                            bundleToView.putString("Discount", discount);
                                            bundleToView.putString("NeedToPay", needToPay);
                                            bundleToView.putString("PaymentOption", paymentOption);
                                            bundleToView.putString("TotalPrice", totalPrice);
                                            bundleToView.putString("TotalQuantity", totalQuantity);
                                            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                            bundleToView.putString("TotalValue", totalValue);
                                            startInvoiceGen2.putExtras(bundleToView);
                                            startActivity(startInvoiceGen2);
                                            finish();
                                        } else {
                                            Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                            finish();
                                            startActivity(startItinerary);
                                        }
                                    }
                                });
                        alertNotSaved.show();
                    }
                } else {
                    if (flagFromInvoiceGen) {
                        Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                        Bundle bundleToView = new Bundle();
                        bundleToView.putString("Id", itineraryId);
                        bundleToView.putString("PharmacyId", pharmacyId);
                        bundleToView.putString("Cash", cash);
                        bundleToView.putString("Credit", credit);
                        bundleToView.putString("Cheque", cheque);
                        bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                        if (chequeEnabled) {
                            bundleToView.putString("ChequeNumber", chequeNumber);
                            bundleToView.putString("CollectionDate", collectionDate);
                            bundleToView.putString("ReleaseDate", releaseDate);
                        }
                        bundleToView.putString("MarketReturns", marketReturns);
                        bundleToView.putString("Discount", discount);
                        bundleToView.putString("NeedToPay", needToPay);
                        bundleToView.putString("PaymentOption", paymentOption);
                        bundleToView.putString("TotalPrice", totalPrice);
                        bundleToView.putString("TotalQuantity", totalQuantity);
                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                        bundleToView.putString("TotalValue", totalValue);
                        startInvoiceGen2.putExtras(bundleToView);
                        startActivity(startInvoiceGen2);
                        finish();
                    } else {
                        Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                        finish();
                        startActivity(startItinerary);
                    }
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String unitPrice = tViewUnitPrice.getText().toString();
                String returnQty = spinReturnQuantity.getText().toString();
                String dicountValue = edDisNvalue.getText().toString();
                String credit = tvCrediAmount.getText().toString();
                if (dicountValue.isEmpty()) {
                    dicountValue = "0";
                }
                if (credit.isEmpty()) {
                    credit = "0";
                }
                if (returnQty.isEmpty()) {
                    returnQty = "0";
                }
                double retVal = 0.0;

                retVal = (Double.parseDouble(unitPrice) * Double.parseDouble(returnQty)) - Double.parseDouble(dicountValue);


                double reTot = retrunTotal;
                double tempTotal = reTot + retVal;

                if (flagFromInvoiceGen == true) {


                    if (Double.parseDouble(totalValue) < tempTotal) {


                        Builder alertNotSaved = new AlertDialog.Builder(ReturnProductHistoryActivity.this)
                                .setTitle("Warning")
                                .setMessage("Total return amount is greater than total invoices amount.")
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });

                        alertNotSaved.show();

                    } else {



                        if (tempTotal > Double.parseDouble(credit)) {
                            Builder alertNotSaved = new AlertDialog.Builder(ReturnProductHistoryActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("Total amount is greater than credit amount.Do you want to proceed?")
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    // TODO Auto-generated method stub
                                                    saveRturnWithProceed();

                                                }
                                            })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            dialog.dismiss();
                                        }
                                    });
                            alertNotSaved.show();
                        } else {
                            saveRturnWithProceed();
                        }
                    }


                } else {
                    if (tempTotal > Double.parseDouble(credit)) {
                        Builder alertNotSaved = new AlertDialog.Builder(ReturnProductHistoryActivity.this)
                                .setTitle("Warning")
                                .setMessage("Total amount is greater than credit amount.Do you want to proceed?")
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                saveRturnWithProceed();

                                            }
                                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        dialog.dismiss();
                                    }
                                });
                        alertNotSaved.show();
                    } else {
                        saveRturnWithProceed();
                    }
                }


                // saveRturnWithProceed();


            }
        });

        btnSaveReturns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                isReturnSaved = saveReturns();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String deviceId = sharedPreferences.getString("DeviceId", "-1");
                String repId = sharedPreferences.getString("RepId", "-1");
                new UploadRetunHeaderTask(ReturnProductHistoryActivity.this, repId, deviceId).execute();

                if (isReturnSaved) {
                    new UploadProductReturnsTask(ReturnProductHistoryActivity.this).execute("1");
                    btnPrint.setEnabled(true);
                    btnSaveReturns.setEnabled(false);
                    saveBtnFlag = true;
                    btnAdd.setEnabled(false);
                    btnCancel.setText("Done");
                }

            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {

                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    boolean prePrintInvoiceFormatEnabled = sharedPreferences.getBoolean(
                            "cbPrefPrePrintInvoice", true);
                    String repId = sharedPreferences.getString("RepId", "-1");


                    Date dateObj = new Date();

                    String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);

                    String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                    boolean flag = true;


                    Reps reps = new Reps(ReturnProductHistoryActivity.this);
                    reps.openReadableDatabase();
                    ArrayList<String> delearDetails = reps.getRepDetailsForPrinting(repId);
                    reps.closeDatabase();
                    int customerNameRemain = 0;
                    int addressRemain = 0;
                    String dealerName = delearDetails.get(1).trim();
                    String dealerCity = delearDetails.get(2).trim();
                    String dealerTel = delearDetails.get(3).trim();


                    String add[] = dealerCity.split("@");
                    String addres[] = add[0].split(",");

                    String telfax = add[1];
                    String delerefax = null;
                    System.out.println("dddddddddddddd : " + add[1]);

                    String delereContactDetails[] = telfax.split("$");
                    String delereNumber = delereContactDetails[0].trim();

                    try {
                        delerefax = delereContactDetails[1].trim();
                    } catch (Exception ex) {
                        delereNumber = delereNumber.substring(0, (delereNumber.length() - 1));
                        delerefax = "";
                    }


                    if (dealerName.length() > 18) {
                        dealerName = dealerName.substring(0, 18);
                    }

                    if (dealerCity.length() > 18) {
                        dealerCity = dealerCity.substring(0, 18);
                    }
                    if (custName.length() > 24) {
                        custName = custName.substring(0, 25);
                    } else {
                        customerNameRemain = 25 - custName.length();
                    }
                    customerNameRemain = customerNameRemain + 1;
                    for (int i = 0; i <= customerNameRemain; i++) {
                        custName = custName + " ";
                    }

                    if (custAddress.length() > 24) {
                        custAddress = custAddress.substring(0, 25);
                    } else {
                        addressRemain = 25 - custAddress.length();
                    }
                    addressRemain = addressRemain + 1;
                    for (int i = 0; i <= addressRemain; i++) {
                        custAddress = custAddress + " ";
                    }

                    String repTelNo = "";
                    for (int i = 0; i <= 26; i++) {
                        repTelNo = repTelNo + " ";
                    }
                    repTelNo = repTelNo + dealerTel;

                    Customers cus = new Customers(ReturnProductHistoryActivity.this);
                    cus.openReadableDatabase();
                    String phone = cus.getCustomerPhoneByPharmacyId(pharmacyId);
                    if (phone.equals("0")) {
                        CustomersPendingApproval pen = new CustomersPendingApproval(ReturnProductHistoryActivity.this);
                        pen.openReadableDatabase();
                        phone = pen.getCustomerPhoneByPharmacyId(pharmacyId);

                    } else {

                    }

                    int custnameLength = custName.length();
                    int addressLength = custAddress.length();
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

               /* for (int i = 0; i < 74 - delerefax.length(); i++) {
                    delerFaxMargin = delerFaxMargin + " ";
                }*/


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

                    String headerData = "\n";
                    headerData = headerData + "                                      RETURN NOTE\n";
                    headerData = headerData + nameMargin + dealerName + "\n";
                    headerData = headerData + delerAdrresMarginone + addres[0] + "\n";
                    headerData = headerData + delerAdrresMargintwo + finalAddress + "\n";
                    headerData = headerData + delerPhoneMargin + delereNumber + "\n";
                    headerData = headerData + delerFaxMargin + delerefax + "\n";
                    headerData = headerData + "CUSTOMER ADDRESS \n";
                    headerData = headerData + custName + cusMargin + "Date:" + date + "\n";
                    headerData = headerData + custAddress + addressMargin + "\n";
                    headerData = headerData + "TEL : " + phone + "\n";
                    headerData = headerData + "\n";
                    headerData = headerData + "No Description               Batch    Price   Qty   Free   Discount  Ammount  \n";
                    headerData = headerData + "--------------------------------------------------------------------------------";
                    headerData = headerData + "\n";

                    String printData = "";

                    double totalPrice = 0.0;
                    int count = 0;
                    int totalItems = 0;
//				            product.setDescription(returnProduct[1]);
//							product.setBatch(returnProduct[2]);
//							product.setInvoiceNumber(Integer.parseInt(returnProduct[0]));
//							product.setQuantity(Integer.parseInt(returnProduct[4]));
//							product.setFree(Integer.parseInt(returnProduct[8]));
//							product.setUnitPrice(Double.parseDouble(returnProduct[3]));
//							product.setIssueMode(returnProduct[7]);
//							product.setReturnValue(Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]));
//							product.setProductId(returnProduct[6]);
//							product.setReturnValidated("true");
//							product.setDiscount(Double.parseDouble(returnProduct[9]));
                    for (String[] returnProduct : returnProducts) {

                        if (flag) {

                            if (printData.length() > 1) {
                                printData = printData + "\n\n\n\n\n\n\n";
                            }

                            printData = printData + headerData;
                            flag = false;
                        }

                        String productDescription = returnProduct[1];
                        String batch = returnProduct[2];

                        int totalQty = 0;

                        if (returnProduct[4] != null && returnProduct[4] != "null") {
                            totalQty = totalQty + Integer.parseInt(returnProduct[4]);
                        }
                        if (returnProduct[8] != null && returnProduct[8] != "null") {
                            totalQty = totalQty + Integer.parseInt(returnProduct[8]);
                        }

                        String price = returnProduct[3];
                        String qty = String.valueOf(returnProduct[4]);
//				            	double totalVal = Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]);
                        String total = returnProduct[5];
                        String free = returnProduct[8];
                        String discount = returnProduct[15];


                        int batchRemain = 0;
                        int quantityRemain = 0;
                        int unitPriceRemain = 0;
                        int valueRemain = 0;
                        int freeRemain = 0;
                        int discountRemain = 0;


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

                        if (batch.length() > 7) {
                            batch = batch.substring(0, 7);
                        } else {
                            batchRemain = 7 - batch.length();
                        }
                        for (int i = 0; i <= batchRemain; i++) {
                            batch = " " + batch;
                        }


                        if (qty.length() > 6) {
                            qty = qty.substring(0, 6);
                        } else if (qty.length() < 6) {
                            quantityRemain = 5 - qty.length();
                        }
                        for (int i = 0; i <= quantityRemain; i++) {
                            qty = " " + qty;
                        }

                        if (free.length() > 6) {
                            free = free.substring(0, 6);
                        } else if (free.length() < 6) {
                            freeRemain = 5 - free.length();
                        }
                        for (int i = 0; i <= freeRemain; i++) {
                            free = " " + free;
                        }

                        //
                        if (discount.length() > 9) {
                            discount = discount.substring(0, 9);
                        } else if (discount.length() < 9) {
                            discountRemain = 8 - discount.length();
                        }
                        for (int i = 0; i <= discountRemain; i++) {
                            discount = " " + discount;
                        }


                        if (price.length() > 9) {
                            price = price.substring(0, 9);
                        } else if (price.length() < 9) {
                            unitPriceRemain = 8 - price.length();
                        }

                        for (int i = 0; i <= unitPriceRemain; i++) {
                            price = " " + price;
                        }


                        if (total.length() > 12) {
                            total = total.substring(0, 12);
                        } else if (total.length() < 12) {
                            valueRemain = 11 - total.length();
                        }
                        for (int i = 0; i <= valueRemain; i++) {
                            total = " " + total;
                        }

                        printData = printData + productDescription + batch + price + qty + free + discount + total;

                        printData = printData + "\n\n";


                        totalPrice = totalPrice + Double.parseDouble(returnProduct[5]);
                        totalItems = totalItems + totalQty;

                        count++;
                        Log.w("COUNT", count + "lines");


                        if (count == 25) {
                            flag = true;
                            count = 0;
                        }
                    }

                    printData = printData + "--------------------------------------------";
                    printData = printData + "\n\n";


                    String totalPriceValue = String.format("%.2f", totalPrice);

                    String footerData = "";

                    if ((count + 7) < 30) {

                        footerData = footerData + "Total Ret : " + totalPriceValue + "\n";
                        footerData = footerData + "Items     : " + totalItems + "\n";

                        footerData = footerData + "\n\n";
                        footerData = footerData + "-----------------------------\n";
                        footerData = footerData + "Customer Signature & Seal\n\n";
                        footerData = footerData + "Technical Advice - 071 44 44 700 \n\n";
                        footerData = footerData + "Print Date & Time : " + printDateTime + "\n\n";

                        footerData = footerData
                                + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                        printData = printData + footerData;

                    } else {

                        int k = 30 - count;


                        footerData = footerData + "Total Ret : " + totalPriceValue + "\n";
                        footerData = footerData + "Items     : " + totalItems + "\n";

                        footerData = footerData + "\n\n";
                        footerData = footerData + "-----------------------------\n";
                        footerData = footerData + "  Customer Signature & Seal\n\n";
                        footerData = footerData + "Technical Advice - 071 44 44 700 \n\n";
                        footerData = footerData + "Print Date & Time : " + printDateTime + "\n\n";

                        footerData = footerData
                                + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                        for (int i = 0; i <= k; i++) {
                            printData = printData + "\n";
                        }

                        printData = printData + headerData;
                        printData = printData + footerData;

                    }


                    Log.w("printData 1  ", printData);
                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("PrintData", printData);

                    Intent activityIntent = new Intent(
                            getApplicationContext(), PrintUtility.class);
                    activityIntent.putExtras(bundleToView);
                    startActivityForResult(activityIntent, 0);


                } catch (Exception e) {
                    Log.w("EROOR printing", e.toString());
                }
            }
        });

    }

    private void populateTable(final ArrayList<String[]> returnProducts) {
        // TODO Auto-generated method stub

        TableRow trHeaders = new TableRow(this);
        trHeaders.setId(0);
        trHeaders.setPadding(0, 3, 0, 3);
        trHeaders.setBackgroundColor(Color.parseColor("#d3d3d3"));
        trHeaders.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        tblProductReturns.setShrinkAllColumns(true);

        TextView labelInvoiceNumber = new TextView(this);
        labelInvoiceNumber.setId(1000);
        labelInvoiceNumber.setText("Inv. #");
        labelInvoiceNumber.setGravity(Gravity.LEFT);
        labelInvoiceNumber.setTextColor(Color.BLACK);
        labelInvoiceNumber.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelInvoiceNumber);

        TextView labelDescription = new TextView(this);
        labelDescription.setId(1001);
        labelDescription.setText("Pr. Desc.");
        labelDescription.setGravity(Gravity.LEFT);
        labelDescription.setTextColor(Color.BLACK);
        labelDescription.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelDescription);

        TextView labelBatch = new TextView(this);
        labelBatch.setId(1002);
        labelBatch.setText("Batch");
        labelBatch.setGravity(Gravity.LEFT);
        labelBatch.setTextColor(Color.BLACK);
        labelBatch.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelBatch);

        TextView labelUnitPrice = new TextView(this);
        labelUnitPrice.setId(1003);
        labelUnitPrice.setText("Unit Price");
        labelUnitPrice.setGravity(Gravity.LEFT);
        labelUnitPrice.setTextColor(Color.BLACK);
        labelUnitPrice.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelUnitPrice);

        TextView labelReturnsQty = new TextView(this);
        labelReturnsQty.setId(1004);
        labelReturnsQty.setText("Returns");
        labelReturnsQty.setGravity(Gravity.LEFT);
        labelReturnsQty.setTextColor(Color.BLACK);
        labelReturnsQty.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelReturnsQty);

        TextView labelreturnsValue = new TextView(this);
        labelreturnsValue.setId(1005);
        labelreturnsValue.setText("Ret. Value");
        labelreturnsValue.setGravity(Gravity.LEFT);
        labelreturnsValue.setTextColor(Color.BLACK);
        labelreturnsValue.setTypeface(null, Typeface.BOLD);
        trHeaders.addView(labelreturnsValue);

        tblProductReturns.addView(trHeaders,
                new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));


        try {
            int count = 0;
            for (final String[] itemsToReturn : returnProducts) {

                tr = new TableRow(this);
                tr.setId(count);
                tr.setPadding(0, 3, 0, 3);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);

                }

//				1 - invoiceNumber
//				2 - description
//				3 - batch
//				4 - unitPrice
//				5 - returnQty
//				6 - returnValue

                TextView tvInvoiceNumber = new TextView(this);
                tvInvoiceNumber.setId(1000 + count);
                tvInvoiceNumber.setText(itemsToReturn[0]);
                tvInvoiceNumber.setGravity(Gravity.LEFT);
                tvInvoiceNumber.setTextColor(Color.WHITE);
                tr.addView(tvInvoiceNumber);

                TextView tvDescription = new TextView(this);
                tvDescription.setId(1000 + count);
                tvDescription.setText(itemsToReturn[1]);
                tvDescription.setGravity(Gravity.LEFT);
                tvDescription.setTextColor(Color.WHITE);
                tr.addView(tvDescription);

                TextView tvBatch = new TextView(this);
                tvBatch.setId(1000 + count);
                tvBatch.setText(itemsToReturn[2]);
                tvBatch.setGravity(Gravity.LEFT);
                tvBatch.setTextColor(Color.WHITE);
                tr.addView(tvBatch);

                TextView tvUnitPrice = new TextView(this);
                tvUnitPrice.setId(1000 + count);
                tvUnitPrice.setText(itemsToReturn[3]);
                tvUnitPrice.setGravity(Gravity.LEFT);
                tvUnitPrice.setTextColor(Color.WHITE);
                tr.addView(tvUnitPrice);

                TextView tvReturnsQty = new TextView(this);
                tvReturnsQty.setId(1000 + count);
                tvReturnsQty.setText(String.valueOf(Integer.parseInt(itemsToReturn[4]) + Integer.parseInt(itemsToReturn[8])));
                tvReturnsQty.setGravity(Gravity.LEFT);
                tvReturnsQty.setTextColor(Color.WHITE);
                tr.addView(tvReturnsQty);

                TextView returnsValue = new TextView(this);
                returnsValue.setId(1000 + count);
                returnsValue.setText(itemsToReturn[5]);
                returnsValue.setGravity(Gravity.LEFT);
                returnsValue.setTextColor(Color.WHITE);
                tr.addView(returnsValue);

                tr.setOnLongClickListener(new View.OnLongClickListener() {
                    @SuppressWarnings("static-access")
                    public boolean onLongClick(final View v) {
                        // TODO Auto-generated method stub

                        if (!saveBtnFlag) {
                            Vibrator longPressVibe = (Vibrator) getApplication().getSystemService(getApplication().VIBRATOR_SERVICE);
                            longPressVibe.vibrate(50);
                            v.setBackgroundColor(Color.parseColor("#0099CC"));
                            AlertDialog.Builder returnLongClick = new AlertDialog.Builder(ReturnProductHistoryActivity.this);
                            returnLongClick.setTitle("Choose what you want to do:")
                                    .setItems(new String[]{"Delete"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    TableRow selectedRow = (TableRow) v;
                                                    TextView dynaReturn = (TextView) selectedRow.getChildAt(5);
                                                    double total = 0;
                                                    double deDuctValue = 0;
                                                    if (!tvHtotalAmount.getText().toString().isEmpty()) {
                                                        total = Double.parseDouble(tvHtotalAmount.getText().toString());
                                                    }

                                                    deDuctValue = Double.parseDouble(dynaReturn.getText().toString());
                                                    total -= deDuctValue;
                                                    String[] deleteItem = returnProducts.get(v.getId());
                                                    totalDiscount -= Double.parseDouble(deleteItem[9]);
                                                    Log.i("del -d->", deleteItem[9].toString());
                                                    tvHtotalAmount.setText(String.format("%.2f", total));
                                                    tvHtotDiscount.setText(String.format("%.2f", totalDiscount));
                                                    returnProducts.remove(v.getId());
                                                    tblProductReturns.removeAllViews();
                                                    populateTable(returnProducts);
                                                    break;
                                            }
                                        }
                                    });
                            returnLongClick.setOnCancelListener(new OnCancelListener() {

                                public void onCancel(DialogInterface dialog) {
                                    // TODO Auto-generated method stub
                                    tblProductReturns.removeAllViews();
                                    populateTable(returnProducts);
                                }
                            });
                            String[] temp = returnProducts.get(v.getId());
                            returnLongClick.setTitle("Choose what you want to do: " + temp[1]);
                            returnLongClick.show();

                        }
                        return false;
                    }
                });
                count++;
                tblProductReturns.addView(tr,
                        new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));
            }
        } catch (Exception e) {

        }
    }

    private void getDataFromPreviousActivity() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();

        try {
            itineraryId = extras.getString("Id");
            pharmacyId = extras.getString("PharmacyId");
            cash = extras.getString("Cash");
            credit = extras.getString("Credit");
            cheque = extras.getString("Cheque");
            marketReturns = extras.getString("MarketReturns");
            discount = extras.getString("Discount");
            needToPay = extras.getString("NeedToPay");
            totalPrice = extras.getString("TotalPrice");
            totalQuantity = extras.getString("TotalQuantity");
            invoiceNumber = extras.getString("InvoiceNumber");
            onTimeOrNot = extras.getString("onTimeOrNot");
            manualFreeEnable=extras.getBoolean("ManualFreeEnable");


            selectedProductsArray = extras.getParcelableArrayList("SelectedProducts");
            if (extras.containsKey("ReturnProducts")) {
                returnProductsArray = extras.getParcelableArrayList("ReturnProducts");
            }

            if (extras.containsKey("InvoiceNumber")) {
                flagFromInvoiceGen = true;
            }

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);

            totalValue = extras.getString("TotalValue");

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

                Log.w("cheque details", chequeNumber + " # " + collectionDate + " # " + releaseDate);

            }


        } catch (Exception e) {
            Log.w("Return Product: Error Getting Initial Data", e.toString());
        }
    }

    private void setInitialData() {
        // TODO Auto-generated method stub

        Itinerary itinerary = new Itinerary(this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(itineraryId);
        itinerary.closeDatabase();

        if (status.contentEquals("true")) {
            itinerary.openReadableDatabase();
            String[] itnDetails = itinerary.getItineraryDetailsForTemporaryCustomer(itineraryId);
            itinerary.closeDatabase();
            String address = itnDetails[2] + ", " + itnDetails[3] + ", " + itnDetails[4] + ", " + itnDetails[5];

            custName = itnDetails[0];
            tViewCustomerName.setText(itnDetails[0]);
            custAddress = address;
        } else {
            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();
            String[] customerDetails = customersObject.getCustomerDetailsByPharmacyId(pharmacyId);
            customersObject.closeDatabase();
            tViewCustomerName.setText(customerDetails[5]);
            custName = customerDetails[5];
            custAddress = customerDetails[6];
        }

        String systemDate = DateFormat.getDateInstance().format(new Date());

        tViewDate.setText(systemDate);

        if (!returnProductsArray.isEmpty()) {

            returnProducts = new ArrayList<String[]>();

            for (ReturnProduct returns : returnProductsArray) {
                String[] returnDetails = new String[10];

                returnDetails[0] = String.valueOf(returns.getInvoiceNumber());
                returnDetails[1] = returns.getDescription();
                returnDetails[2] = returns.getBatch();
                returnDetails[3] = String.valueOf(returns.getUnitPrice());
                returnDetails[4] = String.valueOf(returns.getQuantity());


                Log.w("Return Product: 123", "getDiscount : " + returns.getDiscount());
                Log.w("Return Product: 123", "getQuantity : " + returns.getQuantity());
                Log.w("Return Product: 123", "getUnitPrice : " + returns.getUnitPrice());

                double value = 0.0;
                if (returns.getDiscount() > 0) {
                    double normalValue = returns.getQuantity() * returns.getUnitPrice();
                    value = normalValue - ((normalValue / 100) * returns.getDiscount());
                } else {
                    value = returns.getQuantity() * returns.getUnitPrice();
                }

                Log.w("Return Product: 123", "value : " + value);

                returnDetails[5] = String.valueOf(value);
                returnDetails[6] = returns.getProductId();
                returnDetails[7] = returns.getIssueMode();
                returnDetails[8] = String.valueOf(returns.getFree());
                returnDetails[9] = String.valueOf(returns.getDiscount());
                returnProducts.add(returnDetails);
            }

        }

        if (!returnProducts.isEmpty()) {
            tblProductReturns.removeAllViews();
            populateTable(returnProducts);

            ArrayList<String> nullBatchList = new ArrayList<String>();
            ArrayList<String> nullInvoiceList = new ArrayList<String>();
            // setBatchListAdapter(nullBatchList);
            //setInvoiceNumberSpinnerAdapter(nullInvoiceList);
            setInvoiceQuantitySpinnerAdapter("0");
            tViewUnitPrice.setText("0");
            txtProduct.setText(null);
        }
        Boolean isChecked = cbHistory.isChecked();
        checkHistoryValidation(isChecked);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (!returnProducts.isEmpty()) {
                if (isReturnSaved) {
                    if (flagFromInvoiceGen) {
                        Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                        Bundle bundleToView = new Bundle();
                        bundleToView.putString("Id", itineraryId);
                        bundleToView.putString("PharmacyId", pharmacyId);
                        bundleToView.putString("Cash", cash);
                        bundleToView.putString("Credit", credit);
                        bundleToView.putString("Cheque", cheque);
                        bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                        if (chequeEnabled) {
                            bundleToView.putString("ChequeNumber", chequeNumber);
                            bundleToView.putString("CollectionDate", collectionDate);
                            bundleToView.putString("ReleaseDate", releaseDate);
                        }
                        bundleToView.putString("MarketReturns", marketReturns);
                        bundleToView.putString("Discount", discount);
                        bundleToView.putString("NeedToPay", needToPay);
                        bundleToView.putString("PaymentOption", paymentOption);
                        bundleToView.putString("TotalPrice", totalPrice);
                        bundleToView.putString("TotalQuantity", totalQuantity);
                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                        bundleToView.putString("TotalValue", totalValue);
                        startInvoiceGen2.putExtras(bundleToView);
                        startActivity(startInvoiceGen2);
                        finish();
                    } else {
                        Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                        finish();
                        startActivity(startItinerary);
                    }
                } else {
                    Builder alertNotSaved = new AlertDialog.Builder(ReturnProductHistoryActivity.this)
                            .setTitle("Warning")
                            .setMessage("Changes have not been saved. Do you want to save and exit?")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            isReturnSaved = saveReturns();
                                            if (isReturnSaved) {

                                                btnPrint.setEnabled(true);
                                                btnSaveReturns.setEnabled(false);
                                                btnCancel.setText("Done");

                                                if (flagFromInvoiceGen) {
                                                    Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                                                    Bundle bundleToView = new Bundle();
                                                    bundleToView.putString("Id", itineraryId);
                                                    bundleToView.putString("PharmacyId", pharmacyId);
                                                    bundleToView.putString("Cash", cash);
                                                    bundleToView.putString("Credit", credit);
                                                    bundleToView.putString("Cheque", cheque);
                                                    bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                                                    if (chequeEnabled) {
                                                        bundleToView.putString("ChequeNumber", chequeNumber);
                                                        bundleToView.putString("CollectionDate", collectionDate);
                                                        bundleToView.putString("ReleaseDate", releaseDate);
                                                    }
                                                    bundleToView.putString("MarketReturns", marketReturns);
                                                    bundleToView.putString("Discount", discount);
                                                    bundleToView.putString("NeedToPay", needToPay);
                                                    bundleToView.putString("PaymentOption", paymentOption);
                                                    bundleToView.putString("TotalPrice", totalPrice);
                                                    bundleToView.putString("TotalQuantity", totalQuantity);
                                                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                    bundleToView.putString("TotalValue", totalValue);
                                                    startInvoiceGen2.putExtras(bundleToView);
                                                    startActivity(startInvoiceGen2);
                                                    finish();
                                                } else {
                                                    Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                                    finish();
                                                    startActivity(startItinerary);
                                                }
                                            }
                                        }
                                    })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    return;

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    if (flagFromInvoiceGen) {
                                        Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                                        Bundle bundleToView = new Bundle();
                                        bundleToView.putString("Id", itineraryId);
                                        bundleToView.putString("PharmacyId", pharmacyId);
                                        bundleToView.putString("Cash", cash);
                                        bundleToView.putString("Credit", credit);
                                        bundleToView.putString("Cheque", cheque);
                                        bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                                        if (chequeEnabled) {
                                            bundleToView.putString("ChequeNumber", chequeNumber);
                                            bundleToView.putString("CollectionDate", collectionDate);
                                            bundleToView.putString("ReleaseDate", releaseDate);
                                        }
                                        bundleToView.putString("MarketReturns", marketReturns);
                                        bundleToView.putString("Discount", discount);
                                        bundleToView.putString("NeedToPay", needToPay);
                                        bundleToView.putString("PaymentOption", paymentOption);
                                        bundleToView.putString("TotalPrice", totalPrice);
                                        bundleToView.putString("TotalQuantity", totalQuantity);
                                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                        bundleToView.putString("TotalValue", totalValue);
                                        startInvoiceGen2.putExtras(bundleToView);
                                        startActivity(startInvoiceGen2);
                                        finish();
                                    } else {
                                        Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                                        finish();
                                        startActivity(startItinerary);
                                    }
                                }
                            });
                    alertNotSaved.show();
                }
            } else {
                if (flagFromInvoiceGen) {
                    Intent startInvoiceGen2 = new Intent(getApplication(), InvoiceGen2Activity.class);
                    Bundle bundleToView = new Bundle();
                    bundleToView.putString("Id", itineraryId);
                    bundleToView.putString("PharmacyId", pharmacyId);
                    bundleToView.putString("Cash", cash);
                    bundleToView.putString("Credit", credit);
                    bundleToView.putString("Cheque", cheque);
                    bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                    if (chequeEnabled) {
                        bundleToView.putString("ChequeNumber", chequeNumber);
                        bundleToView.putString("CollectionDate", collectionDate);
                        bundleToView.putString("ReleaseDate", releaseDate);
                    }
                    bundleToView.putString("MarketReturns", marketReturns);
                    bundleToView.putString("Discount", discount);
                    bundleToView.putString("NeedToPay", needToPay);
                    bundleToView.putString("PaymentOption", paymentOption);
                    bundleToView.putString("TotalPrice", totalPrice);
                    bundleToView.putString("TotalQuantity", totalQuantity);
                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                    bundleToView.putString("TotalValue", totalValue);
                    startInvoiceGen2.putExtras(bundleToView);
                    startActivity(startInvoiceGen2);
                    finish();
                } else {
                    Intent startItinerary = new Intent(getApplication(), ItineraryList.class);
                    finish();
                    startActivity(startItinerary);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getDataForProductsList() {
        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        products = productsObject.getInvoicedProductsForCustomer(pharmacyId);
        productsObject.closeDatabase();
        Log.w("Product Size", products.size() + "");
        productList = new ArrayList<String>();
        int i = 0;
        for (String[] p : products) {
            productsObject.openReadableDatabase();
            long invoicedTotal = productsObject.getTotalInvoicedQuantityForProduct(pharmacyId, p[0]);
            productsObject.closeDatabase();

            ProductReturns productReturns = new ProductReturns(this);
            productReturns.openReadableDatabase();
            long returnedTotal = productReturns.getTotalReturnedProductQuantityByPharmacyIdAndProductId(pharmacyId, p[0]);
            productReturns.closeDatabase();
            Log.w("product: " + p[1], "ret: " + returnedTotal + " inv: " + invoicedTotal);
            if (returnedTotal < invoicedTotal) {
                productList.add(p[1]);
                i++;
            }

        }

        if (i == 0) {
            Toast invoiceEmpty = Toast.makeText(this, "The products invoiced for this invoice number have all been returned", Toast.LENGTH_LONG);
            invoiceEmpty.setGravity(Gravity.TOP, 100, 100);
            invoiceEmpty.show();
        }

        setProductListAdapter(productList);
    }

    private void setProductListAdapter(ArrayList<String> pList) {
        ArrayAdapter<String> productAdapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, pList);
        ((AutoCompleteTextView) txtProduct).setAdapter(productAdapterList);
    }

//    private void getDataForBatchList(String productId, String pharmacyId) {
//        // TODO Auto-generated method stub
//        InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
//        invoicedProductsObject.openReadableDatabase();
//        ArrayList<String> batches = invoicedProductsObject.getInvoicedProductBatchesForCustomer(pharmacyId, productId);
//        invoicedProductsObject.closeDatabase();
//
//        ArrayList<String> batchesByProductIdForCustomer = new ArrayList<String>();
//
//        for (String b : batches) {
//            invoicedProductsObject.openReadableDatabase();
//            long invoicedTotal = invoicedProductsObject.getTotalInvoicedQuantityByBatchAndPharmacyId(b, pharmacyId);
//            invoicedProductsObject.closeDatabase();
//
//            ProductReturns productReturns = new ProductReturns(this);
//            productReturns.openReadableDatabase();
//            long returnedTotal = productReturns.getTotalReturnedQuantityByBatchAndPharmacyId(b, pharmacyId);
//            productReturns.closeDatabase();
//
//            if (returnedTotal < invoicedTotal) {
//                batchesByProductIdForCustomer.add(b);
//            }
//        }
//
//
//        setBatchListAdapter(batchesByProductIdForCustomer);
//    }

//    private void setBatchListAdapter(ArrayList<String> batchList) {
//        // TODO Auto-generated method stub
//        ArrayAdapter<String> batchListAdapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_dropdown_item_1line, batchList);
//        batchListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinBatches.setAdapter(batchListAdapter);
//
//    }

//    private void getDataForInvoiceSpinner() {
//        // TODO Auto-generated method stub
//
//        DEL_Outstandiing outstandiing = new DEL_Outstandiing(this);
//       // outstandiing.openReadableDatabase();
//        ArrayList<String> invoiceNumberList =    outstandiing.loadOutSatingInvoiceNumberBYId(pharmacyId);
//                //invoiceProductsObject.getInvoiceNumbersForCustomerByBatch(pId, batch);
//        //outstandiing.closeDatabase();
//
//        setInvoiceNumberSpinnerAdapter(invoiceNumberList);
//    }

//    private void setInvoiceNumberSpinnerAdapter(ArrayList<String> invoiceNumberList) {
//        ArrayAdapter<String> invoiceListAdapter = new ArrayAdapter<String>(
//                this, android.R.layout.simple_dropdown_item_1line, invoiceNumberList);
//        invoiceListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinInvoiceNumber.setAdapter(invoiceListAdapter);
//    }

    private void getDataForInvoice(String invoiceId) {
        // TODO Auto-generated method stub
        InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);
        invoicedProductsObject.openReadableDatabase();
        invoicedProductDetails = invoicedProductsObject.getInvoiceDataByInvoiceNumber(invoiceId, productId);
        invoicedProductsObject.closeDatabase();

        String batch = spinBatches.getText().toString();
        ProductReturns productReturns = new ProductReturns(this);
        productReturns.openReadableDatabase();
        long returnAmount = productReturns.getSumReturnsByBatch(invoiceId, batch);
        productReturns.closeDatabase();
        productReturns.openReadableDatabase();
        long returnedFreeAmount = productReturns.getSumFreeReturnsByBatch(invoiceId, batch);
        productReturns.closeDatabase();


        String unitPrice = invoicedProductDetails[9];
        tViewUnitPrice.setText(unitPrice);
        if (invoicedProductDetails[5].isEmpty()) {
            txtFree.setText("0");
        } else {
            int totalFreeAmount = 0;
            try {
                totalFreeAmount = Integer.parseInt(invoicedProductDetails[5]);
            } catch (NumberFormatException e) {
                Log.w("ReturnProductHistory getDataForInvoice", e.toString());
                totalFreeAmount = 0;
            }
            maxFreeReturn = totalFreeAmount - returnedFreeAmount;
            txtFree.setText(String.valueOf(totalFreeAmount - returnedFreeAmount));
        }

        if (invoicedProductDetails[6].isEmpty()) {
            // tViewDiscount.setText("0");
        } else {
            // tViewDiscount.setText(String.valueOf(invoicedProductDetails[6]));
        }

        String quantity = String.valueOf(Integer.parseInt(invoicedProductDetails[7]) - returnAmount);

        setInvoiceQuantitySpinnerAdapter(quantity);

    }

    private void setInvoiceQuantitySpinnerAdapter(String quantity) {

        ArrayList<String> invoiceQuantityList = new ArrayList<String>();
        int q = Integer.parseInt(quantity);
        for (int i = 0; i <= q; i++) {
            invoiceQuantityList.add(String.valueOf(i));
        }


        ArrayAdapter<String> quantityListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, invoiceQuantityList);
        quantityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spinReturnQuantity.setAdapter(quantityListAdapter);
    }

    private boolean saveReturns() {
        returnProductsArray = new ArrayList<ReturnProduct>();

        if (!returnProducts.isEmpty()) {
            saveReturnHeader();
            if (flagFromInvoiceGen) {
                for (String[] returnProduct : returnProducts) {
                    ReturnProduct product = new ReturnProduct();

                    product.setDescription(returnProduct[1]);
                    product.setBatch(returnProduct[2]);
                    //product.setInvoiceNumber(returnProduct[0]);
                    product.setInvoiceNumber(tvReturnNoH.getText().toString());
                    product.setQuantity(Integer.parseInt(returnProduct[4]));
                    product.setFree(Integer.parseInt(returnProduct[8]));
                    product.setUnitPrice(Double.parseDouble(returnProduct[3]));
                    product.setIssueMode(returnProduct[7]);
                    product.setReturnValue(Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]));
                    product.setProductId(returnProduct[6]);
                    product.setReturnValidated("true");
                    product.setDiscountval(returnProduct[15]);
                    double returnDiscount = 0;
                    try {
                        returnDiscount = Double.parseDouble(returnProduct[9]);
                    } catch (Exception e) {
                        returnDiscount = 0;
                    }
                    product.setDiscount(returnDiscount);

                    returnProductsArray.add(product);

                }
                return true;
            } else {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                double total = 0;

                for (String[] returnProduct : returnProducts) {
                    ReturnProduct product = new ReturnProduct();

                    product.setDescription(returnProduct[1]);
                    product.setBatch(returnProduct[2]);
                    // product.setInvoiceNumber(returnProduct[0]);
                    product.setInvoiceNumber(tvReturnNoH.getText().toString());

                    product.setQuantity(Integer.parseInt(returnProduct[4]));
                    product.setFree(Integer.parseInt(returnProduct[8]));
                    product.setUnitPrice(Double.parseDouble(returnProduct[3]));
                    product.setIssueMode(returnProduct[7]);
                    product.setReturnValue(Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]));
                    product.setProductId(returnProduct[6]);
                    product.setReturnValidated("true");
                    product.setDiscountval(returnProduct[15]);
                    double returnDiscount = 0;
                    try {
                        returnDiscount = Double.parseDouble(returnProduct[9]);
                    } catch (Exception e) {
                        returnDiscount = 0;
                    }
                    product.setDiscount(returnDiscount);

                    returnProductsArray.add(product);

                }

                for (String[] returnDetails : returnProducts) {
                    ProductReturns productReturnObject = new ProductReturns(ReturnProductHistoryActivity.this);
                    productReturnObject.openWritableDatabase();
                    //productReturnObject.insertProductReturn(productCode, batchNo, invoiceNo, timeStamp, normal, free, returnDate, customerNo, uploadedStatus)

                    //change invoice number
                    //  productReturnObject.insertProductReturn(returnDetails[6], returnDetails[2], returnDetails[0], returnDetails[7], returnDetails[4], returnDetails[8], timeStamp, pharmacyId, "false", returnDetails[3], returnDetails[9], returnDetails[0], "true",Double.toString(lat),Double.toString(lng),onTimeOrNot);
                    productReturnObject.insertProductReturn(returnDetails[6], returnDetails[2], tvReturnNoH.getText().toString(), returnDetails[7], returnDetails[4], returnDetails[8], timeStamp, pharmacyId, "false", returnDetails[3], returnDetails[9], returnDetails[0], "true", Double.toString(lat), Double.toString(lng), onTimeOrNot);
                    productReturnObject.closeDatabase();

                    Invoice invoice = new Invoice(this);
                    invoice.openWritableDatabase();
                    invoice.setIsReturnedStatus(true, returnDetails[0]);
                    invoice.closeDatabase();

                    if (returnDetails[7].contentEquals("SR")) {
                        ProductRepStore productRepStoreObject = new ProductRepStore(ReturnProductHistoryActivity.this);
                        productRepStoreObject.openWritableDatabase();
                        int returnSize = Integer.parseInt(returnDetails[4]) + Integer.parseInt(returnDetails[8]);
                        productRepStoreObject.updateProductRepStoreReturns(returnDetails[2], String.valueOf(returnSize));
                        productRepStoreObject.closeDatabase();
                    }

                    double temp = Double.parseDouble(returnDetails[3]) * Double.parseDouble(returnDetails[4]);
                    total = total + temp;
                }

                marketReturns = String.format("%.2f", total);
                Log.w("marketRetruns", marketReturns);
                return true;
            }

        } else {
            Toast returnProductsEmpty = Toast.makeText(ReturnProductHistoryActivity.this, "Select atleast one Product To Return!", Toast.LENGTH_SHORT);
            returnProductsEmpty.setGravity(Gravity.TOP, 100, 100);
            returnProductsEmpty.show();
            return false;
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

//		returnProductsArray = new ArrayList<ReturnProduct>();

        if (!saveBtnFlag) {

            returnProductsArray = new ArrayList<ReturnProduct>();

            for (String[] returnProduct : returnProducts) {
                ReturnProduct product = new ReturnProduct();

                product.setDescription(returnProduct[1]);
                product.setBatch(returnProduct[2]);
                product.setInvoiceNumber(returnProduct[0]);
                product.setQuantity(Integer.parseInt(returnProduct[4]));
                product.setFree(Integer.parseInt(returnProduct[8]));
                product.setUnitPrice(Double.parseDouble(returnProduct[3]));
                product.setIssueMode(returnProduct[7]);
                product.setReturnValue(Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]));
                product.setProductId(returnProduct[6]);
                product.setReturnValidated("true");
                double returnDiscount = 0;
                try {
                    returnDiscount = Double.parseDouble(returnProduct[9]);
                } catch (Exception e) {
                    returnDiscount = 0;
                }
                product.setDiscount(returnDiscount);

                returnProductsArray.add(product);

            }
            returnProducts = new ArrayList<String[]>();
        }
        outState.putParcelableArrayList("returnProductsArray", returnProductsArray);
        outState.putParcelableArrayList("selectedProductsArray", selectedProductsArray);

        outState.putBoolean("saveBtnFlag", saveBtnFlag);
        outState.putBoolean("ManualFreeEnable", manualFreeEnable);



        outState.putStringArray("invoicedProductDetails", invoicedProductDetails);
        outState.putString("itineraryId", itineraryId);
        outState.putString("pharmacyId", pharmacyId);
        outState.putString("custName", custName);
        outState.putString("custAddress", custAddress);
        outState.putString("productId", productId);

        outState.putString("cash", cash);
        outState.putString("credit", credit);
        outState.putString("marketReturns", marketReturns);
        outState.putString("discount", discount);
        outState.putString("needToPay", needToPay);

        outState.putString("paymentOption", paymentOption);
        outState.putString("totalPrice", totalPrice);
        outState.putString("totalQuantity", totalQuantity);
        outState.putString("cheque", cheque);
        outState.putString("invoiceNumber", invoiceNumber);

        outState.putString("issueMode", issueMode);
        outState.putString("returnDiscount", returnDiscount);
        outState.putString("collectionDate", collectionDate);
        outState.putString("releaseDate", releaseDate);
        outState.putString("chequeNumber", chequeNumber);
        outState.putString("creditDuration", creditDuration);

        outState.putBoolean("flagFromInvoiceGen", flagFromInvoiceGen);
        outState.putBoolean("isReturnSaved", isReturnSaved);
        outState.putBoolean("chequeEnabled", chequeEnabled);

        outState.putLong("maxFreeReturn", maxFreeReturn);
        outState.putDouble("retrunTotal", retrunTotal);
        outState.putDouble("totalDiscount", totalDiscount);

        outState.putString("TotalValue", totalValue);

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvHtotalAmount.setText(String.format("%.2f", savedInstanceState.getDouble("retTotal")));
        // txtCredit.setText(savedInstanceState.getString("Credit"));
    }

    private void setBundleData(Bundle bundlData) {

        invoicedProductDetails = bundlData.getStringArray("invoicedProductDetails");

        saveBtnFlag = bundlData.getBoolean("saveBtnFlag");

        itineraryId = bundlData.getString("itineraryId");
        pharmacyId = bundlData.getString("pharmacyId");
        custName = bundlData.getString("custName");
        custAddress = bundlData.getString("custAddress");
        productId = bundlData.getString("productId");


        cash = bundlData.getString("cash");
        credit = bundlData.getString("credit");
        marketReturns = bundlData.getString("marketReturns");
        discount = bundlData.getString("discount");
        needToPay = bundlData.getString("needToPay");
        onTimeOrNot = bundlData.getString("onTimeOrNot");
        paymentOption = bundlData.getString("paymentOption");
        totalPrice = bundlData.getString("totalPrice");
        totalQuantity = bundlData.getString("totalQuantity");
        cheque = bundlData.getString("cheque");
        invoiceNumber = bundlData.getString("invoiceNumber");

        issueMode = bundlData.getString("issueMode");
        returnDiscount = bundlData.getString("returnDiscount");
        collectionDate = bundlData.getString("collectionDate");
        releaseDate = bundlData.getString("releaseDate");
        chequeNumber = bundlData.getString("chequeNumber");
        creditDuration = bundlData.getString("creditDuration");

        flagFromInvoiceGen = bundlData.getBoolean("flagFromInvoiceGen");
        isReturnSaved = bundlData.getBoolean("isReturnSaved");
        chequeEnabled = bundlData.getBoolean("chequeEnabled");
        manualFreeEnable= bundlData.getBoolean("ManualFreeEnable");

        maxFreeReturn = bundlData.getLong("maxFreeReturn");

        returnProductsArray = bundlData.getParcelableArrayList("returnProductsArray");
        selectedProductsArray = bundlData.getParcelableArrayList("selectedProductsArray");
        totalDiscount = bundlData.getDouble("totalDiscount");
        retrunTotal = bundlData.getDouble("retrunTotal");

        totalValue = bundlData.getString("TotalValue");
        Log.i("retrunTotal ->", "" + retrunTotal);
        tvHtotalAmount.setText(String.format("%.2f", retrunTotal));
        tvHtotDiscount.setText(String.format("%.2f", totalDiscount));

//		returnProducts = new ArrayList<String[]>();
//		for (ReturnProduct returns: returnProductsArray) {
//			String[] returnDetails = new String[10];
//			returnDetails[0] = String.valueOf(returns.getInvoiceNumber());
//			returnDetails[1] = returns.getDescription();
//			returnDetails[2] = returns.getBatch();
//			returnDetails[3] = String.valueOf(returns.getUnitPrice());
//			returnDetails[4] = String.valueOf(returns.getQuantity());
//
//
//			Log.w("Return Product: 123", "getDiscount : "+returns.getDiscount());
//			Log.w("Return Product: 123", "getQuantity : "+returns.getQuantity());
//			Log.w("Return Product: 123", "getUnitPrice : "+returns.getUnitPrice());
//
//			double value = 0.0;
//			if (returns.getDiscount()>0) {
//				double normalValue  = returns.getQuantity() * returns.getUnitPrice();
//				value = normalValue - ((normalValue/100) * returns.getDiscount());
//			} else {
//				value = returns.getQuantity() * returns.getUnitPrice();
//			}
//
//			Log.w("Return Product: 123", "value : "+value);
//
//			returnDetails[5] = String.valueOf(value);
//			returnDetails[6] = returns.getProductId();
//			returnDetails[7] = returns.getIssueMode();
//			returnDetails[8] = String.valueOf(returns.getFree());
//			returnDetails[9] = String.valueOf(returns.getDiscount());
//			returnProducts.add(returnDetails);
//		}
//
//		returnProductsArray = new ArrayList<ReturnProduct>();

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

    private String loadCreditAmount(String prodId) {
        // TODO Auto-generated method stub

        String creditAmount = "0";

        outstandiing.openReadableDatabase();
        creditAmount = outstandiing.GetCredit_value(prodId).toString();
        outstandiing.closeDatabase();


        return creditAmount;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkHistoryValidation(isChecked);
    }


    private void checkHistoryValidation(Boolean isChecked) {
        if (isChecked == true) {
            /*if with history get the product list from the invoiced products*/

            if (iswebApprovalActive == true) {
                try {
                    productList.clear();
                    productList = dealerSalesController.getAllProductsFromDealerSales(spinInvoiceNumber.getSelectedItem().toString());
                    ArrayAdapter<String> productAdapterList = new ArrayAdapter<String>(this,
                            android.R.layout.simple_dropdown_item_1line, productList);
                    ((AutoCompleteTextView) txtProduct).setAdapter(productAdapterList);
                } catch (Exception e) {

                }
            } else {
                productList.clear();
                invoicedProducts.openReadableDatabase();
                try {
                    productList = invoicedProducts.getAllInvoicedProductsByInvoNO(spinInvoiceNumber.getSelectedItem().toString());
                    invoicedProducts.closeDatabase();
                    ArrayAdapter<String> productAdapterList = new ArrayAdapter<String>(this,
                            android.R.layout.simple_dropdown_item_1line, productList);
                    ((AutoCompleteTextView) txtProduct).setAdapter(productAdapterList);
                } catch (Exception ex) {
                    Toast.makeText(ReturnProductHistoryActivity.this, "NO History", Toast.LENGTH_LONG).show();
                }

            }
            // productList =
            //load invoice products
        } else {
            //if without history then get the data from product table
            productList.clear();
            productsObject.openReadableDatabase();
            productList = productsObject.getProductNameArray();
            productsObject.closeDatabase();
            ArrayAdapter<String> productAdapterList = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, productList);
            ((AutoCompleteTextView) txtProduct).setAdapter(productAdapterList);
            //load all products
        }

    }

    private void setBatchList() {

        if (cbHistory.isChecked() == true) {
            // InvoicedProducts invoicedProductsObject = new InvoicedProducts(this);

            invoicedProducts.openReadableDatabase();
            ArrayList<String> batches = invoicedProducts.getInvoicedProductBatchesForCustomer(pharmacyId, productId);
            invoicedProducts.closeDatabase();
            batcAdapterList = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, batches);
            ((AutoCompleteTextView) spinBatches).setAdapter(batcAdapterList);
            if (batches.size() > 0) {
                spinBatches.setText(batches.get(0));
            }

        } else {

            productsObject.openReadableDatabase();
            ArrayList<String> batches = productsObject.getBatchForProdcuctAndPharma(productId);
            productsObject.closeDatabase();
            batcAdapterList = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, batches);
            ((AutoCompleteTextView) spinBatches).setAdapter(batcAdapterList);
            if (batches.size() > 0) {
                spinBatches.setText(batches.get(0));
            }
        }
    }


    private void updateLabel() {

        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        edExpiryDateHistory.setText(sdf.format(calendar.getTime()));
    }


    private void saveReturnHeader() {
        int totalQuantity = 0;
        for (String[] itemList : returnProducts) {
            totalQuantity += Integer.parseInt(itemList[4]) + Integer.parseInt(itemList[8]);
        }
        endTime = formatDate(new Date());
        Log.i("Tqty ->", "" + totalQuantity);
        ReturnHeaderEntity header = new ReturnHeaderEntity();
        header.setInvoiceNumber(spinInvoiceNumber.getSelectedItem().toString());
        header.setReturnDate(formatDate(new Date()));
        header.setTotalAmount(tvHtotalAmount.getText().toString());
        header.setTotalQuantity(totalQuantity);
        header.setDiscountAmount(tvHtotDiscount.getText().toString());
        header.setStartTime(startTime);
        header.setEndTime(endTime);
        header.setCutomerNo(pharmacyId);
        header.setReturnInvoiceNumber(tvReturnNoH.getText().toString());
        header.setLatitude(Double.toString(lat));
        header.setLongitude(Double.toString(lng));
        header.setIsUpload(false);

        ReturnHeader returnHeaderController = new ReturnHeader(ReturnProductHistoryActivity.this);
        returnHeaderController.openWritableDatabase();
        returnHeaderController.insertReturnHeader(header);
        returnHeaderController.closeDatabase();
    }


    private String formatDate(Date date) {

        String myFormat = "MM/dd/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        return sdf.format(date);
    }


    private void saveRturnWithProceed() {
        try {

            String invoiceNumber = String.valueOf(spinInvoiceNumber.getSelectedItem());
            String description = txtProduct.getText().toString();
            String batch = String.valueOf(spinBatches.getText().toString()).trim();
            String unitPrice = tViewUnitPrice.getText().toString();
            String returnQty = spinReturnQuantity.getText().toString();
            // String returnQty = o.toString();
            double returnDiscount = 0;
            try {
                returnDiscount = Double.parseDouble(edDisNvalue.getText().toString());
            } catch (NumberFormatException e) {
                returnDiscount = 0;
            }
            //double retVal = (Double.parseDouble(unitPrice) * Double.parseDouble(returnQty)) - ((Double.parseDouble(unitPrice) * Double.parseDouble(returnQty)) * (returnDiscount / 100));


            int freeAmt = 0;

            double retVal = 0.0;

            retVal = (Double.parseDouble(unitPrice) * Double.parseDouble(returnQty)) - returnDiscount;


            retrunTotal += retVal;
            String returnValue = String.format("%.2f", retVal);
            tvHtotalAmount.setText(String.format("%.2f", retrunTotal));
            totalDiscount += returnDiscount;
            tvHtotDiscount.setText(String.format("%.2f", totalDiscount));
            try {
                freeAmt = Integer.parseInt(txtFree.getText().toString());
            } catch (NumberFormatException e) {
                Log.w("ReturnPRoductHistory spinReturnQty", e.toString());
                freeAmt = 0;
            }
            // String free = String.valueOf(freeAmt);


            if (!txtProduct.getText().toString().isEmpty()) {
                if (!batch.contentEquals("null")) {
                    if (!invoiceNumber.contentEquals("null")) {

                        int returnType = rGroupOptions.getCheckedRadioButtonId();

                        switch (returnType) {
                            case R.id.rbResalable:
                                //issueMode = "resalable";
                                if (freeAmt > 0) {
                                    issueMode = "SF";
                                } else {
                                    issueMode = "SR";
                                }
                                break;
                            case R.id.rbCompanyReturns:
                                //issueMode = "company_returns";
                                if (freeAmt > 0) {
                                    issueMode = "CF";
                                } else {
                                    issueMode = "CR";
                                }
                                break;
                            case R.id.rbExpiredC:
                                if (freeAmt > 0) {
                                    issueMode = "EF";
                                } else {
                                    issueMode = "ER";
                                }
                                break;
                            default:
                                break;
                        }


                        String[] returnDetails = new String[16];
                        returnDetails[0] = invoiceNumber;
                        returnDetails[1] = description;
                        returnDetails[2] = batch;
                        returnDetails[3] = unitPrice;
                        returnDetails[4] = returnQty;
                        returnDetails[5] = returnValue;
                        returnDetails[6] = productId;
                        returnDetails[7] = issueMode;
                        returnDetails[8] = String.valueOf(freeAmt);
                        returnDetails[9] = String.valueOf(returnDiscount);
                        returnDetails[10] = pid;
                        returnDetails[11] = edExpiryDateHistory.getText().toString();
                        returnDetails[12] = tViewUnitPrice.getText().toString();//selling
                        returnDetails[13] = edrPrice.getText().toString();//retail
                        returnDetails[14] = purPrice;
                        returnDetails[15] = edDisNPercentage.getText().toString();

                        Log.w("NiGGA", "You're here");


                        if (returnProducts.isEmpty()) {
                            if (!((spinReturnQuantity.getText().toString().contentEquals("0")) || (spinReturnQuantity.getText().toString().isEmpty()))) {
                                tblProductReturns.removeAllViews();
                                returnProducts.add(returnDetails);
                                populateTable(returnProducts);

                                ArrayList<String> nullBatchList = new ArrayList<String>();
                                ArrayList<String> nullInvoiceList = new ArrayList<String>();
                                // setBatchListAdapter(nullBatchList);
                                //setInvoiceNumberSpinnerAdapter(nullInvoiceList);
                                setInvoiceQuantitySpinnerAdapter("0");
                                tViewUnitPrice.setText("0");
                                // tViewDiscount.setText("0");
                                txtProduct.setText(null);
                                spinReturnQuantity.setText("0");
                                spinReturnQuantity.setEnabled(false);
                                edDisNvalue.setText("0.00");
                                edDisNPercentage.setText("0");
                                edDisNPercentage.setEnabled(false);
                                edDisNvalue.setEnabled(false);
                                edrPrice.setText("0");
                                spinBatches.setText("");

                            } else {
                                Toast selectionEmpty = Toast.makeText(ReturnProductHistoryActivity.this, "Please select a valid quantity!", Toast.LENGTH_SHORT);
                                selectionEmpty.setGravity(Gravity.TOP, 100, 100);
                                selectionEmpty.show();
                            }
                        } else {
                            for (String[] r : returnProducts) {
                                if (!(r[2].contentEquals(returnDetails[2]))) {
                                    if (!((spinReturnQuantity.getText().toString().contentEquals("0")) || (spinReturnQuantity.getText().toString().isEmpty()))) {
                                        tblProductReturns.removeAllViews();
                                        returnProducts.add(returnDetails);
                                        populateTable(returnProducts);
                                        ArrayList<String> nullBatchList = new ArrayList<String>();
                                        ArrayList<String> nullInvoiceList = new ArrayList<String>();
                                        // setBatchListAdapter(nullBatchList);
                                        // setInvoiceNumberSpinnerAdapter(nullInvoiceList);
                                        setInvoiceQuantitySpinnerAdapter("0");
                                        tViewUnitPrice.setText("0");
                                        //.setText("0");
                                        txtProduct.setText(null);
                                        spinReturnQuantity.setText("0");
                                        spinReturnQuantity.setEnabled(false);
                                        edDisNvalue.setText("0.00");
                                        edDisNPercentage.setText("0");
                                        edDisNPercentage.setEnabled(false);
                                        edDisNvalue.setEnabled(false);
                                        edrPrice.setText("0");
                                        spinBatches.setText("");
                                        //edrPrice.setText("0");


                                    } else {
                                        Toast selectionEmpty = Toast.makeText(ReturnProductHistoryActivity.this, "Please select a valid quantity!", Toast.LENGTH_SHORT);
                                        selectionEmpty.setGravity(Gravity.TOP, 100, 100);
                                        selectionEmpty.show();
                                        retrunTotal -= retVal;
                                        //String returnValue = String.format("%.2f", retVal);

                                        totalDiscount -= returnDiscount;

                                    }
                                } else {
                                    Toast alreadyAdded = Toast.makeText(ReturnProductHistoryActivity.this, "This product has already been added!", Toast.LENGTH_SHORT);
                                    alreadyAdded.setGravity(Gravity.TOP, 100, 100);
                                    alreadyAdded.show();
                                    retrunTotal -= retVal;
                                    //String returnValue = String.format("%.2f", retVal);

                                    totalDiscount -= returnDiscount;
                                }
                            }
                        }
                    }
                } else {
                    Toast invalidProduct = Toast.makeText(ReturnProductHistoryActivity.this, "Invalid product!", Toast.LENGTH_SHORT);
                    invalidProduct.setGravity(Gravity.TOP, 100, 100);
                    invalidProduct.show();
                }
            }
            tvHtotDiscount.setText(String.format("%.2f", totalDiscount));
            tvHtotalAmount.setText(String.format("%.2f", retrunTotal));
            isReturnSaved = false;
            btnSaveReturns.setEnabled(true);
            btnPrint.setEnabled(false);
            btnCancel.setText("Cancel");
        } catch (Exception e) {
            Log.w("Return Product Activity:", e.toString());
        }

    }
}
