package com.kharche;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
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
}
