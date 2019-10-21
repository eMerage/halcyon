package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Puritha Dev on 11/28/2014.
 */
public class InvoicePaymentType {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_TypeID = "TypeID";
    private static final String KEY_PAYMENT_TYPE = "PaymentType";
    private static final String KEY_ModifyDate = "ModifyDate";
    private static final String KEY_IS_ACTIVE = "IsActive";
    String[] columns = new String[]{KEY_ROW_ID, KEY_TypeID, KEY_PAYMENT_TYPE, KEY_IS_ACTIVE, KEY_ModifyDate};
    private static final String TABLE_NAME = "tb_PaymentType";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TypeID + " TEXT NOT NULL,"
            + KEY_PAYMENT_TYPE + " TEXT ,"
            + KEY_IS_ACTIVE + " TEXT ,"
            + KEY_ModifyDate + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public InvoicePaymentType(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COLLECTION_NOTE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public InvoicePaymentType openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public InvoicePaymentType openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertInvoicePaymentType(String TypeID, String payment_type, String IsActive, String modify_date) throws SQLException {

        ContentValues cv = new ContentValues();


        cv.put(KEY_TypeID, TypeID);
        cv.put(KEY_PAYMENT_TYPE, payment_type);
        cv.put(KEY_IS_ACTIVE, IsActive);
        cv.put(KEY_ModifyDate, modify_date);

        return database.insert(TABLE_NAME, null, cv);

    }

    public void Deletedata() {
        database.execSQL("delete from " + TABLE_NAME);

    }


    public String GetPyementtypeCode(String paytype) {
        String code = "";

        try {
            String strqu = "select " + KEY_TypeID + " from " + TABLE_NAME + " where " + KEY_PAYMENT_TYPE + " ='" + paytype + "' ";
            Log.i("sm->",strqu);
            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {

                    code = cur.getString(0);


                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }
        } catch (Exception e) {

        }


        return code;
    }
}
