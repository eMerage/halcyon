package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Reps {
    //
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_REP_ID = "rep_id";
    private static final String KEY_REP_NAME = "rep_name";
    private static final String KEY_REP_ADDRESS = "rep_address";
    private static final String KEY_REP_NID = "rep_nid";
    private static final String KEY_REP_HIRE_DATE = "rep_hire_date";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_REP_TYPE = "rep_type";
    private static final String KEY_DEALER_ID = "dealer_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TIME_STAMP = "time_stamp";
    private static final String KEY_NAME = "name";
    private static final String KEY_TOWN = "town";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_COORDINATORS_TP = "coordinators_number";

    String[] columns = new String[]{KEY_ROW_ID, KEY_REP_ID, KEY_REP_NAME,
            KEY_REP_ADDRESS, KEY_REP_NID, KEY_REP_HIRE_DATE, KEY_IS_ACTIVE, KEY_REP_TYPE, KEY_DEALER_ID, KEY_USER_ID, KEY_TIME_STAMP,
            KEY_NAME, KEY_TOWN, KEY_TELEPHONE,KEY_COORDINATORS_TP};

    private static final String USER_LOGIN_TABLE = "user_login";

    private static final String TABLE_NAME = "reps";
    private static final String REPS_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_REP_ID + " TEXT NOT NULL, "
            + KEY_REP_NAME + " TEXT NOT NULL, "
            + KEY_REP_ADDRESS + " TEXT, "
            + KEY_REP_NID + " TEXT NOT NULL, "
            + KEY_REP_HIRE_DATE + " TEXT, "
            + KEY_IS_ACTIVE + " TEXT NOT NULL, "
            + KEY_REP_TYPE + " TEXT NOT NULL, "
            + KEY_DEALER_ID + " TEXT NOT NULL, "
            + KEY_USER_ID + " INTEGER NOT NULL, "
            + KEY_TIME_STAMP + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_TOWN + " TEXT, "
            + KEY_TELEPHONE + " TEXT, "
            + KEY_COORDINATORS_TP + " TEXT, "
            + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + USER_LOGIN_TABLE + "(" + KEY_ROW_ID + ")"
            + " );";
    public final Context repContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Reps(Context c) {
        repContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(REPS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Reps openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(repContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Reps openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(repContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertRep(String repId, String repName, String repAddress, String repNId, String repHireDate,
                          String isActive, String repType, String dealerId, int userId, String timeStamp, String name, String town,
                          String telephone,String coordinatordtp) throws SQLException {


        Cursor cursor = database.rawQuery("SELECT row_id FROM reps WHERE rep_id =? ", new String[]{repId});
        ContentValues cv = new ContentValues();

        if(cursor.getCount()>0){
            cv.put(KEY_REP_NAME, repName);
            cv.put(KEY_REP_ADDRESS, repAddress);
            cv.put(KEY_REP_NID, repNId);
            cv.put(KEY_REP_HIRE_DATE, repHireDate);
            cv.put(KEY_IS_ACTIVE, isActive);
            cv.put(KEY_REP_TYPE, repType);
            cv.put(KEY_DEALER_ID, dealerId);
            cv.put(KEY_TIME_STAMP, timeStamp);
            cv.put(KEY_NAME, name);
            cv.put(KEY_TOWN, town);
            cv.put(KEY_TELEPHONE, telephone);
            cv.put(KEY_COORDINATORS_TP, coordinatordtp);
            return database.update(TABLE_NAME, cv, KEY_REP_ID + "=?" , new String[]{repId});


        }else {

            cv.put(KEY_REP_ID, repId);
            cv.put(KEY_REP_NAME, repName);
            cv.put(KEY_REP_ADDRESS, repAddress);
            cv.put(KEY_REP_NID, repNId);
            cv.put(KEY_REP_HIRE_DATE, repHireDate);
            cv.put(KEY_IS_ACTIVE, isActive);
            cv.put(KEY_REP_TYPE, repType);
            cv.put(KEY_DEALER_ID, dealerId);
            cv.put(KEY_USER_ID, userId);
            cv.put(KEY_TIME_STAMP, timeStamp);
            cv.put(KEY_NAME, name);
            cv.put(KEY_TOWN, town);
            cv.put(KEY_TELEPHONE, telephone);
            cv.put(KEY_COORDINATORS_TP, coordinatordtp);

            return database.insert(TABLE_NAME, null, cv);

        }


    }


    public String getRepNameByRepId(String repId) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_REP_NAME}, KEY_REP_ID + "='" + repId + "'", null, null, null, null);

        cursor.moveToFirst();

        String repName = cursor.getString(0);
        cursor.close();
        return repName;
    }

    public ArrayList<String[]> getAllRepsDetails() {
        ArrayList<String[]> reps = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] userData = new String[11];
            userData[0] = cursor.getString(0);
            userData[1] = cursor.getString(1);
            userData[2] = cursor.getString(2);
            userData[3] = cursor.getString(3);
            userData[4] = cursor.getString(4);
            userData[5] = cursor.getString(5);
            userData[6] = cursor.getString(6);
            userData[7] = cursor.getString(7);
            userData[8] = cursor.getString(8);
            userData[9] = cursor.getString(9);
            userData[10] = cursor.getString(10);

            reps.add(userData);
            cursor.moveToNext();
        }

        cursor.close();

        return reps;
    }

    public String[] getRepDetails() {
        String[] userData = new String[11];

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            userData[0] = cursor.getString(0);
            userData[1] = cursor.getString(1);
            userData[2] = cursor.getString(2);
            userData[3] = cursor.getString(3);
            userData[4] = cursor.getString(4);
            userData[5] = cursor.getString(5);
            userData[6] = cursor.getString(6);
            userData[7] = cursor.getString(7);
            userData[8] = cursor.getString(8);
            userData[9] = cursor.getString(9);
            userData[10] = cursor.getString(10);

          //  reps.add(userData);
            cursor.moveToNext();
        }

        cursor.close();

        return userData;
    }

    public ArrayList<String> getRepDetailsForPrinting(String repId) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_REP_NAME, KEY_NAME, KEY_TOWN, KEY_TELEPHONE}, KEY_REP_ID + "=?", new String[]{repId}, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> repDetailsForPriniting = new ArrayList<String>();

        if (cursor.getCount() != 0) {
            repDetailsForPriniting.add(cursor.getString(0));
            repDetailsForPriniting.add(cursor.getString(1));
            repDetailsForPriniting.add(cursor.getString(2));
            repDetailsForPriniting.add(cursor.getString(3));
        }
        cursor.close();
        return repDetailsForPriniting;
    }


    public String getASM(String repId) {

        openReadableDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_COORDINATORS_TP}, KEY_REP_ID + "='" + repId + "'", null, null, null, null);

        cursor.moveToFirst();

        String asmNumber = cursor.getString(0);
        cursor.close();
        closeDatabase();
        return asmNumber;
    }


}
