package com.example.dts_1.Admin_Ui.home;

import static com.example.dts_1.LogInActivity.userFullNameLogin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(userFullNameLogin);
    }

    public LiveData<String> getText() {
        return mText;
    }
}