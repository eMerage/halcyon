package com.halcyon.channelbridge;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Approval_Details;
import com.halcyon.channelbridgedb.Approval_Persons;
import com.halcyon.channelbridgedb.Branch;
import com.halcyon.channelbridgedb.CreditPeriod;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.InvoicePaymentType;
import com.halcyon.channelbridgedb.Master_Banks;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.Sequence;
import com.halcyon.channelbridgews.WebService;

public class InvoiceGen2Activity extends Activity {
    static final int COLLECTED_DATE = 0;
    static final int REALIZE_DATE = 1;
    static boolean saveFlag; // this one is used to stop the save button getting enabled when putting in to sleep and again opens.
    Button btnGenerateInvoice, btnReturns, btnDiscard;
    ImageButton iBtnChequeDetails;
    RadioGroup rGroupPaymentOption;
    TextView tViewInvoiceNo, tViewCustomerName, tViewInvoiceQuantity, tViewInvoiceGross, tViewNetInvoiceAmount, tViewReturnsGross, tViewReturnsDiscount, tViewNetReturnAmount, tViewNetPayable;
    TextView labelCash, labelCheque, labelCredit, tViewRemain;
    TextView tViewCollectedDate, tViewRealizedDate;
    Dialog chequeDetailsPopup;
    Spinner spinCreditPeriod;//spInvoType
    EditText txtCheque, txtCash, txtCredit, txtDiscountPercentage, txtDiscountValue;
    String rowId, pharmacyId;
    ArrayList<String> creditList;
    ArrayList<String> bankList;
    ArrayList<String> branchList;
    String paymentOption, cash = "0", credit = "0", remain = "0", totalPrice = "0", marketReturns = "0", discount = "0", needToPay = "0", totalQty = "0", cheque = "0", invoiceNumber = "0", returnedInvoice = "-1";
    String chequeNumber = "0", collectionDate = "", releaseDate = "", creditDuration = "";
    double temporaryAmountToPay = 0;
    double temporaryNeedToPay;
    boolean discountEditable = true;
    boolean discountEntered = false;
    boolean chequeEnabled = false, chequeAllowed = false, creditAllowd = false;
    String startTime = "";
    ArrayList<String[]> selectedData = new ArrayList<String[]>();
    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();
    ArrayList<ReturnProduct> returnProductsArray = new ArrayList<ReturnProduct>();
    private int collectedYear;
    private int collectedMonth;
    private int collectedDay;
    private int selectedCreditPeriodIndex;
    private Boolean isInvoiceOption1 = false;
    Spinner spBank, spBranch;
    Master_Banks bankController;
    private String selectedBank = "", selectedBranch = "";
    private DatePickerDialog.OnDateSetListener collectedDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            collectedYear = year;
            collectedMonth = monthOfYear;
            collectedDay = dayOfMonth;
            updateCollectedDateDisplay();
        }
    };
    Intent startInvoiceGen1;
    private int realizeYear;
    private int realizeMonth;
    private int realizeDay;
    private Boolean invoiceOption2Enable;
    private DatePickerDialog.OnDateSetListener realizeDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            realizeYear = year;
            realizeMonth = monthOfYear;
            realizeDay = dayOfMonth;
            updateRealizeDateDisplay();
        }
    };
    private CreditPeriod creditPeriodController;
    private Branch branchController;
    private int selectedBranchIndex, selectedBankIndex;
    private String referenceNumber;
    private Boolean isCheckEntered = false;
    List<String> paymentTypeList;
    InvoicePaymentType pt;
    String selectedInvoOption = "";
    String seletedPaymentOptionCode = "";
    String branchCode = "", cusName,totalValue;
    private static final int CAMERA_REQUEST = 1889;
    Bitmap photo;
    byte[] chequeimage = new byte[0];
    private String disountValue = "0";
    int totalQuantity, customertype;
    Customers customersHandler;
    private Boolean isBlockerActivated;
    String[] customerDetails;
    Button btncreditAllow;

    boolean manualFreeEnable;

    //  double discountValue = 0.0;
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_gen_2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tViewInvoiceGross = (TextView) findViewById(R.id.tvInvoiceGross);
        tViewNetInvoiceAmount = (TextView) findViewById(R.id.tvNetInvoiceAmount);
        tViewReturnsGross = (TextView) findViewById(R.id.tvReturnGross);
        tViewReturnsDiscount = (TextView) findViewById(R.id.tvReturnDiscount);
        tViewNetReturnAmount = (TextView) findViewById(R.id.tvNetReturnAmount);
        tViewNetPayable = (TextView) findViewById(R.id.tvNetPayable);
        tViewInvoiceNo = (TextView) findViewById(R.id.tvInvoiceNo);
        tViewInvoiceQuantity = (TextView) findViewById(R.id.tvInvoiceQty);
        spinCreditPeriod = (Spinner) findViewById(R.id.sCreditPeriod);
        labelCash = (TextView) findViewById(R.id.labelCash);
        labelCheque = (TextView) findViewById(R.id.labelCheque);
        labelCredit = (TextView) findViewById(R.id.labelCredit);
        tViewRemain = (TextView) findViewById(R.id.tvRemain);
        txtCash = (EditText) findViewById(R.id.etCash);
        // spInvoType = (Spinner)findViewById(R.id.spInvOption);
        txtCheque = (EditText) findViewById(R.id.etCheque);
        txtDiscountPercentage = (EditText) findViewById(R.id.etDiscountPercentage);
        txtDiscountValue = (EditText) findViewById(R.id.etDiscountValue);
        txtCredit = (EditText) findViewById(R.id.etCredit);

        btncreditAllow = (Button) findViewById(R.id.button_creditAllow);

        btnGenerateInvoice = (Button) findViewById(R.id.bGenerateInvoice);
        btnDiscard = (Button) findViewById(R.id.bDiscard);
        btnReturns = (Button) findViewById(R.id.bProductReturns);
        iBtnChequeDetails = (ImageButton) findViewById(R.id.bChequeDetails);
        creditList = new ArrayList<>();
        branchList = new ArrayList<>();
        saveFlag = true;
        bankController = new Master_Banks(InvoiceGen2Activity.this);
        branchController = new Branch(InvoiceGen2Activity.this);
        bankList = new ArrayList<>();
        chequeDetailsPopup = new Dialog(InvoiceGen2Activity.this);
        chequeDetailsPopup.setContentView(R.layout.cheque_details_popup);
        totalQuantity = 0;
        referenceNumber = new String();
        customersHandler = new Customers(InvoiceGen2Activity.this);
        if (savedInstanceState != null) {
            getDataFromPreviousActivity(savedInstanceState);
        } else {
            getDataFromPreviousActivity(getIntent().getExtras());
        }

        SharedPreferences sharedCB = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);

        chequeAllowed = false;


        isBlockerActivated = (sharedCB.getBoolean("ISOutStandingBlock", true));
//        paymentTypeList = new ArrayList<>();
        pt = new InvoicePaymentType(InvoiceGen2Activity.this);
        customerDetails = new String[20];
