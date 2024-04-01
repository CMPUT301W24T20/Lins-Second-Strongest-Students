package com.example.qrcodereader.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.util.SetDefaultProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfilePictureFrag extends BottomSheetDialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_picture_frag, container, false);

        TextView Upload = view.findViewById(R.id.UploadProfile);
        TextView Remove = view.findViewById(R.id.RemoveProfile);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

        Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set default profile
                String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRefUser = db.collection("users").document(deviceID);

                // remove Uploaded Picture
                String imageName = deviceID + "PROFILEPICTURE.png";
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("UploadedProfilePics").child(imageName);
                imageRef.delete().addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image deleted successfully: " + imageName);
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "Error deleting image " + imageName + ": " + exception.getMessage());
                });

                SetDefaultProfile.generate(deviceID, 2, null, docRefUser, new SetDefaultProfile.ProfilePicCallback() {
                    @Override
                    public void onImageURLReceived(String imageURL) {
                        ProfileEditFrag editProfile = (ProfileEditFrag) requireActivity().getSupportFragmentManager().findFragmentByTag("Edit Profile");
                        editProfile.setPicture(null, imageURL);
                    }
                });
            }
        });
        return view;
    }

    // Google, March 4 2024, Youtube, https://www.youtube.com/watch?v=H1ja8gvTtBE
    /**
     * Handle the result of the gallery intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri uploaded = data.getData();
            if (uploaded != null) {
                ProfileEditFrag editProfile = (ProfileEditFrag) requireActivity().getSupportFragmentManager().findFragmentByTag("Edit Profile");
                editProfile.setPicture(uploaded, null); // Set the image directly from URI
            }
        }
    }
}
