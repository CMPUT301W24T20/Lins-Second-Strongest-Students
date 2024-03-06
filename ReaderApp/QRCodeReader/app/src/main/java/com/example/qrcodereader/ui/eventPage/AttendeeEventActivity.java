package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;

import com.example.qrcodereader.entity.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendeeEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private DocumentReference userDocRef;
    private List<String> attendeeEvents;
//    private void addNewEvent(Event event) {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("Name", event.getEventName());
//        eventsRef.document(event.getEventName()).set(data);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events1");
        usersRef = db.collection("users1");


        ListView eventList = findViewById(R.id.event_list_attendee);
        ArrayList<Event> eventDataList = new ArrayList<>();
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        // Getting user through MainActivity. This is the user who is using the app
        User user = (User) getIntent().getSerializableExtra("user");
        userDocRef = db.collection("users").document(user.getUserID());

        // I was attempting to get the map of eventsAttended in the user document and turn it into a list in the comment below
        // Then compare it with each event in events1 down in the SnapshotListener below
        // But it couldn't work not sure why
        // You can delete this code if you don't need it

//        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                Log.d("Firestore", "Successfully fetch user document");
//                Map<String, Object> eventsAttended = (Map<String, Object>) documentSnapshot.get("eventsAttended");
//                if (eventsAttended != null) {
//                    attendeeEvents = new ArrayList<String>(eventsAttended.keySet());
//                    Log.d("AttendeeEvents", "Attendee Events List: " + attendeeEvents);
//
//                    eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot querySnapshots,
//                                            @Nullable FirebaseFirestoreException error) {
//                            if (error != null) {
//                                Log.e("Firestore", error.toString());
//                                return;
//                            }
//                            if (querySnapshots != null) {
//                                eventDataList.clear();
//                                for (QueryDocumentSnapshot doc: querySnapshots) {
////                        Event event = doc.toObject(Event.class);
//                                    String eventID = doc.getId();
//                                    if (attendeeEvents.contains(eventID)) {
//                                        String name = doc.getString("name");
//                                        String organizer = doc.getString("organizer");
//                                        String location = doc.getString("location");
//                                        Timestamp time = doc.getTimestamp("time");
//                                        Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");
//
//                                        Log.d("Firestore", "Event fetched");
//                                        eventArrayAdapter.addEvent(eventID, name, organizer, location, time);
//                                    }
//                                }
//                            }
//                        }
//                    });
//                }
//            } else {
//                Log.d("Firestore", "No such user document");
//            }
//        }).addOnFailureListener(e -> Log.e("Firestore", "Error getting user document", e));

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
//                        Event event = doc.toObject(Event.class);
                        String eventID = doc.getId();

                        /*if (attendeeEvents.contains(eventID))*/ // Heres the trying to compare
                            String name = doc.getString("name");
                            String organizer = doc.getString("organizer");
                            GeoPoint location = doc.getGeoPoint("location");
                            Timestamp time = doc.getTimestamp("time");
                            Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                            Log.d("Firestore", "Event fetched");
                            eventArrayAdapter.addEvent(eventID, name, organizer, location, time);

                    }
                }
            }
        });

        Button returnButton = findViewById(R.id.return_button_attendee);
        returnButton.setOnClickListener(v -> finish());

        // Go to BrowseEventActivity
        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventActivity.this, BrowseEventActivity.class);
                // Sending the user object to BrowseEventActivity
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
}



