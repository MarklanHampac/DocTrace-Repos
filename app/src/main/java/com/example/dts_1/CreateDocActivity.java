package com.example.dts_1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateDocActivity extends AppCompatActivity {
    private String FileName;
    private String FilePath;
    private int selectedValue;
    private static final int PICK_FILE_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doc);

        EditText docTitle = findViewById(R.id.docTitleText);
        EditText docDescription = findViewById(R.id.docDescriptionText);
        Spinner docDocumentType = findViewById(R.id.docTypeSpinner);

        final String[] displayValues = getResources().getStringArray(R.array.document_types);
        final int[] associatedValues = getResources().getIntArray(R.array.documenttype_values);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, displayValues);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        docDocumentType.setAdapter(adapter);

        docDocumentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = associatedValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("TAG", "onNothingSelected: ");
            }
        });


        Button SubmitButton = findViewById(R.id.submitButton);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DocTitle = docTitle.getText().toString();
                String DocDescription = docDescription.getText().toString();
                int DocumentType = selectedValue;
                String fileName = FileName;
                String filePath = FilePath;

                // Sample file metadata
                JSONObject fileData = new JSONObject();
                try {
                    fileData.put("title", DocTitle);
                    fileData.put("user_id", 3);
                    fileData.put("description", DocDescription);
                    fileData.put("author", "Mark Man");
                    fileData.put("status", 1);
                    fileData.put("documenttype_id", DocumentType);
                    fileData.put("department_id", 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Submitted File", "onClick: " + fileData);

                //SendToAPI.sendFile(fileData, filePath, fileName,"http://192.168.8.101:5000/Module/createDoc");
                Toast.makeText(CreateDocActivity.this, "Submitted File: " + filePath, Toast.LENGTH_LONG).show();
            }
        });

        ImageView selectFileButton = findViewById(R.id.uploadImageView);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
    }
        private void openFilePicker() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Set MIME type to all files
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityIfNeeded(intent, PICK_FILE_REQUEST_CODE);
        }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    FileName = getFileName(uri); // Get the actual file name
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FileName;
                    }

                    Log.i("FileName", "onActivityResult: " + FilePath);
                    Log.i("FileName", "onActivityResult: " + FileName);
                    Toast.makeText(this, "Selected file: " + FileName, Toast.LENGTH_LONG).show();
                    // Now you can use the fileName in your sendFile method
                }
            }
        }
    }

}
