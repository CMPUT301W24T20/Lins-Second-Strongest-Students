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

/**
 * Listener service to record attendance milestones for events.
 */
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

    /**
     * Listen for milestones per event
     */
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

                                        // Get the last notified milestone
                                        int lastNotifiedMilestone = 0;
                                        if (documentSnapshot.contains("lastNotifiedMilestone")) {
                                            lastNotifiedMilestone = documentSnapshot.getLong("lastNotifiedMilestone").intValue();
                                        } else {
                                            // Add the lastNotifiedMilestone field to the document if it doesn't exist
                                            eventRef.update("lastNotifiedMilestone", 0);
                                        }

                                        // Check if the number of attendees has reached the next milestone
                                        if ((numUsers % milestone == 0 && numUsers > 0) || (lastNotifiedMilestone == 0 && numUsers == 1)) {
                                            // Call the milestoneNotify method
                                            notifier.milestoneNotification(eventId, numUsers);
                                            Log.d("Event Notified:", "ID:" + eventId);

                                            // Update the last notified milestone
                                            if (lastNotifiedMilestone == 0) {lastNotifiedMilestone = 1;} else {lastNotifiedMilestone = numUsers/milestone;}
                                            eventRef.update("lastNotifiedMilestone", lastNotifiedMilestone);
                                        }
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Log.d("EventFindError", "Error getting documents: ", task.getException());
                }
            }
        });
    }

}