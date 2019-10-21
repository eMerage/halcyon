package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Sequence {

    private static final String KEY_NAME = "name";
    private static final String KEY_SEQ = "seq";
    public final Context sqliteSequenceContext;
    private final String TABLE_NAME = "sqlite_sequence";
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Sequence(Context c) {
        sqliteSequenceContext = c;
    }

    public Sequence openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(sqliteSequenceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public Sequence openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(sqliteSequenceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public String getLastRowId(String name) {
        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_SEQ}, KEY_NAME + "='" + name + "'", null, null, null, null, null);
        String lastRowId = "null";

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            lastRowId = cursor.getString(0);

        } else {
            lastRowId = "0";
        }
        cursor.close();

        return lastRowId;
    }

    public long setlastInvoiceNumber(String number, String tableName) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_SEQ, number);

        return database.update(TABLE_NAME, cv, KEY_NAME + "=?", new String[]{tableName});


//		String updateQuery = "UPDATE " + TABLE_NAME + " SET "
//				+ KEY_SEQ + " = '" + number + "' WHERE " + KEY_NAME
//				+ " = " + tableName;
//
//		Log.w("updateQuery = ", updateQuery);
//		
//		database.execSQL(updateQuery);

    }


    public long insertSequence(String number, String tableName) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SEQ, number);
        cv.put(KEY_NAME, tableName);

        return database.insert(TABLE_NAME, null, cv);

    }


}
