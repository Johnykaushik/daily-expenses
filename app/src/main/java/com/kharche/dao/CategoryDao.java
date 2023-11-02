package com.kharche.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kharche.db.TableName;
import com.kharche.db.DatabaseHelper;
import com.kharche.model.Category;
import com.kharche.model.Spent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDao {
    private DatabaseHelper databaseHelper;
    private Context context;

    public CategoryDao(Context context) {
        // Initialize MyDatabaseHelper with the provided context
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public Category getCategory(String category_name) {
        Category category = new Category();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT id, category FROM  " + TableName.CATEGORY + " WHERE category= '" + category_name + "'";
        Log.d("TAG", "getCategory: " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int categoryIndex = cursor.getColumnIndex("category");
                int idIndex = cursor.getColumnIndex("id");
                if (categoryIndex != -1) {
                    String categoryName = cursor.getString(categoryIndex);
                    Integer cat_id = cursor.getInt(idIndex);
                    category.setId(cat_id);
                    category.setCategoryName(categoryName);
                }
            }
        }
        return category;
    }

    public long addCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put("category", category.getCategoryName());
        long isAdded = databaseHelper.getReadableDatabase().insert(TableName.CATEGORY, null, values);
        return isAdded;
    }

    public List<Category> getAllCategories(boolean maxByAssociated) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();


    String sortBy = maxByAssociated ? "associated_spent" : "id";
        String query = "SELECT  (SELECT COUNT(*) FROM " + TableName.SPENT_AMOUNT + " WHERE " + TableName.SPENT_AMOUNT + ".category_id = " + TableName.CATEGORY + ".id) as associated_spent, id, category FROM  " + TableName.CATEGORY + " order by " + sortBy + " desc";

        Cursor cursor = db.rawQuery(query, null);

        System.out.println("queryquery " + query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    int categoryIndex = cursor.getColumnIndex("category");
                    int idIndex = cursor.getColumnIndex("id");
                    int associated_spent_index = cursor.getColumnIndex("associated_spent");
                    if (categoryIndex != -1) {
                        String category = cursor.getString(categoryIndex);
                        Integer cat_id = cursor.getInt(idIndex);
                        Category categoryBase = new Category();
                        categoryBase.setId(cat_id);
                        categoryBase.setCategoryName(category);
                        if (associated_spent_index != -1) {
                            String associatedSpent = cursor.getString(associated_spent_index);
                            categoryBase.setAssociatedSpent(Integer.valueOf(associatedSpent));
                        }
                        categories.add(categoryBase);
                    }
                } catch (Exception e) {
                    System.out.println("sssssssssss eee " + e.getMessage());
                }
            }
            cursor.close();
        }
        return categories;
    }

    private int deleteCategoryById(long id) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        return db.delete(TableName.CATEGORY, "id=" + id, null);
    }

    public int deleteCategory(long id) {
        SpentDao spentDao = new SpentDao(this.context);
        Map<String, Object> where = new HashMap<>();
        where.put("category_id", id);
        Spent spent = spentDao.getSpentOne(where);

        if (spent != null) {
            if (spent.getId() != 0) {
                return 0; //  category associate with spent, can't be delete
            } else {
                int isDeleted = deleteCategoryById(id);
                return isDeleted == 1 ? 1 : 2;
                // is truly deleted, 2 either deleted already or failed to delete
            }
        }
        return 3; // nothing happend, try again
    }
}
