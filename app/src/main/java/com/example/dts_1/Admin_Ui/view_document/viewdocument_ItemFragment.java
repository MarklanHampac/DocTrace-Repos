package com.example.dts_1.Admin_Ui.view_document;

import static com.example.dts_1.LogInActivity.userIdLogin;
import static com.example.dts_1.LogInActivity.userTypeLogin;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.Admin_Ui.state_definition.statedefinition_PlaceholderContent;
import com.example.dts_1.Admin_Ui.state_definition.statedefinition_RecyclerViewAdapter;
import com.example.dts_1.Admin_Ui.view_document.viewdocument_PlaceholderContent;
import com.example.dts_1.PopulateSpinner;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
import com.example.dts_1.User_Ui.view_document.user_viewdocument_PlaceholderContent;
import com.example.dts_1.ipGetter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class viewdocument_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public viewdocument_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static viewdocument_ItemFragment newInstance(int columnCount) {
        viewdocument_ItemFragment fragment = new viewdocument_ItemFragment();
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

    List<viewdocument_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();


        //=================================Setting Up The Dialog=====================================//
        View view = inflater.inflate(R.layout.admin_view_document_item_list, container, false);


        //=================================Getting Data For The List=================================//
        items = fetchDocumentItems(Integer.parseInt(userIdLogin), userTypeLogin, wifiIpAddress);
        //=================================Getting Data For The List End=============================//


        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new viewdocument_RecyclerViewAdapter(items));

        return view;
    }

    public List<viewdocument_PlaceholderContent.PlaceholderItem> fetchDocumentItems(int positionId, String userType,String wifiIpAddress) {
        JSONObject pos = new JSONObject();
        List<viewdocument_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        try {
            //parameters for the sp
            pos.put("position_id", positionId);
            pos.put("user_type", userType);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-document";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
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
                items.add(new viewdocument_PlaceholderContent.PlaceholderItem(id, documentId, documentTitle, documentDescription, author, creationDate, lastModifiedDate, status, documentTypeName, filePath, isDone, state, stateDesc)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

}