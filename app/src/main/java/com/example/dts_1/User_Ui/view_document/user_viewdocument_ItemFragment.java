package com.example.dts_1.User_Ui.view_document;

import static com.example.dts_1.LogInActivity.userIdLogin;
import static com.example.dts_1.LogInActivity.userPositionLogin;
import static com.example.dts_1.LogInActivity.userTypeLogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dts_1.Admin_Ui.accesscontrol.accesscontrol_ItemRecyclerViewAdapter;
import com.example.dts_1.Admin_Ui.accesscontrol.accesscontrol_PlaceholderContent;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
import com.example.dts_1.User_Ui.create_document.createDocument;
import com.example.dts_1.User_Ui.document_history.user_documenthistory_PlaceholderContent;
import com.example.dts_1.ipGetter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class user_viewdocument_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public user_viewdocument_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static user_viewdocument_ItemFragment newInstance(int columnCount) {
        user_viewdocument_ItemFragment fragment = new user_viewdocument_ItemFragment();
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
    List<user_viewdocument_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the WiFi IP address using the ipGetter class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();

        View view = inflater.inflate(R.layout.user_viewdocument_item_list, container, false);

        //=================================Getting Data For The List=================================//
        items = fetchViewDocumentItems(Integer.parseInt(userIdLogin), wifiIpAddress, userTypeLogin);
        //=================================Getting Data For The List End=============================//


        // Find the RecyclerView in the view // replace "recycler_view" with the id of your RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist);

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new user_viewdocument_RecyclerViewAdapter(items, this::onItemClick));

        return view;
    }

    public List<user_viewdocument_PlaceholderContent.PlaceholderItem> fetchViewDocumentItems(int positionId, String wifiIpAddress, String userType) {
        JSONObject pos = new JSONObject();
        List<user_viewdocument_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        try {
            pos.put("user_id", positionId);
            pos.put("user_type", userType);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-document";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array and create a list of PlaceholderItems
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("documenttype_id");
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
                items.add(new user_viewdocument_PlaceholderContent.PlaceholderItem(id, documentId, documentTitle, documentDescription, author, creationDate, lastModifiedDate, status, documentTypeName, filePath, isDone, state, stateDesc)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void updateListAndNotify() {
        items = fetchViewDocumentItems(Integer.parseInt(userPositionLogin), wifiIpAddress, "view");
        RecyclerView recyclerView = requireView().findViewById(R.id.newlist);
        recyclerView.setAdapter(new user_viewdocument_RecyclerViewAdapter(items, this::onItemClick));
    }

    public void onItemClick(user_viewdocument_PlaceholderContent.PlaceholderItem item) {
        // Handle item click event here
        Toast.makeText(requireContext(), "Clicked item: " + item.author, Toast.LENGTH_SHORT).show();

        CustomDialogFragment dialogFragment = new CustomDialogFragment(this);

        Bundle args = new Bundle();
        args.putString("document id", item.documentId);
        args.putString("document type", item.documentTypeName);
        args.putString("documenttype id", item.id);
        args.putString("title", item.documentTitle);
        args.putString("author", item.author);
        args.putString("description", item.documentDescription);
        args.putInt("status", Integer.parseInt(item.status));
        args.putInt("isDone", Integer.parseInt(item.isDone));
        dialogFragment.setArguments(args);
        dialogFragment.show(getChildFragmentManager(), "CustomDialog");
    }

    //========================Logic for displaying the document_status form==========================//
    public static class CustomDialogFragment extends DialogFragment {
        private final user_viewdocument_ItemFragment fragment;

        public CustomDialogFragment(user_viewdocument_ItemFragment fragment) {
            this.fragment = fragment;
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceAsColor"})
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

            Button EditButton = view.findViewById(R.id.documentStatusApproveButton);
            Button CancelButton = view.findViewById(R.id.documentStatusRejectButton);

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

                EditButton.setText("EDIT");
                EditButton.setBackgroundColor(getResources().getColor(R.color.blue));
                CancelButton.setText("CANCEL");

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

            EditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getArguments() != null) {
                        //setting the textviews and button values
                        String docTypeName = getArguments().getString("document type");

                        String docTitle = getArguments().getString("title");

                        String description = getArguments().getString("description");

                        String documentId = getArguments().getString("document id");

                        String documentTypeId = getArguments().getString("documenttype id");

                        // Create a Bundle to hold the data
                        Bundle args = new Bundle();
                        args.putString("document type",docTypeName);
                        args.putString("title",docTitle);
                        args.putString("description",description);
                        args.putString("document id",documentId);
                        args.putString("documenttype id",documentTypeId);

                        // Create the fragment and set arguments
                        createDocument createDocumentFragment = new createDocument();
                        createDocumentFragment.setArguments(args);

                        // Navigate to the create document fragment
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.list, createDocumentFragment);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.addToBackStack(null); // Optional: Adds the fragment to the back stack
                        fragmentTransaction.commit();
                    }

                    // Update the list and dismiss the current fragment
                    if (fragment != null) {
                        fragment.updateListAndNotify();
                    }
                    dismiss();
                }
            });

            //=====================Updating Document Status End===================================//


            //=====================Updating Document Status(Reject)=======================================//
            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fragment != null) {
                        fragment.updateListAndNotify();
                    }
                    dismiss();
                }
            });
            //=====================Updating Document Status End===================================//

            return view;
        }
    }
    //========================Logic for displaying the document_status form end==========================//

}