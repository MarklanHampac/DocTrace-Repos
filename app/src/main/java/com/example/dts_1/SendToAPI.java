package com.example.dts_1;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SendToAPI {
    public static int sendData(JSONObject jsonData, String endpoint) {
        StrictMode.ThreadPolicy policy = StrictMode.ThreadPolicy.LAX; // Allow network operations on main thread (for simplicity)
        StrictMode.setThreadPolicy(policy);

        int responseCode = -1; // Default value for response code

        try {
            URL url = new URL(endpoint); // Replace with your API URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // Write JSON data to the connection
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonData.toString().getBytes(StandardCharsets.UTF_8)); // Example using byte array

            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //returning the output data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.d("Result", "sendData() returned: " + line);
                }
                reader.close();


            } else {
                // Handle failed response
                Log.e("Error", "sendData: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Log or handle the exception as needed
        } finally {
            StrictMode.setThreadPolicy(policy); // Restore default thread policy
        }

        return responseCode;
    }

    public static String retrieveData(JSONObject jsonData, String endpoint) {
        StrictMode.ThreadPolicy policy = StrictMode.ThreadPolicy.LAX; // Allow network operations on main thread (for simplicity)
        StrictMode.setThreadPolicy(policy);

        StringBuilder responseData = new StringBuilder();
        HttpURLConnection connection = null;

        try {
            URL url = new URL(endpoint); // Replace with your API URL
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // Write JSON data to the connection
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonData.toString().getBytes(StandardCharsets.UTF_8)); // Example using byte array

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read data from the connection
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    responseData.append(line);
                }
                in.close();
            } else {
                // Handle failed response
                Log.e("LoginActivity", "API call failed with code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            StrictMode.setThreadPolicy(policy); // Restore default thread policy
        }

        return responseData.toString();
    }


    public static void sendFile(JSONObject fileData, String filePath, String filename, String endpoint) {
        // Consider using an AsyncTask or other background task handler to avoid blocking the main thread
        new Thread(() -> {
            try {
                Log.i("From Front End", "Initial filePath: " + filePath);
                File file = new File(filePath);
                if (!file.exists()) {
                    Log.e("SendToAPI", "File does not exist (Wala nakuha ang file!!)");
                    Log.i("File Not Found", "After Putting To file class: " + file);
                    Log.i("SendToDataBase", "JsonData: " + fileData);
                    Log.i("SendToDataBase", "FileName: " + filename);
                    return;
                }
                Log.i("FileFound", "File Found: " + filename);

                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---Boundary");

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                String boundary = "---Boundary";
                String lineEnd = "\r\n";
                String twoHyphens = "--";

                // Add JSON part
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"metadata\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/json; charset=UTF-8" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(fileData.toString() + lineEnd);

                // Add file part
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                outputStream.writeBytes(lineEnd);

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.writeBytes(lineEnd);

                // End of multipart/form-data
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                outputStream.flush();
                outputStream.close();
                fileInputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("SendToAPI", "API call succeeded with code: " + responseCode);
                } else {
                    Log.e("SendToAPI", "API call failed with code: " + responseCode);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void downloadFile(Context context, String apiUrl, Uri localFileUri, String remoteFilePath) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // Prepare the JSON data
                JSONObject requestData = new JSONObject();
                requestData.put("remote_filepath", remoteFilePath);
                requestData.put("local_filepath", localFileUri.toString());

                // Make the API call to the endpoint
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Write the JSON data to the request body
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(requestData.toString().getBytes());
                }

                // Check the response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // If the request was successful, proceed with file download
                    try (InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                         ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(localFileUri, "w")) {
                        if (pfd != null) {
                            try (FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor())) {
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, bytesRead);
                                }
                                fileOutputStream.flush();
                            }
                        }
                    }
                    // Log success message
                    Log.i("FileDownloader", "File downloaded successfully to " + localFileUri.toString());
                } else {
                    // Handle error response
                    Log.e("FileDownloader", "API call failed with code: " + responseCode);
                }

            } catch (Exception e) {
                // Handle any exceptions that occur during the process
                Log.e("FileDownloader", "Error downloading file", e);
            } finally {
                // Close the connection
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }


}
