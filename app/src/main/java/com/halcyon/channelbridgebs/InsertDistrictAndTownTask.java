package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.District;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.Town;
import com.halcyon.channelbridgews.WebService;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InsertDistrictAndTownTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    District district;
    Town town;

    public InsertDistrictAndTownTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {


    }

    protected void onProgressUpdate(Void... progress) {
    }

    protected void onPostExecute(Void returnCode) {


    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub

        return null;
    }


}
