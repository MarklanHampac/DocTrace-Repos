package com.example.dts_1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PopulateSpinner {

    private final Context context;

    public PopulateSpinner(Context context) {
        this.context = context;
    }

    // Method to get the selected value

    public void fetchPositionsAndPopulateSpinner(Spinner spinner, String wifiIpAddress, String endpoint, String field_name, String field_id, SpinnerSelectionCallback callback) {
        String apiUrl = "http://" + wifiIpAddress + "//Module/"+endpoint; // Replace with your API endpoint
        JSONArray itemsArray = null;
        try {
            // Make a synchronous API call to get the positions array
            String jsonResponse = SendToAPI.retrieveData(new JSONObject(), apiUrl);
            itemsArray = new JSONArray(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (itemsArray != null) {

            try {

                List<String> displayValues = new ArrayList<>();
                List<Integer> associatedValues = new ArrayList<>();

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject jsonObject = itemsArray.getJSONObject(i);
                    String displayName = jsonObject.getString(field_name);
                    int associatedValue = jsonObject.getInt(field_id);

                    // Remove dashes from the displayName
                    displayName = displayName.replace("-", "");
                    displayValues.add(displayName);
                    associatedValues.add(associatedValue);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, displayValues);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView textView = (TextView) parent.getChildAt(0);
                        if (textView != null) {
                            int textColor = ContextCompat.getColor(context, R.color.third);
                            int hintColor = ContextCompat.getColor(context, R.color.hintColor);
                            textView.setTextColor(textColor);
                            textView.setHintTextColor(hintColor);
                            textView.setTextSize(18);
                        }
                        int selectedValue = associatedValues.get(position);
                        callback.onItemSelected(selectedValue); // Invoke the callback

                        Log.i("Submitted File", "onClick: " + selectedValue);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(context, "Please Select a Position", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
            }
        } else {
            // Handle null JSON array (failed API request, empty response, etc.)
            Toast.makeText(context, "Error Fetching Data", Toast.LENGTH_SHORT).show();
        }

    }

    public interface SpinnerSelectionCallback {
        void onItemSelected(int selectedValue);
    }

    public JSONArray processSelectedValue(int[] selectedValue) {
        ipGetter ipGetter = new ipGetter(context);
        String wifiIpAddress = ipGetter.getWifiIpAddress();

        JSONObject pos = new JSONObject();
        JSONArray itemsArray = new JSONArray();
        try {
            pos.put("documenttype_id", selectedValue[0]); // Use selectedValue[0] as it's an int array

            String apiUrl = "http://" + wifiIpAddress + "//Module/load-state-definition-perdoc";
            String jsonFromDatabase = SendToAPI.retrieveData(pos, apiUrl);
            Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

            // Parse the JSON array response
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String state = jsonObject.getString("state");
                String stateDesc = jsonObject.getString("state_desc");

                // Create a JSON object for each item
                JSONObject itemObject = new JSONObject();
                itemObject.put("state", state);
                itemObject.put("state_desc", stateDesc);

                // Add the JSON object to the array
                itemsArray.put(itemObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON creation or parsing error
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle other exceptions, such as network or API call errors
        }
        return itemsArray;
    }

}
