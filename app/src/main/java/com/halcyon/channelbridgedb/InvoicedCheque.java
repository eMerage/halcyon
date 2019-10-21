package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InvoicedCheque {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_INVOICE_ID = "invoice_id";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_CHEQUE_ID = "cheque_id";
    private static final String KEY_CHEQUE_AMOUNT = "cheque_amount";
    private static final String KEY_COLLECTED_DATE = "collected_date";
    private static final String KEY_RELEASE_DATE = "released_date";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_UPLOADED_STATUS = "uploaded_status";

    String[] columns = {KEY_ROW_ID, KEY_INVOICE_ID, KEY_CUSTOMER_ID,
            KEY_CHEQUE_ID, KEY_CHEQUE_AMOUNT, KEY_COLLECTED_DATE,
            KEY_RELEASE_DATE, KEY_TIMESTAMP, KEY_UPLOADED_STATUS};

    private static final String TABLE_NAME = "invoiced_cheques";
    private static final String REMARKS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_INVOICE_ID + " TEXT NOT NULL ,"
            + KEY_CUSTOMER_ID + " TEXT NOT NULL ,"
            + KEY_CHEQUE_ID + " TEXT NOT NULL ,"
            + KEY_CHEQUE_AMOUNT + " TEXT NOT NULL ,"
            + KEY_COLLECTED_DATE + " TEXT NOT NULL ,"
            + KEY_RELEASE_DATE + " TEXT NOT NULL, "
            + KEY_TIMESTAMP + " TEXT NOT NULL, "
            + KEY_UPLOADED_STATUS + " TEXT NOT NULL );";
    public final Context remarksContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public InvoicedCheque(Context c) {
        remarksContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(REMARKS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public InvoicedCheque openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(remarksContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public InvoicedCheque openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(remarksContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertInvoicedCheque(String invoiceId, String customerId, String chequeId, String chequeAmount, String collectedDate,
                                     String releaseDate, String timeStamp, String uploadStatus) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_INVOICE_ID, invoiceId);
        cv.put(KEY_CUSTOMER_ID, customerId);
        cv.put(KEY_CHEQUE_ID, chequeId);
        cv.put(KEY_CHEQUE_AMOUNT, chequeAmount);
        cv.put(KEY_COLLECTED_DATE, collectedDate);
        cv.put(KEY_RELEASE_DATE, releaseDate);
        cv.put(KEY_TIMESTAMP, timeStamp);
        cv.put(KEY_UPLOADED_STATUS, uploadStatus);

        return database.insert(TABLE_NAME, null, cv);

    }


    public List<String[]> getInvoicedChequesByStatus(String status) {

        List<String[]> invoicedCheques = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOADED_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoicedChequeData = new String[9];
            invoicedChequeData[0] = cursor.getString(0);
            invoicedChequeData[1] = cursor.getString(1);
            invoicedChequeData[2] = cursor.getString(2);
            invoicedChequeData[3] = cursor.getString(3);
            invoicedChequeData[4] = cursor.getString(4);
            invoicedChequeData[5] = cursor.getString(5);
            invoicedChequeData[6] = cursor.getString(6);
            invoicedChequeData[7] = cursor.getString(7);
            invoicedChequeData[8] = cursor.getString(8);


            invoicedCheques.add(invoicedChequeData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + invoicedCheques.size());

        return invoicedCheques;
    }

    public void setInvoicedChequesUploadedStatus(String invoiceChequeId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOADED_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = '"
                + invoiceChequeId
                + "'";

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice cheque uploaded status to :" + status + " of id : " + invoiceChequeId + "");
    }
}
