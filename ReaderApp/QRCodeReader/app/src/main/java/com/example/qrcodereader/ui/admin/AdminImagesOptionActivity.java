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
                AdminImageView listfrag = new AdminImageView();
                Bundle bundle = new Bundle();
                bundle.putString("Title", "Posters");
                bundle.putString("Type", "EventPoster");
                listfrag.setArguments(bundle);
                listfrag.show(getSupportFragmentManager(), "View Posters");
            }
        });

        SeeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminImageView listfrag = new AdminImageView();
                Bundle bundle = new Bundle();
                bundle.putString("Title", "Profile Pictures");
                bundle.putString("Type", "UploadedProfilePics");
                listfrag.setArguments(bundle);
                listfrag.show(getSupportFragmentManager(), "View Posters");
            }
        });

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }
}
