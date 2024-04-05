package com.example.qrcodereader;

import static android.app.Service.START_STICKY;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class MilestoneListeningService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MilestoneStart", "OnStartCommand");
        listenForMilestones();
        return START_STICKY;
    }

    private void listenForMilestones() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        Notifier notifier = Notifier.getInstance(this);
        Log.d("MilestoneListener", "Listener loading...");

        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot event : task.getResult()) {
                        String eventId = event.getId();
                        DocumentReference eventRef = eventsRef.document(eventId);

                        // Set up a listener for each event's attendees subcollection
                        eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    Map<String, Object> attendeesMap = (Map<String, Object>) documentSnapshot.get("attendees");

                                    if (attendeesMap != null) {
                                        // Get the current number of attendees
                                        int numUsers = attendeesMap.size();

                                        // Assign milestone
                                        int milestone = 10;

                                        // Check if the number of attendees has reached the milestone
                                        if (numUsers % milestone == 0 && numUsers > 9) {
                                            // Call the milestoneNotify method
                                            notifier.milestoneNotification(eventId, numUsers);
                                            Log.d("Event Notified:", "ID:" + eventId);
                                        }
                                    } else {
                                        Log.d("MilestoneFail", "No attendees map in the document");
                                    }
                                } else {
                                    Log.d("MilestoneFail", "Document does not exist");
                                }
                            }
                        });
                    }
                } else {
                    Log.d("EventMilestoneError", "Error updating/notifying on milestone");
                }
            }
        });
    }

}
