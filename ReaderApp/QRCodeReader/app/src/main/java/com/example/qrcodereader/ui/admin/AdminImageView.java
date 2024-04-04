package com.example.qrcodereader.ui.admin;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;
import com.example.qrcodereader.ui.profile.ProfileActivity;
import com.example.qrcodereader.ui.profile.ProfileEditFrag;
import com.example.qrcodereader.util.SetDefaultProfile;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImageView extends DialogFragment {
    private GridView imageGridView;
    private List<String> selectedImages;
    private ImageAdapter adapter;
    private StorageReference storageRef;
    private String TypeRef;


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_activity_admin_image_list, null);

        String Title = getArguments().getString("Title");
        TypeRef = getArguments().getString("Type");

        storageRef = FirebaseStorage.getInstance().getReference().child(TypeRef);

        imageGridView = view.findViewById(R.id.imageGridView);
        selectedImages = new ArrayList<>();
        adapter = new ImageAdapter(requireContext(), selectedImages);
        imageGridView.setAdapter(adapter);

        populateListView();

        imageGridView.setOnItemClickListener((parent, view1, position, id) -> {
            String imageUrl = (String) parent.getItemAtPosition(position);
            if (selectedImages.contains(imageUrl)) { // deselect
                selectedImages.remove(imageUrl);
            } else {
                selectedImages.add(imageUrl); //select
            }
            adapter.notifyDataSetChanged();
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle(Title)
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    // able to press Save Button, it is not greyed out == input in edit texts are valid
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedImages();
                    }
                });

        AlertDialog alertDialog = builder.create();


        return alertDialog;
    }

    private void populateListView() {
        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    selectedImages.add(uri.toString());
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    private void deleteSelectedImages() {
        List<String> imagesToDelete = adapter.getSelectedImages();
        for (String imageUrl : imagesToDelete) {
            // Decode the image URL to extract the image name
            String decodedImageUrl = Uri.decode(imageUrl);
            String imageToken = decodedImageUrl.substring(decodedImageUrl.lastIndexOf("/") + 1);
            int indexOfQuestionMark = imageToken.indexOf('?');
            String imageName = imageToken.substring(0, indexOfQuestionMark);

            if (TypeRef.equals("UploadedProfilePics")){
                String deviceID = imageName.substring(0, 16);
                DocumentReference docRefUser = FirebaseFirestore.getInstance().collection("users").document(deviceID);
                SetDefaultProfile.generate(deviceID, 2, null, docRefUser, new SetDefaultProfile.ProfilePicCallback() {
                    @Override
                    public void onImageURLReceived(String imageURL) {
                        Log.d(TAG, "Default profile picture regenerated successfully for user " + deviceID);
                    }
                });
            }

            StorageReference imageRef = storageRef.child(imageName);
            // Delete the image from Firebase Storage
            imageRef.delete().addOnSuccessListener(aVoid -> {
                // Image deleted successfully
                Log.d(TAG, "Image deleted successfully: " + imageName);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e(TAG, "Error deleting image " + imageName + ": " + exception.getMessage());
            });
        }

        // Clear the list of selected images
        selectedImages.clear();
    }

}
