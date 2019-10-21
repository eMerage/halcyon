package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.halcyon.Entity.ReturnHeaderEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Amila on 12/11/15.
 */
public class ReturnHeader {

    private static final String KEY_ROW_ID = "rowId";
    private static final String KEY_INVOICE_NUMBER = "invoiceNumber";
    private static final String KEY_RETURN_DATE = "returnDate";
    private static final String KEY_TOTAL_AMOUNT = "totalAmount";
    private static final String KEY_TOTAL_DISCOUNT_AMOUNT = "discountAmount";
    private static final String KEY_TOTAL_RETURN_QUANTITY = "totalQuantity";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_LONGITIUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_CUSTOMER_N0 = "cutomerNo";
    private static final String KEY_RETURN_INVO = "returnInvoiceNumber";
    private static final String KEY_ISUPLOAD = "isUpload";


    private static final String TABLE_NAME = "return_header";
    private static final String TEMPORARY_INVOICE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + KEY_INVOICE_NUMBER + " TEXT  ,"
            + KEY_RETURN_DATE + " TEXT  ,"
            + KEY_TOTAL_AMOUNT + " TEXT  ,"
            + KEY_TOTAL_DISCOUNT_AMOUNT + " TEXT  ,"
            + KEY_TOTAL_RETURN_QUANTITY + " INTEGER  ,"
            + KEY_START_TIME + " TEXT  ,"
            + KEY_END_TIME + " TEXT  ,"
            + KEY_LONGITIUDE + " TEXT  ,"
            + KEY_LATITUDE + " TEXT  ,"
            + KEY_CUSTOMER_N0 + " TEXT  ,"
            + KEY_RETURN_INVO  + " TEXT  ,"
            + KEY_ISUPLOAD  + " TEXT  "
            +" );";


    public final Context context;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ReturnHeader(Context c) {
        context = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TEMPORARY_INVOICE_CREATE);

        Log.i("rHeader-->", TEMPORARY_INVOICE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }


    public ReturnHeader openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ReturnHeader openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void insertReturnHeader(ReturnHeaderEntity entity)  {

        ContentValues cv = new ContentValues();
        int result = 0;
        try {
            cv.put(KEY_INVOICE_NUMBER, entity.getInvoiceNumber());
            cv.put(KEY_RETURN_DATE ,entity.getReturnDate());
            cv.put(KEY_TOTAL_AMOUNT, entity.getTotalAmount());
            cv.put(KEY_TOTAL_DISCOUNT_AMOUNT,entity.getDiscountAmount());
            cv.put(KEY_TOTAL_RETURN_QUANTITY ,entity.getTotalQuantity());
            cv.put(KEY_START_TIME,entity.getStartTime());
            cv.put(KEY_END_TIME,entity.getEndTime());
            cv.put(KEY_LONGITIUDE ,entity.getLongitude());
            cv.put(KEY_LATITUDE,entity.getLatitude());
            cv.put(KEY_CUSTOMER_N0,entity.getCutomerNo());
            cv.put(KEY_RETURN_INVO,entity.getReturnInvoiceNumber());
            cv.put(KEY_ISUPLOAD,entity.getIsUpload());
            database.insert(TABLE_NAME, null, cv);
        }catch (SQLException e){
            Log.e("Return header error - >",e.toString());
        }
        catch (Exception e){
            Log.e("Return header error - >",e.toString());
        }


    }

    public ArrayList<ReturnHeaderEntity> getNotUploadedHeaders(){
        ArrayList<ReturnHeaderEntity> entities = new ArrayList<>();

        openReadableDatabase();
        String query = "select * from "+TABLE_NAME+" where isUpload = 0";
        ReturnHeaderEntity entity = null;

//        try{
            Cursor cursor = database.rawQuery(query,null);
            cursor.moveToFirst();


            while (!cursor.isAfterLast()) {

                Cursor cursorDetailsCount = database.rawQuery("select row_id from product_returns where invoice_number = ?",new String[]{cursor.getString(cursor.getColumnIndex(KEY_ROW_ID))});

                entity = new ReturnHeaderEntity();
                entity.setId(cursor.getString(cursor.getColumnIndex(KEY_ROW_ID)));
                entity.setInvoiceNumber(cursor.getString(cursor.getColumnIndex(KEY_INVOICE_NUMBER)));
                entity.setDiscountAmount(cursor.getString(cursor.getColumnIndex(KEY_TOTAL_DISCOUNT_AMOUNT)));
                entity.setTotalQuantity(cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_RETURN_QUANTITY)));
                entity.setTotalAmount(cursor.getString(cursor.getColumnIndex(KEY_TOTAL_AMOUNT)));
                entity.setStartTime(cursor.getString(cursor.getColumnIndex(KEY_START_TIME)));
                entity.setEndTime(cursor.getString(cursor.getColumnIndex(KEY_END_TIME)));
                entity.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITIUDE)));
                entity.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                entity.setReturnDate(cursor.getString(cursor.getColumnIndex(KEY_RETURN_DATE)));
                entity.setCutomerNo(cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_N0)));
                entity.setReturnInvoiceNumber(cursor.getString(cursor.getColumnIndex(KEY_RETURN_INVO)));
                entity.setReturnDate(cursor.getString(cursor.getColumnIndex("returnDate")));
                entity.setDetailsCount(cursorDetailsCount.getCount());


                entities.add(entity);
                cursorDetailsCount.close();
                cursor.moveToNext();

            }
            cursor.close();

        closeDatabase();
        return  entities;

    }


    public void updateStatus(String invoId) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = null;
      //  try {
           // openWritableDatabase();
            cv = new ContentValues();
            cv.put(KEY_ISUPLOAD,"1");

            database.update(TABLE_NAME, cv, KEY_ROW_ID + " = ? ", new String[]{invoId});
           // Log.i("updated successfully", productCode + "_" + batchNO);
           // closeDatabase();
//        }catch (SQLException e){
//            Log.e("Temp invoice ---->","Error updating temp request stock");
//        }


    }
    public void SetupdateStatusFalse(String invoId) {
        ContentValues cv = null;
        cv = new ContentValues();
        cv.put(KEY_ISUPLOAD,"0");
        database.update(TABLE_NAME, cv, KEY_ROW_ID + " = ? ", new String[]{invoId});
    }

    public int getToBeUploadeReturn(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select rowId from return_header where isUpload = 0",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }
    public int getUploadedReturn(){
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select rowId from return_header where isUpload = 1",null);
        int count =cursor.getCount();
        cursor.close();
        closeDatabase();
        return count;
    }

    public ArrayList<String> getReturnInvoiceCountByDate() {

        Date date = new Date(System.currentTimeMillis());
        DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
        ArrayList<String> invoiceId = new ArrayList<String>();

        Cursor cur = database.rawQuery("SELECT rowId FROM return_header where returnDate like '%"+df1.format(date).toString()+"%'", null);

        cur.moveToFirst();

        while (!cur.isAfterLast()) {
            invoiceId.add(cur.getString(0));
            cur.moveToNext();
        }
        cur.close();
        return invoiceId;




    }
}
