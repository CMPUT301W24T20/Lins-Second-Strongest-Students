package com.example.qrcodereader.ui.eventPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.Notifier;
import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.AttendeeArrayAdapter;
import com.example.qrcodereader.entity.FirestoreManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference docRefEvent;
    private final Notifier notifier = Notifier.getInstance(this);
    private Map.Entry<String, Long> selectedUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_attendance);
        ListView attendeesList = findViewById(R.id.event_attendees);
        String eventID = FirestoreManager.getInstance().getEventID();
        docRefEvent = FirestoreManager.getInstance().getEventDocRef();

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                ArrayList<Map.Entry<String, Long>> attendeesDataList = new ArrayList<>(eventsAttended.entrySet());
                String eventName = (String) documentSnapshot.get("name");

                TextView notifyButton = findViewById(R.id.notify_button);
                notifyButton.setOnClickListener(v -> {
                    if (!attendeesDataList.isEmpty()) {
                        notifier.prompt(AttendanceActivity.this, new Notifier.OnInputListener() {
                            @Override
                            public void onInput(String[] details) {
                                details[0] = eventName + ": " + details[0];
                                notifier.notifyUsers(attendeesDataList, details, eventID);
                            }
                        });
                    } else {
                        //No attendees found
                    }
                });

                // Create the custom adapter
                AttendeeArrayAdapter attendeesAdapter = new AttendeeArrayAdapter(this, attendeesDataList);
                // Set the custom adapter to the ListView
                attendeesList.setAdapter(attendeesAdapter);

                attendeesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                        // Get the item that was clicked
                        selectedUser = attendeesDataList.get(position);

                        // Display a toast with the selected item
                        Intent detailIntent = new Intent(AttendanceActivity.this, UserDetailsOrganizerActivity.class);
                        detailIntent.putExtra("attendeeID", selectedUser.getKey());
                        Log.d("UserDetailsOrganizer", "Received attendee ID: " + selectedUser.getKey());
                        startActivity(detailIntent);
                        selectedUser = null;
                    }
                });
            }
        });


        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }
}
