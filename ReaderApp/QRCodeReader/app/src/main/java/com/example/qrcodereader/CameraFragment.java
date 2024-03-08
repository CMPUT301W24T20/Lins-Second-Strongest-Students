package com.example.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class CameraFragment extends Fragment implements View.OnClickListener {
    /*
    CameraFragment
    Contains code for a fragment implementing a QR code scanner
     */
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CameraFragment() {
        super(R.layout.fragment_camera);
    }

    Button scanButton;
    private View thisView;

    /*
     * startScan
     * Opens QR code reader
     */
    private void startScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(CameraFragment.this);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Code");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned Code: " + result.getContents(), Toast.LENGTH_LONG).show();
                findEvent(result.getContents());
            }
        }
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        String userID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
                            userRef.update("eventsAttended." + event, 1)
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
        eventRef.update("attendees." + userID, userID)
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set up fragment
        thisView = inflater.inflate(R.layout.fragment_camera, container, false);
        scanButton = (Button) thisView.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);
        return thisView;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Scanning...", Toast.LENGTH_LONG).show();
        startScan();
    }

}
