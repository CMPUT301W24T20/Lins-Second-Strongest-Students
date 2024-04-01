package com.example.qrcodereader.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;
import android.provider.Settings;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.util.LocalUserStorage;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.EventManager;

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

    public static void loadData(Context context) {
        currentUser = LocalUserStorage.loadUser(context);
        browseEvents = LocalEventsStorage.loadEvents(context, "browseEvents.json");
        attendeeEvents = LocalEventsStorage.loadEvents(context, "attendeeEvents.json");
        organizerEvents = LocalEventsStorage.loadEvents(context, "organizerEvents.json");
    }

    public static void loadBrowseEvents(Context context) {
        browseEvents = LocalEventsStorage.loadEvents(context, "browseEvents.json");
    }
    public static void loadAttendeeEvents(Context context) {
        attendeeEvents = LocalEventsStorage.loadEvents(context, "attendeeEvents.json");
    }
    public static void loadOrganizerEvents(Context context) {
        organizerEvents = LocalEventsStorage.loadEvents(context, "organizerEvents.json");
    }

    public static ArrayList<Event> getBrowseEvents(Context context) {
        return browseEvents;
    }

    public static ArrayList<Event> getAttendeeEvents(Context context) {
        return attendeeEvents;
    }
    public static ArrayList<Event> getOrganizerEvents(Context context) {
        return organizerEvents;
    }

    /**
     * Fetches the user info from the firebase and updates the user information in the local file
     * call this after you update anything related to the user to the firebase
     * @param userId The user ID (device ID)
     * @param context The context
     */
    public static void fetchAndUpdateUserInfo(String userId, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                Map<String, Long> attendeeEvents = (Map<String, Long>) documentSnapshot.get("eventsAttended");
                String image = documentSnapshot.getString("ProfilePic");

                currentUser = new User(userId, name, location, attendeeEvents, image);
                LocalUserStorage.saveUser(context, currentUser);

            }
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }

    public static void fetchAndLoadBrowseEvents(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp now = Timestamp.now();

        db.collection("events")
                .whereGreaterThan("time", now) // Query for documents where eventTime is in the future
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<Event> events = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                //Event event = documentSnapshot.toObject(Event.class);
                                String id = documentSnapshot.getId();
                                String name = documentSnapshot.getString("name");
                                GeoPoint location = documentSnapshot.getGeoPoint("location");
                                String locationName = documentSnapshot.getString("locationName");
                                Timestamp time = documentSnapshot.getTimestamp("time");
                                String organizer = documentSnapshot.getString("organizer");
                                String organizerID = documentSnapshot.getString("organizerID");
                                QRCode qrCode = new QRCode(documentSnapshot.getString("qrCode"));
                                int attendeeLimit = documentSnapshot.getLong("attendeeLimit").intValue();
                                Map<String, Long> attendees = (Map<String, Long>) documentSnapshot.get("attendees");
                                String EPoster = documentSnapshot.getString("EPoster");

                                Event event = new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, attendees, EPoster);

                                events.add(event);
                            }

                            // Save the future events to local storage
                            LocalEventsStorage.saveEvents(context, events, "browseEvents.json");
                            browseEvents = events;
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}