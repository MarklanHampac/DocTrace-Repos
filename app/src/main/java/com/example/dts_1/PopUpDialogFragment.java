package com.example.dts_1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class PopUpDialogFragment extends DialogFragment {

    private static final String ARG_SAMPLE_TEXT = "sample_text";
    private String mSampleText;
    private GifImageView gifImageView;
    private Button button;
    private TextView text;

    public PopUpDialogFragment() {
        // Required empty public constructor
    }

    public static PopUpDialogFragment newInstance(String sampleText) {
        PopUpDialogFragment fragment = new PopUpDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SAMPLE_TEXT, sampleText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSampleText = getArguments().getString(ARG_SAMPLE_TEXT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pop_up_dialog, container, false);

        Button closeButton = view.findViewById(R.id.CloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Use dismiss() to close the DialogFragment
            }
        });
        gifImageView = view.findViewById(R.id.gifImageView);
        text = view.findViewById(R.id.NotifContent);
        button = closeButton;

        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateGifImageView(int drawableResId, String message, Boolean show) {

        if (gifImageView != null) {
            gifImageView.setImageResource((drawableResId));
            text.setText(message);
            if (show){
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.INVISIBLE);
            }

        }
    }
}
