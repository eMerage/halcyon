package com.halcyon.channelbridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgebs.UploadNewCustomersTask;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.District;
import com.halcyon.channelbridgedb.ImageGallery;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.Sequence;
import com.halcyon.channelbridgedb.Town;
import com.halcyon.channelbridgews.WebService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.SecureRandom;
import java.sql.RowId;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomerActivity extends Activity implements LocationListener {

    static final int DATE_DIALOG_ID = 0;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE_IMAGE = 2;
    static public final int REQUEST_LOCATION = 100;

    static int cameraData = 0;
    boolean isImageset;
    EditText txtName, txtEmail, txtWeb, txtOwnerContact, txtPharmacistName,
            txtPurchasingOfficer, txtNoStaff, txtPharmacyRegNo, txtlatitude, txtlongitude;
    TextView tViewDate, tViewOwnerWifeBday;
    Spinner sCustomerStatus, txtDistrict;
    AutoCompleteTextView txtArea, txtTown, txtCustomerStatus, txtAddress, txtTelephone, txtBrNo, txtFax;
    ImageButton iBtnCalendar;
    Button btnSave, btnCancel, btsetGps;
    Builder alertCancel, alertSave;
    AlertDialog alertDialog;
    Intent ItineraryListIntent = new Intent("com.halcyon.channelbridge.ITINERARYLIST");
    Intent cameraIntent;
    ImageView ibtnCustomerImage;
    Bitmap customerImage;
    String customerId, displayPicture = "-1";
    double lat, lng;
    String pharmCode = "-1";
    boolean saveFlag = false;
    ArrayList<String> customerNameList = new ArrayList<String>();
    Location location;
    private LocationManager locationManager;
    private String provider;
    private int mYear;
    private int mMonth;
    private int mDay;
    AddCustomerActivity listener;

    byte[] image;

    String test;

    String picturePath;
    Uri selectedImage;

    String rowID;

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };

    //convert image to bitmap array
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        return outputStream.toByteArray();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 0;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    // convert from bitmap to byte array sk
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

