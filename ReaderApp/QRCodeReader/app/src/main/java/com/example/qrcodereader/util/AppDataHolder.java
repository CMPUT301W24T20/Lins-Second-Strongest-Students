package com.example.qrcodereader.util;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.provider.Settings;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.util.LocalUserStorage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A singleton class to hold user info and events.
 * Ensures that user data and events are loaded once and reused across the application.
 */
public class AppDataHolder {
    private static User currentUser;
    private static AppDataHolder instance;


    // Private constructor to prevent direct instantiation
    private AppDataHolder() { }

    // Get the singleton instance
    public static synchronized AppDataHolder getInstance() {
        if (instance == null) {
            instance = new AppDataHolder();
        }
        return instance;
    }


    /**
     * Get the current user - the one who is using the app
     * @param context The context
     * @return the user object
     */
    public User getCurrentUser(Context context) {
        currentUser = LocalUserStorage.loadUser(context);
        return currentUser;
    }

    /**
     * Fetches the user info from the firebase and updates the user information in the local file
     * call this after you update anything related to the user to the firebase
     * @param userId The user ID (device ID)
     * @param context The context
     */
    public void fetchAndUpdateUserInfo(String userId, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                Map<String, Long> attendeeEvents = (Map<String, Long>) documentSnapshot.get("eventsAttended");
                String image = documentSnapshot.getString("ProfilePic");

                User user = new User(userId, name, location, attendeeEvents, image);

                if (user.getUserID() != null) {
                    currentUser = user;
                    LocalUserStorage.saveUser(context, currentUser);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }


}