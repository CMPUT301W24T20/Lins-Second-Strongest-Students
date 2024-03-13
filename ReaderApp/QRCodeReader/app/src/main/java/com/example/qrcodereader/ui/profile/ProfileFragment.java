package com.example.qrcodereader.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fragment for displaying the profile of user
 * @author Tiana
 */
public class ProfileFragment extends DialogFragment {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private ImageView Picture;
    private String image;
    private DocumentReference docRefUser;
    private Uri uploaded;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_frag, null);

        // find Views
        EditText ETname = view.findViewById(R.id.name);
        EditText ETemail = view.findViewById(R.id.email);
        EditText ETphone = view.findViewById(R.id.phone);
        Button Upload = view.findViewById(R.id.UploadProfileButton);
        Button Remove = view.findViewById(R.id.RemoveProfilePicButton);
        Picture = view.findViewById(R.id.ProfilePic);

        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("users").document(deviceID);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ETname.setText(CheckEmpty(documentSnapshot.getString("name")));
                ETemail.setText(CheckEmpty(documentSnapshot.getString("email")));
                ETphone.setText(CheckEmpty(documentSnapshot.getString("phone")));

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
                        if (uploaded != null) {
                            // i have retrieved a uri image from gallery, how do i save it into the firestore database?, 3/12/24
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Context context = getContext();

                            ContentResolver contentResolver = context.getContentResolver();
                            try {
                                InputStream is = contentResolver.openInputStream(uploaded);
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = is.read(buffer)) != -1) {
                                    baos.write(buffer, 0, len);
                                }
                                byte[] imageData = baos.toByteArray();
                                String imageName = deviceID + "PICTURE" + ".png";

                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference imageRef = storageRef.child("UploadedProfilePics/" + imageName);
                                UploadTask uploadTask = imageRef.putBytes(imageData);

                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    image = uri.toString();
                                    docRefUser.update("ProfilePic",  image);
                                }).addOnFailureListener(e -> {
                                    // Handle getting download URL failure
                                });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        docRefUser.update("name",  ETname.getText().toString());
                        // do checks for valid email / phone
                        docRefUser.update("email",  ETemail.getText().toString());
                        docRefUser.update("phone",  ETphone.getText().toString());

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
            uploaded = data.getData();
            if (uploaded != null) {
                Picture.setImageURI(uploaded); // Set the image directly from URI
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