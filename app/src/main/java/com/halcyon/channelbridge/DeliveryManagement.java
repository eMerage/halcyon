package com.halcyon.channelbridge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.halcyon.Entity.DeliveryEntity;
import com.halcyon.channelbridgeaddapters.DeliveryAdapter;
import com.halcyon.channelbridgedb.Delivery;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.util.ArrayList;

public class DeliveryManagement extends Activity {



    RecyclerView recyclerInvoiceDeliveryStatus;

    ProgressBar progressBar;

    Delivery deliveryDB;
    DeliveryAdapter deliveryAdapter;


    ArrayList<DeliveryEntity> deliveryStatusInvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_management);

        recyclerInvoiceDeliveryStatus = (RecyclerView)findViewById(R.id.recyclerview_delivery);
        progressBar = findViewById(R.id.progressBar);

        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(this, 1);
        recyclerInvoiceDeliveryStatus.setLayoutManager(mLayoutManager);
        recyclerInvoiceDeliveryStatus.setItemAnimator(new DefaultItemAnimator());

        deliveryStatusInvList = new ArrayList<DeliveryEntity>();
        deliveryDB = new Delivery(this);

        if(isOnline()){
            progressBar.setVisibility(View.VISIBLE);
            new getDeliverySync(this).execute();

        }else {
            progressBar.setVisibility(View.GONE);
            deliveryStatusInvList =deliveryDB.getDelivery();
            deliveryAdapter = new DeliveryAdapter(this,deliveryStatusInvList);
            recyclerInvoiceDeliveryStatus.setAdapter(deliveryAdapter);
        }





    }

    public boolean isOnline() {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            flag = true;
        }
        return flag;
    }

    private class getDeliverySync extends AsyncTask<Void, Void, Void> {
        ArrayList<String[]> responseArr ;
        Context context;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");

        private getDeliverySync(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            responseArr = null;
            WebService webService = new WebService();
            try {
                responseArr = webService.getInvoiceDelivery(deviceId, repId);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


            for (int i = 0; i < responseArr.size(); i++) {
                System.out.println("bbbbb statssssssssss");

                String[] custDetails = responseArr.get(i);

                int satus=0;

                try {
                    satus = Integer.parseInt(custDetails[5]);
                }catch (NumberFormatException num){

                }
                deliveryDB.insertDelivery(custDetails[0],custDetails[1], custDetails[2], custDetails[3], custDetails[4],satus,custDetails[6]);
            }

            System.out.println("bbbbb endddddddd");
            progressBar.setVisibility(View.GONE);
            deliveryStatusInvList.clear();
            deliveryStatusInvList =deliveryDB.getDelivery();
            deliveryAdapter = new DeliveryAdapter(context,deliveryStatusInvList);
            recyclerInvoiceDeliveryStatus.setAdapter(deliveryAdapter);
            System.out.println("bbbbb adaptoe ed");

        }

    }


}
