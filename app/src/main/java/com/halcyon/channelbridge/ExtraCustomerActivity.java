package com.halcyon.channelbridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgebs.UploadRemarksTask;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.Remarks;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgehelp.RemarksType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//
public class ExtraCustomerActivity extends Activity implements LocationListener{

    TableLayout tblLayoutCustomerTable;
    Button btnSaveExtraCustomer, btnCancel;
    Builder alertCancel;
    AlertDialog alertDialog;
    TextView tViewDate;
    AutoCompleteTextView atViewSearchCustomer;
    ImageButton ibtnClearSearchString;
    static public final int REQUEST_LOCATION = 100;
    HashMap<String, String[]> selectedCustomers;
    Intent ItineraryListIntent = new Intent(
            "com.halcyon.channelbridge.ITINERARYLIST");
    int key = 0;
    int j = 0;
    private LocationManager locationManager;
    Location location;
    double lat, lng;
    Dialog reason;
    Button btnSaveReason, btnCancelPopup;
    EditText txtReason;
    TextView tViewTitle;

    Dialog reasonForIndividual;
    TextView tViewTitleForIndividual;
    EditText txtReasonForIndividual;
    Button btnSaveReasonForIndividual, btnCancelPopupForIndividual;
    List<String[]> result;
    DateFormat formatter;
    private Reps repconnector;
    private String repId;

    Spinner spinerReason;
    String selectedReason;
    //  List<String[]> extraCustomers;
    //  ArrayList<String> customerNames=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_customer);
        btnSaveExtraCustomer = (Button) findViewById(R.id.bSaveExtraCustomer);
        btnCancel = (Button) findViewById(R.id.bGoBack);
        tblLayoutCustomerTable = (TableLayout) findViewById(R.id.tlCustomerTable);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        atViewSearchCustomer = (AutoCompleteTextView) findViewById(R.id.etSearchCustomer);
        ibtnClearSearchString = (ImageButton) findViewById(R.id.bClearSearch);
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        reason = new Dialog(this);
        reason.setContentView(R.layout.extra_customer_reason_popup_new);
        reason.setTitle("Alert");
        tViewTitle = (TextView) reason.findViewById(R.id.tvMessage);
     //   txtReason = (EditText) reason.findViewById(R.id.etReason);
        spinerReason = (Spinner) reason.findViewById(R.id.spinner);

        btnSaveReason = (Button) reason.findViewById(R.id.bSave);
        btnCancelPopup = (Button) reason.findViewById(R.id.bCancel);
        tViewTitle.setText("Please enter reason for adding extra customer!");
        selectedReason = spinerReason.getSelectedItem().toString();

        spinerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedReason=   adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        reason.setCanceledOnTouchOutside(false);

