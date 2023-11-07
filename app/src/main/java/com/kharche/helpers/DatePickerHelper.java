package com.kharche.helpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.kharche.interfaces.IDatePicker;

import java.util.Calendar;

public class DatePickerHelper  {

    Context context;
    IDatePicker iDatePicker;

    public DatePickerHelper(Context context, IDatePicker idatePicker) {
        this.context = context;
        this.iDatePicker = idatePicker;
    }

    public void showDatePicker(boolean isStartDate, Calendar minDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                iDatePicker.getPickerDate(isStartDate, dayOfMonth,monthOfYear, year);
            }
        },
                year, month, day);
        Long maxTime = c.getTimeInMillis();
        if (!isStartDate) {
            if (minDate != null) {
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            }
        }
        datePickerDialog.getDatePicker().setMaxDate(maxTime);
        datePickerDialog.show();
    }
}
