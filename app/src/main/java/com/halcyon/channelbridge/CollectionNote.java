package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.halcyon.channelbridgeaddapters.CollectionNoteAdapter;
import com.halcyon.channelbridgeaddapters.CollectionNoteList;
import com.halcyon.channelbridgedb.Branch;
import com.halcyon.channelbridgedb.CollectionNoteSendToApprovel;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.InvoicePaymentType;
import com.halcyon.channelbridgedb.Master_Banks;
import com.halcyon.channelbridgebs.UploadCollectionNoteTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class CollectionNote extends Activity implements com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener {

    private static final int CAMERA_REQUEST = 1888;

    TextView textViewRealizedate, textCheqe, textViewCustmomerNumber, textViewInvoiceCradite;
    RelativeLayout relativeLayoutCheque, calenderView, layoutChequeImage, btnSubmit;
    ImageView chequeimage;
    AutoCompleteTextView bankTextView, customerName;
    MaterialSpinner invoiceNumbersSp;
    Button btnAdd;
    EditText editCash;
    ListView addedInvoiceList;

    List<String> bankList, CustomerNameList, InvoiceNumberList, outsatandingAmmountList, branchList;
    ArrayList<String[]> returnProducts;
    ArrayList<CollectionNoteList> listCollectionNoteItem = new ArrayList<CollectionNoteList>();
    ArrayList<String[]> tempreturnProducts;
    List<String[]> cheqeDetails ;

    String[] returnDetails;
    String selectedInvoiceNum, cheqAmmount = "0", cheqNumber = "0", cheqBank = "0", cheqBranch = "0", cheqRealizeDate, cusName;
    double balance = 0.0, cashBal = 0, cheqBal = 0, cashbalance = 0.0, cheqbalance = 0.0;
    boolean stsuts = false;


    Bitmap photo;
    byte[] chequeimageByte;

    CollectionNoteAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_note);


        //layout initialization
        relativeLayoutCheque = (RelativeLayout) findViewById(R.id.relativeLayoutCheque);
        textCheqe = (TextView) findViewById(R.id.editText_cheqe);
        customerName = (AutoCompleteTextView) findViewById(R.id.AutoComplete_CustomerName);
        textViewCustmomerNumber = (TextView) findViewById(R.id.textViewcustmomerNumber);
        textViewInvoiceCradite = (TextView) findViewById(R.id.textView_invoiceCradite);
        editCash = (EditText) findViewById(R.id.editTextCash);
        btnAdd = (Button) findViewById(R.id.button_add);
        addedInvoiceList = (ListView) findViewById(R.id.listViewCollecton);
        invoiceNumbersSp = (MaterialSpinner) findViewById(R.id.spinnerInvoiceNum);
        btnSubmit = (RelativeLayout) findViewById(R.id.relativeLayoutsubmit);

        //variable initialization
        bankList = new ArrayList<String>();
        CustomerNameList = new ArrayList();
        InvoiceNumberList = new ArrayList();
        outsatandingAmmountList = new ArrayList();
        tempreturnProducts = new ArrayList<String[]>();
        cheqeDetails = new ArrayList<String[]>();


        returnProducts = new ArrayList<String[]>();


        //adapter
        listAdapter = new CollectionNoteAdapter(this, listCollectionNoteItem);


        final Customers customer = new Customers(CollectionNote.this);
        customer.openReadableDatabase();

        if (customer.get_rowcount() > 0) {
            try {
                CustomerNameList = customer.getAllCustomerDetails();
            } catch (Exception e) {

            }
        }
        customer.closeDatabase();


        ArrayAdapter<String> customerAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, CustomerNameList);
        customerName.setAdapter(customerAdapterList);

        // final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, InvoiceNumberList);


        //click event

        relativeLayoutCheque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tempreturnProducts.isEmpty()){
                    showChequeDialog();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionNote.this);
                    alertDialogBuilder.setTitle("Warring");
                    alertDialogBuilder
                            .setMessage("Do you want to change Cheque Details ,if yes you will lost all date which you add to list ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    tempreturnProducts.clear();
                                    textViewInvoiceCradite.setText("");
                                    cheqeDetails.clear();


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    customerName.setText(cusName);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }


            }
        });

        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                final DEL_Outstandiing oustanding = new DEL_Outstandiing(CollectionNote.this);
                oustanding.openReadableDatabase();

                String selectCusName[] = arg0.getItemAtPosition(arg2).toString().split("-");
                customerName.setText(selectCusName[0]);
                textViewCustmomerNumber.setText(selectCusName[1]);

                if (customerName.getText().toString().equals("") || tempreturnProducts.size() == 0) {
                    cusName = selectCusName[0];
                    if (oustanding.get_rowcount() > 0) {
                        InvoiceNumberList.clear();
                        try {

                            Cursor invoiesdetails = oustanding.loadInvoiceNumberFromCusID(selectCusName[1]);
                            invoiesdetails.moveToFirst();
                            for (invoiesdetails.moveToFirst(); !invoiesdetails.isAfterLast(); invoiesdetails.moveToNext()) {
                                InvoiceNumberList.add(invoiesdetails.getString(0));
                                outsatandingAmmountList.add(invoiesdetails.getString(1));

                            }

                            invoiesdetails.close();
                            if (InvoiceNumberList.size() == 0) {
                                Toast.makeText(CollectionNote.this, "No any outstanding for this Customer", Toast.LENGTH_LONG).show();
                                InvoiceNumberList.add("No Invoice Numbers");
                                invoiceNumbersSp.setItems(InvoiceNumberList);

                            } else {
                                selectedInvoiceNum = InvoiceNumberList.get(0);
                                invoiceNumbersSp.setItems(InvoiceNumberList);
                                textViewInvoiceCradite.setText(outsatandingAmmountList.get(0));

                            }

                        } catch (Exception e) {
                            Toast.makeText(CollectionNote.this, "No any outstanding for this Customer", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(CollectionNote.this, "No any outstanding", Toast.LENGTH_LONG).show();
                    }

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionNote.this);
                    alertDialogBuilder.setTitle("Warring");
                    alertDialogBuilder
                            .setMessage("Do you want to change cash ,if yes you will lost all date which you add to list ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    customerName.setText("");
                                    tempreturnProducts.clear();
                                    listAdapter.notifyDataSetChanged();
                                    listCollectionNoteItem.clear();
                                    InvoiceNumberList.clear();
                                    outsatandingAmmountList.clear();
                                    textViewInvoiceCradite.setText("");
                                    InvoiceNumberList.add("Invoice Number");
                                    invoiceNumbersSp.setItems(InvoiceNumberList);
                                    addedInvoiceList.setAdapter(listAdapter);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    customerName.setText(cusName);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

                oustanding.closeDatabase();
            }

        });


        customerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerName.setTextColor(getResources().getColor(R.color.black));
            }
        });


        editCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCash.getText().toString().equals("") || tempreturnProducts.size() == 0) {

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionNote.this);
                    alertDialogBuilder.setTitle("Warring");
                    alertDialogBuilder
                            .setMessage("Do you want to change cash ,if yes you will lost all date which you add to list ")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    customerName.setText("");
                                    tempreturnProducts.clear();
                                    listAdapter.notifyDataSetChanged();
                                    listCollectionNoteItem.clear();
                                    InvoiceNumberList.clear();
                                    outsatandingAmmountList.clear();
                                    textViewInvoiceCradite.setText("");
                                    InvoiceNumberList.add("Invoice Number");
                                    invoiceNumbersSp.setItems(InvoiceNumberList);
                                    addedInvoiceList.setAdapter(listAdapter);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    editCash.setFocusable(false);
                                    editCash.setClickable(true);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        invoiceNumbersSp.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                textViewInvoiceCradite.setText(outsatandingAmmountList.get(position));
                selectedInvoiceNum = item;
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnDetails = new String[7];
                boolean zrooChek = false;
                try {
                    int convertAmmount = 1 / Integer.parseInt(editCash.getText().toString().trim());
                } catch (ArithmeticException a) {
                    zrooChek = true;
                } catch (NumberFormatException n) {

                }

                String chequeAmmount[] = textCheqe.getText().toString().split(":");
                if (customerName.getText().toString().equals("")) {
                    Toast.makeText(CollectionNote.this, "Customer name is empty", Toast.LENGTH_LONG).show();
                    customerName.setTextColor(getResources().getColor(R.color.myRed));
                    customerName.setText("Customer name is empty");
                } else if ((chequeAmmount[1].toString().trim().equals("0.0")) && (editCash.getText().toString().equals(""))) {
                    Toast.makeText(CollectionNote.this, "Need cash amount or cheque amount.", Toast.LENGTH_LONG).show();
                } else if (zrooChek == true) {
                    Toast.makeText(CollectionNote.this, "Please enter valid number to cash", Toast.LENGTH_LONG).show();
                } else if (selectedInvoiceNum == null) {
                    Toast.makeText(CollectionNote.this, "No Invoice Numbers ", Toast.LENGTH_LONG).show();
                } else if (!tempreturnProducts.isEmpty() && balance == 0) {
                    Toast.makeText(CollectionNote.this, "Your balance is 0 ", Toast.LENGTH_LONG).show();
                } else {
                    //cheque & cash
                    if ((!chequeAmmount[1].toString().trim().equals("0.0")) && (!editCash.getText().toString().equals(""))) {
                        if (tempreturnProducts.isEmpty()) {
                            balance = (Double.parseDouble(chequeAmmount[1].toString().trim()) + Double.parseDouble(editCash.getText().toString()) - Double.parseDouble(textViewInvoiceCradite.getText().toString()));
                            if (Double.parseDouble(textViewInvoiceCradite.getText().toString()) > Double.parseDouble(editCash.getText().toString())) {
                                stsuts = true;
                                cashbalance = Double.parseDouble(editCash.getText().toString());
                                cheqbalance = Double.parseDouble(textViewInvoiceCradite.getText().toString()) - Double.parseDouble(editCash.getText().toString());

                                if (balance < 0) {
                                    cheqbalance = Double.parseDouble(chequeAmmount[1].toString().trim());
                                } else {
                                }

                            } else {
                                cashbalance = Double.parseDouble(editCash.getText().toString()) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                            }

                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                            if (stsuts == true) {
                                cashbalance = 0.0;
                                cheqbalance = balance;

                            } else {
                                cashbalance = Double.parseDouble(rBal[1]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                cheqbalance = (Double.parseDouble(chequeAmmount[1].toString().trim()));
                            }
                            if (balance < 0) {
                                if (stsuts == true) {
                                    cashbalance = 0.0;
                                    cheqbalance = Double.parseDouble(rBal[3]);
                                } else {
                                    cashbalance = Double.parseDouble(rBal[3]);
                                    cheqbalance = 0.0;
                                }
                            } else {

                            }
                            // cheqbalance =Double.parseDouble(rBal[2]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());


                        }


                        if (balance < 0) {
                            balance = 0;
                        } else {

                        }


                        if (tempreturnProducts.size() == 0) {

                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = String.valueOf(cashbalance);
                            returnDetails[2] = String.valueOf(cheqbalance);
                            returnDetails[3] = String.valueOf(balance);

                            //cashbalance = balance;
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, String.valueOf(cashbalance), String.valueOf(cheqbalance), String.valueOf(balance)));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);

                            int listaddedStutes = 0;
                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;

                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashbalance);
                                returnDetails[2] = String.valueOf(cheqbalance);
                                returnDetails[3] = String.valueOf(balance);


                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }
