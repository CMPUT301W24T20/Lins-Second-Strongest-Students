package com.example.qrcodereader.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;
import com.example.qrcodereader.ui.admin.AdminAllOptionsFrag;
import com.example.qrcodereader.ui.admin.AdminEventActivity;
import com.example.qrcodereader.ui.admin.AdminImagesOptionActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.net.URL;

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

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        region = findViewById(R.id.regionSelector);
        TextView adminButton = findViewById(R.id.admin_button);
        TextView Edit = findViewById(R.id.EditButton);
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

                Picasso.get().load(imageURL).resize(200, 200).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
                   Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        /*
            OpenAI, ChatGPT, 07/03/24
            "I want the program to check if the deviceID is in the administrator collection as ID.
            If it is then the button will display the dialog box. Otherwise it will not.
         */
        final boolean[] isAdmin = {false};
        db.collection("administrator")
                .document(deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        isAdmin[0] = true;
                    } else{
                        adminButton.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEditFrag listfrag = new ProfileEditFrag();
                listfrag.setOnSaveClickListener(ProfileActivity.this);
                listfrag.show(getSupportFragmentManager(), "Edit Profile");
            }
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                AdminAllOptionsFrag optionfrag = new AdminAllOptionsFrag();
                optionfrag.show(getSupportFragmentManager(), "Admin Actions");
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.profile;
    }

    /**
     * meow
     */
    private String CheckEmpty(String text){
        if (text == null || text.length() == 0) {return "";}
        else {return text;}
    }

    @Override
    public void onSaveClicked(String EditName, String EditEmail, String EditPhone, String EditRegion, Uri EditPicture) {
        name.setText(EditName);
        email.setText(EditEmail);
        phone.setText(EditPhone);
        region.setText(EditRegion);
        if (EditPicture != null){
            Picasso.get().load(EditPicture).resize(200, 200).centerInside().into(Picture);
        }
    }

    @Override
    protected void onResume() {
        // admin may have deleted their own profile picture, reset imageView
        super.onResume();
        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {;
                String imageURL = documentSnapshot.getString("ProfilePic");
                Picasso.get().load(imageURL).resize(200, 200).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });
    }
}
