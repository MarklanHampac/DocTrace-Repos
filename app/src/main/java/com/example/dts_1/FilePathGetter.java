package com.example.dts_1;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import java.io.File;

public class FilePathGetter {
    public static String getFilePathFromUri(Uri uri, Context context) {
        String filePath = null;

        if ("content".equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                // Query for display name
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    // Get display name
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        String displayName = cursor.getString(displayNameIndex);

                        // Get absolute path of the directory where the file is stored
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        // Construct full path by appending file name to directory path
                        File file = new File(dir, displayName);
                        filePath = file.getAbsolutePath();
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        if (filePath == null) {
            // Fallback to URI path if file path couldn't be retrieved
            filePath = uri.getPath();
        }

        return filePath;
    }
}
