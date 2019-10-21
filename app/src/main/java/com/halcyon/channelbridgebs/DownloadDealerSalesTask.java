package com.halcyon.channelbridgebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.halcyon.Entity.DealerSaleEntity;
import com.halcyon.channelbridgedb.DealerSales;
import com.halcyon.channelbridgews.WebService;

import java.util.ArrayList;

/**
 * Created by Amila on 12/21/15.
 */
 public class DownloadDealerSalesTask extends AsyncTask<Void, Void, Void> {

    Context context;
    DealerSales dealerSalesController;
    WebService webService;
    String deviceId;
    String repId;
    ArrayList<DealerSaleEntity> salesList;
    private ProgressDialog dialog;

    public  DownloadDealerSalesTask(Context context){
        this.context = context;
        dealerSalesController = new DealerSales(context);
        webService = new WebService();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        deviceId = sharedPreferences.getString("DeviceId", "-1");
        repId = sharedPreferences.getString("RepId", "-1");
        salesList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        this.dialog.setMessage("Downloading dealer sales");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
         this.dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        salesList =  webService.getDealerSalesFromServer(deviceId,repId);
       // salesList =  webService.getDealerSalesFromServer(deviceId,repId);

        for (DealerSaleEntity entity:salesList){
            dealerSalesController.insertDealerSales(entity);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
