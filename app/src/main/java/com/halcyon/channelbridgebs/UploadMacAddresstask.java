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
 * Created by Amila on 12/30/15.
 */
public class UploadMacAddresstask extends AsyncTask<Void, Void, Void> {

        Context context;
        DealerSales dealerSalesController;
        WebService webService;
        String deviceId;
        String repId;
        String mac;
        ArrayList<DealerSaleEntity> salesList;
        private ProgressDialog dialog;

        public  UploadMacAddresstask(Context context,String mac){
            this.context = context;
            dealerSalesController = new DealerSales(context);
            webService = new WebService();
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(context);
            deviceId = sharedPreferences.getString("DeviceId", "-1");
            repId = sharedPreferences.getString("RepId", "-1");
           this.mac = mac;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new ProgressDialog(context);
//            this.dialog.setMessage("Uploading printer data...");
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//            this.dialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
             webService.uploadMacAddress(repId,deviceId,mac);
            // salesList =  webService.getDealerSalesFromServer(deviceId,repId);



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
        }

}
