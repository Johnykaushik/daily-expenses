package com.kharche.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kharche.Utils;
import com.kharche.db.DatabaseHelper;
import com.kharche.db.TableName;
import com.kharche.model.Category;
import com.kharche.model.Spent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SpentDao {
    private DatabaseHelper databaseHelper;
    private  Utils utils = new Utils();

    public SpentDao(Context context) {
        // Initialize MyDatabaseHelper with the provided context
        databaseHelper = new DatabaseHelper(context);
    }

    public long addSpent(Spent spent) {
        ContentValues values = new ContentValues();
        values.put("amount", spent.getAmount());
        values.put("description", spent.getDescription());
        values.put("category_id", spent.getCategoryId().getId());
        values.put("created_at", utils.getCurTime());
        long isAdded = databaseHelper.getReadableDatabase().insert(TableName.SPENT_AMOUNT, null, values);
        return isAdded;
    }

    public List<Spent> getAllSpents() {
        List<Spent> spents = new ArrayList<>();
        try {

            SQLiteDatabase db = databaseHelper.getReadableDatabase();
//            String tableName = TableName.SPENT_AMOUNT;
//            String query1 = "PRAGMA table_info(" + tableName + ");";
//
//            Cursor cursor1 = db.rawQuery(query1, null);
//            if (cursor1 != null) {
//                if (cursor1.moveToFirst()) {
//                    do {
//                        int nameIndex = cursor1.getColumnIndex("name");
//                        String columnName = cursor1.getString(nameIndex);
//
//                        // Print the column names
//                        Log.d("TableInfo", "Column Name: " + columnName);
//                    } while (cursor1.moveToNext());
//                }
//                cursor1.close();
//            }


            String query = "SELECT " + TableName.SPENT_AMOUNT + ".id AS id, " +
                    TableName.SPENT_AMOUNT + ".amount AS amount, " +
                    TableName.SPENT_AMOUNT + ".created_at AS created_at, " +
                    TableName.SPENT_AMOUNT + ".description AS description, " +
                    TableName.CATEGORY + ".id AS category_id, " +
                    TableName.CATEGORY + ".category AS category " +
                    " FROM " + TableName.SPENT_AMOUNT +
                    " JOIN " + TableName.CATEGORY + " ON " +
                    TableName.CATEGORY + ".id = " + TableName.SPENT_AMOUNT + ".category_id " +
                    " ORDER BY " + TableName.SPENT_AMOUNT + ".id DESC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {

                        int id_index = cursor.getColumnIndex("id");
                        int amount_index = cursor.getColumnIndex("amount");
                        int description_index = cursor.getColumnIndex("description");
                        int category_id_index = cursor.getColumnIndex("category_id");
                        int category_name_index = cursor.getColumnIndex("category");
                        int created_index = cursor.getColumnIndex("created_at");

                        if (amount_index != -1) {
                            Integer spent_id = cursor.getInt(id_index);
                            Integer amount = cursor.getInt(amount_index);
                            String description = cursor.getString(description_index);
                            String category_id = cursor.getString(category_id_index);
                            String category_name = cursor.getString(category_name_index);
                            String created_at = cursor.getString(created_index);


                            Spent spent = new Spent();
                            spent.setId(spent_id);
                            spent.setAmount(amount);
                            spent.setDescription(description);

                            if(!created_at.isEmpty()){
                                spent.setCreatedAt(utils.formatDateTime(created_at));
                            }

                            if (!category_id.isEmpty()) {
                                Category category = new Category();
                                category.setCategoryName(category_name);
                                category.setId(Integer.valueOf(category_id));
                                spent.setCategoryId(category);
                            }
                            spents.add(spent);
                        }
                    } catch (Exception e) {
                        System.out.println("error found " + e.getMessage());
                    }
                }
                cursor.close();
            }
        } catch (Exception ex) {
            System.out.println("Fetch error " + ex.getMessage() + " >> " + ex.getStackTrace());
        }
        return spents;
    }
}
