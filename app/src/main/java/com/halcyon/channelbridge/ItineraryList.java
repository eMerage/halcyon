package com.halcyon.channelbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgebs.UploadAttendenceTask;
import com.halcyon.channelbridgedb.Attendence;
import com.halcyon.channelbridgedb.DealerSales;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgehelp.VideoListDemoActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ItineraryList extends Activity implements
        SearchView.OnQueryTextListener {
    //
    TextView tViewDayTarget, tViewInvoiceValue, tViewVariance,
            tViewVariancePrecentage, tViewDate;
    Location location;
    private LocationManager locationManager;
    private Invoice invoiceHandler;
    private String provider;
    private Boolean iswebApprovalActive;
    private DealerSales dealerSalesHandler;
    String pharmacyId = "";
    // private SearchView mSearchView;
//	public static String DEVICE_ID, REP_ID, USER_LOGIN;

    // static boolean firstTime = true;
// Start the  service


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_itinerary);
        checkAttendenceIsUpload();
        tViewDayTarget = (TextView) findViewById(R.id.tvDayTarget);
        tViewInvoiceValue = (TextView) findViewById(R.id.tvInvoiceValue);
        tViewVariance = (TextView) findViewById(R.id.tvVariance);
        tViewVariancePrecentage = (TextView) findViewById(R.id.tvVariancePercentage);

        tViewDate = (TextView) findViewById(R.id.tvDate);
        invoiceHandler = new Invoice(ItineraryList.this);
        dealerSalesHandler = new DealerSales(ItineraryList.this);

        if (getIntent().hasExtra("DeviceId")) {
            Bundle extras = getIntent().getExtras();
			pharmacyId = extras.getString("PharmacyId");
//			REP_ID = extras.getString("RepId");
//			USER_LOGIN = extras.getString("UserLogin");			

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DeviceId", extras.getString("DeviceId"));
            editor.putString("RepId", extras.getString("RepId"));
            editor.putString("UserLogin", extras.getString("UserLogin"));
            editor.commit();

        }

        // Log.w("USER LOGIN ROW ID zzzzzzzzzzzz", USER_LOGIN);
        setItinerarySummary();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Builder alertCancel = new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want Exit?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();

                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    return;
                                }
                            });
            alertCancel.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.itinerary_list_action_menu, menu);

        return true;
    }

    // private void setupSearchView(MenuItem searchItem) {
    // if (isAlwaysExpanded()) {
    // mSearchView.setIconifiedByDefault(false);
    // } else {
    // searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
    // | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    // }
    // SearchManager searchManager = (SearchManager)
    // getSystemService(Context.SEARCH_SERVICE);
    // if (searchManager != null) {
    // List<SearchableInfo> searchables =
    // searchManager.getSearchablesInGlobalSearch();
    // SearchableInfo info =
    // searchManager.getSearchableInfo(getComponentName());
    // for (SearchableInfo inf : searchables) {
    // if (inf.getSuggestAuthority() != null
    // && inf.getSuggestAuthority().startsWith("applications")) {
    // info = inf;
    // }
    // }
    // mSearchView.setSearchableInfo(info);
    // }
    // mSearchView.setOnQueryTextListener((OnQueryTextListener) this);
    // }
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        AlertDialog alertDialog = new AlertDialog.Builder(ItineraryList.this)
                .create();
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("test : " + query);
        alertDialog.show();
        return false;
    }

    public boolean onClose() {
        // AlertDialog alertDialog = new
        // AlertDialog.Builder(ItineraryList.this).create();
        // alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int which) {
        // return;
        // }
        // });
        // alertDialog.setTitle("Alert");
        // alertDialog.setMessage("test : Closed");
        // alertDialog.show();
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_delivery_management:

                Intent deliveryManagement = new Intent("com.halcyon.channelbridge.DeliveryManagement");
                startActivity(deliveryManagement);

                break;


            case R.id.menu_addCustomer:

                finish();

                Intent addCustomerIntent = new Intent("com.halcyon.channelbridge.ADDCUSTOMERACTIVITY");
                startActivity(addCustomerIntent);

                break;

            case R.id.menu_extraCustomer:

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean extraCustomerEnabled = sharedPreferences.getBoolean("cbPrefEnableAddExtraCustomer", true);

                if (extraCustomerEnabled) {
                    finish();
                    Intent extraCustomerIntent = new Intent("com.halcyon.channelbridge.EXTRACUSTOMERACTIVITY");
                    startActivity(extraCustomerIntent);
                } else {
                    Toast noMatchesFound = Toast.makeText(getApplication(), "This feature is not availabe.", Toast.LENGTH_SHORT);
                    noMatchesFound.show();
                }

                break;
            case R.id.menu_CompetitorProducts:
                Intent startCompetitorProducts = new Intent("com.halcyon.channelbridge.COMPETITORPRODUCTACTIVITY");
                startActivity(startCompetitorProducts);
                break;

            case R.id.menu_preference:
                Intent preferences = new Intent("com.halcyon.channelbridge.PREF");
                finish();
                startActivity(preferences);
                break;

            case R.id.menu_repStore:

                Intent repStore = new Intent("com.halcyon.channelbridge.PRODUCTREPSTOREACTIVITY");
                finish();
                startActivity(repStore);
                break;

            case R.id.menu_priceList:
                Intent priceListIntent = new Intent("com.halcyon.channelbridge.PRICELISTACTIVITY");
                finish();
                startActivity(priceListIntent);
                break;

            case R.id.menu_syncronizePreference:
               // Intent syncronizePreferences = new Intent("com.halcyon.channelbridge.SYNCRONIZEPREFERENCE");
                Intent syncronizePreferences = new Intent("com.halcyon.channelbridge.ManualSync");
                startActivity(syncronizePreferences);
                break;

            case R.id.menu_about:
                Intent about = new Intent(
                        "com.halcyon.channelbridge.ABOUTAPPLICATION");
                finish();
                startActivity(about);
                break;
            case R.id.menu_repAddence:
                finish();
                Intent attendence = new Intent(
                        "RepAttendance");

                startActivity(attendence);
                break;

            case R.id.menu_collection_note:

                SharedPreferences sharedPreferences1 = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                String deviceId = sharedPreferences1.getString("DeviceId", "-1");
                String repId = sharedPreferences1.getString("RepId", "-1");

/**
 * commented for error handle....should do the synch task manually
 */
             /*   new Download_DEL_Outstanding(ItineraryList.this)
                        .execute(deviceId,
                                repId);

                new DownloadCustomersTask(ItineraryList.this)
                        .execute(deviceId,
                                repId);

*/

                Intent startItinerary = new Intent(ItineraryList.this,
                        CollectionNote.class);
                finish();
                startActivity(startItinerary);

                break;
            case R.id.menu_help:
                //    new RefreshTask(getApplicationContext()).execute();
                Intent goVideodemo = new Intent(ItineraryList.this,
                        VideoListDemoActivity.class);
                startActivity(goVideodemo);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setItinerarySummary() {

        Itinerary itinerary = new Itinerary(ItineraryList.this);
        itinerary.openReadableDatabase();

        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
        String dayTarget = "0";
         dayTarget = itinerary.getItineraryDayTarget(currentDate);

        tViewDayTarget.setText(dayTarget);

        itinerary.closeDatabase();

        String systemDate = DateFormat.getDateInstance().format(new Date());

        tViewDate.setText(systemDate);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String returnNumber = preferences.getString("ReturnNumber", null);
        Log.w("RETURN NUMBER DDDDDDDDDDDDDD", returnNumber + "");

        String invoDate = new SimpleDateFormat("dd/MM/yyyy")
                .format(new Date());

        SharedPreferences shared = getApplicationContext().getSharedPreferences("CBHesPreferences", Context.MODE_PRIVATE);
        iswebApprovalActive = (shared.getBoolean("WebApproval",true));
        String invoiceSum = "0";

        if (iswebApprovalActive == false) {
            invoiceSum = invoiceHandler.getInvoiceSumforGivenDate(invoDate);
        }else{
            invoiceSum = dealerSalesHandler.getInvoiceSumforGivenDate(invoDate);
        }
        tViewInvoiceValue.setText(invoiceSum);

        if (dayTarget.isEmpty()){
            dayTarget = "0";
        }
        if (invoiceSum.isEmpty()){
            invoiceSum = "0";
        }

        double variance = 0.00;
        double target = Double.parseDouble(dayTarget);
        double invoiceTotal = Double.parseDouble(invoiceSum);
        variance = target - invoiceTotal;


        tViewVariance.setText(String.format("%.2f", variance));
        double variPercentage = (variance * 100)/target;

        tViewVariancePrecentage.setText(String.format("%.2f", variPercentage));




    }

    public void setFirstItineraryStatusTrue() {
        Itinerary itinerary = new Itinerary(this);
        itinerary.openWritableDatabase();
        itinerary.setIsActiveTrue("1");
        itinerary.closeDatabase();
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                // GetGPS();
                                // dialog.cancel();

                                // finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void checkAttendenceIsUpload() {

        Attendence attendence = new Attendence(this);
        attendence.openReadableDatabase();
        ArrayList<String> notUploadList = attendence.getAttendenceNotUpload();
        attendence.closeDatabase();

        if (notUploadList.size() > 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Attendance might not be uploaded .Manual sync should perform")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (isNetworkAvailable() == true) {

                                        new UploadAttendenceTask(getApplicationContext()).execute();
                                        dialog.dismiss();
                                    }
                                }
                            });
            alertDialogBuilder.show();

        }

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(ItineraryList.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public static class DetailsActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                finish();
                return;
            }

            if (savedInstanceState == null) {

                ItineraryDetailsFragment details = new ItineraryDetailsFragment();
                details.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();

            }
        }
    }
}
