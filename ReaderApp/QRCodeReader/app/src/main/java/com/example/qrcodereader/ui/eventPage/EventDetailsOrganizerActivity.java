package com.example.qrcodereader.ui.eventPage;

import com.example.qrcodereader.DisplayQRCode;
import com.example.qrcodereader.MapViewOrganizer;
import com.example.qrcodereader.Notifier;
import com.example.qrcodereader.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 *  Activity for users to view details of event they have created, including its QR code.
 *  @author Son and Duy
 */
public class EventDetailsOrganizerActivity extends AppCompatActivity {

    private final Notifier notifier = Notifier.getInstance(this);
    private FirebaseFirestore db;
    private DocumentReference docRefEvent;
    private CollectionReference qrRef;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;

    private QRCode qrCode;
    private QRCode qrCodePromotional;
    private
    String eventID;
    String TAG = "";
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
        setContentView(R.layout.activity_event_details_organizer);
        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.event_organizer);
        TextView eventLocationTextView = findViewById(R.id.event_location);
        TextView eventTimeTextView = findViewById(R.id.event_time);
        ImageView eventPoster = findViewById(R.id.event_poster);
        db = FirebaseFirestore.getInstance();

        String TAG = "MapOrg";
        Log.d(TAG, "Event ID: " + eventID);

        db = FirebaseFirestore.getInstance();
        docRefEvent = FirestoreManager.getInstance().getEventDocRef();

        qrRef = FirestoreManager.getInstance().getQrCodeCollection();

        fetchPromotionalQRCode();

        usersRef = FirestoreManager.getInstance().getUserCollection();
        eventsRef = FirestoreManager.getInstance().getEventCollection();
        eventID = FirestoreManager.getInstance().getEventID();


        TextView seeQRCodeButton = findViewById(R.id.see_qr_button);
        seeQRCodeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DisplayQRCode.class);
            intent.putExtra("qrCode", qrCode.getString());
            intent.putExtra("promotionalQRCode", qrCodePromotional.getString());
            startActivity(intent);
        });

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventName = documentSnapshot.getString("name");
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String locationName = documentSnapshot.getString("locationName");
                String organizer = documentSnapshot.getString("organizer");
                Timestamp time = documentSnapshot.getTimestamp("time");
                String qrCodeString = documentSnapshot.getString("qrCode");
                String poster = documentSnapshot.getString("poster");
                if (poster != null && !poster.isEmpty()) {
                    Picasso.get().load(poster).resize(410, 240).centerInside().into(eventPoster);
                } else {
                    // Handle the case where the poster URL is null or empty
                    // For example, you might want to load a default image
                }
                qrCode = new QRCode(qrCodeString);
                Log.d("Firestore", "Successfully fetch document: ");

                eventNameTextView.setText(eventName);
                String organizerText = "Organizer: " + organizer;
                eventOrganizerTextView.setText("Organizer: " + organizer);
                eventLocationTextView.setText(locationName);
                String timeText = "Time: " + time.toDate().toString();
                eventTimeTextView.setText(timeText);

            }
        }).addOnFailureListener(e -> {
            Log.d("Firestore", "Failed to fetch document");
        });

        ImageView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());

        LinearLayout attendeeButton = findViewById(R.id.attendee_button);
        attendeeButton.setOnClickListener((v -> {
            Intent intent = new Intent(this, AttendanceActivity.class);
            startActivity(intent);
        }));

        LinearLayout mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> {
            goToMapActivity();
        });

        TextView removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> {
            removeEvent(eventID, eventsRef, usersRef);
        });
    }


    private void goToMapActivity() {
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        Intent intent = new Intent(this, MapViewOrganizer.class);
        intent.putExtra("eventID", eventID);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }


    private void fetchPromotionalQRCode() {
        String eventId = FirestoreManager.getInstance().getEventID();

        // Query for the promotional QR code document associated with this event
        qrRef.whereEqualTo("eventID", eventId)
                .whereEqualTo("type", "promotional")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            qrCodePromotional = new QRCode(document.getString("qrCode"));
                        }
                    } else {
                        Log.d("EventDetailsOrganizer", "Error getting promotional QR code: ", task.getException());
                    }
                });
    }

    public void removeEvent(String eventID, CollectionReference eventsRef, CollectionReference usersRef) {
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
        finish();
    }
}
