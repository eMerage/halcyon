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
 * Created by srinath1983 on 9/25/2014.
 */
public class Rep_GPS {


    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_DeviceId = "DeviceId";
    private static final String KEY_Latitude = "Latitude";
    private static final String KEY_Longitude = "Longitude";
    private static final String KEY_GetDate = "GetDate";
    private static final String KEY_IsUpLoad = "IsUpload";

    String[] columns = {KEY_ROW_ID, KEY_DeviceId, KEY_Latitude, KEY_Longitude, KEY_GetDate, KEY_IsUpLoad};
    private static final String TABLE_NAME = "Rep_GPS";
    private static final String Rep_GPS_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_DeviceId + " TEXT, "
            + KEY_Latitude + " TEXT, "
            + KEY_Longitude + " TEXT, "
            + KEY_GetDate + " TEXT, "
            + KEY_IsUpLoad + " TEXT "

            + ");";
    public final Context Rep_GPS;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Rep_GPS(Context c) {
        Rep_GPS = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(Rep_GPS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Rep_GPS openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(Rep_GPS);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Rep_GPS openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(Rep_GPS);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertGPS(String Latitude, String Longitude, String GetDate, String deviceid) throws SQLException {

        ;


        // String deviceid="";
        ContentValues cv = new ContentValues();
        cv.put(KEY_DeviceId, deviceid);
        cv.put(KEY_Latitude, Latitude);
        cv.put(KEY_Longitude, Longitude);
        cv.put(KEY_GetDate, GetDate);
        cv.put(KEY_IsUpLoad, "0");
        return database.insert(TABLE_NAME, null, cv);

    }

    public List<String[]> getGPS(String status) {
        List<String[]> GPS = new ArrayList<String[]>();


        Log.w("invoice size", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_IsUpLoad + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] GPSData = new String[5];
            GPSData[0] = cursor.getString(0);
            GPSData[1] = cursor.getString(1);
            GPSData[2] = cursor.getString(2);
            GPSData[3] = cursor.getString(3);
            GPSData[4] = cursor.getString(4);

            GPS.add(GPSData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + GPS.size());

        return GPS;
    }

    public int deleteGPSByRowId(String rowId) {

        return database.delete(TABLE_NAME, KEY_ROW_ID + "=?",
                new String[]{rowId});
    }


}
