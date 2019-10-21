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
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

import com.halcyon.Entity.ReturnHeaderEntity;
import com.halcyon.channelbridgebs.UploadProductReturnsTask;
import com.halcyon.channelbridgebs.UploadRetunHeaderTask;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductReturns;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.ReturnHeader;
import com.halcyon.channelbridgedb.Sequence;


public class ReturnProductNoHistoryActivity extends Activity implements LocationListener, AdapterView.OnItemSelectedListener {
    Location location;
    double lat, lng;
    private LocationManager locationManager;
    private String onTimeOrNot;
    TextView tViewDate, tViewCustomerName, tViewTotalReturns, tViewInvoiceNumber, tvTotalAmount, tvCrediAmount, tvTotalDiscount;
    EditText txtFree, edExpiryDate, edRetailPrice, edDisPercentage, edPercentagevalue;
    Button btnAdd, btnCancel, btnSaveReturns, btnPrint, btnDeleteReturns;
    ImageButton iBtnClearSearch;
    AutoCompleteTextView txtProduct, txtReturnQuantity, txtBatches, txtUnitPrice;
    TableLayout tblProductReturns;
    String[] productNames;
    String productId, NewunitPrice;
    String cash, credit, needToPay, paymentOption, totalPrice, totalQuantity, cheque, invoiceNumber, issueMode, discount;
    String itineraryId, pharmacyId;
    boolean flagFromInvoiceGen = false;
    boolean isReturnSaved = false;
    RadioGroup rGroupOptions;
    TableRow tr;
    String custName, custAddress;
    boolean chequeEnabled = false;
    String collectionDate = "", releaseDate = "", chequeNumber = "", creditDuration = "";
    boolean saveBtnFlag = false;
    boolean addBtnFlag = false;
    private Spinner spInvoiceNumber;
    private DEL_Outstandiing outstandiing;
    private Calendar calendar;
    private String pid = "";
    List<String[]> productList = new ArrayList<String[]>();
    ArrayList<String> batches = new ArrayList<String>();
    ArrayList<ReturnProduct> returnProductsArray = new ArrayList<ReturnProduct>();
    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();
    ArrayList<String[]> returnProducts = new ArrayList<String[]>();
    ArrayList<String[]> tempreturnProducts = new ArrayList<String[]>();
    ArrayList<String> invoicenumberList;
    ArrayAdapter<String> invoiceAdapter;
    DatePickerDialog.OnDateSetListener dateSetListener;
    double retrunTotal = 0, totalDiscount = 0;
    private String startTime = "";
    private String endTime = "";
    private String pPrice = "", totalValue;

