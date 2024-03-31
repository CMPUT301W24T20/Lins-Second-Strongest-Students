package com.example.qrcodereader.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A singleton class to hold user info and events.
 * Ensures that user data and events are loaded once and reused across the application.
 */
public class AppDataHolder {
    private static User currentUser;
    private static AppDataHolder instance;
    private static ArrayList<Event> browseEvents;
    private static ArrayList<Event> attendeeEvents;
    private static ArrayList<Event> organizerEvents;



    // Private constructor to prevent direct instantiation
    private AppDataHolder() { }

    // Get the singleton instance
    public static synchronized AppDataHolder getInstance() {
        if (instance == null) {
            browseEvents = new ArrayList<>();
            attendeeEvents = new ArrayList<>();
            organizerEvents = new ArrayList<>();
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

    public void loadData(Context context) {
        currentUser = LocalUserStorage.loadUser(context);
        browseEvents = LocalEventsStorage.loadEvents(context, "browseEvents.json");
        attendeeEvents = LocalEventsStorage.loadEvents(context, "attendeeEvents.json");
        organizerEvents = LocalEventsStorage.loadEvents(context, "organizerEvents.json");
    }

    public ArrayList<Event> getBrowseEvents(Context context) {
        browseEvents = LocalEventsStorage.loadEvents(context, "browseEvents.json");
        return browseEvents;
    }

    public ArrayList<Event> getAttendeeEvents(Context context) {
        attendeeEvents = LocalEventsStorage.loadEvents(context, "attendeeEvents.json");
        return attendeeEvents;
    }

    public ArrayList<Event> getOrganizerEvents(Context context) {
        organizerEvents = LocalEventsStorage.loadEvents(context, "organizerEvents.json");
        return organizerEvents;
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

    public void fetchAndUpdateBrowseEvents(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp now = Timestamp.now();

        db.collection("events")
                .whereGreaterThan("time", now) // Query for documents where eventTime is in the future
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<Event> futureEvents = new ArrayList<>();

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
//
                                futureEvents.add(event);
                                Toast.makeText(context, "Event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
                            }

                            // Save the future events to local storage
                            LocalEventsStorage.saveEvents(context, futureEvents, "browseEvents.json");
                            browseEvents = futureEvents;
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void fetchAndUpdateOrganizerEvents(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("organizerID", currentUser.getUserID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ArrayList<Event> events = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
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
//
                                events.add(event);
                                Toast.makeText(context, "Event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
                            }

                            // Save the future events to local storage
                            LocalEventsStorage.saveEvents(context, events, "organizerEvents.json");
                            organizerEvents = events;
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void fetchAndUpdateAttendeeEvents(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsRef = db.collection("events");
        CollectionReference usersRef = db.collection("users");

        DocumentReference userDocRef = usersRef.document(currentUser.getUserID());


        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Long> attendeeEvents = (Map<String, Long>) snapshot.get("eventsAttended");
                    if(attendeeEvents != null && !attendeeEvents.isEmpty()) {
                        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                                @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.e("Firestore", error.toString());
                                    return;
                                }
                                if (querySnapshots != null) {
                                    ArrayList<Event> events = new ArrayList<>();

                                    for (QueryDocumentSnapshot doc : querySnapshots) {
                                        String id = doc.getId();

                                        // Check if the user has attended the event
                                        if (attendeeEvents.containsKey(id)) {
                                            String name = doc.getString("name");
                                            GeoPoint location = doc.getGeoPoint("location");
                                            String locationName = doc.getString("locationName");
                                            Timestamp time = doc.getTimestamp("time");
                                            String organizer = doc.getString("organizer");
                                            String organizerID = doc.getString("organizerID");
                                            QRCode qrCode = new QRCode(doc.getString("qrCode"));
                                            int attendeeLimit = doc.getLong("attendeeLimit").intValue();
                                            Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");
                                            String EPoster = doc.getString("EPoster");

                                            Event event = new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, attendees, EPoster);
//
                                            events.add(event);
                                            Toast.makeText(context, "Event: " + event.getEventName(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // Save the future events to local storage
                                    LocalEventsStorage.saveEvents(context, events, "attendeeEvents.json");
                                    organizerEvents = events;
                                }
                            }
                        });
                    } else{
                        Log.d("Firestore", "No events attended by the user");
                    }
                } else {
                    Log.d("Firestore", "Current data: null");
                }
            }
        });
    }
}