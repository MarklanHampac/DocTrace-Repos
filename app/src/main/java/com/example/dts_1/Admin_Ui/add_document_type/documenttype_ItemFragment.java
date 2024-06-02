package com.example.dts_1.Admin_Ui.add_document_type;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.Admin_Ui.accesscontrol.accesscontrol_ItemRecyclerViewAdapter;
import com.example.dts_1.Admin_Ui.accesscontrol.accesscontrol_PlaceholderContent;
import com.example.dts_1.Admin_Ui.add_document_type.documenttype_PlaceholderContent;
import com.example.dts_1.PopulateSpinner;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
import com.example.dts_1.ipGetter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class documenttype_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public documenttype_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static documenttype_ItemFragment newInstance(int columnCount) {
        documenttype_ItemFragment fragment = new documenttype_ItemFragment();
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
    List<documenttype_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();

        //=================================Setting Up The Dialog=====================================//
        View view = inflater.inflate(R.layout.admin_document_type_item_list, container, false);

        // Find the button by its ID
        FloatingActionButton AddAccessControlButton = view.findViewById(R.id.button2);
        // Set an OnClickListener for the button
        AddAccessControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Dialog dialog = new Dialog(requireContext(), R.style.StyleDialog);
                dialog.setContentView(R.layout.fragment_admin_add_document_type_form);

                EditText documentTypeName = dialog.findViewById(R.id.documenttypenamebox);


                //=====================Sending Data To DataBase======================================//

                Button SubmitButton = dialog.findViewById(R.id.save_button);
                SubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String DocumentTypeName = documentTypeName.getText().toString();

                        // Sample file metadata
                        JSONObject JsonData = new JSONObject();
                        try {
                            JsonData.put("documenttype_name", DocumentTypeName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Submitted File", "onClick: " + JsonData);

                        SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"/Module/update-document-type");
                        Toast.makeText(getActivity(), "Submitted File: " + JsonData, Toast.LENGTH_LONG).show();
                        updateListAndNotify();
                        dialog.dismiss();

                    }
                });
                //=====================Sending Data To DataBase End==================================//

                // Find the "Cancel" button in the dialog view
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close the dialog when the "Cancel" button is clicked
                        updateListAndNotify();//update list

                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();

            }
        });
        //=================================Setting Dialog End========================================//


        //=================================Getting Data For The List=================================//
        items = fetchDocumentTypeItems(1, wifiIpAddress);
        //=================================Getting Data For The List End=============================//

        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new documenttype_ItemRecyclerViewAdapter(items));

        return view;
    }

    public List<documenttype_PlaceholderContent.PlaceholderItem> fetchDocumentTypeItems(int positionId, String wifiIpAddress) {
        JSONObject pos = new JSONObject();
        List<documenttype_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        try {
            pos.put("position_id", positionId);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-document-type";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array and create a list of PlaceholderItems
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("documenttype_id"); // must use the user_id of the logged-in user
                String content = jsonObject.getString("documenttype_name"); // replace "description" with the appropriate key for the content
                items.add(new documenttype_PlaceholderContent.PlaceholderItem(id, content, null)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void updateListAndNotify() {
        items = fetchDocumentTypeItems(1, wifiIpAddress);
        RecyclerView recyclerView = requireView().findViewById(R.id.newlist);
        recyclerView.setAdapter(new documenttype_ItemRecyclerViewAdapter(items));
    }

}