package com.example.qrcodereader.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 *  ViewModel for Dashboard
 *  @author Vinay
 */
public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the DashboardViewModel class
     */
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}