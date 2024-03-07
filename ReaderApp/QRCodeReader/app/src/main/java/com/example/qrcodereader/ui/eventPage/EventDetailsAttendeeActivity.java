package com.example.qrcodereader.ui.eventPage;

import com.example.qrcodereader.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.AttendeeArrayAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class EventDetailsAttendeeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefEvent;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.event_organizer);
        TextView eventLocationTextView = findViewById(R.id.event_location);
        TextView eventTimeTextView = findViewById(R.id.event_time);
        ListView attendeesListView = findViewById(R.id.event_attendees);

        db = FirebaseFirestore.getInstance();
        String eventID = getIntent().getStringExtra("eventID");
        docRefEvent = db.collection("events").document(eventID);

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventName = documentSnapshot.getString("name");
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String organizer = documentSnapshot.getString("organizer");
                Timestamp time = documentSnapshot.getTimestamp("time");
                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");

                String nameText = "Event Name " + eventID;
                eventNameTextView.setText(nameText);
                String organizerText = "Organizer " + organizer;
                eventOrganizerTextView.setText("Organizer " + organizer);
                String locationText = "Location " + String.format(Locale.getDefault(), "%f, %f",
                        location.getLatitude(),
                        location.getLongitude());
                eventLocationTextView.setText(locationText);
                String timeText = "Time " + time.toDate().toString();
                eventTimeTextView.setText(timeText);

//                List<String> attendeesList = new ArrayList<>(eventsAttended.keySet());
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        this, android.R.layout.simple_list_item_1, attendeesList);
//                attendeesListView.setAdapter(adapter);

                ArrayList<Map.Entry<String, Long>> attendeesList = new ArrayList<>(eventsAttended.entrySet());
                // Create the custom adapter
                AttendeeArrayAdapter attendeesAdapter = new AttendeeArrayAdapter(this, attendeesList);
                // Set the custom adapter to the ListView
                attendeesListView.setAdapter(attendeesAdapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());

    }
}