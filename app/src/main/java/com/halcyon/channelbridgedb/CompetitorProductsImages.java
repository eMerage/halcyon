package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CompetitorProductsImages {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_IMAGE_ID = "image_id"; //This is the image sequence for one customer
    private static final String KEY_IMAGE_NAME = "image_name"; //This is the image name eg: customerId_imageId --> 1_0
    private static final String KEY_UPLOAD_STATUS = "upload_status";


    String[] columns = {KEY_ROW_ID, KEY_COMPANY_ID, KEY_IMAGE_ID, KEY_IMAGE_NAME, KEY_UPLOAD_STATUS};
    private static final String TABLE_NAME = "competitor_products_images";
    private static final String COMPETITOR_PRODUCTS_IMAGES_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_COMPANY_ID + " TEXT ,"
            + KEY_IMAGE_ID + " TEXT ,"
            + KEY_IMAGE_NAME + " TEXT ,"
            + KEY_UPLOAD_STATUS + " TEXT "
            + ");";
    public final Context competitorProductsImagesContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CompetitorProductsImages(Context c) {
        competitorProductsImagesContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(COMPETITOR_PRODUCTS_IMAGES_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public CompetitorProductsImages openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(competitorProductsImagesContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public CompetitorProductsImages openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(competitorProductsImagesContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertCompetitorProductImage(String companyId, String imageId, String imageName, String uploadStatus) throws SQLException {

        ContentValues cv = new ContentValues();


        cv.put(KEY_COMPANY_ID, companyId);
        cv.put(KEY_IMAGE_ID, imageId);
        cv.put(KEY_IMAGE_NAME, imageName);
        cv.put(KEY_UPLOAD_STATUS, uploadStatus);

        return database.insert(TABLE_NAME, null, cv);

    }

    public String getLastImageNameForCompetitor(String competitorId) throws SQLException {
        String query = "SELECT " + KEY_IMAGE_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_COMPANY_ID + "=" + competitorId;
        Log.w("QUERY", query);
        Cursor cursor = database.rawQuery(query, null);
        String img = "-1";
        boolean status = cursor.moveToPosition(cursor.getCount() - 1);
        if (status) {
            img = cursor.getString(0);
        } else {
            Log.w("CompetitorImageGallery DB", "getLastImageNameForCompany" + status);
        }
        cursor.close();
        return img;
    }

    public void deleteImageByCompetitorIdAndImageId(String competitorId, String imageId) throws SQLException {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_COMPANY_ID + "=" + competitorId + " AND " + KEY_IMAGE_NAME + "='" + imageId + "'";
        database.execSQL(query);
    }
}
