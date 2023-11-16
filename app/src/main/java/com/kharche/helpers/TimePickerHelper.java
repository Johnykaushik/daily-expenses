package com.kharche.helpers;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.kharche.interfaces.ITimePicker;

import java.util.Calendar;

public class TimePickerHelper implements TimePickerDialog.OnTimeSetListener {

    Context context;
    ITimePicker iTimePicker;

    public TimePickerHelper(Context context, ITimePicker iTimePicker) {
        this.context = context;
        this.iTimePicker = iTimePicker;
    }

    public void showTimePicker() {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, this, hour, minute, false);
        timePickerDialog.show();

        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        iTimePicker.getPickerTime(hourOfDay, minute);
    }
}
