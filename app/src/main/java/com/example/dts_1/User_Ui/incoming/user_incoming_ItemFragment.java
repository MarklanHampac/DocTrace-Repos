package com.example.dts_1.User_Ui.incoming;

import static com.example.dts_1.LogInActivity.userIdLogin;
import static com.example.dts_1.LogInActivity.userPositionLogin;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.PopUpDialogFragment;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
import com.example.dts_1.ipGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class user_incoming_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public user_incoming_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static user_incoming_ItemFragment newInstance(int columnCount) {
        user_incoming_ItemFragment fragment = new user_incoming_ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


    }

    int accessTypeSelectedValue;
    List<user_incoming_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the WiFi IP address using the ipGetter class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();

        View view = inflater.inflate(R.layout.user_incoming_item_list, container, false);

        //=================================Getting Data For The List=================================//
        items = fetchIncomingItems(Integer.parseInt(userPositionLogin), wifiIpAddress, "incoming");
        //=================================Getting Data For The List End=============================//

        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new user_incoming_RecyclerViewAdapter(items, this::onItemClick));

        return view;

    }
    public List<user_incoming_PlaceholderContent.PlaceholderItem> fetchIncomingItems(int positionId, String wifiIpAddress, String type) {
        JSONObject pos = new JSONObject();
        List<user_incoming_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        String jsonFromDatabase = null;
        try {
            pos.put("position_id", positionId);
            pos.put("type", type);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-document-per-position";
            jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array and create a list of PlaceholderItems
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("document_id");
                String documentId = jsonObject.getString("document_id");
                String documentTitle = jsonObject.getString("title");
                String documentDescription = jsonObject.getString("description");
                String author = jsonObject.getString("author");
                String creationDate = jsonObject.getString("creation_date");
                String lastModifiedDate = jsonObject.getString("last_modified_date");
                String status = jsonObject.getString("status");
                String documentTypeName = jsonObject.getString("documenttype_name");
                String filePath = jsonObject.getString("file_path");
                String isDone = jsonObject.getString("isDone");
                String state = jsonObject.getString("state");
                String stateDesc = jsonObject.getString("state_desc");
                String docStatus = jsonObject.getString("doc_status");
                items.add(new user_incoming_PlaceholderContent.PlaceholderItem(id, documentId, documentTitle, documentDescription, author, creationDate, lastModifiedDate, status, documentTypeName, filePath, isDone, state, stateDesc, docStatus)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Clicked item: " + jsonFromDatabase, Toast.LENGTH_SHORT).show();
        }

        return items;
    }

    private void updateListAndNotify() {
        items = fetchIncomingItems(Integer.parseInt(userPositionLogin), wifiIpAddress, "incoming");
        RecyclerView recyclerView = requireView().findViewById(R.id.newlist);
        recyclerView.setAdapter(new user_incoming_RecyclerViewAdapter(items, this::onItemClick));
    }

    public void onItemClick(user_incoming_PlaceholderContent.PlaceholderItem item) {
        // Handle item click event here
        Toast.makeText(requireContext(), "Clicked item: " + item.author, Toast.LENGTH_SHORT).show();

        CustomDialogFragment dialogFragment = new CustomDialogFragment(this);
        //Data that are passed to the custom dialog on create
        Bundle args = new Bundle();
        args.putString("document id", item.id);
        args.putString("document type", item.documentTypeName);
        args.putString("title", item.documentTitle);
        args.putString("author", item.author);
        args.putString("description", item.documentDescription);
        args.putInt("status", Integer.parseInt(item.status));
        args.putInt("isDone", Integer.parseInt(item.isDone));
        args.putInt("doc_status", Integer.parseInt(item.doc_status));
        args.putString("file_path", item.filePath);
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "CustomDialog");
    }

    //region========================Logic for displaying the document_status form==========================//
    public static class CustomDialogFragment extends DialogFragment {
        private final user_incoming_ItemFragment fragment;

        public CustomDialogFragment(user_incoming_ItemFragment fragment) {
            this.fragment = fragment;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.all_document_status_dialog, container, false);

            // Get the WiFi IP address using the WifiUtils class
            ipGetter ipGetter = new ipGetter(requireContext());
            String wifiIpAddress = ipGetter.getWifiIpAddress();

            // Initialize your dialog UI components
            TextView documentTypeName = view.findViewById(R.id.DocumentTypeView);
            TextView documentTitle = view.findViewById(R.id.DocumentTitleView);
            TextView authorTextView = view.findViewById(R.id.DocumentAuthorView);
            TextView descriptionTextView = view.findViewById(R.id.DocumentDescriptionView);

            ImageView SubmittedCheckBox = view.findViewById(R.id.SubmittedCheckBox);
            ImageView ReviewCheckBox = view.findViewById(R.id.ReviewCheckBox);
            ImageView ApprovalCheckBox = view.findViewById(R.id.ApprovalCheckBox);
            ImageView ApprovedCheckBox = view.findViewById(R.id.ApprovedCheckBox);

            Button RecieveButton = view.findViewById(R.id.documentStatusApproveButton);
            Button ReturnButton = view.findViewById(R.id.documentStatusRejectButton);

            // Retrieve the text argument and set it to the TextView
            if (getArguments() != null) {

                //setting the textviews and button values
                String docTypeName = getArguments().getString("document type");
                documentTypeName.setText(docTypeName);

                String docTitle = getArguments().getString("title");
                documentTitle.setText(docTitle);

                String author = getArguments().getString("author");
                authorTextView.setText(author);

                String description = getArguments().getString("description");
                descriptionTextView.setText(description);


                RecieveButton.setText(R.string.recieve);
                ReturnButton.setText(R.string.returns);

                int doc_status = getArguments().getInt("doc_status");
                int status = getArguments().getInt("status"); // Assuming the status is an integer value
                int isDone = getArguments().getInt("isDone"); // Assuming the isDone is an integer value

                switch (status) {
                    case 1:
                        if (isDone == 0) {
                            SubmittedCheckBox.setImageResource(R.drawable.check);
                            SubmittedCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                        }
                        break;
                    case 2:
                        if (isDone == 0) {
                            SubmittedCheckBox.setImageResource(R.drawable.check);
                            SubmittedCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ReviewCheckBox.setImageResource(R.drawable.check);
                            ReviewCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                        }
                        break;
                    case 3:
                        if (isDone == 0) {
                            SubmittedCheckBox.setImageResource(R.drawable.check);
                            SubmittedCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ReviewCheckBox.setImageResource(R.drawable.check);
                            ReviewCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ApprovalCheckBox.setImageResource(R.drawable.check);
                            ApprovalCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                        }
                        break;
                    case 4:
                        if (isDone == 0) {
                            SubmittedCheckBox.setImageResource(R.drawable.check);
                            SubmittedCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ReviewCheckBox.setImageResource(R.drawable.check);
                            ReviewCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ApprovalCheckBox.setImageResource(R.drawable.check);
                            ApprovalCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                            ApprovedCheckBox.setImageResource(R.drawable.check);
                            ApprovedCheckBox.setBackground(getResources().getDrawable(R.drawable.round_border_green));
                        }
                        break;
                    default:
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }

            TextView DownloadFile = view.findViewById(R.id.DownloadFileTextView);
            DownloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        assert getArguments() != null;
                        String remoteFilePath = getArguments().getString("file_path");

                        // Create a ContentValues object to represent the file metadata
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, remoteFilePath);
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                        // Request write access to the Downloads directory
                        ContentResolver resolver = requireContext().getContentResolver();
                        Uri uri = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                        }

                        if (uri != null) {
                            // Call the method to download the file and pass the localFilePath
                            SendToAPI.downloadFile(requireContext(),"http://" + wifiIpAddress + "/Module/download-document", uri, remoteFilePath);

                            // Show a toast message indicating successful download
                            Toast.makeText(getActivity(), "File downloaded successfully to " + uri.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            // Handle the case where the URI is null
                            Toast.makeText(getActivity(), "Failed to create a file in the Downloads directory", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle any exceptions
                    }
                }
            });


            //region=====================Updating Document Status(Receive)=======================================//
            Button SubmitButton = view.findViewById(R.id.documentStatusApproveButton);
            SubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        if (getArguments() != null) {

                            String setState = "receive";
                            int userId = Integer.parseInt(userIdLogin); // gikan sa naka log in nga user
                            String docId = getArguments().getString("document id");

                            // Sample file metadata
                            JSONObject JsonData = new JSONObject();
                            try {
                                JsonData.put("document_id", docId);
                                JsonData.put("user_id", userId);
                                JsonData.put("status", setState);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("Submitted Data", "onClick: " + JsonData);

                            SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"/Module/update-document-docstatus"); //via usb tethering ip//
                            Toast.makeText(getActivity(), "Submitted Data: " + JsonData, Toast.LENGTH_LONG).show();

                            // Show custom dialog fragment after data submission
                            PopUpDialogFragment dialogFragment = PopUpDialogFragment.newInstance("Document approved successfully.");
                            dialogFragment.show(getParentFragmentManager(), "PopUpDialogFragment");
                            // Use a handler to delay the update until the view is created
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    dialogFragment.updateGifImageView(R.drawable.approved, "Document Received",false);
                                }
                            });
                        }
                            //For refreshing the page
                            if (fragment != null) {
                                fragment.updateListAndNotify();
                            }
                            dismiss();

                    } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();

                        // Show custom dialog fragment if Error
                        PopUpDialogFragment dialogFragment = PopUpDialogFragment.newInstance("Document approved successfully.");
                        dialogFragment.show(getParentFragmentManager(), "PopUpDialogFragment");
                        // Use a handler to delay the update until the view is created
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                dialogFragment.updateGifImageView(R.drawable.approved, "Error: " + e.getMessage(),true);
                            }
                        });
                    }
                }
            });
            //endregion=====================Updating Document Status End===================================//

            //region=====================Updating Document Status(Return)=======================================//
            Button RejectButton = view.findViewById(R.id.documentStatusRejectButton);
            RejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getArguments() != null) {

                        String setState = "return";
                        int userId = Integer.parseInt(userIdLogin); // gikan sa naka log in nga user
                        String docId = getArguments().getString("document id");

                        // Sample file metadata
                        JSONObject JsonData = new JSONObject();
                        try {
                            JsonData.put("document_id", docId);
                            JsonData.put("user_id", userId);
                            JsonData.put("status", setState);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Submitted Data", "onClick: " + JsonData);
                        SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"//Module/update-document-docstatus"); //via usb tethering ip//
                        Toast.makeText(getActivity(), "Submitted Data: " + JsonData, Toast.LENGTH_LONG).show();

                    }
                    if (fragment != null) {
                        fragment.updateListAndNotify();
                    }
                    dismiss();
                }
            });
            //endregion=====================Updating Document Status End===================================//

            return view;
        }
    }
    //endregion========================Logic for displaying the document_status form end==========================//

}