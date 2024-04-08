package com.example.qrcodereader.ui.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    String organizerID;
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the views with event data.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_event_details_admin);
        String userid = AttendeeEventActivity.userID;

        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.event_organizer);
        TextView eventLocationTextView = findViewById(R.id.event_location);
        TextView eventTimeTextView = findViewById(R.id.event_time);
        ImageView eventPoster = findViewById(R.id.event_poster);
        //ListView attendeesListView = findViewById(R.id.event_attendees);

        db = FirestoreManager.getInstance().getDb();
        eventID = FirestoreManager.getInstance().getEventID();
        docRefEvent = FirestoreManager.getInstance().getEventDocRef();
        usersRef = FirestoreManager.getInstance().getUserCollection();
        eventsRef = FirestoreManager.getInstance().getEventCollection();

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch the event details
                String eventName = documentSnapshot.getString("name");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String locationName = documentSnapshot.getString("locationName");
                Timestamp time = documentSnapshot.getTimestamp("time");
                String organizer = documentSnapshot.getString("organizer");
                organizerID = documentSnapshot.getString("organizerID");
                String poster = documentSnapshot.getString("poster");
                String qrCodeString = documentSnapshot.getString("qrCode");
                QRCode qrCode = new QRCode(qrCodeString);
                int attendeeLimit = documentSnapshot.contains("attendeeLimit") ? (int)(long)documentSnapshot.getLong("attendeeLimit") : -1;
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");

                if (poster != null && !poster.isEmpty()) {
                    Picasso.get().load(poster).resize(410, 240).centerInside().into(eventPoster);
                } else {
                    Picasso.get().load(R.drawable._49e43ff77b9c6ecc64d8a9b55622ddd7_2).centerInside().fit().into(eventPoster);
                }
                selectedEvent = new Event(eventID, eventName, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, eventsAttended, poster);

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
            removeEvent(eventID, eventsRef, usersRef);
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

    public void removeEvent(String eventID, CollectionReference eventsRef, CollectionReference usersRef) {
        selectedEvent.getOrganizerID();
        String imageName = eventID +"_" + selectedEvent.getOrganizerID() + ".png";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("EventPoster");
        StorageReference imageRef = storageRef.child(imageName);
        // Delete the image from Firebase Storage
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deleted successfully
            Log.d(TAG, "Image deleted successfully: " + imageName);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e(TAG, "Error deleting image " + imageName + "or event has no poster: " + exception.getMessage());
        });

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
            eventsRef.document(eventID)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        }

        Intent intent = new Intent();
        intent.putExtra("removedEventID", eventID);
        setResult(RESULT_OK, intent);
        finish();
    }
}
