package com.example.qrcodereader;

import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * ScanHandler
 * Contains methods to update necessary firebase details upon scanning code
 */
public class ScanHandler {

    FirebaseFirestore db;
    String userID;

    public ScanHandler(FirebaseFirestore db, String userID) {
        this.db = db;
        this.userID = userID;
    }

    public void scannedCode(String code) {
        findEvent(code);
    }

    /**
     * findEvent(String code)
     * Retrieves the eventID matching the code scanned
     * Calls updateAttendance(code)
     * @param code String matching QR code scanned
     */
    private void findEvent(String code) {
        /*
        Microsoft Copilot, 07/03/24
        "I need a way to find a document in firebase based off of the of one
        of the fields"
         */

        // Get a reference to the 'events' collection
        CollectionReference eventsRef = db.collection("events");

        // Query the collection for documents where the 'qrCode' field equals 'pointCode'
        eventsRef.whereEqualTo("qrCode", code).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentName = document.getId();
                        updateAttendance(documentName);
                    }
                } else {
                    Log.d("EventFindError", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    /**
     * updateAttendance(String event)
     * In the firebase DB, update the current user's
     * EventsAttended to increment the scanned event by 1.
     *
     * @param event the Firebase eventID for the scanned code
     */
    private void updateAttendance(String event) {
        /*
        Microsoft Copilot, 07/03/24
        "I need to increment a value in a firestore db"
         */
        Log.d("UserID", userID);
        addAttendee(event, userID);

        DocumentReference userRef = db.collection("users").document(userID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> eventsAttended = (Map<String, Object>) document.get("eventsattended");
                        if (eventsAttended == null || !eventsAttended.containsKey(event)) {
                            // The event does not exist in the 'eventsattended' map, add it
                            userRef.update("eventsAttended." + event, FieldValue.increment(1))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("AddedEvent", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("CouldntAddEvent", "Error updating document", e);
                                        }
                                    });
                        }
                        else {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("eventsAttended." + event, FieldValue.increment(1));

                            userRef.update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Incremented", "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("IncrementFail", "Error updating document", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d("NoDoc", "No such document");
                    }
                } else {
                    Log.d("Update Failed", "get failed with ", task.getException());
                }
            }

        });
    }

    /**
     * addAttendee(String, String)
     * Adds the given userID to the attendees field of the given event
     * @param event eventID of event to access
     * @param userID userID to add
     */
    public void addAttendee(String event, String userID) {
        DocumentReference eventRef = db.collection("events").document(event);

        // Use the update method to add a string to the 'attendees' map
        eventRef.update("attendees." + userID, FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AttendeeAdded", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AttendeeAddFailure", "Error updating document", e);
                    }
                });
    }

}
