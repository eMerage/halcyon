package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CompetitorProducts {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COMPANY = "company";
    private static final String KEY_PRODUCT = "product";
    private static final String KEY_VALUE = "value";
    private static final String KEY_PACK_SIZES = "pack_sizes";
    private static final String KEY_OTHER_INFO = "other_info";
    private static final String KEY_SEND_TO = "send_to";
    private static final String KEY_TIMESTAMP = "timestamp";


    String[] columns = {KEY_ROW_ID, KEY_COMPANY, KEY_PRODUCT, KEY_VALUE, KEY_PACK_SIZES, KEY_OTHER_INFO, KEY_SEND_TO, KEY_TIMESTAMP};
    private static final String TABLE_NAME = "competitor_products";
    private static final String COMPETITOR_PRODUCTS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COMPANY + " TEXT ,"
            + KEY_PRODUCT + " TEXT ,"
            + KEY_VALUE + " TEXT ,"
            + KEY_PACK_SIZES + " TEXT ,"
            + KEY_OTHER_INFO + " TEXT ,"
            + KEY_SEND_TO + " TEXT ,"
            + KEY_TIMESTAMP + " TEXT );";
    public final Context competitorProductsContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CompetitorProducts(Context c) {
        competitorProductsContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COMPETITOR_PRODUCTS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CompetitorProducts openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(competitorProductsContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CompetitorProducts openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(competitorProductsContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertCompetitorProduct(String company, String product, String value, String packSizes, String otherInfo, String sendTo, String timeStamp) throws SQLException {

        ContentValues cv = new ContentValues();


        cv.put(KEY_COMPANY, company);
        cv.put(KEY_PRODUCT, product);
        cv.put(KEY_VALUE, value);
        cv.put(KEY_PACK_SIZES, packSizes);
        cv.put(KEY_OTHER_INFO, otherInfo);
        cv.put(KEY_SEND_TO, sendTo);
        cv.put(KEY_TIMESTAMP, timeStamp);

        return database.insert(TABLE_NAME, null, cv);

    }

    public List<String[]> getAllCompetitorProducts() {
        List<String[]> competitorProducts = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] productData = new String[8];
            productData[0] = cursor.getString(0);
            productData[1] = cursor.getString(1);
            productData[2] = cursor.getString(2);
            productData[3] = cursor.getString(3);
            productData[4] = cursor.getString(4);
            productData[5] = cursor.getString(5);
            productData[6] = cursor.getString(6);
            productData[7] = cursor.getString(7);

            competitorProducts.add(productData);
            cursor.moveToNext();
        }

        cursor.close();

        return competitorProducts;
    }

}
