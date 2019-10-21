package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import android.util.Log;

public class CustomersPendingApproval {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AREA = "area";
    private static final String KEY_TOWN = "town";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_FAX = "fax";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_WEB = "web";
    private static final String KEY_BR_NO = "br_no";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_OWNER_CONTACT = "owner_contact";
    private static final String KEY_CUSTOMER_STATUS = "customer_status";
    private static final String KEY_OWNER_WIFE_BDAY = "owner_wife_bday";
    private static final String KEY_PHARMACY_REG_NO = "pharmacy_reg_no";
    private static final String KEY_PHARMACIST_NAME = "pharmacist_name";
    private static final String KEY_PURCHASING_OFFICER = "purchasing_officer";
    private static final String KEY_NO_OF_STAFF = "no_of_staff";
    private static final String KEY_UPLOAD_STATUS = "upload_status";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_GLB_PHARMACY_CODE = "glb_pharmacy_code";
    private static final String KEY_CUS_IMAGE = "cus_Image";
    private static final String KEY_IMAGE_BLOB = "image_blob";
    private static final String KEY_CREDIT_LIMIT = "credit_limit";


    String[] columns = {KEY_ROW_ID, KEY_CUSTOMER_NAME, KEY_ADDRESS, KEY_AREA, KEY_TOWN, KEY_DISTRICT,
            KEY_TELEPHONE, KEY_FAX, KEY_EMAIL, KEY_WEB, KEY_CUSTOMER_STATUS, KEY_BR_NO, KEY_IS_ACTIVE, KEY_OWNER_CONTACT,
            KEY_OWNER_WIFE_BDAY, KEY_PHARMACY_REG_NO, KEY_PHARMACIST_NAME, KEY_PURCHASING_OFFICER,
            KEY_NO_OF_STAFF, KEY_UPLOAD_STATUS, KEY_LATITUDE, KEY_LONGITUDE, KEY_GLB_PHARMACY_CODE, KEY_CUS_IMAGE, KEY_IMAGE_BLOB, KEY_CREDIT_LIMIT};

    private static final String TABLE_NAME = "customers_pending_approval";
    private static final String CUSTOMERS_PENDING_APPROVAL_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " TEXT PRIMARY KEY, "
            + KEY_CUSTOMER_NAME + " TEXT, "
            + KEY_ADDRESS + " TEXT, "
            + KEY_AREA + " TEXT, "
            + KEY_TOWN + " TEXT, "
            + KEY_DISTRICT + " TEXT, "
            + KEY_TELEPHONE + " TEXT, "
            + KEY_FAX + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_WEB + " TEXT, "
            + KEY_CUSTOMER_STATUS + " TEXT, "// is customer status == isActive
            + KEY_BR_NO + " TEXT, "
            + KEY_IS_ACTIVE + " TEXT, "
            + KEY_OWNER_CONTACT + " TEXT, "
            + KEY_OWNER_WIFE_BDAY + " NUMERIC, "
            + KEY_PHARMACY_REG_NO + " TEXT, "
            + KEY_PHARMACIST_NAME + " TEXT, "
            + KEY_PURCHASING_OFFICER + " TEXT, "
            + KEY_NO_OF_STAFF + " INTEGER, "
            + KEY_UPLOAD_STATUS + " TEXT, "
            + KEY_LATITUDE + " TEXT, "
            + KEY_LONGITUDE + " TEXT, "
            + KEY_GLB_PHARMACY_CODE + " TEXT, "
            + KEY_CUS_IMAGE + " TEXT, "
            + KEY_IMAGE_BLOB + " BLOB, "
            + KEY_CREDIT_LIMIT + " TEXT " + " );";


    public final Context customersPendingApprovalContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CustomersPendingApproval(Context c) {
        customersPendingApprovalContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CUSTOMERS_PENDING_APPROVAL_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CustomersPendingApproval openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customersPendingApprovalContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CustomersPendingApproval openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customersPendingApprovalContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertCustomer(String rowid, String customerName, String address, String area, String town, String district, String telephone,
                               String fax, String email, String web, String customerStatus, String brNo, String isActive, String ownerContact, String ownerWifeBday, String pharmacyRegNo, String pharmacistName, String purchasingOfficer,
                               String noStaff, String latitude, String longitude, String pharmacyId, byte[] data) throws SQLException {


        ContentValues cv = new ContentValues();
        cv.put(KEY_ROW_ID, rowid);
        cv.put(KEY_CUSTOMER_NAME, customerName);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_AREA, area);
        cv.put(KEY_TOWN, town);
        cv.put(KEY_DISTRICT, district);
        cv.put(KEY_TELEPHONE, telephone);
        cv.put(KEY_FAX, fax);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_WEB, web);
        cv.put(KEY_CUSTOMER_STATUS, customerStatus);
        cv.put(KEY_BR_NO, brNo);
        cv.put(KEY_IS_ACTIVE, isActive);
        cv.put(KEY_OWNER_CONTACT, ownerContact);
        cv.put(KEY_OWNER_WIFE_BDAY, ownerWifeBday);
        cv.put(KEY_PHARMACY_REG_NO, pharmacyRegNo);
        cv.put(KEY_PHARMACIST_NAME, pharmacistName);
        cv.put(KEY_PURCHASING_OFFICER, purchasingOfficer);
        cv.put(KEY_NO_OF_STAFF, noStaff);
        cv.put(KEY_UPLOAD_STATUS, "false");
        cv.put(KEY_LATITUDE, latitude);
        cv.put(KEY_LONGITUDE, longitude);
        cv.put(KEY_GLB_PHARMACY_CODE, pharmacyId);
        cv.put(KEY_CUS_IMAGE, "NO");//

