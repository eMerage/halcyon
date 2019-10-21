package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customers {
    //
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PHARMACY_ID = "pharmacy_id";
    private static final String KEY_PHARMACY_CODE = "pharmacy_code";
    private static final String KEY_DEALER_ID = "dealer_id";
    private static final String KEY_COMPANY_CODE = "company_code";
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AREA = "area";
    private static final String KEY_TOWN = "town";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_FAX = "fax";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CUSTOMER_STATUS = "customer_status";
    private static final String KEY_CREDIT_LIMIT = "credit_limit";
    private static final String KEY_CURRENT_CREDIT = "current_credit";
    private static final String KEY_CREDIT_EXPIRY_DATE = "credit_expiry_date";
    private static final String KEY_CREDIT_DURATION = "credit_duration";
    private static final String KEY_VAT_NO = "vat_no";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIME_STAMP = "time_stamp";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_WEB = "web";
    private static final String KEY_BR_NO = "br_no";
    private static final String KEY_OWNER_CONTACT = "owner_contact";
    private static final String KEY_OWNER_WIFE_BDAY = "owner_wife_bday";
    private static final String KEY_PHARMACY_REG_NO = "pharmacy_reg_no";
    private static final String KEY_PHARMACIST_NAME = "pharmacist_name";
    private static final String KEY_PURCHASING_OFFICER = "purchasing_officer";
    private static final String KEY_NO_OF_STAFF = "no_of_staff";
    private static final String KEY_CUSTOMER_CODE = "customer_code";
    private static final String KEY_MAX_JOB_ID = "max_job_id";
    private static final String KEY_IMAGE_ID = "image_id";
    private static final String KEY_IMAGE_BLOB = "image_blob";//new add sk

    private static final String KEY_IS_INVOICE_ALLOWED = "is_Invoice_allowed";
    private static final String KEY_MAX_INVOICE_COUNT = "max_invoice_count";
    private static final String KEY_CUSTOMER_BLOCKED = "customer_blocked";

    String[] columns = new String[]{KEY_ROW_ID, KEY_PHARMACY_ID,
            KEY_PHARMACY_CODE, KEY_DEALER_ID, KEY_COMPANY_CODE,
            KEY_CUSTOMER_NAME, KEY_ADDRESS, KEY_AREA, KEY_TOWN, KEY_DISTRICT,
            KEY_TELEPHONE, KEY_FAX, KEY_EMAIL, KEY_CUSTOMER_STATUS,
            KEY_CREDIT_LIMIT, KEY_CURRENT_CREDIT, KEY_CREDIT_EXPIRY_DATE,
            KEY_CREDIT_DURATION, KEY_VAT_NO, KEY_STATUS, KEY_TIME_STAMP,
            KEY_LATITUDE, KEY_LONGITUDE, KEY_WEB, KEY_BR_NO, KEY_OWNER_CONTACT,
            KEY_OWNER_WIFE_BDAY, KEY_PHARMACY_REG_NO, KEY_PHARMACIST_NAME,
            KEY_PURCHASING_OFFICER, KEY_NO_OF_STAFF, KEY_CUSTOMER_CODE,
            KEY_MAX_JOB_ID, KEY_IMAGE_ID, KEY_IMAGE_BLOB,KEY_IS_INVOICE_ALLOWED,KEY_MAX_INVOICE_COUNT,KEY_CUSTOMER_BLOCKED};//add customer image  sk


    private static final String TABLE_NAME = "customers";
    private static final String CUSTOMER_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PHARMACY_ID + " TEXT NOT NULL,"
            + KEY_PHARMACY_CODE + " TEXT ,"
            + KEY_DEALER_ID + " TEXT ,"
            + KEY_COMPANY_CODE + " TEXT ,"
            + KEY_CUSTOMER_NAME + " TEXT ,"
            + KEY_ADDRESS + " TEXT ,"
            + KEY_AREA + " TEXT ,"
            + KEY_TOWN + " TEXT ,"
            + KEY_DISTRICT + " TEXT ,"
            + KEY_TELEPHONE + " TEXT ,"
            + KEY_FAX + " TEXT ,"
            + KEY_EMAIL + " TEXT ,"
            + KEY_CUSTOMER_STATUS + " TEXT ,"
            + KEY_CREDIT_LIMIT + " INTEGER ,"
            + KEY_CURRENT_CREDIT + " INTEGER ,"
            + KEY_CREDIT_EXPIRY_DATE + " TEXT ,"
            + KEY_CREDIT_DURATION + " TEXT ," + KEY_VAT_NO + " TEXT ,"
            + KEY_STATUS + " TEXT ," + KEY_TIME_STAMP + " TEXT ,"
            + KEY_LATITUDE + " TEXT ," + KEY_LONGITUDE + " TEXT , " + KEY_WEB
            + " TEXT, " + KEY_BR_NO + " TEXT, " + KEY_OWNER_CONTACT + " TEXT, "
            + KEY_OWNER_WIFE_BDAY + " NUMERIC, " + KEY_PHARMACY_REG_NO
            + " TEXT, " + KEY_PHARMACIST_NAME + " TEXT, "
            + KEY_PURCHASING_OFFICER + " TEXT, " + KEY_NO_OF_STAFF
            + " INTEGER, " + KEY_CUSTOMER_CODE + " TEXT, "
            + KEY_MAX_JOB_ID + " TEXT, "
            + KEY_IMAGE_ID + " TEXT, "
            + KEY_IMAGE_BLOB + " BLOB, "
            + KEY_IS_INVOICE_ALLOWED + " INTEGER, "
            + KEY_MAX_INVOICE_COUNT + " INTEGER, "
            + KEY_CUSTOMER_BLOCKED + " INTEGER " + " );"; //add customer image  sk

    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    // 0 - rowid
    // 1 - pharmacyId
    // 2 - pharmacyCode
    // 3 - dealerId
    // 4 - companyCode
    // 5 - customerName
    // 6 - address
    // 7 - area
    // 8 - town
    // 9 - district
    // 10 - telephone
    // 11 - fax
    // 12 - email
    // 13 - customerStatus
    // 14 - creditLimit
    // 16 - currentCredit
    // 16 - creditExpiryDate
    // 17 - creditDuration
    // 18 - vatNo
    // 19 - status

    public Customers(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CUSTOMER_CREATE);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

    }

    public Customers openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Customers openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertCustomer(String pharmacyId, String pharmacyCode,
                               String dealerId, String companyCode, String customerName,
                               String address, String area, String town, String district,
                               String telephone, String fax, String email, String customerStatus,
                               String creditLimit, String currentCredit, String creditExpiryDate,
                               String creditDuration, String vatNo, String status,
                               String timeStamp, String latitude, String longitude, String web,
                               String brNo, String ownerContact, String ownerWifeBday,
                               String pharmacyRegNo, String pharmacistName,
                               String purchasingOfficer, String noStaff, String customerCode,
                               String maxJobId, String imageId, byte[] image,String invoiceallowd,String maxcount,String cusBlock) throws SQLException {//add new byte image

        ContentValues cv = new ContentValues();

        cv.put(KEY_PHARMACY_ID, pharmacyId);
        cv.put(KEY_PHARMACY_CODE, pharmacyCode);
        cv.put(KEY_DEALER_ID, dealerId);
        cv.put(KEY_COMPANY_CODE, companyCode);
        cv.put(KEY_CUSTOMER_NAME, customerName);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_AREA, area);
        cv.put(KEY_TOWN, town);
        cv.put(KEY_DISTRICT, district);
        cv.put(KEY_TELEPHONE, telephone);
        cv.put(KEY_FAX, fax);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_CUSTOMER_STATUS, customerStatus);
        cv.put(KEY_CREDIT_LIMIT, creditLimit);
        cv.put(KEY_CURRENT_CREDIT,currentCredit);
        cv.put(KEY_CREDIT_EXPIRY_DATE, creditExpiryDate);
        cv.put(KEY_CREDIT_DURATION, creditDuration);
        cv.put(KEY_VAT_NO, vatNo);
        cv.put(KEY_STATUS, status);
        cv.put(KEY_TIME_STAMP, timeStamp);
        cv.put(KEY_LATITUDE, latitude);
        cv.put(KEY_LONGITUDE, longitude);
        cv.put(KEY_WEB, web);
        cv.put(KEY_BR_NO, brNo);
        cv.put(KEY_OWNER_CONTACT, ownerContact);
        cv.put(KEY_OWNER_WIFE_BDAY, ownerWifeBday);
        cv.put(KEY_PHARMACY_REG_NO, pharmacyRegNo);
        cv.put(KEY_PHARMACIST_NAME, pharmacistName);
        cv.put(KEY_PURCHASING_OFFICER, purchasingOfficer);
        cv.put(KEY_NO_OF_STAFF, noStaff);
        cv.put(KEY_CUSTOMER_CODE, customerCode);
        cv.put(KEY_MAX_JOB_ID, maxJobId);
        cv.put(KEY_IMAGE_ID, imageId);
        cv.put(KEY_IMAGE_BLOB, image);

        System.out.println("KEY_PHARMACY_ID :"+pharmacyId);
        System.out.println("KEY_CURRENT_CREDIT :"+currentCredit);
        System.out.println("KEY_IS_INVOICE_ALLOWED :"+invoiceallowd);
        cv.put(KEY_IS_INVOICE_ALLOWED, Integer.parseInt(invoiceallowd));



        cv.put(KEY_MAX_INVOICE_COUNT, Integer.parseInt(maxcount));
        cv.put(KEY_CUSTOMER_BLOCKED, Integer.parseInt(cusBlock));
        // cv.put(KEY_IMAGE_BLOB,);//add new byte image sk

        return database.insert(TABLE_NAME, null, cv);

    }

    public List<String[]> getAllCustomers() {
        List<String[]> customers = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);
        // Log.w("cursor Size", cursor.getCount() + "");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] customerData = new String[21];
            customerData[0] = cursor.getString(0);
            customerData[1] = cursor.getString(1);
            customerData[2] = cursor.getString(2);
            customerData[3] = cursor.getString(3);
            customerData[4] = cursor.getString(4);
            customerData[5] = cursor.getString(5);//
            customerData[6] = cursor.getString(6);
            customerData[7] = cursor.getString(7);
            customerData[8] = cursor.getString(8);//
            customerData[9] = cursor.getString(9);
            customerData[10] = cursor.getString(10);//
            customerData[11] = cursor.getString(11);
            customerData[12] = cursor.getString(12);
            customerData[13] = cursor.getString(13);//
            customerData[14] = cursor.getString(14);
            customerData[15] = cursor.getString(15);
            customerData[16] = cursor.getString(16);
            customerData[17] = cursor.getString(17);
            customerData[18] = cursor.getString(18);
            customerData[19] = cursor.getString(19);
            customerData[20] = cursor.getString(20);

            // Log.w("customerData[0]", cursor.getString(0));
            // Log.w("customerData[1]", cursor.getString(1));
            // Log.w("customerData[2]", cursor.getString(2));
            // Log.w("customerData[3]", cursor.getString(3));
            // Log.w("customerData[4]", cursor.getString(4));
            // Log.w("customerData[5]", cursor.getString(5));
            // Log.w("customerData[6]", cursor.getString(6));
            // Log.w("customerData[7]", cursor.getString(7));
            // Log.w("customerData[8]", cursor.getString(8));
            // Log.w("customerData[9]", cursor.getString(9));
            // Log.w("customerData[10]", cursor.getString(10));
            // Log.w("customerData[11]", cursor.getString(11));
            // Log.w("customerData[12]", cursor.getString(12));
            // Log.w("customerData[13]", cursor.getString(13));
            // Log.w("customerData[14]", cursor.getString(14));
            // Log.w("customerData[15]", cursor.getString(15));
            // Log.w("customerData[16]", cursor.getString(16));
            // Log.w("customerData[17]", cursor.getString(17));
            // Log.w("customerData[18]", cursor.getString(18));
            // Log.w("customerData[19]", cursor.getString(19));
            // Log.w("customerData[20]", cursor.getString(20));

            customers.add(customerData);
            cursor.moveToNext();
        }

        cursor.close();

        return customers;
    }

    public List<String[]> searchCustomers(String custName, String order) {
        List<String[]> customers = new ArrayList<String[]>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMER_NAME + " LIKE ?",
                    new String[]{custName + "%"});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String[] customerData = new String[21];
                customerData[0] = cursor.getString(0);
                customerData[1] = cursor.getString(1);
                customerData[2] = cursor.getString(2);
                customerData[3] = cursor.getString(3);
                customerData[4] = cursor.getString(4);
                customerData[5] = cursor.getString(5);//
                customerData[6] = cursor.getString(6);
                customerData[7] = cursor.getString(7);
                customerData[8] = cursor.getString(8);//
                customerData[9] = cursor.getString(9);
                customerData[10] = cursor.getString(10);//
                customerData[11] = cursor.getString(11);
                customerData[12] = cursor.getString(12);
                customerData[13] = cursor.getString(13);//
                customerData[14] = cursor.getString(14);
                customerData[15] = cursor.getString(15);
                customerData[16] = cursor.getString(16);
                customerData[17] = cursor.getString(17);
                customerData[18] = cursor.getString(18);
                customerData[19] = cursor.getString(19);
                customerData[20] = cursor.getString(20);

                customers.add(customerData);
                cursor.moveToNext();
            }


        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
        }
        cursor.close();

        return customers;
    }


    public ArrayList<String> getCustomerNames() {

        Cursor cursor = database.query(TABLE_NAME,
                new String[]{KEY_CUSTOMER_NAME}, null, null, null, null,
                null);
        ArrayList<String> customerNames = new ArrayList<String>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            customerNames.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return customerNames;
    }

    public String[] getCustomerDetailsByPharmacyId(String pharmacyId) {

        String[] data = new String[20];

        Cursor cursor = database.query(TABLE_NAME, columns, KEY_PHARMACY_ID
                + " = ?", new String[]{pharmacyId}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            data[0] = cursor.getString(0);
            // data[1] = cursor.getString(1);
            data[2] = cursor.getString(2);
            data[3] = cursor.getString(3);
            data[4] = cursor.getString(4);
            data[5] = cursor.getString(5);
            data[6] = cursor.getString(6);
            data[7] = cursor.getString(7);
            data[8] = cursor.getString(8);
            data[9] = cursor.getString(9);
            data[10] = cursor.getString(10);
            data[11] = cursor.getString(11);
            data[12] = cursor.getString(12);
            data[13] = cursor.getString(13);
            data[14] = cursor.getString(14);
            data[15] = cursor.getString(15);
            data[16] = cursor.getString(16);
            data[17] = cursor.getString(17);
            data[18] = cursor.getString(18);
            data[19] = cursor.getString(19);

            Log.w("Log", "data[0] sisze : " + data[0]);
            cursor.moveToNext();
        }
        cursor.close();
        return data;

    }


    public ArrayList<String> getCustomerStatusTypes() {

        final String query = "SELECT DISTINCT " + KEY_CUSTOMER_STATUS + " FROM " + TABLE_NAME;


        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> statusList = new ArrayList<String>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            statusList.add(cursor.getString(0));
            Log.w("customer getlists", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return statusList;
    }

    public ArrayList<String> getTownList() {

        final String query = "SELECT DISTINCT " + KEY_TOWN + " FROM "
                + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> townList = new ArrayList<String>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            townList.add(cursor.getString(0));
            Log.w("customer getlists", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return townList;
    }

    public ArrayList<String> getAreaList() {

        final String query = "SELECT DISTINCT " + KEY_AREA + " FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> areaList = new ArrayList<String>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            areaList.add(cursor.getString(0));
            Log.w("customer getlists", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return areaList;
    }

    public ArrayList<String> getDistrictList() {

        final String query = "SELECT DISTINCT " + KEY_DISTRICT + " FROM "
                + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> districtList = new ArrayList<String>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            districtList.add(cursor.getString(0));
            Log.w("customer getlists", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return districtList;
    }


    public ArrayList<String> getAllImageIds() {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{KEY_IMAGE_ID}, null, null, null, null, null);
        cursor.moveToFirst();

        ArrayList<String> imageIds = new ArrayList<String>();

        while (!cursor.isAfterLast()) {
            if (cursor.getString(0) != null) {
                imageIds.add(cursor.getString(0));

            }
            cursor.moveToNext();
        }
        cursor.close();
        return imageIds;
    }



    public long updateCustomerDetails(String pharmacyId, String pharmacyCode,
                                      String dealerId, String companyCode, String customerName,
                                      String address, String area, String town, String district,
                                      String telephone, String fax, String email, String customerStatus,
                                      String creditLimit, String currentCredit, String creditExpiryDate,
                                      String creditDuration, String vatNo, String status,
                                      String timeStamp, String latitude, String longitude, String web,
                                      String brNo, String ownerContact, String ownerWifeBday,
                                      String pharmacyRegNo, String pharmacistName,
                                      String purchasingOfficer, String noStaff, String customerCode,
                                      String maxJobId, String imageId, byte[] image,String invoiceallowd,String maxcount,String cusBlock) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_PHARMACY_ID, pharmacyId);
        cv.put(KEY_PHARMACY_CODE, pharmacyCode);
        cv.put(KEY_DEALER_ID, dealerId);
        cv.put(KEY_COMPANY_CODE, companyCode);
        cv.put(KEY_CUSTOMER_NAME, customerName);
        cv.put(KEY_ADDRESS, address);
        cv.put(KEY_AREA, area);
        cv.put(KEY_TOWN, town);
        cv.put(KEY_DISTRICT, district);
        cv.put(KEY_TELEPHONE, telephone);
        cv.put(KEY_FAX, fax);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_CUSTOMER_STATUS, customerStatus);
        cv.put(KEY_CREDIT_LIMIT, creditLimit);
        cv.put(KEY_CURRENT_CREDIT, currentCredit);
        cv.put(KEY_CREDIT_EXPIRY_DATE, creditExpiryDate);
        cv.put(KEY_CREDIT_DURATION, creditDuration);
        cv.put(KEY_VAT_NO, vatNo);
        cv.put(KEY_STATUS, status);
        cv.put(KEY_TIME_STAMP, timeStamp);
        cv.put(KEY_LATITUDE, latitude);
        cv.put(KEY_LONGITUDE, longitude);
        cv.put(KEY_WEB, web);
        cv.put(KEY_BR_NO, brNo);
        cv.put(KEY_OWNER_CONTACT, ownerContact);
        cv.put(KEY_OWNER_WIFE_BDAY, ownerWifeBday);
        cv.put(KEY_PHARMACY_REG_NO, pharmacyRegNo);
        cv.put(KEY_PHARMACIST_NAME, pharmacistName);
        cv.put(KEY_PURCHASING_OFFICER, purchasingOfficer);
        cv.put(KEY_NO_OF_STAFF, noStaff);
        cv.put(KEY_CUSTOMER_CODE, customerCode);
        cv.put(KEY_MAX_JOB_ID, maxJobId);
        cv.put(KEY_IMAGE_ID, imageId);
        cv.put(KEY_IMAGE_BLOB, image);

        cv.put(KEY_IS_INVOICE_ALLOWED, Integer.parseInt(invoiceallowd));
        cv.put(KEY_MAX_INVOICE_COUNT, Integer.parseInt(maxcount));
        cv.put(KEY_CUSTOMER_BLOCKED, Integer.parseInt(cusBlock));

        return database.update(TABLE_NAME, cv, KEY_PHARMACY_ID + "=?",
                new String[]{pharmacyId});
    }

    public boolean isCustomerDownloaded(String pharmacyId) {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{KEY_PHARMACY_ID}, KEY_PHARMACY_ID + "=?",
                new String[]{pharmacyId}, null, null, null);
        boolean isAvailable = false;
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            isAvailable = false;
        } else {
            isAvailable = true;
        }
        cursor.close();
        return isAvailable;
    }

    public String getMaxCustomerId() {

        Cursor cursor = database.query(TABLE_NAME, new String[]{"Max("
                + KEY_MAX_JOB_ID + ")"}, null, null, null, null, null, null);

        String productId = "0";
        if (cursor.getCount() == 0) {

        } else {
            cursor.moveToFirst();
            productId = cursor.getString(0);
        }
        cursor.close();
        return productId;
    }

    public void setImageIdByCustomerPharmacyCode(String pharmacyId, String imageId) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_IMAGE_ID
                + " = '"
                + imageId
                + "' WHERE "
                + KEY_PHARMACY_ID
                + " = "
                + pharmacyId;

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + imageId + " of id : " + pharmacyId + "");
    }

    public void setBinaryImageIdByCustomerPharmacyCode(String pharmacyId, byte[] imageId) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_IMAGE_BLOB
                + " = '"
                + imageId
                + "' WHERE "
                + KEY_PHARMACY_ID
                + " = "
                + pharmacyId;

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + imageId + " of id : " + pharmacyId + "");
    }

    public byte[] getByteArrayImage(String selectedItem1) {
        byte[] byteArray = new byte[0];
        Cursor cur = null;
        try {
            String strqu = "select " + KEY_IMAGE_BLOB + " from " + TABLE_NAME + " where " + KEY_PHARMACY_ID + " ='" + selectedItem1 + "' ";

            cur = database.rawQuery(strqu, null);
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
            cur.close();

        }


        return byteArray;


    }

    public int get_rowcount() {
        int count = 0;
        Cursor cur = null;
        try {
            String countQuery = "SELECT  * FROM " + TABLE_NAME;
            cur = database.rawQuery(countQuery, null);
            count = cur.getCount();
            cur.close();


        } catch (Exception e) {
            cur.close();
        }
        return count;
    }

    public int getrowcount() {
        openReadableDatabase();
        int count = 0;
        Cursor cur = null;
        try {
            String countQuery = "SELECT  * FROM " + TABLE_NAME;
            cur = database.rawQuery(countQuery, null);
            count = cur.getCount();
            cur.close();


        } catch (Exception e) {
            cur.close();
        }
        closeDatabase();
        return count;
    }


    public String getCompanyCodeFromPhamcyId(String pharmacyId) {

        String code = "Null";
        Cursor cur = null;
        try {
            String strqu = "select " + KEY_COMPANY_CODE + " from " + TABLE_NAME + " where " + KEY_PHARMACY_ID + " ='" + pharmacyId + "' ";

            cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {
                    code = cur.getString(0);
                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {
            cur.close();

        }
        return code;
    }

    public double GetOustand_value(String selectedItem1) {
        double value = 0.0;
        Cursor cur = null;
        try {
            String strqu = "select " + KEY_CURRENT_CREDIT + " from " + TABLE_NAME + " where " + KEY_PHARMACY_ID + " ='" + selectedItem1 + "' ";

            cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {
                    value = Double.parseDouble(String.valueOf(cur.getInt(0)));
                    // value =value+value_sub;
                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {
            cur.close();

        }


        return value;


    }

    //Himanshu
    public String getInvoiceAlloweStstusByPharmacyId(String PharmacyID) {
        String isInvoiceallowed;
        Cursor cur = null;

        try {
            cur = database.rawQuery("SELECT is_Invoice_allowed FROM customers where pharmacy_id =" + PharmacyID + " ", null);
            cur.moveToFirst();
            isInvoiceallowed = cur.getString(0);
            cur.close();
        }catch (SQLiteException sql){
            isInvoiceallowed="Null";
        }catch (Exception e){
            isInvoiceallowed="Null";
        }

        return isInvoiceallowed;
    }

    public String getMaxInvoiceCountByPharmacyId(String PharmacyID) {

        Cursor cur = database.rawQuery("SELECT max_invoice_count FROM customers where pharmacy_id =" + PharmacyID + " ", null);
        cur.moveToFirst();

        String productId = cur.getString(0);
        cur.close();
        return productId;
    }
    public void setInvoiceAlloweStstus(String PharmacyID,int status) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_IS_INVOICE_ALLOWED, status);
        database.update(TABLE_NAME, cv, KEY_PHARMACY_ID + "=?",
                new String[]{PharmacyID});
    }

    public String getCustomerByPharmacyId(String PharmacyID) {

        String result;
        Cursor cur = database.rawQuery("SELECT customer_name FROM customers where pharmacy_id ='" + PharmacyID + "' ", null);

        if(cur.getCount()!=0){
            cur.moveToFirst();
            result =cur.getString(0);
            cur.close();
            return result;
        }else {
            cur.close();
            return null;
        }



    }

    public String getCustomerBlockStatesByPharmacyId(String PharmacyID) {

        Cursor cur = database.rawQuery("SELECT customer_blocked FROM customers where pharmacy_id ='" + PharmacyID + "' ", null);
        String result;
        if(((cur != null) && (cur.getCount() > 0))) {
            cur.moveToFirst();
            result =cur.getString(0);
            cur.close();
            return result;
        }else{
            cur.close();
            return "4";
        }

    }
    //
    public  List<String> getAllCustomerDetails() {

        List<String> CustomerNameList = new ArrayList();
        Cursor cur = null;
        try {

            String strqu = "select pharmacy_id,customer_name,pharmacy_code from " + TABLE_NAME + "  ";
            cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {
                    CustomerNameList.add(cur.getString(1) + "~" + cur.getString(2));
                    // CustomerNameList.add(cur.getString(1));

                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {
            cur.close();

        }
        cur.close();

        return CustomerNameList;
    }

    public String getCustomerPhoneByPharmacyId(String PharmacyID) {

        Cursor cur = database.rawQuery("SELECT telephone FROM customers where pharmacy_id ='" + PharmacyID + "' ", null);
        String result = "0";
        if(((cur != null) && (cur.getCount() > 0))) {
            cur.moveToFirst();
            result = cur.getString(0);
            cur.close();
            return result;
        }else{
            cur.close();
            return result;
        }

    }

    public ArrayList<String> getCustomerpharmacyID() {
        openReadableDatabase();
        ArrayList<String> CustomerId = new ArrayList<String>();

        Cursor cur = database.rawQuery("SELECT pharmacy_id FROM customers", null);

        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            CustomerId.add(cur.getString(0));
            cur.moveToNext();
        }
        cur.close();
        closeDatabase();
        return CustomerId;




    }

}
