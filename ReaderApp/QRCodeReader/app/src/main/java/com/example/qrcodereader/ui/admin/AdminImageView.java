package com.example.qrcodereader.ui.admin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodereader.R;
import com.example.qrcodereader.util.SetDefaultProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminImageView extends DialogFragment implements ImageAdapter.OnImageLongClickListener{
    private RecyclerView imageRecyclerView;
    private List<String> loadedImages;
    private ImageAdapter adapter;
    private StorageReference storageRef;
    private String TypeRef;
    private String Title;

    /**
     * Constructor to initialize AdminImageView with the required parameters
     * @param title the String to be the title of the fragment
     * @param type the String that represents type of contents of fragment
     */
    public AdminImageView(String title, String type) {
        this.Title = title;
        this.TypeRef = type;
        this.storageRef = FirebaseStorage.getInstance().getReference().child(this.TypeRef);
    }

    /**
     * This method creates the Dialog fragment of a recycler view containing images from FirebaseStorage
     * @param savedInstanceState the Bundle that is previous saved state
     * @return Return a new Dialog instance to be displayed by the fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_activity_admin_image_list, null);

        imageRecyclerView = view.findViewById(R.id.imageRecyclerView);
        loadedImages  = new ArrayList<>();
        adapter = new ImageAdapter(requireContext(), loadedImages);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3)); // 3 columns grid layout
        imageRecyclerView.setAdapter(adapter);

        adapter.setOnImageLongClickListener(this);

        populateListView();

        builder.setView(view)
                .setTitle(Title)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedImages());

        return builder.create();
    }

    /**
     * This method loads images from FirebaseStorage into the recycler view
     */
    @SuppressLint("NotifyDataSetChanged")
    public void populateListView() {
        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    loadedImages.add(uri.toString());

                    // Notify adapter about the newly added item
                    adapter.notifyItemInserted(loadedImages.size() - 1);
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                });
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    /**
     * This method deletes the images that were selected by user
     */
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
                SetDefaultProfile.generateNoName(new SetDefaultProfile.ProfilePicCallback() {
                    @Override
                    public void onImageURLReceived(String imageURL) {
                        docRefUser.update("ProfilePic", imageURL);
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
    }

    /**
     * This method leads user to fragment displaying the full view of the selected image upon long click
     * @param imageUrl the String of the selected image
     */
    @Override
    public void onImageLongClick(String imageUrl) {
        ImageDetail details = new ImageDetail();
        Bundle args = new Bundle();
        args.putString("URL", imageUrl);
        args.putString("type", TypeRef);
        details.setArguments(args);
        details.show(getParentFragmentManager(), "View Images");
    }
}