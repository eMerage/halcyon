package com.halcyon.channelbridge;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;

public class PriceListActivity extends Activity {

    ListView lViewPriceList;
    AutoCompleteTextView txtSearchProducts;
    ImageButton iBtnClearSearch;
    Button btnDone, btnExport;
    List<String[]> productList = new ArrayList<String[]>();
    PriceListAdapter priceListAdapter;
    ArrayList<String> productPrinciple = new ArrayList<String>();
    ArrayList<String> productNames = new ArrayList<String>();
    Dialog exportOptions;
    RadioGroup rGroupSearchOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_list);

        lViewPriceList = (ListView) findViewById(R.id.lvProductList);
        txtSearchProducts = (AutoCompleteTextView) findViewById(R.id.etSearchProducts);
        iBtnClearSearch = (ImageButton) findViewById(R.id.ibClearSearch);
        btnDone = (Button) findViewById(R.id.bDone);
        btnExport = (Button) findViewById(R.id.bExport);
        rGroupSearchOption = (RadioGroup) findViewById(R.id.rgSearchOption);

        exportOptions = new Dialog(PriceListActivity.this);
        exportOptions.setContentView(R.layout.export_options);
        exportOptions.setTitle("Select how you want to export:");
        ImageButton iBtnPdf = (ImageButton) exportOptions.findViewById(R.id.ibPdf);
        ImageButton iBtnXls = (ImageButton) exportOptions.findViewById(R.id.ibXls);
        ImageButton iBtnPrint = (ImageButton) exportOptions.findViewById(R.id.ibPrint);
        Button btnCancel = (Button) exportOptions.findViewById(R.id.bCancel);


        getAllProducts();
        getAllProductNames();
        getAllPrincipleNames();
        setProductListAdapter(this, productList);

        txtSearchProducts.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                int searchOption = rGroupSearchOption.getCheckedRadioButtonId();
                String searchString = s.toString();
                Products productsObject = new Products(PriceListActivity.this);
                List<String[]> products = new ArrayList<String[]>();

                if (!searchString.isEmpty()) {
                    switch (searchOption) {

                        case R.id.rbProduct:
                            setSearchAdapter(productNames);

                            products.clear();
                            productsObject.openReadableDatabase();
                            products = productsObject.searchProducts(searchString);
                            productsObject.closeDatabase();

                            setProductListAdapter(PriceListActivity.this, products);
                            break;

                        case R.id.rbPrinciple:
                            setSearchAdapter(productPrinciple);

                            products.clear();
                            productsObject.openReadableDatabase();
                            products = productsObject.searchPrinciple(searchString);
                            productsObject.closeDatabase();

                            setProductListAdapter(PriceListActivity.this, products);
                            break;
                    }
                } else {
                    setProductListAdapter(PriceListActivity.this, productList);
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

        iBtnClearSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                txtSearchProducts.setText(null);

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent itineraryIntent = new Intent(getApplication(), ItineraryList.class);
                finish();
                startActivity(itineraryIntent);

            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                exportOptions.show();

            }
        });

        iBtnPdf.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        iBtnXls.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });

        iBtnPrint.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date dateObj = new Date();

                String date = new SimpleDateFormat("yyyy-MM-dd").format(dateObj);
                String time = new SimpleDateFormat("hh:mm:ss a").format(dateObj);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                String repId = sharedPreferences.getString("RepId", "-1");

                Reps reps = new Reps(PriceListActivity.this);
                reps.openReadableDatabase();
                String repName = reps.getRepNameByRepId(repId);
                reps.closeDatabase();

                String printDateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date());

                String printData = "         Indoscan Private Limited.\n";
                printData = printData + "441/2A 2nd Ln, Sri Jayawardenepura Kotte\n";
                printData = printData + "       TP. +94 11 2886034\n";
                printData = printData + "  Athorized Distributor for Indoscan Private Limited.\n";
                printData = printData + "\n\n";
                printData = printData + "               Price List";
                printData = printData + "\n\n";
                printData = printData + "Sales Rep Name :" + "  " + repName + "\n";
                printData = printData + "Date : " + "  " + date + "\n";
                printData = printData + "Time: " + "  " + time + "\n\n";

                printData = printData + "Price List\n\n";
                printData = printData + "--------------------------------------------";
                printData = printData + "\n";

                for (String[] product : productList) {
                    printData = printData + product[8] + " ";
                    printData = printData + ("Code: " + product[2] + "\n" + " Principle:" + product[11] + "\n" + " Wholesale: Rs " + product[12] + " Ret: Rs " + product[14]) + "\n\n";

                }


                printData = printData + "--------------------------------------------";
                printData = printData + "\n\n";


                printData = printData + "Print Date & Time : " + printDateTime + "\n\n";

                printData = printData + "   Powered by Mobitel (Pvt) Ltd..\n";
                printData = printData + "             Tel: +94 (0) 712755777\n";


                Bundle bundleToView = new Bundle();
                bundleToView.putString("PrintData", printData);

                Intent activityIntent = new Intent(
                        getApplicationContext(), PrintUtility.class);
                activityIntent.putExtras(bundleToView);
                startActivityForResult(activityIntent, 0);
                exportOptions.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                exportOptions.dismiss();

            }
        });

    }

    private void setProductListAdapter(Activity priceListActivity, List<String[]> products) {
        // TODO Auto-generated method stub
        priceListAdapter = new PriceListAdapter(priceListActivity, products);
        lViewPriceList.setAdapter(priceListAdapter);
    }

    private void getAllProducts() {
        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        productList = productsObject.getAllProducts();
        productsObject.closeDatabase();
    }

    private void getAllProductNames() {
        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        String[] names = productsObject.getProductNames();
        productsObject.closeDatabase();

        for (int i = 0; i < names.length; i++) {
            productNames.add(names[i]);
        }
    }

    private void getAllPrincipleNames() {
        Products productsObject = new Products(this);
        productsObject.openReadableDatabase();
        productPrinciple = productsObject.getAllProductPrinciple();
        productsObject.closeDatabase();
    }

    private void setSearchAdapter(ArrayList<String> list) {
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, list);
        ((AutoCompleteTextView) txtSearchProducts).setAdapter(searchAdapter);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent itineraryIntent = new Intent(getApplication(), ItineraryList.class);
            finish();
            startActivity(itineraryIntent);
        }
        return super.onKeyDown(keyCode, event);
    }


}
