package com.example.qrcodereader.ui.admin;

import com.example.qrcodereader.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.AttendeeArrayAdapter;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 *  Activity for admin to view details of event and possibly remove it.
 *  <p>
 *      This is where the remove operation happen
 *  </p>
 *  @author Son
 */
public class EventDetailsAdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private DocumentReference docRefEvent;
    private Event selectedEvent;
    private final String TAG = "EventDetailsAdminActivity";
    String eventID;
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the views with event data.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_admin);
        String userid = AttendeeEventActivity.userID;

        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.event_organizer);
        TextView eventLocationTextView = findViewById(R.id.event_location);
        TextView eventTimeTextView = findViewById(R.id.event_time);
        //ListView attendeesListView = findViewById(R.id.event_attendees);

        initializeFirestore();

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch the event details
                String eventName = documentSnapshot.getString("name");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String locationName = documentSnapshot.getString("locationName");
                Timestamp time = documentSnapshot.getTimestamp("time");
                String organizer = documentSnapshot.getString("organizer");
                String organizerID = documentSnapshot.getString("organizerID");
                String EventPoster = documentSnapshot.getString("poster");
                String qrCodeString = documentSnapshot.getString("qrCode");
                QRCode qrCode = new QRCode(qrCodeString);
                int attendeeLimit = documentSnapshot.contains("attendeeLimit") ? (int)(long)documentSnapshot.getLong("attendeeLimit") : -1;
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                selectedEvent = new Event(eventID, eventName, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, eventsAttended, EventPoster);

                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");

                eventNameTextView.setText(eventName);
                String organizerText = "Organizer: " + organizer;
                eventOrganizerTextView.setText(organizerText);
                eventLocationTextView.setText(locationName);
                String timeText = "Time: " + time.toDate().toString();
                eventTimeTextView.setText(timeText);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        TextView removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> {
            if (eventID != null) {
                usersRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Check if the user has the event in eventsAttended
                            Map<String, Object> eventsAttended = (Map<String, Object>) document.get("eventsAttended");
                            if (eventsAttended != null && eventsAttended.containsKey(eventID)) {
                                // Prepare the delete operation for the specific eventID
                                DocumentReference userDocRef = usersRef.document(document.getId());
                                userDocRef.update("eventsAttended." + eventID, FieldValue.delete())
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event ID deleted from user's eventsAttended."))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting event ID from user's eventsAttended", e));
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
                db.collection("events").document(eventID)
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
            }
            finish();
        });

        ImageView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    protected void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
        eventID = getIntent().getStringExtra("eventID");
        docRefEvent = db.collection("events").document(eventID);
        usersRef = db.collection("users");
    }
}