//cheque
                    } else if (editCash.getText().toString().equals("")) {

                        String cheqAmmount = chequeAmmount[1].toString().trim();

                        if (tempreturnProducts.isEmpty()) {
                            balance = Double.parseDouble(chequeAmmount[1].toString().trim()) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                        }

                        if (balance < 0) {
                            balance = 0;
                        } else {

                        }


                        if (tempreturnProducts.size() == 0) {
                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = editCash.getText().toString();
                            returnDetails[2] = chequeAmmount[1].toString().trim();
                            returnDetails[3] = String.valueOf(balance);
                            cheqBal = balance;
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, editCash.getText().toString(), cheqAmmount, String.valueOf(balance)));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);

                            int listaddedStutes = 0;
                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;
                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashBal);
                                returnDetails[2] = String.valueOf(cheqBal);
                                returnDetails[3] = String.valueOf(balance);
                                cheqBal = balance;

                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }

                    }


                    //cash
                    else if (chequeAmmount[1].toString().trim().equals("0.0")) {
                        String cashAmmount;
                        cashAmmount = editCash.getText().toString();

                        if (tempreturnProducts.isEmpty()) {

                            balance = Double.parseDouble(cashAmmount) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                        } else {
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);
                            balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());

                        }

                        if (balance < 0) {
                            balance = 0;
                        } else {

                        }
                        if (tempreturnProducts.size() == 0) {

                            returnDetails[0] = selectedInvoiceNum;
                            returnDetails[1] = editCash.getText().toString();
                            returnDetails[2] = chequeAmmount[1].toString();
                            returnDetails[3] = String.valueOf(balance);
                            cashBal = balance;
                            tempreturnProducts.add(returnDetails);
                            listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                            addedInvoiceList.setAdapter(listAdapter);
                        } else {
                            int listaddedStutes = 0;
                            String[] rBal = tempreturnProducts.get(tempreturnProducts.size() - 1);

                            for (int i = 0; i < tempreturnProducts.size(); i++) {
                                String[] r = tempreturnProducts.get(i);
                                if ((r[0].contentEquals(selectedInvoiceNum))) {
                                    listaddedStutes = 1;
                                    balance = Double.parseDouble(rBal[3]) - Double.parseDouble(textViewInvoiceCradite.getText().toString());
                                    Toast.makeText(CollectionNote.this, "This Invoice number has already been added!", Toast.LENGTH_LONG).show();
                                    break;
                                } else {
                                    listaddedStutes = 0;
                                }

                            }
                            if (listaddedStutes == 0) {
                                String[] r = tempreturnProducts.get(tempreturnProducts.size() - 1);
                                returnDetails[0] = selectedInvoiceNum;
                                returnDetails[1] = String.valueOf(cashBal);
                                returnDetails[2] = String.valueOf(cheqBal);
                                returnDetails[3] = String.valueOf(balance);
                                cashBal = balance;

                                tempreturnProducts.add(returnDetails);
                                listCollectionNoteItem.add(new CollectionNoteList(selectedInvoiceNum, returnDetails[1], returnDetails[2], returnDetails[3]));
                                addedInvoiceList.setAdapter(listAdapter);
                            } else {

                            }
                        }

                    }
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CollectionNoteSendToApprovel cns = new CollectionNoteSendToApprovel(CollectionNote.this);
                cns.openWritableDatabase();
                String paymentType = null;

                String chequeAmmount[] = textCheqe.getText().toString().split(":");

                if (tempreturnProducts.isEmpty()) {
                    Toast.makeText(CollectionNote.this, "Empty Collection!", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < tempreturnProducts.size(); i++) {
                        String cNoteDetail[] = tempreturnProducts.get(i);

                        if (!cNoteDetail[1].trim().equals("0.0") && !cNoteDetail[2].trim().equals("0.0")) {
                            paymentType = "Cash+Cheque";
                        } else if (!cNoteDetail[1].equals("0.0")) {
                            paymentType = "Cash";
                        } else if (!cNoteDetail[2].equals("0.0")) {
                            paymentType = "Cheque";
                        }
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CollectionNote.this);
                        String repId = sharedPreferences.getString("RepId", "-1");

                        double OutStand_value = 0;
                        try {
                            OutStand_value = customer.GetOustand_value(textViewCustmomerNumber.getText().toString());
                        } catch (Exception e) {

                        }

                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time => " + c.getTime());

                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        String collectdate = df.format(c.getTime());

                        if ((paymentType.equals("Cash+Cheque")) || (paymentType.equals("Cheque"))) {
                            cns.insertCollectionNoteSendToApprovel(
                                    GenaterCollectionNoteNumber(),
                                    repId,
                                    customerName.getText().toString(),
                                    String.valueOf(OutStand_value),
                                    cNoteDetail[0].toString(),
                                    textViewInvoiceCradite.getText().toString(),
                                    paymentType,
                                    editCash.getText().toString(),
                                    cheqAmmount,
                                    cheqNumber,
                                    cheqBank,
                                    cheqBranch,
                                    collectdate,
                                    cheqRealizeDate,
                                    chequeimageByte,
                                    "",
                                    "",
                                    textViewCustmomerNumber.getText().toString(),
                                    "",
                                    cNoteDetail[3].toString());

                        } else {
                            cns.insertCollectionNoteSendToApprovel(
                                    GenaterCollectionNoteNumber(),
                                    repId,
                                    customerName.getText().toString(),
                                    String.valueOf(OutStand_value),
                                    cNoteDetail[0].toString(),
                                    textViewInvoiceCradite.getText().toString(),
                                    paymentType,
                                    editCash.getText().toString(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    chequeimageByte,
                                    "",
                                    "",
                                    textViewCustmomerNumber.getText().toString(),
                                    "",
                                    cNoteDetail[3].toString());
                        }

                        Toast.makeText(CollectionNote.this, "Collection note save successfully", Toast.LENGTH_SHORT).show();
                        cns.closeDatabase();
                        valueclear();


                        if (isNetworkAvailable() == true) {
                            upload();

                        }


                    }

                }


            }
        });
        // GenaterCollectionNoteNumber();

    }

    public void valueclear() {
        cheqAmmount = "";
        cheqNumber = "";
        cheqBank = "";
        cheqBranch = "";
        cheqRealizeDate = "";

        editCash.setText("");
        textViewInvoiceCradite.setText("0");
        textCheqe.setText("Cheque Ammount : 0.0");

        listCollectionNoteItem.clear();
        tempreturnProducts.clear();
        addedInvoiceList.setAdapter(listAdapter);


    }

    private String GenaterCollectionNoteNumber() {
        String Cnumber = null;
        try {
            CollectionNoteSendToApprovel aprove = new CollectionNoteSendToApprovel(CollectionNote.this);
            Cnumber = aprove.GenareCollectionNoteNumber();
            aprove.closeDatabase();
        } catch (Exception e) {
        }
        return Cnumber;
    }

    private void showChequeDialog() {

        final Dialog dialogBox = new Dialog(CollectionNote.this);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBox.setContentView(R.layout.dialog_colection_note_cheque);
        dialogBox.setCancelable(false);

        //dialog layout
        calenderView = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayout_Dialog_calender);
        textViewRealizedate = (TextView) dialogBox.findViewById(R.id.textViewRealizedate);
        bankTextView = (AutoCompleteTextView) dialogBox.findViewById(R.id.editTextdilaog_bank);
        layoutChequeImage = (RelativeLayout) dialogBox.findViewById(R.id.relativeLayoutCheqeImage);
        chequeimage = (ImageView) dialogBox.findViewById(R.id.imageViewCheque);
        ImageView btnClose = (ImageView) dialogBox.findViewById(R.id.imageViewClose);

        final EditText editAmmount = (EditText) dialogBox.findViewById(R.id.editTextdilaog_ammount);
        final EditText edtNumber = (EditText) dialogBox.findViewById(R.id.editTextdilaog_number);
        final AutoCompleteTextView edtBranch = (AutoCompleteTextView) dialogBox.findViewById(R.id.editTextdilaog_branch);
        Button btnDone = (Button) dialogBox.findViewById(R.id.button_dialog_done);
        final Button btnChange = (Button) dialogBox.findViewById(R.id.buttonChange);





        if (!cheqAmmount.equals("0")) {
            editAmmount.setText(cheqAmmount);
            edtNumber.setText(cheqNumber);
            edtBranch.setText(cheqBranch);
            bankTextView.setText(cheqBank);
            textViewRealizedate.setText(cheqRealizeDate);

            try {
                Bitmap bmp = BitmapFactory.decodeByteArray(chequeimageByte, 0, chequeimageByte.length);
                chequeimage.setImageBitmap(bmp);
            } catch (NullPointerException n) {

            }


            editAmmount.setEnabled(false);
            edtNumber.setEnabled(false);
            edtBranch.setEnabled(false);
            bankTextView.setEnabled(false);
            calenderView.setEnabled(false);
            layoutChequeImage.setEnabled(false);

            btnChange.setEnabled(true);

        } else {
            btnChange.setEnabled(false);
        }




        //
        Master_Banks banks = new Master_Banks(this);
        banks.openReadableDatabase();
        bankList = banks.GetBank();
        banks.closeDatabase();

        Branch branch = new Branch(this);
        branch.openReadableDatabase();
        branchList = branch.GetBranchName();
        branch.closeDatabase();


        ArrayAdapter<String> bankAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, bankList);
        bankTextView.setAdapter(bankAdapterList);

        ArrayAdapter<String> barnchAdapterList = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, branchList);
        edtBranch.setAdapter(barnchAdapterList);


        //click event
