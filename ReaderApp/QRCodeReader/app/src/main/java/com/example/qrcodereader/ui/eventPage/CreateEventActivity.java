package com.example.qrcodereader.ui.eventPage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.util.ImageUpload;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;

import com.example.qrcodereader.R;
//import com.google.android.libraries.places.api.Places;
import com.example.qrcodereader.util.AppDataHolder;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *  Activity for users to create events by entering the details and press create.
 *  @author Duy
 */
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
public class CreateEventActivity extends AppCompatActivity implements ImageUpload {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference qrCodeRef;
    private Calendar eventDateTime;
    private GeoPoint eventLocation;
    private String eventLocationName;
    private EditText getLocation;
    private String userName;
    private String selectedQRCode;
    private TextView qrReuseText;
    private EditText eventName;
    private QRCode qrCode;
    private String selectedPastEvent;

    private CheckBox qrReuseCheckBox;
    private TextView qrReuseWarning;
    private String deviceID;
    private ImageView Poster;
    private Uri uploaded;
    private HashMap<String, Object> event;
    private StorageReference storage;
    private TextView qrReuseButton;
    private String generatedQRCode;
    private String generatedPromotionalQRCode;

    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and sets up the views for event creation.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_event);

        eventsRef = FirestoreManager.getInstance().getEventCollection();

        eventDateTime = Calendar.getInstance();

        eventName = findViewById(R.id.event_name);

        deviceID = FirestoreManager.getInstance().getUserID();

        db = FirestoreManager.getInstance().getDb();

        qrCodeRef = FirestoreManager.getInstance().getQrCodeCollection();

        selectedQRCode = "";

        generateAndCheckQRCode();

        EditText eventDate = findViewById(R.id.event_date);
        eventDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showDatePickerDialog(eventDate);
                return true; // Return true to indicate the event was handled
            }
            return false; // Return false for other actions to proceed
        });

        EditText eventTime = findViewById(R.id.event_time);
        eventTime.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showTimePickerDialog(eventTime);
                return true; // Return true to indicate the event was handled
            }
            return false; // Return false for other actions to proceed
        });

        if (!Places.isInitialized()) {
            String apiKey = getString(R.string.google_maps_api_key);
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        // OpenAI, 2024, ChatGPT, Prompt to set the edit text of the location so that when the user clicks on it, it will open the Places API
        // ChatGPT code start here
        getLocation = findViewById(R.id.event_location);
        getLocation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Perform any action that should happen with the click
                v.performClick(); // Call this to ensure clicks are handled properly

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(CreateEventActivity.this);
                startActivityForResult(intent, 123); // Start the activity for result
                return true;
            }
            return false;
        });
        // ChatGPT code end here

        EditText attendeeLimit = findViewById(R.id.attendee_limit);

        storage = FirebaseStorage.getInstance().getReference();
        Poster = findViewById(R.id.PosterUpload);
        Poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

        qrReuseButton = findViewById(R.id.reuse_QR_button);
        setReuseQRButtonVisible();

        TextView save_button = findViewById(R.id.create_button);
        save_button.setOnClickListener(v -> {
            if (validateUserInput()){
                CollectionReference usersRef = db.collection("users");
                DocumentReference userDocRef = usersRef.document(deviceID);

                userDocRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName = document.getString("name");
                            Timestamp timeOfEvent = new Timestamp(eventDateTime.getTime());

                            // Create a new list of attendees for the event
                            Map<String, Integer> attendees = new HashMap<>();

                            // Add the new event to the database
                            event = new HashMap<>();
                            event.put("attendees", attendees);
                            event.put("location", eventLocation);
                            event.put("locationName", eventLocationName);
                            event.put("name", eventName.getText().toString());
                            event.put("organizer", userName);
                            event.put("organizerID", deviceID);
                            event.put("time", timeOfEvent);
                            event.put("poster", "");

                            if (!attendeeLimit.getText().toString().isEmpty()) {
                                event.put("attendeeLimit", Integer.parseInt(attendeeLimit.getText().toString()));
                            }
                            else {
                                event.put("attendeeLimit", -1);
                            }

                            if (!selectedQRCode.isEmpty()) {
                                event.put("qrCode", selectedQRCode);
                            }
                            else {
                                event.put("qrCode", generatedQRCode);
                            }
                            AddEvent();
                            finish();
                        } else {
                            // Document does not exist
                            Log.d("CreateEventActivity", "No such document");
                            Toast.makeText(CreateEventActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Task failed with an exception
                        Log.d("CreateEventActivity", "get failed with ", task.getException());
                    }
                });

                finish();
            }
        });

        ImageView cancel_button = findViewById(R.id.return_button);
        cancel_button.setOnClickListener(v -> {finish();
        });
    }

    @Override
    public void isUploaded(String eventID) {
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
            StorageReference imageRef = storage.child("EventPoster/" + imageName);
            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL directly from the task snapshot
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();

                downloadUrlTask.addOnSuccessListener(uri -> {
                    DocumentReference newEvent = db.collection("events").document(eventID);
                    newEvent.update("poster", uri.toString());
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

    public void AddEvent(){
        eventsRef.add(event)
                .addOnSuccessListener(documentReference -> {

                    String documentId = documentReference.getId();
                    updateQRCodeReference(documentId);
                    if (uploaded !=null){
                        isUploaded(documentId);
                    }

                    Log.d("CreateEventActivity", "Event added with ID: " + documentId);

                    Toast.makeText(CreateEventActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateEventActivity", "Error adding event", e);
                    Toast.makeText(CreateEventActivity.this, "Failed to add event.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * This method shows a DatePickerDialog to get the date from the user
     * @param eventDate the EditText where the date will be displayed
     */
    private void showDatePickerDialog(EditText eventDate) {
        // OpenAI, 2024, ChatGPT, Prompt to set the edit text of the date so that when the user clicks on it, it will open the DatePickerDialog
        // Get current date
        int year = eventDateTime.get(Calendar.YEAR);
        int month = eventDateTime.get(Calendar.MONTH);
        int day = eventDateTime.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Set the selected date to the eventDateTime
                    eventDateTime.set(Calendar.YEAR, year1);
                    eventDateTime.set(Calendar.MONTH, monthOfYear);
                    eventDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Update EditText with selected date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    eventDate.setText(dateFormat.format(eventDateTime.getTime()));
                }, year, month, day);
        datePickerDialog.show();
        // ChatGPT code end here
    }

    /**
     * This method shows a TimePickerDialog to get the time from the user
     * @param eventTime the EditText where the time will be displayed
     */
    private void showTimePickerDialog(EditText eventTime) {
        // OpenAI, 2024, ChatGPT, Prompt to set the edit text of the time so that when the user clicks on it, it will open the TimePickerDialog
        // Get current time
        int hour = eventDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = eventDateTime.get(Calendar.MINUTE);

        // Show TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    eventDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    eventDateTime.set(Calendar.MINUTE, minute1);

                    // Update EditText with selected time
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    eventTime.setText(timeFormat.format(eventDateTime.getTime()));
                }, hour, minute, true);
        timePickerDialog.show();
        // ChatGPT code end here
    }

    /**
     * This method handles the result from the Places API and updates the event location
     * It also handle the get QR code from the past event
     * @param requestCode the request code (123 - Places API, 234 - get QR code from past event)
     * @param resultCode the result code (RESULT_OK if the operation is successful)
     * @param data the intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                eventLocation = new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                eventLocationName = place.getName();
                getLocation.setText(eventLocationName);
            }
        }
        else if (requestCode == 234) {
            if (!data.getStringExtra("selectedEventID").equals("none")) {
                selectedPastEvent = data.getStringExtra("selectedEventID");
                selectedQRCode = data.getStringExtra("selectedQRCode");
                updatePastEvent();
            }
            else {
                selectedQRCode = new QRCode().getString();
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            uploaded = data.getData();
            if (uploaded != null) {
                Poster.setImageURI(uploaded); // Set the image directly from URI
            }
        }
    }

    /**
     * This method validates the user input for the event
     * @return true if the user input is valid, false otherwise
     */
    public boolean validateUserInput() {
        eventName = findViewById(R.id.event_name);
        EditText getDate = findViewById(R.id.event_date);
        EditText getTime = findViewById(R.id.event_time);
        EditText attendeeLimit = findViewById(R.id.attendee_limit);

        if (eventName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (getLocation.getText().toString().isEmpty() || eventLocation == null) {
            Toast.makeText(this, "Please enter an event location", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (getDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an event date", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (getTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an event time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (attendeeLimit.getText().toString().isEmpty()) {
            return true;
        }
        try {
            BigInteger limit = new BigInteger(attendeeLimit.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "The attendee limit is not a valid number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Integer.parseInt(attendeeLimit.getText().toString()) < 0) {
            Toast.makeText(this, "The attendee limit must be a positive number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * This method generate a new QR code for the selected past event
     * to avoid 2 events have the same QR code
     */
    private void updatePastEvent() {
        // OpenAI, 2024, ChatGPT, Prompt to update the QR code of the selected past event
        // ChatGPT code start here
        DocumentReference eventDocRef = db.collection("events").document(selectedPastEvent);

        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Document with the matching event ID found
                    Log.d("Firestore", "Document with matching event ID found: " + document.getId());

                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("qrCode", "-1");

                    eventDocRef.update(updateData)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Past event successfully updated"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error updating past event", e));
                } else {
                    Log.d("CreateEventActivity", "No document found with the selected event ID.");
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
        // ChatGPT code end here
    }
    /**
     * Sets the visibility of the QR code reuse button based on the availability of past events.
     * If there are past events, the button is visible and clickable, allowing the user to browse past events.
     * If there are no past events, the button is invisible.
     */
    public void setReuseQRButtonVisible() {
        ArrayList<Event> pastEvents = new ArrayList<>();
        pastEvents = AppDataHolder.getInstance().getPastEvents(this);
        if (pastEvents.size() > 0) {
            qrReuseButton.setOnClickListener(v -> {
                Intent intent = new Intent(CreateEventActivity.this, CreateEventActivityBrowsePastEvent.class);
                startActivityForResult(intent, 234);
            });
        }
        else {
            // Disable the QR reuse button if there are no past events
            qrReuseButton.setVisibility(View.INVISIBLE);
        }
    }
    /**
     * Generates QR codes for the event and checks their uniqueness in the Firestore database.
     * If the generated QR code already exists, it recursively generates new ones until unique codes are found.
     */
    public void generateAndCheckQRCode() {
        generatedQRCode = new QRCode().getString();
        generatedPromotionalQRCode = new QRCode().getString();

        // Assuming 'qrcoderef' is your collection name
        checkQRCodeExistence(db, generatedQRCode, exists -> {
            if (exists) {
                // If QR code exists, generate a new one and check again recursively
                generateAndCheckQRCode();
            } else {
                // QR code is unique, proceed with your logic, e.g., save it to Firestore or use it
                // Similarly for the second QR code
                checkQRCodeExistence(db, generatedPromotionalQRCode, exists2 -> {
                    if (exists2) {
                        generateAndCheckQRCode();
                    }
                });
            }
        });
    }
    /**
     * Checks the existence of a QR code in the Firestore database.
     *
     * @param db      The instance of the Firestore database.
     * @param qrCode  The QR code string to check.
     * @param callback The callback interface for existence check.
     */
    private void checkQRCodeExistence(FirebaseFirestore db, String qrCode, ExistenceCallback callback) {
        db.collection("qrcoderef")
                .whereEqualTo("qrCode", qrCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            callback.onChecked(true); // QR code exists
                        } else {
                            callback.onChecked(false); // QR code does not exist
                        }
                    } else {
                        // Handle the failure of Firestore operation, e.g., log an error or retry
                    }
                });
    }
    /**
     * Updates the reference of a QR code in Firestore with the event ID.
     * If the selected QR code exists, it updates its reference with the event ID.
     * If no QR code is selected, it creates new documents for both check-in and promotional QR codes.
     *
     * @param eventId The ID of the event associated with the QR code.
     */
    private void updateQRCodeReference(String eventId) {
        if (!selectedQRCode.isEmpty()) {
            // Query the 'qrcoderef' collection for the document with the matching 'qrCode'
            qrCodeRef.whereEqualTo("qrCode", selectedQRCode)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Assuming only one document matches, get the first document
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            DocumentReference documentReference = documentSnapshot.getReference();

                            // Update the document with the new event ID
                            documentReference.update("eventID", eventId)
                                    .addOnSuccessListener(aVoid -> Log.d("CreateEventActivity", "Existing QR code reference updated with new event ID"))
                                    .addOnFailureListener(e -> Log.e("CreateEventActivity", "Error updating existing QR code reference", e));
                        } else {
                            Log.e("CreateEventActivity", "No QR code found with the selected QR code string");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CreateEventActivity", "Error querying for existing QR code document", e));
        } else {
            // If no QR code is selected, create a new document for check-in QR code (as before)
            Map<String, Object> checkInQRCode = new HashMap<>();
            checkInQRCode.put("qrCode", generatedQRCode);
            checkInQRCode.put("eventID", eventId);
            checkInQRCode.put("type", "checkin");

            qrCodeRef.add(checkInQRCode)
                    .addOnSuccessListener(documentReference -> Log.d("CreateEventActivity", "Check-in QR code reference added successfully"))
                    .addOnFailureListener(e -> Log.e("CreateEventActivity", "Error adding check-in QR code reference", e));
        }


        Map<String, Object> promotionalQRCode = new HashMap<>();
        promotionalQRCode.put("qrCode", generatedPromotionalQRCode);
        promotionalQRCode.put("eventID", eventId);
        promotionalQRCode.put("type", "promotional");

        qrCodeRef.add(promotionalQRCode)
                .addOnSuccessListener(documentReference -> Log.d("CreateEventActivity", "QR code reference updated successfully"))
                .addOnFailureListener(e -> Log.e("CreateEventActivity", "Error updating QR code reference", e));
    }

    // Callback interface for existence check
    interface ExistenceCallback {
        void onChecked(boolean exists);
    }
    /**
     * For testing purposes, injects event date, time, and location into the activity.
     * This method is used to simulate setting these parameters programmatically.
     *
     * @param year         The year of the event.
     * @param month        The month of the event.
     * @param day          The day of the event.
     * @param hour         The hour of the event.
     * @param minute       The minute of the event.
     * @param latitude     The latitude of the event location.
     * @param longitude    The longitude of the event location.
     * @param locationName The name of the event location.
     */
    public void testCreateEventDateTimeLocationInjection(int year, int month, int day, int hour, int minute, double latitude, double longitude, String locationName) {
        eventDateTime.set(Calendar.YEAR, year);
        eventDateTime.set(Calendar.MONTH, month);
        eventDateTime.set(Calendar.DAY_OF_MONTH, day);
        eventDateTime.set(Calendar.HOUR_OF_DAY, hour);
        eventDateTime.set(Calendar.MINUTE, minute);
        eventLocation = new GeoPoint(latitude, longitude);
        eventLocationName = locationName;
    }
}