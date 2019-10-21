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
 * Created by srinath1983 on 9/30/2014.
 */
public class Attendence {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_INOUT_TIME = "inOut_time";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_FLAG = "flag";
    private static final String KEY_DATE = "modifydate";
    private static final String KEY_IS_UPDATE = "IsUpdate";
    String[] columns = new String[]{KEY_ROW_ID, KEY_DEVICE_ID,
            KEY_INOUT_TIME, KEY_LATITUDE, KEY_LONGITUDE,
            KEY_LOCATION, KEY_COMMENTS, KEY_FLAG, KEY_DATE, KEY_IS_UPDATE};

    private static final String TABLE_NAME = "Attendence";
    private static final String ATTENDENCE_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_DEVICE_ID + " TEXT, "
            + KEY_INOUT_TIME + " TEXT, "
            + KEY_LATITUDE + " TEXT, "
            + KEY_LONGITUDE + " TEXT, "
            + KEY_LOCATION + " TEXT, "
            + KEY_COMMENTS + " TEXT, "
            + KEY_FLAG + " TEXT, "
            + KEY_DATE + " TEXT, "
            + KEY_IS_UPDATE + " TEXT "

            + ");";
    public final Context AttendenceContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Attendence(Context c) {
        AttendenceContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(ATTENDENCE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Attendence openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AttendenceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Attendence openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AttendenceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertRepAttendence(String deviceid, String INtime, String Latitude, String Longitude, String Location, String INComments, String GetDate, String flag) throws SQLException {

        ;

        ContentValues cv = new ContentValues();
        cv.put(KEY_DEVICE_ID, deviceid);
        cv.put(KEY_INOUT_TIME, INtime);
        cv.put(KEY_LATITUDE, Latitude);
        cv.put(KEY_LONGITUDE, Longitude);
        cv.put(KEY_LOCATION, Location);
        cv.put(KEY_COMMENTS, INComments);
        cv.put(KEY_FLAG, flag);
        cv.put(KEY_DATE, GetDate);
        cv.put(KEY_IS_UPDATE, "0");
        return database.insert(TABLE_NAME, null, cv);

    }

    public boolean isInActive(String location, String date) {

        //  final String MY_QUERY = "SELECT 1 FROM "+TABLE_NAME+ " WHERE "+KEY_LOCATION+"='" + location + "' AND " + KEY_DATE + "='" + date + "' AND " + KEY_FLAG + "='1' AND " + KEY_IS_UPDATE + "='0' "  ;
        final String MY_QUERY = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + KEY_LOCATION + "='" + location + "' AND " + KEY_DATE + "='" + date + "' AND " + KEY_FLAG + "='1' ";
        Cursor cursor = database.rawQuery(MY_QUERY, null);


        boolean isAvailable = false;
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            isAvailable = false;
        } else {
            isAvailable = true;
        }
        cursor.close();
        return isAvailable;
    }

    public boolean isOutActive(String location, String date) {

        // final String MY_QUERY = "SELECT 1 FROM "+TABLE_NAME+ " WHERE "+KEY_LOCATION+"='" + location + "' AND " + KEY_DATE + "='" + date + "' AND " + KEY_FLAG + "='0' AND " + KEY_IS_UPDATE + "='0' "  ;
        final String MY_QUERY = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + KEY_LOCATION + "='" + location + "' AND " + KEY_DATE + "='" + date + "' AND " + KEY_FLAG + "='0' ";
        Cursor cursor = database.rawQuery(MY_QUERY, null);


        boolean isAvailable = false;
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            isAvailable = false;
        } else {
            isAvailable = true;
        }
        cursor.close();
        return isAvailable;
    }


    public List<String[]> getAttendence(String status) {
        List<String[]> Attendence = new ArrayList<String[]>();


        Log.w("invoice size", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_IS_UPDATE + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] GPSData = new String[9];
            GPSData[0] = cursor.getString(0);
            GPSData[1] = cursor.getString(1);
            GPSData[2] = cursor.getString(2);
            GPSData[3] = cursor.getString(3);
            GPSData[4] = cursor.getString(4);
            GPSData[5] = cursor.getString(5);
            GPSData[6] = cursor.getString(6);
            GPSData[7] = cursor.getString(7);
            GPSData[8] = cursor.getString(8);
            Attendence.add(GPSData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + Attendence.size());

        return Attendence;
    }


    public long UpdateAttendence(String rowid, String date, String flag) {


        ContentValues cv = new ContentValues();
        cv.put(KEY_IS_UPDATE, flag);
        return database.update(TABLE_NAME, cv, KEY_ROW_ID + "=? AND " + KEY_DATE + "=?", new String[]{rowid, date});


    }

    /**
     *  get list who did not upload the attendence
     * @return
     */

    public ArrayList<String>  getAttendenceNotUpload() {

        final String query = "SELECT " + KEY_DEVICE_ID+ " FROM "
                + TABLE_NAME + " where " + KEY_IS_UPDATE+ " =  '0'  ";
        Cursor cursor = database.rawQuery(query, null);
        ArrayList<String> isUpdateList = new ArrayList<String>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            isUpdateList.add(cursor.getString(0));
            Log.w("is upload", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return isUpdateList;
    }
}
