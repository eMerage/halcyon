package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.halcyon.Entity.DealerSaleEntity;

import java.util.ArrayList;

/**
 * Created by Amila on 12/21/15.
 */
public class DealerSales {



        private static final String KEY_ROW_ID        = "rowId";
        private static final String KEY_DealerID = "DealerID";
        private static final String KEY_ITEMID = "itemId";
        private static final String KEY_PPRICE = "purchasingPrice";
        private static final String KEY_SPRICE = "sellingPrice";
        private static final String KEY_RPRICE = "retailPrice";
        private static final String KEY_DISCOUNT_METHOD = "discountMethod";
        private static final String KEY_DISCOUNT_RATE = "discountRate";
        private static final String KEY_FREE_ISSUES = "freeIssues";
        private static final String KEY_INVOICENO = "invoiceNo";
        private static final String KEY_ISSUEMODE = "issueMode";
        private static final String KEY_Qty = "Quantity";
        private static final String KEY_UNITPRICE = "unitPrice";
        private static final String KEY_DISCOUNT = "discount";
        private static final String KEY_REP_ID = "repId";
        private static final String KEY_REP_NAME = "repName";
        private static final String KEY_CUST_NO = "custNo";
        private static final String KEY_INVOICE_DATE = "inovoice_date";
        private static final String KEY_PAYMENT_TYPE = "paymentType";
        private static final String KEY_TOTAL = "total";
        private static final String KEY_EXPIRY_DATE = "expiryDate";
        private static final String KEY_BATCH = "batch";
        private static final String KEY_REFNO = "refNo";

        String[] columns = new String[]{KEY_ROW_ID, KEY_DealerID, KEY_ITEMID, KEY_PPRICE,KEY_SPRICE,KEY_RPRICE, KEY_DISCOUNT_METHOD,KEY_DISCOUNT_RATE,
                KEY_FREE_ISSUES, KEY_ISSUEMODE, KEY_Qty, KEY_UNITPRICE, KEY_DISCOUNT, KEY_REP_ID, KEY_REP_NAME, KEY_CUST_NO, KEY_INVOICE_DATE, KEY_PAYMENT_TYPE
        ,KEY_TOTAL,KEY_EXPIRY_DATE,KEY_BATCH,KEY_REFNO};
        private static final String TABLE_NAME = "dealerSales";




        private static final String DEALER_CREATE = "CREATE TABLE " + TABLE_NAME
                + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DealerID + " TEXT ,"
                + KEY_ITEMID + " TEXT ,"
                + KEY_PPRICE + " TEXT ,"
                + KEY_SPRICE + " TEXT ,"
                + KEY_RPRICE + " TEXT ,"
                + KEY_DISCOUNT_METHOD + " TEXT ,"
                + KEY_DISCOUNT_RATE + " TEXT ,"
                + KEY_FREE_ISSUES + " TEXT ,"
                + KEY_INVOICENO +" TEXT,"
                + KEY_ISSUEMODE + " TEXT ,"
                + KEY_Qty + " INTEGER  ,"
                + KEY_UNITPRICE + " TEXT ,"
                + KEY_DISCOUNT + " TEXT ,"
                + KEY_REP_ID + " TEXT ,"
                + KEY_REP_NAME + " TEXT ,"
                + KEY_CUST_NO + " TEXT, "
                + KEY_INVOICE_DATE + " TEXT, "
                + KEY_PAYMENT_TYPE+ " TEXT, "
                + KEY_TOTAL+ " TEXT, "
                + KEY_EXPIRY_DATE+ " TEXT, "
                + KEY_BATCH+ " TEXT, "
                + KEY_REFNO+ " TEXT "
                + " );";
        public final Context customerContext;
        public DatabaseHelper databaseHelper;
        private SQLiteDatabase database;


