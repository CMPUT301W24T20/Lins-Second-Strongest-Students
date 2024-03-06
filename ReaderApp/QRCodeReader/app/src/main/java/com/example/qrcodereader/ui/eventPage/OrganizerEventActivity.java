package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;

public class OrganizerEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String userid;
    private String username;
    private ListView eventList;
    private EventArrayAdapter eventArrayAdapter;
    ArrayList<Event> eventDataList;


//    private void addNewEvent(Event event) {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("Name", event.getEventName());
//        eventsRef.document(event.getEventName()).set(data);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = findViewById(R.id.event_list_organizer);
        eventDataList = new ArrayList<>();


        userid = getIntent().getStringExtra("userid");
        username = getIntent().getStringExtra("username");

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);


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
                        String name = doc.getString("name");
                        String organizer = doc.getString("organizer");
                        GeoPoint location = doc.getGeoPoint("location");
                        Timestamp time = doc.getTimestamp("time");

                        Log.d("Firestore", "Event fetched");
                        eventArrayAdapter.addEvent(eventID, name, organizer, location, time);
                    }
                }
            }
        });

        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventActivity.this, CreateEventActivity.class);
            intent.putExtra("userid", userid);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        Button returnButton = findViewById(R.id.return_button_organizer);
        returnButton.setOnClickListener(v -> finish());


    }

    ActivityResultLauncher<Intent> createEventLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        // Extract the event details from the Intent
                        String eventName = data.getStringExtra("eventName");
                        String eventLocation = data.getStringExtra("eventLocation");
                        String attendeeLimit = data.getStringExtra("attendeeLimit");

                        // Create a new Event object
                        Log.d("OrganizerEventActivity", "Event Created: " + eventName);

                        // Add the new event to the database
                        HashMap<String, Object> event = new HashMap<>();
                        event.put("eventID", 586865);
                        event.put("name", eventName);
                        event.put("organizer", "G");
                        event.put("location", eventLocation);
                        event.put("time", Timestamp.now());

                        eventsRef.add(event);
                        Toast.makeText(this, "Event Created: " + eventName, Toast.LENGTH_SHORT).show();
                    }
                }
            });
}