        try {
            Itinerary itineraryObject = new Itinerary(getApplication());
            itineraryObject.openReadableDatabase();
            SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
            result = itineraryObject.getAllItinerariesForADay(currentDate);
            itineraryObject.closeDatabase();
            selectedCustomers = new HashMap<String, String[]>();
            setDate();//skk
            Customers customersObject = new Customers(this);
            customersObject.openReadableDatabase();
            List<String[]> extraCustomers = customersObject.getAllCustomers();
            Log.w("EXTRACUSTOMER", "GET ALL CUSTOMERS CALLED");
            ArrayList<String> customerNames = customersObject.getCustomerNames();
            List<String[]> extraCustomers1 = new ArrayList<>();

            if (extraCustomers.size() > 50) {
                extraCustomers1 = extraCustomers.subList(0, 50);
            } else {
                extraCustomers1 = extraCustomers;
            }

            customersObject.closeDatabase();
            repconnector = new Reps(getApplicationContext());
            repconnector.openReadableDatabase();
            List<String[]> repList = repconnector.getAllRepsDetails();
            repconnector.closeDatabase();
            if (!repList.isEmpty()) {
                for (String[] ids : repList) {
                    repId = ids[1];
                }
            }
            for (int i = 0; i < result.size(); i++) {
                for (int j = 0; j < extraCustomers1.size(); j++) {
                    String[] itinerary = result.get(i);
                    String[] customer = extraCustomers1.get(j);
                    if (itinerary[4].equals(customer[2])) {
                        extraCustomers1.remove(j);
                    }
                }
            }

            if (savedInstanceState != null) {
                setBundleData(savedInstanceState);
            }
            if (extraCustomers1 != null) {
                populateExtraCustomerTable(extraCustomers1);
            }

            ArrayAdapter<String> customerSearchAdapter = new ArrayAdapter<String>(
                    ExtraCustomerActivity.this, android.R.layout.simple_dropdown_item_1line,
                    customerNames);
            ((AutoCompleteTextView) atViewSearchCustomer)
                    .setAdapter(customerSearchAdapter);


            btnCancelPopup.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    reason.cancel();
                }
            });

            alertCancel = new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want Cancel?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                    startActivity(ItineraryListIntent);

                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });


            btnCancel.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    alertCancel.show();

                }
            });

            final Toast noMatchesFound = Toast.makeText(getApplication(), "No match found!", Toast.LENGTH_SHORT);

            atViewSearchCustomer.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                    if (!s.toString().isEmpty()) {
                        tblLayoutCustomerTable.removeAllViews();
                        String searchString = s.toString();//sk
                        Customers customersObject = new Customers(ExtraCustomerActivity.this);
                        customersObject.openReadableDatabase();
                        List<String[]> extraCustomers = customersObject.searchCustomers(searchString, "asc");
                        customersObject.closeDatabase();

                        for (int i = 0; i < result.size(); i++) {
                            for (int j = 0; j < extraCustomers.size(); j++) {
                                String[] itinerary = result.get(i);
                                String[] customer = extraCustomers.get(j);
                                if (itinerary[4].equals(customer[2])) {
                                    extraCustomers.remove(j);
                                }
                            }
                        }
                        if (!extraCustomers.isEmpty()) {
                            noMatchesFound.cancel();
                            populateExtraCustomerTable(extraCustomers);
                        } else {
                            noMatchesFound.show();
                        }
                    } else {
                        Customers customersObject = new Customers(ExtraCustomerActivity.this);
                        customersObject.openReadableDatabase();
                        List<String[]> extraCustomers = customersObject.getAllCustomers();
                        Log.w("EXTRACUSTOMER", "GET ALL CUSTOMERS CALLED");
                        customersObject.closeDatabase();

                        for (int i = 0; i < result.size(); i++) {
                            for (int j = 0; j < extraCustomers.size(); j++) {
                                String[] itinerary = result.get(i);
                                String[] customer = extraCustomers.get(j);
                                if (itinerary[4].equals(customer[2])) {
                                    extraCustomers.remove(j);
                                }
                            }
                        }


                        populateExtraCustomerTable(extraCustomers);

                    }


                }

                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                    tblLayoutCustomerTable.removeAllViews();
                }

                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                    Customers customersObject = new Customers(ExtraCustomerActivity.this);
                    customersObject.openReadableDatabase();
                    List<String[]> extraCustomers = customersObject.getAllCustomers();
                    Log.w("EXTRACUSTOMER", "GET ALL CUSTOMERS CALLED");
                    customersObject.closeDatabase();
                    Log.w("NAME", s.toString() + "");


                    for (String[] e : extraCustomers) {
                        if (e[5].contentEquals(s.toString())) {
                            for (String[] r : result) {
                                if (e[1].contentEquals(r[4])) {
                                    noMatchesFound.cancel();
                                    Toast customerAlreadyInItinerary = Toast.makeText(ExtraCustomerActivity.this, "This Customer is allready in your Itinerary!", Toast.LENGTH_SHORT);
                                    customerAlreadyInItinerary.setGravity(Gravity.TOP, 100, 100);
                                    customerAlreadyInItinerary.show();
                                }
                            }

                        }
                    }

                }
            });

            ibtnClearSearchString.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    atViewSearchCustomer.setText(null);

                }
            });


        } catch (Exception e) {
            Log.d("extra customer ", e.toString());
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            alertCancel.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.extra_customer_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_sortAsc:

                selectedCustomers.clear();

                break;

            case R.id.menu_sortDesd:

                selectedCustomers.clear();
                break;

            case R.id.menu_search_customer:

                // slectedCustomers.clear();
                // tblLayoutCustomerTable.removeAllViews();

                alertDialog.setTitle("Alert");
                alertDialog.setMessage("test : ");
                alertDialog.show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void populateExtraCustomerTable(List<String[]> extraCustomers) {
        Log.w("called", "inside populate");

        TableRow tr;
        tr = new TableRow(this);
        tr.setId(0);
        tr.setPadding(0, 3, 0, 3);
        tr.setBackgroundColor(Color.parseColor("#d3d3d3"));
        tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        tblLayoutCustomerTable.setShrinkAllColumns(true);

        TextView labelSelect = new TextView(this);
        labelSelect.setId(1);
        labelSelect.setText("Select");
        labelSelect.setGravity(Gravity.LEFT);
        labelSelect.setTextColor(Color.BLACK);
        labelSelect.setTypeface(null, Typeface.BOLD);
        tr.addView(labelSelect);

        TextView labelCustomerName = new TextView(this);
        labelCustomerName.setId(2);
        labelCustomerName.setText("Customer Name");
        labelCustomerName.setGravity(Gravity.LEFT);
        labelCustomerName.setTextColor(Color.BLACK);
        labelCustomerName.setTypeface(null, Typeface.BOLD);
        tr.addView(labelCustomerName);

        TextView labelAddress = new TextView(this);
        labelAddress.setId(3);
        labelAddress.setText("Address");
        labelAddress.setGravity(Gravity.LEFT);
        labelAddress.setTextColor(Color.BLACK);
        labelAddress.setTypeface(null, Typeface.BOLD);
        tr.addView(labelAddress);

        TextView labelTelephone = new TextView(this);
        labelTelephone.setId(4);
        labelTelephone.setText("Telephone");
        labelTelephone.setGravity(Gravity.LEFT);
        labelTelephone.setTextColor(Color.BLACK);
        labelTelephone.setTypeface(null, Typeface.BOLD);
        tr.addView(labelTelephone);

        TextView labelCustomerStatus = new TextView(this);
        labelCustomerStatus.setId(5);
        labelCustomerStatus.setText("Status");
        labelCustomerStatus.setGravity(Gravity.LEFT);
        labelCustomerStatus.setTextColor(Color.BLACK);
        labelCustomerStatus.setTypeface(null, Typeface.BOLD);
        tr.addView(labelCustomerStatus);
        tblLayoutCustomerTable.addView(tr,
                new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

        try {
            int count = 1;

            for (final String[] custmerData : extraCustomers) {

                tr = new TableRow(this);
                tr.setId(1000 + count);
                tr.setPadding(0, 3, 0, 3);
                tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                        LayoutParams.WRAP_CONTENT));

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.DKGRAY);

                }

                CheckBox extraCustomerSelect = new CheckBox(this);
                tr.addView(extraCustomerSelect);

                TextView tvCustomerName = new TextView(this);
                tvCustomerName.setId(200 + count);
                tvCustomerName.setText(custmerData[5]);
                tvCustomerName.setGravity(Gravity.LEFT);
                tvCustomerName.setTextColor(Color.WHITE);
                tr.addView(tvCustomerName);

                TextView tvAddress = new TextView(this);
                tvAddress.setId(200 + count);
                tvAddress.setText(custmerData[8]);
                tvAddress.setGravity(Gravity.LEFT);
                tvAddress.setTextColor(Color.WHITE);
                tr.addView(tvAddress);

                TextView tvTelephone = new TextView(this);
                tvTelephone.setId(200 + count);
                tvTelephone.setText(custmerData[10]);
                tvTelephone.setGravity(Gravity.LEFT);
                tvTelephone.setTextColor(Color.WHITE);
                tr.addView(tvTelephone);

                TextView tvCustomerStatus = new TextView(this);
                tvCustomerStatus.setId(200 + count);
                tvCustomerStatus.setText(custmerData[13]);
                tvCustomerStatus.setGravity(Gravity.LEFT);
                tvCustomerStatus.setTextColor(Color.WHITE);
                tr.addView(tvCustomerStatus);

                count++;

                tblLayoutCustomerTable.addView(tr,
                        new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT));

                extraCustomerSelect
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                // TODO Auto-generated method stub
                                if (isChecked) {
                                    selectedCustomers.put(custmerData[0],
                                            custmerData);
                                } else {

                                    selectedCustomers.remove(custmerData[0]);
                                }

                            }
                        });

            }

            btnSaveExtraCustomer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Itinerary itineraryObject = new Itinerary(getApplication());
                    itineraryObject.openReadableDatabase();
                    SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    final String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
                    //List<String[]> result = itineraryObject.getAllItinerariesForADay(currentDate);
                    itineraryObject.closeDatabase();
                    key = 0;
                    //HARD CODED THE IF CONDITION TO HAVE TEMPORARY FUNCTIONALITY
