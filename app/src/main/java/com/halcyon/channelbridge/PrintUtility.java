package com.halcyon.channelbridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.util.Base64;

public class PrintUtility extends Activity {

    final String ERROR_MESSAGE = "There has been an error in printing the bill.";
    BluetoothAdapter mBTAdapter;
    BluetoothSocket mBTSocket = null;
    Dialog dialogProgress;
    String BILL, TRANS_ID;
    String PRINTER_MAC_ID;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            try {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(PrintUtility.this,
                            device.getName() + " found", Toast.LENGTH_LONG)
                            .show();

                    System.out.println("***" + device.getName() + " : "
                            + device.getAddress());

                    if (device.getAddress().equalsIgnoreCase(PRINTER_MAC_ID)) {
                        mBTAdapter.cancelDiscovery();
                        dialogProgress.dismiss();
                        Toast.makeText(PrintUtility.this,
                                device.getName() + " Printing data",
                                Toast.LENGTH_LONG).show();
                        printBillToDevice(PRINTER_MAC_ID);

                    }
                }
            } catch (Exception e) {
                Log.e("Class  ", "My Exe ", e);
                // Toast.makeText(BluetoothPrint.this, ERROR_MESSAGE,
                // Toast.LENGTH_SHORT).show();

            }
        }
    };
    FileInputStream fin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        try {

//			 PRINTER_MAC_ID = "00:02:72:B1:53:53";

            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            PRINTER_MAC_ID = preferences.getString("etPrefEnterMac", "");

            if (PRINTER_MAC_ID != "") {

                String id = "";

                for (int i = 0; i < PRINTER_MAC_ID.length(); i++) {

                    if (i % 2 == 0 && i != 0) {
                        id = id + ":";
                    }
                    id = id + PRINTER_MAC_ID.charAt(i);

                }

                PRINTER_MAC_ID = id;

                Bundle extras = getIntent().getExtras();
                BILL = extras.getString("PrintData");

                mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                dialogProgress = new Dialog(PrintUtility.this);

                System.out.println("*** MAC ID :" + PRINTER_MAC_ID);
                System.out.println(BILL);


                try {
                    if (mBTAdapter.isDiscovering())
                        mBTAdapter.cancelDiscovery();
                    else
                        mBTAdapter.startDiscovery();
                } catch (Exception e) {
                    Log.e("Class ", "My Exe ", e);
                }
                System.out.println("BT Searching status :"
                        + mBTAdapter.isDiscovering());
                if (mBTAdapter == null) {
                    Toast.makeText(PrintUtility.this,
                            "Device has no bluetooth capability",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (!mBTAdapter.isEnabled()) {
                        Intent i = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(i, 0);
                    }

                    // Register the BroadcastReceiver
                    IntentFilter filter = new IntentFilter(
                            BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter); // Don't forget to
                    // unregister during
                    // onDestroy
                    dialogProgress.setTitle("Finding printer ");
                    dialogProgress
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                public void onDismiss(DialogInterface dialog) {
                                    dialog.dismiss();
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            });
                    dialogProgress.show();

                }

            } else {

                Toast.makeText(
                        PrintUtility.this,
                        "No MAC address saved, Please enter your printer MAC address in preferences.",
                        Toast.LENGTH_LONG).show();

                setResult(RESULT_CANCELED);
                finish();

            }

        } catch (Exception e) {
            Log.e("Class ", "My Exe ", e);
        }

    }

    public void printBillToDevice(final String address) {
        new Thread(new Runnable() {

            public void run() {
                runOnUiThread(new Runnable() {

                    public void run() {
                        dialogProgress.setTitle("Connecting...");
                        dialogProgress.show();
                    }

                });

                mBTAdapter.cancelDiscovery();



                try {
                    System.out.println("**************************#****connecting");
                    BluetoothDevice mdevice = mBTAdapter.getRemoteDevice(address);
                    Method m = mdevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    mBTSocket = (BluetoothSocket) m.invoke(mdevice, 1);

                    mBTSocket.connect();


                    OutputStream os = mBTSocket.getOutputStream();

                    System.out.println(BILL);




                    os.write(BILL.getBytes());

                    os.flush();

                    if (mBTAdapter != null) {
                        mBTAdapter.cancelDiscovery();
                    }
                    mBTSocket.close();
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    Log.e("Class ", "My Exe ", e);
                    // Toast.makeText(BluetoothPrint.this, ERROR_MESSAGE,
                    // Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    setResult(RESULT_CANCELED);
                    finish();

                }

                runOnUiThread(new Runnable() {

                    public void run() {
                        try {
                            dialogProgress.dismiss();
                        } catch (Exception e) {
                            Log.e("Class ", "My Exe ", e);
                        }
                    }

                });

            }

        }).start();
    }

    @Override
    protected void onDestroy() {
        Log.i("Dest ", "Checking Ddest");
        super.onDestroy();
        try {
            if (dialogProgress != null)
                dialogProgress.dismiss();
            if (mBTAdapter != null)
                mBTAdapter.cancelDiscovery();
            this.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e("Class ", "My Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBTAdapter != null)
                mBTAdapter.cancelDiscovery();
            this.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e("Class ", "My Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    private static String encodeFileToBase64Binary(File fileName) throws IOException {
        byte[] bytes = loadFile(fileName);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }
}
