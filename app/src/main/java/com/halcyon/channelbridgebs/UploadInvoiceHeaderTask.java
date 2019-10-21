package com.halcyon.channelbridgebs;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.halcyon.channelbridgedb.Invoice;
import com.halcyon.channelbridgews.WebService;

import java.util.List;

/**
 * Created by Amila on 12/2/15.
 */
  public class UploadInvoiceHeaderTask extends AsyncTask<Void,Void,Void> {

    Invoice invoiceObject;
    Context context;
    private String repId,deviceId,dealerId;
    private WebService webService;

    public UploadInvoiceHeaderTask(Context context,String repId,String deviceId,String dealerId){

        this.context = context;
        invoiceObject = new Invoice(context);
        this.repId = repId;
        this.deviceId = deviceId;
        webService =  new WebService();
        this.dealerId = dealerId;
    }

    @Override
    public Void doInBackground(Void... params) {
        invoiceObject.openReadableDatabase();
        List<String[]> invoice = invoiceObject.getInvoicesByStatus("false");
        invoiceObject.closeDatabase();
        Log.i("Called -->","web service");
        if(invoice.size()==0){
        }else {
            webService.uploadInvoiceHeader(deviceId, repId, dealerId, invoice);
        }
        return null;
    }
}
