package com.example.dts_1.Admin_Ui.add_user;

import android.app.Dialog;
import android.os.Bundle;
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
public class adduser_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public adduser_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static adduser_ItemFragment newInstance(int columnCount) {
        adduser_ItemFragment fragment = new adduser_ItemFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(requireContext());
        String wifiIpAddress = ipGetter.getWifiIpAddress();
        Log.e("New Ip", "onCreateView: " + wifiIpAddress );

        //=================================Setting Up The Dialog(Pops ups when a button is clicked)=====================================//
        View view = inflater.inflate(R.layout.admin_add_user_item_list, container, false);

        // Find the button by its ID
        FloatingActionButton AddAccessControlButton = view.findViewById(R.id.button2);
        // Set an OnClickListener for the button
        AddAccessControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(requireContext(), R.style.StyleDialog);
                dialog.setContentView(R.layout.fragment_admin_add_user_form);

                // Find the "Cancel" button in the dialog view
                Button cancelButton = dialog.findViewById(R.id.addUserCancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close the dialog when the "Cancel" button is clicked
                        dialog.dismiss();
                    }
                });

                //======================Populating the Position and Department Spinners==============//
                EditText userName = dialog.findViewById(R.id.usernameAddUser);
                EditText fullName = dialog.findViewById(R.id.fullnameAddUser);
                EditText email = dialog.findViewById(R.id.emailAddUser);
                EditText password = dialog.findViewById(R.id.passwordAddUser);
                EditText userType = dialog.findViewById(R.id.userTypeAddUser);
                Spinner position = dialog.findViewById(R.id.positionAddUser);
                Spinner department = dialog.findViewById(R.id.departmentAddUser);

                final int[] positionSelectedValue = new int[1];
                final int[] departmentSelectedValue = new int[1];

                PopulateSpinner spinnerUtils = new PopulateSpinner(requireContext());
                spinnerUtils.fetchPositionsAndPopulateSpinner(position, wifiIpAddress, "load-position", "position_name", "position_id", new PopulateSpinner.SpinnerSelectionCallback() {
                    @Override
                    public void onItemSelected(int selectedValue) {
                        positionSelectedValue[0] = selectedValue;
                    }
                });
                spinnerUtils.fetchPositionsAndPopulateSpinner(department, wifiIpAddress, "load-department", "department_name", "department_id", new PopulateSpinner.SpinnerSelectionCallback() {
                    @Override
                    public void onItemSelected(int selectedValue) {
                        departmentSelectedValue[0] = selectedValue;
                    }
                });


                //==================Populating the "Position" and "Department" Spinners End==============//


                //=====================Sending Data To DataBase======================================//
                Button SubmitButton = dialog.findViewById(R.id.addUserButton);
                SubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String UserName = userName.getText().toString();
                        String FullName = fullName.getText().toString();
                        String Email = email.getText().toString();
                        String Password = password.getText().toString();
                        String UserType = userType.getText().toString();
                        int Position = positionSelectedValue[0];
                        int Department = departmentSelectedValue[0];


                        // Sample file metadata
                        JSONObject JsonData = new JSONObject();
                        try {
                            JsonData.put("username", UserName);
                            JsonData.put("full_name", FullName);
                            JsonData.put("email", Email);
                            JsonData.put("password", Password);
                            JsonData.put("position_id", Position);
                            JsonData.put("department_id", Department);
                            JsonData.put("user_type", UserType);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Submitted File", "onClick: " + JsonData);

                        int response = SendToAPI.sendData(JsonData,"http://"+ wifiIpAddress +"//Module/register-user"); //via usb tethering ip//

                        Toast.makeText(getActivity(), "Submitted User: " + JsonData, Toast.LENGTH_LONG).show();
                        Log.e("Reply Sa Database", "onClick: " + response );
                        dialog.dismiss();
                    }

                });
                //=====================Sending Data To DataBase End==================================//
                // Show the dialog
                dialog.show();
            }

        });
        //=================================Setting Dialog End========================================//



        //=================================Getting Data For The List=================================//
        int position_id = 2; //need kuhaon ang position_id sa naka login nga user
        JSONObject pos = new JSONObject();
        try {
            pos.put("position_id", position_id);

        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON creation error (optional)
        }

        String jsonFromDatabase = SendToAPI.retrieveData(pos,"http://"+ wifiIpAddress +"//Module/load-user-info" );
        Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

        // Modify Here For List Item Contents
        List<adduser_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String accessType = jsonObject.getString("access_type");
                String fullName = jsonObject.getString("full_name");
                String documenttypeName = jsonObject.getString("documenttype_name");
                String positionName = jsonObject.getString("position_name");
                items.add(new adduser_PlaceholderContent.PlaceholderItem(accessType, fullName, documenttypeName, positionName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.newlist); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new adduser_RecyclerViewAdapter(items));

        return view;
    }

}