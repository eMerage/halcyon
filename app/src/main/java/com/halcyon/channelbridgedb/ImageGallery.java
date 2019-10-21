package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

  public class ImageGallery {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_IMAGE_ID = "image_id"; //This is the image sequence for one customer
    private static final String KEY_IMAGE_NAME = "image_name"; //This is the image name eg: customerId_imageId --> 1_0
    private static final String KEY_IS_PRIMARY_IMAGE = "is_primary_image";
    private static final String KEY_UPLOAD_STATUS = "upload_status";
    String[] columns = {KEY_ROW_ID, KEY_CUSTOMER_ID, KEY_IMAGE_ID, KEY_IMAGE_NAME, KEY_IS_PRIMARY_IMAGE, KEY_UPLOAD_STATUS};
    private static final String KEY_IMAGE_BLOB = "image_blob";
    private static final String TABLE_NAME = "image_gallery";
    private static final String IMAGE_GALLERY_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CUSTOMER_ID + " TEXT NOT NULL,"
            + KEY_IMAGE_ID + " TEXT ,"
            + KEY_IMAGE_NAME + " TEXT ,"
            + KEY_IS_PRIMARY_IMAGE + " TEXT ,"
            + KEY_UPLOAD_STATUS + " TEXT"
            + " );";
    public final Context imageGalleryContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public ImageGallery(Context c) {
        imageGalleryContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(IMAGE_GALLERY_CREATE);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(database);
    }

    public ImageGallery openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(imageGalleryContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ImageGallery openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(imageGalleryContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertNewImage(String customerId, String imageId, String imageName, String isPrimaryImage, String uploadStatus) throws SQLException {

        ContentValues cv = new ContentValues();

        cv.put(KEY_CUSTOMER_ID, customerId);
        cv.put(KEY_IMAGE_ID, imageId);
        cv.put(KEY_IMAGE_NAME, imageName);
        cv.put(KEY_IS_PRIMARY_IMAGE, isPrimaryImage);
        cv.put(KEY_UPLOAD_STATUS, uploadStatus);

        return database.insert(TABLE_NAME, null, cv);

    }


    public String getLastImageNameForCustomer(String customerId) throws SQLException {
        String query = "SELECT " + KEY_IMAGE_ID + " FROM " + TABLE_NAME + " WHERE " + KEY_CUSTOMER_ID + "=" + customerId;
        Log.w("QUERY", query);
        Cursor cursor = database.rawQuery(query, null);
        String img = "-1";
        boolean status = cursor.moveToPosition(cursor.getCount() - 1);
        if (status) {
            img = cursor.getString(0);
        } else {
            Log.w("ImageGallert DB", "getLastImageNameForCustomer" + status);
        }
        cursor.close();
        return img;
    }


    public void setPrimaryImageByImageId(String imageId) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_IS_PRIMARY_IMAGE + "='true' WHERE " + KEY_IMAGE_NAME + "='" + imageId + "'";
        database.execSQL(query);
    }

    public String getPrimaryImageforCustomerId(String customerId) throws SQLException {

        Cursor cursor=database.rawQuery("SELECT image_name FROM image_gallery WHERE customer_id = ?", new String[]{customerId });

        cursor.moveToFirst();
        String primaryImage = "null";
        if (!(cursor.getCount() == 0)) {
            primaryImage = cursor.getString(0);
        }
        cursor.close();
        return primaryImage;
    }

    public void setPrimaryImageFalse(String imageName) throws SQLException {
        String query = "UPDATE " + TABLE_NAME + " SET " + KEY_IS_PRIMARY_IMAGE + "='false' WHERE " + KEY_IMAGE_NAME + "='" + imageName + "'";
        database.execSQL(query);
    }

    public List<String[]> getImagesByStatus(String status) {
        List<String[]> rtnProducts = new ArrayList<String[]>();
        Log.w("invoice size", "status : " + status);
        Cursor cursor = database.query(TABLE_NAME,
                columns, KEY_UPLOAD_STATUS + " = '" + status + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoiceData = new String[6];
            invoiceData[0] = cursor.getString(0);
            invoiceData[1] = cursor.getString(1);
            invoiceData[2] = cursor.getString(2);
            invoiceData[3] = cursor.getString(3);
            invoiceData[4] = cursor.getString(4);
            invoiceData[5] = cursor.getString(5);

            rtnProducts.add(invoiceData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoice size", "inside : " + rtnProducts.size());

        return rtnProducts;
    }

    public void setImageUploadedStatus(String imageId, String status) {

        String updateQuery = "UPDATE " + TABLE_NAME
                + " SET "
                + KEY_UPLOAD_STATUS
                + " = '"
                + status
                + "' WHERE "
                + KEY_ROW_ID
                + " = "
                + imageId;

        database.execSQL(updateQuery);
        Log.w("Upload service", "<Invoice> Set invoice uploaded status to :" + status + " of id : " + imageId + "");
    }
}
