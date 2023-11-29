package com.ccs114.fisda.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Helper class for managing the SQLite database used in the FiSDA application.
 */
public class CollectionsDbHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DB_NAME = "DbFisda.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "tbl_collections";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FILENAME = "filename";
    private static final String COLUMN_DATE_TAKEN = "date_taken";
    private static final String COLUMN_FILEPATH = "filepath";
    private static final String COLUMN_IMAGEURI = "image_uri";
    private static final String COLUMN_1ST_NAME = "first_name";
    private static final String COLUMN_2ND_NAME = "second_name";
    private static final String COLUMN_3RD_NAME = "third_name";
    private static final String COLUMN_1ST_CONF = "first_confidence";
    private static final String COLUMN_2ND_CONF = "second_confidence";
    private static final String COLUMN_3RD_CONF = "third_confidence";

    public CollectionsDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +  " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILENAME + " TEXT, " +
                COLUMN_DATE_TAKEN + " DATE, " +
                COLUMN_IMAGEURI + " TEXT, " +
                COLUMN_FILEPATH + " TEXT, " +
                COLUMN_1ST_NAME + " TEXT, " +
                COLUMN_2ND_NAME + " TEXT, " +
                COLUMN_3RD_NAME + " TEXT, " +
                COLUMN_1ST_CONF + " TEXT, " +
                COLUMN_2ND_CONF + " TEXT, " +
                COLUMN_3RD_CONF + " TEXT ) ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addFishData(String currentPhotoPath, String imageUri, String imageFileName, String[] topFishSpecies, String[] topConfidences) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COLUMN_FILENAME, imageFileName);
        content.put(COLUMN_FILEPATH, currentPhotoPath);
        content.put(COLUMN_IMAGEURI, imageUri);
        content.put(COLUMN_1ST_NAME, topFishSpecies[0]);
        content.put(COLUMN_2ND_NAME, topFishSpecies[1]);
        content.put(COLUMN_3RD_NAME, topFishSpecies[2]);

        content.put(COLUMN_1ST_CONF, topConfidences[0]);
        content.put(COLUMN_2ND_CONF, topConfidences[1]);
        content.put(COLUMN_3RD_CONF, topConfidences[2]);

        content.put(COLUMN_DATE_TAKEN,new SimpleDateFormat("dd-MMM-yyyy")
                .format(Calendar.getInstance().getTime()));

        long result = db.insert(TABLE_NAME, null, content);

        if (result == -1) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "Image Saved Successfully.", Toast.LENGTH_SHORT).show();

        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;

        if(db != null) {
            cursor = db.rawQuery(query, null);

        }
        return cursor;
    }
}
