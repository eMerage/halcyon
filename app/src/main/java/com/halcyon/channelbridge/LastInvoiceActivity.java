package com.halcyon.channelbridge;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.Itinerary;

public class LastInvoiceActivity extends Activity {

    ListView lViewInvoiceList;
    Button btnDone;
    String rowId, pharmacyId;
    TextView tViewCustomerName;
    ArrayList<String[]> invoiceListData = new ArrayList<String[]>();
    LastInvoiceListAdapter lastInvoiceListAdapter;
    boolean temporaryCustomer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_invoice);

        lViewInvoiceList = (ListView) findViewById(R.id.lvInvoiceList);
        btnDone = (Button) findViewById(R.id.bDone);
        tViewCustomerName = (TextView) findViewById(R.id.tvCustomerName);

        if (savedInstanceState != null) {
            setBundleData(savedInstanceState);
        } else {
            getDataFromPreviousActivity();
        }


        setInitialData();
        getDataForInvoiceList();
        setListAdapter(invoiceListData);


        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent itineraryList = new Intent(LastInvoiceActivity.this, ItineraryList.class);
                finish();
                startActivity(itineraryList);
            }
        });

        lViewInvoiceList.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("static-access")
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Vibrator longPressVibe = (Vibrator) getApplication().getSystemService(getApplication().VIBRATOR_SERVICE);
                longPressVibe.vibrate(50);


                String[] temp = invoiceListData.get(arg2);
                String invoiceNumber = temp[0];
                Intent viewInvoice = new Intent(LastInvoiceActivity.this, InvoiceViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Id", rowId);
                extras.putString("PharmacyId", pharmacyId);
                extras.putString("InvoiceNumber", invoiceNumber);

                viewInvoice.putExtras(extras);
                finish();
                startActivity(viewInvoice);
            }
        });

        lViewInvoiceList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @SuppressWarnings("static-access")
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // TODO Auto-generated method stub
                Vibrator longPressVibe = (Vibrator) getApplication().getSystemService(getApplication().VIBRATOR_SERVICE);
                longPressVibe.vibrate(50);


                String[] temp = invoiceListData.get(arg2);
                String invoiceNumber = temp[0];
                Intent viewInvoice = new Intent(LastInvoiceActivity.this, InvoiceViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("Id", rowId);
                extras.putString("PharmacyId", pharmacyId);
                extras.putString("InvoiceNumber", invoiceNumber);

                viewInvoice.putExtras(extras);
                finish();
                startActivity(viewInvoice);
                return false;
            }
        });


    }

    private void setInitialData() {
        // TODO Auto-generated method stub
        Itinerary itinerary = new Itinerary(LastInvoiceActivity.this);
        itinerary.openReadableDatabase();
        String status = itinerary.getItineraryStatus(rowId);
        itinerary.closeDatabase();

        if (status.contentEquals("true")) {
            CustomersPendingApproval customersPendingApproval = new CustomersPendingApproval(this);
            customersPendingApproval.openReadableDatabase();
            String[] customerPendingDetails = customersPendingApproval.getCustomerDetailsByPharmacyId(pharmacyId);
            customersPendingApproval.closeDatabase();
            tViewCustomerName.setText(customerPendingDetails[1]);
            Log.w("NAME", customerPendingDetails[1] + "");
            temporaryCustomer = true;

        } else if (status.contentEquals("false")) {
            Customers customers = new Customers(this);
            customers.openReadableDatabase();
            String[] customerDetails = customers.getCustomerDetailsByPharmacyId(pharmacyId);
            customers.closeDatabase();
            tViewCustomerName.setText(customerDetails[5]);
            Log.w("NAME", customerDetails[5] + "");
            temporaryCustomer = false;
        }
    }

    private void getDataForInvoiceList() {
        // TODO Auto-generated method stub

//		if (temporaryCustomer) {
//			Itinerary itinerary = new Itinerary(LastInvoiceActivity.this);
//			Invoice invoice = new Invoice(LastInvoiceActivity.this); 
//			
//			itinerary.openReadableDatabase();
//			String[] itns = itinerary.getAllItinerayRowIdsForPharmacyId(pharmacyId);
//			itinerary.closeDatabase();
//			Log.w("Itinerary for pharmacy ID", itns.length + "");
//			
//			for (int i = 0; i < itns.length; i++) {
//				String itineraryId = itns[i];
//				
//				invoice.openReadableDatabase();
//				ArrayList<String> invoiceIds = invoice.getInvoiceIdByItineraryId(itineraryId);
//				invoice.closeDatabase();
//				Log.w("itinerary ids size",invoiceIds.size() + "");
//				for (String invoiceId: invoiceIds) {
//					invoice.openReadableDatabase();
//					ArrayList<String> invoiceDetails = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
//					invoice.closeDatabase();
//					Log.w("Invoice details size", invoiceDetails.size() + "");
//					invoiceListData.add(convertArrayListToArray(invoiceDetails));
//				}
//			}
//		} else {
        Itinerary itinerary = new Itinerary(LastInvoiceActivity.this);
        Invoice invoice = new Invoice(LastInvoiceActivity.this);

        itinerary.openReadableDatabase();
        String[] itns = itinerary.getAllItinerayRowIdsForPharmacyId(pharmacyId);
        itinerary.closeDatabase();
        Log.w("Itinerary for pharmacy ID", itns.length + "");

        for (int i = 0; i < itns.length; i++) {
            String itineraryId = itns[i];

            invoice.openReadableDatabase();
            ArrayList<String> invoiceIds = invoice.getInvoiceIdByItineraryId(itineraryId);
            invoice.closeDatabase();
            Log.w("itinerary ids size", invoiceIds.size() + "");
            for (String invoiceId : invoiceIds) {
                invoice.openReadableDatabase();
                ArrayList<String> invoiceDetails = invoice.getInvoiceDetailsByInvoiceId(invoiceId);
                invoice.closeDatabase();
                Log.w("Invoice details size", invoiceDetails.size() + "");
                invoiceListData.add(convertArrayListToArray(invoiceDetails));
            }
        }
//		}
    }

    private void getDataFromPreviousActivity() {
        // TODO Auto-generated method stub
        Bundle extras = getIntent().getExtras();
        rowId = extras.getString("Id");
        pharmacyId = extras.getString("PharmacyId");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent customerItineraryListIntent = new Intent("com.halcyon.channelbridge.ITINERARYLIST");
            finish();
            startActivity(customerItineraryListIntent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private String[] convertArrayListToArray(ArrayList<String> listToConvert) {
        String[] convertedArray = new String[listToConvert.size()];

        for (int i = 0; i < listToConvert.size(); i++) {
            String temp = listToConvert.get(i);
            convertedArray[i] = temp;
        }

        return convertedArray;
    }

    private void setListAdapter(ArrayList<String[]> invoiceList) {
        Log.w("SIZE", invoiceList.size() + "");
        lastInvoiceListAdapter = new LastInvoiceListAdapter(this, invoiceListData);
        lViewInvoiceList.setAdapter(lastInvoiceListAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        super.onSaveInstanceState(outState);

        outState.putString("rowId", rowId);
        outState.putString("pharmacyId", pharmacyId);

    }

    private void setBundleData(Bundle bundlData) {

        rowId = bundlData.getString("rowId");
        pharmacyId = bundlData.getString("pharmacyId");


    }

}
