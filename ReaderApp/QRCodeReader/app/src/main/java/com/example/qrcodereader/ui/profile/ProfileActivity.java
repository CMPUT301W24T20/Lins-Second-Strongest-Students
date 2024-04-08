package com.example.qrcodereader.ui.profile;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.admin.AdminAllOptionsFrag;

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
    private String pictureURL;
    private TextView reviewLocationPermissions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.profile);

        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);
        reviewLocationPermissions = findViewById(R.id.reviewPerms);
        View view = LayoutInflater.from(this).inflate(R.layout.profile, null);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        region = findViewById(R.id.regionSelector);
        Picture = findViewById(R.id.user_profile_photo);
        TextView adminButton = findViewById(R.id.admin_button);
        TextView Edit = findViewById(R.id.EditButton);


        FirebaseFirestore db = FirestoreManager.getInstance().getDb();
        String deviceID = FirestoreManager.getInstance().getUserID();
        docRefUser = FirestoreManager.getInstance().getUserDocRef();

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                name.setText(CheckEmpty(documentSnapshot.getString("name")));
                email.setText(CheckEmpty(documentSnapshot.getString("email")));
                phone.setText(CheckEmpty(documentSnapshot.getString("phone")));
                region.setText(CheckEmpty(documentSnapshot.getString("phoneRegion")));

                pictureURL = documentSnapshot.getString("ProfilePic");

                Picasso.get().load(pictureURL).resize(200, 200).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
                   Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        /*
            OpenAI, ChatGPT, 03/07/24
            "I want the program to check if the deviceID is in the administrator collection as ID.
            If it is then the button will display the dialog box. Otherwise it will not.
         */
        // ChatGPT code start here
        db.collection("administrator")
                .document(deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().exists()) {
                        adminButton.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
        // ChatGPT code ends here

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEditFrag listfrag = new ProfileEditFrag();

                Bundle bundle = new Bundle();
                bundle.putString("name", name.getText().toString());
                bundle.putString("email", email.getText().toString());
                bundle.putString("phone", phone.getText().toString());
                bundle.putString("region", region.getText().toString());
                bundle.putString("pfp", pictureURL);
                listfrag.setArguments(bundle);

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
         //Microsoft Copilot 2024 "Create a button which takes me to location settings"
        reviewLocationPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct the user to app settings
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.profile;
    }

    /**
     * This method checks if text provided is blank
     * @param text the String retrieved from a document's field in the database
     * @return An empty string if the parameter text is null or empty, otherwise the original text
     */
    private String CheckEmpty(String text){
        if (text == null || text.isEmpty()) {return "";}
        else {return text;}
    }

    /**
     * This method alters the view of the profile display with the inputs from the edit fragment that the user wants to save
     * @param EditName the String of the input in the name input of the edit fragment
     * @param EditEmail the String of the input in the email input of the edit fragment
     * @param EditPhone the String of the input in the phone input of the edit fragment
     * @param EditRegion the String of the phone region selected in the edit fragment
     *
     */
    @Override
    public void onSaveClicked(String EditName, String EditEmail, String EditPhone, String EditRegion, Uri upload) {
        name.setText(EditName);
        email.setText(EditEmail);
        phone.setText(EditPhone);
        region.setText(EditRegion);
        if (upload != null) {
            Picasso.get().load(upload).resize(200, 200).centerInside().into(Picture);
        } else{
            onResume();
        }
    }

    /**
     * This method handles the edge case of the user being an administrator and removing their own uploaded profile picture
     * by reloading their profile picture (which is now a default picture) from the database into the profile picture ImageView
     */
    @Override
    protected void onResume() {
        super.onResume();
        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {;
                pictureURL = documentSnapshot.getString("ProfilePic");
                Picasso.get().load(pictureURL).resize(200, 200).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });
    }
}