//        pt.openReadableDatabase();
//        if (pt.get_rowcount() > 0) {
//            try {
//                paymentTypeList = pt.loadPayment_Type();
//            } catch (Exception e) {
//
//            }
//        }
//        pt.closeDatabase();
//        ArrayAdapter<String> payOptionAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.single_list_item,paymentTypeList);
//        spInvoType.setAdapter(payOptionAdapter);
        creditPeriodController = new CreditPeriod(getApplicationContext());
        creditList = creditPeriodController.getCreditPeriodList();
//        spInvoType.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedInvoOption =  spInvoType.getSelectedItem().toString();
//                pt.openReadableDatabase();
//                seletedPaymentOptionCode  = pt.GetPyementtypeCode(selectedInvoOption);
//                pt.closeDatabase();
//                Log.i("OptionCode -O->2 ",""+seletedPaymentOptionCode);
//                switch (selectedInvoOption){
//                    case "Cash":
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(false);
//                        break;
//                    case "Cheque":
//                        txtCash.setEnabled(false);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                    case "Cash+Cheque":
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                    default:
//                        txtCash.setEnabled(true);
//                        iBtnChequeDetails.setEnabled(true);
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        txtCredit.setEnabled(false);

        setCreditSpinner(creditList);
        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        invoiceOption2Enable = (shared.getBoolean("InvoiceOption", true));
        txtDiscountPercentage.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (!s.toString().isEmpty()) {
                    discount = s.toString();
                    txtCash.setText("0");
                    txtCheque.setText("0");
                    final EditText txtChequeAmount0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
                    final EditText txtChequeNumber0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
                    cheque = "0";
                    chequeNumber = "0";
                    txtChequeAmount0.setText("0");
                    txtChequeNumber0.setText("");
                } else {
                    discount = "0";
                }

                if (!discount.isEmpty()) {
                    double discountPercentage = 0;
                    if (!txtDiscountPercentage.getText().toString().isEmpty()) {
                        discountPercentage = Double.parseDouble(txtDiscountPercentage.getText().toString());
                    } else {
                        discountPercentage = 0;
                    }
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRet = 0;
                    discount = txtDiscountPercentage.getText().toString();
                    try {
                        marketRet = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }
                    if ((discountPercentage <= 100)) {
                        if ((discountPercentage >= 0)) {
                            double discountValue = (discountPercentage / 100) * totalAmount;
                            txtDiscountValue.setText(String.format("%.2f", discountValue));
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRet);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            if(netPayable>0){
                                txtCredit.setText(String.format("%.2f", netPayable));
                            }else {
                                txtCredit.setText("0");
                            }


                        } else {

                            txtDiscountPercentage.setText("0");
                            txtDiscountPercentage.requestFocus();

                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Percentage cannot be less than 0!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }
                    } else {
                        txtDiscountPercentage.setText("0");
                        txtDiscountPercentage.requestFocus();
                        Toast discountPercentageMax = Toast.makeText(getApplication(), "Discount Percentage cannot be greater than 100!", Toast.LENGTH_SHORT);
                        discountPercentageMax.show();
                    }

                } else {
                    txtDiscountValue.setText("");
                }

