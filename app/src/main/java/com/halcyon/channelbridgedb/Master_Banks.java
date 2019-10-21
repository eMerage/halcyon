package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Puritha Dev on 12/1/2014.
 */
public class Master_Banks {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ID = "ID";
    private static final String KEY_BankName = "BankName";
    private static final String KEY_ModifyDate = "ModifyDate";
    private static final String KEY_IS_ACTIVE = "IsActive";
    String[] columns = new String[]{KEY_ROW_ID, KEY_ID, KEY_BankName, KEY_IS_ACTIVE, KEY_ModifyDate};
    private static final String TABLE_NAME = "Master_Bank";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_ID + " TEXT ,"
            + KEY_BankName + " TEXT ,"
            + KEY_IS_ACTIVE + " TEXT ,"
            + KEY_ModifyDate + " TEXT "
            + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Master_Banks(Context c) {
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

    public Master_Banks openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Master_Banks openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insert_Master_Banks(String ID, String CreditPeriod, String IsActive, String modify_date) throws SQLException {
        ContentValues cv = new ContentValues();

        cv.put(KEY_ID, ID);
        cv.put(KEY_BankName, CreditPeriod);
        cv.put(KEY_IS_ACTIVE, IsActive);
        cv.put(KEY_ModifyDate, modify_date);

        return database.insert(TABLE_NAME, null, cv);

    }

    public void Deletedata() {
        database.execSQL("delete from " + TABLE_NAME);

    }


    public List<String> GetBank() {
        List<String> loadInvoiceNumberList = new ArrayList();
        try {

            //  String strqu = "select "+KEY_BankName+" from " + TABLE_NAME + " where "+KEY_IS_ACTIVE+"='"+0+"' ";
            String strqu = "select " + KEY_BankName + " from " + TABLE_NAME + " ";
            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {

                    loadInvoiceNumberList.add(cur.getString(0));


                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {


        }


        return loadInvoiceNumberList;
    }


    public ArrayList<String> getBankList(){

        ArrayList<String> bankList = new  ArrayList<String>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select BankName from Master_Bank where isActive = ?",new String[]{"true"});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String period = cursor.getString(0);
            bankList.add(period);
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return  bankList;
    }


}
