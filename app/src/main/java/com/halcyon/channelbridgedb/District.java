package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.halcyon.Entity.CustomerEntity;
import com.halcyon.Entity.DistrictEntity;

import java.util.ArrayList;
import java.util.List;

public class District {
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_DistrictID = "districtID";
    private static final String KEY_District = "district";
    private static final String KEY_IS_ACTIVE = "isActive";
    String[] columns = new String[]{KEY_ROW_ID, KEY_DistrictID, KEY_IS_ACTIVE, KEY_District};
    private static final String TABLE_NAME = "districtTAB";

    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_DistrictID + " INTEGER ,"
            + KEY_District + " TEXT ,"
            + KEY_IS_ACTIVE + " INTEGER " + " );";



    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public District(Context c) {
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

    public District openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public District openReadableDatabase() throws SQLException {
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


    public long insert_District(int DistrictID, String District) throws SQLException {
        openWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DistrictID, DistrictID);
        cv.put(KEY_IS_ACTIVE, 1);
        cv.put(KEY_District, District);

        database.insert(TABLE_NAME, null, cv);
        closeDatabase();

        return 0;


    }


    public boolean checkDistrictAvalability() throws SQLException {
        Boolean result = false;

        openReadableDatabase();

        Cursor cursor = database.rawQuery("select * from districtTAB ",null);

        if(cursor.getCount() == 0){
            result = false;
        }else {
            result = true;
        }

        closeDatabase();

        return result;

    }


    public ArrayList<DistrictEntity> getAllDistricts() {
        openReadableDatabase();
        ArrayList<DistrictEntity> districtsList = new  ArrayList<DistrictEntity>();
        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            districtsList.add(new DistrictEntity(cursor.getInt(1),cursor.getString(3)));
            cursor.moveToNext();
        }

        cursor.close();
        closeDatabase();
        return districtsList;
    }


}
