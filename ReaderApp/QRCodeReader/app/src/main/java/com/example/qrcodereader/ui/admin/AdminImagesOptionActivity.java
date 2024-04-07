package com.example.qrcodereader.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;

public class AdminImagesOptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_image_option);

        TextView SeeProfilePic = findViewById(R.id.ViewProfilePics);
        TextView SeePoster = findViewById(R.id.ViewEventPoster);

        SeePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {preload("Event Posters", "EventPoster");
            }
        });

        SeeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preload("Profile Pictures", "UploadedProfilePics");
            }
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    private void preload(String title, String type){
        AdminImageView listfrag = new AdminImageView(title, type);
        listfrag.show(getSupportFragmentManager(), "View Images");
    }
}
