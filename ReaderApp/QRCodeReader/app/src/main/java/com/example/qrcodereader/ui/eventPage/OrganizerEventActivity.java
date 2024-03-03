package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class OrganizerEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;

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

        ListView eventList = findViewById(R.id.event_list_organizer);
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
                        eventDataList.add(new Event(eventID, name, organizer, location, time));
                        eventArrayAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        Button returnButton = findViewById(R.id.return_button_organizer);
        returnButton.setOnClickListener(v -> finish());

    }
}

