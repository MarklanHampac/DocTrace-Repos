package com.example.dts_1.Admin_Ui.document_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dts_1.Admin_Ui.document_history.documenthistory_PlaceholderContent;
import com.example.dts_1.R;
import com.example.dts_1.SendToAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class documenthistory_ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public documenthistory_ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static documenthistory_ItemFragment newInstance(int columnCount) {
        documenthistory_ItemFragment fragment = new documenthistory_ItemFragment();
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
        View view = inflater.inflate(R.layout.admin_document_history_item_list, container, false);

        int position_id = 2; //need kuhaon ang position_id sa naka login nga user
        JSONObject pos = new JSONObject();
        try {
            pos.put("position_id", position_id);

        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON creation error (optional)
        }

        String jsonFromDatabase = SendToAPI.retrieveData(pos,"http://192.168.162.29:5000//Module/loadDoc" );
        Log.i("Gikan DB", "onCreateView: " + jsonFromDatabase);

        // Parse the JSON array and create a list of PlaceholderItems
        List<documenthistory_PlaceholderContent.PlaceholderItem> items = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonFromDatabase);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("user_id"); // must use the user_id of the logged in user
                String content = jsonObject.getString("author"); // replace "description" with the appropriate key for the content
                String creation_date = jsonObject.getString("description"); // replace "description" with the appropriate key for the content
                items.add(new documenthistory_PlaceholderContent.PlaceholderItem(id, content, creation_date)); // replace null with any details you want to include
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Find the RecyclerView in the view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list); // replace "recycler_view" with the id of your RecyclerView

        // Set the layout manager and adapter for the RecyclerView
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(new documenthistory_RecyclerViewAdapter(items));

        return view;
    }

}