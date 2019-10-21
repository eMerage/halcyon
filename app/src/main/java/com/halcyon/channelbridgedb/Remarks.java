package com.halcyon.channelbridgedb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Remarks {
    //
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_ITINERARY_ID = "itinerary_id";
    private static final String KEY_REMARK = "remark";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_ITINERARY_DATE = "Itinerary_Date";
    private static final String KEY_CUSTOMERID = "CustomerID";
    private static final String KEY_REPID = "RepID";
    private static final String KEY_REMARKTYPE = "RemarkType";
    private static final String KEY_ISUPLOAD = "IsUpload";
    private static final String KEY_ITINERARY_ID_SERVER = "itinerary_id_server";
    private static final String KEY_ISACTIVE = "IsActive";
    private static final String KEY_LONGTIUDE = "longitude";
    private static final String KEY_LATITUDE  = "latitude";


    String[] columns = {KEY_ROW_ID, KEY_ITINERARY_ID, KEY_REMARK, KEY_TIMESTAMP,KEY_ITINERARY_DATE,KEY_CUSTOMERID,KEY_REPID,KEY_REMARKTYPE,KEY_ISUPLOAD,KEY_ITINERARY_ID_SERVER,KEY_ISACTIVE,KEY_LONGTIUDE,KEY_LATITUDE };

    private static final String TABLE_NAME = "remarks";
    private static final String ITINERARY_TABLE_NAME = "itinerary";
    private static final String REMARKS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ITINERARY_ID + " TEXT NOT NULL ,"
            + KEY_REMARK + " TEXT NOT NULL ,"
            + KEY_TIMESTAMP + " TEXT NOT NULL, "
            + KEY_ITINERARY_DATE + " TEXT NOT NULL, "
            + KEY_CUSTOMERID + " TEXT NOT NULL, "
            + KEY_REPID + " TEXT NOT NULL, "
            + KEY_REMARKTYPE + " TEXT NOT NULL, "
            + KEY_ISUPLOAD + " TEXT NOT NULL, "
            + KEY_ITINERARY_ID_SERVER + " TEXT NOT NULL, "
            + KEY_ISACTIVE + " INTEGER NOT NULL, "
            + KEY_LONGTIUDE  + " TEXT NOT NULL, "
            + KEY_LATITUDE  + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + KEY_ITINERARY_ID + ") REFERENCES " + ITINERARY_TABLE_NAME + "(" + KEY_ROW_ID + ")"
            + " );";
    public final Context remarksContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Remarks(Context c) {
        remarksContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(REMARKS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Remarks openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(remarksContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Remarks openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(remarksContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertRemark(String itineraryId, String remark, String timeStamp,String itDate,String cusId,String repId,String remarkType,String isUpload,String serverItID,String longs,String lati) throws SQLException {

        ContentValues cv = new ContentValues();


        final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMERID + "='" + cusId + "' AND  " + KEY_TIMESTAMP + "='" + timeStamp + "' ";

        Cursor cursor = database.rawQuery(query, null);
        int count = cursor.getCount();

        if(count==0){
            cv.put(KEY_ITINERARY_ID, itineraryId);
            cv.put(KEY_REMARK, remark);
            cv.put(KEY_TIMESTAMP, timeStamp);
            cv.put(KEY_ITINERARY_DATE,itDate);
            cv.put(KEY_CUSTOMERID,cusId);
            cv.put(KEY_REPID,repId);
            cv.put(KEY_REMARKTYPE,remarkType);
            cv.put(KEY_ISUPLOAD,isUpload);
            cv.put(KEY_ITINERARY_ID_SERVER,serverItID);
            cv.put(KEY_ISACTIVE,0);
            cv.put(KEY_LONGTIUDE,longs);
            cv.put(KEY_LATITUDE,lati);

            return database.insert(TABLE_NAME, null, cv);
        }else {

            return -1;
        }



    }


    public ArrayList<String[]> getRemarkDetailsByItineraryId(String id) {

        ArrayList<String[]> remarkDetails = new ArrayList<String[]>();

        final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ITINERARY_ID + "='" + id + "' ORDER BY " + KEY_ROW_ID + " DESC";
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] remark = new String[10];
            remark[0] = cursor.getString(0);
            remark[1] = cursor.getString(1);
            remark[2] = cursor.getString(2);
            remark[3] = cursor.getString(3);
            remark[4] = cursor.getString(4);
            remark[5] = cursor.getString(5);
            remark[6] = cursor.getString(6);
            remark[7] = cursor.getString(7);
            remark[8] = cursor.getString(8);
            remark[8] = cursor.getString(9);
            remarkDetails.add(remark);
            cursor.moveToNext();
        }
        cursor.close();

        return remarkDetails;

    }

    public List<String[]> getRemarksZer0(String status) {
        List<String[]> remarksList = new ArrayList<String[]>();


        Log.w("invoice size", "status : " + status);

//		Cursor cursor = database.query(TABLE_NAME,
//				columns, KEY_UPLOADED_STATUS+" = ?", new String[]{status}, null, null, null);

        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_ISUPLOAD + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] remarksData = new String[12];
            remarksData[0] = cursor.getString(0);
            remarksData[1] = cursor.getString(1);
            remarksData[2] = cursor.getString(2);
            remarksData[3] = cursor.getString(3);
            remarksData[4] = cursor.getString(4);
            remarksData[5] = cursor.getString(5);
            remarksData[6] = cursor.getString(6);
            remarksData[7] = cursor.getString(7);
            remarksData[8] = cursor.getString(8);
            remarksData[9] = cursor.getString(9);
            remarksData[10] = cursor.getString(10);
            remarksData[11] = cursor.getString(11);
            remarksList.add(remarksData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("remarksList size", "inside : " + remarksList.size());

        return remarksList;
    }

    /**
     *
     * remarks did not upload = 0
     * remarks uploaded = 1
     *
     * @return
     */
    public long updateRemarksUpload(String rowId) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = new ContentValues();
        cv.put(KEY_ISUPLOAD , "1");

        return database.update(TABLE_NAME, cv, KEY_ROW_ID + " = ?", new String[]{rowId});

    }



    public long updateRemarks(String CustomerID) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ISACTIVE , 1);

        return database.update(TABLE_NAME, cv, KEY_CUSTOMERID + " = ?", new String[]{CustomerID});

    }



    public boolean checkRemarkCount(String CustomerID){

        openReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMERID + "='" + CustomerID + "' AND  " + KEY_ISACTIVE + "='" + 0 + "' ";
        Cursor cursor = database.rawQuery(query, null);
        int count = cursor.getCount();
        closeDatabase();
        cursor.close();
        if(count==3 || count>3){
            return true;
        }else {
            return false;
        }

    }


    public boolean checkRemarkAvalability(int itenID){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = mdformat.format(calendar.getTime());
        openReadableDatabase();



        final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ITINERARY_ID + "='" + itenID + "' AND  " + KEY_TIMESTAMP + "='" + strDate + "' ";
        Cursor cursor = database.rawQuery(query, null);
        int count = cursor.getCount();
        closeDatabase();
        cursor.close();

        if(count>=1 && count<3){
            return true;
        }else {
            return false;
        }


    }


}
