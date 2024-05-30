package com.example.dts_1.User_Ui.create_document;

import static com.example.dts_1.LogInActivity.userDepartmentLogin;
import static com.example.dts_1.LogInActivity.userIdLogin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.dts_1.FilePathGetter;
import com.example.dts_1.PopUpDialogFragment;
import com.example.dts_1.PopulateSpinner;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
import com.example.dts_1.ipGetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createDocument#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createDocument extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public createDocument() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment createDocument.
     */
    // TODO: Rename and change types and number of parameters
    public static createDocument newInstance(String param1, String param2) {
        createDocument fragment = new createDocument();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ActivityResultLauncher<Intent> filePickerLauncher;

    private String FileName;
    private String FilePath;
    private int selectedValue;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue with file picker
                try {
                    openFilePicker();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Permission is denied. Show a message to the user
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission permanently denied, navigate to app settings
                    showPermissionDeniedDialog();
                }
            }
        });

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        FileName = getFileName(uri);
                        FilePath = FilePathGetter.getFilePathFromUri(uri, requireContext());

                        Log.i("Uri", "onActivityResult: " + uri);
                        Log.i("FilePath", "onActivityResult: " + FilePath);
                        Log.i("FileName", "onActivityResult: " + FileName);
                        Toast.makeText(requireContext(), "Selected file: " + FileName, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    final int[] positionSelectedValue = new int[1];
    String wifiIpAddress;
    String docId;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();


        View view = inflater.inflate(R.layout.fragment_user_create_document, container, false);

        // For Updating the document
        Bundle args = getArguments();
        if (args != null) {
            // Example: Retrieve a String argument with key "key1"
            String docType = args.getString("document type");
            String docTitle = args.getString("title");
            String description = args.getString("description");
            docId = args.getString("document id");
            positionSelectedValue[0] = Integer.parseInt(args.getString("documenttype id"));

            // Populate a TextView with the retrieved value
            TextView docTitleTextView = view.findViewById(R.id.docTitleText);
            docTitleTextView.setText(docTitle);

            Spinner docDocTypeTextView = view.findViewById(R.id.docTypeSpinner);
            docDocTypeTextView.setPrompt(docType);

            TextView docDescriptionTextView = view.findViewById(R.id.docDescriptionText);
            docDescriptionTextView.setText(description);

            TextView updateButton = view.findViewById(R.id.submitButton);
            updateButton.setText("Update");
            //updateButton.setBackgroundColor(getResources().getColor(R.color.blue));
        }

        //For Selecting a File From Device
        ImageView selectFileButton = view.findViewById(R.id.uploadImageView);
        selectFileButton.setOnClickListener(v -> checkAndRequestPermission());

        //=================================Populate Spinner=========================================//
        EditText docTitle = view.findViewById(R.id.docTitleText);
        EditText docDescription = view.findViewById(R.id.docDescriptionText);
        Spinner docDocumentType = view.findViewById(R.id.docTypeSpinner);

        PopulateSpinner spinnerUtils = new PopulateSpinner(requireContext());
        spinnerUtils.fetchPositionsAndPopulateSpinner(docDocumentType, wifiIpAddress, "load-document-type", "documenttype_name", "documenttype_id", new PopulateSpinner.SpinnerSelectionCallback() {
            @Override
            public void onItemSelected(int selectedValue) {
                positionSelectedValue[0] = selectedValue;
            }
        });
        //=================================Populate Spinner End=====================================//

        //Uploading New Document
        Button SubmitButton = view.findViewById(R.id.submitButton);
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if docId is null and set a default value if it is
                int defaultDocId = 0; // Set your default value here
                int docIdValue = docId != null ? Integer.parseInt(docId) : defaultDocId;

                String DocTitle = docTitle.getText().toString();
                String DocDescription = docDescription.getText().toString();
                int DocumentType = positionSelectedValue[0];
                String fileName = FileName;
                String filePath = FilePath;

                // Sample file metadata
                JSONObject fileData = new JSONObject();
                try {
                    if (docIdValue != defaultDocId) { // Only include document_id if it's not the default value
                        fileData.put("document_id", docIdValue);
                    }
                    fileData.put("title", DocTitle);
                    fileData.put("user_id", userIdLogin);
                    fileData.put("description", DocDescription);
                    fileData.put("author", "Mark Man");
                    fileData.put("status", 1);
                    fileData.put("documenttype_id", DocumentType);
                    fileData.put("department_id", userDepartmentLogin); //from the currently logged in user

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Submitted File", "onClick: " + fileData);
                // Show custom dialog fragment after data submission
                PopUpDialogFragment dialogFragment = PopUpDialogFragment.newInstance("Document Created.");
                dialogFragment.show(getParentFragmentManager(), "PopUpDialogFragment");
                // Use a handler to delay the update until the view is created
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        dialogFragment.updateGifImageView(R.drawable.approved, "Document Saved",false);
                    }
                });
                SendToAPI.sendFile(fileData, filePath, fileName,"http://"+wifiIpAddress+"/Module/create-document");
                docTitle.setText("");
                docDescription.setText("");
                Toast.makeText(getActivity(), "Submitted File: " + fileData, Toast.LENGTH_LONG).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void openFilePicker() throws URISyntaxException {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Allow selection of all file types
        filePickerLauncher.launch(intent);
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
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
    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Show rationale and request permission
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    // Permission is permanently denied, show a dialog directing the user to settings
                    showPermissionDeniedDialog();
                }
            } else {
                // Permission already granted, proceed with file access
                try {
                    openFilePicker();
                    Toast.makeText(requireActivity(), "Select a File", Toast.LENGTH_SHORT).show();
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            // Permission already granted, proceed with file access
            try {
                openFilePicker();
                Toast.makeText(requireActivity(), "Select a File", Toast.LENGTH_SHORT).show();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Required")
                .setMessage("This app needs storage access to function. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", requireContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}