package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BrowseEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Event selectedEvent = null;


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
                        String eventID = doc.getId();
                        String name = doc.getString("name");
                        String organizer = doc.getString("organizer");
                        GeoPoint location = doc.getGeoPoint("location");
                        Timestamp time = doc.getTimestamp("time");
//                        Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                        Log.d("Firestore", "Event fetched");
                        eventArrayAdapter.addEvent(eventID, name, organizer, location, time);
                    }

                }
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Get the item that was clicked
                selectedEvent = eventDataList.get(position);

                // Display a toast with the selected item
                Intent detailIntent = new Intent(BrowseEventActivity.this, EventDetailsAttendeeActivity.class);
                detailIntent.putExtra("eventID", selectedEvent.getEventID());
                startActivity(detailIntent);
            }
        });




        Button returnButton = findViewById(R.id.return_button_browse);
        returnButton.setOnClickListener(v -> finish());

    }
}