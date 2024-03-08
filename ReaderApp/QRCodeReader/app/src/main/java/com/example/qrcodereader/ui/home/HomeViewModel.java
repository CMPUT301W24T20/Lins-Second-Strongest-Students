package com.example.qrcodereader.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the HomeViewModel class
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * Getter method for the text
     * @return mText
     */
    public LiveData<String> getText() {
        return mText;
    }
}