/*

        bankTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (bankTextView.getText().toString().equals("")) {

                } else {
                    bankCode.setText("Bank Code : " + "BCEYLKLX131");
                }

            }
        });

        edtBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                if (barnchCode.getText().toString().equals("")) {
                } else {
                    barnchCode.setText("Branch Code : " + "131");
                }

            }
        });
*/


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBox.dismiss();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CollectionNote.this);
                alertDialogBuilder.setTitle("Warring");
                alertDialogBuilder
                        .setMessage("Do you want to clear cheque data,if yes you will lost all date which you add to list ")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                editAmmount.setEnabled(true);
                                edtNumber.setEnabled(true);
                                edtBranch.setEnabled(true);
                                bankTextView.setEnabled(true);
                                calenderView.setEnabled(true);
                                layoutChequeImage.setEnabled(true);
                                tempreturnProducts.clear();


                                listAdapter.notifyDataSetChanged();
                                listCollectionNoteItem.clear();

                                addedInvoiceList.setAdapter(listAdapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        calenderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                com.borax12.materialdaterangepicker.date.DatePickerDialog dpd = new com.borax12.materialdaterangepicker.date.DatePickerDialog().newInstance(CollectionNote.this, now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });


        layoutChequeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });


        edtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 6) {
                    Toast.makeText(CollectionNote.this, "You exceed maximum characters", Toast.LENGTH_LONG).show();
                    edtNumber.setText(charSequence.toString().substring(0, 6));
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean zrooChek = false;
                try {
                    int convertAmmount = 1 / Integer.parseInt(editAmmount.getText().toString().trim());
                } catch (ArithmeticException a) {
                    zrooChek = true;
                } catch (NumberFormatException nu) {

                }
                if (!editAmmount.getText().toString().trim().equals("")) {
                    if (edtNumber.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the cheque number!", Toast.LENGTH_LONG).show();
                    } else if (zrooChek == true) {
                        Toast.makeText(CollectionNote.this, "Please enter valid number!", Toast.LENGTH_LONG).show();
                    } else if (bankTextView.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Bank name!", Toast.LENGTH_LONG).show();
                    } else if (edtBranch.getText().toString().trim().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Bank Branch name!", Toast.LENGTH_LONG).show();
                    } else if (textViewRealizedate.getText().toString().equals("")) {
                        Toast.makeText(CollectionNote.this, "Please enter the Realize Date!", Toast.LENGTH_LONG).show();
                    } else {


                        textCheqe.setText("Cheque Ammount : " + editAmmount.getText().toString().trim());
                        cheqAmmount = editAmmount.getText().toString().trim();
                        cheqNumber = edtNumber.getText().toString().trim();
                        cheqBank = bankTextView.getText().toString().trim();
                        cheqBranch = edtBranch.getText().toString().trim();
                        cheqRealizeDate = textViewRealizedate.getText().toString();
                        dialogBox.dismiss();
                    }
                } else {
                    dialogBox.dismiss();
                }

            }
        });

        dialogBox.show();

    }

    @Override
    public void onDateSet(com.borax12.materialdaterangepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Date dateSpecified = c.getTime();
        if (dateSpecified.after(today)) {
            String month,day;
            if(String.valueOf(dayOfMonth).length()==1){
                day="0"+String.valueOf(dayOfMonth);
            }else {
                day=String.valueOf(dayOfMonth);
            }

            if(String.valueOf((monthOfYear+1)).length()==1){
                month="0"+String.valueOf((monthOfYear+1));
            }else {
                month=String.valueOf((monthOfYear+1));
            }




            textViewRealizedate.setText(day + "-" + month + "-" + String.valueOf(year));
        } else {

            Toast.makeText(CollectionNote.this, "Please select future date", Toast.LENGTH_LONG).show();
        }

    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            chequeimageByte = getBytes(photo);
            chequeimage.setImageBitmap(photo);
        }

    }

    public void getDeleteIdFromList(String id) {
        listCollectionNoteItem.clear();
        for (int i = 0; i < tempreturnProducts.size(); i++) {
            String[] r = tempreturnProducts.get(i);
            if ((r[0].contentEquals(id))) {
                String chequeAmmount[] = textCheqe.getText().toString().split(":");
                if ((tempreturnProducts.size() - 1) == i) {
                    String[] r2;
                    if(tempreturnProducts.size()==1){
                        r2 = tempreturnProducts.get(0);
                    }else {
                        r2 = tempreturnProducts.get(i-1);
                    }

                    if ((!chequeAmmount[1].toString().trim().equals("0.0")) && (!editCash.getText().toString().equals(""))) {
                        cashbalance = Double.parseDouble(r2[1]);
                        cheqbalance = Double.parseDouble(r2[2]);
                        balance = Double.parseDouble(r2[3]);

                    } else if (editCash.getText().toString().equals("0.0")) {
                        cheqBal = Double.parseDouble(r[2]);
                        balance = Double.parseDouble(r[3]);
                    } else if (chequeAmmount[1].toString().trim().equals("0.0")) {
                        cashBal = Double.parseDouble(r[1]);
                        balance = Double.parseDouble(r[3]);
                    }

                    tempreturnProducts.remove(i);

                } else {
                    Toast.makeText(CollectionNote.this, "You can remove only the last row", Toast.LENGTH_SHORT).show();
                }


            } else {

            }
        }
        for (int i = 0; i < tempreturnProducts.size(); i++) {
            String[] r = tempreturnProducts.get(i);
            listCollectionNoteItem.add(new CollectionNoteList(r[0], r[1], r[2], r[3]));
        }

        addedInvoiceList.setAdapter(listAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent in = new Intent(CollectionNote.this, ItineraryList.class);
        startActivity(in);
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void upload() {
        try {

            UploadCollectionNoteTask up = new UploadCollectionNoteTask(CollectionNote.this);
            up.execute();
        } catch (Exception e) {

        }

    }
}