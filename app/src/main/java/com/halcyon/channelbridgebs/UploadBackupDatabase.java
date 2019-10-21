package com.halcyon.channelbridgebs;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.halcyon.channelbridge.R;
import com.halcyon.channelbridgedb.AutoSyncOnOffFlag;

public class UploadBackupDatabase extends AsyncTask<Void, Void, Boolean> {
    private String dbName;
    private Context context;

    public UploadBackupDatabase(String name, Context context) {
        Log.e(">>>>>>>>>>>>>>>>>", "UploadBackupDatabase");
        dbName = name;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        FTPClient ftpClient = new FTPClient();
        AutoSyncOnOffFlag autoSyncOnOffFlag = new AutoSyncOnOffFlag(context);
        autoSyncOnOffFlag.openReadableDatabase();
        String dbStatus = autoSyncOnOffFlag.GetAutoSyncStatus();
        autoSyncOnOffFlag.closeDatabase();
        if (dbStatus == "0") {

            try {
                Log.e(">>>>>>>>>>>>>>>>>", "UploadBackupDatabase");
                ftpClient.connect(context.getString(R.string.ftp_host), Integer.parseInt(context.getString(R.string.ftp_port)));

                if (ftpClient.login(context.getString(R.string.ftp_username), context.getString(R.string.ftp_password))) {
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);


                    String str = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + dbName;

                    FileInputStream srcFileStream = new FileInputStream(str);

                    boolean status = ftpClient.storeFile(dbName, srcFileStream);

                    srcFileStream.close();
                    Log.e(">>>>>>>>>>>>>>>>>", "UploadBackupDatabase doInBackground :" + status);
                    return status;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            return false;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            Log.e(">>>>>>>>>>>>>>>>>", "UploadBackupDatabase onPostExecute :" + result);
            Toast.makeText(context, "Database uploaded successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
