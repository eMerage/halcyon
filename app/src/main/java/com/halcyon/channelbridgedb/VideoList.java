package com.halcyon.channelbridgedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.halcyon.channelbridgehelp.VideoObject;

import java.util.ArrayList;

/**
 * Created by Hasitha on 4/29/15.
 */
public class VideoList {


    private static final String KEY_ROW_ID = "row_id";
    private static final String TEXT = "text";
    private static final String VIDEO_ID = "video_id";


    String[] columns = new String[] { KEY_ROW_ID, TEXT, VIDEO_ID};

    private static final String VIDEO_DEMOS_TABLE = "video_demos";

    private static final String TABLE_NAME = "video_demos";
    public final Context repContext;
    public DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private static final String REPS_CREATE = "CREATE TABLE "
            + TABLE_NAME
            + " ("
            + KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEXT + " TEXT NOT NULL, "
            + VIDEO_ID+" TEXT "
            + " );";



    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(REPS_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public VideoList(Context c) {
        repContext = c;
    }

    public VideoList openWritableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(repContext);
        database = databaseHelper.getWritableDatabase();
        return this;

    }

    public VideoList openReadableDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper(repContext);
        database = databaseHelper.getReadableDatabase();
        return this;

    }

    public void closeDatabase() throws SQLException {
        databaseHelper.close();
    }


    public long insertVideo(String text,String videoId)throws SQLException{


        ContentValues cv = new ContentValues();
        cv.put(TEXT, text);
        cv.put(VIDEO_ID, videoId);

        return database.insert(TABLE_NAME, null, cv);

    }


    public ArrayList<VideoObject> getVideoDetails() {
        ArrayList<VideoObject> reps = new ArrayList<VideoObject>();

        Cursor cursor = database.query(TABLE_NAME, columns, null, null, null,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            reps.add(new VideoObject(cursor.getString(1),cursor.getString(2)));
            cursor.moveToNext();
        }

        cursor.close();

        return reps;
    }


}
