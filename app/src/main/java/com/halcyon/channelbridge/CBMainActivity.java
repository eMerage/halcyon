package com.halcyon.channelbridge;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halcyon.Entity.CreditPeriod;
import com.halcyon.channelbridgebs.Download_Branch;
import com.halcyon.channelbridgebs.Download_Master_Banks;
import com.halcyon.channelbridgebs.Download_PaymentType;
import com.halcyon.channelbridgedb.Approval_Persons;
import com.halcyon.channelbridgedb.Branch;
import com.halcyon.channelbridgedb.Customers;
import com.halcyon.channelbridgedb.DEL_Outstandiing;
import com.halcyon.channelbridgedb.DatabaseHelper;
import com.halcyon.channelbridgedb.Delivery;
import com.halcyon.channelbridgedb.DiscountStructures;
import com.halcyon.channelbridgedb.InvoicePaymentType;
import com.halcyon.channelbridgedb.Itinerary;
import com.halcyon.channelbridgedb.Master_Banks;
import com.halcyon.channelbridgedb.ProductRepStore;
import com.halcyon.channelbridgedb.Products;
import com.halcyon.channelbridgedb.Reps;
import com.halcyon.channelbridgedb.Sequence;
import com.halcyon.channelbridgedb.Target;
import com.halcyon.channelbridgedb.UserLogin;
import com.halcyon.channelbridgews.WebService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CBMainActivity extends Activity implements LocationListener {


    /**
     * Called when the activity is first created.
     */


    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 001;

    ProgressDialog dialog;
    Thread wsCall;
    AlertDialog alertDialog;

    EditText txtDeviceId;
    EditText txtUserName;
    EditText txtPassword;
    private LocationManager locationManager;
    Location location;
    double lat, lng;
    String pwd = "", uName;

    WebService webService;

    String timeStamp;

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkdatabase()) {
            finish();
            Intent viewCustomers = new Intent(
                    "com.halcyon.channelbridge.LOGINACTIVITY");
            startActivity(viewCustomers);

        } else {

            setContentView(R.layout.initialize);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ReturnNumber", String.valueOf(0));
            editor.putString("UniqueBatchNumber", String.valueOf(1));
            editor.commit();
            initialize();

        }


    }

    public void initialize() {

        txtDeviceId = (EditText) findViewById(R.id.etDeviceId);
        txtUserName = (EditText) findViewById(R.id.etUserName);
        txtPassword = (EditText) findViewById(R.id.etPassword);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnAtchDb = (Button) findViewById(R.id.btnAtchDb);

        webService = new WebService();

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        getGPS();
        btnSubmit.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (isOnline()) {
                    if (txtDeviceId.getText().toString().length() > 0 && txtPassword.getText().toString().length() > 0 && txtUserName.getText().length() > 0) {

                        uName = txtUserName.getText().toString();
                        pwd = txtPassword.getText().toString();
                        timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date().getTime());


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
                            } else {
                                new DownloadFilesTask(CBMainActivity.this).execute(txtDeviceId.getText().toString());
                            }


                        }else {

                            new DownloadFilesTask(CBMainActivity.this).execute(txtDeviceId.getText().toString());

                        }


                    } else {
                        alertDialog.setMessage("Please fill all the required fields");
                        alertDialog.show();
                    }

                } else {
                    alertDialog
                            .setMessage("There is no Internet Connectivity, Please check network connectivity.");
                    alertDialog.show();
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                finish();
                System.exit(0);

            }
        });

        btnAtchDb.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                FileDialog fileDialog = new FileDialog(CBMainActivity.this, mPath);
                fileDialog.setFileEndsWith(".db");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(final File file) {
                        Log.d(getClass().getName(), "selected file " + file.toString());

                        if (file.getName().endsWith(".db")) {


                            if (file.getName().contains("DBVersion-" + DatabaseHelper.DATABASE_VERSION)) {

                                Builder alertCancel = new AlertDialog.Builder(CBMainActivity.this)
                                        .setTitle("Warning")
                                        .setMessage("Are you sure you want restore the database with the " + file.getName() + " file ?")
                                        .setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        restoreDB(file);

                                                    }
                                                })
                                        .setNegativeButton("No",
                                                new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog,
                                                                        int which) {
                                                        return;
                                                    }
                                                });
                                alertCancel.show();


                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(CBMainActivity.this).create();
                                alertDialog.setTitle("Alert");
                                alertDialog
                                        .setMessage("Selected file is version is not matched with the current database version, please contact the system administrator");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                alertDialog.show();

                            }


                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(CBMainActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog
                                    .setMessage("Selected file is not a Database file, Please check the file.");
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            alertDialog.show();
                        }

                    }
                });
                //fileDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                //  public void directorySelected(File directory) {
                //      Log.d(getClass().getName(), "selected dir " + directory.toString());
                //  }
                //});
                //fileDialog.setSelectDirectoryOption(false);
                fileDialog.showDialog();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new DownloadFilesTask(CBMainActivity.this).execute(txtDeviceId.getText().toString());
        }else {
            Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreDB(File file) {


        try {
            FileInputStream fis;

            fis = new FileInputStream(file);

            String outFileName = getBaseContext().getDatabasePath("channel_bridge_db").getAbsolutePath();

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();


            AlertDialog alertDialog = new AlertDialog.Builder(CBMainActivity.this).create();
            alertDialog.setTitle("Successfull");
            alertDialog
                    .setMessage("Database succesfully restored.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent loginActivity = new Intent(
                            "com.halcyon.channelbridge.LOGINACTIVITY");
                    startActivity(loginActivity);
                }
            });
            alertDialog.show();


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    boolean checkdatabase() {

        boolean flag = false;
        try {
            UserLogin userLogin = new UserLogin(CBMainActivity.this);
            userLogin.openReadableDatabase();

            List<String> users = userLogin.getAllUsers();

            if (users.size() > 0) {
                flag = true;
            }
            userLogin.closeDatabase();
            Log.w("Log", "checkdatabase result : " + flag);

        } catch (Exception e) {
            Log.w("Error", "error : " + e.toString());
        }

        return flag;

    }

    public ArrayList<String> loadRepData(final String deviceId) {

        ArrayList<String> response = null;

        while (response == null) {
            try {

                WebService webService = new WebService();
                response = webService.getRepForDevice(deviceId, getApplicationContext());

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
                response = new ArrayList<String>();
                response.add("No Connection");

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = new ArrayList<String>();
                response.add("No Connection");
            }
        }
        Log.w("Log", "loadRepData result : " + response.size());

        return response;
    }


    public ArrayList<String[]> loadOutStandData(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.Download_DEL_Outstanding(deviceId, repId);

                Thread.sleep(100);

            } catch (Exception e) {


            }
        }

        Log.w("Log", "load DEL_Sales result : " + responseArr.size());

        return responseArr;
    }

    public ArrayList<String[]> loadBranchData(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.Download_Branch(deviceId, repId);

                Thread.sleep(100);

            } catch (Exception e) {


            }
        }


        return responseArr;
    }

    public ArrayList<CreditPeriod> downloadCreditPeriods(String deviceId, String repId) {
        ArrayList<CreditPeriod> responseArr = null;

        try {

            WebService webService = new WebService();
            responseArr = webService.getCreditPeriods(deviceId, Integer.parseInt(repId));


        } catch (Exception e) {


        }


        return responseArr;
    }


    public ArrayList<String[]> loadCustomerData(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getCustomerListForRep(deviceId, repId);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadCustomerData result : " + responseArr.size());

        return responseArr;
    }

    public ArrayList<String[]> load_PaymentType(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;

        try {
            WebService webService = new WebService();
            responseArr = webService
                    .Download_Payment_Type(
                            deviceId,
                            repId);
            Thread.sleep(100);

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Log.w("Log", "loadProductRepStoreData result : " + responseArr.size());

        return responseArr;
    }

    public ArrayList<String[]> load_Master_Banks(String deviceId,
                                                 String repId) {
        ArrayList<String[]> responseArr = null;
        try {
            WebService webService = new WebService();
            responseArr = webService
                    .Download_Master_Banks(
                            deviceId,
                            repId);

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Log.w("Log", "loadProductRepStoreData result : " + responseArr.size());

        return responseArr;
    }


    public ArrayList<String[]> loadItineraryData(String repId, String deviceId, String maxRowId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getItineraryListForRep(repId, deviceId, maxRowId);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadItineraryData result : " + responseArr.size());

        return responseArr;
    }

    public ArrayList<String[]> loadProductData(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getProductListForRep(deviceId, repId);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadProductData result : " + responseArr.size());

        return responseArr;
    }

    public ArrayList<String[]> loadProductRepStoreData(String deviceId, String repId, int maxRowID) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getProductRepStoreList(deviceId, repId, maxRowID);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadProductRepStoreData result : " + responseArr.size());

        return responseArr;
    }


    public ArrayList<String[]> loadProductTarget(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getProductTarget(deviceId, repId);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadProductRepStoreData result : " + responseArr.size());

        return responseArr;
    }


    public ArrayList<String[]> loadInvoiceDeliveryStatus(String deviceId, String repId) {
        ArrayList<String[]> responseArr = null;
        while (responseArr == null) {
            try {

                WebService webService = new WebService();
                responseArr = webService.getInvoiceDelivery(deviceId, repId);

                Thread.sleep(100);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Log.w("Log", "loadProductRepStoreData result : " + responseArr.size());

        return responseArr;
    }





    private String saveOutStandData(ArrayList<String[]> custData) {

        String rtnStr = "";

        DEL_Outstandiing Outstandiing = new DEL_Outstandiing(CBMainActivity.this);
        Outstandiing.openWritableDatabase();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < custData.size(); i++) {
            String[] DEL_Sales = custData.get(i);

            Long result = Outstandiing.insertDEL_Out_Standiing(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3], DEL_Sales[4], DEL_Sales[5],
                    DEL_Sales[6], DEL_Sales[7], DEL_Sales[8], DEL_Sales[9], DEL_Sales[10], DEL_Sales[11], DEL_Sales[12],
                    DEL_Sales[13], DEL_Sales[14]
            );

            if (result == -1) {
                rtnStr = "error";
                break;
            }

            rtnStr = "success";
        }
        Outstandiing.closeDatabase();
        return rtnStr;
    }


    private String saveBranchData(ArrayList<String[]> custData) {

        String rtnStr = "";
        Branch PaymentType = new Branch(
                CBMainActivity.this);
        PaymentType.openWritableDatabase();
        PaymentType.Deletedata();

        for (int i = 0; i < custData.size(); i++) {
            String[] DEL_Sales = custData.get(i);

            Long result = PaymentType.insert_Branch(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3],
                    DEL_Sales[4], DEL_Sales[5]

            );


            if (result == -1) {
                rtnStr = "error";
                break;
            }

            rtnStr = "success";
        }
        PaymentType.closeDatabase();
        return rtnStr;
    }


    private String savePaymentType(ArrayList<String[]> PaymentType) {

        String rtnStr = "";

        InvoicePaymentType productRepStore = new InvoicePaymentType(CBMainActivity.this);


        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < PaymentType.size(); i++) {

            String[] DEL_Sales = PaymentType.get(i);

            boolean flag = false;
          /*  productRepStore.openReadableDatabase();
            flag = productRepStore.isBatchAvailable(custDetails[5], custDetails[2]);
            productRepStore.closeDatabase();*/


            if (!flag) {
                productRepStore.openWritableDatabase();
                Long result = productRepStore.insertInvoicePaymentType(DEL_Sales[0], DEL_Sales[1], DEL_Sales[2], DEL_Sales[3]
                );

                if (result == -1) {
                    rtnStr = "error";
                    productRepStore.closeDatabase();
                    break;
                }
                productRepStore.closeDatabase();

            }


            rtnStr = "success";
        }

        productRepStore.closeDatabase();

        return rtnStr;
    }


    private String saveMaster_Banks(ArrayList<String[]> PaymentType) {

        String rtnStr = "";

        Master_Banks productRepStore = new Master_Banks(CBMainActivity.this);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < PaymentType.size(); i++) {

            String[] DEL_Sales = PaymentType.get(i);

            boolean flag = false;
          /*  productRepStore.openReadableDatabase();
            flag = productRepStore.isBatchAvailable(custDetails[5], custDetails[2]);
            productRepStore.closeDatabase();*/


            if (!flag) {
                productRepStore.openWritableDatabase();
                Long result = productRepStore.insert_Master_Banks(DEL_Sales[0], DEL_Sales[1],
                        DEL_Sales[2], DEL_Sales[3]

                );

                if (result == -1) {
                    rtnStr = "error";
                    productRepStore.closeDatabase();
                    break;
                }
                productRepStore.closeDatabase();

            }


            rtnStr = "success";
        }

        productRepStore.closeDatabase();

        return rtnStr;
    }


    private String saveUserData(ArrayList<String> repData) {

        String rtnStr = "";

        Log.w("Log", "saveUserData : ");

        try {

            UserLogin userLogin = new UserLogin(CBMainActivity.this);
            userLogin.openWritableDatabase();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

            String userPassword = new Utility(CBMainActivity.this).encryptString(txtPassword.getText().toString());

            long rowId = userLogin.insertLogin(timeStamp, txtUserName.getText()
                    .toString(), "Rep", userPassword, "H", "N", txtDeviceId
                    .getText().toString());

            userLogin.closeDatabase();

            if (rowId != -1) {

                Reps reps = new Reps(CBMainActivity.this);
                reps.openWritableDatabase();

                Log.w("Log", "save rep Data : ");

                Long insRowId = reps.insertRep(repData.get(0), repData.get(2),
                        repData.get(3), repData.get(4), repData.get(5),
                        repData.get(6), repData.get(7), repData.get(1),
                        (int) rowId, timeStamp, repData.get(9), repData.get(10), repData.get(11), repData.get(18));

                reps.closeDatabase();

                if (insRowId != -1) {
                    rtnStr = "success";
                } else {
                    rtnStr = "error";
                }


            } else {
                rtnStr = "error";
            }

            Log.w("Log", "saveUserData : " + rtnStr);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

    }

    private String saveCustomerData(ArrayList<String[]> custData) {

        String rtnStr = "";

        Customers customers = new Customers(CBMainActivity.this);

        customers.openWritableDatabase();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < custData.size(); i++) {

            String[] custDetails = custData.get(i);

            Long result = customers.insertCustomer(
                    custDetails[0], // pharmacyId
                    custDetails[1], // pharmacyCode,
                    custDetails[2], // dealerId,
                    custDetails[3], // companyCode,
                    custDetails[4], // customerName,
                    custDetails[5], // address,
                    custDetails[7], // area,
                    custDetails[8], // town,
                    custDetails[6], // district,
                    custDetails[9], // telephone,
                    custDetails[10], // fax,
                    custDetails[11], // email,
                    custDetails[12], // customerStatus,
                    custDetails[13], // creditLimit,
                    custDetails[33], // currentCredit, //test
                    custDetails[14], // creditExpiryDate,
                    custDetails[15], // creditDuration,
                    custDetails[16], // vatNo,
                    custDetails[17], // status,
                    timeStamp, // timeStamp,
                    custDetails[28], // latitude,
                    custDetails[29], // longitude,
                    custDetails[20], // web,
                    custDetails[21], // brNo,
                    custDetails[22], // ownerContact,
                    custDetails[24], // ownerWifeBday,
                    custDetails[23], // pharmacyRegNo,
                    custDetails[25], // pharmacistName,
                    custDetails[26], // purchasingO
                    // fficer,
                    custDetails[27], // noStaff,
                    custDetails[19], // customerCode
                    custDetails[30],
                    custDetails[31],
                    android.util.Base64.decode(custDetails[32], Base64.DEFAULT),
                    custDetails[33],
                    custDetails[34],
                    custDetails[35]
            );

            if (result == -1) {
                rtnStr = "error";
                break;
            }

            rtnStr = "success";
        }

        customers.closeDatabase();

        return rtnStr;
    }

    private String saveItineraryData(ArrayList<String[]> itnData) {

        String rtnStr = "";

        Itinerary itinerary = new Itinerary(CBMainActivity.this);
        itinerary.openWritableDatabase();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < itnData.size(); i++) {

            String[] itnDetails = itnData.get(i);

            Long result = itinerary.insertItinerary(itnDetails[8], itnDetails[0],
                    itnDetails[1], itnDetails[2], itnDetails[3], itnDetails[4],
                    itnDetails[5], itnDetails[6], itnDetails[7], timeStamp, "false", "false", "false");

            if (result == -1) {
                rtnStr = "error";
                break;
            }

            rtnStr = "success";
        }

        itinerary.closeDatabase();

        return rtnStr;
    }

    private String saveProductData(ArrayList<String[]> prodData) {

        String rtnStr = "";

        Products products = new Products(CBMainActivity.this);

        products.openWritableDatabase();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < prodData.size(); i++) {

            String[] custDetails = prodData.get(i);

            Long result = products.insertProduct(custDetails[0].trim(),
                    custDetails[1], custDetails[2], custDetails[3],
                    custDetails[4], custDetails[5], custDetails[6],
                    custDetails[7], custDetails[8], custDetails[9],
                    custDetails[10], custDetails[11], custDetails[12],
                    custDetails[13], custDetails[14], custDetails[15],
                    custDetails[16], custDetails[17], timeStamp, "0"); //custDetails[0].trim()

            if (result == -1) {
                rtnStr = "error";
                break;
            }

            rtnStr = "success";
        }

        products.closeDatabase();


        return rtnStr;
    }

    private String saveProductTagget(ArrayList<String[]> prodData) {
        String rtnStr = "";
        Target targetDB = new Target(this);
        for (int i = 0; i < prodData.size(); i++) {
            String[] custDetails = prodData.get(i);

            int target;
            int achievement;

            try {
                target = Integer.parseInt(custDetails[2]);
            } catch (NumberFormatException numEx) {
                target = 0;
            }

            try {
                achievement = Integer.parseInt(custDetails[3]);
            } catch (NumberFormatException numEx) {
                achievement = 0;
            }
            targetDB.insertTarget(custDetails[0], custDetails[1], target, achievement, custDetails[4]);


        }

        return rtnStr;
    }





    private String saveInvoiceDeliveryStatus(ArrayList<String[]> invStatus) {
        String rtnStr = "";
        Delivery deliveryDB = new Delivery(this);


        for (int i = 0; i < invStatus.size(); i++) {
            String[] custDetails = invStatus.get(i);

            int satus=0;

            try {
                satus = Integer.parseInt(custDetails[5]);
            }catch (NumberFormatException num){

            }

            deliveryDB.insertDelivery(custDetails[0],custDetails[1], custDetails[2], custDetails[3], custDetails[4],satus,custDetails[6]);



        }

        return rtnStr;
    }

    private String saveProductRepStoreData(ArrayList<String[]> prodData) {

        String rtnStr = "";

        ProductRepStore productRepStore = new ProductRepStore(CBMainActivity.this);

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        for (int i = 0; i < prodData.size(); i++) {
            String[] custDetails = prodData.get(i);
            productRepStore.openWritableDatabase();
            Long result = productRepStore.insertProductRepStore(
                    custDetails[0], custDetails[2], custDetails[5],
                    custDetails[3], custDetails[4], custDetails[6], custDetails[7], custDetails[8], timeStamp);

            if (result == -1) {
                rtnStr = "error";
                productRepStore.closeDatabase();
                break;
            } else {

            }
            productRepStore.closeDatabase();


            rtnStr = "success";
        }

        productRepStore.closeDatabase();

        return rtnStr;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {

            case 1:

                builder.setMessage("Unable to find the entered Device Id in server")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                Dialog alert = builder.create();
                return alert;

            case 2:

                builder.setMessage("Unable to Save Data to tab")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                Dialog alertTwo = builder.create();
                return alertTwo;

            case 3:

                builder.setMessage("Device updated successfully, Please login")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        finish();
                                        Intent loginActivity = new Intent(
                                                "com.halcyon.channelbridge.LOGINACTIVITY");
                                        startActivity(loginActivity);
                                    }
                                });
                Dialog alertThree = builder.create();
                return alertThree;

            case 4:

                builder.setMessage("Unable to load Data")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                Dialog alertFour = builder.create();
                return alertFour;


            case 5:

                builder.setMessage("Cannot connect to the server")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                Dialog alertFive = builder.create();
                return alertFive;

            case 6:

                builder.setMessage("Invalid imei number for this user")
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                Dialog alertsix = builder.create();
                return alertsix;
        }

        return null;
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

    private class DownloadFilesTask extends AsyncTask<String, Integer, Integer> {

        private final Context context;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Fetching User Data from Server...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setProgress(0);

                dialog.setMax(100);
                dialog.show();
            } catch (Exception e) {
                dialog.dismiss();
            }


        }

        protected void onProgressUpdate(Integer... progress) {
            setProgress(progress[0]);
            switch (progress[0]) {
                case 1:
                    Log.w("Log", "yyyyyyyyyy: ");
                    dialog.setMessage("Saving User Data to Tab...");
                    break;
                case 2:
                    dialog.setMessage("Fetching Customer Data from Server...");
                    break;

                case 3:
                    dialog.setMessage("Saving Customer Data to Tab...");
                    break;
                case 4:
                    dialog.setMessage("Fetching Product Data from Server...");
                    break;
                case 5:
                    dialog.setMessage("Saving Product Data to Tab...");
                    break;
                case 6:
                    dialog.setMessage("Fetching Itinerary Data from Server...");
                    break;
                case 7:
                    dialog.setMessage("Saving Itinerary Data to Tab...");
                    break;
                case 8:
                    dialog.setMessage("Fetching Rep Store Data from Server...");
                    break;
                case 9:
                    dialog.setMessage("Saving Rep Store Data to Tab...");
                    break;
                case 12:
                    dialog.setMessage("Fetching  Oustanding  Data from Server...");
                    break;

                case 13:
                    dialog.setMessage("Saving Oustanding  Data to Tab...");
                    break;
                case 14:
                    dialog.setMessage("Fetching  Branch  Data from Server...");
                    break;

                case 15:
                    dialog.setMessage("Saving Branch  Data to Tab...");
                    break;
                case 16:
                    dialog.setMessage("Fetching Payment Type  Data from Server...");
                    break;

                case 17:
                    dialog.setMessage("Saving Payment Type  Data to Tab...");
                    break;
                case 20:
                    dialog.setMessage("Fetching Bank  Data from Server...");
                    break;

                case 21:
                    dialog.setMessage("Saving Bank Data to Tab...");
                    break;
                default:
                    break;
            }
        }

        protected void onPostExecute(Integer returnCode) {
            dialog.dismiss();
            showDialog(returnCode);

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            int returnValue = 1;

            Looper.prepare();

            Log.w("Log", "param result : " + params[0]);

            ArrayList<String> response = loadRepData(params[0]);

            Log.w("Log", "dddddddddddd result : " + response.get(0));
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(CBMainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return 6;
            }
            mngr.getDeviceId();
            String emi = mngr.getDeviceId();
            String emiedr;
            try {
                emiedr = response.get(17);
            } catch (Exception ex) {
                emiedr = "";
            }

            if(!emi.equals(emiedr)){
              returnValue =6;
            }else
            if (response.get(0) != "No Data" && response.get(0) != "No Connection") {

                publishProgress(1);
                String responseStr = saveUserData(response);
                if (responseStr.equals("success")) {

                    publishProgress(2);

                    ArrayList<String[]> approvedPersonList = null;
                    approvedPersonList = loadApprovedPerson(response.get(0));
                    saveApreovedPersons(approvedPersonList);

                    ArrayList<String[]> DiscountStructures = null;
                    DiscountStructures = loadDiscountStructures(response.get(0));
                    saveDiscountStructures(DiscountStructures);

                    String lastInvoice = null;
                    lastInvoice = loadLastInvoice(response.get(0));
                    if (lastInvoice == null) {

                    } else {
                        saveLastInvoice(lastInvoice);
                    }


                    String retrunlastInvoice = null;
                    retrunlastInvoice = loadReturnLastInvoice(response.get(0));
                    if (retrunlastInvoice == null) {
                    } else {
                        saveReturnLastInvoice(retrunlastInvoice);
                    }


                    ArrayList<String[]> custDataResponse = loadCustomerData(params[0], response.get(0));

                    if (custDataResponse.size() > 0) {

                        publishProgress(3);
                        webService.uploadUserCredentials(response.get(0), params[0], uName, pwd, lng, lat, timeStamp);

                        String custStr = saveCustomerData(custDataResponse);

                        if (custStr.equals("success")) {

                            publishProgress(4);

                            ArrayList<String[]> productDataResponse = loadProductData(params[0], response.get(0));
                            if (productDataResponse.size() > 0) {
                                publishProgress(5);
                                String productResult = saveProductData(productDataResponse);
                                if (productResult.equals("success")) {
                                    publishProgress(8);
                                    ArrayList<String[]> repStoreDataResponse = loadProductRepStoreData(params[0], response.get(0), 0);
                                    ArrayList<String[]> productTargetResponse = loadProductTarget(params[0], response.get(0));


                                    if (productTargetResponse.size() > 0) {
                                        String productTargetResult = saveProductTagget(productTargetResponse);
                                    } else {

                                    }

                                    ArrayList<String[]> InvoiceDeliveryStatus = loadInvoiceDeliveryStatus(params[0], response.get(0));

                                    if (InvoiceDeliveryStatus.size() > 0) {
                                        String invoiceDeliveryStatusResult = saveInvoiceDeliveryStatus(InvoiceDeliveryStatus);
                                    } else {

                                    }


                                    if (repStoreDataResponse.size() > 0) {
                                        publishProgress(9);

                                        String repStoreResult = saveProductRepStoreData(repStoreDataResponse);

                                        if (repStoreResult.equals("success")) {

                                            publishProgress(6);


                                            ArrayList<String[]> itineraryDataResponse = loadItineraryData(response.get(0), params[0], "0");
                                            if (itineraryDataResponse.size() > 0) {

                                                publishProgress(7);

                                                String itnResult = saveItineraryData(itineraryDataResponse);

                                                if (itnResult.equals("success")) {
                                                    publishProgress(16);
                                                    ArrayList<String[]> PaymentTypeResponse = load_PaymentType(params[0], response.get(0));
                                                    ArrayList<CreditPeriod> periodArrayList = downloadCreditPeriods(params[0], response.get(0));
                                                    com.halcyon.channelbridgedb.CreditPeriod periodController = new com.halcyon.channelbridgedb.CreditPeriod(context);
                                                    if (periodArrayList.size() > 0) {
                                                        for (CreditPeriod period : periodArrayList) {
                                                            periodController.addCreditPeriods(period);
                                                        }
                                                    }
                                                    CollectionNoteMaterDownload();
                                                    if (PaymentTypeResponse.size() > 0) {
                                                        publishProgress(17);
                                                        String PaymentTypeResulte = savePaymentType(PaymentTypeResponse);
                                                        if (PaymentTypeResulte.equals("success")) {
                                                            publishProgress(14);

                                                            ArrayList<String[]> BranchResponse = loadBranchData(params[0], response.get(0));
                                                            if (BranchResponse.size() > 0) {


                                                                publishProgress(15);
                                                                String BranchStr = saveBranchData(BranchResponse);
                                                                if (BranchStr.equals("success")) {
                                                                    publishProgress(20);
                                                                    ArrayList<String[]> MBResponse = load_Master_Banks(params[0], response.get(0));
                                                                    if (MBResponse.size() > 0) {
                                                                        publishProgress(21);
                                                                        String MBStr = saveMaster_Banks(MBResponse);
                                                                        if (MBStr.equals("success")) {
                                                                            publishProgress(12);
                                                                            ArrayList<String[]> outstandResponse = loadOutStandData(params[0], response.get(0));
                                                                            if (outstandResponse.size() > 0) {
                                                                                publishProgress(13);
                                                                                String outstandStr = saveOutStandData(outstandResponse);
                                                                                if (outstandStr.equals("success")) {
                                                                                    returnValue = 3;
                                                                                } else {
                                                                                    returnValue = 2;
                                                                                }


                                                                            } else {

                                                                                returnValue = 4;
                                                                            }
                                                                        } else {
                                                                            returnValue = 2;

                                                                        }

                                                                    } else {

                                                                        returnValue = 4;
                                                                    }


                                                                } else {
                                                                    returnValue = 2;
                                                                }
                                                            } else {

                                                                returnValue = 4;

                                                            }

                                                        } else {
                                                            returnValue = 2;
                                                        }

                                                    } else {
                                                        returnValue = 4;

                                                    }

                                                } else {
                                                    returnValue = 2;
                                                }

                                            } else {
                                                returnValue = 4;
                                            }

                                        } else {
                                            returnValue = 2;
                                        }
                                    } else {
                                        returnValue = 4;
                                    }

                                } else {
                                    returnValue = 2;
                                }

                            } else {
                                returnValue = 4;
                            }

                        } else {
                            returnValue = 2;
                        }

                    } else {
                        returnValue = 4;
                    }

                } else {
                    returnValue = 2;
                }

            } else if (response.get(0) == "No Connection") {
                returnValue = 5;
            } else {
                returnValue = 1;
            }
//

            return returnValue;
        }
    }

    private void CollectionNoteMaterDownload() {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        String deviceId = sharedPreferences.getString("DeviceId", "-1");
        String repId = sharedPreferences.getString("RepId", "-1");

        InvoicePaymentType PaymentType = new InvoicePaymentType(
                CBMainActivity.this);

        try {
            new Download_PaymentType(CBMainActivity.this).execute(deviceId, repId);
        } catch (Exception e) {

        }

        try {
            new Download_Master_Banks(CBMainActivity.this).execute(deviceId, repId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new Download_Branch(CBMainActivity.this).execute(deviceId, repId);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private class UploadUserCredintialTask extends AsyncTask<Void, Void, Void> {

        WebService webService;
        String deviceId = "";
        String repId = "";
        String timeStamp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());
            deviceId = sharedPreferences.getString("DeviceId", "-1");
            repId = sharedPreferences.getString("RepId", "-1");

        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;

        }
    }

    private void getGPS() {

        String GPS = "";

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 25, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {


            lat = (double) (location.getLatitude());
            lng = (double) (location.getLongitude());


        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //By Himanshu
    public ArrayList<String[]> loadDiscountStructures(String repId) {

        ArrayList<String[]> response = null;

        while (response == null) {
            try {

                WebService webService = new WebService();
                response = webService.getDiscountStructures(repId);

                Thread.sleep(100);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        Log.w("Log", "loadRepData result : " + response.size());

        return response;
    }

    private String saveDiscountStructures(ArrayList<String[]> discountStructuresData) {
        String rtnStr = "";
        try {
            DiscountStructures ds = new DiscountStructures(CBMainActivity.this);
            ds.openReadableDatabase();
            for (int i = 0; i < discountStructuresData.size(); i++) {
                String[] disDetails = discountStructuresData.get(i);
                ds.insertDiscountStructures(disDetails[0], disDetails[1], disDetails[2], disDetails[3], disDetails[4], disDetails[5], disDetails[6], disDetails[7], disDetails[8]);

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

    }

    public ArrayList<String[]> loadApprovedPerson(String repId) {

        ArrayList<String[]> response = null;

        while (response == null) {
            try {

                WebService webService = new WebService();
                response = webService.getApprvedList(repId);

                Thread.sleep(100);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        Log.w("Log", "loadRepData result : " + response.size());

        return response;
    }

    private String saveApreovedPersons(ArrayList<String[]> approvedPersonData) {

        String rtnStr = "";

        try {
            Approval_Persons approvedperson = new Approval_Persons(CBMainActivity.this);
            approvedperson.openWritableDatabase();
            for (int i = 0; i < approvedPersonData.size(); i++) {
                String[] personDetails = approvedPersonData.get(i);
                approvedperson.insertPersons(personDetails[0], personDetails[1], personDetails[2], personDetails[3], personDetails[4]);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

    }

    public String loadLastInvoice(String repId) {
        String response = null;
        while (response == null) {
            try {

                WebService webService = new WebService();
                response = webService.lastInvoiceNumber(repId);

                Thread.sleep(100);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }


        return response;
    }

    private String saveLastInvoice(String invoceNum) {
        String rtnStr = "";
        try {

            Sequence sequence = new Sequence(CBMainActivity.this);
            sequence.openReadableDatabase();

            if(invoceNum==null){

            }else {
                long result = sequence.insertSequence(invoceNum, "invoice");
            }




            sequence.closeDatabase();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

    }

    public String loadReturnLastInvoice(String repId) {
        String response = null;
        while (response == null) {
            try {

                WebService webService = new WebService();
                response = webService.lastRetuernInvoiceNumber(repId);

                Thread.sleep(100);

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }


        return response;
    }
    private String saveReturnLastInvoice(String retruninvoceNum) {
        String rtnStr = "";
        try {

            Sequence sequence = new Sequence(CBMainActivity.this);
            sequence.openReadableDatabase();

            if(retruninvoceNum==null){
            }else {
                long result = sequence.insertSequence(retruninvoceNum, "return_header");
            }


            sequence.closeDatabase();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rtnStr;

    }


}