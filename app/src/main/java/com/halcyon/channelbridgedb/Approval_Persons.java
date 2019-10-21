package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Himanshu on 3/12/2016.
 */
public class Approval_Persons {
    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_SEVER_ID = "sever_id";
    private static final String KEY_NAME = "approved_person_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TELEPHONE = "telephone";
    private static final String KEY_IS_ACTIVE = "is_active";





    String[] columns = new String[]{KEY_ROW_ID,KEY_SEVER_ID,KEY_NAME, KEY_EMAIL, KEY_TELEPHONE, KEY_IS_ACTIVE};//add customer image  sk


    private static final String TABLE_NAME = "approvalpersons";

    private static final String APPROVE_PERSON_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_SEVER_ID + " TEXT NOT NULL,"
            + KEY_NAME + " TEXT ,"
            + KEY_EMAIL + " TEXT ,"
            + KEY_TELEPHONE + " INTEGER ,"
            + KEY_IS_ACTIVE + " INTEGER " + " );"; //add customer image  sk


    public final Context customerContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    public Approval_Persons(Context c) {
        customerContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(APPROVE_PERSON_CREATE);

    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);

    }

    public Approval_Persons openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Approval_Persons openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(customerContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public long insertPersons(String severId, String name,
                               String email, String phone, String isActive) throws SQLException {//add new byte image

        ContentValues cv = new ContentValues();

        cv.put(KEY_SEVER_ID, severId);
        cv.put(KEY_NAME, name);
        cv.put(KEY_EMAIL, email);
        cv.put(KEY_TELEPHONE, phone);
        cv.put(KEY_IS_ACTIVE, isActive);

        // cv.put(KEY_IMAGE_BLOB,);//add new byte image sk

        return database.insert(TABLE_NAME, null, cv);

    }


    public ArrayList<String> getAllPerson(){
        ArrayList<String> personNames =null;
        personNames = new ArrayList<String>();
        Cursor c = database.rawQuery("SELECT approved_person_name FROM approvalpersons ", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                personNames.add( c.getString( c.getColumnIndex("approved_person_name")) );
                c.moveToNext();
            }
        }else {

        }
        c.close();
        return personNames;
    }

    public int getPhoneNumberByPersonName(String name){
        int number = 0;

        Cursor c = database.rawQuery("SELECT telephone FROM approvalpersons where approved_person_name ='" + name + "'", null);

        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                number = c.getInt(c.getColumnIndex(KEY_TELEPHONE));

            }
        } else {

        }
        c.close();
        return number;
    }


}
