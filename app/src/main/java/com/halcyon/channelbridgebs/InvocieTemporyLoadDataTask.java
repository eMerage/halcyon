package com.halcyon.channelbridgebs;

import android.content.Context;
import android.os.AsyncTask;

import com.halcyon.Entity.Product;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.TemporaryInvoice;

import java.util.ArrayList;

/**
 * Created by Amila on 11/15/15.
 */
public class InvocieTemporyLoadDataTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private ProductRepStore productRepStoreController;
    private TemporaryInvoice temporaryInvoiceController;
    private ArrayList<Product> repStockList;

   public InvocieTemporyLoadDataTask(Context context){
        this.context = context;
        productRepStoreController = new ProductRepStore(context);
       temporaryInvoiceController = new TemporaryInvoice(context);
       repStockList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        productRepStoreController.openReadableDatabase();
        temporaryInvoiceController.openWritableDatabase();
        //temporaryInvoiceController.deleteAllRecords();
    }

    @Override
    protected Void doInBackground(Void... params) {
        repStockList = productRepStoreController.getAllRepAtoreDetails();

        for (Product repStock:repStockList){
            temporaryInvoiceController.insertTempInvoStock(repStock);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        productRepStoreController.closeDatabase();
        temporaryInvoiceController.closeDatabase();
    }
}
