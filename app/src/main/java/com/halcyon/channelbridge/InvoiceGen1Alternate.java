package com.halcyon.channelbridge;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;

import android.text.InputType;
import android.text.TextWatcher;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;


import com.halcyon.Entity.TempInvoiceStock;
import com.halcyon.channelbridgeaddapters.RecyclerListProductAdapter;
import com.halcyon.channelbridgeaddapters.RecyclerListProductAdapterMullBatch;
import com.halcyon.channelbridgeaddapters.SuggestionsAdapter;
import com.halcyon.channelbridgedb.DiscountStructures;
import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Sequence;
import com.halcyon.channelbridgedb.ShelfQuantity;
import com.halcyon.channelbridgedb.TemporaryInvoice;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by Amila on 11/12/15.
 */
public class InvoiceGen1Alternate extends Activity {


    private RecyclerView.LayoutManager mLayoutManager, mLayoutManagerMull;

    RelativeLayout layout;
    CheckBox freeIssue;
    TextView totalValue;
    Button btnAdd;

    AlertDialog.Builder alertCancel;

    TextView textCode, textDiscr, textBatch, textStock, textPrice, mullTextCode, mullTextDiscr, mullTextBatch, mullTextStock, mullTextPrice;
    EditText edtShelf, edtRequest, edtOrder, edtFree, edtDiscountProduct, mullEdtShelf, mullEdtRequest, mullEdtOrder, mullEdtFree, mullEdtDiscountProduct;


    String productCode = null, productBatch, productStock, rowID, rowId, pharmacyId, startTime = "",
            collectionDate = "", releaseDate = "", chequeNumber = "";
    int multipelBatch, itemIndex;
    String mullBatchCode, mullBatchDiscription, mullBatchBatch, mullBatchStock, mullBatchPrice, mullBatchShlef, mullBatchRequest,
            mullBatchOrder, mullBatchFree, mullBatchDiscount, mullBatchRowID, mullBatchType, mullBatchFreeAllowed, mullBatchDiscountAllowed;


    ArrayList<ReturnProduct> returnProductsArray;
    ArrayList<SelectedProduct> mergeList;
    ArrayAdapter<String> productDesAdapter;
    ArrayList<String> productDescList;
    ArrayList<TempInvoiceStock> prductListMull;


    private ArrayList<TempInvoiceStock> prductList;

    private RecyclerView recyclerView, recyclerViewMull;
    private RecyclerListProductAdapter adapter;
    private RecyclerListProductAdapterMullBatch adapterMull;


    private Products productController;
    TemporaryInvoice tempInvoiceStockController;
    DiscountStructures discoutStrac;

