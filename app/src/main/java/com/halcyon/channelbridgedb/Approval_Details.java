package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Himanshu on 3/15/2016.
 */
public class Approval_Details {
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_CUSTOMER_NO = "customer_no";
    private static final String KEY_DATE = "date";
    private static final String KEY_CODE = "code";
    private static final String KEY_REASON = "reason";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_IS_ACCESS = "is_access";
    private static final String KEY_ACCESS_DATE = "access_date";
    private static final String KEY_APPROVAL_PERSON = "approval_person";
    private static final String KEY_SENT_DATE = "sent_date";
    private static final String KEY_IS_UPLOAD = "is_upload";


    String[] columns = new String[]{KEY_ROW_ID, KEY_CUSTOMER_NO, KEY_DATE, KEY_CODE, KEY_REASON, KEY_COMMENT, KEY_IS_ACCESS, KEY_ACCESS_DATE, KEY_APPROVAL_PERSON,
            KEY_SENT_DATE, KEY_IS_UPLOAD};


    private static final String TABLE_NAME = "approvaldetails";

    private static final String APPROVE_DETAILS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CUSTOMER_NO + " TEXT NOT NULL,"
            + KEY_DATE + " TEXT ,"
            + KEY_CODE + " TEXT ,"
            + KEY_REASON + " TEXT ,"
            + KEY_COMMENT + " TEXT ,"
            + KEY_IS_ACCESS + " INTEGER ,"
            + KEY_ACCESS_DATE + " TEXT ,"
            + KEY_APPROVAL_PERSON + " TEXT ,"
            + KEY_SENT_DATE + " TEXT ,"
            + KEY_IS_UPLOAD + " INTEGER " + " );"; //add customer image  sk


    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Approval_Details(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(APPROVE_DETAILS_CREATE);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

    }

    public Approval_Details openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Approval_Details openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertDetails(String customerNo, String code, String reason, String comment, String person) throws SQLException {

        ContentValues cv = new ContentValues();
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        cv.put(KEY_CUSTOMER_NO, customerNo);
        cv.put(KEY_DATE, dateFormat.format(date));
        cv.put(KEY_CODE, code);
        cv.put(KEY_REASON, reason);
        cv.put(KEY_COMMENT, comment);

        cv.put(KEY_IS_ACCESS, 0);
        cv.put(KEY_APPROVAL_PERSON, person);
        cv.put(KEY_SENT_DATE, dateFormat.format(date));
        cv.put(KEY_IS_UPLOAD, 0);


        return database.insert(TABLE_NAME, null, cv);

    }

    public int checkAccessibility(String id) {
        Cursor c = database.rawQuery("SELECT row_id FROM approvaldetails where customer_no ='" + id + "' AND is_access =0", null);
        System.out.println("count " + c.getCount());
        int result = 0;
        if (c.getCount() == 0) {
            result = 0;
        } else {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(c.getColumnIndex(KEY_ROW_ID));

            }

        }
        c.close();
        System.out.println(result);
        return result;
    }

    public boolean checkCode(String id, String code) {
        Cursor c = database.rawQuery("SELECT row_id FROM approvaldetails where customer_no ='" + id + "' AND code ='" + code + "' ", null);
        if (c.getCount() == 0) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }


    public boolean checkCode(String code) {

        int isAcces = 0;
        Cursor c = null;
        try {
            c = database.rawQuery("SELECT row_id FROM approvaldetails where code ='" + code + "' AND is_access ='" + isAcces + "' ", null);
            if (c.getCount() == 0) {
                c.close();
                return false;
            } else {
                c.close();
                return true;
            }
        } catch (NullPointerException nullpointer) {
            return false;
        }


    }

    public boolean setAccess(String id, String code) {
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        ContentValues v = new ContentValues();
        v.put(KEY_IS_ACCESS, 1);
        v.put(KEY_ACCESS_DATE, dateFormat.format(date));
        database.update(TABLE_NAME, v, KEY_CUSTOMER_NO + " = ? AND " + KEY_CODE + " = ?", new String[]{id, code});
        database.close();
        return true;
    }

    public List<String[]> getApprovaDetails() {

        List<String[]> approvalDataList = new ArrayList<String[]>();
        Cursor c = database.rawQuery("SELECT * FROM approvaldetails where is_access = 1 AND is_upload = 0 ", null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            String[] remarksData = new String[12];
            remarksData[0] = c.getString(0);
            remarksData[1] = c.getString(1);
            remarksData[2] = c.getString(2);
            remarksData[3] = c.getString(3);
            remarksData[4] = c.getString(4);
            remarksData[5] = c.getString(5);
            remarksData[6] = c.getString(6);
            remarksData[7] = c.getString(7);
            remarksData[8] = c.getString(8);
            remarksData[9] = c.getString(9);
            approvalDataList.add(remarksData);
            c.moveToNext();
        }

        c.close();


        return approvalDataList;
    }

    public boolean checkIsApprove(String cusId) {


        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


        openWritableDatabase();
        Cursor c = database.rawQuery("SELECT row_id FROM approvaldetails where customer_no ='" + cusId + "' AND is_access =1 AND sent_date ='" + dateFormat.format(date) + "' ORDER BY row_id DESC LIMIT 1", null);

        if (c.getCount() == 0) {
            c.close();
            closeDatabase();
            return false;
        } else {
            c.close();
            closeDatabase();
            return true;
        }


    }


    public boolean updateCode(int id, String code) {

        ContentValues v = new ContentValues();
        v.put(KEY_CODE, code);
        database.update(TABLE_NAME, v, KEY_ROW_ID + "=" + id, null);
        return true;
    }

    public boolean updateUploadStstus(String id) {

        ContentValues v = new ContentValues();
        v.put(KEY_IS_UPLOAD, 1);
        database.update(TABLE_NAME, v, KEY_ROW_ID + "=" + id, null);

        return true;
    }


    public boolean updateAccessStstus(String code) {
        openWritableDatabase();
        Cursor c = database.rawQuery("SELECT row_id FROM approvaldetails where code ='" + code + "' ", null);
        c.moveToFirst();

        if(c.getCount()==0){
            return false;
        }else {
            ContentValues v = new ContentValues();
            v.put(KEY_IS_ACCESS, 1);
            database.update(TABLE_NAME, v, KEY_ROW_ID+ "=" + c.getString(0), null);
            c.close();
            closeDatabase();

            return true;
        }

    }
}

