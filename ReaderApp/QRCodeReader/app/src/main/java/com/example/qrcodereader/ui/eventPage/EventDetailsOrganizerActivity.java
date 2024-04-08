package com.example.qrcodereader.ui.eventPage;

import com.example.qrcodereader.DisplayQRCode;
import com.example.qrcodereader.MapViewOrganizer;
import com.example.qrcodereader.Notifier;
import com.example.qrcodereader.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.ui.profile.ProfileEditFrag;
import com.example.qrcodereader.util.ImageUpload;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 *  Activity for users to view details of event they have created, including its QR code.
 *  @author Son and Duy
 */
public class EventDetailsOrganizerActivity extends AppCompatActivity implements ImageUpload {

    private final Notifier notifier = Notifier.getInstance(this);
    private FirebaseFirestore db;
    private DocumentReference docRefEvent;
    private CollectionReference qrRef;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private ImageView eventPoster;
    private Uri uploaded;

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
        eventPoster = findViewById(R.id.event_poster);
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
            FirestoreManager.getInstance().setEventDocRef(eventID);
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

        eventPoster.setOnClickListener(v ->{
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
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

    // Google, March 4 2024, Youtube, https://www.youtube.com/watch?v=H1ja8gvTtBE
    /**
     * This method alters the view of the profile display with the inputs from the edit fragment that the user wants to save
     * @param requestCode the integer code that represents what type of activity was resulted
     * @param resultCode the integer code that represents if activity result was error free
     * @param data the Intent upon returning from activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            uploaded = data.getData();
            if (uploaded != null) {
                eventPoster.setImageURI(uploaded);
                String deviceID =FirestoreManager.getInstance().getUserID();
                isUploaded(deviceID);
            }
        }
    }

    /**
     * This method uploads user's uploaded event poster image to FirebaseStorage
     * @param deviceID the String of the ID of the user's device
     */
    @Override
    public void isUploaded(String deviceID) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ContentResolver contentResolver = getContentResolver();
        try {
            InputStream is = contentResolver.openInputStream(uploaded);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] imageData = baos.toByteArray();
            String imageName = eventID + "_" + deviceID + ".png";
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("EventPoster/" + imageName);
            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL directly from the task snapshot
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(uri -> {
                    docRefEvent.update("poster", uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e("CreateEventActivity", "Error assigning poster to event" + eventID, e);
                });
            }).addOnFailureListener(e -> {
                Log.e("CreateEventActivity", "Error uploading poster for event" + eventID, e);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}