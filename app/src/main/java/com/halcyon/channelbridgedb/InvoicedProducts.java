package com.halcyon.channelbridgedb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InvoicedProducts {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_INVOICE_ID = "invoice_id";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_BATCH_NO = "batch_no";
    private static final String KEY_REQUEST_QTY = "request_qty";
    private static final String KEY_FREE = "free";
    private static final String KEY_DISCOUNT = "discount";
    private static final String KEY_NORMAL = "normal";
    private static final String KEY_DATE = "date";
    private static final String KEY_PRICE = "price";
    private static final String KEY_FREE_SYSTEM = "free_system";

    private static final String KEY_EXP = "expiry_date";
    private static final String KEY_PPRICE= "price_purchase";
    private static final String KEY_REPRICE= "price_retail";

    String[] columns = {KEY_ROW_ID, KEY_INVOICE_ID, KEY_PRODUCT_CODE, KEY_BATCH_NO, KEY_REQUEST_QTY, KEY_FREE,KEY_FREE_SYSTEM, KEY_DISCOUNT, KEY_NORMAL, KEY_DATE, KEY_PRICE,KEY_EXP,KEY_PPRICE,KEY_REPRICE};

    private static final String TABLE_NAME = "invoiced_product";
    private static final String INVOICE_TABLE = "invoice";
    private static final String INVOICED_PRODUCTS_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_INVOICE_ID + " TEXT NOT NULL,"
            + KEY_PRODUCT_CODE + " TEXT ,"
            + KEY_BATCH_NO + " TEXT ,"
            + KEY_REQUEST_QTY + " TEXT ,"
            + KEY_FREE + " TEXT ,"
            + KEY_DISCOUNT + " TEXT ,"
            + KEY_NORMAL + " TEXT NULL ,"
            + KEY_DATE + " TEXT NULL ,"
            + KEY_PRICE + " TEXT NULL ,"
            + KEY_FREE_SYSTEM + " TEXT NULL ,"
            + KEY_EXP + " TEXT NULL ,"
            + KEY_PPRICE + " TEXT NULL ,"
            + KEY_REPRICE + " TEXT NULL ,"
            + "FOREIGN KEY(" + KEY_INVOICE_ID + ") REFERENCES " + INVOICE_TABLE + "(" + KEY_ROW_ID + ")"
            + " );";
    public final Context invoicedProductContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public InvoicedProducts(Context c) {
        invoicedProductContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(INVOICED_PRODUCTS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public InvoicedProducts openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoicedProductContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public InvoicedProducts openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoicedProductContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertInvoicedProducts(String invoiceId, String productCode, String batchNo, String requestQty, String free, String discount, String normal, String date, String price,String systemFree,String exe, String pprice, String rprice) throws SQLException {

        ContentValues cv = new ContentValues();

        String d = "'" + date + "'";


        cv.put(KEY_INVOICE_ID, invoiceId);
        cv.put(KEY_PRODUCT_CODE, productCode);
        cv.put(KEY_BATCH_NO, batchNo);
        cv.put(KEY_REQUEST_QTY, requestQty);
        cv.put(KEY_FREE, free);
        cv.put(KEY_DISCOUNT, discount);
        cv.put(KEY_NORMAL, normal);
        cv.put(KEY_DATE, d);
        cv.put(KEY_PRICE, price);
        cv.put(KEY_FREE_SYSTEM, systemFree);
        cv.put(KEY_EXP, exe);
        cv.put(KEY_PPRICE, pprice);
        cv.put(KEY_REPRICE, rprice);

        return database.insert(TABLE_NAME, null, cv);

    }


    public List<String[]> getInvoicedProductsByInvoiceId(String invoiceId) {
        List<String[]> invoicedProducts = new ArrayList<String[]>();


           final String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_INVOICE_ID + "='" + invoiceId + "' " ;

        Cursor cursor = database.rawQuery(query, null);

     //   Cursor cursor = database.query(TABLE_NAME, columns, KEY_INVOICE_ID + " = '" + invoiceId + "'", null, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoicedProductData = new String[16];
            invoicedProductData[0] = cursor.getString(0);
            invoicedProductData[1] = cursor.getString(1);
            invoicedProductData[2] = cursor.getString(2);
            invoicedProductData[3] = cursor.getString(3);
            invoicedProductData[4] = cursor.getString(4);
            invoicedProductData[5] = cursor.getString(5);
            invoicedProductData[6] = cursor.getString(6);
            invoicedProductData[7] = cursor.getString(7);
            invoicedProductData[8] = cursor.getString(8);
            invoicedProductData[9] = cursor.getString(9);
            invoicedProductData[10] = cursor.getString(10);

            invoicedProductData[11] = cursor.getString(11);
            invoicedProductData[12] = cursor.getString(12);
            invoicedProductData[13] = cursor.getString(13);



            invoicedProducts.add(invoicedProductData);
            cursor.moveToNext();
        }

        cursor.close();

        Log.w("invoicedProducts size", "inside : " + invoicedProducts.size());

        return invoicedProducts;
    }

    public ArrayList<String[]> getInvoicesByItineraryDate(String itineraryId) throws SQLException {
        Log.w("invoicedP", " getInvoicesByItineraryDate");
        ArrayList<String[]> invoiceData = new ArrayList<String[]>();

        final String PRODUCT_TABLE = "products";
        final String INVOICE_TABLE = "invoice";

        final String PRODUCT_DESCRIPTION = "pro_des";
        final String TOTAL_AMOUNT = "total_amount";
        final String ITINERARY_ID = "itinerary_id";
        final String CODE = "code";
        final String ROW_ID = "row_id";

        final String MY_QUERY = "SELECT "
                + TABLE_NAME + "." + KEY_BATCH_NO + ", "
                + TABLE_NAME + "." + KEY_NORMAL + ", "
                + TABLE_NAME + "." + KEY_INVOICE_ID + ", "
                + INVOICE_TABLE + "." + TOTAL_AMOUNT + ", "
                + INVOICE_TABLE + "." + ITINERARY_ID + ", "
                + PRODUCT_TABLE + "." + PRODUCT_DESCRIPTION + ", "
                + TABLE_NAME + "." + KEY_PRICE + ","
                + PRODUCT_TABLE + "." + CODE + ", "
                + TABLE_NAME + "." + KEY_FREE + ","
                + TABLE_NAME + "." + KEY_DISCOUNT
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + INVOICE_TABLE + " ON " + TABLE_NAME + "." + KEY_INVOICE_ID + "=" + INVOICE_TABLE + "." + ROW_ID
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "=" + PRODUCT_TABLE + "." + CODE
                + " WHERE " + INVOICE_TABLE + "." + ITINERARY_ID + " = '" + itineraryId + "'";

        Cursor cursor = database.rawQuery(MY_QUERY, null);
        Log.w("Invoiced Products", "Query: " + MY_QUERY);
        Log.w("invoicedP", "Cursor sisze colums: " + cursor.getColumnCount());
        Log.w("invoicedP", "Cursor sisze rows: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] data = new String[11];
            // 0 - batch
            // 1 - normal
            // 2 - invoiceId
            // 3 - total amount
            // 4 - itineraryId
            // 5 - product description
            // 6 - product price
            // 7 - product code
            data[0] = cursor.getString(0);
            data[1] = cursor.getString(1);
            data[2] = cursor.getString(2);
            data[3] = cursor.getString(3);
            data[4] = cursor.getString(4);
            data[5] = cursor.getString(5);
            data[6] = cursor.getString(6);
            data[7] = cursor.getString(7);
            data[8] = cursor.getString(8);
            data[9] = cursor.getString(9);
//				data[10] = cursor.getString(10);
            invoiceData.add(data);

            Log.w("Log", "data[0] sisze : " + data[0]);
            Log.w("Log", "data[0] sisze : " + data[1]);
            Log.w("Log", "data[0] sisze : " + data[2]);
            Log.w("Log", "data[0] sisze : " + data[3]);
            Log.w("Log", "data[0] sisze : " + data[4]);
            Log.w("Log", "data[0] sisze : " + data[5]);
            Log.w("Log", "data[0] sisze : " + data[6]);
            Log.w("Discount", "data[10] sisze : " + data[10]);

            cursor.moveToNext();
        }
        Log.w("invoicedP", "data sisze : " + invoiceData.size());
        cursor.close();
        return invoiceData;
    }

    public ArrayList<String> getInvoicedProductBatchesForCustomer(String pharmacyId, String productCode) {


        final String ITINERARY_TABLE = "itinerary";
        final String INVOICE_TABLE = "invoice";
        final String ITINERARY_ID = "itinerary_id";
        final String KEY_PRODUCT_CODE = "product_code";
        final String PHARMACY_ID = "glb_pharmacy_id";
        final String KEY_INVOICE_ID = "invoice_id";
        final String KEY_CODE = "code";
        final String PRODUCT_TABLE = "products";

        final String QUERY = "SELECT DISTINCT "
                + KEY_BATCH_NO
                + " FROM "
                + TABLE_NAME
                + " INNER JOIN " + ITINERARY_TABLE + " ON " + ITINERARY_TABLE + "." + KEY_ROW_ID + "=" + INVOICE_TABLE + "." + ITINERARY_ID
                + " INNER JOIN " + INVOICE_TABLE + " ON " + INVOICE_TABLE + "." + KEY_ROW_ID + "=" + TABLE_NAME + "." + KEY_INVOICE_ID
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + PRODUCT_TABLE + "." + KEY_CODE + "=" + TABLE_NAME + "." + KEY_PRODUCT_CODE
                + " WHERE "
                + ITINERARY_TABLE + "." + PHARMACY_ID + "='" + pharmacyId + "' AND " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "='" + productCode + "'";
        Log.w("QUERY: ", QUERY);
        Cursor cursor = database.rawQuery(QUERY, null);
        cursor.moveToFirst();
        ArrayList<String> batchesByPharmacyId = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            batchesByPharmacyId.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return batchesByPharmacyId;
    }


    public String[] getInvoiceDataByInvoiceNumber(String invoiceId, String productCode) {

        Cursor cursor = database.query(TABLE_NAME, columns, KEY_INVOICE_ID + "='" + invoiceId + "' AND " + KEY_PRODUCT_CODE + "='" + productCode + "'", null, null, null, null);
        String[] invoicedProductDetails = new String[11];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            invoicedProductDetails[0] = cursor.getString(0);
            invoicedProductDetails[1] = cursor.getString(1);
            invoicedProductDetails[2] = cursor.getString(2);
            invoicedProductDetails[3] = cursor.getString(3);
            invoicedProductDetails[4] = cursor.getString(4);
            invoicedProductDetails[5] = cursor.getString(5);
            invoicedProductDetails[6] = cursor.getString(6);
            invoicedProductDetails[7] = cursor.getString(7);
            invoicedProductDetails[8] = cursor.getString(8);
            invoicedProductDetails[9] = cursor.getString(9);
            cursor.moveToNext();
        }
        cursor.close();
        return invoicedProductDetails;
    }


    public ArrayList<String[]> getInvoiceDetailsForReturnsByInvoiceId(String invoiceId) {
        Log.w("invoicedP", " getInvoicesByItineraryDate");
        ArrayList<String[]> invoiceData = new ArrayList<String[]>();

        final String PRODUCT_TABLE = "products";
        final String INVOICE_TABLE = "invoice";

        final String PRODUCT_DESCRIPTION = "pro_des";
        final String TOTAL_AMOUNT = "total_amount";
        final String ITINERARY_ID = "itinerary_id";
        final String CODE = "code";
        final String ROW_ID = "row_id";

        final String MY_QUERY = "SELECT "
                + TABLE_NAME + "." + KEY_BATCH_NO + ", "
                + TABLE_NAME + "." + KEY_NORMAL + ", "
                + TABLE_NAME + "." + KEY_INVOICE_ID + ", "
                + INVOICE_TABLE + "." + TOTAL_AMOUNT + ", "
                + INVOICE_TABLE + "." + ITINERARY_ID + ", "
                + PRODUCT_TABLE + "." + PRODUCT_DESCRIPTION + ", "
                + TABLE_NAME + "." + KEY_PRICE + ","
                + PRODUCT_TABLE + "." + CODE + ", "
                + TABLE_NAME + "." + KEY_FREE + ","
                + TABLE_NAME + "." + KEY_DISCOUNT
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + INVOICE_TABLE + " ON " + TABLE_NAME + "." + KEY_INVOICE_ID + "=" + INVOICE_TABLE + "." + ROW_ID
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "=" + PRODUCT_TABLE + "." + CODE
                + " WHERE " + INVOICE_TABLE + "." + KEY_ROW_ID + " = '" + invoiceId + "'";

        Cursor cursor = database.rawQuery(MY_QUERY, null);
        Log.w("Invoiced Products", "Query: " + MY_QUERY);
        Log.w("invoicedP", "Cursor sisze colums: " + cursor.getColumnCount());
        Log.w("invoicedP", "Cursor sisze rows: " + cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] data = new String[11];
            // 0 - batch
            // 1 - normal
            // 2 - invoiceId
            // 3 - total amount
            // 4 - itineraryId
            // 5 - product description
            // 6 - product price
            // 7 - product code
            data[0] = cursor.getString(0);
            data[1] = cursor.getString(1);
            data[2] = cursor.getString(2);
            data[3] = cursor.getString(3);
            data[4] = cursor.getString(4);
            data[5] = cursor.getString(5);
            data[6] = cursor.getString(6);
            data[7] = cursor.getString(7);
            data[8] = cursor.getString(8);
            data[9] = cursor.getString(9);
//				data[10] = cursor.getString(10);
            invoiceData.add(data);

            Log.w("Log", "data[0] sisze : " + data[0]);
            Log.w("Log", "data[0] sisze : " + data[1]);
            Log.w("Log", "data[0] sisze : " + data[2]);
            Log.w("Log", "data[0] sisze : " + data[3]);
            Log.w("Log", "data[0] sisze : " + data[4]);
            Log.w("Log", "data[0] sisze : " + data[5]);
            Log.w("Log", "data[0] sisze : " + data[6]);
            Log.w("Discount", "data[10] sisze : " + data[10]);

            cursor.moveToNext();
        }
        Log.w("invoicedP", "data sisze : " + invoiceData.size());
        cursor.close();

        return invoiceData;
    }





    public ArrayList<String> getAllInvoicedProductsByInvoNO(String invoNo) throws SQLException {
        ArrayList<String> invoicedProducts = new ArrayList<>();

        Cursor cursor = database.rawQuery("select pro_des  from invoiced_product  inner join products on products.code = invoiced_product.product_code   And invoice_id = ? ", new String[]{invoNo});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] invoicedProductData = new String[10];


            invoicedProducts.add(cursor.getString(cursor.getColumnIndex("pro_des")));
            cursor.moveToNext();
        }

        cursor.close();

        return invoicedProducts;
    }

    public String[] getSelectedInvoiceProduct(String invoNo,String proCode) throws SQLException {


        Cursor cursor = database.rawQuery("select * from invoiced_product  where  invoice_id = ? And product_code = ? ", new String[]{invoNo,proCode});
        String[] invoicedProductData = new String[10];
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {


            invoicedProductData[0] = cursor.getString(cursor.getColumnIndex("invoice_id"));
            invoicedProductData[1] = cursor.getString(cursor.getColumnIndex("product_code"));
            invoicedProductData[2] = cursor.getString(cursor.getColumnIndex("batch_no"));
            invoicedProductData[3] = cursor.getString(cursor.getColumnIndex("request_qty"));
            invoicedProductData[4] = cursor.getString(cursor.getColumnIndex("free"));
            invoicedProductData[5] = cursor.getString(cursor.getColumnIndex("discount"));
            invoicedProductData[6] = cursor.getString(cursor.getColumnIndex("price"));
            cursor.moveToNext();

        }

        cursor.close();

        return invoicedProductData;
    }


}
