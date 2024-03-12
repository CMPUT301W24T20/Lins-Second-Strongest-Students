package com.example.qrcodereader.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.Map;


/**
 * Fragment for displaying the profile of user
 * @author Tiana
 */
public class ProfileFragment extends DialogFragment {
    User userinfo;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private ImageView Picture;
    private String image;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_frag, null);

        // find Views
        EditText ETname = view.findViewById(R.id.name);
        EditText ETcontact = view.findViewById(R.id.contact);
        Button Upload = view.findViewById(R.id.UploadProfileButton);
        Button Remove = view.findViewById(R.id.RemoveProfilePicButton);
        Picture = view.findViewById(R.id.ProfilePic);

        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefUser = db.collection("users").document(deviceID);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ETname.setText(CheckEmpty(documentSnapshot.getString("name")));
                ETcontact.setText(CheckEmpty(documentSnapshot.getString("contact")));
                image = documentSnapshot.getString("ProfilePic");

                String imageURL = documentSnapshot.getString("ProfilePic");
                Picasso.get().load(imageURL).resize(100, 100).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Profile")
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        docRefUser.update("name",  ETname.getText().toString());
                        docRefUser.update("contact",  ETcontact.getText().toString());
                        docRefUser.update("ProfilePic",  image);
                    }
                });

        return builder.create();
    }

    /**
     * Open the gallery to select an image
     */
    // Google, March 4 2024, Youtube, https://www.youtube.com/watch?v=H1ja8gvTtBE
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * Handle the result of the gallery intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Picture.setImageURI(selectedImageUri); // Set the image directly from URI
                // maybe also associate it in DB
            }
        }
    }
    private String CheckEmpty(String text){
        if (text.length() == 0) {
            return "";
        } else{
            return text;
        }
    }

}