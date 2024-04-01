package com.example.qrcodereader.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcodereader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

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
                CollectionReference ColRefPic = db.collection("DefaultProfilePics");
                ColRefPic.document("P4").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the value of the string field
                                String imageURL = document.getString("URL");
                                docRefUser.update("ProfilePic",  imageURL);
                                ProfileEditFrag editProfile = (ProfileEditFrag) requireActivity().getSupportFragmentManager().findFragmentByTag("Edit Profile");
                                editProfile.setPicture(null, imageURL);
                            } else {
                            }
                        } else {
                        }
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
