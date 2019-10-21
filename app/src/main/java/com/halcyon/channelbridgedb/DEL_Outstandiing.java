package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Puritha Dev on 12/1/2014.
 */
public class DEL_Outstandiing {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_DealerCode = "DealerCode";
    private static final String KEY_DealerName = "DealerName";
    private static final String KEY_SalesRepID = "SalesRepID";
    private static final String KEY_SalesRep = "SalesRep";
    private static final String KEY_CustomerName = "CustomerName";
    private static final String KEY_InvoiceNo = "InvoiceNo";
    private static final String KEY_InvoiceDate = "InvoiceDate";
    private static final String KEY_Total = "Total";
    private static final String KEY_CreditAmount = "CreditAmount";
    private static final String KEY_CreditDuration = "CreditDuration";
    private static final String KEY_NewRepID = "NewRepID";
    private static final String KEY_NewRepName = "NewRepName";
    private static final String KEY_JobNo = "JobNo";
    private static final String KEY_CustomerNo = "CustomerNo";
    private static final String KEY_ID = "id";
    String[] columns = new String[]{KEY_ROW_ID, KEY_DealerCode, KEY_DealerName, KEY_SalesRepID, KEY_SalesRep, KEY_CustomerNo,
            KEY_CustomerName, KEY_InvoiceNo, KEY_InvoiceDate, KEY_Total, KEY_CreditAmount, KEY_CreditDuration, KEY_NewRepID, KEY_NewRepName, KEY_JobNo, KEY_ID};
    private static final String TABLE_NAME = "DEL_Outstanding";
    private static final String COLLECTION_NOTE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_DealerCode + " TEXT NOT NULL,"
            + KEY_DealerName + " TEXT ,"
            + KEY_SalesRepID + " TEXT ,"
            + KEY_SalesRep + " TEXT ,"
            + KEY_CustomerNo + " TEXT ,"
            + KEY_CustomerName + " TEXT ,"
            + KEY_InvoiceNo + " TEXT ,"
            + KEY_InvoiceDate + " TEXT ,"
            + KEY_Total + " TEXT ,"
            + KEY_CreditAmount + " TEXT ,"
            + KEY_CreditDuration + " TEXT ,"
            + KEY_NewRepID + " TEXT ,"
            + KEY_NewRepName + " TEXT ,"
            + KEY_JobNo + " TEXT ,"
            + KEY_ID + " TEXT " + " );";
    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public DEL_Outstandiing(Context c) {
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

    public DEL_Outstandiing openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public DEL_Outstandiing openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertDEL_Out_Standiing(String Id, String dealer_code, String DealerName,
                                        String SalesRepID, String SalesRep, String CustomerNo, String CustomerName, String InvoiceNo,
                                        String InvoiceDate, String Total,
                                        String CreditAmount, String CreditDuration, String NewRepID, String NewRepName, String JobNo
    ) throws SQLException {

        ContentValues cv = new ContentValues();
        //  RowID	DealerCode	DealerName	SalesRepID	SalesRep	CustomerNo	CustomerName	InvoiceNo	InvoiceDate	TotalAmount	CreditAmount	CreditDuration	NewRepID	NewRepName	JobNo
        cv.put(KEY_DealerCode, dealer_code);
        cv.put(KEY_DealerName, DealerName);
        cv.put(KEY_SalesRepID, SalesRepID);
        cv.put(KEY_SalesRep, SalesRep);
        cv.put(KEY_CustomerNo, CustomerNo);
        cv.put(KEY_CustomerName, CustomerName);
        cv.put(KEY_InvoiceNo, InvoiceNo);
        cv.put(KEY_InvoiceDate, InvoiceDate);
        cv.put(KEY_Total, Total);
        cv.put(KEY_CreditAmount, CreditAmount);
        cv.put(KEY_CreditDuration, CreditDuration);
        cv.put(KEY_NewRepID, NewRepID);
        cv.put(KEY_NewRepName, NewRepName);
        cv.put(KEY_JobNo, JobNo);
        cv.put(KEY_ID, Id);
        return database.insert(TABLE_NAME, null, cv);

    }

  /*  public boolean isBatchAvailable(String ID) {
        Cursor cursor = database.query(TABLE_NAME, columns, KEY_ID+"=?", new String[] {ID}, null, null, null);
        cursor.moveToFirst();
        boolean available = false;
        if (cursor.getCount() == 0) {
            available = false;
        } else {
            available = true;
        }
        return available;
    }*/

  /*  public long update(String Id,String dealer_code, String DealerName,
                                String SalesRepID, String SalesRep, String CustomerNo,String CustomerName,String InvoiceNo,
                                String InvoiceDate, String Total,
                                String CreditAmount,String CreditDuration,String NewRepID ,String NewRepName,String JobNo) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID ,Id);
        cv.put(KEY_DealerCode, dealer_code);
        cv.put(KEY_DealerName,DealerName);
        cv.put(KEY_SalesRepID,SalesRepID);
        cv.put(KEY_SalesRep,SalesRep);
        cv.put(KEY_CustomerNo,CustomerNo);
        cv.put(KEY_CustomerName, CustomerName);
        cv.put(KEY_InvoiceNo,InvoiceNo);
        cv.put(KEY_InvoiceDate,InvoiceDate);
        cv.put(KEY_Total,Total);
        cv.put(KEY_CreditAmount,CreditAmount);
        cv.put(KEY_CreditDuration, CreditDuration);
        cv.put(KEY_NewRepID,NewRepID );
        cv.put(KEY_NewRepName,NewRepName);
        cv.put(KEY_JobNo ,JobNo);

        return database.update(TABLE_NAME, cv, KEY_ID+"=?", new String[] {Id});
    }*/


    public List<String> LoadCustomerName() {


        List<String> CustomerNameList = new ArrayList();
        //  String strqu = "select "+KEY_CustomerName+" from "+ TABLE_NAME + " ";
        String strqu = "select * from " + TABLE_NAME + " group by " + KEY_CustomerName + " ";
        try {
            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {

                    CustomerNameList.add(cur.getString(6));


                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }
        } catch (Exception e) {

        }
        return CustomerNameList;

    }


    public int get_rowcount() {
        int count = 0;


        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        try {
            Cursor cur = database.rawQuery(countQuery, null);
            count = cur.getCount();
            cur.close();
        } catch (Exception e) {

        }
        return count;
    }

    public List<String> loadInvoiceNumber(String customername) {
        List<String> loadInvoiceNumberList = new ArrayList();
        try {
            String strqu = "select  InvoiceNo,Total from " + TABLE_NAME + " where " + KEY_CustomerNo + "='" + customername + "' ";
            Cursor cur = database.rawQuery(strqu, null);
            loadInvoiceNumberList.add("Select Invoice No");
            if (cur.moveToFirst()) {
                do {
                    loadInvoiceNumberList.add(cur.getString(0));

                } while (cur.moveToNext());
            }

            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {

        }

        if (loadInvoiceNumberList.size() == 0) {
            loadInvoiceNumberList.add("NULL");

        }
        return loadInvoiceNumberList;

    }


    public Double GetCredit_value(String selectedItem) {
        Double value = 0.0;
        try {
            String strqu = "select " + KEY_CreditAmount + " from " + TABLE_NAME + " where " + KEY_InvoiceNo + "='" + selectedItem + "' ";

            Cursor cur = database.rawQuery(strqu, null);
            if (cur.moveToFirst()) {
                do {
                    value = Double.parseDouble(cur.getString(0));
                } while (cur.moveToNext());
            }
            if (cur != null & !cur.isClosed()) {
                cur.close();
            }

        } catch (Exception e) {

        }
        return value;


    }

    /**
     * update credit amount when customer no and invoice number was given
     */


    public long updateCreditAmountByCusNOAndInvoNo(String creditAmount, String customerNo, String invoiceNo) {
        //  String strqu = "UPDATE " +TABLE_NAME+" SET "+KEY_CreditAmount+" = "+creditAmount+" WHERE "+KEY_CustomerNo+" = "+customerNo+" AND "+KEY_InvoiceNo+" = "+invoiceNo+";";
        Log.i("update called", "----------------->");
        ContentValues cv = new ContentValues();
        cv.put(KEY_CreditAmount, creditAmount);

        return database.update(TABLE_NAME, cv, KEY_CustomerNo + " = ? AND " + KEY_InvoiceNo + " = ?", new String[]{customerNo, invoiceNo});
    }


    /**
     * if id not exist in the db then return false else true
     */

    public boolean isExistOutstandingRow(String id) {
        String returnValue = null;
        boolean returnExistance;
        String strqu = "select " + KEY_ID + "      from " + TABLE_NAME + " where " + KEY_ID + " = '" + id + "' ";
        Cursor cur = database.rawQuery(strqu, null);

        if (cur.moveToFirst()) {
            do {

                returnValue = cur.getString(0);

            } while (cur.moveToNext());
        }

        if (cur != null & !cur.isClosed()) {
            cur.close();
        }
        if (returnValue == null) {
            returnExistance = false;
        } else {
            returnExistance = true;
        }
        return returnExistance;
    }

    /**
     * load invoice numbers which has credits
     * @return
     */

    public ArrayList<String> loadOutSatingInvoiceNumber(String id) {
        ArrayList<String> loadInvoiceNumberList = new ArrayList();

        openReadableDatabase();
        //  try {
        String strqu = "SELECT InvoiceNo FROM DEL_Outstanding WHERE CreditAmount > 0 AND CustomerNo = '" + id + "' ";
        Cursor cur = database.rawQuery(strqu, null);

        if (cur.moveToFirst()) {
            do {

                loadInvoiceNumberList.add(cur.getString(0));

            } while (cur.moveToNext());
        }

        if (cur != null & !cur.isClosed()) {
            cur.close();
        }

        //   } catch (Exception e) {
        // loadInvoiceNumberList.add("NULL");
        //   }
        closeDatabase();
        return loadInvoiceNumberList;

    }


    //Himanshu
    public int getOustandCount(String id) {
        Cursor c = database.rawQuery("SELECT row_id FROM DEL_Outstanding where CustomerNo ='" + id + "'", null);
        return c.getCount();

    }
    public Cursor loadInvoiceNumberFromCusID(String customername) {
        String strqu = "select  DISTINCT  InvoiceNo,CreditAmount from " + TABLE_NAME + " where " + KEY_CustomerNo + "='" + customername + "' AND " + KEY_CreditAmount + " != '0.00'";
        Cursor cur = database.rawQuery(strqu, null);
        return cur;

    }

    public int getRowCount() {
        openReadableDatabase();
        String count = "SELECT count(*) FROM products";
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        mcursor.close();
        closeDatabase();

        return icount;
    }

}
