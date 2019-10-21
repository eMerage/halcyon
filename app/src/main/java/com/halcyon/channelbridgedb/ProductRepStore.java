package com.halcyon.channelbridgedb;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.halcyon.Entity.Product;

public class ProductRepStore {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_BATCH_NO = "batch_number";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_EXPIRY_DATE = "expiry_date";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_PURCHASE_PRICE = "price_purchase";
    private static final String KEY_SELLING_PRICE = "price_selling";
    private static final String KEY_RETAIL_PRICE = "price_retail";


    String[] columns = {KEY_ROW_ID, KEY_PRODUCT_ID, KEY_PRODUCT_CODE, KEY_BATCH_NO, KEY_QUANTITY, KEY_EXPIRY_DATE,KEY_PURCHASE_PRICE,KEY_SELLING_PRICE,KEY_RETAIL_PRICE,KEY_TIMESTAMP};
    // String[] columns = {KEY_ROW_ID, KEY_PRODUCT_ID, KEY_PRODUCT_CODE, KEY_BATCH_NO, KEY_QUANTITY, KEY_EXPIRY_DATE,KEY_TIMESTAMP};
    private static final String TABLE_NAME = "productRepStore";
    private static final String PRODUCT_REP_STORE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRODUCT_ID + " INTEGER NOT NULL ,"
            + KEY_PRODUCT_CODE + " TEXT NOT NULL ,"
            + KEY_BATCH_NO + " TEXT NOT NULL ,"
            + KEY_QUANTITY + " TEXT NOT NULL ,"
            + KEY_EXPIRY_DATE + " TEXT NOT NULL ,"
            + KEY_PURCHASE_PRICE  + " TEXT,"
            + KEY_SELLING_PRICE  + " TEXT,"
            + KEY_RETAIL_PRICE  + " TEXT,"
            + KEY_TIMESTAMP + " TEXT NOT NULL);";
    public final Context productRepStoretContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ProductRepStore(Context c) {
        productRepStoretContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(PRODUCT_REP_STORE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public ProductRepStore openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productRepStoretContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public ProductRepStore openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(productRepStoretContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertProductRepStore(String productId, String productCode, String batchNo, String quantity, String expiryDate,String pPrice,String sellPrice,String retailPrice, String timestamp) throws SQLException {

        String exdate =expiryDate.substring(0,10);
        ContentValues cv = new ContentValues();
        Cursor cursor = database.rawQuery("SELECT row_id FROM productRepStore WHERE product_code =? AND batch_number =? ", new String[]{productCode,batchNo});
        if(cursor.getCount()>0){
            cv.put(KEY_QUANTITY, quantity);
            cv.put(KEY_SELLING_PRICE, sellPrice);
            cv.put(KEY_RETAIL_PRICE, retailPrice);
            cv.put(KEY_PURCHASE_PRICE, pPrice);
            cursor.close();
            return database.update(TABLE_NAME, cv, KEY_BATCH_NO + "=?" , new String[]{batchNo});
        }else {
            cv.put(KEY_PRODUCT_ID, productId);
            cv.put(KEY_PRODUCT_CODE, productCode);
            cv.put(KEY_BATCH_NO, batchNo);
            cv.put(KEY_QUANTITY, quantity);
            cv.put(KEY_EXPIRY_DATE, exdate);
            cv.put(KEY_PURCHASE_PRICE,pPrice);
            cv.put(KEY_SELLING_PRICE,sellPrice);
            cv.put(KEY_RETAIL_PRICE,retailPrice);
            cv.put(KEY_TIMESTAMP, timestamp);
            cursor.close();
            return database.insert(TABLE_NAME, null, cv);
        }


    }


    public ArrayList<String[]> getAllProductRepstore() {
        ArrayList<String[]> productsStore = new ArrayList<String[]>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] productData = new String[7];
            productData[0] = cursor.getString(0);// rowId
            productData[1] = cursor.getString(1);// productId
            productData[2] = cursor.getString(2);// productCode
            productData[3] = cursor.getString(3);// batch
            productData[4] = cursor.getString(4);// quantity
            productData[5] = cursor.getString(5);// expDate
            productData[6] = cursor.getString(6);// timestamp


            productsStore.add(productData);
            cursor.moveToNext();
        }

        cursor.close();

        return productsStore;
    }

    public ArrayList<String[]> getProductDetailsByProductCode(String id) {

        ArrayList<String[]> productInformation = new ArrayList<String[]>();
        Log.w("repstore :", "getProductDetailsByProductCode " + id);
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_PRODUCT_CODE + " = '" + id + "'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String[] productDetails = new String[7];

            productDetails[0] = cursor.getString(0);
            productDetails[1] = cursor.getString(1);
            productDetails[2] = cursor.getString(2);
            productDetails[3] = cursor.getString(3);
            productDetails[4] = cursor.getString(4);
            productDetails[5] = cursor.getString(5);
            productDetails[6] = cursor.getString(6);

            productInformation.add(productDetails);


            cursor.moveToNext();
        }
        cursor.close();

        return productInformation;

    }

