package com.example.mad_project_fun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KElectricDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "customer_requests";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ISSUE = "issue";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IMAGE = "image";  // Base64 encoded image
    private static final String COLUMN_STATUS = "status"; // To store the status (false initially)

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ISSUE + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_STATUS + " INTEGER DEFAULT 0)"; // 0 for false, 1 for true
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to insert data into the database
    public void insertRequest(String issue, String email, String message, String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISSUE, issue);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_STATUS, 0); // Set initial status to false (0)

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM your_table_name", null);
    }



    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}

