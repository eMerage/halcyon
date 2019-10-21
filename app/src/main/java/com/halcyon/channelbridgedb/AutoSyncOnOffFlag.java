package com.halcyon.channelbridgedb;

/**
 * Created by srinath1983 on 9/18/2014.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;

public class AutoSyncOnOffFlag {
    private static final String KEY_ROW_ID = "row_id";
    String[] columns = new String[]{KEY_ROW_ID};
    private static final String TABLE_NAME = "AutoSync";
    private static final String AUTOSYNC_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER " + " );";
    public final Context AutoSyncContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public AutoSyncOnOffFlag(Context c) {
        AutoSyncContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(AUTOSYNC_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public AutoSyncOnOffFlag openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AutoSyncContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public AutoSyncOnOffFlag openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(AutoSyncContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public String GetAutoSyncStatus() {
        String status = "";
        final String query = "SELECT " + KEY_ROW_ID + " FROM "
                + TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);

        try {
            if (cursor.getCount() == 0)
                status = "0";
            else {
                cursor.moveToFirst();
                status = cursor.getString(0);

            }
        }catch (Exception ex){
            status = "0";

        }




        cursor.close();
        return status;

    }

    public long AutoSyncActive(int status) {
        //database.delete(TABLE_NAME, null, null);
        deleteBit();
        ContentValues cv = new ContentValues();
        cv.put(KEY_ROW_ID, status);
        return database.insert(TABLE_NAME, null, cv);
    }


    public int deleteBit() {

        // String deleteSQL = "DELETE FROM " + TABLE_NAME;
        return database.delete(TABLE_NAME, null, null);
    }

}
