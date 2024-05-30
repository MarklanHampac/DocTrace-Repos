package com.example.dts_1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dts_1.Admin_Ui.add_user.adduser_PlaceholderContent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


public class LogInActivity extends AppCompatActivity {
    public static String userEmailLogin;
    public static String userPasswordLogin;
    public static String userPositionLogin;
    public static String userIdLogin;
    public static String userTypeLogin;
    public static String userDepartmentLogin;
    public static String userFullNameLogin;
    public static String userNameLogin;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLoginText);
        password = findViewById(R.id.passwordLoginText);

        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(this);
        String wifiIpAddress = ipGetter.getWifiIpAddress();

        Button LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                // Create JSONObject
                JSONObject loginData = new JSONObject();
                try {

                    loginData.put("email", userEmail);
                    loginData.put("password", userPassword);

                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON creation error (optional)
                }

                String loginStatus = SendToAPI.retrieveData(loginData, "http://" + wifiIpAddress + "/Module/check-login-status");
                Log.d("Check login status", "onClick: " + loginStatus);

                int response = 0;
                try {
                    JSONObject statusJson = new JSONObject(loginStatus);
                    boolean loggedIn = statusJson.getBoolean("logged_in");

                    if (!loggedIn) {
                        Log.d("Login Data", loginData.toString());
                        response = SendToAPI.sendData(loginData, "http://" + wifiIpAddress + "/Module/login-user");
                        Log.d("Login Response", "onClick: " + response);

                        Intent loginIntent = new Intent(LogInActivity.this, DashboardActivity.class);
                        startActivity(loginIntent);
                    } else {
                        Toast.makeText(LogInActivity.this, "User Already Logged In", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //add conditions before changing to dashboard
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please Fill The Fields", Toast.LENGTH_LONG).show();
                } else {
                        if (response == HttpURLConnection.HTTP_OK) {

                            JSONObject userInfo = new JSONObject();
                            try {
                                userInfo.put("email", userEmail);
                                userInfo.put("password", userPassword);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Handle JSON creation error (optional)
                            }

                            String jsonFromDatabase = SendToAPI.retrieveData(userInfo,"http://"+ wifiIpAddress +"//Module/load-user" );
                            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

                            // Modify Here For List Item Contents(Userdata)
                            try {
                                JSONArray jsonArray = new JSONArray(jsonFromDatabase);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int userId = Integer.parseInt(jsonObject.getString("user_id"));
                                    String userName = jsonObject.getString("username");
                                    String userPass = jsonObject.getString("password");
                                    String userFullName = jsonObject.getString("full_name");
                                    String emailUser = jsonObject.getString("email");
                                    int userPositionId = Integer.parseInt(jsonObject.getString("position_id"));
                                    int userDepartmentId = Integer.parseInt(jsonObject.getString("department_id"));
                                    String userType = jsonObject.getString("user_type");

                                    userIdLogin = String.valueOf(userId);
                                    userNameLogin = userName;
                                    userFullNameLogin = userFullName;
                                    userPasswordLogin = userPass;
                                    userEmailLogin = emailUser;
                                    userPositionLogin = String.valueOf(userPositionId);
                                    userDepartmentLogin = String.valueOf(userDepartmentId);
                                    userTypeLogin = userType;

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//                            Intent signupIntent = new Intent(LogInActivity.this, DashboardActivity.class);
//                            startActivity(signupIntent);

                            Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(LogInActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }
                }
            }

        });
//        //Temporary signup
//        TextView SignUpText = findViewById(R.id.SignUpText);
//        SignUpText.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent signupIntent = new Intent(LogInActivity.this, SignupActivity.class);
//                startActivity(signupIntent);
//            }
//        });
    }
}


