package com.example.dts_1;

import static com.example.dts_1.LogInActivity.userTypeLogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dts_1.databinding.ActivityDashboardBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class DashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private String userType;
    private String wifiIpAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize userType based on your logic
        userType = userTypeLogin; // Must use the usertype of currently logged in user

        setupNavigation();
    }


    private void setupNavigation() {
        setSupportActionBar(binding.appBarDashboard.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_admin_AddUser, R.id.nav_admin_AddPosition, R.id.nav_admin_AccessControl, R.id.nav_admin_AddDocumentType,
                R.id.nav_admin_StateDefinition, R.id.nav_admin_DocumentList, R.id.nav_user_Incoming, R.id.nav_user_Pending, R.id.nav_user_CreateDocument,
                R.id.nav_user_DocumentList, R.id.nav_user_DocumentHistory)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Hide or show menu items based on userType
        Menu navMenu = navigationView.getMenu();
        MenuItem addUserAdmin = navMenu.findItem(R.id.nav_admin_AddUser);
        MenuItem addPositionAdmin = navMenu.findItem(R.id.nav_admin_AddPosition);
        MenuItem accessControlAdmin = navMenu.findItem(R.id.nav_admin_AccessControl);
        MenuItem addDocumentTypeAdmin = navMenu.findItem(R.id.nav_admin_AddDocumentType);
        MenuItem stateDefinitionAdmin = navMenu.findItem(R.id.nav_admin_StateDefinition);
        MenuItem documentListAdmin = navMenu.findItem(R.id.nav_admin_DocumentList);
        MenuItem incomingDocUser = navMenu.findItem(R.id.nav_user_Incoming);
        MenuItem pendingDocUser = navMenu.findItem(R.id.nav_user_Pending);
        MenuItem createDocUser = navMenu.findItem(R.id.nav_user_CreateDocument);
        MenuItem documentHistoryUser = navMenu.findItem(R.id.nav_user_DocumentHistory);
        MenuItem documentListUser = navMenu.findItem(R.id.nav_user_DocumentList);

        if (userType.equals("ADMIN")) {
            incomingDocUser.setVisible(false); // Show admin item
            pendingDocUser.setVisible(false); // Hide user item
            createDocUser.setVisible(false); // Hide user item
            documentHistoryUser.setVisible(false); // Hide user item
            documentListUser.setVisible(false); // Hide user item
        } else {
            addUserAdmin.setVisible(false); // Hide admin item
            addPositionAdmin.setVisible(false); // Show user item
            accessControlAdmin.setVisible(false); // Show user item
            addDocumentTypeAdmin.setVisible(false); // Show user item
            stateDefinitionAdmin.setVisible(false); // Show user item
            documentListAdmin.setVisible(false); // Show user item
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Get the WiFi IP address using the WifiUtils class
        ipGetter ipGetter = new ipGetter(this);
        wifiIpAddress = ipGetter.getWifiIpAddress();

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Handle settings action
            showConfirmationDialog();
            return true;
        }
        // Handle other menu item clicks here

        return super.onOptionsItemSelected(item);
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to Logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    // Create JSONObject
                    JSONObject sample = new JSONObject();
                    sample.put("email", "Sample");

                    String loginStatus = SendToAPI.retrieveData(sample, "http://" + wifiIpAddress + "/Module/logout-user");
                    Log.d("Logout Function", "onClick: "+ loginStatus);

                    JSONObject responseJson = new JSONObject(loginStatus);
                    String message = responseJson.optString("message");

                    if ("Logged out".equals(message)) {
                        Intent intent = new Intent(DashboardActivity.this, LogInActivity.class);
                        startActivity(intent);
                        Toast.makeText(DashboardActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DashboardActivity.this, "Error Logging Out", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}