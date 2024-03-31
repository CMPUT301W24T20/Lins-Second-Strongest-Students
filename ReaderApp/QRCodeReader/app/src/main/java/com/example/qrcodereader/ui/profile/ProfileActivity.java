package com.example.qrcodereader.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends NavBar implements ProfileEditFrag.OnSaveClickListener {
    private ImageView Picture;
    private DocumentReference docRefUser;
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView region;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);
        View view = LayoutInflater.from(this).inflate(R.layout.profile, null);

        // find Views

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        //region = findViewById(R.id.); Missing

        //Button Edit = findViewById(R.id.SaveProfileButton); Missing
        Picture = findViewById(R.id.user_profile_photo);


        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("users").document(deviceID);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                name.setText(CheckEmpty(documentSnapshot.getString("name")));
                email.setText(CheckEmpty(documentSnapshot.getString("email")));
                phone.setText(CheckEmpty(documentSnapshot.getString("phone")));
                region.setText(CheckEmpty(documentSnapshot.getString("phoneRegion")));

                String imageURL = documentSnapshot.getString("ProfilePic");
                Picasso.get().load(imageURL).resize(100, 100).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

//        Edit.setOnClickListener(new View.OnClickListener() { Missing
//            @Override
//            public void onClick(View v) {
//                ProfileEditFrag listfrag = new ProfileEditFrag();
//                listfrag.setOnSaveClickListener(ProfileActivity.this);
//                listfrag.show(getSupportFragmentManager(), "Edit Profile");
//            }
//        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.profile_activity;
    }


    /**
     * meow
     */
    private String CheckEmpty(String text){
        if (text == null || text.length() == 0) {return "";}
        else {return text;}
    }

    @Override
    public void onSaveClicked(String EditName, String EditRegion, String EditPhone, String EditEmail, Uri EditPicture) {
        name.setText(EditName);
        email.setText(EditEmail);
        phone.setText(EditPhone);
        region.setText(EditRegion);
        Picasso.get().load(EditPicture).resize(100, 100).centerInside().into(Picture);
    }
}