//					if (key==0) {
                    if (selectedCustomers.size() > 0) {
                        reason.show();

                        btnSaveReason.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                String timeStamp = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                    Itinerary itinerary = new Itinerary(getApplication());
                                    Remarks remarksObject = new Remarks(ExtraCustomerActivity.this);


                                    for (String customerKey : selectedCustomers.keySet()) {
                                        String[] customerDetails = selectedCustomers.get(customerKey);


                                        itinerary.openWritableDatabase();
                                        String tempITenararyID = "EC" + customerDetails[1] + currentDate;
                                        long itineraryRowId = itinerary.insertItinerary("0", "EC" + customerDetails[1] + currentDate, currentDate, "" + key,
                                                customerDetails[1], customerDetails[2],
                                                customerDetails[5], "0", "2", timeStamp, "false", "false", "false");
                                        itinerary.closeDatabase();

                                        Log.w("st", "EC" + customerDetails[1] + currentDate);
                                        Log.w("st", "" + customerDetails[1]);
                                        Log.w("st", "" + customerDetails[2]);
                                        Log.w("st", "" + customerDetails[5]);

                                        if (key == 0) {
                                            itinerary.closeDatabase();
                                            itinerary.openWritableDatabase();
                                            itinerary.setIsActiveTrue(String.valueOf(itineraryRowId));
                                            itinerary.closeDatabase();
                                        }
                                        key++;

/**
 * there are hardcode values
 * tempITenararyID
 */


                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                            } else {
                                                getGPS();
                                            }


                                        }else {
                                            getGPS();

                                        }




                                        remarksObject.openWritableDatabase();
                                        remarksObject.insertRemark(String.valueOf(itineraryRowId),selectedReason, timeStamp, currentDate, customerDetails[1], repId, RemarksType.EXTAR_CUSTOMER.toString(), "0", tempITenararyID,Double.toString(lng),Double.toString(lat));
                                        remarksObject.closeDatabase();


                                    }


                                    finish();
                                    Intent iternaryListActivity = new Intent(getApplicationContext(), ItineraryList.class);
                                    startActivity(iternaryListActivity);
                                  //  txtReason.setText(null);
                                    reason.cancel();

                                    Toast successfullyAdded = Toast.makeText(getApplication(), "Customers added successfully to Itinerary", Toast.LENGTH_SHORT);
                                    successfullyAdded.setGravity(Gravity.TOP, 100, 100);
                                    successfullyAdded.show();

                                new UploadRemarksTask(getApplicationContext()).execute();
                            }
                        });
                    } else {
                        Toast itineraryAddFail = Toast.makeText(getApplication(), "Please select atleast one customer", Toast.LENGTH_SHORT);
                        itineraryAddFail.setGravity(Gravity.TOP, 50, 100);
                        itineraryAddFail.show();
                    }

