package com.kharche;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kharche.model.SmsMessage;
import com.kharche.model.SmsThread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TextMessages {
    MainActivity mainActivity;

    public  TextMessages(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public boolean isSmsPermissionGranted() {

        return ContextCompat.checkSelfPermission(mainActivity,"android.permission.READ_SMS") ==  PackageManager.PERMISSION_GRANTED;
    }

    public String formatTimestamp(long timestamp) {
        // Convert the timestamp to a Date object
        Date date = new Date(timestamp);

        // Define the desired date and time format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the Date object to a string
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public void readMessage(){
        Calendar cal = Calendar.getInstance();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),   cal.getTimeInMillis() +"-" + "918059017051-messages.txt");

        Uri inboxUri = Uri.parse("content://sms/inbox");
        List<SmsThread> smsThreads = new ArrayList<>();
        Uri uri = Uri.parse("content://sms");
        String[] projection = {"_id", "address", "body", "date", "type"};
        String selection = "address = ?"; // Filter by contact number
        String[] selectionArgs = {"+918059017051"}; // Replace with the desired contact number
        String sortOrder = "date ASC"; // Sort by date in ascending order

        Cursor cursor = mainActivity.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            SmsThread currentThread = null;

            while (cursor.moveToNext()) {
                String address = cursor.getString(1);
                String body = cursor.getString(2);
                long date = cursor.getLong(3);
                int messageType = cursor.getInt(4);

//                Log.d("TAG", "readMessage: " + " address: " + address +" body: " + body + " date:" + date + " messageType: " + messageType);

                if (currentThread == null || !currentThread.getAddress().equals(address)) {
                    // Start a new conversation thread
                    currentThread = new SmsThread(address);
                    smsThreads.add(currentThread);
                }

                SmsMessage message = new SmsMessage(address, body, date, messageType);
                currentThread.addMessage(message);
            }
            cursor.close();
        }

    try {

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));

        for (SmsThread smsThread : smsThreads) {

            // Access the conversation thread's address (phone number)
            String address = smsThread.getAddress();

            // Access the list of messages in the conversation thread
            List<SmsMessage> messages = smsThread.getMessages();

            for (SmsMessage message : messages) {
                // Access individual message properties
                String messageBody = message.getBody();
                long messageDate = message.getDate();
                int messageType = message.getMessageType(); // 1 for received, 2 for sent

                // Process or display the message details as needed
//                Log.d("MessageMessage", "From: " + address);
//                Log.d("MessageMessage", "Body: " + messageBody);
                Log.d("MessageMessage", "Date: " + formatTimestamp(messageDate));
//                Log.d("MessageMessage", "Type: " + (messageType == 1 ? "Received" : "Sent"));

                writer.write("Timestamp: " + formatTimestamp(messageDate));
                writer.newLine();
                writer.write("Type: " + messageType);
                writer.newLine();
                writer.write("Message: " + messageBody);
                writer.newLine();
                writer.newLine();
            }
        }
        writer.close();

    }catch(Exception ex){
        Log.d("TAG", "readMessage:  error " + ex.getMessage());
    }

    }

    public void requestSmsPermission() {
        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{"android.permission.READ_SMS"}, 1);
        }else {
            readMessage();
            Log.d("TAG", "requestSmsPermission: true");
        }
    }

}
