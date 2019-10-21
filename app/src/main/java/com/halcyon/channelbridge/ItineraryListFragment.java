package com.halcyon.channelbridge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.halcyon.channelbridge.ItineraryList.DetailsActivity;
import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.CustomersPendingApproval;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.Itinerary;


public class ItineraryListFragment extends ListFragment {

    boolean mDualPane;
    int mCurCheckPosition = 0;
    String mCurName = null;
    Cursor c, d;
    List<String> array = new ArrayList<String>();
    String error = "";
    // int rowid;
    String rowid;
    String rowIdString;

    HashMap<Integer, String> itineraryListId;
    //HashMap<Integer, Integer> itineraryListId;
    ArrayList<String> isActiveStatus = new ArrayList<String>();

    Invoice invoHandler;
    Itinerary itinerary;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);

            itineraryListId = new HashMap<Integer, String>();
            invoHandler = new Invoice(getActivity().getApplicationContext());
            itinerary = new Itinerary(getActivity());
            // itineraryListId = new HashMap<Integer,Integer>();

            List<String> itinararyNameList = getItineryNameList();


            // Populate list with our static array of titles.
            if (itinararyNameList.size() > 0) {

                setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, itinararyNameList));

                getListView().setSelector(R.color.itinerary_list_selector);


                // Check to see if we have a frame in which to embed the details
                // fragment directly in the containing UI.
                View detailsFrame = getActivity().findViewById(R.id.details);


                mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

                if (savedInstanceState != null) {
                    // Restore last state for checked position.
                    mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);

                }
                getListView().setItemChecked(getRowIdSelect(), true);
                if (mDualPane) {
                    // In dual-pane mode, the list view highlights the selected
                    // item.
                    getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    // Make sure our UI is in the correct state.
                    showDetails(itineraryListId.get(getRowIdSelect()), getRowIdSelect(),"0");
                }


            } else {


                AlertDialog aDialog = new AlertDialog.Builder(
                        this.getActivity()).create();

                aDialog.setButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = PreferenceManager
                                .getDefaultSharedPreferences(getActivity().getBaseContext());
                        boolean extraCustomerEnabled = sharedPreferences.getBoolean(
                                "cbPrefEnableAddExtraCustomer", true);

                        if (extraCustomerEnabled) {

                            getActivity().finish();
                            Intent extraCustomerIntent = new Intent(
                                    "com.halcyon.channelbridge.EXTRACUSTOMERACTIVITY");
                            startActivity(extraCustomerIntent);

                        }
                    }
                });

                aDialog.setTitle("Alert");
                aDialog.setMessage("No Itineraries for today");
                aDialog.show();

            }

        } catch (Exception e) {

            Log.e("Error", e.toString());
//            String error1 = e.toString();
//            AlertDialog aDialog = new AlertDialog.Builder(this.getActivity())
//                    .create();
//
//            aDialog.setButton("OK", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    return;
//                }
//            });
//
//            aDialog.setTitle("Error");
//            aDialog.setMessage(error1);
//            aDialog.show();

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);

        outState.putString(rowIdString, rowIdString);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if (position == 0) {
            showDetails(itineraryListId.get(position), position,"0");
        } else {
            String itineraryRowid = itineraryListId.get(position - 1);
            showDetails(itineraryListId.get(position), position, itineraryRowid);


         /*
            String invoDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            itinerary.openReadableDatabase();
            String pharmacyId =itinerary.getGlobalPharmaIDForRowID(itineraryRowid);
            itinerary.closeDatabase();
            String invoNo = invoHandler.getLastInvoiceForFivenDate(pharmacyId, invoDate);



            
            if(invoNo.equals("Invoice No.")  ||invoNo.equals("") ){
                Toast.makeText(getActivity(), "Invoice No ssds", Toast.LENGTH_SHORT).show();
            }else {
                showDetails(itineraryListId.get(position), position);
            }
            */

        }


    }



/*    void showDetails(int index, int id) {
        mCurCheckPosition = id;
        rowid = index;
        Log.w("list index", index + "");
        rowIdString = String.valueOf(index);
        Log.w("list rowid", mCurCheckPosition + "");

        if (mDualPane) {

            getListView().setItemChecked(id, true);


            ItineraryDetailsFragment details = (ItineraryDetailsFragment) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = ItineraryDetailsFragment.newInstance(index, rowIdString);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {


            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            Bundle bundle = new Bundle();
            // intent.putExtra("rowid", rowid);
            bundle.putString("rowid", rowIdString);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

  */

    void showDetails(String index, int id, String itineraryRowid) {
        mCurCheckPosition = id;
        rowid = index;
        Log.w("list index", index + "");
        rowIdString = String.valueOf(index);
        Log.w("list rowid", mCurCheckPosition + "");


        if (mDualPane) {

              //  getListView().setItemChecked(id, true);

		/*	for (int i = 0; i < getListView().getCount(); i ++) {
				String status = isActiveStatus.get(i);
				if (status.contentEquals("true")) {
					 long itemId = getListView().getItemIdAtPosition(i);
					getListView().findViewById(Integer.parseInt(String.valueOf(itemId))).setBackgroundColor(Color.GREEN);
				}
			}
*/


            // Check what fragment is currently shown, replace if needed.
            ItineraryDetailsFragment details = (ItineraryDetailsFragment) getFragmentManager().findFragmentById(R.id.details);


            if (details == null || details.getShownIndex() != Integer.parseInt(index)) {

                details = ItineraryDetailsFragment.newInstance(Integer.parseInt(index), rowIdString, itineraryRowid);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {

            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("indexindex", index);
            Bundle bundle = new Bundle();
            // intent.putExtra("rowid", rowid);
            bundle.putString("rowid", rowIdString);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public List<String> getItineryNameList() {

        Log.w("Log", " in getItineryNameList ");

        List<String> itnCustName = new ArrayList<String>();


        itinerary.openReadableDatabase();

        CustomersPendingApproval cpa = new CustomersPendingApproval(getActivity());
        cpa.openReadableDatabase();

        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));

        List<String[]> result = itinerary.getAllItinerariesForADay(currentDate);
        /* List<String[]> resultNewCustomer = cpa.getAllCustomersPendingApproval();*/
        itinerary.closeDatabase();

        int key = 0;
        for (String[] ItineraryData : result) {

            itnCustName.add(ItineraryData[6]);
            // itineraryListId.put(key, Integer.parseInt(ItineraryData[0]));
            itineraryListId.put(key, ItineraryData[0]);
            isActiveStatus.add(ItineraryData[12]);
            key++;
        }

        /*int countNewCustomer = key;
        for (String[] ItineraryNotApprData : resultNewCustomer) {

            itnCustName.add(ItineraryNotApprData[1]);
            itineraryListId.put(countNewCustomer, "A" + ItineraryNotApprData[0]);

            isActiveStatus.add(ItineraryNotApprData[12]);

            countNewCustomer++;
        }*/


        Log.w("Log", "itnCustName size: " + itnCustName.size());

        return itnCustName;

    }

    private int getRowIdSelect() {
        Itinerary itineraryObject = new Itinerary(getActivity());


        itineraryObject.openReadableDatabase();
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String currentDate = sdfDateTime.format(new Date(System.currentTimeMillis()));
        List<String[]> result = itineraryObject.getAllItinerariesForADay(currentDate);
        itineraryObject.closeDatabase();

        int rId = 0;
        for (int i = 0; i < result.size(); i++) {
            String[] temp = result.get(i);
            if (temp[9].contentEquals("true")) {
                rId = i;
            }
        }

        return rId;

    }

}