    boolean manualFreeEnable, chequeEnabled = false;
    private SuggestionsAdapter mSuggestionsAdapter;

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invoices_serach, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search ....");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.equals("") || query.isEmpty()) {
                    productTablefill(null);
                } else {
                    productTablefill(query);
                }

                return true;

            }

        });


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_gen_new);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        returnProductsArray = new ArrayList<ReturnProduct>();

        if (savedInstanceState != null) {
            getDataFromPreviousActivity(savedInstanceState);
        } else {
            getDataFromPreviousActivity(getIntent().getExtras());
        }

        productController = new Products(getApplicationContext());
        tempInvoiceStockController = new TemporaryInvoice(getApplicationContext());
        tempInvoiceStockController.openWritableDatabase();


        textCode = (TextView) findViewById(R.id.textViewCodeSingleProduct);
        textDiscr = (TextView) findViewById(R.id.textViewdescriptionSingleProduct);
        textBatch = (TextView) findViewById(R.id.textViewBatchSingleProduct);
        textStock = (TextView) findViewById(R.id.txtStockSingleProduct);
        textPrice = (TextView) findViewById(R.id.textViewPriceSingleProduct);

        btnAdd = (Button) findViewById(R.id.btnNextToPayment);

        edtShelf = (EditText) findViewById(R.id.editTextShelfSingleProduct);
        edtRequest = (EditText) findViewById(R.id.editTextRequestSingleProduct);
        edtOrder = (EditText) findViewById(R.id.editTextOrderSingleProduct);
        edtFree = (EditText) findViewById(R.id.editTextFreeSingleProduct);
        edtDiscountProduct = (EditText) findViewById(R.id.editTextDiscountSingleProduct);

        layout = (RelativeLayout) findViewById(R.id.relativeLayout99);
        freeIssue = (CheckBox) findViewById(R.id.checkBox_freeIssues);


        totalValue = (TextView) findViewById(R.id.textViewtot);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        prductList = new ArrayList<>();
        mergeList = new ArrayList<>();
        productDescList = new ArrayList<String>();
        prductListMull = new ArrayList<>();


        discoutStrac = new DiscountStructures(InvoiceGen1Alternate.this);


        productTablefill(null);


        if (manualFreeEnable == true) {
            freeIssue.setChecked(true);
            freeIssue.setEnabled(false);
        } else {
            freeIssue.setEnabled(true);
        }

        totalValue.setText("Total Value : " + String.valueOf(tempInvoiceStockController.getCurrentTotal()));


        freeIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (freeIssue.isChecked()) {
                    manualFreeEnable = true;
                } else {
                    manualFreeEnable = false;
                }
                layout.setVisibility(View.GONE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<TempInvoiceStock> selectedProductList = new ArrayList<>();
                selectedProductList = tempInvoiceStockController.getProductTempList();
                if (!selectedProductList.isEmpty()) {
                    Intent invoiceGen2Intent = new Intent(InvoiceGen1Alternate.this, InvoiceGen2Activity.class);
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
                    ArrayList<SelectedProduct> selectedProductsArray = new ArrayList<SelectedProduct>();

                    for (TempInvoiceStock stockData : selectedProductList) {
                        SelectedProduct product = new SelectedProduct();
                        product.setRowId(Integer.parseInt(stockData.getRow_ID()));
                        product.setProductId(stockData.getProductId());
                        product.setProductCode(stockData.getProductCode());
                        product.setProductBatch(stockData.getBatchCode());
                        product.setQuantity(stockData.getStock());
                        product.setExpiryDate(stockData.getExpiryDate());
                        product.setTimeStamp(stockData.getTimestamp());
                        product.setRequestedQuantity(Integer.parseInt(stockData.getRequestQuantity()));
                        product.setFree(Integer.parseInt(stockData.getFreeQuantity()));
                        product.setNormal(Integer.parseInt(stockData.getNormalQuantity()));
                        product.setFreeSystem(Integer.parseInt(stockData.getFreeQuantitySystem()));
                        product.setDiscount(stockData.getPercentage());
                        product.setShelfQuantity(Integer.parseInt(stockData.getShelfQuantity()));
                        product.setProductDescription(stockData.getProductDes());
                        product.setPrice(Double.parseDouble(stockData.getPrice()));
                        product.setRetailPrice(stockData.getRetailPrice());
                        product.setPrechesPrice(stockData.getPerchesPrice());

                        selectedProductsArray.add(product);

                    }

                    bundleToView.putParcelableArrayList("SelectedProducts", selectedProductsArray);
                    bundleToView.putParcelableArrayList("ReturnProducts", returnProductsArray);
                    invoiceGen2Intent.putExtras(bundleToView);
                    finish();
                    startActivity(invoiceGen2Intent);


                } else {
                    mergeList = tempInvoiceStockController.getShelfQuantityTempList();
                    if (mergeList.size() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceGen1Alternate.this);
                        builder.setTitle("Save");
                        builder.setMessage("You just only entered shelf quantity.Are you want to save");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new ShelfQuantityTask(InvoiceGen1Alternate.this).execute();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

            }
        });


        edtShelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (rowID == null || productCode == null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                        tempInvoiceStockController.updateShelfQuantity(String.valueOf(rowID), charSequence.toString());
                    } else {
                        tempInvoiceStockController.updateShelfQuantity(String.valueOf(rowID), "0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(null);
            }
        });


        edtRequest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (rowID == null || productCode == null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if (!charSequence.toString().isEmpty() || !charSequence.toString().equals("")) {
                        tempInvoiceStockController.updateRequestQuantity(String.valueOf(rowID), charSequence.toString());
                        edtOrder.setText(charSequence.toString());

                    } else {
                        edtOrder.setText("0");
                        tempInvoiceStockController.updateRequestQuantity(String.valueOf(rowID), charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if (rowID == null || productCode == null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((s.toString().isEmpty() || s.toString().equals(""))) {
                        tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), "0");

                    } else if (s.toString().equals("0")) {
                        tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), "0");

                    } else {

                        int freeIssuesQty = discoutStrac.getFreeIssues(productCode, Integer.parseInt(s.toString()));
                        int stock = Integer.parseInt(productStock);

                        int request = Integer.parseInt(s.toString());
                        int freeIssuesQtyforStok = discoutStrac.getFreeIssues(productCode, stock);
                        int maualfree;

                        if (edtFree.getText().toString().equals("")) {
                            maualfree = 0;
                        } else {
                            maualfree = Integer.parseInt(edtFree.getText().toString());
                        }

                        if (manualFreeEnable == true) {
                            if (stock < (maualfree + request)) {
                                tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), String.valueOf(stock - maualfree));
                                edtOrder.removeTextChangedListener(this);
                                edtOrder.setText(String.valueOf(stock - maualfree));
                                edtOrder.addTextChangedListener(this);
                                Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                                edtFree.setText(String.valueOf(maualfree));

                            } else {
                                tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), s.toString());
                                edtFree.setText(String.valueOf(freeIssuesQty));

                            }
                        } else {
                            if (stock < (freeIssuesQty + request)) {
                                tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), String.valueOf(stock - freeIssuesQtyforStok));
                                edtOrder.removeTextChangedListener(this);
                                edtOrder.setText(String.valueOf(stock - freeIssuesQtyforStok));
                                edtOrder.addTextChangedListener(this);
                                Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                                edtFree.setText(String.valueOf(freeIssuesQtyforStok));

                            } else {
                                tempInvoiceStockController.updateNormalQuantity(String.valueOf(rowID), s.toString());
                                edtFree.setText(String.valueOf(freeIssuesQty));

                            }
                        }

                    }

                }

            }


            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(null);
                totalValue.setText("Total Value : " + String.valueOf(tempInvoiceStockController.getCurrentTotal()));

            }
        });

        edtFree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {


                if (rowID == null || productCode == null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((!s.toString().isEmpty() || !s.toString().equals(""))) { // check whether that entered string is  empty

                        int stock = Integer.parseInt(textStock.getText().toString());
                        int request = 0;
                        if (!edtOrder.getText().toString().equals("")) {
                            request = Integer.parseInt(edtOrder.getText().toString());
                        }
                        int free = Integer.parseInt(edtFree.getText().toString());
                        if (stock - request >= free) { // check whether entered free quantity is smaller than stock -  requested
                            if (manualFreeEnable == true) {
                                tempInvoiceStockController.updateFreeSystemQuantity(String.valueOf(rowID), String.valueOf(tempInvoiceStockController.getFreeByBatchAndCode(productCode, productBatch)));
                                tempInvoiceStockController.updateFreeQuantity(String.valueOf(rowID), s.toString());
                            } else {
                                tempInvoiceStockController.updateFreeQuantity(String.valueOf(rowID), s.toString());
                            }


                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                            edtFree.removeTextChangedListener(this);
                            edtFree.setText("0");
                            edtFree.addTextChangedListener(this);
                            tempInvoiceStockController.updateFreeQuantity(String.valueOf(rowID), "0");
                        }

                        if ((free <= 0) && (manualFreeEnable == true)) {
                            tempInvoiceStockController.updateFreeAlloed(String.valueOf(rowID), Boolean.toString(false));
                            edtDiscountProduct.setEnabled(true);
                        } else {
                            tempInvoiceStockController.updateFreeAlloed(String.valueOf(rowID), Boolean.toString(true));
                            edtDiscountProduct.setEnabled(false);
                        }

                    } else {
                        tempInvoiceStockController.updateFreeSystemQuantity(String.valueOf(rowID), String.valueOf(tempInvoiceStockController.getFreeByBatchAndCode(productCode, productBatch)));
                        tempInvoiceStockController.updateFreeQuantity(String.valueOf(rowID), "0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(null);
            }
        });

        edtDiscountProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {


                if (rowID == null || productCode == null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((!s.toString().isEmpty() || !s.toString().equals(""))) {
                        if (s.length() < 4) {
                            if (Double.parseDouble(s.toString()) <= 100) {
                                tempInvoiceStockController.updateDicount(String.valueOf(rowID), s.toString());

                                double discount = Double.parseDouble(s.toString());

                                if ((discount <= 0.0) && (manualFreeEnable == true)) {
                                    edtFree.setEnabled(true);
                                } else {
                                    tempInvoiceStockController.updateFreeAlloed(String.valueOf(rowID), Boolean.toString(false));
                                    edtFree.setEnabled(false);
                                }

                            } else {
                                Toast.makeText(InvoiceGen1Alternate.this, "Enter valid discount", Toast.LENGTH_SHORT).show();
                                edtDiscountProduct.removeTextChangedListener(this);
                                edtDiscountProduct.setText("0.0");
                                edtDiscountProduct.addTextChangedListener(this);
                            }
                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Enter valid amount", Toast.LENGTH_SHORT).show();
                            edtDiscountProduct.removeTextChangedListener(this);
                            edtDiscountProduct.setText("0.0");
                            edtDiscountProduct.addTextChangedListener(this);
                        }

                    } else {
                        tempInvoiceStockController.updateDicount(String.valueOf(rowID), "0.0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefill(null);
            }
        });



    }




    public void productTablefill(String proDec) {
        mLayoutManager.scrollToPosition(itemIndex);
        try {
            prductList.clear();
            prductList = tempInvoiceStockController.getTempDataForTable(proDec, productCode);
            adapter = new RecyclerListProductAdapter(this, prductList);
            recyclerView.setAdapter(adapter);
        } catch (Exception ex) {
            adapter = new RecyclerListProductAdapter(this, prductList);
            recyclerView.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "No Products", Toast.LENGTH_LONG).show();
        }


    }

    public void lodeSelectedProducutCode(final String icode, String dis, final String batch, String productstock, String price, String shelf, String request, String order, String free, String discount, String rowid, String sType, String freeallowd, String discountallow, int mulBatch, int index) {

        multipelBatch = mulBatch;
        itemIndex = index;
        mLayoutManager.scrollToPosition(itemIndex);
        rowID = rowid;
        productCode = icode;
        productStock = productstock;
        productBatch = batch;

        textCode.setText(icode);
        textDiscr.setText(dis);
        textBatch.setText(batch);
        textStock.setText(productstock);
        textPrice.setText(price);


        if (manualFreeEnable == true) {
            edtFree.setEnabled(true);
        } else {
            edtFree.setEnabled(false);
        }

        if (mulBatch == 1) {
            edtShelf.setText(shelf);
            edtRequest.setText(request);
            edtOrder.setText(order);
            edtFree.setText(free);
            edtDiscountProduct.setText(discount);
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
            mullBatchDialog();
            productTablefill(null);
        }

    }


    private void getDataFromPreviousActivity(Bundle extras) {
        try {
            rowId = extras.getString("Id");
            pharmacyId = extras.getString("PharmacyId");
            startTime = extras.getString("startTime");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            chequeEnabled = preferences.getBoolean("cbPrefEnableCheckDetails", true);
            manualFreeEnable = extras.getBoolean("ManualFreeEnable");

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
            }
            if (extras.containsKey("ReturnProducts")) {
                returnProductsArray = extras.getParcelableArrayList("ReturnProducts");
            }
        } catch (Exception e) {

        }
    }

    public class ShelfQuantityTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private ProgressDialog dialog;

        public ShelfQuantityTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            this.dialog.setMessage("Saving shelf quantities");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            updateShelfQuantityDB(mergeList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.dialog.dismiss();
            finish();
        }
    }

    protected void updateShelfQuantityDB(ArrayList<SelectedProduct> shelfQuantityList) {
        ShelfQuantity shelfQuantity = new ShelfQuantity(this);
        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
        shelfQuantity.openWritableDatabase();
        Sequence sequence = new Sequence(this);

        sequence.openReadableDatabase();
        String lastInv = sequence.getLastRowId("invoice");
        sequence.closeDatabase();

        String invNum = String.valueOf(Integer.parseInt(lastInv) + 1);
        invNum = "NOT" + invNum;
        for (SelectedProduct shelfQuantityDetails : shelfQuantityList) {
            shelfQuantity.insertShelfQuantity(invNum, timeStamp,
                    pharmacyId, shelfQuantityDetails.getProductCode(),
                    shelfQuantityDetails.getProductBatch(),
                    String.valueOf(shelfQuantityDetails.getShelfQuantity()),
                    timeStamp, "false");
        }
        shelfQuantity.closeDatabase();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertCancel;
        alertCancel = new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Are you sure you want Cancel this Invoice?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent customerItineraryListIntent = new Intent("com.halcyon.channelbridge.ITINERARYLIST");

                                tempInvoiceStockController.deleteAllRecords();

                                finish();

                                startActivity(customerItineraryListIntent);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        alertCancel.show();

    }

    public void errorMessage(String titel, String msg) {
        alertCancel = new AlertDialog.Builder(this)
                .setTitle(titel)
                .setMessage(msg)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
        alertCancel.show();
    }


    public void mullBatchDialog() {
        final Dialog dialogBox = new Dialog(InvoiceGen1Alternate.this);
        dialogBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBox.setContentView(R.layout.dialog_invoice_multiple_batch);
        dialogBox.setCancelable(false);

        dialogBox.show();
        recyclerViewMull = (RecyclerView) dialogBox.findViewById(R.id.recycler_view_multipale);
        recyclerViewMull.setHasFixedSize(true);

        mLayoutManagerMull = new LinearLayoutManager(this);
        recyclerViewMull.setLayoutManager(mLayoutManagerMull);

        productTablefillForMultipelBatch();

        mullTextCode = (TextView) dialogBox.findViewById(R.id.textViewCodeMultipleProduct);
        mullTextDiscr = (TextView) dialogBox.findViewById(R.id.textViewdescriptionMultipleProduct);
        mullTextBatch = (TextView) dialogBox.findViewById(R.id.textViewBatchMultipleProduct);
        mullTextStock = (TextView) dialogBox.findViewById(R.id.txtStockMultipleProduct);
        mullTextPrice = (TextView) dialogBox.findViewById(R.id.textViewPriceMultipleProduct);

        mullEdtShelf = (EditText) dialogBox.findViewById(R.id.editTextShelfMultipleProduct);
        mullEdtRequest = (EditText) dialogBox.findViewById(R.id.editTextRequestMultipleProduct);
        mullEdtOrder = (EditText) dialogBox.findViewById(R.id.editTextOrderMultipleProduct);
        mullEdtFree = (EditText) dialogBox.findViewById(R.id.editTextFreeMultipleProduct);
        mullEdtDiscountProduct = (EditText) dialogBox.findViewById(R.id.editTextDiscountMultipleProduct);


        Button done = (Button) dialogBox.findViewById(R.id.button_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productTablefill(null);
                dialogBox.dismiss();
            }
        });


        oderDetailsEdittextOntextChangeForMulBatch();


    }


    public void productTablefillForMultipelBatch() {

        prductListMull.clear();
        prductListMull = tempInvoiceStockController.getTempDataForTableForMultipaleBatch(productCode);
        adapterMull = new RecyclerListProductAdapterMullBatch(InvoiceGen1Alternate.this, prductListMull);
        recyclerViewMull.setAdapter(adapterMull);


    }

    public void lodeSelectedProducutCodeMull(final String icode, String dis, final String batch, String productstock,
                                             String price, String shelf, String request, String order, String free,
                                             String discount, String rowid, String sType, String freeallowd,
                                             String discountallow) {


        // mLayoutManager.scrollToPosition(itemIndex);

        mullBatchCode = icode;
        mullBatchDiscription = dis;
        mullBatchBatch = batch;
        mullBatchStock = productstock;
        mullBatchPrice = price;
        mullBatchShlef = shelf;
        mullBatchRequest = request;
        mullBatchOrder = order;
        mullBatchFree = free;
        mullBatchDiscount = discount;
        mullBatchRowID = rowid;
        mullBatchType = sType;
        mullBatchFreeAllowed = freeallowd;
        mullBatchDiscountAllowed = discountallow;


        mullTextCode.setText(mullBatchCode);
        mullTextDiscr.setText(mullBatchDiscription);
        mullTextBatch.setText(mullBatchBatch);
        mullTextStock.setText(mullBatchStock);
        mullTextPrice.setText(mullBatchPrice);


        mullEdtShelf.setText(mullBatchShlef);
        mullEdtRequest.setText(mullBatchRequest);

        if (mullBatchOrder.equals("0")) {
            mullEdtOrder.setText("");
        } else {
            mullEdtOrder.setText(mullBatchOrder);
        }
        mullEdtFree.setText(mullBatchFree);
        mullEdtDiscountProduct.setText(mullBatchDiscount);


    }

    public void oderDetailsEdittextOntextChangeForMulBatch() {

        mullEdtShelf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if( mullBatchRowID== null || mullBatchCode== null) {
                    errorMessage("Warning", "Please select the Product");
                } else{
                    if ((!charSequence.toString().isEmpty() || !charSequence.toString().equals(""))) {
                        tempInvoiceStockController.updateShelfQuantity(mullBatchRowID, charSequence.toString());
                    } else {
                        tempInvoiceStockController.updateShelfQuantity(mullBatchRowID, "0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                productTablefillForMultipelBatch();
            }
        });

        mullEdtRequest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if( mullBatchRowID== null || mullBatchCode== null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if (!charSequence.toString().isEmpty() || !charSequence.toString().equals("")) {
                        tempInvoiceStockController.updateRequestQuantity(mullBatchRowID, charSequence.toString());
                        mullEdtOrder.setText(charSequence.toString());
                    } else {
                        mullEdtOrder.setText("0");
                        tempInvoiceStockController.updateRequestQuantity(mullBatchRowID, charSequence.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mullEdtOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if( mullBatchRowID== null || mullBatchCode== null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((s.toString().isEmpty() || s.toString().equals(""))) {
                        tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, "0");

                    } else if (s.toString().equals("0")) {
                        tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, "0");
                    } else {

                        int freeIssuesQty = discoutStrac.getFreeIssues(productCode, Integer.parseInt(s.toString()));
                        int stock = Integer.parseInt(mullBatchStock);

                        int request = Integer.parseInt(s.toString());
                        int freeIssuesQtyforStok = discoutStrac.getFreeIssues(productCode, stock);
                        int maualfree;

                        if (mullEdtFree.getText().toString().equals("")) {
                            maualfree = 0;
                        } else {
                            maualfree = Integer.parseInt(mullEdtFree.getText().toString());
                        }

                        if (manualFreeEnable == true) {
                            if (stock < (maualfree + request)) {
                                tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, String.valueOf(stock - maualfree));
                                mullEdtOrder.removeTextChangedListener(this);
                                mullEdtOrder.setText(String.valueOf(stock - maualfree));
                                mullEdtOrder.addTextChangedListener(this);
                                Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                                mullEdtFree.setText(String.valueOf(maualfree));

                            } else {
                                tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, s.toString());
                                mullEdtFree.setText(String.valueOf(freeIssuesQty));

                            }
                        } else {
                            if (stock < (freeIssuesQty + request)) {
                                tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, String.valueOf(stock - freeIssuesQtyforStok));
                                mullEdtOrder.removeTextChangedListener(this);
                                mullEdtOrder.setText(String.valueOf(stock - freeIssuesQtyforStok));
                                mullEdtOrder.addTextChangedListener(this);
                                Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                                mullEdtFree.setText(String.valueOf(freeIssuesQtyforStok));

                            } else {
                                tempInvoiceStockController.updateNormalQuantity(mullBatchRowID, s.toString());
                                mullEdtFree.setText(String.valueOf(freeIssuesQty));

                            }
                        }

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                productTablefillForMultipelBatch();
            }
        });


        mullEdtFree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( mullBatchRowID== null || mullBatchCode== null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((!s.toString().isEmpty() || !s.toString().equals(""))) { // check whether that entered string is  empty

                        int stock = Integer.parseInt(mullBatchStock);
                        int request = 0;
                        if (!mullEdtOrder.getText().toString().equals("")) {
                            request = Integer.parseInt(mullEdtOrder.getText().toString());
                        }
                        int free = Integer.parseInt(mullEdtFree.getText().toString());
                        if (stock - request >= free) { // check whether entered free quantity is smaller than stock -  requested
                            if (manualFreeEnable == true) {
                                tempInvoiceStockController.updateFreeSystemQuantity(mullBatchRowID, String.valueOf(tempInvoiceStockController.getFreeByBatchRowID(mullBatchRowID)));
                                tempInvoiceStockController.updateFreeQuantity(mullBatchRowID, s.toString());
                            } else {
                                tempInvoiceStockController.updateFreeQuantity(mullBatchRowID, s.toString());
                            }


                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Not enough quantity", Toast.LENGTH_SHORT).show();
                            mullEdtFree.removeTextChangedListener(this);
                            mullEdtFree.setText("0");
                            mullEdtFree.addTextChangedListener(this);
                            tempInvoiceStockController.updateFreeQuantity(mullBatchRowID, "0");
                        }

                        if ((free <= 0) && (manualFreeEnable == true)) {
                            tempInvoiceStockController.updateFreeAlloed(mullBatchRowID, Boolean.toString(false));
                            mullEdtDiscountProduct.setEnabled(true);
                        } else {
                            tempInvoiceStockController.updateFreeAlloed(mullBatchRowID, Boolean.toString(true));
                            mullEdtDiscountProduct.setEnabled(false);
                        }

                    } else {
                        tempInvoiceStockController.updateFreeSystemQuantity(mullBatchRowID, String.valueOf(tempInvoiceStockController.getFreeByBatchRowID(mullBatchRowID)));
                        tempInvoiceStockController.updateFreeQuantity(mullBatchRowID, "0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                productTablefillForMultipelBatch();
            }
        });


        mullEdtDiscountProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( mullBatchRowID== null || mullBatchCode== null) {
                    errorMessage("Warning", "Please select the Product");
                } else {
                    if ((!s.toString().isEmpty() || !s.toString().equals(""))) {
                        if (s.length() < 4) {
                            if (Double.parseDouble(s.toString()) <= 100) {
                                tempInvoiceStockController.updateDicount(mullBatchRowID, s.toString());

                                double discount = Double.parseDouble(s.toString());

                                if ((discount <= 0.0) && (manualFreeEnable == true)) {
                                    mullEdtFree.setEnabled(true);
                                } else {
                                    tempInvoiceStockController.updateFreeAlloed(mullBatchRowID, Boolean.toString(false));
                                    mullEdtFree.setEnabled(false);
                                }

                            } else {
                                Toast.makeText(InvoiceGen1Alternate.this, "Enter valid discount", Toast.LENGTH_SHORT).show();
                                mullEdtDiscountProduct.removeTextChangedListener(this);
                                mullEdtDiscountProduct.setText("0.0");
                                mullEdtDiscountProduct.addTextChangedListener(this);
                            }
                        } else {
                            Toast.makeText(InvoiceGen1Alternate.this, "Enter valid amount", Toast.LENGTH_SHORT).show();
                            mullEdtDiscountProduct.removeTextChangedListener(this);
                            mullEdtDiscountProduct.setText("0.0");
                            mullEdtDiscountProduct.addTextChangedListener(this);
                        }

                    } else {
                        tempInvoiceStockController.updateDicount(mullBatchRowID, "0.0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                productTablefillForMultipelBatch();
            }
        });


    }


}
