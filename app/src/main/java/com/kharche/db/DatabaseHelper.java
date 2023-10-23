package com.kharche.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kharchev1.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TableName.CATEGORY + " (id INTEGER PRIMARY KEY AUTOINCREMENT, category CHAR(50),created_at default current_timestamp, updated default current_timestamp)");
        db.execSQL("CREATE TABLE " + TableName.SPENT_AMOUNT + " (id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER, amount INTEGER, description TEXT, created_at default current_timestamp, updated default current_timestamp)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades here
        // Typically, you would drop the existing tables and recreate them
    }
}
