package com.igorronner.loginbackupsample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    protected static final String TAG = "DB_DEBUG";
    private static DatabaseOpenHelper sInstance;

    public static final String DATABASE_NAME = "db_example";
    protected static final int DATABASE_VERSION = 1;

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }

    public static synchronized DatabaseOpenHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private Context context;
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}