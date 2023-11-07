package com.kharche.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = TableName.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context,  String externalDatabasePath) {
        super(context, externalDatabasePath, null, DATABASE_VERSION);
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

    public boolean attachExternalDatabase(String externalDatabasePath, String alias) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Attach the external database with the specified alias
        String attachSql = "ATTACH DATABASE ? AS " + alias;
        try {
            db.execSQL(attachSql, new String[]{externalDatabasePath});
            return true; // Attachment was successful
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Attachment failed
        }
    }

    public void detachExternalDatabase(String alias) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Detach the external database using the alias
        String detachSql = "DETACH DATABASE " + alias;
        db.execSQL(detachSql);
    }
}
