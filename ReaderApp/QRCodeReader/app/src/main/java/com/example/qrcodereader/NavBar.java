package com.example.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.example.qrcodereader.ui.notifications.NotificationsActivity;
import com.example.qrcodereader.ui.notifications.NotificationsViewModel;
import com.example.qrcodereader.ui.profile.ProfileActivity;

/*
    OpenAI, ChatGPT, 30/03/2024
    "I want to create a super class which handles the navigation for
    the bottom nav bar provided xml file and class names."
 */
public abstract class NavBar extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);
    }

    // Method to be implemented by child classes to return their layout resource ID
    protected abstract int getLayoutResourceId();

    // Method to setup a TextView button
    protected void setupTextViewButton(int viewId) {
        View viewButton = findViewById(viewId);
        if (viewButton != null) {
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTextViewButtonClicked(viewId);
                }
            });
        }
    }

    // Method to handle the TextView button click
    protected void onTextViewButtonClicked(int viewId) {
        Intent intent;



        Class<?> targetClass = null;

        if (viewId == R.id.home_button) {
            targetClass = AttendeeEventActivity.class;
        }
        else if (viewId == R.id.event_button) {
            targetClass = OrganizerEventActivity.class;
        }
//        else if (viewId == R.id.scanner_button) {
//            targetClass = ScannerActivity.class; // Replace with your actual ScannerActivity class
//        }
        else if (viewId == R.id.notification_button) {
            targetClass = NotificationsActivity.class;
        }
        else if (viewId == R.id.bottom_profile_icon) {
            targetClass = ProfileActivity.class;
        }

        // Check if the target activity is the same as the current activity
        if (this.getClass().equals(targetClass)) {
            return; // If it is, do nothing
        }

        // Otherwise, start the target activity
        intent = new Intent(this, targetClass);
        startActivity(intent);
    }
}

