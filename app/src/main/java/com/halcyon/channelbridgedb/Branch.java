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
 * Created by Puritha Dev on 12/2/2014.
 */
public class Branch {


    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ID = "ID";
    private static final String KEY_Town = "Town";
    private static final String KEY_DistrictID = "DistrictID";
    private static final String KEY_District = "District";
    private static final String KEY_ModifyDate = "ModifyDate";
    private static final String KEY_IS_ACTIVE = "IsActive";
    String[] columns = new String[]{KEY_ROW_ID, KEY_ID, KEY_DistrictID, KEY_Town, KEY_IS_ACTIVE, KEY_ModifyDate, KEY_District};
    private static final String TABLE_NAME = "Branch";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ID + " TEXT NOT NULL,"
            + KEY_DistrictID + " TEXT ,"
            + KEY_Town + " TEXT ,"
            + KEY_IS_ACTIVE + " TEXT ,"
            + KEY_ModifyDate + " TEXT ,"
            + KEY_District + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Branch(Context c) {
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

    public Branch openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Branch openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public void Deletedata() {
        database.execSQL("delete from " + TABLE_NAME);

    }


    public long insert_Branch(String ID, String DistrictID, String Town, String IsActive, String modify_date, String District) throws SQLException {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, ID);
        cv.put(KEY_DistrictID, DistrictID);
        cv.put(KEY_Town, Town);
        cv.put(KEY_IS_ACTIVE, IsActive);
        cv.put(KEY_ModifyDate, modify_date);
        cv.put(KEY_District, District);

        return database.insert(TABLE_NAME, null, cv);

    }

    public String GetBranchCode(String Branch) {
        openWritableDatabase();
        String code = null;
        String strqu = "select " + KEY_ID + " from " + TABLE_NAME + " where " + KEY_Town + " ='" + Branch + "' ";
        Cursor cur = database.rawQuery(strqu, null);
        if (cur.moveToFirst()) {
            do {

                code = cur.getString(0);


            } while (cur.moveToNext());
        }

        if (cur != null & !cur.isClosed()) {
            cur.close();
        }
        cur.close();
        openWritableDatabase();
        return code;


    }


    public ArrayList<String> getBranchList(){

        ArrayList<String> bankList = new  ArrayList<String>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select Town from Branch where isActive = ?",new String[]{"true"});

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



    public List<String> GetBranchName() {
        List<String> loadInvoiceNumberList = new ArrayList();
        try {

            //  String strqu = "select "+KEY_BankName+" from " + TABLE_NAME + " where "+KEY_IS_ACTIVE+"='"+0+"' ";
            String strqu = "select " + KEY_Town + " from " + TABLE_NAME + "";
            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {

                    if(cur.getString(0)==null){

                    }else {
                        loadInvoiceNumberList.add(cur.getString(0));
                    }



                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {


        }


        return loadInvoiceNumberList;
    }

}
