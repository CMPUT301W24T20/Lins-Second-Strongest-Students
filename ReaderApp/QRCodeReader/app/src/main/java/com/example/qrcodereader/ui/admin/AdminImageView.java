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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImageView extends DialogFragment {
    private GridView imageGridView;
    private List<String> selectedImages;
    private ImageAdapter adapter;
    private StorageReference storageRef;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_activity_admin_image_list, null);

//        assert getArguments() != null;
//        String Title = getArguments().getString("Title");

        storageRef = FirebaseStorage.getInstance().getReference().child("EventPoster");


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
                .setTitle("meow")
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    // able to press Save Button, it is not greyed out == input in edit texts are valid
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedImages();
//                        if (uploaded != null) {
//                            isUploaded();
//                        }
//                        docRefUser.update("name",  ETname.getText().toString());
//                        docRefUser.update("email",  ETemail.getText().toString());
//                        docRefUser.update("phone",  ETphone.getText().toString());
//                        docRefUser.update("phoneRegion",Sregion.getSelectedItem().toString());
//
//                        if (onSaveClickListener != null) {
//                            onSaveClickListener.onSaveClicked(ETname.getText().toString(),
//                                    ETemail.getText().toString(),
//                                    ETphone.getText().toString(),
//                                    Sregion.getSelectedItem().toString(),
//                                    uploaded);
//                        }
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
//            yea = imageUrl;
//
            String imageName = imageToken.substring(0, indexOfQuestionMark);

            // Construct the StorageReference for the image to be deleted
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
