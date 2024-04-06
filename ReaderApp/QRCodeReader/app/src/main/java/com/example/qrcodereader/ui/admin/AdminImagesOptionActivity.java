package com.example.qrcodereader.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.ui.profile.ProfileActivity;
import com.example.qrcodereader.ui.profile.ProfileEditFrag;

public class AdminImagesOptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_image_option);

        Button SeeProfilePic = findViewById(R.id.ViewProfilePics);
        Button SeePoster = findViewById(R.id.ViewEventPoster);

        SeePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preload("Event Posters", "EventPoster");
            }
        });

        SeeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preload("Profile Pictures", "UploadedProfilePics");
            }
        });

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    private void preload(String title, String type){
        AdminImageView listfrag = new AdminImageView(title, type);
        listfrag.populateListView(); // Fetch images before showing the dialog
        listfrag.show(getSupportFragmentManager(), "View Images");
    }
}