    public String[] getProductDetailsByProductBatchAndProductCode(String batch, String productCode) {

        //ArrayList<String[]> productInformation = new ArrayList<String[]>();
        Log.w("repstore :", "getProductDetailsByProductBatch " + batch);
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_BATCH_NO + " = '" + batch + "' AND " + KEY_PRODUCT_CODE + "='" + productCode + "'",
                null, null, null, null);
        String[] productDetails = new String[14];

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            productDetails[0] = cursor.getString(0);
            productDetails[1] = cursor.getString(1);
            productDetails[2] = cursor.getString(2);
            productDetails[3] = cursor.getString(3);
            productDetails[4] = cursor.getString(4);
            productDetails[5] = cursor.getString(5);
            productDetails[6] = cursor.getString(6);
            productDetails[7] = "0";
            productDetails[8] = "0";
            productDetails[9] = "0";
            productDetails[10] = "0";
            productDetails[13] = "0";

            Log.w("productRepStoreData 0:", productDetails[0]);
            Log.w("productRepStoreData 1:", productDetails[1]);
            Log.w("productRepStoreData 2:", productDetails[2]);
            Log.w("productRepStoreData 3:", productDetails[3]);
            Log.w("productRepStoreData 4:", productDetails[4]);
            Log.w("productRepStoreData 5:", productDetails[5]);
            Log.w("productRepStoreData 6:", productDetails[6]);

            cursor.moveToNext();
        }

