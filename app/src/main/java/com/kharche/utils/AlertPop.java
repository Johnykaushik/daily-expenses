package com.kharche.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertPop {
    Context context;

    public AlertPop(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        // Set the title and message
//        builder.setTitle(title);
        builder.setMessage(message);

        // Set positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
