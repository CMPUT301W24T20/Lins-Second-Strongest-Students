package com.example.qrcodereader.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class SetDefaultProfile {

    // how do i return the value of imageURL, april 1
    public interface ProfilePicCallback {
        void onImageURLReceived(String imageURL);
    }

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