//					} else {
//						if (selectedCustomers.size() > 0) {
//							j = 0;
//
//							for (String customerKey : selectedCustomers.keySet()) {
//								String[] customerDetails = selectedCustomers.get(customerKey);
//								
//								key = 0;
//								String timeStamp = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//								
//								final String arg1 = customerDetails[1] + currentDate;
//								final String arg2 = currentDate;
//								final String arg3 = "" + key; 
//								final String arg4 = customerDetails[1];
//								final String arg5 = customerDetails[2];
//								final String arg6 = customerDetails[5];
//								final String arg7 =  "10000";
//								final String arg8 =  "2";
//								final String arg9 = timeStamp;
//								
//								reason.show();
//								final Itinerary itinerary = new Itinerary(getApplication());
//								itinerary.openWritableDatabase();
//								
//								try {
//									getApplication().wait();
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								btnSaveReason.setOnClickListener(new View.OnClickListener() {
//								
//									public void onClick(View v) {
//										if (!txtReason.getText().toString().isEmpty()) {					
//											itinerary.insertItinerary(arg1, arg2, arg3,	arg4, arg5, arg6, arg7, arg8, arg9);
//											key++;
//											itinerary.closeDatabase();
//											Toast successfullyAdded = Toast.makeText(getApplication(), "Customers added successfully to Itinerary", Toast.LENGTH_SHORT);
//											successfullyAdded.show();
//											j++;
//											txtReason.setText(null);
//											reason.cancel();
//											getApplication().notifyAll();
//										}
//									}
//								});
//							}
//							finish();
//							Intent iternaryListActivity = new Intent(getApplicationContext(), ItineraryList.class);
//							startActivity(iternaryListActivity);
//						} else {
//							Toast itineraryAddFail = Toast.makeText(getApplication(), "Please select atleast one customer", Toast.LENGTH_SHORT);
//							itineraryAddFail.setGravity(Gravity.TOP, 50, 100);
//							itineraryAddFail.show();
//						}
//					}
                }
            });

        } catch (Exception e) {
            alertDialog.setTitle("Error");
            alertDialog.setMessage(e.toString());
            alertDialog.show();
        }

    }

    void setDate() {
        String currentDate = DateFormat.getDateInstance().format(new Date());
        tViewDate.setText(currentDate);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getGPS();
        }else {
            Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putSerializable("selectedCustomers", selectedCustomers);
        outState.putInt("key", key);
        outState.putInt("j", j);

    }

    @SuppressWarnings("unchecked")
    private void setBundleData(Bundle bundlData) {

        selectedCustomers = (HashMap<String, String[]>) bundlData.getSerializable("selectedCustomers");
        key = bundlData.getInt("key");
        j = bundlData.getInt("j");

    }


    private void getGPS() {

        String GPS = "";

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_LOCATION);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }


        if (location != null){


            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());


        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

