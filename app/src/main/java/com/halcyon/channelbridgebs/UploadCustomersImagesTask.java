package com.halcyon.channelbridgebs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.ImageGallery;
import com.halcyon.channelbridgedb.ShelfQuantity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class UploadCustomersImagesTask extends
        AsyncTask<String, Integer, Integer> {

    private final Context context;

    public UploadCustomersImagesTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {


    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer returnCode) {
        if (returnCode == 2) {
            Toast notificationToast = Toast.makeText(context, "Customer images uploaded to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 1) {
            Toast notificationToast = Toast.makeText(context, "Unable to upload Customer Images to the server.", Toast.LENGTH_SHORT);
            notificationToast.show();
        } else if (returnCode == 3) {
            Toast notificationToast = Toast.makeText(context,
                    "Manual sync active.Auto sync disable.", Toast.LENGTH_SHORT);
            notificationToast.show();
        }


    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub

        int returnValue = 1;
        if (isNetworkAvailable() == true) {
            AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
            autoSyncOnOffFlag.openReadableDatabase();
            String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
            autoSyncOnOffFlag.closeDatabase();
            int id = Integer.parseInt(dbStatus);
            if (id == 0) {


                try {

                    Log.w("Log", "param result : " + params[0]);

                    Log.w("Log", "loadProductRepStoreData result : starting ");

                    publishProgress(1);

                    ImageGallery rtnProdObject = new ImageGallery(
                            context);
                    rtnProdObject.openReadableDatabase();

                    List<String[]> rtnProducts = rtnProdObject
                            .getImagesByStatus("false");
                    rtnProdObject.closeDatabase();

                    Log.w("Log", "rtnProducts sized :  " + rtnProducts.size());

                    if (rtnProducts.size() < 1) {
                        returnValue = 3;

                    } else {

                        for (String[] rtnProdData : rtnProducts) {

                            Log.w("Log", "SimpleFTP ???");

                            Log.w("Log", "rtnProducts id :  " + rtnProdData[0]);

                            FTPClient con = new FTPClient();
                            try {
                                con.connect(context
                                        .getString(R.string.ftp_host), Integer
                                        .parseInt(context
                                                .getString(R.string.ftp_port)));
                                if (con.login(context
                                                .getString(R.string.ftp_username),
                                        context
                                                .getString(R.string.ftp_password)
                                )) {

                                    con.enterLocalPassiveMode();
                                    con.setFileType(FTP.BINARY_FILE_TYPE);

                                    String str = Environment
                                            .getExternalStorageDirectory()
                                            + File.separator
                                            + "DCIM"
                                            + File.separator
                                            + "Channel_Bridge_Images"
                                            + File.separator
                                            + rtnProdData[3];

                                    FileInputStream srcFileStream = new FileInputStream(
                                            str);

                                    boolean status = con.storeFile(rtnProdData[3],
                                            srcFileStream);

                                    srcFileStream.close();

                                    if (status) {

                                        Log.w("Log", "Update the iternarary status");

                                        ShelfQuantity rtnProdObj = new ShelfQuantity(
                                                context);
                                        rtnProdObj.openReadableDatabase();
                                        rtnProdObj.setShelfQtyUploadedStatus(
                                                rtnProdData[0], "true");
                                        rtnProdObj.closeDatabase();

                                        returnValue = 2;

                                    }

                                    // con.stor(str);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                returnValue = 1;
                            }

                        }
                    }


                } catch (Exception e) {
                    Log.w("Log", "Upload customer images error: "
                            + e.toString());
                }
            } else
                returnValue = 3;
        }
        return returnValue;

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