//                if (!s.toString().isEmpty()) {
//                    txtCash.setText("0");
//                    txtCheque.setText("0");
//                    final EditText txtChequeAmount0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
//                    final EditText txtChequeNumber0 = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
//                    cheque = "0";
//                    chequeNumber = "0";
//                    txtChequeAmount0.setText("0");
//                    txtChequeNumber0.setText("");
//                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {

            }
        });


        btncreditAllow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (creditAllowd == true) {
                    Customers customersObject = new Customers(InvoiceGen2Activity.this);
                    customersObject.openReadableDatabase();

                    if (customersObject.getCustomerBlockStatesByPharmacyId(pharmacyId).equals("0")) {
                        showDialogSendMessage(InvoiceGen2Activity.this, 4);
                    } else {

                        showDialogSendMessage(InvoiceGen2Activity.this, 2);
                    }

                } else {
                    showDialogSendMessage(InvoiceGen2Activity.this, 2);

                }

            }
        });

        txtCredit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() || !charSequence.toString().equals("")) {
                    Double totel;
                    if (txtCheque.getText().toString().equals("0")) {
                        totel = Double.parseDouble(tViewNetPayable.getText().toString()) - Double.parseDouble(charSequence.toString());
                    } else {
                        totel = Double.parseDouble(tViewNetPayable.getText().toString()) - (Double.parseDouble(charSequence.toString()) + Double.parseDouble(txtCheque.getText().toString()));
                    }

                    if (customertype == 0) {
                        txtCash.setText(String.valueOf(totel));
                    } else {

                    }


                } else {

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        txtCash.addTextChangedListener(new TextWatcher() {
            double totalAmount = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (!s.toString().equals("0")) {

                    if (!s.toString().equals("") || !s.toString().isEmpty()) {
                        if (txtCheque.getText().equals("0")) {
                            totalAmount = Double.parseDouble(tViewNetInvoiceAmount.getText().toString()) - Double.parseDouble(s.toString());
                        } else {

                            try {
                                totalAmount = Double.parseDouble(tViewNetInvoiceAmount.getText().toString()) - (Double.parseDouble(txtCheque.getText().toString()) + Double.parseDouble(s.toString()));
                            }catch (Exception ex){
                                totalAmount = Double.parseDouble(tViewNetInvoiceAmount.getText().toString()) - 0.0;

                            }

                        }
                        if (customertype == 0) {

                        } else {

                            if(totalAmount>0){
                                txtCredit.setText(String.format("%.2f", totalAmount));
                            }else {
                                txtCredit.setText("0");
                            }

                        }


                    } else {

                    }

                } else {

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        txtCheque.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double chequeValue = 0;
                double cash = 0;
                if (!s.toString().isEmpty() || !s.toString().equals("")) {

                    try {
                        chequeValue = Double.parseDouble(s.toString());
                    } catch (NumberFormatException e) {
                        Log.i("Num exception", e.toString());
                    }

                }
                if (!txtCash.getText().toString().isEmpty() || !txtCash.getText().toString().equals("")) {
                    try {
                        cash = Double.parseDouble(txtCash.getText().toString());
                    } catch (NumberFormatException e) {
                        Log.i("Num exception", e.toString());
                    }
                }
//                double totalAmount1 = Double.parseDouble(tViewNetInvoiceAmount.getText().toString()); him
                //       double deductingValue = chequeValue + cash;  him
                //      totalAmount1 -=  deductingValue;   him
                //     txtCredit.setText(String.format("%.2f", totalAmount1));  him
            }
        });

        txtDiscountValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub


                if (!txtDiscountValue.getText().toString().isEmpty()) {
                    double discountValue = Double.parseDouble(txtDiscountValue.getText().toString());
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRet = 0;
                    try {
                        marketRet = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }

                    if (!(discountValue == 0)) {
                        if (discountValue < totalAmount) {
                            double discountPercentage = 100 - (((totalAmount - discountValue) / totalAmount) * 100);
                            txtDiscountPercentage.setText(String.format("%.2f", discountPercentage));
                            discount = txtDiscountPercentage.getText().toString();
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRet);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            if(netPayable>0){
                                txtCredit.setText(String.format("%.2f", netPayable));
                            }else {
                                txtCredit.setText("0");
                            }


                        } else {
                            txtDiscountValue.setText("0");
//							txtDiscountValue.requestFocus();
                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Value cannot be greater than Total!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }

                    }
                }


            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub


                if (isInvoiceOption1) {
                    startInvoiceGen1 = new Intent("com.halcyon.channelbridge.INVOICEGEN1ACTIVITY");
                } else {
                    startInvoiceGen1 = new Intent("com.halcyon.channelbridge.INVOICEGEN1ALTERNATE");
                }
                Bundle bundleToView = new Bundle();
                bundleToView.putString("Id", rowId);
                bundleToView.putString("PharmacyId", pharmacyId);
                bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);
                if (chequeEnabled) {
                    bundleToView.putString("ChequeNumber", chequeNumber);
                    bundleToView.putString("CollectionDate", collectionDate);
                    bundleToView.putString("ReleaseDate", releaseDate);
                }
                bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                bundleToView.putString("startTime", startTime);
                bundleToView.putString("TotalValue", tViewInvoiceGross.getText().toString());

                startInvoiceGen1.putExtras(bundleToView);
                finish();
                startActivity(startInvoiceGen1);


            }
        });

        btnGenerateInvoice.setOnClickListener(new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      // TODO Auto-generated method stub
                                                      if (creditDuration.equals("")) {
                                                          Toast t = Toast.makeText(getApplicationContext(), "Please select credit duration", Toast.LENGTH_SHORT);
                                                          t.show();

                                                      } else {
                                                          gotoGen3();

                                                      }
                                                  }
                                              }

        );

        btnReturns.setOnClickListener(new View.OnClickListener()

                                      {

                                          public void onClick(View v) {
                                              // TODO Auto-generated method stub


                                              discount = txtDiscountPercentage.getText().toString();
                                              AlertDialog.Builder returnPopup = new AlertDialog.Builder(InvoiceGen2Activity.this);
                                              returnPopup.setMessage("Where do you want to go?")
                                                      .setPositiveButton("Return with history validation", new DialogInterface.OnClickListener() {
                                                          public void onClick(DialogInterface dialog, int id) {
                                                              Intent startProductReturns = new Intent(getApplication(), ReturnProductHistoryActivity.class);
                                                              Bundle bundleToView = new Bundle();
                                                              bundleToView.putString("Id", rowId);
                                                              bundleToView.putString("PharmacyId", pharmacyId);
                                                              bundleToView.putString("Cash", cash);
                                                              bundleToView.putString("Credit", credit);
                                                              bundleToView.putString("onTimeOrNot", "" + 0);
                                                              bundleToView.putString("MarketReturns", marketReturns);
                                                              bundleToView.putString("Discount", discount);
                                                              bundleToView.putString("Cheque", cheque);
                                                              bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);

                                                              if (chequeEnabled) {
                                                                  bundleToView.putString("ChequeNumber", chequeNumber);
                                                                  bundleToView.putString("CollectionDate", collectionDate);
                                                                  bundleToView.putString("ReleaseDate", releaseDate);
                                                              }
                                                              bundleToView.putString("NeedToPay", needToPay);
                                                              bundleToView.putString("PaymentOption", paymentOption);
                                                              bundleToView.putString("TotalPrice", totalPrice);
                                                              bundleToView.putString("TotalQuantity", totalQty);
                                                              bundleToView.putString("InvoiceNumber", invoiceNumber);
                                                              bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                              bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                              bundleToView.putString("startTime", startTime);
                                                              bundleToView.putString("TotalValue", tViewInvoiceGross.getText().toString());
                                                              startProductReturns.putExtras(bundleToView);
                                                              finish();
                                                              startActivity(startProductReturns);
                                                          }
                                                      })
                                                      .setNegativeButton("Return without validation", new DialogInterface.OnClickListener() {
                                                          public void onClick(DialogInterface dialog, int id) {
                                                              SharedPreferences preferences = PreferenceManager
                                                                      .getDefaultSharedPreferences(getBaseContext());
                                                              boolean history = preferences.getBoolean("cbPrefEnableNoHistoryReturns", true);
                                                              if (history) {
                                                                  Intent startProductReturns = new Intent(getApplication(), ReturnProductNoHistoryActivity.class);
                                                                  Bundle bundleToView = new Bundle();
                                                                  bundleToView.putString("Id", rowId);
                                                                  bundleToView.putString("PharmacyId", pharmacyId);
                                                                  bundleToView.putString("onTimeOrNot", "" + 0);
                                                                  bundleToView.putString("Cash", cash);
                                                                  bundleToView.putString("Credit", credit);
                                                                  bundleToView.putString("MarketReturns", marketReturns);
                                                                  bundleToView.putString("Discount", discount);
                                                                  bundleToView.putString("Cheque", cheque);
                                                                  bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);

                                                                  if (chequeEnabled) {
                                                                      bundleToView.putString("ChequeNumber", chequeNumber);
                                                                      bundleToView.putString("CollectionDate", collectionDate);
                                                                      bundleToView.putString("ReleaseDate", releaseDate);
                                                                  }
                                                                  bundleToView.putString("NeedToPay", needToPay);
                                                                  bundleToView.putString("PaymentOption", paymentOption);
                                                                  bundleToView.putString("TotalPrice", totalPrice);
                                                                  bundleToView.putString("TotalQuantity", totalQty);
                                                                  bundleToView.putString("InvoiceNumber", invoiceNumber);
                                                                  bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                                                                  bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                                                                  bundleToView.putString("startTime", startTime);
                                                                  bundleToView.putString("TotalValue", tViewInvoiceGross.getText().toString());
                                                                  startProductReturns.putExtras(bundleToView);
                                                                  finish();
                                                                  startActivity(startProductReturns);
                                                              } else {
                                                                  Toast featureNotEnabled = Toast.makeText(InvoiceGen2Activity.this, "Sorry, this feature has not been enabled", Toast.LENGTH_SHORT);
                                                                  featureNotEnabled.setGravity(Gravity.TOP, 100, 100);
                                                                  featureNotEnabled.show();
                                                              }
                                                          }
                                                      });
                                              returnPopup.create();
                                              returnPopup.show();


                                          }
                                      }

        );

        iBtnChequeDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customertype == 0 && chequeAllowed == false) {
                    showDialogSendMessage(InvoiceGen2Activity.this, 1);
                } else {
                    chequDilaog();

                }


            }
        });


        spinCreditPeriod.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                try {
                    creditDuration = spinCreditPeriod.getSelectedItem().toString();
                    selectedCreditPeriodIndex = arg2;

                } catch (Exception e) {
                    Log.e("InvoiceGen2: error getting data from spinCreditPeriod ", e.toString());
                }


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case COLLECTED_DATE:
                return new DatePickerDialog(this,
                        collectedDateSetListener,
                        collectedYear, collectedMonth, collectedDay);
            case REALIZE_DATE:
                return new DatePickerDialog(this,
                        realizeDateSetListener,
                        realizeYear, realizeMonth, realizeDay);
        }
        return null;
    }

    private void updateCollectedDateDisplay() {

        try {

            String finalCollectedMonth = "";
            String finalCollectedDay = "";
            if (collectedMonth + 1 < 10) {
                finalCollectedMonth = "0" + String.valueOf(collectedMonth + 1);
            } else {
                finalCollectedMonth = String.valueOf(collectedMonth + 1);
            }

            if (collectedDay < 10) {
                finalCollectedDay = "0" + String.valueOf(collectedDay);
            } else {
                finalCollectedDay = String.valueOf(collectedDay);
            }

            tViewCollectedDate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(finalCollectedMonth).append("-").append(finalCollectedDay).append("-")
                    .append(collectedYear).append(""));
        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());

        }


    }

    private void updateRealizeDateDisplay() {

        try {
            String finalRealizeMonth = "";
            String finalRealizeDay = "";
            if (realizeMonth + 1 < 10) {
                finalRealizeMonth = "0" + String.valueOf(realizeMonth + 1);
            } else {
                finalRealizeMonth = String.valueOf(realizeMonth + 1);
            }

            if (realizeDay < 10) {
                finalRealizeDay = "0" + String.valueOf(realizeDay);
            } else {
                finalRealizeDay = String.valueOf(realizeDay);
            }

            tViewRealizedDate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(finalRealizeMonth).append("-").append(finalRealizeDay).append("-")
                    .append(realizeYear).append(""));

        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from updateRealizeDateDisplay ", e.toString());
        }


    }

    private void setCreditSpinner(ArrayList<String> crList) {
        // TODO Auto-generated method stub

        try {

            ArrayAdapter<String> creditListAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_dropdown_item_1line, crList);
            creditListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinCreditPeriod.setAdapter(creditListAdapter);
            spinCreditPeriod.setSelection(selectedCreditPeriodIndex);
        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setCreditSpinner ", e.toString());

        }


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (invoiceOption2Enable == true) {
                startInvoiceGen1 = new Intent("com.halcyon.channelbridge.INVOICEGEN1ALTERNATE");
            } else {
                startInvoiceGen1 = new Intent("com.halcyon.channelbridge.INVOICEGEN1ACTIVITY");
            }
            Bundle bundleToView = new Bundle();
            bundleToView.putString("Id", rowId);
            bundleToView.putString("PharmacyId", pharmacyId);
            bundleToView.putString("startTime", startTime);
            bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);

            if (chequeEnabled) {
                bundleToView.putString("ChequeNumber", chequeNumber);
                bundleToView.putString("CollectionDate", collectionDate);
                bundleToView.putString("ReleaseDate", releaseDate);
            }
            bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
            bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
            bundleToView.putString("startTime", startTime);

            bundleToView.putString("TotalValue", tViewInvoiceGross.getText().toString());
            startInvoiceGen1.putExtras(bundleToView);
            finish();
            startActivity(startInvoiceGen1);
        }
        return super.onKeyDown(keyCode, event);


    }

    private void setInitialData(String rId, String pId) {
        // TODO Auto-generated method stub


        try {


            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);

            if (chequeEnabled) {
                iBtnChequeDetails.setEnabled(true);
                txtCheque.setEnabled(false);
                txtCheque.setHint("");
                iBtnChequeDetails.setVisibility(View.VISIBLE);
            } else {
                iBtnChequeDetails.setEnabled(false);
                txtCheque.setEnabled(true);
                txtCheque.setHint(R.string.cheque_amount);
                iBtnChequeDetails.setVisibility(View.GONE);
            }


            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();

            customerDetails = customersObject.getCustomerDetailsByPharmacyId(pharmacyId);
            tViewCustomerName.setText(customerDetails[5]);


            if (customerDetails[5] == null) {
                CustomersPendingApproval cpa = new CustomersPendingApproval(this);
                cpa.openReadableDatabase();
                String cusName = cpa.getPendingCustomerByPharmacyId(pharmacyId);
                cpa.closeDatabase();
                customertype = 0;//not approved customer
                tViewCustomerName.setText(cusName);


            } else {
                tViewCustomerName.setText(customerDetails[5]);
                customertype = 1;
                creditAllowd = true;


            }


/**
 * may be need to enabled
 */
//            creditList[0] = customerDetails[17];
//            if ((!creditDuration.isEmpty()) && (creditDuration != "")) {
//                for (int i = 0; i < creditList.size(); i++) {
//                    if (creditDuration.contentEquals(creditList[i])) {
//                        spinCreditPeriod.setSelection(i);
//                    }
//                }
//            }


            //0 - rowid //1 - pharmacyId //2 - pharmacyCode //3 - dealerId //4 - companyCode //5 - customerName //6 - address //7 - area //8 - town //9 - district
            //10 - telephone //11 - fax //12 - email //13 - customerStatus //14 - creditLimit //16 - currentCredit //16 - creditExpiryDate //17 - creditDuration
            //18 - vatNo //19 - status


            double totalP = 0.0;
            int totalQ = 0;
            Sequence sequence = new Sequence(this);

            sequence.openReadableDatabase();
            String lastInv = sequence.getLastRowId("invoice");
            sequence.closeDatabase();

            String invNum = String.valueOf(Integer.parseInt(lastInv) + 1);

            tViewInvoiceNo.setText(invNum);
            this.invoiceNumber = invNum;

            if ((!cheque.isEmpty()) || (cheque != "")) {
                txtCheque.setText(cheque);
            }


            for (SelectedProduct selectedProduct : selectedProductsArray) {
                double normalP = 0.0;
                double discounted = 0.0;
                normalP = (selectedProduct.getNormal() * selectedProduct.getPrice());
                if ((selectedProduct.getDiscount() == 0) || (selectedProduct.getDiscount() == 0.0)) {
                    discounted = normalP;
                } else {
                    discounted = normalP * ((100 - selectedProduct.getDiscount()) / 100);
                }

                totalP = totalP + discounted;
                totalPrice = String.format("%.2f", totalP);

            }


            tViewInvoiceGross.setText(String.format("%.2f", totalP));

            for (SelectedProduct selectedProduct : selectedProductsArray) {
                int normalQ = 0;
                normalQ = selectedProduct.getNormal() + selectedProduct.getFree();
                totalQ = totalQ + normalQ;
                totalQty = String.valueOf(totalQ);

            }

            tViewInvoiceQuantity.setText(String.valueOf(totalQ));
            tViewNetPayable.setText(String.format("%.2f", totalP));
            totalQuantity = totalQ;

            double marketRet = 0.0;
            if (!returnProductsArray.isEmpty()) {
                for (ReturnProduct retProduct : returnProductsArray) {

                    Log.w("Return Product: 456", "getDiscount : " + retProduct.getDiscount());
                    Log.w("Return Product: 456", "getQuantity : " + retProduct.getQuantity());
                    Log.w("Return Product: 456", "getUnitPrice : " + retProduct.getUnitPrice());

                    double value = 0.0;
                    if (retProduct.getDiscount() > 0) {
                        double normalValue = retProduct.getQuantity() * retProduct.getUnitPrice();
                        value = normalValue - ((normalValue / 100) * retProduct.getDiscount());
                    } else {
                        value = retProduct.getQuantity() * retProduct.getUnitPrice();
                    }

                    marketRet = marketRet + value;
                }
            } else {
                marketRet = 0.0;
            }

            Log.w("Return Product: 456", "marketRet : " + marketRet);

            marketReturns = String.valueOf(String.format("%.2f", marketRet));
            double totalAmt = Double.parseDouble(totalPrice);
            double needToPayAmt = totalAmt - marketRet;

            tViewReturnsGross.setText(marketReturns);
            tViewNetReturnAmount.setText(marketReturns);
            tViewNetPayable.setText(String.format("%.2f", needToPayAmt));

            if (customertype == 0) {
                txtCash.setText(String.format("%.2f", needToPayAmt));
            } else {
                if(needToPayAmt>0){
                    txtCredit.setText(String.format("%.2f", needToPayAmt));
                }else {
                    txtCredit.setText("0");
                }

                txtCredit.selectAll();
            }


            tViewReturnsDiscount.setText("0");
            tViewNetInvoiceAmount.setText(String.format("%.2f", totalP));

            for (SelectedProduct sProduct : selectedProductsArray) {
                if (sProduct.getDiscount() != 0) {
                    discountEditable = false;
                }
            }

            if (discountEditable || discountEntered) {
                txtDiscountValue.setEnabled(true);
                txtDiscountPercentage.setEnabled(true);


                if (!discount.isEmpty()) {
                    double discountPercentage = 0;
                    if (!txtDiscountPercentage.getText().toString().isEmpty()) {
                        discountPercentage = Double.parseDouble(txtDiscountPercentage.getText().toString());
                    } else {
                        discountPercentage = 0;
                    }
                    double totalAmount = Double.parseDouble(totalPrice);
                    double marketRetn = 0;
                    discount = txtDiscountPercentage.getText().toString();
                    try {
                        marketRetn = Double.parseDouble(marketReturns);
                    } catch (Exception e) {
                        Log.w("Error from market ret", e.toString());
                    }
                    if (!(discountPercentage > 100)) {
                        if (!(discountPercentage < 0)) {
                            double discountValue = (discountPercentage / 100) * totalAmount;
                            txtDiscountValue.setText(String.format("%.2f", discountValue));
                            double needToPayAmount = totalAmount - (discountValue);
                            tViewNetInvoiceAmount.setText(String.format("%.2f", needToPayAmount));
                            double netPayable = totalAmount - (discountValue + marketRetn);
                            tViewNetPayable.setText(String.format("%.2f", netPayable));

                            if (customertype == 0) {
                                txtCredit.setText("0");

                            } else {

                                if(needToPayAmt>0){
                                    txtCredit.setText(String.format("%.2f", needToPayAmt));
                                }else {
                                    txtCredit.setText("0");
                                }

                            }


                        } else {
                            Toast discountPercentageMin = Toast.makeText(getApplication(), "Discount Percentage cannot be less than 0!", Toast.LENGTH_SHORT);
                            discountPercentageMin.show();
                        }
                    } else {
                        Toast discountPercentageMax = Toast.makeText(getApplication(), "Discount Percentage cannot be greater than 100!", Toast.LENGTH_SHORT);
                        discountPercentageMax.show();
                    }

                } else {
                    txtDiscountValue.setText("");
                }


            } else {
                txtDiscountValue.setEnabled(false);
                txtDiscountPercentage.setEnabled(false);
            }

            if (customertype == 0) {
                txtCash.setText(tViewNetPayable.getText().toString());

            } else {
                txtCash.setText(cash);
                txtCheque.setText(cheque);
            }


        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());

        }


    }

    private void getDataFromPreviousActivity(Bundle extras) {
        // TODO Auto-generated method stub
        // try {
        referenceNumber = extras.getString("referenceNumber");
        rowId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
        selectedProductsArray = extras.getParcelableArrayList("SelectedProducts");
        selectedCreditPeriodIndex = extras.getInt("selectedCreditIndex");
        selectedBranchIndex = extras.getInt("selectedBranchIndex");
        selectedBankIndex = extras.getInt("selectedBankIndex");
        startTime = extras.getString("startTime");
        totalPrice = extras.getString("TotalPrice");

        manualFreeEnable = extras.getBoolean("ManualFreeEnable");
        //On Log.i("time gen2 -e->",startTime);
        if (extras.containsKey("Cash")) {
            cash = extras.getString("Cash");
        }

        if (extras.containsKey("Cheque")) {
            cheque = extras.getString("Cheque");
        }

        if (extras.containsKey("ReturnProducts")) {
            returnProductsArray = extras.getParcelableArrayList("ReturnProducts");
        }
        if (extras.containsKey("CreditDuration")) {
            creditDuration = extras.getString("CreditDuration");
        }


        if (extras.containsKey("Discount")) {
            txtDiscountPercentage.setText(extras.getString("Discount"));
            txtDiscountPercentage.setFocusable(true);
            txtDiscountPercentage.requestFocus();
            //  txtCredit.setFocusable(true);
            //  txtCredit.requestFocus();
            Log.w("discount UIPO((B$%&%976", String.valueOf(extras.containsKey("Discount")));
        }

        if (extras.containsKey("DiscountEntered")) {
            discountEntered = extras.getBoolean("DiscountEntered");
        }


        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);


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


            totalValue = extras.getString("TotalValue");
            Log.w("cheque details", chequeNumber + " # " + collectionDate + " # " + releaseDate);
        }


        setInitialData(rowId, pharmacyId);


        Log.w("invoicegen2", "selectedProductsArray size : " + selectedProductsArray.size());
        Log.w("invoicegen2", "returnProductArray size : " + returnProductsArray.size());
