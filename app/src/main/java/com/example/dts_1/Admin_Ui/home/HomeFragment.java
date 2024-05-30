package com.example.dts_1.Admin_Ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dts_1.R;
import com.example.dts_1.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView currentTimeTextView;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the TextView with the current time
            currentTimeTextView.setText(getCurrentTime());

            // Call this runnable again after 1 second
            handler.postDelayed(this, 1000);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.WelcomeText;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Find the TextView for displaying current time
        currentTimeTextView = root.findViewById(R.id.TimeText);

        // Start updating current time
        handler.post(updateTimeRunnable);

        return root;
    }

    // Method to get the current time
    private String getCurrentTime() {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());

        // Get the current time
        return sdf.format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        // Remove the callbacks to prevent memory leaks
        handler.removeCallbacks(updateTimeRunnable);
    }
}
