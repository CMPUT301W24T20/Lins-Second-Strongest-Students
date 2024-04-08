package com.example.qrcodereader.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.QRCode;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class EventFetcher {

    private EventArrayAdapter eventArrayAdapter;
    private Context context;

    public EventFetcher(EventArrayAdapter eventArrayAdapter, Context context) {
        this.eventArrayAdapter = eventArrayAdapter;
        this.context = context;
    }

    public ArrayList<Event> fetchOrganizerEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        ArrayList<Event> events = new ArrayList<>();

        db.collection("events")
                .whereEqualTo("organizerID", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {

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

                                events.add(event);
                            }
                            LocalEventsStorage.saveEvents(context, events, "organizerEvents.json");
                            AppDataHolder.getInstance().loadOrganizerEvents(context);
                            updateAdapter(events);
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
        return events;
    }

    public void fetchAttendeeEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsRef = db.collection("events");
        CollectionReference usersRef = db.collection("users");

        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userDocRef = usersRef.document(deviceID);

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

                                            events.add(event);
                                        }
                                    }

                                    LocalEventsStorage.saveEvents(context, events, "attendeeEvents.json");
                                    AppDataHolder.getInstance().loadAttendeeEvents(context);
                                    updateAdapter(events);
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

    public void fetchBrowseEvents() {
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

                            LocalEventsStorage.saveEvents(context, events, "browseEvents.json");
                            AppDataHolder.getInstance().loadBrowseEvents(context);
                            updateAdapter(events);
                        }
                    } else {
                        Toast.makeText(context, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(ArrayList<Event> events) {
        // Run on UI thread because notifyDataSetChanged() needs to update the UI
        new Handler(Looper.getMainLooper()).post(() -> {
            if (events.size() >= 2) {
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        return e1.getTime().compareTo(e2.getTime()); // Ascending
                    }
                });
            }

            eventArrayAdapter.clear();
            for (Event event : events) {
                eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
            }
            eventArrayAdapter.notifyDataSetChanged();
        });
    }
}
