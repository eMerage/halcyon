package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Amila on 11/27/15.
 */
public class CreditPeriod {


    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_CREDIT_PERIOD = "Credit_Period";
    private static final String KEY_ISACTIVE = "isActive";
    private static final String KEY_COMP_ID = "CompID";

    String[] columns = new String[]{KEY_ROW_ID,KEY_CREDIT_PERIOD,KEY_ISACTIVE,KEY_COMP_ID};

    private static final String TABLE_NAME = "credit_period";
    private static final String ATTENDENCE_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_CREDIT_PERIOD + " INTEGER, "
            + KEY_ISACTIVE + " BOOLEAN, "
            + KEY_COMP_ID + " INTEGER "

            + ");";
    public final Context AttendenceContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CreditPeriod(Context c) {
        AttendenceContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(ATTENDENCE_CREATE);
        Log.i("crrrrrrrrrr->",ATTENDENCE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CreditPeriod openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AttendenceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CreditPeriod openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AttendenceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void addCreditPeriods(com.halcyon.Entity.CreditPeriod period) throws SQLException {

        openWritableDatabase();
        try {

            ContentValues cv = new ContentValues();
            cv.put(KEY_ROW_ID, period.getRowID());
            cv.put(KEY_CREDIT_PERIOD, period.getPeriod());
            cv.put(KEY_ISACTIVE, period.getIsActive());
            cv.put(KEY_COMP_ID, period.getCompId());

            long l = database.insert(TABLE_NAME, null, cv);
        }catch (Exception e){

        }finally {
            closeDatabase();
        }

        //return  l;
     }

    public ArrayList<String> getCreditPeriodList(){

        ArrayList<String> periodList = new  ArrayList<String>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select Credit_Period from credit_period where isActive = ?",new String[]{"1"});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String period = cursor.getString(0);
            periodList.add(period);
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return periodList;
    }

    public int getCreditPeriodCount(){
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

}
