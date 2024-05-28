package com.example.dts_1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {


    private int selectedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText signUpUsername = findViewById(R.id.signUpUsername);
        EditText signUpPassword = findViewById(R.id.signUpPass);
        EditText signUpFullname = findViewById(R.id.signUpFullname);
        EditText signUpEmail = findViewById(R.id.signUpEmail);
        Spinner signUpPosition = findViewById(R.id.positionAddUser);
        int department_id = 1; //Hard coded

        final String[] displayValues = getResources().getStringArray(R.array.positions);
        final int[] associatedValues = getResources().getIntArray(R.array.position_values);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, displayValues);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        signUpPosition.setAdapter(adapter);

        signUpPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = associatedValues[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("TAG", "onNothingSelected: ");
            }
        });

        Button SignUpButton = findViewById(R.id.buttonSignUp);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Uname = signUpUsername.getText().toString();
                String Password = signUpPassword.getText().toString();
                String Fullname = signUpFullname.getText().toString();
                String Email = signUpEmail.getText().toString();
                int Position = selectedValue;

                // Create JSONObject
                JSONObject registerData = new JSONObject();
                try {
                    registerData.put("username", Uname);
                    registerData.put("password", Password);
                    registerData.put("full_name", Fullname);
                    registerData.put("email", Email);
                    registerData.put("position_id", Position);
                    registerData.put("department_id", department_id);

                    Log.i("TAG", "onClick: " + registerData);

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON creation error (optional)
                }
                SendToAPI.sendData(registerData, "http://192.168.65.29:5000/Module/register");
            }
        });
    }
}
