package com.kharche.helpers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUploadHelper {
    private final int REQUEST_CODE = 101; // You can choose any suitable request code
    private long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB in bytes
    public String externalDbPath;

    private static FileUploadHelper instance;

    public interface IOnSetExternalDatabase {
        void setDbPath(String path);
    }

    public static  FileUploadHelper getInstance(){
        if(instance == null){
            Log.d("TAG", "set instance" );
            instance = new FileUploadHelper();

        }

        Log.d("TAG", "get instance" );
        return  instance;
    }

    public IOnSetExternalDatabase iOnSetExternalDatabase;

    // Start the file picker activity
    public void selectAndSaveFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    // Handle the result of the file picker activity
    public void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedFileUri = data.getData();

                String filePath = getRealPathFromURI(activity, selectedFileUri);
                if (filePath != null) {
                    this.setExternalDbPath(filePath);
                    Log.d("TAG", "onActivityResult: saved path " + filePath + " isSet : " + externalDbPath);
                } else {
                    Log.e("FileUploadHelper", "Could not retrieve the file path.");
                }
            }
        }
    }

    private void setExternalDbPath(String path){
        Log.d("TAG", "setExternalDbPath: " + path);
        this.externalDbPath = path;
    }

    public String getExternalDbPath(){
        return  this.externalDbPath;
    }

    // Get the actual file path from the content URI
    private String getRealPathFromURI(Activity activity, Uri contentUri) {
        try {
            // Open an InputStream from the content URI
            InputStream inputStream = activity.getContentResolver().openInputStream(contentUri);

            if (inputStream != null) {
                long fileSize = getFileSize(activity, contentUri);
                Log.d("TAG", "getRealPathFromURI: file size is exceeding " + fileSize);
                if (fileSize > MAX_FILE_SIZE) {

                    return null;
                } else {
                    // Create a temporary file to save the data
                    File tempFile = File.createTempFile("temp-", ".db", activity.getCacheDir());

                    // Copy the data from the InputStream to the temporary file
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }

                    // Close the streams
                    inputStream.close();
                    fos.close();

                    // Return the path of the temporary file
                    return tempFile.getAbsolutePath();
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.e("TAG", "getRealPathFromURI: error " + ex.getMessage());
            return null;
        }
    }

    private long getFileSize(Activity activity, Uri fileUri) {
        Cursor cursor = activity.getContentResolver().query(fileUri, null, null, null, null);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long size = cursor.getLong(sizeIndex);
        cursor.close();
        return size;
    }

}

