package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.halcyon.Entity.DeliveryEntity;


import android.database.SQLException;
import java.util.ArrayList;

public class Delivery {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_INVOICE_NO = "invoice_number";
    private static final String KEY_INVOICE_DATE = "invoice_date";
    private static final String KEY_CUSTOMER_NUMBER = "customer_number";
    private static final String KEY_CUSTOMER = "customer";
    private static final String KEY_INVOICE_VALUE = "invoice_value";
    private static final String KEY_DELIVERY_STATUS = "delivery_status";
    private static final String KEY_DELIVERY_REASON = "delivery_reason";
    private static final String KEY_IS_ACTIVE = "is_active";


    String[] columns = {KEY_ROW_ID, KEY_INVOICE_NO, KEY_INVOICE_DATE,KEY_CUSTOMER_NUMBER, KEY_CUSTOMER, KEY_INVOICE_VALUE,KEY_DELIVERY_STATUS ,KEY_DELIVERY_REASON,KEY_IS_ACTIVE};

    private static final String TABLE_NAME = "delivery";


    private static final String INVOICE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_INVOICE_NO + " TEXT NOT NULL,"
            + KEY_INVOICE_DATE + " TEXT NOT NULL,"
            + KEY_CUSTOMER_NUMBER + " TEXT NOT NULL,"
            + KEY_CUSTOMER + " TEXT ,"
            + KEY_INVOICE_VALUE + " TEXT ,"
            + KEY_DELIVERY_STATUS + " INTEGER ,"
            + KEY_DELIVERY_REASON + " TEXT ,"
            + KEY_IS_ACTIVE + " INTEGER "
            + ");";


    private static final String INVOICE_INDEX = "CREATE INDEX  delivery_index ON delivery (row_id)";
    private static final String INVOICE_INDEX2 = "CREATE INDEX  delivery_index_inv ON delivery (invoice_number)";



    public final Context invoiceContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Delivery(Context c) {
        invoiceContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(INVOICE_CREATE);
        database.execSQL(INVOICE_INDEX);
        database.execSQL(INVOICE_INDEX2);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Delivery openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Delivery openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void insertDelivery(String invoiceNumber, String invoiceDate, String customerNumber, String customer,String invoiceValue,int deliveryStatus,String reason) throws SQLException {
        openWritableDatabase();
        database.beginTransaction();

        Cursor cursorProductAvalability = database.rawQuery("SELECT row_id FROM delivery WHERE invoice_number =?", new String[]{invoiceNumber});
        ContentValues cv = new ContentValues();

        if (cursorProductAvalability.getCount() == 0) {
            cv.put(KEY_INVOICE_NO, invoiceNumber);
            cv.put(KEY_INVOICE_DATE, invoiceDate);
            cv.put(KEY_CUSTOMER_NUMBER, customerNumber);
            cv.put(KEY_CUSTOMER, customer);
            cv.put(KEY_INVOICE_VALUE, invoiceValue);
            cv.put(KEY_DELIVERY_STATUS, deliveryStatus);
            cv.put(KEY_DELIVERY_REASON, reason);
            cv.put(KEY_IS_ACTIVE, 1);
            database.insert(TABLE_NAME, null, cv);

        } else {
            cv.put(KEY_DELIVERY_STATUS, deliveryStatus);
            cv.put(KEY_DELIVERY_REASON, reason);
            database.update(TABLE_NAME, cv, KEY_INVOICE_NO + "=?", new String[]{invoiceNumber});

        }
        database.setTransactionSuccessful();
        database.endTransaction();

        cursorProductAvalability.close();
        closeDatabase();

    }



    public ArrayList<DeliveryEntity> getDelivery() throws SQLException {
        openReadableDatabase();
        ArrayList<DeliveryEntity> allDelivery = new ArrayList<>();
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM delivery ", new String[]{});
        cursor.moveToFirst();
        DeliveryEntity temp = null;
        while (!cursor.isAfterLast()) {
            temp = new DeliveryEntity();
            temp.setInvoiceNumber(cursor.getString(1));
            temp.setInvoiceDate(cursor.getString(2));
            temp.setCustomerNumber(cursor.getString(3));
            temp.setCustomer(cursor.getString(4));
            temp.setInvoiceValue(cursor.getString(5));
            temp.setDeliveryStatus(cursor.getInt(6));
            temp.setInvoiceReason(cursor.getString(7));
            allDelivery.add(temp);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return allDelivery;
    }


}

