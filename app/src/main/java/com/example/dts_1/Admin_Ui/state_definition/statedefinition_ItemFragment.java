package com.example.dts_1.Admin_Ui.state_definition;

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

import com.example.dts_1.PopulateSpinner;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;
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
public class statedefinition_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public statedefinition_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static statedefinition_ItemFragment newInstance(int columnCount) {
        statedefinition_ItemFragment fragment = new statedefinition_ItemFragment();
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

    List<statedefinition_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();

        //=================================Setting Up The Dialog=====================================//
        View view = inflater.inflate(R.layout.admin_state_definition_item_list, container, false);

        // Find the button by its ID
        FloatingActionButton AddAccessControlButton = view.findViewById(R.id.button2);
        // Set an OnClickListener for the button
        AddAccessControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(requireContext(), R.style.StyleDialog);
                dialog.setContentView(R.layout.fragment_admin_add_state_definition_form);

                EditText state = dialog.findViewById(R.id.statebox);
                EditText stateDesc = dialog.findViewById(R.id.descriptionbox);
                Spinner documentTypeId = dialog.findViewById(R.id.documentTypebox);

                //populating "state" textview based on the values in "stateDesc" textview
                stateDesc.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Process and populate the state EditText
                        processStateDesc(s.toString(), state);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                //populating the documenttype spinner
                final int[] documentTypeSelectedValue = new int[1];
                PopulateSpinner spinnerUtils = new PopulateSpinner(requireContext());
                spinnerUtils.fetchPositionsAndPopulateSpinner(documentTypeId, wifiIpAddress, "load-document-type", "documenttype_name", "documenttype_id", new PopulateSpinner.SpinnerSelectionCallback() {
                    @Override
                    public void onItemSelected(int selectedValue) {
                        documentTypeSelectedValue[0] = selectedValue;
                    }
                });

                //=====================Sending Data To DataBase======================================//

                Button SubmitButton = dialog.findViewById(R.id.save_button);
                SubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String StateDesc = stateDesc.getText().toString();
                        // Split the stateDescValue into individual states
                        String statesDesc = StateDesc.replace(", ","-");

                        String State = state.getText().toString();
                        String DocTypeId = String.valueOf(documentTypeSelectedValue[0]);

                        // Sample file metadata
                        JSONObject JsonData = new JSONObject();
                        try {

                            JsonData.put("state", State);
                            JsonData.put("state_desc", statesDesc);
                            JsonData.put("documenttype_id", DocTypeId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Submitted File", "onClick: " + JsonData);

                        SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"/Module/update-state-definition");
                        Toast.makeText(getActivity(), "Submitted File: " + JsonData, Toast.LENGTH_LONG).show();
                        updateListAndNotify();
                        dialog.dismiss();

                    }
                });

                // Find the "Cancel" button in the dialog view
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close the dialog when the "Cancel" button is clicked
                        updateListAndNotify();
                        dialog.dismiss();

                    }
                });

                // Show the dialog
                dialog.show();

                //=====================Sending Data To DataBase End==================================//
            }
        });
        //=================================Setting Dialog End========================================//


        //=================================Getting Data For The List=================================//
        items = fetchStateDefItems(1, wifiIpAddress);
        //=================================Getting Data For The List End=============================//


        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new statedefinition_RecyclerViewAdapter(items));

        return view;
    }

    public List<statedefinition_PlaceholderContent.PlaceholderItem> fetchStateDefItems(int positionId, String wifiIpAddress) {
        JSONObject pos = new JSONObject();
        List<statedefinition_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        try {
            pos.put("position_id", positionId);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-state-definition";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array and create a list of PlaceholderItems
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("documenttype_id"); // must use the user_id of the logged-in user
                String content = jsonObject.getString("state"); // replace "description" with the appropriate key for the content
                String details = jsonObject.getString("state_desc"); // replace "description" with the appropriate key for the content
                String documentTypeName = jsonObject.getString("documenttype_name"); // replace "description" with the appropriate key for the content
                items.add(new statedefinition_PlaceholderContent.PlaceholderItem(id, content, details, documentTypeName)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void updateListAndNotify() {
        items = fetchStateDefItems(1, wifiIpAddress);
        RecyclerView recyclerView = requireView().findViewById(R.id.newlist);
        recyclerView.setAdapter(new statedefinition_RecyclerViewAdapter(items));
    }

    private void processStateDesc(String stateDescValue, EditText stateEditText) {
        // Check if the input is empty
        if (stateDescValue.isEmpty()) {
            stateEditText.setText(""); // Clear the state EditText
            return;
        }

        // Split the stateDescValue into individual states
        String[] states = stateDescValue.split(", ");

        StringBuilder processedStates = new StringBuilder();

        // Loop through each state
        for (int i = 0; i < states.length; i++) {
            // Append the index or position of the state
            processedStates.append(i + 1);

            // Add "-" after each state except the last one
            if (i < states.length - 1) {
                processedStates.append("-");
            }
        }

        // Set the processed states to the state EditText
        stateEditText.setText(processedStates.toString());
    }



}