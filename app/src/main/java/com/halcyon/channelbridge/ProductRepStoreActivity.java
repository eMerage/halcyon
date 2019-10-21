package com.halcyon.channelbridge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.ProductUnload;
import com.halcyon.channelbridgedb.Reps;

public class ProductRepStoreActivity extends Activity {

    Button btnPrint, btnDone;
    ListView lViewProductList;
    AutoCompleteTextView txtSearchProducts;
    ImageButton iBtnClearSearch;
    TextView tViewRepName;
    ProductRepStoreProductListAdapter productRepStoreProductListAdapter;
    String repName;
    ArrayList<String[]> productsWithDetails = new ArrayList<String[]>();
    int currentStockInt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_rep_store);

        tViewRepName = (TextView) findViewById(R.id.tvRepName);

        txtSearchProducts = (AutoCompleteTextView) findViewById(R.id.etSearchProducts);

        iBtnClearSearch = (ImageButton) findViewById(R.id.ibClearSearch);

        lViewProductList = (ListView) findViewById(R.id.lvProductList);

        btnPrint = (Button) findViewById(R.id.bPrint);
        btnDone = (Button) findViewById(R.id.bDone);

        setInitialData();
        setUpProductList();
        setSearchAdapter();

        lViewProductList.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                final String[] productDetails = productsWithDetails.get(arg2);

                final Dialog stockUnloadDialog = new Dialog(
                        ProductRepStoreActivity.this);
                stockUnloadDialog.setContentView(R.layout.unload_popup);
                stockUnloadDialog.setTitle(productDetails[5] + " Product Unload");

                final Button btnUnload = (Button) stockUnloadDialog
                        .findViewById(R.id.bUnload);
                final Button btnClose = (Button) stockUnloadDialog
                        .findViewById(R.id.bCloseUnload);
                final EditText txtUnloadQty = (EditText) stockUnloadDialog
                        .findViewById(R.id.etUnloadQuantity);
                final ListView lViewUnloadHistory = (ListView) stockUnloadDialog
                        .findViewById(R.id.lvUnloadHistory);

                ArrayList<String[]> unloadProdHistoryDetails = new ArrayList<String[]>();

                final String productCode = productDetails[12];
                final String batch = productDetails[13];
                final String expireDate = productDetails[15];

                Log.w("Log", "productCode : " + productCode);
                Log.w("Log", "batch : " + batch);

                final String currentStock = productDetails[14];
                ProductUnload stockUnload = new ProductUnload(getApplication());
                stockUnload.openReadableDatabase();
                unloadProdHistoryDetails = stockUnload
                        .getProdUnloadsByProdCodeAndBatch(productCode, batch);
                stockUnload.closeDatabase();

                Log.w("Log", "productCode : " + productCode);
                Log.w("Log", "batch : " + batch);
                Log.w("Log", "currentStock : " + currentStock);
                Log.w("Log", "unloadProdHistoryDetails : "
                        + unloadProdHistoryDetails.size());

                currentStockInt = Integer.parseInt(currentStock);


                btnClose.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        stockUnloadDialog.cancel();
                        setUpProductList();
                    }
                });

                btnUnload.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub


                        if (txtUnloadQty.getText().length() > 0) {

                            String unloadQtyStr = txtUnloadQty.getText().toString().trim();

                            int unloadQty = Integer.parseInt(unloadQtyStr);


                            if (unloadQty > 0) {
                                if (unloadQty <= currentStockInt) {


                                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                                    ProductUnload stockUnload = new ProductUnload(getApplication());
                                    stockUnload.openReadableDatabase();
                                    Long result = stockUnload.insertProdUnload(productCode, batch, expireDate, unloadQtyStr, "1", timeStamp);
                                    stockUnload.closeDatabase();


                                    ProductRepStore ProductRepStore = new ProductRepStore(getApplication());
                                    ProductRepStore.openReadableDatabase();
                                    Long resultTwo = ProductRepStore.updateRepStoreData(productDetails[16], unloadQty);
                                    ProductRepStore.closeDatabase();

                                    if (result != -1 && resultTwo != -1) {

                                        txtUnloadQty.setText("");

                                        Toast notification = Toast.makeText(ProductRepStoreActivity.this, "Product unload has been added, Please synchornise with the server", Toast.LENGTH_SHORT);
                                        notification.setGravity(Gravity.TOP, 100, 100);
                                        notification.show();


                                        ProductUnload prodUnload = new ProductUnload(getApplication());
                                        prodUnload.openReadableDatabase();
                                        ArrayList<String[]> unloadProdHistoryDetail = prodUnload.getProdUnloadsByProdCodeAndBatch(productCode, batch);
                                        prodUnload.closeDatabase();


                                        RepStoreUnloadListAdapter repStoreUnloadListAdapter = new RepStoreUnloadListAdapter(ProductRepStoreActivity.this, unloadProdHistoryDetail);
                                        lViewUnloadHistory.setAdapter(repStoreUnloadListAdapter);

                                        currentStockInt = currentStockInt - unloadQty;

                                        Log.w("Log", "currentStockInt ### : " + currentStockInt);

                                        setUpProductList();

                                    } else {

                                        Toast notification = Toast.makeText(ProductRepStoreActivity.this, "Cannot save the unload quantity, Please contact Administrator", Toast.LENGTH_SHORT);
                                        notification.setGravity(Gravity.TOP, 100, 100);
                                        notification.show();

                                    }

                                } else {

                                    Toast featureNotEnabled = Toast.makeText(ProductRepStoreActivity.this,
                                            "Unload Quantity should be lesser than current stock",
                                            Toast.LENGTH_SHORT);
                                    featureNotEnabled.setGravity(Gravity.TOP, 100, 100);
                                    featureNotEnabled.show();
                                }
                            } else {

                                Toast featureNotEnabled = Toast.makeText(
                                        ProductRepStoreActivity.this,
                                        "Unload Quantity should be greater than 0",
                                        Toast.LENGTH_SHORT);
                                featureNotEnabled.setGravity(Gravity.TOP, 100, 100);
                                featureNotEnabled.show();
                            }


                        } else {

                            Toast featureNotEnabled = Toast.makeText(
                                    ProductRepStoreActivity.this,
                                    "Please enter Unload Quantity",
                                    Toast.LENGTH_SHORT);
                            featureNotEnabled.setGravity(Gravity.TOP, 100, 100);
                            featureNotEnabled.show();
                        }

                    }
                });

                RepStoreUnloadListAdapter repStoreUnloadListAdapter = new RepStoreUnloadListAdapter(
                        ProductRepStoreActivity.this, unloadProdHistoryDetails);
                lViewUnloadHistory.setAdapter(repStoreUnloadListAdapter);
                stockUnloadDialog.show();

            }
        });

        txtSearchProducts.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String searchString = s.toString();
                if (!searchString.isEmpty()) {

                    ProductRepStore productRepStoreObject = new ProductRepStore(
                            ProductRepStoreActivity.this);
                    productRepStoreObject.openReadableDatabase();
                    productsWithDetails = productRepStoreObject
                            .SearchProductRepStoreWithDetails(searchString);
                    productRepStoreObject.closeDatabase();

                    productRepStoreProductListAdapter = new ProductRepStoreProductListAdapter(
                            ProductRepStoreActivity.this, productsWithDetails);


                    lViewProductList
                            .setAdapter(productRepStoreProductListAdapter);

                    lViewProductList.computeScroll();

                } else {

                    ProductRepStore productRepStoreObject = new ProductRepStore(
                            ProductRepStoreActivity.this);
                    productRepStoreObject.openReadableDatabase();
                    productsWithDetails = productRepStoreObject
                            .getAllProductRepStoreWithDetails();
                    productRepStoreObject.closeDatabase();

                    productRepStoreProductListAdapter = new ProductRepStoreProductListAdapter(
                            ProductRepStoreActivity.this, productsWithDetails);


                    lViewProductList
                            .setAdapter(productRepStoreProductListAdapter);

                    lViewProductList.computeScroll();
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

        btnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Date dateObj = new Date();

                String date = new SimpleDateFormat("yyyy-MM-dd")
                        .format(dateObj);
                String time = new SimpleDateFormat("hh:mm:ss a")
                        .format(dateObj);

                String printDateTime = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss a").format(new Date());
                String printData = "";
                printData = printData + "               STOCKLIST";
                printData = printData + "\n\n";
                printData = printData + "Sales Rep Name :" + "  " + repName
                        + "\n";
                printData = printData + "Date : " + "  " + date + "\n";
                printData = printData + "Time: " + "  " + time + "\n\n";

                printData = printData + "Current Stock\n\n";
                printData = printData
                        + "--------------------------------------------";
                printData = printData + "\n";

                for (String[] product : productsWithDetails) {

                    printData = printData + product[5] + " ";
                    printData = printData
                            + ("Batch:" + product[13] + "\n" + " Price: Rs "
                            + product[10] + " Qty: " + product[14]
                            + " Exp: " + product[15].substring(0, 10))
                            + "\n\n";

                }

                printData = printData
                        + "--------------------------------------------";
                printData = printData + "\n\n";

                printData = printData + "Print Date & Time : " + printDateTime
                        + "\n\n";

                printData = printData
                        + "Powered by Mobitel (Pvt) Ltd. - +94 (0) 712755777\n";

                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                Intent activityIntent = new Intent(getApplicationContext(),
                        PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);
            }
        });

        iBtnClearSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                txtSearchProducts.setText(null);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent itineraryListIntent = new Intent(
                        ProductRepStoreActivity.this, ItineraryList.class);
                startActivity(itineraryListIntent);
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent startItineraryList = new Intent(this, ItineraryList.class);
            finish();
            startActivity(startItineraryList);
        }
        return true;
    }

    private void setInitialData() {
        // TODO Auto-generated method stub

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String repId = sharedPreferences.getString("RepId", "-1");

        Reps repsObject = new Reps(this);
        repsObject.openReadableDatabase();
        repName = repsObject.getRepNameByRepId(repId);
        repsObject.closeDatabase();

        tViewRepName.setText(repName);
    }

    private void setSearchAdapter() {
        // TODO Auto-generated method stub
        ArrayList<String> productNames = new ArrayList<String>();
        ProductRepStore productRepStoreObject = new ProductRepStore(this);
        productRepStoreObject.openReadableDatabase();
        productNames = productRepStoreObject.getAllProductRepStoreNames();
        productRepStoreObject.closeDatabase();

        ArrayAdapter<String> productNameListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, productNames);
        ((AutoCompleteTextView) txtSearchProducts)
                .setAdapter(productNameListAdapter);

    }

    private void setUpProductList() {
        // TODO Auto-generated method stub
        ProductRepStore productRepStoreObject = new ProductRepStore(this);
        productRepStoreObject.openReadableDatabase();
        productsWithDetails = productRepStoreObject
                .getAllProductRepStoreWithDetails();
        productRepStoreObject.closeDatabase();

        productRepStoreProductListAdapter = new ProductRepStoreProductListAdapter(
                this, productsWithDetails);
        lViewProductList.setAdapter(productRepStoreProductListAdapter);

    }

}
