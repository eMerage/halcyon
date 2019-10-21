package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.halcyon.Entity.ReturnHeaderEntity;
import com.halcyon.channelbridgedb.ReturnHeader;
import com.halcyon.channelbridgews.WebService;

import java.util.ArrayList;

/**
 * Created by Amila on 12/12/15.
 */
 public class UploadRetunHeaderTask extends AsyncTask<Void,Void,Void>{

    private String repId;
    private String deviceId;
    private Context context;
    private ReturnHeader returnHeaderController;
    private ArrayList<ReturnHeaderEntity> headerArrayList;
    WebService webService;
    public UploadRetunHeaderTask(Context context,String repId,String deviceId){
        this.repId = repId;
        this.deviceId = deviceId;
        this.context = context;
        returnHeaderController = new ReturnHeader(context);
        headerArrayList = new ArrayList<>();
        webService = new WebService();
    }


    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    protected void onPreExecute() {
        headerArrayList = returnHeaderController.getNotUploadedHeaders();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
     //   if (isNetworkAvailable() ==true){
        returnHeaderController.openWritableDatabase();


            for(ReturnHeaderEntity entity:headerArrayList){
               String response = webService.uploadReturnHeader(repId,deviceId,entity);

                String []resArray = response.split("-");
                Log.i("resp 0 -0->",resArray[0]);
                String status = resArray[0].toString();
               if(status.trim().equals("OK")){
                    Log.i("inside OK",resArray[1]);
                        returnHeaderController.updateStatus(resArray[1]);
                }

            }
        returnHeaderController.closeDatabase();
//
//        }else{
////            Toast t = Toast.makeText(context,"Internet connection not available ",Toast.LENGTH_SHORT);
////            t.setGravity(Gravity.CENTER,0,0);
////            t.show();
//        }
        return null;
    }
}
