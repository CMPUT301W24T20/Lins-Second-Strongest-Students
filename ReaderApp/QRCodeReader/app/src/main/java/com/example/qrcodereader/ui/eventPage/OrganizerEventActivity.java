package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
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
import java.util.Map;

public class OrganizerEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String userid;
    private String username;
    private ListView eventList;
    private EventArrayAdapter eventArrayAdapter;
    ArrayList<Event> eventDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = findViewById(R.id.event_list_organizer);
        eventDataList = new ArrayList<>();

        userid = getIntent().getStringExtra("userID");
        username = getIntent().getStringExtra("userName");

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);


        //String userid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        eventsRef.whereEqualTo("organizer", username)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                String eventID = doc.getId();
                                String name = doc.getString("name");
                                String organizer = doc.getString("organizer");
                                GeoPoint location = doc.getGeoPoint("location");

                                Timestamp time = doc.getTimestamp("time");
                                Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                                String locationName;
                                if (doc.exists() && doc.contains("locationName") && doc.getString("locationName") != null) {
                                    locationName = doc.getString("locationName");
                                } else {
                                    locationName  = "No location";
                                }

                                Log.d("Firestore", "Event fetched");
                                eventArrayAdapter.addEvent(eventID, name, organizer, location, time, locationName);
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

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);

                Intent detailIntent = new Intent(OrganizerEventActivity.this, EventDetailsOrganizerActivity.class);
                detailIntent.putExtra("eventID", selectedEvent.getEventID());
                startActivity(detailIntent);
            }
        });
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

//    private void showEventDetailsDialog(Event event) {
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.event_detail_dialog_organizer, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(view);
//
//        // Set the event details to the TextViews
//        TextView eventNameTextView = view.findViewById(R.id.event_name);
//        String nameText = "Event Name: " + event.getEventName();
//        eventNameTextView.setText(nameText);
//
//        TextView eventOrganizerTextView = view.findViewById(R.id.event_organizer);
//        String organizerText = "Organizer: " + event.getOrganizer();
//        eventOrganizerTextView.setText(organizerText);
//
//        TextView eventLocationTextView = view.findViewById(R.id.event_location);
//        String locationText = "Location: " + event.getLocation().getLatitude() + ", " + event.getLocation().getLongitude();
//        eventLocationTextView.setText(locationText);
//
//        TextView eventTimeTextView = view.findViewById(R.id.event_time);
//        eventTimeTextView.setText(event.getTime().toDate().toString());
//
//        ListView attendeesListView = view.findViewById(R.id.event_attendees);
//        Map<String, Long> attendees = event.getAttendees();
//        if (attendees != null && !attendees.isEmpty()) {
//            // Convert the map entries to a list
//            ArrayList<Map.Entry<String, Long>> attendeesList = new ArrayList<>(attendees.entrySet());
//            // Create the custom adapter
//            AttendeeArrayAdapter attendeesAdapter = new AttendeeArrayAdapter(this, attendeesList);
//            // Set the custom adapter to the ListView
//            attendeesListView.setAdapter(attendeesAdapter);
//        }
//
//
//        // Create and show the dialog
//
//        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
}