        cv.put(KEY_IMAGE_BLOB, data);//
        cv.put(KEY_CREDIT_LIMIT, 10000);//

        return database.insert(TABLE_NAME, null, cv);

    }

    //edit by himanshu

    public List<String[]> getCustomersByUploadStatus(String status) {
        List<String[]> invoice = new ArrayList<String[]>();


//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOAD_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[25];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);
            invoiceData[7] = cursor.getString(7);
            invoiceData[8] = cursor.getString(8);
            invoiceData[9] = cursor.getString(9);
            invoiceData[10] = cursor.getString(10);
            invoiceData[11] = cursor.getString(11);
            invoiceData[12] = cursor.getString(12);
            invoiceData[13] = cursor.getString(13);
            invoiceData[14] = cursor.getString(14);
            invoiceData[15] = cursor.getString(15);
            invoiceData[16] = cursor.getString(16);
            invoiceData[17] = cursor.getString(17);
            invoiceData[18] = cursor.getString(18);
            invoiceData[19] = cursor.getString(19);
            invoiceData[20] = cursor.getString(20);
            invoiceData[21] = cursor.getString(21);
            invoiceData[22] = cursor.getString(22);
            invoiceData[23] = cursor.getString(23);
            byte[] bb = cursor.getBlob(24);
            invoiceData[24] = ConvertByteArryTobase64String(bb);

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }

    public String ConvertByteArryTobase64String(byte[] data1) {
        String strFile;
        byte[] data = data1;//Convert any file, image or video into byte array
        strFile = Base64.encodeToString(data1, Base64.NO_WRAP);//Convert byte array into string
        return strFile;

    }

    public void setCustomerUploadedStatus(String custId, String status) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_UPLOAD_STATUS, "true");

        database.update(TABLE_NAME, cv, KEY_ROW_ID + "=?", new String[]{custId});

    }


    public String[] getCustomerDetailsByPharmacyId(String pharmacyId) {
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_GLB_PHARMACY_CODE + "=?", new String[]{pharmacyId}, null, null, null);
        cursor.moveToFirst();

        Log.w("cursor size", cursor.getCount() + "");
        Log.w("Pharmacy Id recieved", pharmacyId + "");
        String[] customerDetails = new String[23];
        customerDetails[0] = cursor.getString(0);
        customerDetails[1] = cursor.getString(1);
        customerDetails[2] = cursor.getString(2);
        customerDetails[3] = cursor.getString(3);
        customerDetails[4] = cursor.getString(4);
        customerDetails[5] = cursor.getString(5);
        customerDetails[6] = cursor.getString(6);
        customerDetails[7] = cursor.getString(7);
        customerDetails[8] = cursor.getString(8);
        customerDetails[9] = cursor.getString(9);
        customerDetails[10] = cursor.getString(10);
        customerDetails[11] = cursor.getString(11);
        customerDetails[12] = cursor.getString(12);
        customerDetails[13] = cursor.getString(13);
        customerDetails[14] = cursor.getString(14);
        customerDetails[15] = cursor.getString(15);
        customerDetails[16] = cursor.getString(16);
        customerDetails[17] = cursor.getString(17);
        customerDetails[18] = cursor.getString(18);
        customerDetails[19] = cursor.getString(19);
        customerDetails[20] = cursor.getString(20);
        customerDetails[21] = cursor.getString(21);
        customerDetails[22] = cursor.getString(22);

        cursor.close();
        return customerDetails;

    }


    public byte[] getByteArrayImage(String selectedItem1) {
        byte[] byteArray = new byte[0];
        try {
            String strqu = "select " + KEY_IMAGE_BLOB + " from " + TABLE_NAME + " where " + KEY_ROW_ID + " ='" + selectedItem1 + "' ";

            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {
                    //value=Double.parseDouble(String.valueOf(cur.getInt(0)));
                    // value =value+value_sub;
                    byteArray = cur.getBlob(0);
                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {


        }


        return byteArray;


    }

    public String getPendingCustomerByPharmacyId(String PharmacyID) {

        Cursor cur = database.rawQuery("SELECT customer_name FROM customers_pending_approval where glb_pharmacy_code ='" + PharmacyID + "' ", null);
        cur.moveToFirst();
        String penCus = cur.getString(0);
        cur.close();
        return penCus;


    }


    public String getCustomerPhoneByPharmacyId(String PharmacyID) {

        Cursor cur = database.rawQuery("SELECT telephone FROM customers_pending_approval where row_id ='" + PharmacyID + "' ", null);
        String result = "0";
        if (((cur != null) && (cur.getCount() > 0))) {
            cur.moveToFirst();
            result = cur.getString(0);
            cur.close();
            return result;
        } else {
            cur.close();
            return result;
        }

    }

    public int getToBeUploadeCustomers(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select row_id from customers_pending_approval where upload_status ='false'",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }
    public int getUploadedCustomers(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select row_id from customers_pending_approval where upload_status ='true'",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }


}