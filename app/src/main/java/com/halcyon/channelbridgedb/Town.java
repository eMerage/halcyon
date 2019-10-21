package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Town {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_TOWN_ID = "town_id";
    private static final String KEY_TOWN = "town";
    private static final String KEY_DistrictID = "districtID";
    private static final String KEY_IS_ACTIVE = "isActive";
    String[] columns = new String[]{KEY_ROW_ID, KEY_TOWN_ID,KEY_TOWN,KEY_DistrictID, KEY_IS_ACTIVE,};
    private static final String TABLE_NAME = "District";

    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TOWN_ID + " INTEGER ,"
            + KEY_TOWN + " TEXT ,"
            + KEY_DistrictID + "INTEGER ,"
            + KEY_IS_ACTIVE + " INTEGER " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Town(Context c) {
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

    public Town openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Town openReadableDatabase() throws SQLException {
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


    public long insert_Town(int townid,String town,int DistrictID) throws SQLException {


        ContentValues cv = new ContentValues();
        cv.put(KEY_TOWN_ID, townid);
        cv.put(KEY_TOWN, town);
        cv.put(KEY_DistrictID, DistrictID);
        cv.put(KEY_IS_ACTIVE, 1);
        database.insert(TABLE_NAME, null, cv);


        return 0;

    }

}
