package com.example.dts_1.Admin_Ui.add_user;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.example.dts_1.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link admin_add_user_form#newInstance} factory method to
 * create an instance of this fragment.
 */
public class admin_add_user_form extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public admin_add_user_form() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUser.
     */
    // TODO: Rename and change types and number of parameters
    public static admin_add_user_form newInstance(String param1, String param2) {
        admin_add_user_form fragment = new admin_add_user_form();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private int selectedValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_user_form, container, false);

        EditText userNameAddUser = view.findViewById(R.id.usernameAddUser);
        EditText fullnameAddUser = view.findViewById(R.id.fullnameAddUser);
        EditText emailAddUser = view.findViewById(R.id.emailAddUser);
        Spinner positionAddUser = view.findViewById(R.id.positionAddUser);
        EditText passwordAddUser = view.findViewById(R.id.passwordAddUser);
        int department_id = 1; //Hard coded

        final String[] displayValues = getResources().getStringArray(R.array.positions);
        final int[] associatedValues = getResources().getIntArray(R.array.position_values);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_item, displayValues);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        positionAddUser.setAdapter(adapter);

        positionAddUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) parent.getChildAt(0);
                if (textView != null) {
                    int color = ContextCompat.getColor(requireContext(), R.color.hintColor);
                    textView.setTextColor(color);
                    textView.setTextSize(18);
                }

                selectedValue = associatedValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("TAG", "onNothingSelected: ");
            }
        });

        Button AddUserButton = view.findViewById(R.id.addUserButton);
        AddUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Uname = userNameAddUser.getText().toString();
                String Fullname = fullnameAddUser.getText().toString();
                String Email = emailAddUser.getText().toString();
                int Position = selectedValue;
                String Password = passwordAddUser.getText().toString();
                int DepartmentId = 1;

                // Create JSONObject
                JSONObject registerData = new JSONObject();
                try {
                    registerData.put("username", Uname);
                    registerData.put("full_name", Fullname);
                    registerData.put("department_id", department_id);
                    registerData.put("email", Email);
                    registerData.put("position_id", Position);
                    registerData.put("password", Password);

                    Log.i("TAG", "onClick: " + registerData);

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON creation error (optional)
                }
//                SendToAPI.sendData(registerData, "http://192.168.8.101:5000/Module/register");
                Toast.makeText(requireContext(), "Sent to DataBase" + registerData, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}