    boolean manualFreeEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_product_no_history);
        startTime = formatDate(new Date());
        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewDate = (TextView) findViewById(R.id.labelDate);
        tViewTotalReturns = (TextView) findViewById(R.id.tvReturnQty);
        txtProduct = (AutoCompleteTextView) findViewById(R.id.etProduct);
        txtBatches = (AutoCompleteTextView) findViewById(R.id.etBatch);
        tViewInvoiceNumber = (TextView) findViewById(R.id.tvInvoice);
        txtReturnQuantity = (AutoCompleteTextView) findViewById(R.id.etQty);
        txtUnitPrice = (AutoCompleteTextView) findViewById(R.id.etUnitPrice);
        btnCancel = (Button) findViewById(R.id.bCancel);
        btnAdd = (Button) findViewById(R.id.bAdd);
        btnSaveReturns = (Button) findViewById(R.id.bSaveReturn);
        btnPrint = (Button) findViewById(R.id.bPrint);
        btnDeleteReturns = (Button) findViewById(R.id.bDeleteReturns);
        rGroupOptions = (RadioGroup) findViewById(R.id.rgOptions);
        iBtnClearSearch = (ImageButton) findViewById(R.id.ibClearSearch);
        txtFree = (EditText) findViewById(R.id.etFree);
        tblProductReturns = (TableLayout) findViewById(R.id.tlItemsToRemove);
        spInvoiceNumber = (Spinner) findViewById(R.id.spInvoiceNumber);
        spInvoiceNumber.setOnItemSelectedListener(this);
        edExpiryDate = (EditText) findViewById(R.id.edExpiryDate);
        edRetailPrice = (EditText) findViewById(R.id.edRetailPrice);
        tvTotalAmount = (TextView) findViewById(R.id.lblTotalAmount);
        edDisPercentage = (EditText) findViewById(R.id.edDisPercentage);
        edPercentagevalue = (EditText) findViewById(R.id.edPercentagevalue);
        tvCrediAmount = (TextView) findViewById(R.id.tvCrediAmount);
        tvTotalDiscount = (TextView) findViewById(R.id.tvTotalDiscount);
        txtReturnQuantity.setEnabled(false);
        edDisPercentage.setEnabled(false);
        edPercentagevalue.setEnabled(false);
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

        edExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReturnProductNoHistoryActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();//2020,00,01
            }
        });
        updateLabel();
        invoicenumberList = new ArrayList<>();


        txtFree.setText("0");
        txtUnitPrice.setText("0");
        tViewTotalReturns.setText("0");
        edPercentagevalue.setText("0");
        edDisPercentage.setText("0");
        tvTotalAmount.setText("0.00");
        btnPrint.setEnabled(false);
        btnDeleteReturns.setEnabled(false);

        GetGPS();
        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDataFromPreviousActivity();
        }

        outstandiing = new DEL_Outstandiing(ReturnProductNoHistoryActivity.this);
        invoicenumberList = outstandiing.loadOutSatingInvoiceNumber(pharmacyId);

        invoiceAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, invoicenumberList);
        spInvoiceNumber.setAdapter(invoiceAdapter);

        if (saveBtnFlag) {
            btnSaveReturns.setEnabled(false);
            btnAdd.setEnabled(false);
            btnCancel.setText("Done");
            btnPrint.setEnabled(true);

        }

        Log.w("returnProducts", "################ 01:" + returnProducts.size() + "");
        setInitialData();
        getDataForProductsList();

        iBtnClearSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                txtProduct.setText(null);
            }
        });

        edDisPercentage.addTextChangedListener(new TextWatcher() {
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
                if (!txtUnitPrice.getText().toString().isEmpty()) {
                    unitPrice = Double.parseDouble(txtUnitPrice.getText().toString());
                }
                if (!txtReturnQuantity.getText().toString().isEmpty()) {
                    qty = Integer.parseInt(txtReturnQuantity.getText().toString());
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
                edPercentagevalue.setText(String.format("%.2f", dicountValue));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtProduct.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                ArrayList<String> nullBatchList = new ArrayList<String>();
                setBatchListAdapter(nullBatchList);
                try {
                    for (String[] productDetails : productList) {
                        if (productDetails[8].contentEquals(s.toString())) {
                            productId = productDetails[2];
                            Log.i("productId ->", productId);
                            pid = productDetails[1];
                            Log.i("pId ->", pid);
                            pPrice = productDetails[12];
                            getDataForBatchList(productId, pharmacyId);

                            txtUnitPrice.setText(loadUnitPrice(productId));
                            edRetailPrice.setText(loadRetailPrice(productId));
                            NewunitPrice = "";
                            NewunitPrice = txtUnitPrice.getText().toString();
                            txtReturnQuantity.setEnabled(true);
                            spInvoiceNumber.setClickable(false);
//                            edDisPercentage.setEnabled(true);
//                            edPercentagevalue.setEnabled(true);
                        }
                    }
                } catch (Exception e) {
                    Log.w("error getting product info", e.toString());
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        txtReturnQuantity.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String freeQty = "0";
                try {
                    if (txtFree.getText().toString().isEmpty()) {
                        txtFree.setText("0");
                    } else {
                        if (Integer.parseInt(s.toString()) > 0) {
                            txtFree.setEnabled(true);
                            edDisPercentage.setEnabled(true);
                            edPercentagevalue.setEnabled(true);
                        } else {
                            txtFree.setEnabled(false);
                            edDisPercentage.setEnabled(false);
                            edPercentagevalue.setEnabled(false);
                        }
                    }
                    freeQty = txtFree.getText().toString();

                    if (freeQty.isEmpty() || freeQty.equals("")) {
                        freeQty = "0";
                    }
                    // int total = Integer.parseInt(s.toString()) + Integer.parseInt(freeQty);
                    int total = Integer.parseInt(s.toString());
                    tViewTotalReturns.setText(String.valueOf(total));

                } catch (Exception e) {
                    Log.w("unable to add total from returnqty", e.toString());
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub


            }
        });

        txtFree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                try {
                    if (txtReturnQuantity.getText().toString().isEmpty()) {
                        txtReturnQuantity.setText("0");
                    } else {
                        if (Integer.parseInt(s.toString()) <= 0) {
                            edDisPercentage.setEnabled(true);
                            edPercentagevalue.setEnabled(true);

                        } else {
                            edDisPercentage.setEnabled(false);
                            edPercentagevalue.setEnabled(false);
                        }
                    }

                    int total = Integer.parseInt(s.toString()) + Integer.parseInt(txtReturnQuantity.getText().toString());
                    tViewTotalReturns.setText(String.valueOf(total));
                } catch (Exception e) {
                    Log.w("unable to add total from free qty", e.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                int a = 5;
                // TODO Auto-generated method stub
                if (!returnProducts.isEmpty()) {
                    if (isReturnSaved) {
                        if (flagFromInvoiceGen == true) {
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
                            bundleToView.putString("NeedToPay", needToPay);
                            bundleToView.putString("Discount", discount);
                            bundleToView.putString("PaymentOption", paymentOption);
                            bundleToView.putString("TotalPrice", totalPrice);
                            bundleToView.putString("TotalQuantity", totalQuantity);
                            bundleToView.putString("InvoiceNumber", invoiceNumber);
                            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                            bundleToView.putString("startTime", totalPrice);
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
                        Builder alertNotSaved = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
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
                                                        bundleToView.putString("Discount", discount);
                                                        bundleToView.putString("NeedToPay", needToPay);
                                                        bundleToView.putString("PaymentOption", paymentOption);
                                                        bundleToView.putString("TotalPrice", totalPrice);
                                                        bundleToView.putString("TotalQuantity", totalQuantity);
                                                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                        bundleToView.putString("startTime", totalPrice);
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
                                            bundleToView.putString("NeedToPay", needToPay);
                                            bundleToView.putString("Discount", discount);
                                            bundleToView.putString("PaymentOption", paymentOption);
                                            bundleToView.putString("TotalPrice", totalPrice);
                                            bundleToView.putString("TotalQuantity", totalQuantity);
                                            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                            bundleToView.putString("startTime", totalPrice);
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
                    if (flagFromInvoiceGen == true) {
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
                        bundleToView.putString("NeedToPay", needToPay);
                        bundleToView.putString("PaymentOption", paymentOption);
                        bundleToView.putString("Discount", discount);
                        bundleToView.putString("TotalPrice", totalPrice);
                        bundleToView.putString("TotalQuantity", totalQuantity);
                        bundleToView.putString("InvoiceNumber", invoiceNumber);
                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                        bundleToView.putString("TotalValue", totalValue);
                        bundleToView.putString("startTime", totalPrice);


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
            boolean inList = false;

            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {

                    if (checkDatatoAdd()) {
                        String unitPrice1 = txtUnitPrice.getText().toString();

                        if (txtUnitPrice.getText().toString().isEmpty())
                            Toast.makeText(ReturnProductNoHistoryActivity.this, "Unite price cannot be empty", Toast.LENGTH_SHORT).show();
                        else if (Double.parseDouble(unitPrice1) == 0)

                            Toast.makeText(ReturnProductNoHistoryActivity.this, "Unite price cannot be 0.", Toast.LENGTH_SHORT).show();
                        else if (Double.parseDouble(unitPrice1) < (Double.parseDouble(NewunitPrice) / 2))
                            Toast.makeText(ReturnProductNoHistoryActivity.this, "Unite price lower than half of actual unit price", Toast.LENGTH_SHORT).show();
                        else if ((Double.parseDouble(txtReturnQuantity.getText().toString()) + Double.parseDouble(txtFree.getText().toString())) <= 0)
                            Toast.makeText(ReturnProductNoHistoryActivity.this, "Total Quantity cannot be 0.", Toast.LENGTH_SHORT).show();


                        else {
//                            txtReturnQuantity.setEnabled(false);
//                            invoiceNumber = tViewInvoiceNumber.getText().toString();
//                            String description = txtProduct.getText().toString();
                            //                           String batch = txtBatches.getText().toString();
                            String unitPrice = txtUnitPrice.getText().toString();
                            // String returnQty = txtReturnQuantity.getText().toString();
                            String dicountValue = edPercentagevalue.getText().toString();
                            String credit = tvCrediAmount.getText().toString();

                            if (dicountValue.isEmpty()) {
                                dicountValue = "0";
                            }
                            if (credit.isEmpty()) {
                                credit = "0";
                            }
                            double retVal = 0.0;

                            retVal = (Double.parseDouble(unitPrice) * Double.parseDouble(txtReturnQuantity.getText().toString())) - Double.parseDouble(dicountValue);


                            double tempTotal = retrunTotal + retVal;


                            if (flagFromInvoiceGen == true) {

                                if (Double.parseDouble(totalValue) < tempTotal) {
                                    Builder alertNotSaved = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
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
                                        Builder alertNotSaved = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
                                                .setTitle("Warning")
                                                .setMessage("Total amount is greater than credit amount.Do you want to proceed?")
                                                .setPositiveButton("Yes",
                                                        new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface dialog,
                                                                                int which) {
                                                                // TODO Auto-generated method stub
                                                                saveRturnIfOk(inList);

                                                            }
                                                        })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // TODO Auto-generated method stub
                                                        dialog.dismiss();
                                                        addBtnFlag = false;
                                                    }
                                                });
                                        alertNotSaved.show();
                                    } else {
                                        saveRturnIfOk(inList);
                                    }

                                }


                            } else {


                                if (tempTotal > Double.parseDouble(credit)) {
                                    Builder alertNotSaved = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
                                            .setTitle("Warning")
                                            .setMessage("Total amount is greater than credit amount.Do you want to proceed?")
                                            .setPositiveButton("Yes",
                                                    new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog,
                                                                            int which) {
                                                            // TODO Auto-generated method stub
                                                            saveRturnIfOk(inList);

                                                        }
                                                    })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method stub
                                                    dialog.dismiss();
                                                    addBtnFlag = false;
                                                }
                                            });
                                    alertNotSaved.show();
                                } else {
                                    saveRturnIfOk(inList);
                                }


                            }


                        }

                    }

                } catch (Exception e) {
                    Log.w("Return Product Activity:", e.toString());
                }


            }
        });

        btnSaveReturns.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                isReturnSaved = saveReturns();
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                String deviceId = sharedPreferences.getString("DeviceId", "-1");
                String repId = sharedPreferences.getString("RepId", "-1");
                new UploadRetunHeaderTask(ReturnProductNoHistoryActivity.this, repId, deviceId).execute();
                if (isReturnSaved) {
                    new UploadProductReturnsTask(ReturnProductNoHistoryActivity.this)
                            .execute("1");
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

                Builder alertPrint = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
                        .setTitle("Customer Saved")
                        .setMessage("Are you sure you want to print?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {


                                SharedPreferences sharedPreferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());
                                boolean prePrintInvoiceFormatEnabled = sharedPreferences.getBoolean(
                                        "cbPrefPrePrintInvoice", true);
                                String repId = sharedPreferences.getString("RepId", "-1");


                                Date dateObj = new Date();

                                String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                                String time = new SimpleDateFormat("hh:mm:ss a").format(dateObj);

                                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());


                                boolean flag = true;

                                Reps reps = new Reps(ReturnProductNoHistoryActivity.this);
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

                                Customers cus = new Customers(ReturnProductNoHistoryActivity.this);
                                cus.openReadableDatabase();
                                String phone = cus.getCustomerPhoneByPharmacyId(pharmacyId);
                                if (phone.equals("0")) {
                                    CustomersPendingApproval pen = new CustomersPendingApproval(ReturnProductNoHistoryActivity.this);
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

                                /*String repTelNo = "";
                                for (int i = 0; i <= 26; i++) {
                                    repTelNo = repTelNo + " ";
                                }*/
                                repTelNo = repTelNo + dealerTel;

                                String invoiceNoString = tViewInvoiceNumber.getText().toString().trim();
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
//

                                for (String[] product : returnProducts) {
//

                                    if (flag) {

                                        if (printData.length() > 1) {
                                            printData = printData + "\n\n\n\n\n\n\n";
                                        }

                                        printData = printData + headerData;
                                        flag = false;
                                    }


                                    String productDescription = product[1];
                                    String batch = product[2];

                                    int totalQty = 0;

                                    // if (product[4] != null && product[4] != "null") {
                                    totalQty = totalQty + Integer.parseInt(product[4]);
                                    //  }
                                    //  if (product[8] != null && product[8] != "null") {
                                    totalQty = totalQty + Integer.parseInt(product[8]);
                                    // }

                                    String price = product[3];
                                    String qty = String.valueOf(product[4]);
                                    String total = product[5];
                                    String free = product[8];
                                    String discount = product[15];


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

                                    totalPrice = totalPrice + Double.parseDouble(product[5]);
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
                                    footerData = footerData + "Customer Signature & Seal\n\n";
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


                                Log.w("printData 3 ", printData);
                                Bundle bundleToView = new Bundle();
                                bundleToView.putString("PrintData", printData);

                                Intent activityIntent = new Intent(
                                        getApplicationContext(), PrintUtility.class);
                                activityIntent.putExtras(bundleToView);
                                startActivityForResult(activityIntent, 0);


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {

                                return;
                            }
                        });
                if (!returnProducts.isEmpty()) {
                    alertPrint.show();
                } else {
                    Toast returnProductsEmpty = Toast.makeText(ReturnProductNoHistoryActivity.this, "Select atleast one Product To Return!", Toast.LENGTH_SHORT);
                    returnProductsEmpty.setGravity(Gravity.TOP, 100, 100);
                    returnProductsEmpty.show();
                }
            }
        });

    }

    protected boolean checkDatatoAdd() {
        // TODO Auto-generated method stub

        boolean check = false;
        if (!txtProduct.getText().toString().isEmpty()) {
            if (!txtBatches.getText().toString().isEmpty()) {
                if (!txtReturnQuantity.getText().toString().isEmpty()) {
                    if ((!txtUnitPrice.getText().toString().isEmpty()) && (!txtUnitPrice.getText().toString().equals("0"))) {
                        if (!txtFree.getText().toString().isEmpty()) {
                            check = true;
                        } else {
                            txtFree.setText("0");
                            check = true;
                        }
                        boolean productValid = false;
                        for (int i = 0; i < productNames.length; i++) {
                            if (txtProduct.getText().toString().contentEquals(productNames[i])) {


                                if (!checkBatch(productId, txtBatches.getText().toString().trim())) {

                                    ProductRepStore productRepStoreObject = new ProductRepStore(ReturnProductNoHistoryActivity.this);
                                    productRepStoreObject.openWritableDatabase();
                                    boolean isBatchAvailabel = productRepStoreObject.isBatchAvailableWithoutProdCode(txtBatches.getText().toString().trim());
                                    productRepStoreObject.closeDatabase();


                                    if (isBatchAvailabel) {

                                        String batchNo = txtBatches.getText().toString().trim();

                                        String newBatchNo = "";

                                        do {
                                            SharedPreferences preferences = PreferenceManager
                                                    .getDefaultSharedPreferences(getBaseContext());
                                            String batchAddNo = preferences.getString("UniqueBatchNumber", null);

                                            newBatchNo = batchNo + batchAddNo;

                                            ProductRepStore productRepStoreObjectTwo = new ProductRepStore(ReturnProductNoHistoryActivity.this);
                                            productRepStoreObjectTwo.openWritableDatabase();
                                            isBatchAvailabel = productRepStoreObjectTwo.isBatchAvailableWithoutProdCode(newBatchNo);
                                            productRepStoreObjectTwo.closeDatabase();

                                            String next = String.valueOf(Integer.parseInt(batchAddNo) + 1);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("UniqueBatchNumber", String.valueOf(next));
                                            editor.commit();


                                        } while (isBatchAvailabel);

                                        txtBatches.setText(newBatchNo);

                                        Toast toast = Toast.makeText(this, "Duplicate batch no found and updated the batch no", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.TOP, 100, 100);
                                        toast.show();
                                    }

                                }

                                productValid = true;
                            }
                        }
                        if (productValid) {
                            check = true;
                        } else {
                            check = false;
                            Toast toast = Toast.makeText(this, "Invalid product!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, 100, 100);
                            toast.show();
                            batches.clear();
                            txtProduct.setText("");
                            txtProduct.setFocusable(true);
                            txtProduct.requestFocus();
                        }
                    } else {
                        Toast toast = Toast.makeText(this, "Unit price cannot be empty or 0 !", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 100, 100);
                        toast.show();
                        txtUnitPrice.setFocusable(true);
                        txtUnitPrice.requestFocus();
                    }
                } else {
                    Toast toast = Toast.makeText(this, "Return quantity cannot be empty!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 100, 100);
                    toast.show();
                    txtReturnQuantity.setFocusable(true);
                    txtReturnQuantity.requestFocus();
                }
            } else {
                Toast toast = Toast.makeText(this, "Batch cannot be empty!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 100, 100);
                toast.show();
                txtBatches.setFocusable(true);
                txtBatches.requestFocus();
            }
        } else {
            Toast toast = Toast.makeText(this, "Product cannot be empty!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 100, 100);
            toast.show();
            txtProduct.setFocusable(true);
            txtProduct.requestFocus();
        }

        return check;
    }

    protected void populateTable(final ArrayList<String[]> returnProducts) {
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
                            AlertDialog.Builder returnLongClick = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this);
                            returnLongClick.setTitle("Choose what you want to do:")
                                    .setItems(new String[]{"Delete"}, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    TableRow selectedRow = (TableRow) v;
                                                    TextView dynaReturn = (TextView) selectedRow.getChildAt(5);
                                                    double total = 0;
                                                    double deDuctValue = 0;
                                                    if (!tvTotalAmount.getText().toString().isEmpty()) {
                                                        total = Double.parseDouble(tvTotalAmount.getText().toString());
                                                    }

                                                    deDuctValue = Double.parseDouble(dynaReturn.getText().toString());
                                                    total -= deDuctValue;
                                                    String[] deleteItem = returnProducts.get(v.getId());
                                                    totalDiscount -= Double.parseDouble(deleteItem[9]);
                                                    Log.i("del -d->", deleteItem[9].toString());
                                                    tvTotalAmount.setText(String.format("%.2f", total));
                                                    tvTotalDiscount.setText(String.format("%.2f", totalDiscount));
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
        itineraryId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");

        try {
            pharmacyId = extras.getString("PharmacyId");
            cash = extras.getString("Cash");
            credit = extras.getString("Credit");
            cheque = extras.getString("Cheque");
            needToPay = extras.getString("NeedToPay");
            manualFreeEnable= extras.getBoolean("ManualFreeEnable");
            //
            totalPrice = extras.getString("TotalPrice");
            discount = extras.getString("Discount");
            totalQuantity = extras.getString("TotalQuantity");
            onTimeOrNot = extras.getString("onTimeOrNot");
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
            totalPrice = extras.getString("startTime");
        } catch (Exception e) {
            Log.w("Return Product: Error Getting Invoice Number", e.toString());
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

            //0 - rowid
            //1 - pharmacyId
            //2 - pharmacyCode
            //3 - dealerId
            //4 - companyCode
            //5 - customerName
            //6 - address
            //7 - area
            //8 - town
            //9 - district
            //10 - telephone
            //11 - fax
            //12 - email
            //13 - customerStatus
            //14 - creditLimit
            //16 - currentCredit
            //16 - creditExpiryDate
            //17 - creditDuration
            //18 - vatNo
            //19 - status
            custName = customerDetails[5];
            custAddress = customerDetails[6];
            tViewCustomerName.setText(customerDetails[5]);
        }

        String systemDate = DateFormat.getDateInstance().format(new Date());
        tViewDate.setText(systemDate);


        if (!returnProductsArray.isEmpty()) {

            returnProducts = new ArrayList<String[]>();

            for (ReturnProduct returns : returnProductsArray) {
                String[] returnDetails = new String[20];
                returnDetails[0] = String.valueOf(returns.getInvoiceNumber());
                returnDetails[1] = returns.getDescription();
                returnDetails[2] = returns.getBatch();
                returnDetails[3] = String.valueOf(returns.getUnitPrice());
                returnDetails[4] = String.valueOf(returns.getQuantity());
                returnDetails[5] = String.valueOf(returns.getQuantity() * returns.getUnitPrice());
                returnDetails[6] = returns.getProductId();
                returnDetails[7] = returns.getIssueMode();
                returnDetails[8] = String.valueOf(returns.getFree());
                returnDetails[9] = Double.toString(returns.getDiscount());
                // returnDetails[10] = Double.toString(returns.getDiscount());
                returnProducts.add(returnDetails);
            }

        }

        if (!returnProducts.isEmpty()) {

            Log.w("returnProducts", "################ : 02" + returnProducts.size() + "");

            tblProductReturns.removeAllViews();
            populateTable(returnProducts);
        }


        if (flagFromInvoiceGen) {
            Sequence sequence = new Sequence(ReturnProductNoHistoryActivity.this);
            sequence.openReadableDatabase();
            String inv = sequence.getLastRowId("return_header");
            sequence.closeDatabase();

            int invNo = Integer.parseInt(inv) + 1;

            if (saveBtnFlag) {
                tViewInvoiceNumber.setText(invoiceNumber);
            } else {
                tViewInvoiceNumber.setText(String.valueOf(invNo));
            }


        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String returnNumber = preferences.getString("ReturnNumber", null);
            String year = new SimpleDateFormat("yyyy").format(new Date());

            Sequence sequence = new Sequence(ReturnProductNoHistoryActivity.this);
            sequence.openReadableDatabase();
            String inv = sequence.getLastRowId("return_header");
            sequence.closeDatabase();

            int invNo = Integer.parseInt(inv) + 1;

            if (saveBtnFlag) {
                tViewInvoiceNumber.setText(invoiceNumber);
            } else {
                tViewInvoiceNumber.setText(String.valueOf(invNo));
            }

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!returnProducts.isEmpty()) {
                if (isReturnSaved) {
                    if (flagFromInvoiceGen == true) {
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
                        bundleToView.putString("Discount", discount);
                        bundleToView.putString("NeedToPay", needToPay);
                        bundleToView.putString("PaymentOption", paymentOption);
                        bundleToView.putString("TotalPrice", totalPrice);
                        bundleToView.putString("TotalQuantity", totalQuantity);
                        bundleToView.putString("InvoiceNumber", invoiceNumber);
                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                        bundleToView.putString("startTime", startTime);
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
                    Builder alertNotSaved = new AlertDialog.Builder(ReturnProductNoHistoryActivity.this)
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
                                                    bundleToView.putString("NeedToPay", needToPay);
                                                    bundleToView.putString("Discount", discount);
                                                    bundleToView.putString("PaymentOption", paymentOption);
                                                    bundleToView.putString("TotalPrice", totalPrice);
                                                    bundleToView.putString("TotalQuantity", totalQuantity);
                                                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                    bundleToView.putString("startTime", startTime);
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
                                        bundleToView.putString("NeedToPay", needToPay);
                                        bundleToView.putString("PaymentOption", paymentOption);
                                        bundleToView.putString("Discount", discount);
                                        bundleToView.putString("TotalPrice", totalPrice);
                                        bundleToView.putString("TotalQuantity", totalQuantity);
                                        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                        bundleToView.putString("TotalValue", totalValue);

                                        bundleToView.putString("startTime", startTime);
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
                if (flagFromInvoiceGen == true) {
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
                    bundleToView.putString("NeedToPay", needToPay);
                    bundleToView.putString("PaymentOption", paymentOption);
                    bundleToView.putString("Discount", discount);
                    bundleToView.putString("TotalPrice", totalPrice);
                    bundleToView.putString("TotalQuantity", totalQuantity);
                    bundleToView.putString("InvoiceNumber", invoiceNumber);
                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);

                    bundleToView.putString("startTime", startTime);
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
        productNames = productsObject.getProductNames();
        productsObject.closeDatabase();
        setProductListAdapter(productNames);

        productsObject.openReadableDatabase();
        productList = productsObject.getAllProducts();
        productsObject.closeDatabase();
    }

    private void setProductListAdapter(String[] pList) {
        ArrayAdapter<String> productAdapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, pList);
        ((AutoCompleteTextView) txtProduct).setAdapter(productAdapterList);
    }

    private void getDataForBatchList(String productId, String pharmacyId) {
        // TODO Auto-generated method stub

        // ProductRepStore productRepStore = new ProductRepStore(this);
        // productRepStore.openReadableDatabase();
        Products podController = new Products(ReturnProductNoHistoryActivity.this);
        batches = podController.getProductForceByCode(productId);
        // productRepStore.closeDatabase();
        Log.w("batch list sizeaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", batches.size() + "");
        setBatchListAdapter(batches);
    }


    private String loadUnitPrice(String prodId) {
        // TODO Auto-generated method stub

        String unitPrice = "0";

        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        unitPrice = productsObject.getPriceByProductCode(prodId);
        productsObject.closeDatabase();


        return unitPrice;
    }

    private String loadCreditAmount(String prodId) {
        // TODO Auto-generated method stub

        String creditAmount = "0";

        DEL_Outstandiing outstandiingObject = new DEL_Outstandiing(this);
        outstandiingObject.openReadableDatabase();
        creditAmount = outstandiingObject.GetCredit_value(prodId).toString();
        outstandiingObject.closeDatabase();


        return creditAmount;
    }

    private String loadRetailPrice(String prodId) {
        // TODO Auto-generated method stub

        String unitPrice = "0";

        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        unitPrice = productsObject.getRetalPriceByProductCode(prodId);
        productsObject.closeDatabase();


        return unitPrice;
    }

    private boolean checkBatch(String pCode, String b) {
        ProductRepStore productRepStore = new ProductRepStore(this);
        productRepStore.openReadableDatabase();
        ArrayList<String> checkBatches = productRepStore.getBatchesByProductCode(pCode);
        productRepStore.closeDatabase();
        Log.w("checKBatch batch sizeEEEEEEEEEE", checkBatches.size() + "");
        for (String batch : checkBatches) {
            if (batch.contentEquals(b)) {
                return true;
            }
        }

        return false;
    }

    private void setBatchListAdapter(ArrayList<String> batchList) {
        // TODO Auto-generated method stub
        ArrayAdapter<String> batchAdapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, batchList);
        ((AutoCompleteTextView) txtBatches).setAdapter(batchAdapterList);
        if (!batchAdapterList.isEmpty())
            ((AutoCompleteTextView) txtBatches).setText(batchAdapterList.getItem(0));


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
                    product.setInvoiceNumber(returnProduct[0]);
                    product.setQuantity(Integer.parseInt(returnProduct[4]));
                    product.setFree(Integer.parseInt(returnProduct[8]));
                    product.setUnitPrice(Double.parseDouble(returnProduct[3]));
                    product.setIssueMode(returnProduct[7]);
                    product.setReturnValue(Integer.parseInt(returnProduct[4]) * Double.parseDouble(returnProduct[3]));
                    product.setProductId(returnProduct[6]);
                    product.setReturnValidated("false");
                    product.setDiscount(Double.parseDouble(returnProduct[9]));
                    product.setDiscountval(returnProduct[15]);
                    product.setpPrice(returnProduct[14]);
                    product.setRetailPrice(returnProduct[13]);
                    product.setExDate(returnProduct[11]);

                    Log.w("known Validate", "################ :" + product.getReturnValidated() + "");

                    returnProductsArray.add(product);
                }
                return true;
            } else {
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                String returnNumber = preferences.getString("ReturnNumber", null);

                String year = new SimpleDateFormat("yyyy").format(new Date());
                final String invoiceId = year + returnNumber;

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                double total = 0;

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
                    product.setReturnValidated("false");
                    product.setDiscount(Double.parseDouble(returnProduct[9]));
                    product.setDiscountval(returnProduct[15]);
                    product.setpPrice(returnProduct[14]);
                    product.setRetailPrice(returnProduct[13]);
                    product.setExDate(returnProduct[11]);

                    Log.w("known Validate", "################ :" + product.getReturnValidated() + "");

                    returnProductsArray.add(product);
                }

                for (String[] returnDetails : returnProducts) {
                    ProductReturns productReturnObject = new ProductReturns(ReturnProductNoHistoryActivity.this);
                    productReturnObject.openWritableDatabase();
                    //productReturnObject.insertProductReturn(productCode,    batchNo,          invoiceNo,        timeStamp,        normal,            free, returnDate, customerNo, uploadedStatus, discount)
                    productReturnObject.insertProductReturn(returnDetails[6], returnDetails[2], returnDetails[0], returnDetails[7], returnDetails[4], returnDetails[8], timeStamp, pharmacyId, "false", returnDetails[3], "0", invoiceId, "false", Double.toString(lat), Double.toString(lng), onTimeOrNot);
                    productReturnObject.closeDatabase();
                    if (returnDetails[7].contentEquals("SF") || returnDetails[7].contentEquals("SR")) {

                        if (checkBatch(returnDetails[6], returnDetails[2])) {
                            ProductRepStore productRepStoreObject = new ProductRepStore(ReturnProductNoHistoryActivity.this);
                            productRepStoreObject.openWritableDatabase();
                            int returnSize = Integer.parseInt(returnDetails[4]) + Integer.parseInt(returnDetails[8]);
                            productRepStoreObject.updateProductRepStoreReturns(returnDetails[2], String.valueOf(returnSize));
                            productRepStoreObject.closeDatabase();
                            Log.w("known batch", returnDetails[2] + "");
                        } else {

                            ProductRepStore productRepStore = new ProductRepStore(ReturnProductNoHistoryActivity.this);
                            productRepStore.openReadableDatabase();
                            int returnSize = Integer.parseInt(returnDetails[4]) + Integer.parseInt(returnDetails[8]);
                            /**
                             * devAJ need to change
                             */
                            long r = productRepStore.insertProductRepStore(returnDetails[0], returnDetails[6], returnDetails[2], String.valueOf(returnSize), returnDetails[11], returnDetails[14], returnDetails[12], returnDetails[13], timeStamp);
                            productRepStore.closeDatabase();

                        }
                    }

//					InvoicedProducts invoicedProducts = new InvoicedProducts(ReturnProductNoHistoryActivity.this);
//					invoicedProducts.openWritableDatabase();
//					invoicedProducts.setReturnedTrue(returnDetails[0], returnDetails[6]);
//					invoicedProducts.closeDatabase();

                    double temp = Double.parseDouble(returnDetails[3]) * Double.parseDouble(returnDetails[4]);
                    total = total + temp;
                    Log.w("return 3", returnDetails[3]);
                }


//				invoiceObject.openWritableDatabase();
//				invoiceObject.insertInvoice(invoiceId, "R", String.format("%.2f", total), "0", "-"+String.format("%.2f", total), "0", String.format("%.2f", total), "0", "false", timeStamp);
//				invoiceObject.closeDatabase();


                String next = String.valueOf(Integer.parseInt(returnNumber) + 1);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("ReturnNumber", String.valueOf(next));
                editor.commit();


                btnPrint.setEnabled(true);
                btnSaveReturns.setEnabled(false);
                btnCancel.setText("Done");
                return true;
            }

        } else {
            Toast returnProductsEmpty = Toast.makeText(ReturnProductNoHistoryActivity.this, "Select atleast one Product To Return!", Toast.LENGTH_SHORT);
            returnProductsEmpty.setGravity(Gravity.TOP, 100, 100);
            returnProductsEmpty.show();
            return false;
        }


    }

    private void saveReturnHeader() {
        try {
            int totalQuantity = 0;
            for (String[] itemList : returnProducts) {
                totalQuantity += Integer.parseInt(itemList[4]) + Integer.parseInt(itemList[8]);
            }
            endTime = formatDate(new Date());
            Log.i("Tqty ->", "" + totalQuantity);
            ReturnHeaderEntity header = new ReturnHeaderEntity();
            header.setInvoiceNumber(tViewInvoiceNumber.getText().toString());
            header.setReturnDate(formatDate(new Date()));
            header.setTotalAmount(tvTotalAmount.getText().toString());
            header.setTotalQuantity(totalQuantity);
            header.setDiscountAmount(tvTotalDiscount.getText().toString());
            header.setStartTime(startTime);
            header.setEndTime(endTime);
            header.setCutomerNo(pharmacyId);
            header.setReturnInvoiceNumber(tViewInvoiceNumber.getText().toString());
            header.setLatitude(Double.toString(lat));
            header.setLongitude(Double.toString(lng));
            header.setIsUpload(false);

            ReturnHeader returnHeaderController = new ReturnHeader(ReturnProductNoHistoryActivity.this);
            returnHeaderController.openWritableDatabase();
            returnHeaderController.insertReturnHeader(header);
            returnHeaderController.closeDatabase();
        } catch (Exception e) {

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
                product.setReturnValidated("false");
                product.setDiscount(0);

                Log.w("known Validate", "################ :" + product.getReturnValidated() + "");


                returnProductsArray.add(product);
            }

            returnProducts = new ArrayList<String[]>();
        }


        Log.w("returnProductsArray", "################ :" + returnProductsArray.size() + "");

        outState.putParcelableArrayList("returnProductsArray", returnProductsArray);
        outState.putParcelableArrayList("selectedProductsArray", selectedProductsArray);

        outState.putBoolean("saveBtnFlag", saveBtnFlag);
        outState.putBoolean("ManualFreeEnable", manualFreeEnable);
        outState.putString("itineraryId", itineraryId);
        outState.putString("pharmacyId", pharmacyId);
        outState.putString("custName", custName);
        outState.putString("custAddress", custAddress);
        outState.putString("productId", productId);

        outState.putString("cash", cash);
        outState.putString("credit", credit);
        outState.putStringArray("productNames", productNames);
        outState.putString("discount", discount);
        outState.putString("needToPay", needToPay);

        outState.putString("paymentOption", paymentOption);
        outState.putString("totalPrice", totalPrice);
        outState.putString("totalQuantity", totalQuantity);
        outState.putString("cheque", cheque);
        outState.putString("invoiceNumber", invoiceNumber);

        outState.putString("issueMode", issueMode);
        outState.putString("collectionDate", collectionDate);
        outState.putString("releaseDate", releaseDate);
        outState.putString("chequeNumber", chequeNumber);
        outState.putString("creditDuration", creditDuration);

        outState.putBoolean("flagFromInvoiceGen", flagFromInvoiceGen);
        outState.putBoolean("isReturnSaved", isReturnSaved);
        outState.putBoolean("chequeEnabled", chequeEnabled);
        outState.putDouble("retrunTotal", retrunTotal);
        outState.putDouble("totalDiscount", totalDiscount);
        outState.putString("startTime", startTime);
        outState.putString("TotalValue", totalValue);


    }


    private void setBundleData(Bundle bundlData) {

        returnProductsArray = bundlData.getParcelableArrayList("returnProductsArray");
        selectedProductsArray = bundlData.getParcelableArrayList("selectedProductsArray");

        saveBtnFlag = bundlData.getBoolean("saveBtnFlag");

        itineraryId = bundlData.getString("itineraryId");
        pharmacyId = bundlData.getString("pharmacyId");
        custName = bundlData.getString("custName");
        custAddress = bundlData.getString("custAddress");
        productId = bundlData.getString("productId");
        onTimeOrNot = bundlData.getString("onTimeOrNot");

        cash = bundlData.getString("cash");
        credit = bundlData.getString("credit");
        productNames = bundlData.getStringArray("productNames");
        discount = bundlData.getString("discount");
        needToPay = bundlData.getString("needToPay");
        manualFreeEnable= bundlData.getBoolean("ManualFreeEnable");
        paymentOption = bundlData.getString("paymentOption");
        //
        totalPrice = bundlData.getString("totalPrice");
        totalQuantity = bundlData.getString("totalQuantity");
        cheque = bundlData.getString("cheque");
        invoiceNumber = bundlData.getString("invoiceNumber");

        issueMode = bundlData.getString("issueMode");
        collectionDate = bundlData.getString("collectionDate");
        releaseDate = bundlData.getString("releaseDate");
        chequeNumber = bundlData.getString("chequeNumber");
        creditDuration = bundlData.getString("creditDuration");

        flagFromInvoiceGen = bundlData.getBoolean("flagFromInvoiceGen");
        isReturnSaved = bundlData.getBoolean("isReturnSaved");
        chequeEnabled = bundlData.getBoolean("chequeEnabled");
        totalDiscount = bundlData.getDouble("totalDiscount");
        retrunTotal = bundlData.getDouble("retrunTotal");
        startTime = bundlData.getString("startTime");

        totalValue = bundlData.getString("TotalValue");


        Log.i("retrunTotal ->", "" + retrunTotal);
        tvTotalAmount.setText(String.format("%.2f", retrunTotal));
        tvTotalDiscount.setText(String.format("%.2f", totalDiscount));


//		returnProducts = new ArrayList<String[]>();
//		for (ReturnProduct returns: returnProductsArray) {
//			String[] returnDetails = new String[10];
//			returnDetails[0] = String.valueOf(returns.getInvoiceNumber());
//			returnDetails[1] = returns.getDescription();
//			returnDetails[2] = returns.getBatch();
//			returnDetails[3] = String.valueOf(returns.getUnitPrice());
//			returnDetails[4] = String.valueOf(returns.getQuantity());
//			returnDetails[5] = String.valueOf(returns.getQuantity() * returns.getUnitPrice());
//			returnDetails[6] = returns.getProductId();
//			returnDetails[7] = returns.getIssueMode();
//			returnDetails[8] = String.valueOf(returns.getFree());
//			returnDetails[9] = returns.getReturnValidated();
//			returnProducts.add(returnDetails);
//		}
//		returnProductsArray = new ArrayList<ReturnProduct>();


//		Log.w("returnProducts","################ 2:"+ returnProducts.size() + "");
//
//		Log.w("returnProductsArray","################ 2:"+ returnProductsArray.size() + "");
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

    private void updateLabel() {

        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        edExpiryDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // if(parent.getId() == R.id.spInvoiceNumber) {
        tvCrediAmount.setText(loadCreditAmount(spInvoiceNumber.getSelectedItem().toString()));
        // }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private String formatDate(Date date) {

        String myFormat = "MM/dd/yyyy HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        return sdf.format(date);
    }


    private void saveRturnIfOk(boolean inList) {

        txtReturnQuantity.setEnabled(false);
        invoiceNumber = tViewInvoiceNumber.getText().toString();
        String description = txtProduct.getText().toString();
        String batch = txtBatches.getText().toString();
        String unitPrice = txtUnitPrice.getText().toString();
        String returnQty = txtReturnQuantity.getText().toString();
        String dicountValue = edPercentagevalue.getText().toString();
        String credit = tvCrediAmount.getText().toString();
        if (dicountValue.isEmpty()) {
            dicountValue = "0";
        }
        if (credit.isEmpty()) {
            credit = "0";
        }
        double retVal = 0.0;

        retVal = (Double.parseDouble(unitPrice) * Double.parseDouble(txtReturnQuantity.getText().toString())) - Double.parseDouble(dicountValue);

        retrunTotal += retVal;
        totalDiscount += Double.parseDouble(dicountValue);


        String returnValue = String.format("%.2f", retVal);
        String free = txtFree.getText().toString();


        int returnType = rGroupOptions.getCheckedRadioButtonId();

        switch (returnType) {
            case R.id.rbResalable:
                //issueMode = "resalable";
                if (Integer.parseInt(free) > 0) {
                    issueMode = "SF";
                } else {
                    issueMode = "SR";
                }
                break;
            case R.id.rbCompanyReturns:
                //issueMode = "company_returns";
                if (Integer.parseInt(free) > 0) {
                    issueMode = "CF";
                } else {
                    issueMode = "CR";
                }
                break;
            case R.id.rbExpiredReturns:
                if (Integer.parseInt(free) > 0) {
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
        returnDetails[8] = free;
        returnDetails[9] = dicountValue;
        returnDetails[10] = pid;
        returnDetails[11] = edExpiryDate.getText().toString();
        returnDetails[12] = txtUnitPrice.getText().toString();//selling
        returnDetails[13] = edRetailPrice.getText().toString();//retail
        returnDetails[14] = pPrice;
        returnDetails[15] = edDisPercentage.getText().toString();
        // returnProducts.add(returnDetails);


        Log.w("NiGGA", "You're here");
        // ArrayList<String[]> returnProductstest = new ArrayList<String[]>();


//        if (!tempreturnProducts.isEmpty()) {
//            //  returnProducts.clear();
//            returnProducts = new ArrayList<String[]>();
//           returnProducts = tempreturnProducts;
//        }

        for (int i = 0; i < tempreturnProducts.size(); i++) {
            String[] r = tempreturnProducts.get(i);
            if ((r[2].contentEquals(returnDetails[2]))) {
                Toast alreadyAdded = Toast.makeText(ReturnProductNoHistoryActivity.this, "This product has already been added!", Toast.LENGTH_SHORT);
                alreadyAdded.setGravity(Gravity.TOP, 100, 100);
                alreadyAdded.show();
                inList = true;
                retrunTotal -= retVal;
                totalDiscount -= Double.parseDouble(dicountValue);
            }


        }
        if (!inList)
            tempreturnProducts.add(returnDetails);


        if (returnProducts.isEmpty()) {
            if (!((txtReturnQuantity.getText().toString().contentEquals("0")) || (txtReturnQuantity.getText().toString().isEmpty()))) {
                tblProductReturns.removeAllViews();
                // tempreturnProducts.add(returnDetails);
                returnProducts.add(returnDetails);
                populateTable(returnProducts);
                addBtnFlag = true;
                tvTotalAmount.setText(String.format("%.2f", retrunTotal));
                tvTotalDiscount.setText(String.format("%.2f", totalDiscount));
                ArrayList<String> nullBatchList = new ArrayList<String>();
                setBatchListAdapter(nullBatchList);
                txtProduct.setText("");
                txtBatches.setText("");
                txtReturnQuantity.setText("0");
                txtFree.setText("0");
                txtUnitPrice.setText("");
                tViewTotalReturns.setText("");
                edRetailPrice.setText("");
                edPercentagevalue.setText("0.00");
                edDisPercentage.setText("0");
            } else {
                Toast selectionEmpty = Toast.makeText(ReturnProductNoHistoryActivity.this, "Please select a valid quantity!", Toast.LENGTH_SHORT);
                selectionEmpty.setGravity(Gravity.TOP, 100, 100);
                selectionEmpty.show();
            }
        } else {

            if (!((txtReturnQuantity.getText().toString().contentEquals("0")) || (txtReturnQuantity.getText().toString().isEmpty()))) {
                //for (int i=0; i < returnProducts.size(); i++) {
                //String[] r = returnProducts.get(i);
                //if ((r[2].contentEquals(returnDetails[2]))) {
                //Toast alreadyAdded = Toast.makeText(ReturnProductNoHistoryActivity.this, "This product has already been added!", Toast.LENGTH_SHORT);
                //alreadyAdded.setGravity(Gravity.TOP, 100, 100);
                //alreadyAdded.show();
                //inList = true;
                //}
                //}
            } else {
                Toast selectionEmpty = Toast.makeText(ReturnProductNoHistoryActivity.this, "Please select a valid quantity!", Toast.LENGTH_SHORT);
                selectionEmpty.setGravity(Gravity.TOP, 100, 100);
                selectionEmpty.show();
            }
            if (!inList) {
                tblProductReturns.removeAllViews();
                //tempreturnProducts.add(returnDetails);
                returnProducts.add(returnDetails);
                populateTable(returnProducts);
                addBtnFlag = false;
                tvTotalAmount.setText(String.format("%.2f", retrunTotal));
                tvTotalDiscount.setText(String.format("%.2f", totalDiscount));
                ArrayList<String> nullBatchList = new ArrayList<String>();
                setBatchListAdapter(nullBatchList);
                txtProduct.setText("");
                txtBatches.setText("");
                txtReturnQuantity.setText("0");
                txtFree.setText("0");
                txtUnitPrice.setText("");
                tViewTotalReturns.setText("");
                edRetailPrice.setText("");
                edDisPercentage.setEnabled(false);
                edPercentagevalue.setEnabled(false);
            }


        }
        isReturnSaved = false;
        btnSaveReturns.setEnabled(true);
        btnPrint.setEnabled(false);
        btnCancel.setText("Cancel");
    }
}
