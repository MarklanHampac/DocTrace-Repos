package com.example.dts_1.Admin_Ui.accesscontrol;

import static com.example.dts_1.LogInActivity.userIdLogin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
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
import java.util.Arrays;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class accesscontrol_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public accesscontrol_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static accesscontrol_ItemFragment newInstance(int columnCount) {
        accesscontrol_ItemFragment fragment = new accesscontrol_ItemFragment();
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
    List<accesscontrol_PlaceholderContent.PlaceholderItem> items;
    String wifiIpAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        wifiIpAddress = ipGetter.getWifiIpAddress();

        //=================================Setting Up The Dialog=====================================//
        View view = inflater.inflate(R.layout.admin_access_control_item_list, container, false);

        // Find the button by its ID
        FloatingActionButton AddAccessControlButton = view.findViewById(R.id.button2);
        // Set an OnClickListener for the button
        AddAccessControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(requireContext(), R.style.StyleDialog);
                dialog.setContentView(R.layout.fragment_admin_add_access_control_form);


                //=========================Populating Spinners=======================================//

                Spinner position = dialog.findViewById(R.id.positionbox);
                Spinner documentType = dialog.findViewById(R.id.doctypebox);
                Spinner accessType = dialog.findViewById(R.id.documenttypenamebox);

                final int[] positionSelectedValue = new int[1];
                final int[] documentTypetSelectedValue = new int[1];


                PopulateSpinner spinnerUtils = new PopulateSpinner(requireContext());
                spinnerUtils.fetchPositionsAndPopulateSpinner(position, wifiIpAddress, "load-position", "position_name", "position_id", new PopulateSpinner.SpinnerSelectionCallback() {
                    @Override
                    public void onItemSelected(int selectedValue) {
                        positionSelectedValue[0] = selectedValue;
                    }
                });

                spinnerUtils.fetchPositionsAndPopulateSpinner(documentType, wifiIpAddress, "load-document-type", "documenttype_name", "documenttype_id", new PopulateSpinner.SpinnerSelectionCallback() {
                    @Override
                    public void onItemSelected(int selectedValue) {
                        documentTypetSelectedValue[0] = selectedValue;


                        JSONArray itemsArray;
                        itemsArray = spinnerUtils.processSelectedValue(new int[]{documentTypetSelectedValue[0]});
                        try {

                            List<String> displayValues = new ArrayList<>();
                            List<Integer> associatedValues = new ArrayList<>();

                            for (int i = 0; i < itemsArray.length(); i++) {
                                JSONObject jsonObject = itemsArray.getJSONObject(i);
                                String stateDesc = jsonObject.getString("state_desc");
                                String state = jsonObject.getString("state");

                                // Split the state_desc and state by "-" and add each part as a separate spinner item
                                String[] stateDescParts = stateDesc.split("-");
                                String[] stateParts = state.split("-");
                                displayValues.addAll(Arrays.asList(stateDescParts));// Add each part of the state_desc as an displayValues value
                                for (String part : stateParts) {
                                    associatedValues.add(Integer.valueOf(part)); // Add each part of the state as an associated value
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_item, displayValues);
                            adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
                            accessType.setAdapter(adapter);

                            accessType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    TextView textView = (TextView) parent.getChildAt(0);
                                    if (textView != null) {
                                        int textColor = ContextCompat.getColor(requireContext(), R.color.third);
                                        int hintColor = ContextCompat.getColor(requireContext(), R.color.hintColor);
                                        textView.setTextColor(textColor);
                                        textView.setHintTextColor(hintColor);
                                        textView.setTextSize(18);
                                    }
                                    accessTypeSelectedValue = associatedValues.get(position);

                                    Log.i("Submitted File", "onClick: " + selectedValue);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toast.makeText(requireContext(), "Please Select a Position", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                        }

                        Log.d("TAG", "onItemSelected: " + itemsArray);
                    }
                });


                //========================Populating Spinners End====================================//


                //=====================Sending Data To DataBase======================================//

                Button SubmitButton = dialog.findViewById(R.id.save_button);
                SubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String Position = String.valueOf(positionSelectedValue[0]);
                        String DocumentType = String.valueOf(documentTypetSelectedValue[0]);
                        String AccessType = String.valueOf(accessTypeSelectedValue);


                        // Sample file metadata
                        JSONObject JsonData = new JSONObject();
                        try {
                            JsonData.put("position_id", Position);
                            JsonData.put("documenttype_id", DocumentType);
                            JsonData.put("access_type", AccessType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Submitted File", "onClick: " + JsonData);

                        SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"/Module/update-access-control");
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
        items = fetchAccessControlItems(Integer.parseInt(userIdLogin), wifiIpAddress);
        //=================================Getting Data For The List End=============================//

        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new accesscontrol_ItemRecyclerViewAdapter(items));

        return view;
    }

    public List<accesscontrol_PlaceholderContent.PlaceholderItem> fetchAccessControlItems(int positionId, String wifiIpAddress) {
        JSONObject pos = new JSONObject();
        List<accesscontrol_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();

        try {
            pos.put("position_id", positionId);

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-access-control";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array and create a list of PlaceholderItems
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("access_type"); // must use the user_id of the logged-in user
                String content = jsonObject.getString("position_name"); // replace "description" with the appropriate key for the content
                String creation_date = jsonObject.getString("documenttype_name"); // replace "description" with the appropriate key for the content
                items.add(new accesscontrol_PlaceholderContent.PlaceholderItem(id, content, creation_date)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return items;
    }

    private void updateListAndNotify() {
        items = fetchAccessControlItems(1, wifiIpAddress);
        RecyclerView recyclerView = requireView().findViewById(R.id.newlist);
        recyclerView.setAdapter(new accesscontrol_ItemRecyclerViewAdapter(items));
    }


}