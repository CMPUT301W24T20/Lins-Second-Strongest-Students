package com.example.qrcodereader.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetDefaultProfile {
    /*
    OpenAI, ChatGPT, 04/01/24
    how do i return the value of imageURL
     */
    // ChatGPT code start here
    /**
     * Callback interface for receiving a profile picture URL
     */
    public interface ProfilePicCallback {
        /**
         * This method is called when a profile picture URL is received
         * @param imageURL the String URL of the profile picture
         */
        void onImageURLReceived(String imageURL);
    }
    // ChatGPT code ends here

    /**
     * This method retrieves the default profile picture for a user with no name
     * @param callback the callback to receive the generated profile picture URL
     */
    public static void generateNoName(ProfilePicCallback callback) {
        CollectionReference ColRefPic = FirebaseFirestore.getInstance().collection("DefaultProfilePic");
        ColRefPic.document("0").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Get the value of the string field
                        String imageURL = document.getString("URL");

                        callback.onImageURLReceived(imageURL);

                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.e("Firestore", "Error getting document", task.getException());
                }
            }
        });
    }

    /**
     * This method retrieves the default profile picture for a user with a name
     * @param callback the callback to receive the generated profile picture URL
     */
    public static void generateName(String letter, ProfilePicCallback callback){
        CollectionReference ColRefPic = FirebaseFirestore.getInstance().collection("DefaultProfilePic");
        ColRefPic.document(letter).get().addOnSuccessListener(document -> {
            if (document != null && document.exists()) {
                // Get the value of the string field
                String imageURL = document.getString("URL");
                // if new user being created
                callback.onImageURLReceived(imageURL);
            } else {
                Log.d("Firestore", "No such document");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error getting document", e);
        });
    }
}