        public DealerSales(Context c) {
            customerContext = c;
        }

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DEALER_CREATE);
            Log.i("deal ->",DEALER_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }

        public DealerSales openWritableDatabase() throws SQLException {
            databaseHelper = new DatabaseHelper(customerContext);
            database = databaseHelper.getWritableDatabase();
            return this;

        }

        public DealerSales openReadableDatabase() throws SQLException {
            databaseHelper = new DatabaseHelper(customerContext);
            database = databaseHelper.getReadableDatabase();
            return this;

        }

        public void closeDatabase() throws SQLException {
            databaseHelper.close();
        }


        public long insertDealerSales(DealerSaleEntity entity)throws SQLException {

            openWritableDatabase();
            long result;
            ContentValues cv = new ContentValues();
      //  RowID	DealerCode	DealerName	SalesRepID	SalesRep	CustomerNo	CustomerName	InvoiceNo	InvoiceDate	TotalAmount	CreditAmount	CreditDuration	NewRepID	NewRepName	JobNo
            cv.put(KEY_DealerID  , entity.getDealerId());
            cv.put(KEY_ITEMID  , entity.getItemId());
            cv.put(KEY_PPRICE , entity.getpPrice());
            cv.put(KEY_SPRICE , entity.getsPrice());
            cv.put(KEY_RPRICE , entity.getrPrice());
            cv.put(KEY_DISCOUNT_METHOD , entity.getDicountMethod());
            cv.put(KEY_DISCOUNT_RATE  , entity.getDiscountRate());
            cv.put(KEY_FREE_ISSUES , entity.getFreeIssues());
            cv.put(KEY_INVOICENO , entity.getInvoiceNo());
            cv.put(KEY_ISSUEMODE , entity.getIssueMode());
            cv.put(KEY_Qty , entity.getQty());
            cv.put(KEY_UNITPRICE , entity.getUnitPrice());
            cv.put(KEY_DISCOUNT  , entity.getDiscount());
            cv.put(KEY_REP_ID , entity.getRepID());
            cv.put(KEY_REP_NAME  , entity.getRepName());
            cv.put(KEY_CUST_NO , entity.getCustNo());
            cv.put(KEY_INVOICE_DATE, entity.getInvoiceDate());
            cv.put(KEY_PAYMENT_TYPE, entity.getPaymentType());
            cv.put(KEY_TOTAL, entity.getTotal());
            cv.put(KEY_EXPIRY_DATE, entity.getExpiry());
            cv.put(KEY_BATCH, entity.getBatch());
            cv.put(KEY_REFNO, entity.getRefNo());

            result = database.insert(TABLE_NAME, null, cv);
            closeDatabase();

            return result;

        }


    public ArrayList<String> getAllProductsFromDealerSales(String invoNo){
        ArrayList<String> products = new ArrayList<String>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select products.pro_des from dealerSales inner join products on dealerSales.itemId = products.code and dealerSales.invoiceNo = ? group by pro_des", new String[]{invoNo});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            String productName = cursor.getString(cursor.getColumnIndex("pro_des"));


            products.add(productName);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return  products;
    }


    public DealerSaleEntity getProductById(String code,String invoNo){
        ArrayList<DealerSaleEntity> products = new ArrayList<>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select * from dealerSales where dealerSales.invoiceNo = ? and dealerSales.itemId = ?", new String[]{invoNo,code});

        cursor.moveToFirst();
        DealerSaleEntity entity = null;
        while (!cursor.isAfterLast()) {
            entity = new DealerSaleEntity();
            entity.setDealerId(cursor.getString(cursor.getColumnIndex(KEY_DealerID)));
            entity.setItemId(cursor.getString(cursor.getColumnIndex(KEY_ITEMID)));
            entity.setpPrice(cursor.getString(cursor.getColumnIndex(KEY_PPRICE)));
            entity.setsPrice(cursor.getString(cursor.getColumnIndex(KEY_SPRICE)));
            entity.setrPrice(cursor.getString(cursor.getColumnIndex(KEY_RPRICE)));
            entity.setDicountMethod(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_METHOD)));
            entity.setDiscountRate(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT_RATE)));
            entity.setFreeIssues(cursor.getString(cursor.getColumnIndex(KEY_FREE_ISSUES)));
            entity.setInvoiceNo(cursor.getString(cursor.getColumnIndex(KEY_INVOICENO)));
            entity.setIssueMode(cursor.getString(cursor.getColumnIndex(KEY_ISSUEMODE)));
            entity.setQty(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_Qty))));
            entity.setUnitPrice(cursor.getString(cursor.getColumnIndex(KEY_UNITPRICE)));
            entity.setDiscount(cursor.getString(cursor.getColumnIndex(KEY_DISCOUNT)));
            entity.setRepID(cursor.getString(cursor.getColumnIndex(KEY_REP_ID)));
            entity.setRepName(cursor.getString(cursor.getColumnIndex(KEY_REP_NAME)));
            entity.setCustNo(cursor.getString(cursor.getColumnIndex(KEY_CUST_NO)));
            entity.setExpiry(cursor.getString(cursor.getColumnIndex(KEY_EXPIRY_DATE)));
            entity.setBatch(cursor.getString(cursor.getColumnIndex(KEY_BATCH)));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return  entity;
    }


    public ArrayList<String[]> getProductListForInvoice(String invoNo){
        ArrayList<String[]> products = new ArrayList<>();
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select * from dealerSales inner join products on dealerSales.itemId = products.code and dealerSales.invoiceNo = ?", new String[]{invoNo});

        cursor.moveToFirst();
        DealerSaleEntity entity = null;
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






            data[0] = cursor.getString(cursor.getColumnIndex(KEY_BATCH));
            data[1] = cursor.getString(cursor.getColumnIndex(KEY_Qty));
            data[2] = cursor.getString(cursor.getColumnIndex(KEY_INVOICENO));
            data[3] = cursor.getString(cursor.getColumnIndex(KEY_TOTAL));
            data[4] = "";
            data[5] = cursor.getString(cursor.getColumnIndex("pro_des"));
            data[6] = cursor.getString(cursor.getColumnIndex(KEY_UNITPRICE));
            data[7] = cursor.getString(cursor.getColumnIndex(KEY_ITEMID));
            products.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return  products;
    }


    public String getInvoiceSumforGivenDateAndCustomer(String custNo,String date) {
        date = date + "%";
        String sum = "0";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select sum(total) from dealerSales where custNo = ? and inovoice_date like ? ", new String[]{custNo,date});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sum = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        if(sum == null){
            sum = "0.00";
        }
        return sum;
    }

    public String getLastInvoiceForGivenDate(String custNo,String date) {
        date = date + "%";
        String no = "";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select invoiceNo from dealerSales where custNo = ? and inovoice_date like  ? ORDER BY invoiceNo DESC LIMIT 1 ", new String[]{custNo,date});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            no = cursor.getString(0);
            cursor.moveToNext();
        }
        closeDatabase();
        if(no == null){
            no = "Invoice No.";
        }
        cursor.close();
        return no;
    }



    public String getInvoiceSumforGivenDate(String date) {
        date = date + "%";
        String sum = "";
        openReadableDatabase();
        Cursor cursor = database.rawQuery("select sum(total) from dealerSales where inovoice_date like ?", new String[]{date});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sum = cursor.getString(0);
            cursor.moveToNext();
        }
        closeDatabase();
        if(sum == null){
            sum = "0.00";
        }
        cursor.close();
        return sum;
    }

    public int getDealerSalesCount(){
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