//        } catch (Exception e) {
//            Log.e("InvoiceGen2: error getting data from prev activity ", e.toString());
//        }

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);


        try {

            outState.putString("Id", rowId);
            outState.putString("PharmacyId", pharmacyId);
            outState.putString("Cash", cash);
            outState.putString("Credit", credit);
            outState.putString("Cheque", cheque);
            outState.putString("startTime", startTime);
            outState.putString("disountValue", disountValue);
            outState.putString("MarketReturns", marketReturns);
            outState.putString("Discount", discount);
            outState.putString("NeedToPay", needToPay);
            outState.putString("CreditDuration", creditDuration);
            outState.putString("PaymentOption", paymentOption);
            outState.putString("TotalPrice", totalPrice);
            outState.putString("TotalQuantity", totalQty);
            outState.putString("InvoiceNumber", invoiceNumber);
            outState.putBoolean("DiscountEntered", discountEntered);
            outState.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
            outState.putParcelableArrayList("SelectedProducts", selectedProductsArray);
            outState.putParcelableArrayList("ReturnProducts", returnProductsArray);
            outState.putInt("selectedBranchIndex", selectedBranchIndex);
            outState.putInt("selectedBankIndex", selectedBankIndex);
            outState.putBoolean("ManualFreeEnable", manualFreeEnable);
            if (chequeEnabled) {
                outState.putString("ChequeNumber", chequeNumber);
                outState.putString("CollectionDate", collectionDate);
                outState.putString("ReleaseDate", releaseDate);
            }
            outState.putString("TotalValue", tViewInvoiceGross.getText().toString());

        } catch (Exception e) {
            Log.e("InvoiceGen2: error getting data from setInitialData ", e.toString());
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            chequeimage = getBytes(photo);
            // Im.setImageBitmap(photo);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();


        spinCreditPeriod.setSelection(selectedCreditPeriodIndex);


        if (customertype == 0) {
            txtCash.setText(tViewInvoiceGross.getText().toString());
        } else {
            txtCash.setText(cash);
            txtCheque.setText(cheque);
        }


        // txtCash.setText("10000");
        // txtCheque.setText("25000");
        // txtDiscountPercentage.setText(discount);


    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }


    private void gotoGen3() {


        Intent startInvoiceGen3 = new Intent("com.halcyon.channelbridge.INVOICEGEN3ACTIVITY");
        Bundle bundleToView = new Bundle();
        try {
            creditDuration = spinCreditPeriod.getSelectedItem().toString();
        } catch (Exception e) {
            creditDuration = "0";

        }

        cash = txtCash.getText().toString();
        cheque = txtCheque.getText().toString();
        credit = txtCredit.getText().toString();
        discount = txtDiscountPercentage.getText().toString();
        if (discount == null || discount.contentEquals("null") || discount.length() == 0) {
            discount = "0";
        }


        if (!txtDiscountPercentage.getText().toString().isEmpty()) {
            discountEntered = true;
        }
        if (!txtDiscountValue.getText().toString().equals("") || !txtDiscountValue.getText().toString().isEmpty()) {
            disountValue = txtDiscountValue.getText().toString();
        }

        needToPay = tViewNetPayable.getText().toString();
        if (cash.equals("")) {
            cash = "0";
        }
        if (cheque.equals("")) {
            cheque = "0";
        }
        double nuMericCash = Double.parseDouble(cash);
        double nuMericCheque = Double.parseDouble(cheque);
        if (nuMericCash > 0 && nuMericCheque > 0) {
            selectedInvoOption = "Cash+Cheque";
        } else if (nuMericCash > 0) {
            selectedInvoOption = "Cash";
        } else if (nuMericCheque > 0) {
            selectedInvoOption = "Cheque";
        }
        pt.openReadableDatabase();
        seletedPaymentOptionCode = pt.GetPyementtypeCode(selectedInvoOption);
        pt.closeDatabase();
        Log.w("IG3", "MarketReturns 333 " + marketReturns);

        bundleToView.putString("Id", rowId);
        bundleToView.putString("PharmacyId", pharmacyId);
        bundleToView.putString("Cash", cash);
        bundleToView.putString("Credit", credit);
        bundleToView.putString("Cheque", cheque);
        bundleToView.putString("onTimeOrNot", "" + 0);
        Log.w("CHECK VALUE $$$$$$$$$$$###############", cheque);
        bundleToView.putString("MarketReturns", marketReturns);
        bundleToView.putString("Discount", discount);
        bundleToView.putString("NeedToPay", needToPay);
        bundleToView.putString("CreditDuration", creditDuration);
        bundleToView.putString("PaymentOption", paymentOption);
        bundleToView.putString("TotalPrice", totalPrice);
        bundleToView.putString("TotalQuantity", totalQty);
        bundleToView.putString("InvoiceNumber", invoiceNumber);
        bundleToView.putBoolean("DiscountEntered", discountEntered);
        bundleToView.putInt("selectedCreditIndex", selectedCreditPeriodIndex);
        bundleToView.putInt("selectedBranchIndex", selectedBranchIndex);
        bundleToView.putInt("selectedBankIndex", selectedBankIndex);
        bundleToView.putString("branchCode", branchCode);
        bundleToView.putByteArray("chequeimage", chequeimage);
        bundleToView.putString("dicountValue", txtDiscountValue.getText().toString());
        bundleToView.putInt("totalQuantity", totalQuantity);
        bundleToView.putBoolean("ManualFreeEnable", manualFreeEnable);

        //add by thunder
        bundleToView.putString("BankName", selectedBank);
        bundleToView.putString("Branch", selectedBranch);
        if (referenceNumber == null || referenceNumber.isEmpty()) {
            referenceNumber = "";
        }
        bundleToView.putString("referenceNumber", referenceNumber);
        bundleToView.putBoolean("isCheckEntered", isCheckEntered);
        bundleToView.putString("selectedInvoOption", selectedInvoOption);
        bundleToView.putString("seletedPaymentOptionCode", seletedPaymentOptionCode);


        bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
        bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
        bundleToView.putString("startTime", startTime);
        if (chequeEnabled) {
            bundleToView.putString("ChequeNumber", chequeNumber);
            bundleToView.putString("CollectionDate", collectionDate);
            bundleToView.putString("ReleaseDate", releaseDate);

        }
        bundleToView.putString("TotalValue", tViewInvoiceGross.getText().toString());

        startInvoiceGen3.putExtras(bundleToView);
        startActivity(startInvoiceGen3);
        finish();

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        txtCredit.setText(savedInstanceState.getString("Credit"));
    }

    public void chequDilaog() {
        chequeDetailsPopup.setCanceledOnTouchOutside(false);

        final EditText txtChequeAmount = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeAmount);
        final EditText txtChequeNumber = (EditText) chequeDetailsPopup.findViewById(R.id.etChequeNumber);
        final EditText txtRefNo = (EditText) chequeDetailsPopup.findViewById(R.id.edRefNo);
        final ImageButton imgCsmera = (ImageButton) chequeDetailsPopup.findViewById(R.id.btnCamera);
        imgCsmera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        tViewCollectedDate = (TextView) chequeDetailsPopup.findViewById(R.id.tvCollectedDate);
        tViewRealizedDate = (TextView) chequeDetailsPopup.findViewById(R.id.tvRealizeDate);
        spBank = (Spinner) chequeDetailsPopup.findViewById(R.id.spBank);
        spBranch = (Spinner) chequeDetailsPopup.findViewById(R.id.spBranch);
        bankList = bankController.getBankList();
        branchList = branchController.getBranchList();

        ArrayAdapter<String> bankListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, bankList);
        ArrayAdapter<String> branchListAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.single_list_item, branchList);
        // bankListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spBank.setAdapter(bankListAdapter);
        spBank.setSelection(selectedBankIndex);
        spBank.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBankIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spBranch.setAdapter(branchListAdapter);
        spBranch.setSelection(selectedBranchIndex);
        spBranch.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBranchIndex = position;
                selectedBranch = spBranch.getSelectedItem().toString();
                branchCode = branchController.GetBranchCode(selectedBranch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton iBtnCollectedDate = (ImageButton) chequeDetailsPopup.findViewById(R.id.bCalendarCollectedDate);
        ImageButton iBtnRealizeDate = (ImageButton) chequeDetailsPopup.findViewById(R.id.bCalendarRealizeDate);
        Button btnDoneChequePopUp = (Button) chequeDetailsPopup.findViewById(R.id.bDone);
        Button btnCancelChequePopup = (Button) chequeDetailsPopup.findViewById(R.id.bCancel);

        final Calendar c = Calendar.getInstance();
        collectedYear = c.get(Calendar.YEAR);
        collectedMonth = c.get(Calendar.MONTH);
        collectedDay = c.get(Calendar.DAY_OF_MONTH);
        realizeYear = c.get(Calendar.YEAR);
        realizeMonth = c.get(Calendar.MONTH);
        realizeDay = c.get(Calendar.DAY_OF_MONTH);


        // display the current date
        updateCollectedDateDisplay();
        updateRealizeDateDisplay();

        try {
            if ((!cheque.isEmpty()) && (cheque != "")) {
                txtChequeAmount.setText(cheque);
            }

            if ((!chequeNumber.isEmpty()) && (chequeNumber != "")) {
                txtChequeNumber.setText(chequeNumber);
            }

            if ((!collectionDate.isEmpty()) && (collectionDate != "")) {
                tViewCollectedDate.setText(collectionDate);

                collectedDay = Integer.parseInt(collectionDate.substring(0, 2));
                collectedMonth = Integer.parseInt(collectionDate.substring(3, 5));
                collectedYear = Integer.parseInt(collectionDate.substring(6, collectionDate.length()));
            }

            if ((!releaseDate.isEmpty()) && (releaseDate != "")) {
                tViewRealizedDate.setText(releaseDate);

                realizeDay = Integer.parseInt(collectionDate.substring(0, 2));
                realizeMonth = Integer.parseInt(collectionDate.substring(3, 5));
                realizeYear = Integer.parseInt(collectionDate.substring(6, collectionDate.length()));
            }
        } catch (Exception e) {

        }


        iBtnCollectedDate.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(COLLECTED_DATE);
            }
        });


        iBtnRealizeDate.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(REALIZE_DATE);
            }
        });


        btnDoneChequePopUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (txtChequeAmount.getText().toString().isEmpty()) {
                    Toast chequeAmountEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the cheque amount!", Toast.LENGTH_SHORT);
                    chequeAmountEmpty.setGravity(Gravity.TOP, 100, 100);
                    chequeAmountEmpty.show();
                } else if (txtChequeNumber.getText().toString().isEmpty()) {
                    Toast chequeNumberEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the cheque number!", Toast.LENGTH_SHORT);
                    chequeNumberEmpty.setGravity(Gravity.TOP, 100, 100);
                    chequeNumberEmpty.show();
                } else if (txtRefNo.getText().toString().isEmpty()) {
                    Toast chequeNumberEmpty = Toast.makeText(InvoiceGen2Activity.this, "You have to enter the reference number!", Toast.LENGTH_SHORT);
                    chequeNumberEmpty.setGravity(Gravity.TOP, 100, 100);
                    chequeNumberEmpty.show();

                } else {

                    cheque = txtChequeAmount.getText().toString();
                    if (cheque.isEmpty()) {
                        cheque = "0";
                    }


                    chequeNumber = txtChequeNumber.getText().toString();
                    collectionDate = tViewCollectedDate.getText().toString();
                    releaseDate = tViewRealizedDate.getText().toString();
                    referenceNumber = txtRefNo.getText().toString();
                    txtCheque.setText(cheque);

                    double totel = 0;
                    if (customertype == 0) {
                        if (txtCash.getText().toString().equals("0")) {
                            totel = Double.parseDouble(tViewNetPayable.getText().toString()) - Double.parseDouble(cheque);
                        } else {
                            totel = Double.parseDouble(tViewNetPayable.getText().toString()) - (Double.parseDouble(cheque) + Double.parseDouble(txtCredit.getText().toString()));

                        }
                        txtCash.setText(String.valueOf(totel));


                    } else {
                        if (txtCash.getText().toString().equals("0")) {
                            totel = Double.parseDouble(txtCredit.getText().toString()) - Double.parseDouble(cheque);
                        } else {
                            totel = Double.parseDouble(txtCredit.getText().toString()) - (Double.parseDouble(cheque) + Double.parseDouble(txtCredit.getText().toString()));

                        }

                        if(totel>0){
                            txtCredit.setText(String.format("%.2f", totel));
                        }else {
                            txtCredit.setText("0");
                        }


                    }


                    selectedBank = spBank.getSelectedItem().toString();
                    selectedBranch = spBranch.getSelectedItem().toString();
                    Log.w("c day", collectionDate.substring(0, 2));
                    Log.w("c month", collectionDate.substring(3, 5));
                    Log.w("c year", collectionDate.substring(6, collectionDate.length()));

                    Log.w("r day", releaseDate.substring(0, 2));
                    Log.w("r month", releaseDate.substring(3, 5));
                    Log.w("r year", releaseDate.substring(6, releaseDate.length()));

                    chequeDetailsPopup.dismiss();
                }

            }

        });

        btnCancelChequePopup.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                chequeDetailsPopup.dismiss();
            }
        });


        chequeDetailsPopup.show();
    }

    //Himanshu
    public void showDialogSendMessage(Context context, final int status) {

        final Dialog dialogBox = new Dialog(context);
        dialogBox.setTitle("Invoice Approval");
        dialogBox.setContentView(R.layout.dialog_send_approval);
        dialogBox.setCancelable(true);

        String reason = null;
        cusName = tViewCustomerName.getText().toString();


        final TextView txtMessage = (TextView) dialogBox.findViewById(R.id.textView_dialogsendapproval_customername);

        final RelativeLayout layoutResend = (RelativeLayout) dialogBox.findViewById(R.id.layout_dialogsendapproval_resend);
        final TextView txtPhoneNumber = (TextView) dialogBox.findViewById(R.id.textView_dialogsendapproval_phoneNumber);
        final Spinner spinPerson = (Spinner) dialogBox.findViewById(R.id.spinner_approve_person);
        final EditText edtComment = (EditText) dialogBox.findViewById(R.id.editText_dialogsendapproval_comment);
        final EditText edtCode = (EditText) dialogBox.findViewById(R.id.editTextdialogsendapproval_code);
        Button btnContinue = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_continue);
        final Button btnSende = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_send);
        Button btnCancel = (Button) dialogBox.findViewById(R.id.buttondialogsendapproval_cancel);


        final Approval_Persons ap = new Approval_Persons(this);
        ap.openReadableDatabase();

        final Approval_Details aPProDetails = new Approval_Details(this);
        aPProDetails.openWritableDatabase();

        final Reps rep = new Reps(this);
        rep.openReadableDatabase();

        final Customers data = new Customers(this);
        data.openReadableDatabase();

        final ArrayList<String> approvalPersonsList = ap.getAllPerson();
        ArrayAdapter<String> pesronNameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, approvalPersonsList);

        pesronNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPerson.setAdapter(pesronNameAdapter);


        if (aPProDetails.checkAccessibility(pharmacyId) == 0) {
            // edtCode.setEnabled(false);
            layoutResend.setEnabled(false);
        } else {
            btnSende.setEnabled(false);
            //  edtCode.setEnabled(true);
        }

        if (status == 1) {
            reason = "Cheque allowed";
            txtMessage.setText(cusName + ",Cheques are not allowed .Do you want to send for approval and proceed ?");

        } else if (status == 2) {
            reason = "Credit allowed";
            txtMessage.setText(cusName + ",Credits are not allowed .Do you want to send for approval and proceed ?");

        } else if (status == 3) {
            reason = "block customer";
            txtMessage.setText(cusName + ",is Block .Do you want to send for approval and proceed ?");

        }else if (status == 4) {
            reason = "Credit allowed";
            txtMessage.setText(cusName + ",CMMMMM Credits are not allowed .Do you want to send for approval and proceed ?");

        }


        final String pesronName = approvalPersonsList.get(0);
        spinPerson.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinPerson.getSelectedItemPosition();
                        String pesronName = approvalPersonsList.get(position);
                        txtPhoneNumber.setText("Phone Number : " + String.valueOf(ap.getPhoneNumberByPersonName(pesronName)));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                }
        );
        txtPhoneNumber.setText("Phone Number : " + String.valueOf(ap.getPhoneNumberByPersonName(pesronName)));


        //button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ap.closeDatabase();
                aPProDetails.closeDatabase();
                rep.closeDatabase();
                data.closeDatabase();
                dialogBox.dismiss();


            }
        });


        final String finalReason = reason;
        btnSende.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = nextSessionId();
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InvoiceGen2Activity.this);
                final String repId = sharedPreferences.getString("RepId", "-1");
                String msg = "Rep: " + rep.getRepNameByRepId(repId) + " Customer: " + cusName + " Comment: " + edtComment.getText().toString() + " Date: " + dateFormat.format(date) + " Code: " + code;


                boolean resultSend = sendSMS(String.valueOf(ap.getPhoneNumberByPersonName(pesronName)), msg);

                if (resultSend == true) {
                    aPProDetails.insertDetails(pharmacyId, code, finalReason, edtComment.getText().toString(), pesronName);
                    edtComment.setText("");
                    showErrorMessage("Send Approval Successfully");
                    btnSende.setEnabled(false);
                    edtCode.setEnabled(true);
                    layoutResend.setEnabled(true);

                } else {
                    showErrorMessage("Send Approval Fail,please try again");
                }

            }
        });


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtCode.getText().toString().isEmpty()) {
                    showErrorMessage("Code is empty,please try again");

                } else if (edtCode.getText().toString().equals("0000")) {
                    aPProDetails.setAccess(pharmacyId, edtCode.getText().toString());

                    data.setInvoiceAlloweStstus(pharmacyId, 1);
                    ap.closeDatabase();
                    aPProDetails.closeDatabase();
                    rep.closeDatabase();
                    data.closeDatabase();
                    dialogBox.dismiss();

                    if (isOnline()) {
                        new uploadApproveDetails().execute();
                    } else {

                    }

                    if (status == 1) {
                        //chequDilaog(); // Chamal
                        chequeAllowed = true;
                        iBtnChequeDetails.setEnabled(false);// Chamal
                    } else if (status == 2 || status == 3) {
                        creditAllowd = true;
                        txtCredit.setEnabled(true);
                        iBtnChequeDetails.setEnabled(false);// Chamal
                    }else if(status == 4){
                        txtCredit.setEnabled(false);
                    }


                } else if (edtCode.getText().toString().length() == 4) {
                    boolean resultContinue = aPProDetails.checkCode(pharmacyId, edtCode.getText().toString());
                    if (resultContinue == true) {

                        aPProDetails.setAccess(pharmacyId, edtCode.getText().toString());

                        data.setInvoiceAlloweStstus(pharmacyId, 1);
                        ap.closeDatabase();
                        aPProDetails.closeDatabase();
                        rep.closeDatabase();
                        data.closeDatabase();
                        dialogBox.dismiss();

                        if (isOnline()) {
                            new uploadApproveDetails().execute();
                        } else {
                            creditAllowd = true;
                        }

                        if (status == 1) {
                            chequDilaog();
                            chequeAllowed = true;
                        } else if (status == 2 || status == 3) {
                            creditAllowd = true;
                            txtCredit.setEnabled(true);
                        }
                    } else {
                        showErrorMessage("Invalid code,please try again");

                    }

                } else {
                    showErrorMessage("Invalid code,please try again");

                }

            }
        });


        layoutResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = nextSessionId();
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                int rowid = aPProDetails.checkAccessibility(pharmacyId);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InvoiceGen2Activity.this);
                final String repId = sharedPreferences.getString("RepId", "-1");
                String msg = "Rep: " + rep.getRepNameByRepId(repId) + " Customer: " + cusName + " Comment: " + edtComment.getText().toString() + " Date: " + dateFormat.format(date) + " Code: " + code;

                if (sendSMS(String.valueOf(ap.getPhoneNumberByPersonName(pesronName)), msg)) {
                    boolean resultResend = aPProDetails.updateCode(rowid, code);
                    if (resultResend == true) {
                        showErrorMessage("Resend Approval Successfully");
                    } else {
                        showErrorMessage("Resend Approval Fail,please try again");
                    }
                } else {
                    showErrorMessage("Resend Approval Fail,please try again");
                }


            }
        });
        InputMethodManager imm = (InputMethodManager) InvoiceGen2Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
        // ap.closeDatabase();
        dialogBox.show();
    }

    public void showErrorMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(InvoiceGen2Activity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public String nextSessionId() {
        String genCode = null;
        SecureRandom random = new SecureRandom();
        genCode = new BigInteger(20, random).toString(32);
        if (genCode.length() != 4) {
            genCode = genCode + 0;
        } else {

        }
        return genCode;
    }

    public boolean sendSMS(String number, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }

    private class uploadApproveDetails extends AsyncTask<Void, Void, Void> {

        Approval_Details approvdetails;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            approvdetails = new Approval_Details(InvoiceGen2Activity.this);
            approvdetails.openReadableDatabase();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InvoiceGen2Activity.this);
            final String repId = sharedPreferences.getString("RepId", "-1");

            List<String[]> rtnRemarks = approvdetails.getApprovaDetails();

            System.out.println("accesss cout " + rtnRemarks.size());


            for (java.lang.String[] rtnData : rtnRemarks) {
                java.lang.String[] remarksDetails = new String[10];
                remarksDetails[0] = rtnData[0];
                remarksDetails[1] = rtnData[1];
                remarksDetails[2] = rtnData[2];
                remarksDetails[3] = rtnData[3];
                remarksDetails[4] = rtnData[4];
                remarksDetails[5] = rtnData[5];
                remarksDetails[6] = rtnData[6];
                remarksDetails[7] = rtnData[7];
                remarksDetails[8] = rtnData[8];
                remarksDetails[9] = rtnData[9];

                String responseArr = null;

                while (responseArr == null) {
                    try {

                        WebService webService = new WebService();
                        responseArr = webService.uploadApprovalDetails(remarksDetails, repId);

                        if (responseArr.equals("OK")) {
                            approvdetails.updateUploadStstus(rtnData[0]);
                        }
                        Thread.sleep(100);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }

                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }

}