//        Log.w("repstore :", "productDetails " + productDetails[1]);
        cursor.close();
        return productDetails;

    }


    public long updateRepStoreData(String rowId, double quantity) throws SQLException {
        long result = 0;


        String stock = getQuantityByProductCodeAndBatch(rowId);
        double remain = Double.parseDouble(stock) - quantity;
        if (remain < 0 ){
            remain = 0;
        }


        ContentValues cv = new ContentValues();
        cv.put(KEY_QUANTITY, (int)remain);
        result = database.update(TABLE_NAME, cv, KEY_ROW_ID + "= ?", new String[]{rowId});
        return result;
    }


    public ArrayList<String[]> getAllProductRepStoreWithDetails() {
        ArrayList<String[]> productsWithDetails = new ArrayList<String[]>();

        String PRODUCT_TABLE = "products";
        String KEY_CODE = "code";
        String KEY_UNIT_NAME = "unit_name";
        String KEY_UNIT_SIZE = "unit_size";
        String KEY_GEN_NAME = "generic_name";
        String KEY_CATEGORY = "category";
        String KEY_PRO_DES = "pro_des";
        String KEY_INTR_DATE = "introduced_date";
        String KEY_COUN_OF_ORIGIN = "count_of_origin";
        String KEY_PRINCIPLE = "principle";
        String KEY_PURCHASE_PRICE = "purchase_price";
        String KEY_SELLING_PRICE = "selling_price";
        String KEY_RETAIL_PRICE = "retail_price";
        String query = "SELECT "
                + PRODUCT_TABLE + "." + KEY_CODE + ","
                + PRODUCT_TABLE + "." + KEY_UNIT_NAME + ","
                + PRODUCT_TABLE + "." + KEY_UNIT_SIZE + ","
                + PRODUCT_TABLE + "." + KEY_GEN_NAME + ","
                + PRODUCT_TABLE + "." + KEY_CATEGORY + ","
                + PRODUCT_TABLE + "." + KEY_PRO_DES + ","
                + PRODUCT_TABLE + "." + KEY_INTR_DATE + ","
                + PRODUCT_TABLE + "." + KEY_COUN_OF_ORIGIN + ","
                + PRODUCT_TABLE + "." + KEY_PRINCIPLE + ","
                + PRODUCT_TABLE + "." + KEY_PURCHASE_PRICE + ","
                + PRODUCT_TABLE + "." + KEY_SELLING_PRICE + ","
                + PRODUCT_TABLE + "." + KEY_RETAIL_PRICE + ","
                + TABLE_NAME + "." + KEY_PRODUCT_CODE + ","
                + TABLE_NAME + "." + KEY_BATCH_NO + ","
                + TABLE_NAME + "." + KEY_QUANTITY + ","
                + TABLE_NAME + "." + KEY_EXPIRY_DATE + ","
                + TABLE_NAME + "." + KEY_ROW_ID
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "=" + PRODUCT_TABLE + "." + KEY_CODE;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String[] productData = new String[17];

            productData[0] = cursor.getString(0);
            productData[1] = cursor.getString(1);
            productData[2] = cursor.getString(2);
            productData[3] = cursor.getString(3);
            productData[4] = cursor.getString(4);
            productData[5] = cursor.getString(5);
            productData[6] = cursor.getString(6);
            productData[7] = cursor.getString(7);
            productData[8] = cursor.getString(8);
            productData[9] = cursor.getString(9);
            productData[10] = cursor.getString(10);
            productData[11] = cursor.getString(11);
            productData[12] = cursor.getString(12);
            productData[13] = cursor.getString(13);
            productData[14] = cursor.getString(14);
            productData[15] = cursor.getString(15);
            productData[16] = cursor.getString(16);

            productsWithDetails.add(productData);

//    		Log.w("productData 0:", cursor.getString(0));
//			Log.w("productData 1:", cursor.getString(1));
//			Log.w("productData 2:", cursor.getString(2));
//			Log.w("productData 3:", cursor.getString(3));
//			Log.w("productData 4:", cursor.getString(4));
//			Log.w("productData 5:", cursor.getString(5));
//			Log.w("productData 6:", cursor.getString(6));
//			Log.w("productData 7:", cursor.getString(7));
//			Log.w("productData 8:", cursor.getString(8));
//			Log.w("productData 9:", cursor.getString(9));
//			Log.w("productData 10:", cursor.getString(10));
//			Log.w("productData 11:", cursor.getString(11));
//			Log.w("productData 12:", cursor.getString(12));
//			Log.w("productData 13:", cursor.getString(13));
//			Log.w("productData 14:", cursor.getString(14));
//			Log.w("productData 15:", cursor.getString(15));

            cursor.moveToNext();
        }
        cursor.close();

        return productsWithDetails;
    }

    public ArrayList<String[]> SearchProductRepStoreWithDetails(String searchString) {
        ArrayList<String[]> productsWithDetails = new ArrayList<String[]>();

        String PRODUCT_TABLE = "products";
        String KEY_CODE = "code";
        String KEY_UNIT_NAME = "unit_name";
        String KEY_UNIT_SIZE = "unit_size";
        String KEY_GEN_NAME = "generic_name";
        String KEY_CATEGORY = "category";
        String KEY_PRO_DES = "pro_des";
        String KEY_INTR_DATE = "introduced_date";
        String KEY_COUN_OF_ORIGIN = "count_of_origin";
        String KEY_PRINCIPLE = "principle";
        String KEY_PURCHASE_PRICE = "purchase_price";
        String KEY_SELLING_PRICE = "selling_price";
        String KEY_RETAIL_PRICE = "retail_price";
        String query = "SELECT "
                + PRODUCT_TABLE + "." + KEY_CODE + ","
                + PRODUCT_TABLE + "." + KEY_UNIT_NAME + ","
                + PRODUCT_TABLE + "." + KEY_UNIT_SIZE + ","
                + PRODUCT_TABLE + "." + KEY_GEN_NAME + ","
                + PRODUCT_TABLE + "." + KEY_CATEGORY + ","
                + PRODUCT_TABLE + "." + KEY_PRO_DES + ","
                + PRODUCT_TABLE + "." + KEY_INTR_DATE + ","
                + PRODUCT_TABLE + "." + KEY_COUN_OF_ORIGIN + ","
                + PRODUCT_TABLE + "." + KEY_PRINCIPLE + ","
                + PRODUCT_TABLE + "." + KEY_PURCHASE_PRICE + ","
                + PRODUCT_TABLE + "." + KEY_SELLING_PRICE + ","
                + PRODUCT_TABLE + "." + KEY_RETAIL_PRICE + ","
                + TABLE_NAME + "." + KEY_PRODUCT_CODE + ","
                + TABLE_NAME + "." + KEY_BATCH_NO + ","
                + TABLE_NAME + "." + KEY_QUANTITY + ","
                + TABLE_NAME + "." + KEY_EXPIRY_DATE
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "=" + PRODUCT_TABLE + "." + KEY_CODE
                + " WHERE "
                + PRODUCT_TABLE + "." + KEY_PRO_DES + " LIKE ?";
        Cursor cursor = database.rawQuery(query, new String[]{"%" + searchString + "%"});
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String[] productData = new String[16];

            productData[0] = cursor.getString(0);
            productData[1] = cursor.getString(1);
            productData[2] = cursor.getString(2);
            productData[3] = cursor.getString(3);
            productData[4] = cursor.getString(4);
            productData[5] = cursor.getString(5);
            productData[6] = cursor.getString(6);
            productData[7] = cursor.getString(7);
            productData[8] = cursor.getString(8);
            productData[9] = cursor.getString(9);
            productData[10] = cursor.getString(10);
            productData[11] = cursor.getString(11);
            productData[12] = cursor.getString(12);
            productData[13] = cursor.getString(13);
            productData[14] = cursor.getString(14);
            productData[15] = cursor.getString(15);

            productsWithDetails.add(productData);

//    		Log.w("productData 0:", cursor.getString(0));
//			Log.w("productData 1:", cursor.getString(1));
//			Log.w("productData 2:", cursor.getString(2));
//			Log.w("productData 3:", cursor.getString(3));
//			Log.w("productData 4:", cursor.getString(4));
//			Log.w("productData 5:", cursor.getString(5));
//			Log.w("productData 6:", cursor.getString(6));
//			Log.w("productData 7:", cursor.getString(7));
//			Log.w("productData 8:", cursor.getString(8));
//			Log.w("productData 9:", cursor.getString(9));
//			Log.w("productData 10:", cursor.getString(10));
//			Log.w("productData 11:", cursor.getString(11));
//			Log.w("productData 12:", cursor.getString(12));
//			Log.w("productData 13:", cursor.getString(13));
//			Log.w("productData 14:", cursor.getString(14));
//			Log.w("productData 15:", cursor.getString(15));

            cursor.moveToNext();
        }
        cursor.close();
        return productsWithDetails;
    }

    public ArrayList<String> getAllProductRepStoreNames() {
        ArrayList<String> productsNames = new ArrayList<String>();

        String PRODUCT_TABLE = "products";
        String KEY_PRO_DES = "pro_des";
        String KEY_CODE = "code";
        String query = "SELECT DISTINCT "
                + PRODUCT_TABLE + "." + KEY_PRO_DES
                + " FROM " + TABLE_NAME
                + " INNER JOIN " + PRODUCT_TABLE + " ON " + TABLE_NAME + "." + KEY_PRODUCT_CODE + "=" + PRODUCT_TABLE + "." + KEY_CODE;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            productsNames.add(cursor.getString(0));
            Log.w("productData 0:", cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return productsNames;
    }

    public void updateProductRepStoreReturns(String batchNo, String quantity) {
        final String query = "UPDATE " + TABLE_NAME + " SET " + KEY_QUANTITY + "=(" + KEY_QUANTITY + "+" + quantity + ") WHERE " + KEY_BATCH_NO + "='" + batchNo + "'";
        database.execSQL(query);
        Log.w("ProductRepstoreUpdate", query);

    }



    public ArrayList<String> getBatchesByProductCode(String productCode) {
        Log.w("product id recieved to get batches: ", productCode + "");
        Cursor cursor = database.query(TABLE_NAME, new String[]{KEY_BATCH_NO}, KEY_PRODUCT_CODE + "=?", new String[]{productCode}, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> batchList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            batchList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return batchList;
    }

    public String getQuantitySumByProductCode(String productCode) {
        String query = "SELECT SUM(" + KEY_QUANTITY + ") FROM " + TABLE_NAME + " WHERE `" + KEY_PRODUCT_CODE + "`='" + productCode + "'";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        String qty = "0";
        if (cursor.getCount() != 0) {
            qty = cursor.getString(0);
            if (qty.contentEquals("null")) {
                qty = "0";
            }
        }
        cursor.close();
        return qty;
    }




//   public long deleteRepStoreByBatch(String batch) {
//	   return database.delete(TABLE_NAME, KEY_BATCH_NO+"=?", new String[] {batch});
//   }

    public String getMaxRepstoreId() {

//	   String queryStr = "SELECT Max("+KEY_PROD_ROW_ID+") from "+TABLE_NAME;
//	   
//	   Cursor cursor = database.rawQuery(queryStr, null);

        Cursor cursor = database.query(TABLE_NAME, new String[]{"Max(" + KEY_PRODUCT_ID + ")"}, null, null, null, null, null, null);


        String productId = "0";
        if (cursor.getCount() == 0) {

        } else {
            cursor.moveToFirst();
            productId = cursor.getString(0);
        }
        cursor.close();
        return productId;
    }

    public boolean isBatchAvailableWithoutProdCode(String batch) {
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_BATCH_NO + "=?", new String[]{batch}, null, null, null);
        cursor.moveToFirst();
        boolean available = false;
        if (cursor.getCount() > 0) {
            available = true;
        }
        cursor.close();
        return available;
    }


    public long updateRepStoreQtyAdd(String batchNo, int quantity, String productCode) throws SQLException {
        long result = 0;

        final String query = "UPDATE " + TABLE_NAME + " SET " + KEY_QUANTITY + "=(" + KEY_QUANTITY + "+" + quantity + ") WHERE " + KEY_BATCH_NO + "='" + batchNo + "' AND " + KEY_PRODUCT_CODE + "='" + productCode + "'";

        database.execSQL(query);
        Log.w("ProductRepstoreUpdate", query);
        return result;
    }


    /**
     * Author Amila
     * @return
     */


    public ArrayList<Product> getAllRepAtoreDetails() {
        ArrayList<Product> products = new ArrayList<>();

        Cursor cursor = database.rawQuery("select r.row_id,r.product_id,p.code,p.brand,p.unit_name,p.unit_size,p.generic_name,p.category,p.pro_des,p.introduced_date,p.principle,r.price_purchase,r.price_selling,r.price_retail,r.batch_number,r.quantity,r.timestamp,r.expiry_date from products p inner join productRepStore r on p.code = r.product_code", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Product productData = new Product();

            System.out.println("getString(0 :"+cursor.getString(0));
            productData.setRowId(Integer.parseInt(cursor.getString(0)));// rowId of repstore
            productData.setId(cursor.getString(1));// product id from rep stores
            productData.setCode( cursor.getString(2));// Code
            productData.setBrand( cursor.getString(3));// brand
            productData.setUnitName( cursor.getString(4));// unit name
            productData.setUnitSize(cursor.getString(5));// size
            productData.setGenericName(cursor.getString(6));// generic name
            productData.setCategory(cursor.getString(7));// category
            productData.setProDes(cursor.getString(8));// proDes
            productData.setIntroducedDate(cursor.getString(9));// introduced date
            productData.setPrinciple(cursor.getString(10));// principle
            productData.setPurchasePrice(Double.parseDouble(cursor.getString(11)));// purchase Price
            productData.setSellingPrice(Double.parseDouble(cursor.getString(12)));// selling Price----
            productData.setRetailPrice(Double.parseDouble(cursor.getString(13)));// retail Price
            productData.setBatchNumber(cursor.getString(14));
            productData.setQuantity(Integer.parseInt(cursor.getString(15)));//stock repstore
            productData.setTimeStamp(cursor.getString(16));// timestamp
            productData.setExpiryDate(cursor.getString(17));


            products.add(productData);
            cursor.moveToNext();
        }

        cursor.close();

        return products;
    }

    public String getQuantityByProductCodeAndBatch(String rowId) {

        final String MY_QUERY = "SELECT " + KEY_QUANTITY + " FROM " + TABLE_NAME + " WHERE " + KEY_ROW_ID + " = '" + rowId + "'";

        Log.w("MY_QUERY", " : " + MY_QUERY);
        Cursor cursor = database.rawQuery(MY_QUERY, null);

        cursor.moveToFirst();
        String expiry = cursor.getString(0);
        Log.w("expiry", " : " + expiry);
        cursor.close();
        return expiry;
    }
public int getCount(){
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
