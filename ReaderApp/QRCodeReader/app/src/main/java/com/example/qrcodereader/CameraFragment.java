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
    void startScan() {
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
        of the fields
         */

        CollectionReference eventsRef = db.collection("events");

        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Assuming 'qrCodes' is your subcollection
                        CollectionReference qrCodesRef = eventsRef.document(document.getId()).collection("qrCodes");

                        // Query the subcollection based on the 'qrCode' field
                        qrCodesRef.whereEqualTo("qrCode", "yourQrCodeValue").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot qrCodeDocument : task.getResult()) {
                                         String eventID = qrCodeDocument.getId();
                                         updateAttendance(eventID);
                                    }
                                } else {
                                    Log.d("Query Error", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("Query Error", "Error getting documents: ", task.getException());
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

        DocumentReference userRef = db.collection("users").document(userID);

        Map<String, Object> updates = new HashMap<>();
        updates.put("eventsAttended." + event, FieldValue.increment(1));

        userRef.set(updates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UpdatedAttendance", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update Failed", "Error updating document", e);
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