/*
    private class YourAsyncTask extends AsyncTask<Void, Void, List<String[]>> {//sk

        @Override
        protected void onPostExecute(List<String[]> items) {
            // stop the loading animation or something
            ArrayAdapter<String> customerSearchAdapter = new ArrayAdapter<String>(
                   ExtraCustomerActivity. this, android.R.layout.simple_dropdown_item_1line,
                    customerNames);
            ((AutoCompleteTextView) atViewSearchCustomer)
                    .setAdapter(customerSearchAdapter);
            //adapter.addAll(items);
        }

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
           // adapter.clear(); // clear "old" entries (optional)
        }

        @Override
        protected List<String[]> doInBackground(Void... params) {
            // everything in here gets executed in a separate thread
            Customers customersObject = new Customers(ExtraCustomerActivity.this);
            customersObject.openReadableDatabase();
            List<String[]>    extraCustomers1 = customersObject.getAllCustomers();
             customerNames = customersObject.getCustomerNames();
            customersObject.closeDatabase();
            repconnector = new Reps(getApplicationContext());
            repconnector.openReadableDatabase();
            List<String[]> repList =   repconnector.getAllRepsDetails();
            repconnector.closeDatabase();
            if(!repList.isEmpty()){
                for(String[] ids:repList){
                    repId = ids[1];
                }
            }
            for (int i = 0; i < result.size(); i++) {
                for (int j = 0; j < 50; j++) {//sk
                    String[] itinerary = result.get(i);
                    String[] customer = extraCustomers1.get(j);
                    if (itinerary[4].equals(customer[2])) {
                        extraCustomers1.remove(j);
                    }
                }
            }
            extraCustomers=  extraCustomers1;
            return extraCustomers;
        }

    }*/

   /* private class SearchTask extends AsyncTask<String, Void, List<String[]>> {//sk

        @Override
        protected void onPostExecute(List<String[]> items) {
            // stop the loading animation or something

            //adapter.addAll(items);
        }

        @Override
        protected List<String[]> doInBackground(String... params) {
            Customers customersObject = new Customers(ExtraCustomerActivity.this);
            customersObject.openReadableDatabase();
            List<String[]> extraCustomers = customersObject.searchCustomers(params[0].toString(), "asc");
            customersObject.closeDatabase();

            for (int i = 0; i < result.size(); i++) {
                for (int j = 0; j < extraCustomers.size(); j++) {
                    String[] itinerary = result.get(i);
                    String[] customer = extraCustomers.get(j);
                    if (itinerary[4].equals(customer[2])) {
                        extraCustomers.remove(j);
                    }
                }
            }
            return extraCustomers;
        }

        @Override
        protected void onPreExecute() {
            // start loading animation maybe?
            // adapter.clear(); // clear "old" entries (optional)
        }

    }*/
}
