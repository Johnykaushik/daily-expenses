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
import com.kharche.utils.IDateMonthYearType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpentDao {
    private DatabaseHelper databaseHelper;
    private Utils utils = new Utils();

    public SpentDao(Context context) {
        // Initialize MyDatabaseHelper with the provided context
        databaseHelper = new DatabaseHelper(context);
    }

    public long addSpent(Spent spent) {
        ContentValues values = new ContentValues();
        values.put("amount", spent.getAmount());
        values.put("description", spent.getDescription());
        values.put("category_id", spent.getCategoryId().getId());
        if (spent.getCreatedAt() != null && !spent.getCreatedAt().isEmpty()) {
            values.put("created_at", spent.getCreatedAt());
        } else {
            values.put("created_at", utils.getCurTime());
        }

        long isAdded = databaseHelper.getReadableDatabase().insert(TableName.SPENT_AMOUNT, null, values);
        System.out.println("add spent result " + isAdded + " aaded item " + values.toString());
        return isAdded;
//        return 0;
    }

    private Spent cursorToSpent(Cursor cursor) {
        int id_index = cursor.getColumnIndex("id");
        int amount_index = cursor.getColumnIndex("amount");
        int description_index = cursor.getColumnIndex("description");
        int category_id_index = cursor.getColumnIndex("category_id");
        int category_name_index = cursor.getColumnIndex("category");
        int created_index = cursor.getColumnIndex("created_at");

        if (amount_index != -1) {
            Spent spent = new Spent();

            Integer spent_id = cursor.getInt(id_index);
            Integer amount = cursor.getInt(amount_index);
            String description = cursor.getString(description_index);
            String category_id = cursor.getString(category_id_index);
            String category_name = "";
            if (category_name_index != -1) {
                category_name = cursor.getString(category_name_index);
            }

            String created_at = cursor.getString(created_index);
            spent.setId(spent_id);
            spent.setAmount(amount);
            spent.setDescription(description);
            if (!created_at.isEmpty()) {
                spent.setCreatedAt(utils.formatDateTime(created_at));
            }

            Log.d("TAG", "cursorToSpent: " + created_at + "  id " + spent_id);

            if (category_id != null) {
                if (!category_id.isEmpty() && category_id != "0") {
                    Category category = new Category();
                    if (category_name != null && !category_name.isEmpty()) {
                        category.setCategoryName(category_name);
                    }
                    category.setId(Integer.valueOf(category_id));
                    spent.setCategoryId(category);
                }
                return spent;
            }
        }
        return null; // Handle this case as needed
    }


    public List<Spent> getAllSpents(Map<String, Object> queryParam) {
        List<Spent> spents = new ArrayList<>();
        String sortBy = "created_at";
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


            String searchQuery = "";

            ArrayList<String> queryChunk = new ArrayList<>();

//                System.out.println("debugging app 2");
            if (!queryParam.isEmpty()) {
//                    System.out.println("debugging app 3");

                if (queryParam.get("search") != null) {
//                        System.out.println("debugging app 4");

                    String search = queryParam.get("search").toString();
                    if (!search.isEmpty()) {
//                            System.out.println("debugging app 5");

                        queryChunk.add("( " + TableName.SPENT_AMOUNT + ".amount LIKE '%" + search + "%' OR " +
                                TableName.SPENT_AMOUNT + ".description LIKE '%" + search + "%' )");
                    }
                }
//                    System.out.println("debugging app 6");

                if (queryParam.get("category_id") != null) {
//                        System.out.println("debugging app 7" + queryParam.get("category_id"));
                    if (!queryParam.get("category_id").toString().isEmpty()) {
                        Integer category_id = Integer.valueOf(queryParam.get("category_id").toString());
                        queryChunk.add(" " + TableName.SPENT_AMOUNT + ".category_id = " + category_id);
                    }
                }
//                    System.out.println("debugging app 8");

                if (queryParam.get("start_date") != null && queryParam.get("end_date") != null) {
//                        System.out.println("debugging app 9");

                    Calendar startCal = (Calendar) queryParam.get("start_date");
                    Calendar endCal = (Calendar) queryParam.get("end_date");
//                        System.out.println("db query  a " + startCal.getTime() + "   " + endCal.getTime());

                    String startDate = utils.formatDate(startCal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
                    String endDate = utils.formatDate(endCal.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
//                        System.out.println("debugging app 10");

                    queryChunk.add("(" + TableName.SPENT_AMOUNT + ".created_at BETWEEN '" + startDate + "' AND '" + endDate + "' )");
                }

                if (queryParam.get("sort_price") != null) {
                    if ((boolean) queryParam.get("sort_price") == true) {
                        sortBy = "amount";
                    }
                }
            }
//                System.out.println("debugging app 11");

            if (!queryChunk.isEmpty()) {
//                    System.out.println("debugging app 12");

                String whereClause = String.join(" AND ", queryChunk);
                if (!whereClause.isEmpty()) {
//                        System.out.println("debugging app 13");

                    searchQuery += " WHERE " + whereClause;
                }
            }
            System.out.println("filterQueryfilterQuery " + searchQuery);

            String query = "";

            try {

                query = "SELECT " + TableName.SPENT_AMOUNT + ".id AS id, " +
                        TableName.SPENT_AMOUNT + ".amount AS amount, " +
                        TableName.SPENT_AMOUNT + ".created_at AS created_at, " +
                        TableName.SPENT_AMOUNT + ".description AS description, " +
                        TableName.CATEGORY + ".id AS category_id, " +
                        TableName.CATEGORY + ".category AS category " +

                        " FROM " + TableName.SPENT_AMOUNT +
                        " LEFT JOIN " + TableName.CATEGORY + " ON " +
                        TableName.CATEGORY + ".id = " + TableName.SPENT_AMOUNT + ".category_id " +
                        searchQuery +
                        " ORDER BY " + TableName.SPENT_AMOUNT + "." + sortBy + " DESC";


            } catch (Exception ex) {
                System.out.println("Debig error" + ex.getMessage());
            }

            System.out.println("db query " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                int it = 0;
                while (cursor.moveToNext()) {
                    it++;
                    System.out.println("record itereate " + it);
                    try {
                        Spent spent = cursorToSpent(cursor);

                        System.out.println("spentspent " + spent.toString());
                        if (spent != null) {
                            spents.add(spent);
                        }
                    } catch (Exception e) {
                        System.out.println("error found " + e.getMessage());
                    }
                }
                cursor.close();
            }
        } catch (Exception ex) {
            System.out.println("Fetch error " + ex.getMessage() + " >> " + ex.getStackTrace().toString());
        }
        return spents;
    }

    public Spent getSpentOne(Map<String, Object> where) {
        Spent spent = new Spent();
        ArrayList<String> queryChunk = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        for (Map.Entry<String, Object> entry : where.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                queryChunk.add(key + "='" + value + "'");
            } else {
                queryChunk.add(key + "=" + value);
            }
        }
        String whereQuery = "";
        if (queryChunk.size() > 0) {
            whereQuery = " WHERE " + String.join(" and ", queryChunk);
        }

        String query = "SELECT * FROM  " + TableName.SPENT_AMOUNT + whereQuery;
        Log.d("TAG", "getSpentOne: " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                spent = cursorToSpent(cursor);
            }
        }

        return spent;
    }

    //    get spent category wise with filter of date
    public List<Spent> spentCategoryDashboard(Calendar startDateTime, Calendar endDateTime) {
        List<Spent> spentList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<String> queryChunk = new ArrayList<>();
        String whereFilter = "";

        if (startDateTime != null && endDateTime != null) {
            String startDate = utils.formatDate(startDateTime.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
            String endDate = utils.formatDate(endDateTime.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
            queryChunk.add("(" + TableName.SPENT_AMOUNT + ".created_at BETWEEN '" + startDate + "' AND '" + endDate + "' )");
        }

        try {
            if (queryChunk.size() > 0) {
                whereFilter = " WHERE " + String.join(" AND ", queryChunk);
            }
            String query = "SELECT " + TableName.SPENT_AMOUNT + ".id AS id, " +
                    " sum(" + TableName.SPENT_AMOUNT + ".amount) AS amount, " +
                    TableName.SPENT_AMOUNT + ".category_id, " +
                    TableName.SPENT_AMOUNT + ".created_at AS created_at, " +
                    TableName.SPENT_AMOUNT + ".description AS description, " +
                    TableName.CATEGORY + ".category AS category" +
                    " FROM " + TableName.SPENT_AMOUNT +
                    " LEFT JOIN " + TableName.CATEGORY + " ON " +
                    TableName.CATEGORY + ".id = " + TableName.SPENT_AMOUNT + ".category_id" +
                    whereFilter +
                    " GROUP BY " + TableName.SPENT_AMOUNT + ".category_id, " + TableName.CATEGORY + ".category";

            System.out.println("db query  group " + query);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                int it = 0;
                while (cursor.moveToNext()) {
                    try {
                        Spent spent = cursorToSpent(cursor);
                        if (spent != null) {
                            spentList.add(spent);
                        }
                    } catch (Exception e) {
                        System.out.println("error found " + e.getMessage());
                    }
                }
                cursor.close();
            }

        } catch (Exception ex) {
            System.out.println("Fetch error group " + ex.getMessage());
        }
        return spentList;
    }

    public List<Map<String, Object>> getWeekMonthData(IDateMonthYearType group_by, int selectedCategoryId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String queryPart = " GROUP BY year, month, week ORDER BY year, month, week";
        if (group_by == IDateMonthYearType.DAY) {
            queryPart = " GROUP BY year, month, day ORDER BY year, month, day";
        } else if (group_by == IDateMonthYearType.MONTH) {
            queryPart = " GROUP BY year, month ORDER BY  year, month";
        } else if (group_by == IDateMonthYearType.YEAR) {
            queryPart = " GROUP BY year ORDER BY year";
        }
        List<Map<String, Object>> dataList = new ArrayList<>();

        String whereFilter = "";

        if (selectedCategoryId > 0) {
            whereFilter = " WHERE category_id=" + selectedCategoryId;
        }


        String query = "SELECT strftime('%Y'," + TableName.SPENT_AMOUNT + ".created_at) AS year," +
                " strftime('%m', strftime('%s'," + TableName.SPENT_AMOUNT + ".created_at),'UNIXEPOCH') AS month," +
                " strftime('%W', strftime('%s'," + TableName.SPENT_AMOUNT + ".created_at),'UNIXEPOCH') AS week," +
                " strftime('%d'," + TableName.SPENT_AMOUNT + ".created_at) AS day," +
                TableName.SPENT_AMOUNT + ".created_at AS date, " +
                " SUM (" + TableName.SPENT_AMOUNT + ".amount) AS amount " +
                " FROM " + TableName.SPENT_AMOUNT +
                whereFilter +
                queryPart;

        Log.d("TAG", "getWeekMonthData: query " + query);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    int year_index = cursor.getColumnIndex("year");
                    int month_index = cursor.getColumnIndex("month");
                    int week_index = cursor.getColumnIndex("week");
                    int day_index = cursor.getColumnIndex("day");
                    int date_index = cursor.getColumnIndex("date");
                    int total_index = cursor.getColumnIndex("amount");

                    Integer year = cursor.getInt(year_index);
                    String month = cursor.getString(month_index);
                    String week = cursor.getString(week_index);
                    String day = cursor.getString(day_index);
                    String date = cursor.getString(date_index);
                    Integer total = cursor.getInt(total_index);

                    Map<String, Object> singleData = new HashMap<>();
                    singleData.put("year", year);
                    singleData.put("month", month);
                    singleData.put("week", week);
                    singleData.put("day", day);
                    singleData.put("amount", total);
                    Log.d("TAG", group_by + " getWeekData: year " + year + " month " + month + " week " + week + " day " + day + " date " + date + " total " +
                            total);

                    dataList.add(singleData);
                } catch (Exception ex) {

                }

            }
        }
        return dataList;
    }

    public int deleteOne(int id){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String deleteId = String.valueOf(id);
        return db.delete(TableName.SPENT_AMOUNT,"id=?",new String[]{deleteId});
    }
}