package com.kharche;


import android.content.Context;
import android.widget.ArrayAdapter;

import com.kharche.dao.CategoryDao;
import com.kharche.model.Category;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class Utils {

    public String formatDateTime(String inputDateTime) {
        String formattedDateTime = "";

        // Define your input and output date format patterns
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "EE, MMM dd, yyyy h:mm a";

        SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputPattern);
        TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");
        outputDateFormat.setTimeZone(istTimeZone);
        try {
            Date date = inputDateFormat.parse(inputDateTime);
            formattedDateTime = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDateTime;
    }

    public String getCurTime() {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(cal.getTime());

        return formattedDate;
    }
    public String formatDate(Long millisec, String format) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTimeInMillis(millisec);
        SimpleDateFormat dateFormat = new SimpleDateFormat( format);
        String formattedDate = dateFormat.format(cal.getTime());
        return formattedDate;
    }

    public HashMap<String, Object> getCategoryListAdapter(ArrayAdapter arrayAdapter, Context context) {
        HashMap<String, Integer> category_list = new HashMap<>();
        ArrayAdapter<String> dynamic_category_list = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
        if (arrayAdapter != null) {
            ArrayAdapter<String> existingCate = (ArrayAdapter<String>) arrayAdapter;
            for (int i = 0; i < existingCate.getCount(); i++) {
                dynamic_category_list.add(existingCate.getItem(i));
            }
        }

        CategoryDao categoryDao = new CategoryDao(context);
        List<Category> categories = categoryDao.getAllCategories(false);
        for (Category category : categories) {
            int categoryId = category.getId();
            String categoryName = category.getCategoryName();
            category_list.put(categoryName, categoryId);
            dynamic_category_list.add(categoryName);
        }
        HashMap<String, Object> combinedData = new HashMap<>();

        combinedData.put("category_list", dynamic_category_list);
        combinedData.put("category_key_pair", category_list);
        return combinedData;
    }
    public Calendar getYesterdayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Move one day back
        setToStartOfDay(calendar);
        return calendar;
    }

    public Calendar getYesterdayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Move one day back
        setToEndOfDay(calendar);
        return calendar;
    }

    public Calendar getLastWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1); // Move back 1 week for the start of the last week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Set to the first day of the week
        setToStartOfDay(calendar);
        return calendar;
    }

    public Calendar getCurrentWeekStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setToStartOfDay(calendar);
        return calendar;
    }

    public Calendar getLastWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        setToEndOfDay(calendar);
        return calendar;
    }

    public Calendar getLastMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Move back one month
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the previous month
        setToStartOfDay(calendar);
        return calendar;
    }

    public Calendar getLastMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Move one day back for the end of the last month
        setToEndOfDay(calendar);
        return calendar;
    }

    public Calendar getCurrentMonthStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
        setToStartOfDay(calendar);
        return calendar;
    }

    // Helper method to set the time to the start of the day (midnight)
    private void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    // Helper method to set the time to the end of the day (just before midnight)
    private void setToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public Calendar getStartToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    // Helper method to set the time to the end of the day (just before midnight)
    public Calendar  getEndToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return  calendar;
    }

    public String parseBelowTen(int number){
        return  number < 10 ? "0" + number : String.valueOf(number);
    }
}
