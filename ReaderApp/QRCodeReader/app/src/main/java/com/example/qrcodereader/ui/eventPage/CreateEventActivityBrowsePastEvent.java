package com.example.qrcodereader.ui.eventPage;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.QRCode;
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

public class CreateEventActivityBrowsePastEvent extends AppCompatActivity {


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
        setContentView(R.layout.organizer_browse_past_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = findViewById(R.id.event_list_organizer);
        eventDataList = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        // Get the current time as a Timestamp
        Timestamp currentTime = Timestamp.now();


        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsRef.whereLessThan("time", currentTime)
                .whereEqualTo("organizerID", deviceID)
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
                                GeoPoint location = doc.getGeoPoint("location");

                                String locationName;
                                if (doc.getString("locationName") != null) {
                                    locationName = doc.getString("locationName");
                                } else {
                                    locationName  = "No location";
                                }

                                Timestamp time = doc.getTimestamp("time");
                                String organizer = doc.getString("organizer");
                                String organizerID = doc.getString("organizerID");

                                String qrCodeString = doc.getString("qrCode");
                                QRCode qrCode = new QRCode(qrCodeString);

                                Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                                Log.d("Firestore", "Event fetched");
                                Toast.makeText(CreateEventActivityBrowsePastEvent.this, "Event fetched", Toast.LENGTH_SHORT).show();
                                eventArrayAdapter.addEvent(eventID, name, location, locationName, time, organizer, organizerID, qrCode, attendees);
                            }
                        }
                    }
                });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);
                QRCode qrCode = selectedEvent.getQrCode();
                String qrCodeString = qrCode.getString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedQRCode", qrCodeString);

                // Update the selected event document in Firestore with the new QR code
                QRCode newQRCode = new QRCode();
                String newQRCodeString = newQRCode.getString();
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("qrCode", newQRCodeString); // Update qrCode field with new QR code string

                eventsRef.document(selectedEvent.getEventID()).update(updateData)
                        .addOnSuccessListener(aVoid -> {
                            // QR code updated successfully in Firestore
                            Log.d("Firestore", "DocumentSnapshot successfully updated with new QR code");
                            Toast.makeText(CreateEventActivityBrowsePastEvent.this, "Event updated with new QR code", Toast.LENGTH_SHORT).show();

                            // Return the new QR code string to the previous activity
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Firestore", "Error updating document", e);
                            Toast.makeText(CreateEventActivityBrowsePastEvent.this, "Failed to update event with new QR code.", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        });
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });
    }
}
