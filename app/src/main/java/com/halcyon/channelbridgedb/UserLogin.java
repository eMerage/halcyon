package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserLogin {
    ////
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_STATUS = "user_status";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LOCK = "lock";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_TIME_STAMP = "time_stamp";
    String[] columns = {KEY_ROW_ID, KEY_USER_NAME, KEY_USER_STATUS,
            KEY_USER_PASSWORD, KEY_USER_ID, KEY_LOCK, KEY_DEVICE_ID,
            KEY_TIME_STAMP};
    private static final String TABLE_NAME = "user_login";
    private static final String USER_LOGIN_CREATE = "CREATE TABLE "
            + TABLE_NAME + " (" + KEY_ROW_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USER_NAME
            + " TEXT NOT NULL, " + KEY_USER_STATUS + " TEXT, "
            + KEY_USER_PASSWORD + " TEXT NOT NULL, " + KEY_USER_ID + " TEXT, "
            + KEY_LOCK + " TEXT, " + KEY_DEVICE_ID + " TEXT NOT NULL, "
            + KEY_TIME_STAMP + " TEXT " + ");";
    public final Context userLoginContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public UserLogin(Context c) {
        userLoginContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(USER_LOGIN_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public UserLogin openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(userLoginContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public UserLogin openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(userLoginContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertLogin(String timeStamp, String userName,
                            String userStatus, String userPassword, String userId, String lock,
                            String deviceId) throws SQLException {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put(KEY_USER_NAME, userName);
        cv.put(KEY_USER_STATUS, userStatus);
        cv.put(KEY_USER_PASSWORD, userPassword);
        cv.put(KEY_USER_ID, userId);
        cv.put(KEY_LOCK, lock);
        cv.put(KEY_DEVICE_ID, deviceId);
        cv.put(KEY_TIME_STAMP, timeStamp);
        return database.insert(TABLE_NAME, null, cv);
    }

    public List<String> getAllUsers() {
        List<String> userLoginList = new ArrayList<String>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            userLoginList.add(cursor.getString(1));
            cursor.moveToNext();
        }

        cursor.close();

        return userLoginList;
    }

    public int isUseractive(String userName) {

        Log.w("Log", "userName : " + userName);

        int status = 0;

        try {
            Cursor cursor = database.query(TABLE_NAME, new String[]{
                            KEY_ROW_ID, KEY_USER_NAME, KEY_LOCK}, null, null, null,
                    null, null);

            String stat = "";

            cursor.moveToFirst();

            int iName = cursor.getColumnIndex(KEY_USER_NAME);
            int iStatus = cursor.getColumnIndex(KEY_LOCK);

            while (!cursor.isAfterLast()) {

                Log.w("Log", "iName : " + cursor.getString(iName));
                Log.w("Log", "iStatus : " + cursor.getString(iStatus));

                if (cursor.getString(iName).equals(userName)) {
                    stat = cursor.getString(iStatus);

                    if (stat.equals("N")) {
                        status = 1;
                    } else if (stat.equals("Y")) {
                        status = 2;
                    } else {
                        status = 3;
                    }
                    break;
                }

                cursor.moveToNext();
            }

            cursor.close();


        } catch (Exception e) {
            status = 4;
            Log.w("Log", "Error regarding status: " + e.toString());

        }
        Log.w("Log", "status: " + status);

        return status;
    }


    public String[] getUserDetailsByUserName(String userName) {


        final String REP_TABLE_NAME = "reps";
        final String KEY_REP_ID = "rep_id";

        final String MY_QUERY = "SELECT userLogin." + KEY_USER_ID + ", userLogin."
                + KEY_USER_PASSWORD + ", userLogin." + KEY_DEVICE_ID + ", rep."
                + KEY_REP_ID + ", userLogin." + KEY_ROW_ID + " FROM " + TABLE_NAME + " userLogin INNER JOIN "
                + REP_TABLE_NAME + " rep ON userLogin." + KEY_ROW_ID
                + "=rep." + KEY_USER_ID + " WHERE userLogin." + KEY_USER_NAME
                + "=?";

        Log.w("Itenarary", "Query: " + MY_QUERY);

        Cursor cursor = database.rawQuery(MY_QUERY,
                new String[]{String.valueOf(userName)});

        cursor.moveToFirst();
        String[] data = new String[5];

        while (!cursor.isAfterLast()) {

            data[0] = cursor.getString(0);
            data[1] = cursor.getString(1);
            data[2] = cursor.getString(2);
            data[3] = cursor.getString(3);
            data[4] = cursor.getString(4);

            Log.w("Log", "Cust Name : " + data[2]);
            cursor.moveToNext();
        }

        return data;

    }

    public ArrayList<String> getUserDetailsFromRowId(String rowId) {
        ArrayList<String> userDetails = new ArrayList<String>();

        Cursor cursor = database.query(TABLE_NAME, columns, KEY_ROW_ID + "='" + rowId + "'", null, null, null, null);
        cursor.moveToFirst();

        userDetails.add(cursor.getString(0));
        userDetails.add(cursor.getString(1));
        userDetails.add(cursor.getString(2));
        userDetails.add(cursor.getString(3));
        userDetails.add(cursor.getString(4));
        userDetails.add(cursor.getString(5));
        userDetails.add(cursor.getString(6));
        userDetails.add(cursor.getString(7));
        cursor.close();

        return userDetails;

    }

    public void changeUserPassword(String rowId, String password) {
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_USER_PASSWORD + "='" + password + "'" + " WHERE " + KEY_ROW_ID + "='" + rowId + "'";
        database.execSQL(query);
    }

    public ArrayList<String[]> getAllUsersDetails() {
        ArrayList<String[]> remarks = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] userData = new String[8];
            userData[0] = cursor.getString(0);
            userData[1] = cursor.getString(1);
            userData[2] = cursor.getString(2);
            userData[3] = cursor.getString(3);
            userData[4] = cursor.getString(4);
            userData[5] = cursor.getString(5);
            userData[6] = cursor.getString(6);
            userData[7] = cursor.getString(7);

            remarks.add(userData);
            cursor.moveToNext();
        }

        cursor.close();

        return remarks;
    }

}