//frank
 /*   protected void onStop() {
        super.onStop();
        this.finish();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);


        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 25, this);
//	    Criteria criteria = new Criteria();
//	    provider = locationManager.getBestProvider(criteria, false);
        //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        listener = new AddCustomerActivity();

        txtName = (AutoCompleteTextView) findViewById(R.id.etCustomerName);
        txtAddress = (AutoCompleteTextView) findViewById(R.id.etAddress);
        txtArea = (AutoCompleteTextView) findViewById(R.id.etArea);
        txtTown = (AutoCompleteTextView) findViewById(R.id.etTown);
        txtDistrict = (Spinner) findViewById(R.id.etDistrict);
        txtTelephone = (AutoCompleteTextView) findViewById(R.id.etTelephone);
        txtFax = (AutoCompleteTextView) findViewById(R.id.etFax);
        //txtCustomerStatus = (AutoCompleteTextView) findViewById(R.id.etCustomerStatus);
        sCustomerStatus = (Spinner) findViewById(R.id.etCustomerStatus);
        txtCustomerStatus = (AutoCompleteTextView) findViewById(R.id.etCustomerName);
        txtEmail = (EditText) findViewById(R.id.etEmail);
        txtWeb = (EditText) findViewById(R.id.etweb);
        txtBrNo = (AutoCompleteTextView) findViewById(R.id.etBrNo);
        txtOwnerContact = (EditText) findViewById(R.id.etOwnerContact);
        txtPharmacistName = (EditText) findViewById(R.id.etPharmacistName);
        txtPurchasingOfficer = (EditText) findViewById(R.id.etPurchasingOfficer);
        txtNoStaff = (EditText) findViewById(R.id.etNoStaff);
        tViewDate = (TextView) findViewById(R.id.tvDate);
        txtPharmacyRegNo = (EditText) findViewById(R.id.etPharmacyRegistrationNumber);
        tViewOwnerWifeBday = (TextView) findViewById(R.id.tvOwnerWifeBday);
        iBtnCalendar = (ImageButton) findViewById(R.id.bCalendar);
        txtlatitude = (EditText) findViewById(R.id.txtlan);
        txtlongitude = (EditText) findViewById(R.id.txtlon);
        btnSave = (Button) findViewById(R.id.bSaveCustomer);
        btnCancel = (Button) findViewById(R.id.bCancel);
        ibtnCustomerImage = (ImageView) findViewById(R.id.bGallery);
        btsetGps = (Button) findViewById(R.id.bsetGps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showGPSDisabledAlertToUser();

        } else {


            setDate();
            getLastSavedCustomerId();
            setInitialData();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                } else {
                    GetGPS();
                }
            }else {
                GetGPS();

            }




        }


        if (isImageset == true) {
            ibtnCustomerImage.setImageBitmap(customerImage);
        } else {
            ibtnCustomerImage.setImageResource(R.drawable.unknown_image);
        }
        // ibtnCustomerImage.setImageResource(R.drawable.unknown_image);
        iBtnCalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        if (getIntent().getExtras() != null) {
            isImageset = getIntent().getExtras().getBoolean("imageSet");
        }
        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);

        }


        // display the current date
        updateDisplay();

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alertSave = new AlertDialog.Builder(this)
                .setTitle("Customer Saved")
                .setMessage(txtName.getText().toString() + " Has Been saved! Do You want to go to the Itinerary?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
//								Sequence sequence = new Sequence(AddCustomerActivity.this);
//								sequence.openReadableDatabase();
//								String lastCustomer = sequence.getLastRowId("customers");
//								sequence.closeDatabase();
//								String custCode = String.valueOf(Integer.parseInt(lastCustomer));
//								String pharmCode = ItineraryList.DEVICE_ID+"_"+custCode;

                        Itinerary itineraryObject = new Itinerary(getApplication());
                        itineraryObject.openWritableDatabase();
                        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
                        String timeStamp = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        itineraryObject.insertItinerary("0", "TC" + timeStamp, currentDate, "" + 1,
                                rowID, rowID,
                                txtName.getText().toString(), "0", "2", timeStamp, "true", "true", "true");
                        itineraryObject.closeDatabase();
                        locationManager.removeUpdates(AddCustomerActivity.this);
                        //  clearImages();
                        AddCustomerActivity.this.onStop();
                        Clear();
                        finish();
                        startActivity(ItineraryListIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {

                        /*locationManager.removeUpdates(AddCustomerActivity.this);
                                Clear();
                                clearImages();
                                finish();
                                AddCustomerActivity.this.onStop();
                                startActivity(ItineraryListIntent);*/

                        locationManager.removeUpdates(AddCustomerActivity.this);
                        startActivity(ItineraryListIntent);
                        Clear();
                        clearImages();
                        AddCustomerActivity.this.onStop();
                        finish();
                        startActivity(ItineraryListIntent);
                    }
                });

        alertCancel = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(
                        "Changes have not been saved, are you sure you want to Cancel?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                                clearImages();
                                startActivity(ItineraryListIntent);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

        btnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//				Log.w("TEST : ", "CLICK TESTED");
                locationManager.removeUpdates(AddCustomerActivity.this);

                // if(location==null)
                // GetGPS();
                if (CheckDataForSave()) {
                    try {
                        locationManager.removeUpdates(AddCustomerActivity.this);
                        final String cName = txtName.getText().toString();
                        String cAddress = txtAddress.getText().toString();
                        String cArea = txtArea.getText().toString();
                        String cTown = txtTown.getText().toString();
                        String cDistrict = txtDistrict.getSelectedItem().toString();
                        String cTelephone = txtTelephone.getText().toString();
                        String cFax = txtFax.getText().toString();
                        String cEmail = txtEmail.getText().toString();
                        String cWeb = txtWeb.getText().toString();
                        String cCustomerStatus = sCustomerStatus.getSelectedItem().toString();
                        String cBrNo = txtBrNo.getText().toString();
                        String cOwnerContact = txtOwnerContact.getText()
                                .toString();
                        String cOwnerWifeBday = tViewOwnerWifeBday.getText().toString();
                        String cPharmacistName = txtPharmacistName.getText()
                                .toString();
                        String cPurchasingOfficer = txtPurchasingOfficer
                                .getText().toString();
                        String cNoStaff = txtNoStaff.getText().toString();
                        String cIsActive = "false"; // Temporary Fix
                        String pharmacyRegistration = txtPharmacyRegNo.getText().toString();


                        // Bitmap bitmap = ((BitmapDrawable) ibtnCustomerImage.getDrawable()).getBitmap();
                        // byte[] image = getBytes(bitmap);

                        // Get customer location coordinates
//						locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//						TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//						String device_id = tm.getDeviceId();


//						Criteria criteria = new Criteria();
//						provider = locationManager.getBestProvider(criteria, false);
//						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, getApplicationContext());
//						locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, null);
//						final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            System.out.println("Provider " + provider + " has been selected.");
                            lat = (double) (location.getLatitude());
                            lng = (double) (location.getLongitude());
                        }



//						Sequence sequence = new Sequence(AddCustomerActivity.this);
//						sequence.openReadableDatabase();
//						String lastCustomer = sequence.getLastRowId("customers");
//						sequence.closeDatabase();
//						String custCode = String.valueOf(Integer.parseInt(lastCustomer) + 1);
//						pharmCode = ItineraryList.DEVICE_ID+"_"+custCode;

                        Log.w("Latitude : ", String.valueOf(lat));
                        Log.w("Loggtitude : ", String.valueOf(lng));

                        //	Toast.makeText(AddCustomerActivity.this, Double.toString(lat) + " - "+ String.valueOf(lat)+ " - "+
                        //	String.valueOf(lng), Toast.LENGTH_LONG).show();
//
                        CustomersPendingApproval CustomersPendingApprovalObject = new CustomersPendingApproval(
                                AddCustomerActivity.this);
                        CustomersPendingApprovalObject.openWritableDatabase();

                        CustomersPendingApprovalObject.insertCustomer(rowID, cName, cAddress, cArea, cTown, cDistrict, cTelephone,
                                cFax, cEmail, cWeb, cCustomerStatus, cBrNo, cIsActive, cOwnerContact, cOwnerWifeBday, pharmacyRegistration,
                                cPharmacistName, cPurchasingOfficer, cNoStaff, String.valueOf(lat), String.valueOf(lng), rowID, image);

                        CustomersPendingApprovalObject.closeDatabase();

                        saveFlag = true;
                        btnSave.setEnabled(false);
                        //finish();


                        //startActivity(ItineraryListIntent);

                        new UploadNewCustomersTask(AddCustomerActivity.this).execute("1");

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddCustomerActivity.this, AlertDialog.THEME_HOLO_DARK);
                        alertDialog.setTitle("Send New Customer");
                        alertDialog.setMessage("New customer send successfully");

                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                // Intent iternaryListActivity = new Intent(
                                //  "com.marinaDreamronNewV4.channelbridge.ITINERARYLIST");
                                //  startActivity(iternaryListActivity);
                                // Toast.makeText(AddCustomerActivity.this, cName + " has Been saved!",Toast.LENGTH_SHORT).show();

                                alertSave.setMessage(cName + " has Been saved! Do You want to add this customer to the Itinerary?");
                                alertSave.show();

                                /*locationManager.removeUpdates(AddCustomerActivity.this);
                                Clear();
                                clearImages();
                                finish();
                                AddCustomerActivity.this.onStop();
                                startActivity(ItineraryListIntent);*/

                            }
                        });

                        alertDialog.show();


                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                locationManager.removeUpdates(AddCustomerActivity.this);
                // locationManager.removeUpdates((LocationListener) ItineraryListIntent);

                if ((!saveFlag) && CheckDataForCancel()) {
                    alertCancel.show();
                } else {
                    locationManager.removeUpdates(AddCustomerActivity.this);
                    finish();
                    clearImages();
                    startActivity(ItineraryListIntent);
                }

            }
        });

        ibtnCustomerImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

              /* Bundle extras = new Bundle();
                extras.putString("customerId", customerId);

                Intent startGallery = new Intent(getApplication(), CustomerImageGalleryActivity.class);
                //changed
                startGallery.putExtras(extras);
                // startActivityForResult(startGallery,REQUEST_CODE_IMAGE);
                finish();
                startActivity(startGallery);
*/


                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);


            }
        });


        txtName.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtName.getText().toString().isEmpty()) {
                        txtName.clearFocus();
                        txtAddress.requestFocus();
                        return true;
                    }
                    Log.w("tab ebuwa", "tab");
                }
                return false;
            }
        });

        txtAddress.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtAddress.getText().toString().isEmpty()) {
                        txtAddress.clearFocus();
                        txtArea.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtArea.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtArea.getText().toString().isEmpty()) {
                        txtArea.clearFocus();
                        txtTown.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtTown.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtTown.getText().toString().isEmpty()) {
                        txtTown.clearFocus();
                        txtDistrict.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtDistrict.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtDistrict.getSelectedItem().toString().isEmpty()) {
                        txtDistrict.clearFocus();
                        txtTelephone.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtTelephone.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtTelephone.getText().toString().isEmpty()) {
                        txtTelephone.clearFocus();
                        txtFax.setFocusable(true);
                        txtFax.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtFax.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtFax.clearFocus();
                    txtCustomerStatus.setFocusable(true);
                    txtCustomerStatus.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtCustomerStatus.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER) && (event.getRepeatCount() == 0)) {
//					if (!txtCustomerStatus.getText().toString().isEmpty()) {
                    txtCustomerStatus.clearFocus();
                    txtEmail.setFocusable(true);
                    txtEmail.requestFocus();
//						return true;
//					}
                    return true;
                }
                return false;
            }
        });

        txtEmail.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtEmail.clearFocus();
                    txtWeb.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtWeb.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtWeb.clearFocus();
                    txtBrNo.setFocusable(true);
                    txtBrNo.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtBrNo.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtBrNo.getText().toString().isEmpty()) {
                        txtBrNo.clearFocus();
                        txtOwnerContact.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtOwnerContact.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtOwnerContact.clearFocus();
                    txtPharmacistName.setFocusable(true);
                    txtPharmacistName.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtPharmacistName.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!txtPharmacistName.getText().toString().isEmpty()) {
                        txtPharmacistName.clearFocus();
                        txtPurchasingOfficer.setFocusable(true);
                        txtPurchasingOfficer.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        txtPurchasingOfficer.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtPurchasingOfficer.clearFocus();
                    txtNoStaff.setFocusable(true);
                    txtNoStaff.requestFocus();
                    return true;
                }
                return false;
            }
        });

        txtNoStaff.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER)) {
                    txtNoStaff.clearFocus();
                    btnSave.setFocusable(true);
                    btnSave.requestFocus();
                    return true;
                }
                return false;
            }
        });

        btsetGps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GetGPS();
            }
        });





        new InsertDistrictAndTownTask(AddCustomerActivity.this).execute();



    }

    /**
     * newly added by amila
     */

   /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


        // check if the request code is same as what is passed  here it is 2
        // if (requestCode == REQUEST_CODE_IMAGE) {
        // fetch the message String
        if (resultCode == Activity.RESULT_OK) {

            isImageset = data.getExtras().getBoolean("imageSet");
            // Set the message string in textView
        }

        //  }

    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            GetGPS();
        }else {
            Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }

    private void updateDisplay() {
        String finalMonth = "";
        String finalDay = "";
        if (mMonth + 1 < 10) {
            finalMonth = "0" + String.valueOf(mMonth + 1);
        } else {
            finalMonth = String.valueOf(mMonth + 1);
        }

        if (mDay < 10) {
            finalDay = "0" + String.valueOf(mDay);
        } else {
            finalDay = String.valueOf(mDay);
        }
        tViewOwnerWifeBday.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(finalDay).append("/").append(finalMonth).append("/")
                .append(mYear).append(""));
    }

    private void setInitialData() {
        // TODO Auto-generated method stub
        ArrayList<String> nameList = getNameList();
        ArrayAdapter<String> nameAdapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, nameList);
        ((AutoCompleteTextView) txtName).setAdapter(nameAdapterList);

        ArrayList<String> areaList = getAreaList();
        ArrayAdapter<String> areaAdaperList = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, areaList);
        ((AutoCompleteTextView) txtArea).setAdapter(areaAdaperList);

        ArrayList<String> townList = getTownList();
        ArrayAdapter<String> townAdapterList = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, townList);
        ((AutoCompleteTextView) txtTown).setAdapter(townAdapterList);

        //ArrayList<String> districtList = getDistrictList();
        //	ArrayAdapter<String> districtAdapterList = new ArrayAdapter<String>(
        //		this, android.R.layout.simple_dropdown_item_1line, districtList);
        //	((Spinner) txtDistrict).setAdapter(districtAdapterList);

        ArrayList<String> statusList = getStatusList();
        ArrayAdapter<String> statusAdapterList = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, statusList);
        ((AutoCompleteTextView) txtCustomerStatus).setAdapter(statusAdapterList);
        String primaryImage = null;

        try {
            ImageGallery imageGallery = new ImageGallery(this);
            imageGallery.openReadableDatabase();
            primaryImage = imageGallery.getPrimaryImageforCustomerId(customerId);
            imageGallery.closeDatabase();
        } catch (Exception e) {
            System.out.println("Unable to get display pic"+ e.toString());

        }

        try {
            Log.w("Primary Image", primaryImage + "");
            File customerImageFile = new File(
                    Environment.getExternalStorageDirectory() + File.separator
                            + "DCIM" + File.separator + "Channel_Bridge_Images"
                            + File.separator + primaryImage);

            if (customerImageFile.exists()) {

                try {
                    ibtnCustomerImage.setImageBitmap(decodeSampledBitmapFromResource(String.valueOf(customerImageFile), 400, 550));
                } catch (IllegalArgumentException e) {
                    Log.w("Illegal argument exception", e.toString());
                } catch (OutOfMemoryError e) {
                    Log.w("Out of memory error :(", e.toString());
                }

            } else {
                ibtnCustomerImage.setImageResource(R.drawable.unknown_image);
            }
        } catch (Exception e) {
            Log.w("Error setting image file", e.toString());
        }

    }

    public String GetIamge(Bitmap bitmap) {
        String encodedImage;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
        encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        return encodedImage;
    }

    private void getLastSavedCustomerId() {
        // TODO Auto-generated method stub
        Sequence sequence = new Sequence(this);
        sequence.openReadableDatabase();
        String lastRowId = sequence.getLastRowId("customers_pending_approval");
        sequence.closeDatabase();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");

        if (lastRowId.isEmpty()) {
            customerId = "0";
            pharmCode = deviceId + "_" + customerId;
        } else {
            customerId = String.valueOf(Integer.parseInt(lastRowId) + 1);
            pharmCode = deviceId + "_" + customerId;
        }


        Log.w("CUstomerIds SIze", customerId);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if ((!saveFlag) && CheckDataForCancel()) {
                alertCancel.show();
            } else {
                finish();
                clearImages();
                startActivity(ItineraryListIntent);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean CheckDataForCancel() {
        boolean flagForCancel = false;

        if ((txtName.getText().toString().length() != 0)
                || (txtAddress.getText().toString().length() != 0)
                || (txtArea.getText().toString().length() != 0)
                || (txtTown.getText().toString().length() != 0)
                || (txtDistrict.getSelectedItem().toString().length() != 0)
                || (txtTelephone.getText().toString().length() != 0)
                || (txtFax.getText().toString().length() != 0)
                || (txtCustomerStatus.getText().toString().length() != 0)
                || (txtEmail.getText().toString().length() != 0)
                || (txtWeb.getText().toString().length() != 0)
                || (txtBrNo.getText().toString().length() != 0)
                || (txtOwnerContact.getText().toString().length() != 0)
                || (txtPharmacistName.getText().toString().length() != 0)
                || (txtPurchasingOfficer.getText().toString().length() != 0)
                || (txtNoStaff.getText().toString().length() != 0)) {
            flagForCancel = true;
        }

        return flagForCancel;
    }

    public boolean CheckDataForSave() {
        boolean flagForSave = false;

        //  if (location != null) {
        /*    if (isImageset == false) {
                Toast.makeText(this, "Please take a image.Default image cannot be used!", Toast.LENGTH_SHORT).show();
                txtAddress.setFocusable(true);
                txtAddress.requestFocus();
                flagForSave = false;
            }*/
        //  if (image == null){

        if(image != null){
            if (!txtlatitude.getText().toString().isEmpty()) {
                if (!txtlongitude.getText().toString().isEmpty()) {
                    if (isImageset == true) {
                        if (!txtName.getText().toString().isEmpty()) {
                            if (!txtAddress.getText().toString().isEmpty()) {
                                String address = txtAddress.getText().toString();
                                address = address.trim();
                                txtAddress.setText(address);
                                if (!txtArea.getText().toString().isEmpty()) {
                                    if (!txtTown.getText().toString().isEmpty()) {
                                        if (!txtDistrict.getSelectedItem().toString().contentEquals("Select District..")) {
                                            if (!txtTelephone.getText().toString().isEmpty()) {
                                                if (!sCustomerStatus.getSelectedItem().toString().contentEquals("Select Customer Status..")) {
                                                    if (!txtPharmacistName.getText().toString().isEmpty()) {
                                                        if (!((txtTelephone.getText().toString().length() < 10) || (txtTelephone.getText().toString().length() > 10))) {
                                                            if (!((!txtFax.getText().toString().isEmpty()) && ((txtFax.getText().toString().length() < 10) || (txtFax.getText().toString().length() > 10)))) {


                                                                String customerName = txtName.getText().toString();
                                                                flagForSave = true;
                                                                for (String name : customerNameList) {
                                                                    if (customerName.equals(name)) {
                                                                        flagForSave = false;
                                                                        Toast nameDuplicated = Toast.makeText(this, "A Customer with this name already exists!", Toast.LENGTH_SHORT);
                                                                        nameDuplicated.setGravity(Gravity.TOP, 50, 100);
                                                                        nameDuplicated.show();
                                                                        txtName.setFocusable(true);
                                                                        txtName.requestFocus();
                                                                    }
                                                                }


                                                            } else {
                                                                Toast faxDigitError = Toast.makeText(this, "The Fax Number should be 10 digits!", Toast.LENGTH_SHORT);
                                                                faxDigitError.show();
                                                                txtFax.setFocusable(true);
                                                                txtFax.requestFocus();
                                                                flagForSave = false;
                                                            }
                                                        } else {
                                                            Toast telephoneDigitError = Toast.makeText(this, "The Telephone Number should be 10 digits!", Toast.LENGTH_SHORT);
                                                            telephoneDigitError.show();
                                                            txtTelephone.setFocusable(true);
                                                            txtTelephone.requestFocus();
                                                            flagForSave = false;
                                                        }
                                                    } else {
                                                        Toast.makeText(this, "Pharmacist name cannot be empty!", Toast.LENGTH_SHORT).show();
                                                        txtPharmacistName.setFocusable(true);
                                                        txtPharmacistName.requestFocus();
                                                        flagForSave = false;
                                                    }
                                                } else {
                                                    Toast.makeText(this, "Select Customer status field !", Toast.LENGTH_SHORT).show();
                                                    txtCustomerStatus.setFocusable(true);
                                                    txtCustomerStatus.requestFocus();
                                                    flagForSave = false;
                                                }
                                            } else {
                                                Toast.makeText(this, "Telephone number field cannot be empty!", Toast.LENGTH_SHORT).show();
                                                txtTelephone.setFocusable(true);
                                                txtTelephone.requestFocus();
                                                flagForSave = false;
                                            }
                                        } else {
                                            Toast.makeText(this, " Select District field !", Toast.LENGTH_SHORT).show();
                                            txtDistrict.setFocusable(true);
                                            txtDistrict.requestFocus();
                                            flagForSave = false;
                                        }
                                    } else {
                                        Toast.makeText(this, "Town field cannot be empty!", Toast.LENGTH_SHORT).show();
                                        txtTown.setFocusable(true);
                                        txtTown.requestFocus();
                                        flagForSave = false;
                                    }


                                } else {
                                    Toast.makeText(this, "Area field cannot be empty!", Toast.LENGTH_SHORT).show();
                                    txtArea.setFocusable(true);
                                    txtArea.requestFocus();
                                    flagForSave = false;
                                }
                            } else {
                                Toast.makeText(this, "Address field cannot be empty!", Toast.LENGTH_SHORT).show();
                                txtAddress.setFocusable(true);
                                txtAddress.requestFocus();
                                flagForSave = false;
                            }

                            //
                        } else {
                            Toast.makeText(this, "Name field cannot be empty!", Toast.LENGTH_SHORT).show();
                            txtName.setFocusable(true);
                            txtName.requestFocus();
                            flagForSave = false;
                        }
                    } else {
                        Toast.makeText(this, "Please take a image.Default image cannot be used!", Toast.LENGTH_SHORT).show();
                        txtAddress.setFocusable(true);
                        txtAddress.requestFocus();

                        flagForSave = false;
                    }
                } else {
                    Toast.makeText(this, "longitude is empty!", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "Latitude is empty!", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "Please take a image.Default image cannot be used!", Toast.LENGTH_SHORT).show();
            txtAddress.setFocusable(true);
            txtAddress.requestFocus();

            flagForSave = false;
        }




        return flagForSave;
    }

    void setDate() {
        String currentDate = DateFormat.getDateInstance().format(new Date());

        tViewDate.setText(currentDate);
    }

    public ArrayList<String> getNameList() {
        Customers customersObject = new Customers(this);
        customersObject.openReadableDatabase();
        ArrayList<String> customerNamesListArray = customersObject.getCustomerNames();
        customersObject.closeDatabase();
        customerNameList = customerNamesListArray;
        return customerNamesListArray;
    }

    public ArrayList<String> getStatusList() {
        Customers customersObject = new Customers(this);
        customersObject.openReadableDatabase();
        ArrayList<String> statusTypesArrayList = customersObject.getCustomerStatusTypes();
        customersObject.closeDatabase();
        return statusTypesArrayList;
    }

    public ArrayList<String> getTownList() {

        Customers customersObject = new Customers(this);
        customersObject.openReadableDatabase();
        ArrayList<String> townListArrayList = customersObject.getTownList();
        customersObject.closeDatabase();
        return townListArrayList;
    }

    public ArrayList<String> getAreaList() {

        Customers customersObject = new Customers(this);
        customersObject.openReadableDatabase();
        ArrayList<String> areaListArray = customersObject.getAreaList();
        customersObject.closeDatabase();
        return areaListArray;
    }

    public ArrayList<String> getDistrictList() {

        Customers customersObject = new Customers(this);
        customersObject.openReadableDatabase();
        ArrayList<String> districtListArray = customersObject.getDistrictList();
        customersObject.closeDatabase();
        return districtListArray;
    }

    private void clearImages() {
        // TODO Auto-generated method stub
        ArrayList<String> requiredImageIdsForDp = new ArrayList<String>();
        requiredImageIdsForDp = getRequiredFileNames(customerId);
        try {
            for (String fName : requiredImageIdsForDp) {
                File customerImageFile = new File(
                        Environment.getExternalStorageDirectory() + File.separator
                                + "DCIM" + File.separator + "Channel_Bridge_Images"
                                + File.separator + fName);
                if (customerImageFile.exists()) {
                    customerImageFile.delete();
                }
            }
        } catch (Exception e) {
            Log.w("delete on cancel error", e.toString());
        }
    }

    private ArrayList<String> getRequiredFileNames(String customerId) {
        File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images");
        boolean pathExists = path.exists();

        ArrayList<String> requiredImageIds = new ArrayList<String>();

        try {
            Log.w("PATH EXISTS", String.valueOf(pathExists));
            String[] files = path.list();

            if (!(files.length == 0)) {
                Log.w("CUSTOMER ID", customerId);
                for (int i = 0; i < files.length; i++) {

                    String temp = files[i];
                    int firstUnderScore = temp.indexOf("_");
                    int secondUnderScore = temp.indexOf("_", firstUnderScore + 1);
                    Log.w("first" + firstUnderScore, "second" + secondUnderScore);
                    String customerIdfromFile = temp.substring(firstUnderScore + 1, secondUnderScore);

                    Log.w("customerIdFromFile", customerIdfromFile);

                    if (customerId.contentEquals(customerIdfromFile)) {
                        requiredImageIds.add(temp);
                    }
                }
            } else {
                Toast.makeText(this, "This file location is empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.w("CustomerImageGallery: getRequiredFileNames", ex.toString());
        }

        if (!requiredImageIds.isEmpty()) {
            for (String imageId : requiredImageIds) {
                Log.w("REQUIRED IMAGE Ids", imageId.toString());
            }
        }
        return requiredImageIds;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putInt("cameraData", cameraData);
        outState.putInt("mYear", mYear);
        outState.putInt("mMonth", mMonth);
        outState.putInt("mDay", mDay);
        outState.putString("customerId", customerId);
        outState.putString("displayPicture", displayPicture);
        outState.putString("provider", provider);
        outState.putString("pharmCode", pharmCode);
        outState.putDouble("lat", lat);
        outState.putDouble("lng", lng);

        outState.putString("text", test);
        outState.putBoolean("isImageset", isImageset);


        //  System.out.println("onSaveInstanceStatessssssssssssss :"+lat);


    }

    private void setBundleData(Bundle bundlData) {

        cameraData = bundlData.getInt("cameraData");
        mYear = bundlData.getInt("mYear");
        mMonth = bundlData.getInt("mMonth");
        mDay = bundlData.getInt("mDay");
        customerId = bundlData.getString("customerId");
        displayPicture = bundlData.getString("displayPicture");
        provider = bundlData.getString("provider");
        pharmCode = bundlData.getString("pharmCode");
        lat = bundlData.getDouble("lat");
        lng = bundlData.getDouble("lng");

        isImageset = bundlData.getBoolean("isImageset");
        System.out.println("setBundleData :" + isImageset);

        test = bundlData.getString("text");


    }

    public void onLocationChanged(Location location) {

        try {
            this.location = location;
            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());

            String la = Double.toString(lat);
            String lo = Double.toString(lng);
            txtlatitude.setText(la);
            txtlongitude.setText(lo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    private void GetGPS() {


        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_LOCATION);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }






            String la = "0.00";
            String lo = "0.00";

            if (location == null) {

                txtlatitude.setText(la);
                txtlongitude.setText(lo);
                btsetGps.setVisibility(View.VISIBLE);

                // showGPSDisabledAlertToUser();
            } else {

                // locationManager.removeUpdates(AddCustomerActivity.this);
                btsetGps.setVisibility(View.INVISIBLE);
                lat = (double) (location.getLatitude());
                lng = (double) (location.getLongitude());


                la = Double.toString(lat);
                lo = Double.toString(lng);

                locationManager.removeUpdates(AddCustomerActivity.this);
                txtlatitude.setText(la);
                txtlongitude.setText(lo);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent iternaryListActivity = new Intent(
                                        "com.halcyon.channelbridge.ITINERARYLIST");
                                startActivity(iternaryListActivity);
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                // GetGPS();
                                // dialog.cancel();

                                finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  dialog.cancel();
                        Intent iternaryListActivity = new Intent(
                                "com.halcyon.channelbridge.ITINERARYLIST");
                        startActivity(iternaryListActivity);
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void Clear() {
        txtName.setText("");
        txtAddress.setText("");
        txtArea.setText("");
        txtTown.setText("");
        // txtDistrict .getSelectedItem().t;
        txtTelephone.setText("");
        txtFax.setText("");
        //txtCustomerStatus = (AutoCompleteTextView) findViewById(R.id.etCustomerStatus);

        txtCustomerStatus.setText("");
        txtEmail.setText("");
        txtWeb.setText("");
        txtBrNo.setText("");
        txtOwnerContact.setText("");
        txtPharmacistName.setText("");
        txtPurchasingOfficer.setText("");
        txtNoStaff.setText("");
        tViewDate.setText("");
        txtPharmacyRegNo.setText("");
        tViewOwnerWifeBday.setText("");
        // iBtnCalendar = (ImageButton) findViewById(R.id.bCalendar);
        txtlatitude.setText("");
        txtlongitude.setText("");


    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String previousImage = null;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ibtnCustomerImage.setImageBitmap(photo);

            image = getBytes(photo);
            isImageset = true;


            customerImage = photo;
            ImageGallery imageGallery = new ImageGallery(this);
            int imageId = 0;
            try {
                imageGallery.openReadableDatabase();
                previousImage = imageGallery.getLastImageNameForCustomer(customerId);
                imageGallery.closeDatabase();
                if (previousImage.contentEquals("-1")) {
                    imageId = 0;
                } else {
                    imageId = Integer.parseInt(previousImage) + 1;
                    Log.w("imageId", imageId + "");
                }
            } catch (Exception e) {
                Log.w("getlast image ", e.toString());
            }


            rowID = genCusNumber();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String deviceId = sharedPreferences.getString("DeviceId", "-1");

            String filename = deviceId + "_" + customerId + "_" + nextSessionId() + "_" + imageId + ".jpg";
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images" + File.separator + filename);

            try {
                FileOutputStream out = new FileOutputStream(path);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                imageGallery.openWritableDatabase();
                long r = imageGallery.insertNewImage(rowID, String.valueOf(imageId), filename, "false", "false");
                Log.w("result", r + "");
                imageGallery.closeDatabase();

            } catch (Exception e) {
                System.out.println("kjdsheo :" + e);
                Log.w("PHOTO ERROR", e.toString());

            } finally {

            }


        }
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

    public String genCusNumber() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddCustomerActivity.this);
        String deviceId = sharedPreferences.getString("DeviceId", "-1");

        Random ra = new Random();
        int num = ra.nextInt(1000);


        String genCode = null;
        SecureRandom random = new SecureRandom();
        genCode = new BigInteger(20, random).toString(32);
        if (genCode.length() != 3) {
            genCode = genCode + 0;
        } else {

        }

        return deviceId + "_" + num + genCode.toUpperCase();

    }

    public class InsertDistrictAndTownTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        District district;
        Town town;
        ProgressDialog dialog;


        public InsertDistrictAndTownTask(Context context) {
            this.context = context;

            district = new District(context);
            town =  new Town(context);

        }

        @Override
        protected void onPreExecute() {
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Fetching User Data from Server...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setProgress(0);

                dialog.setMax(100);
                dialog.show();
            } catch (Exception e) {
                dialog.dismiss();
            }

        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Void returnCode) {
            dialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
             if(!district.checkDistrictAvalability()){

                /* district.insert_District(1,"Gampaha");

                 town.openWritableDatabase();
                 town.insert_Town(1,"Negombo",1);
                 town.closeDatabase();*/

                 try {
                     JSONArray m_jArry = new JSONArray(loadJSONFromAsset());




                 } catch (JSONException e) {
                     e.printStackTrace();
                 }




             }else {

             }

            return null;
        }


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("districtlist.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
