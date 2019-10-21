package com.halcyon.channelbridgebs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;
import com.halcyon.channelbridgedb.Customers;

public class DownloadCustomerImagesTask extends AsyncTask<String, Integer, Integer> {

    Context context;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    public DownloadCustomerImagesTask(Context cntxt) {
        context = cntxt;
    }

    private FTPFile[] loadCustomerImages() {
        FTPClient connection = new FTPClient();

        String userName = context.getString(R.string.ftp_username);
        String password = context.getString(R.string.ftp_password);
        String directory = context.getString(R.string.ftp_directory);
        String host = context.getString(R.string.ftp_host);
        int port = Integer.parseInt(context.getString(R.string.ftp_port));

        Log.w("USER NAME", userName);

        FTPFile[] fileList = {};

        try {
            connection.connect(host, port);

            if (connection.login(userName, password)) {
                connection.enterLocalPassiveMode(); // important!
                connection.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                connection.changeWorkingDirectory(directory);
                fileList = connection.listFiles();

                Log.w("Names.length", "" + fileList.length);

            } else {

            }

        } catch (Exception e) {
            Log.w("Download images task", e.toString() + "couldnt get filenames");
        } finally {
            try {
                connection.logout();
                connection.disconnect();
            } catch (Exception e) {
                Log.w("Download Images task", e.toString() + "couldnt disconnect from ftp");
            }
        }
        return fileList;

    }

    private void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    private String downloadImages(FTPFile[] fileList) {
        FTPClient connection = new FTPClient();
        updateExternalStorageState();
        String status = "0";

        Customers customers = new Customers(context);
        customers.openReadableDatabase();
        ArrayList<String> recievedImageIds = customers.getAllImageIds();
        customers.closeDatabase();


        try {
            if (mExternalStorageAvailable && mExternalStorageWriteable) {
                if (checkGalleryDirectory()) {
                    File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images");
                    String fileNames[] = path.list();
                    String userName = context.getString(R.string.ftp_username);
                    String password = context.getString(R.string.ftp_password);
                    String directory = context.getString(R.string.ftp_directory);
                    String host = context.getString(R.string.ftp_host);
                    int port = Integer.parseInt(context.getString(R.string.ftp_port));
                    BufferedOutputStream fos = null;

                    connection.connect(host, port);

                    if (connection.login(userName, password)) {
                        connection.enterLocalPassiveMode(); // important!
                        connection.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                        connection.changeWorkingDirectory(directory);
                        for (FTPFile file : fileList) {
                            for (String recievedImageId : recievedImageIds) {
                                if (recievedImageId.substring(recievedImageId.indexOf(".") + 1, recievedImageId.length()).contentEquals("jpg")) {
                                    String name = file.getName();
                                    if (name.substring(name.indexOf(".") + 1, name.length()).contentEquals("jpg")) {
                                        try {
                                            boolean flagIsNotDownloaded = false;
                                            for (int i = 0; i < fileNames.length; i++) {
                                                if (name.contentEquals(fileNames[i])) {
                                                    flagIsNotDownloaded = true;
                                                    Log.w("Eka tiyenawa", "this file is already there :)");
                                                }
                                            }

                                            if (!flagIsNotDownloaded) {
                                                if (recievedImageId.contentEquals(name)) {
                                                    Log.w("Recieved Image id", recievedImageId);
                                                    Log.w("SUB string ", name.substring(name.indexOf(".") + 1, name.length()));
                                                    fos = new BufferedOutputStream(new FileOutputStream(path.toString() + File.separator + name));
                                                    boolean can = connection.retrieveFile(name, fos);
                                                    Log.w("FILE FOUND", String.valueOf(can));
                                                    fos.flush();
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.w("Unable to save", e.toString());
                                            status = "-1";
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (mExternalStorageAvailable && (!mExternalStorageWriteable)) {
                Toast.makeText(context, "The External Storage is not writable", Toast.LENGTH_SHORT).show();
            } else if (!mExternalStorageAvailable) {
                Toast.makeText(context, "External Storage not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w("Image Save from server problem", e.toString());
        } finally {
            try {
                connection.logout();
                connection.disconnect();
            } catch (Exception e) {
                Log.w("Download Images task", e.toString() + "couldnt disconnect from ftp");
            }
        }

        return status;
    }

    private boolean checkGalleryDirectory() {
        // TODO Auto-generated method stub
        boolean flag = false;
        try {
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + "Channel_Bridge_Images");
            if (path.exists()) {
                Log.w("CustomerImageGallery", "Path Already Exist");
                flag = true;
            } else {
                Log.w("CustomerImageGallery", "Path Does not Exist.. Creating Path");
                path.mkdirs();
                flag = true;
            }

        } catch (Exception e) {
            Log.w("CustomerImageGallery: Unable to make path...", e.toString());
            flag = false;
        }

        return flag;

    }

    @Override
    protected void onPostExecute(Integer result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);

        switch (values[0]) {

            case 1:
                //Toast.makeText(context, "Checking for new customer Images", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //Toast.makeText(context, "Loading new Images", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                //Toast.makeText(context, "Downloading customer Images", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                //Toast.makeText(context, "No images to be updated", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(context, "Unable to download images", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(context, "New customer Images download complete", Toast.LENGTH_SHORT).show();
                break;

            case 7:
                Toast.makeText(context, "Manual Sync running.Auto Sync disable.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected Integer doInBackground(String... arg0) {
        // TODO Auto-generated method stub

        AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
        autoSyncOnOffFlag.openReadableDatabase();
        String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
        autoSyncOnOffFlag.closeDatabase();
        if (dbStatus == "0") {

            try {

                publishProgress(1);
                FTPFile[] fileList = loadCustomerImages();
                if (fileList.length == 0) {
                    publishProgress(4);
                } else {
                    publishProgress(2);
                    publishProgress(3);
                    String status = downloadImages(fileList);
                    if (status.contentEquals("-1")) {
                        publishProgress(5);
                        return 0;
                    } else {
                        publishProgress(6);
                        return 0;
                    }
                }


            } catch (Exception e) {
                Log.w("Log", "Download cust images error: "
                        + e.toString());
            }
            return 0;
        } else
            return 7;


    }

}
