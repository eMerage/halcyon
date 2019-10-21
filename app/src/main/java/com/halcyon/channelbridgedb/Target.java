package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.halcyon.Entity.TargetEntity;
import com.halcyon.channelbridge.SelectedProduct;

import java.util.ArrayList;

public class Target {

    private static final String KEY_ROW_ID = "row_id";
    private static final String KEY_PRINCIPLE = "principle";
    private static final String KEY_PRODUCT_CODE = "product_code";
    private static final String KEY_PRODUCT_NAME= "product_name";
    private static final String KEY_TARGET = "product_target";
    private static final String KEY_ACHIEVEMENT = "product_achievement";
    private static final String KEY_IS_ACTIVE = "is_active";


    String[] columns = {KEY_ROW_ID, KEY_PRINCIPLE, KEY_PRODUCT_CODE,KEY_PRODUCT_NAME, KEY_TARGET, KEY_ACHIEVEMENT, KEY_IS_ACTIVE};

    private static final String TABLE_NAME = "target";
    private static final String INVOICE_CREATE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PRINCIPLE + " TEXT NOT NULL,"
            + KEY_PRODUCT_CODE + " TEXT NOT NULL,"
            + KEY_PRODUCT_NAME + " TEXT NOT NULL,"
            + KEY_TARGET + " INTEGER ,"
            + KEY_ACHIEVEMENT + " INTEGER ,"
            + KEY_IS_ACTIVE + " INTEGER "
            + ");";

    public final Context invoiceContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Target(Context c) {
        invoiceContext = c;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(INVOICE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public Target openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public Target openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(invoiceContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }

    public void insertTarget(String principle, String product, int target, int achievement,String proName) throws SQLException {
        openWritableDatabase();


        Cursor cursorProductAvalability = database.rawQuery("SELECT row_id FROM target WHERE product_code =?", new String[]{product});
        ContentValues cv = new ContentValues();

        if (cursorProductAvalability.getCount() == 0) {
            cv.put(KEY_PRINCIPLE, principle);
            cv.put(KEY_PRODUCT_CODE, product);
            cv.put(KEY_PRODUCT_NAME, proName);
            cv.put(KEY_TARGET, target);
            cv.put(KEY_ACHIEVEMENT, achievement);
            cv.put(KEY_IS_ACTIVE, 0);
            database.insert(TABLE_NAME, null, cv);

        } else {
            Cursor cursorTargetChange = database.query(TABLE_NAME, columns, KEY_PRODUCT_CODE + " = '" + product + "' AND " + KEY_TARGET + "='" + target + "'", null, null, null, null);

            if (cursorTargetChange.getCount() == 0) {
                cv.put(KEY_TARGET, target);
                database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + "=?", new String[]{product});
            } else {

            }
            cursorTargetChange.close();

            if (achievement == 0) {
                cv.put(KEY_ACHIEVEMENT, achievement);
                database.update(TABLE_NAME, cv, KEY_PRODUCT_CODE + "=?", new String[]{product});
            } else {

            }

        }
        cursorProductAvalability.close();
        closeDatabase();

    }

    public ArrayList<TargetEntity> getTarget() {
        openReadableDatabase();
        ArrayList<TargetEntity> allTarget = new ArrayList<>();
        Cursor cursor;

        cursor = database.query(TABLE_NAME, columns, KEY_TARGET + " != 0 OR " + KEY_ACHIEVEMENT + "!=0", null, null, null, null);


        cursor.moveToFirst();
        TargetEntity temp = null;
        while (!cursor.isAfterLast()) {
            temp = new TargetEntity();
            temp.setPrinciple(cursor.getString(1));
            temp.setProductCode(cursor.getString(2));
            temp.setProductName(cursor.getString(3));

            int target;
            int achievement;
            try {
                target = Integer.parseInt(cursor.getString(4));
            } catch (NumberFormatException numEx) {
                target = 0;
            }
            try {
                achievement = Integer.parseInt(cursor.getString(5));
            } catch (NumberFormatException numEx) {
                achievement = 0;
            }



            temp.setTarget(target);
            temp.setAchievement(achievement);

            int newDiffrent=target-achievement;
            String diffrentInCharator;

            if(newDiffrent<0){
                int x = Math.abs(newDiffrent);
                diffrentInCharator="+"+String.valueOf(x);
            }else {
                diffrentInCharator=String.valueOf(newDiffrent);
            }


             temp.setDifferent(diffrentInCharator);

            if (target == 0 || achievement == 0) {
                temp.setAchievementPresentage(0.0);
            } else {
                Double achievementPresentage = (Double.valueOf(achievement) / Double.valueOf(target)) * 100;
                temp.setAchievementPresentage(achievementPresentage);
            }

            allTarget.add(temp);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return allTarget;
    }


    public void UpdateAchievement(ArrayList<SelectedProduct> selectedProductsArray) {

        ContentValues v = new ContentValues();

        openWritableDatabase();
        for (SelectedProduct sp : selectedProductsArray) {
            Cursor cursorAchiv = database.query(TABLE_NAME, columns, KEY_PRODUCT_CODE + " = '" + sp.getProductCode() + "' ", null, null, null, null);
            cursorAchiv.moveToFirst();

            if(cursorAchiv.getCount()==0){

            }else {
                int currentAchiv;
                try {
                    currentAchiv=Integer.parseInt(cursorAchiv.getString(5));
                }catch (NumberFormatException num){
                    currentAchiv=0;
                }
                cursorAchiv.close();

                int newAchive = currentAchiv+sp.getNormal();

                v.put(KEY_ACHIEVEMENT, newAchive);
                database.update(TABLE_NAME, v, KEY_PRODUCT_CODE + "=?", new String[]{ sp.getProductCode()});
            }


        }

        closeDatabase();

    }

}
