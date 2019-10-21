package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductReturns {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_BATCH_NO = "batch_no";
    private static final String KEY_INVOICE_NO = "invoice_number";
    private static final String KEY_ISSUE_MODE = "issue_mode";
    private static final String KEY_NORMAL = "normal";
    private static final String KEY_FREE = "free";
    private static final String KEY_RETURN_DATE = "return_date";
    private static final String KEY_PHARMACY_ID = "pharmacy_id";
    private static final String KEY_UPLOADED_STATUS = "uploaded_status";
    private static final String KEY_UNIT_PRICE = "unit_price";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_RETURN_INVOICE = "return_invoice";
    private static final String KEY_RETURN_VALIDATED = "return_validated";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_IS_ONTIME = "IsOnTime";

    String[] columns = {KEY_ROW_ID, KEY_PRODUCT_CODE, KEY_BATCH_NO, KEY_INVOICE_NO, KEY_ISSUE_MODE, KEY_NORMAL, KEY_FREE, KEY_RETURN_DATE, KEY_PHARMACY_ID, KEY_UPLOADED_STATUS, KEY_UNIT_PRICE, KEY_DISCOUNT, KEY_RETURN_INVOICE, KEY_RETURN_VALIDATED,KEY_LATITUDE,KEY_LONGITUDE,KEY_IS_ONTIME};
    private static final String TABLE_NAME = "product_returns";
    private static final String PRODUCT_RETURNS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRODUCT_CODE + " TEXT NOT NULL,"
            + KEY_BATCH_NO + " TEXT ,"
            + KEY_INVOICE_NO + " TEXT ,"
            + KEY_ISSUE_MODE + " TEXT ,"
            + KEY_NORMAL + " TEXT ,"
            + KEY_FREE + " TEXT ,"
            + KEY_RETURN_DATE + " TEXT ,"
            + KEY_PHARMACY_ID + " TEXT ,"
            + KEY_UPLOADED_STATUS + " TEXT " + " ,"
            + KEY_UNIT_PRICE + " TEXT " + " ,"
            + KEY_DISCOUNT + " TEXT " + " ,"
            + KEY_RETURN_INVOICE + " TEXT " + " ,"
            + KEY_RETURN_VALIDATED + " TEXT ,"
            + KEY_LATITUDE + " TEXT ,"
            + KEY_LONGITUDE + " TEXT ,"
            + KEY_IS_ONTIME + " TEXT "
            + ");";
    public final Context productReturnsContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ProductReturns(Context c) {
        productReturnsContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(PRODUCT_RETURNS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public ProductReturns openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productReturnsContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ProductReturns openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productReturnsContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertProductReturn(String productCode, String batchNo, String invoiceNo, String issueMode, String normal, String free, String returnDate,
                                    String customerNo, String uploadedStatus, String unitPrice, String discount, String returnInvoice, String invoiceValidated, String lat, String lon,String ontime) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_PRODUCT_CODE, productCode);
        cv.put(KEY_BATCH_NO, batchNo);
        cv.put(KEY_INVOICE_NO, invoiceNo);
        cv.put(KEY_ISSUE_MODE, issueMode);
        cv.put(KEY_NORMAL, normal);
        cv.put(KEY_FREE, free);
        cv.put(KEY_RETURN_DATE, returnDate);
        cv.put(KEY_PHARMACY_ID, customerNo);
        cv.put(KEY_UPLOADED_STATUS, uploadedStatus);
        cv.put(KEY_UNIT_PRICE, unitPrice);
        cv.put(KEY_DISCOUNT, discount);
        cv.put(KEY_RETURN_INVOICE, returnInvoice);
        cv.put(KEY_RETURN_VALIDATED, invoiceValidated);
        cv.put(KEY_LATITUDE, lat);
        cv.put(KEY_LONGITUDE, lon);
        cv.put(KEY_IS_ONTIME, ontime);
        return database.insert(TABLE_NAME, null, cv);

    }

    public ArrayList<String[]> getAllReturnProducts() throws SQLException {
        ArrayList<String[]> returnProducts = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String[] productReturnData = new String[18];
            productReturnData[0] = cursor.getString(0);
            productReturnData[1] = cursor.getString(1);
            productReturnData[2] = cursor.getString(2);
            productReturnData[3] = cursor.getString(3);
            productReturnData[4] = cursor.getString(4);
            productReturnData[5] = cursor.getString(5);
            productReturnData[6] = cursor.getString(6);
            productReturnData[7] = cursor.getString(7);
            productReturnData[8] = cursor.getString(8);
            productReturnData[9] = cursor.getString(9);
            productReturnData[10] = cursor.getString(10);
            productReturnData[11] = cursor.getString(11);
            productReturnData[12] = cursor.getString(12);
            productReturnData[13] = cursor.getString(13);
            productReturnData[14] = cursor.getString(14);
            productReturnData[15] = cursor.getString(15);
            productReturnData[16] = cursor.getString(16);
            returnProducts.add(productReturnData);

            //			Incase you need to query and see :)

            Log.w("ProductReturn: ", "rowId: " + cursor.getString(0));
            Log.w("ProductReturn: ", "productCode: " + cursor.getString(1));
            Log.w("ProductReturn: ", "batchNo: " + cursor.getString(2));
            Log.w("ProductReturn: ", "invoiceNo:" + cursor.getString(3));
            Log.w("ProductReturn: ", "issueMode: " + cursor.getString(4));
            Log.w("ProductReturn: ", "normal: " + cursor.getString(5));
            Log.w("ProductReturn: ", "free: " + cursor.getString(6));
            Log.w("ProductReturn: ", "returnDate:" + cursor.getString(7));
            Log.w("ProductReturn: ", "customerNumber: " + cursor.getString(8));
            Log.w("ProductReturn: ", "uploadedStatus:" + cursor.getString(9));
            Log.w("ProductReturn: ", "unit price:" + cursor.getString(10));
            cursor.moveToNext();
        }
        cursor.close();
        return returnProducts;
    }

    public List<String[]> getProductReturnsByStatus(String status) {
        List<String[]> rtnProducts = new ArrayList<String[]>();


        Log.w("invoice size", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOADED_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[17];
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
            rtnProducts.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + rtnProducts.size());

        return rtnProducts;
    }

    public void setRtnProductsUploadedStatus(String rtnProdId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOADED_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = '"
                + rtnProdId
                + "'";

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + status + " of id : " + rtnProdId + "");
    }


    public void setRtnProductsUploadedStatusToAudit(String rtnProdId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOADED_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_RETURN_INVOICE
                + " = '"
                + rtnProdId
                + "'";

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + status + " of id : " + rtnProdId + "");
    }
    public ArrayList<String[]> getReturnDetailsByInvoiceId(String invoiceId) {
        String PRODUCT_TABLE = "products";
        String PRO_DES = "pro_des";
        String CODE = "code";
        String query = "SELECT "
                + KEY_PRODUCT_CODE + ", "
                + KEY_BATCH_NO + ", "
                + KEY_INVOICE_NO + ", "
                + KEY_ISSUE_MODE + ", "
                + KEY_NORMAL + ", "
                + KEY_FREE + ", "
                + KEY_RETURN_DATE + ", "
                + KEY_PHARMACY_ID + ", "
                + KEY_UNIT_PRICE + ", "
                + PRODUCT_TABLE + "." + PRO_DES + ", "
                + KEY_DISCOUNT
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + PRODUCT_TABLE + "." + CODE + "=" + TABLE_NAME + "." + KEY_PRODUCT_CODE
                + " WHERE "
                + KEY_RETURN_INVOICE + "='" + invoiceId + "'";


        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        Log.w("Cursor size", cursor.getCount() + "");
        Log.w("query", query);
        ArrayList<String[]> returnProducts = new ArrayList<String[]>();

        while (!cursor.isAfterLast()) {
            String[] returnDetails = new String[12];
            returnDetails[0] = cursor.getString(0);
            returnDetails[1] = cursor.getString(1);
            returnDetails[2] = cursor.getString(2);
            returnDetails[3] = cursor.getString(3);
            returnDetails[4] = cursor.getString(4);
            returnDetails[5] = cursor.getString(5);
            returnDetails[6] = cursor.getString(6);
            returnDetails[7] = cursor.getString(7);
            returnDetails[8] = cursor.getString(8);
            returnDetails[9] = cursor.getString(9);
            returnDetails[10] = cursor.getString(10);

            Log.w("ProductReturns[0]", returnDetails[0]);
            Log.w("ProductReturns[1]", returnDetails[1]);
            Log.w("ProductReturns[2]", returnDetails[2]);
            Log.w("ProductReturns[3]", returnDetails[3]);
            Log.w("ProductReturns[4]", returnDetails[4]);
            Log.w("ProductReturns[5]", returnDetails[5]);
            Log.w("ProductReturns[6]", returnDetails[6]);
            Log.w("ProductReturns[7]", returnDetails[7]);
            Log.w("ProductReturns[8]", returnDetails[8]);
            Log.w("ProductReturns[9]", returnDetails[9]);
            Log.w("ProductReturns[10]", returnDetails[10]);


            returnProducts.add(returnDetails);
            cursor.moveToNext();
        }

        cursor.close();
        Log.w("returnProducts :", "" + returnProducts.size());
        return returnProducts;
    }

    public long getTotalReturnedProductQuantityByPharmacyIdAndProductId(String pharmacyId, String productId) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{"SUM(" + KEY_NORMAL + ")"}, KEY_PHARMACY_ID + "='" + pharmacyId + "' AND " + KEY_PRODUCT_CODE + "='" + productId + "'", null, null, null, null);
        cursor.moveToFirst();

        long total = 0;

        if (cursor.getCount() == 0) {
            total = 0;
        } else {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public long getTotalReturnedQuantityByBatchAndPharmacyId(String batch, String pharmacyId) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{"SUM(" + KEY_NORMAL + ")"}, KEY_BATCH_NO + "='" + batch + "' AND " + KEY_PHARMACY_ID + "='" + pharmacyId + "'", null, null, null, null);
        cursor.moveToFirst();

        long total = 0;

        if (cursor.getCount() == 0) {
            total = 0;
        } else {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public long getSumReturnsByBatch(String invoiceId, String batch) {

        Cursor cursor = database.query(TABLE_NAME, new String[]{"SUM(" + KEY_NORMAL + ")"}, KEY_INVOICE_NO + "='" + invoiceId + "' AND " + KEY_BATCH_NO + "='" + batch + "'", null, null, null, null);
        cursor.moveToFirst();

        long total = 0;

        if (cursor.getCount() == 0) {
            total = 0;
        } else {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    public long getSumFreeReturnsByBatch(String invoiceId, String batch) {

        Cursor cursor = database.query(TABLE_NAME, new String[]{"SUM(" + KEY_FREE + ")"}, KEY_INVOICE_NO + "='" + invoiceId + "' AND " + KEY_BATCH_NO + "='" + batch + "'", null, null, null, null);
        cursor.moveToFirst();

        long total = 0;

        if (cursor.getCount() == 0) {
            total = 0;
        } else {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }


    public ArrayList<String> getAllReturnInvice(){

        ArrayList<String> invoiceId = new ArrayList<String>();
        Cursor cur = database.rawQuery("select return_invoice from product_returns ",null);

        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            invoiceId.add(cur.getString(0));
            cur.moveToNext();
        }
        cur.close();
        return invoiceId;


    }
}
