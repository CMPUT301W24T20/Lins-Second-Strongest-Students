package com.example.qrcodereader.ui.eventPage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.Notifier;
import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.AttendeeArrayAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference docRefEvent;
    private final Notifier notifier = Notifier.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_attendance);
        ListView attendeesListView = findViewById(R.id.event_attendees);
        String eventID = getIntent().getStringExtra("eventID");
        db = FirebaseFirestore.getInstance();
        docRefEvent = db.collection("events").document(eventID);

        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                ArrayList<Map.Entry<String, Long>> attendeesList = new ArrayList<>(eventsAttended.entrySet());

                TextView notifyButton = findViewById(R.id.notify_button);
                notifyButton.setOnClickListener(v -> {
                    if (!attendeesList.isEmpty()) {
                        notifier.prompt(AttendanceActivity.this, new Notifier.OnInputListener() {
                            @Override
                            public void onInput(String[] details) {
                                notifier.notifyUsers(attendeesList, details, eventID);
                            }
                        });
                    } else {
                        //No attendees found
                        Toast.makeText(AttendanceActivity.this, "No attendees found", Toast.LENGTH_SHORT).show();
                    }
                });

                // Create the custom adapter
                AttendeeArrayAdapter attendeesAdapter = new AttendeeArrayAdapter(this, attendeesList);
                // Set the custom adapter to the ListView
                attendeesListView.setAdapter(attendeesAdapter);
            }
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }
}
