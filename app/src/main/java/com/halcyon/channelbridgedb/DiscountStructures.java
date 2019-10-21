package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Himanshu on 4/19/2016.
 */
public class DiscountStructures {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_REP_ID = "rep_id";
    private static final String KEY_SEVER_ID = "server_id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_PRINCIPLE = "principle";
    private static final String KEY_CODE = "item_code";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_NQTY = "nqty";
    private static final String KEY_FQTY = "fqty";
    private static final String KEY_IS_ACTIVE = "is_active";


    String[] columns = new String[]{KEY_ROW_ID, KEY_REP_ID,
            KEY_SEVER_ID, KEY_TYPE, KEY_PRINCIPLE,
            KEY_CODE, KEY_DESCRIPTION, KEY_NQTY, KEY_FQTY, KEY_IS_ACTIVE};

    private static final String TABLE_NAME = "discountstructures";
    private static final String DISCOUNT_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_REP_ID + " TEXT, "
            + KEY_SEVER_ID + " TEXT, "
            + KEY_TYPE + " TEXT, "
            + KEY_PRINCIPLE + " TEXT, "
            + KEY_CODE + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_NQTY + " TEXT, "
            + KEY_FQTY + " TEXT, "
            + KEY_IS_ACTIVE + " TEXT "

            + ");";


    public final Context DicountContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DiscountStructures(Context c) {
        DicountContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DISCOUNT_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public DiscountStructures openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(DicountContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public DiscountStructures openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(DicountContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertDiscountStructures(String repid, String serverID, String type, String principle, String itemCode, String description, String nqty, String fqty, String isActive) throws SQLException {
        ;
        ContentValues cv = new ContentValues();
        Cursor c = database.rawQuery("SELECT row_id FROM discountstructures where  item_code = '" + itemCode + "' AND server_id = '" + serverID + "'", null);
        if(c.getCount()==0){
            cv.put(KEY_REP_ID, repid);
            cv.put(KEY_SEVER_ID, serverID);
            cv.put(KEY_TYPE, type);
            cv.put(KEY_PRINCIPLE, principle);
            cv.put(KEY_CODE, itemCode);
            cv.put(KEY_DESCRIPTION, description);
            cv.put(KEY_NQTY, nqty);
            cv.put(KEY_FQTY, fqty);
            cv.put(KEY_IS_ACTIVE, isActive);

            return database.insert(TABLE_NAME, null, cv);
        }else {
            cv.put(KEY_SEVER_ID, serverID);
            cv.put(KEY_TYPE, type);
            cv.put(KEY_NQTY, nqty);
            cv.put(KEY_FQTY, fqty);
            cv.put(KEY_IS_ACTIVE, isActive);
            return database.update(TABLE_NAME, cv, KEY_CODE + "=?" , new String[]{itemCode});
        }


    }


    public int getFreeIssues(String code, int request) {
        openWritableDatabase();
        Cursor c = database.rawQuery("SELECT nqty,fqty FROM discountstructures where  item_code = '" + code + "' AND is_active =1 ORDER BY nqty DESC", null);
        int[] values = new int[c.getCount()];
        int[] qty = new int[c.getCount()];
        int i = 0;
        int free=0;
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                values[i] = Integer.parseInt(c.getString(c.getColumnIndex(KEY_NQTY)));
                qty[i] = Integer.parseInt(c.getString(c.getColumnIndex(KEY_FQTY)));
                int remaning = request / values[i];
                if (remaning==0) {

                }else {
                    free= remaning*qty[i];
                    break;
                }

                i++;
            }
        } else {

        }
        c.close();
        return free;
    }
    public int getDiscountStructuresCount(){
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
