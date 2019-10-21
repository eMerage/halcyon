package com.halcyon.channelbridgedb;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductUnload {
    //
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_BATCH_NO = "batch_no";
    private static final String KEY_EXPIRE_DATE = "expire_date";
    private static final String KEY_UNLOAD_QTY = "unload_qty";
    private static final String KEY_UPLOAD_STATUS = "upload_status";
    private static final String KEY_TIME_STAMP = "time_stamp";


    String[] columns = new String[]{KEY_ROW_ID, KEY_PRODUCT_ID, KEY_BATCH_NO,
            KEY_EXPIRE_DATE, KEY_UNLOAD_QTY, KEY_UPLOAD_STATUS, KEY_TIME_STAMP};

    private static final String TABLE_NAME = "product_unload";
    private static final String PROD_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRODUCT_ID + " TEXT NOT NULL, "
            + KEY_BATCH_NO + " TEXT NOT NULL, "
            + KEY_EXPIRE_DATE + " TEXT, "
            + KEY_UNLOAD_QTY + " TEXT NOT NULL, "
            + KEY_UPLOAD_STATUS + " TEXT, "
            + KEY_TIME_STAMP + " TEXT ); ";
    public final Context proUnloadContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ProductUnload(Context c) {
        proUnloadContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(PROD_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public ProductUnload openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(proUnloadContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ProductUnload openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(proUnloadContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertProdUnload(String prodId, String batchNo, String expireDate, String unloadQty, String uploadStatus,
                                 String timeStamp) throws SQLException {


        ContentValues cv = new ContentValues();
        cv.put(KEY_PRODUCT_ID, prodId);
        cv.put(KEY_BATCH_NO, batchNo);
        cv.put(KEY_EXPIRE_DATE, expireDate);
        cv.put(KEY_UNLOAD_QTY, unloadQty);
        cv.put(KEY_UPLOAD_STATUS, uploadStatus);
        cv.put(KEY_TIME_STAMP, timeStamp);
        return database.insert(TABLE_NAME, null, cv);


    }

    public ArrayList<String[]> getProdUnloadsByUploadStatus(String status) {
        ArrayList<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOAD_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[7];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }


    public ArrayList<String[]> getProdUnloadsByProdCodeAndBatch(String prodCode, String batch) {
        ArrayList<String[]> invoice = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_PRODUCT_ID + " = '" + prodCode + "' AND " + KEY_BATCH_NO + "='" + batch + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[7];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);
            invoiceData[6] = cursor.getString(6);

            invoice.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoice.size());

        return invoice;
    }

    public void setProdUnloadStatus(String prdUnloadId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOAD_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = '"
                + prdUnloadId
                + "'";

        database.execSQL(updateQuery);
    }

    public int getToBeUploadeProdUnload(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select row_id from product_unload where upload_status = 1 ",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }
    public int getUploadedProdUnload(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select row_id from product_unload where upload_status = 2",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }

}
