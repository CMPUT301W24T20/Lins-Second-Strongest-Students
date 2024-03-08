package com.example.qrcodereader.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the NotificationsViewModel class
     */
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    /**
     * Getter method for the text
     * @return mText
     */
    public LiveData<String> getText() {
        return mText;
    }
}