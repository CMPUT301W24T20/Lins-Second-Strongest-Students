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
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;

public class BrowseEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private void addNewEvent(Event event) {
        HashMap<String, String> data = new HashMap<>(); // Add the other attributes in the right order to the HashMap
        data.put("Organizer", event.getOrganizer()); // Add the other attributes in the right order to the HashMap
        eventsRef.add(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        ListView eventList = findViewById(R.id.event_list_browse);
        ArrayList<Event> eventDataList = new ArrayList<>();


        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
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
                        int eventID = doc.getLong("eventID").intValue();
                        String name = doc.getString("name");
                        String organizer = doc.getString("organizer");
                        String location = doc.getString("location");
                        Timestamp time = doc.getTimestamp("time");

                        Log.d("Firestore", "Event fetched");
                        eventArrayAdapter.addEvent(eventID, name, organizer, location, time);
                    }

                }
            }
        });

        Button createEventButton = findViewById(R.id.sign_up_button);
        createEventButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // There also need to be onItemClickListener for the ListView
            }
        });



        Button returnButton = findViewById(R.id.return_button_browse);
        returnButton.setOnClickListener(v -> finish());

    }
}