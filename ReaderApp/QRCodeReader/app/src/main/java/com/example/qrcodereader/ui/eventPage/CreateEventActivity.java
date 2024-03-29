package com.example.qrcodereader.ui.eventPage;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.ImageUpload;
import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.entity.QRCode;

import com.example.qrcodereader.R;
//import com.google.android.libraries.places.api.Places;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  Activity for users to create events by entering the details and press create.
 *  @author Duy
 */
public class CreateEventActivity extends AppCompatActivity implements ImageUpload {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
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
    private ImageView Poster;
    private Uri uploaded;
    private HashMap<String, Object> event;
    private StorageReference storage;


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
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventDateTime = Calendar.getInstance();

        eventName = findViewById(R.id.event_name);

        EditText eventDate = findViewById(R.id.event_date);
        eventDate.setOnClickListener(v -> showDatePickerDialog(eventDate));

        EditText eventTime = findViewById(R.id.event_time);
        eventTime.setOnClickListener(v -> showTimePickerDialog(eventTime));

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

        CheckBox checkBox = findViewById(R.id.attendee_limit_checkbox);
        EditText attendeeLimit = findViewById(R.id.attendee_limit);

        // Set checkbox change listener
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            attendeeLimit.setEnabled(isChecked);
            if (isChecked) {
                // If checkbox is checked, make attendeeLimit fully opaque
                attendeeLimit.setAlpha(1.0f);
            } else {
                // If checkbox is unchecked, make attendeeLimit faded
                attendeeLimit.setAlpha(0.5f);
                attendeeLimit.setText(""); // Clear the text
            }
        });

        CheckBox qrReuseCheckBox = findViewById(R.id.QR_reuse_checkbox);
        qrReuseText = findViewById(R.id.QR_reuse);

        TextView qrReuseWarning = findViewById(R.id.QR_reuse_warning);

        // Set checkbox change listener
        qrReuseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If checkbox is checked, make qrReuseText fully opaque
                qrReuseText.setAlpha(1.0f);
                qrReuseWarning.setAlpha(1.0f);
                Intent intent = new Intent(CreateEventActivity.this, CreateEventActivityBrowsePastEvent.class);
                startActivityForResult(intent, 234);
            } else {
                // If checkbox is unchecked, make qrReuseText faded
                qrReuseText.setAlpha(0.5f);
                qrReuseWarning.setAlpha(0.0f);
            }
        });
        storage = FirebaseStorage.getInstance().getReference();
        StorageReference storageRef = storage.child("EventPoster/noEventPoster.png");
        Poster = findViewById(R.id.PosterUpload);
        // Download the image into a local file
        try {
            File localFile = File.createTempFile("default", ".png"); // Create a temporary file to store the downloaded image
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    Poster.setImageBitmap(bitmap);
                    Poster.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                    Log.e(TAG, "Failed to download image", e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(v -> {
            if (validateUserInput()){
                String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                CollectionReference usersRef = db.collection("users");
                DocumentReference userDocRef = usersRef.document(deviceID);

                userDocRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userName = document.getString("name");
                            Timestamp timeOfEvent = new Timestamp(eventDateTime.getTime());

                            if (!qrReuseCheckBox.isChecked()) {
                                qrCode = new QRCode();
                                selectedQRCode = qrCode.getString();
//                                Toast.makeText(CreateEventActivity.this, "New QR code generated", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                updatePastEvent();
                            }

                            // Create a new list of attendees for the event
                           Map<String, Integer> attendees = new HashMap<>();

                            // Add the new event to the database
                            event = new HashMap<>();
                            event.put("attendees", attendees);

                            // If the checkbox is checked, add the attendee limit to the event
                            if (checkBox.isChecked()) {
                                // If the organizer didn't specify an attendee limit, set it to -1
                                if (attendeeLimit.getText().toString().isEmpty()) {
                                    event.put("attendeeLimit", -1);
                                }
                                else {
                                    event.put("attendeeLimit", Integer.parseInt(attendeeLimit.getText().toString()));
                                }
                            } else {
                                event.put("attendeeLimit", -1);
                            }

                            event.put("location", eventLocation);
                            event.put("locationName", eventLocationName);
                            event.put("name", eventName.getText().toString());
                            event.put("organizer", userName);
                            event.put("organizerID", deviceID);
                            event.put("qrCode", selectedQRCode);
                            event.put("time", timeOfEvent);
                            if (uploaded != null) {
                                isUploaded();
                            } else{
                                // event.put ( the default poster)
                                AddEvent();
                            }
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

        Button cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(v -> finish());
    }
    @Override
    public void isUploaded() {
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
            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String imageName = eventName.getText().toString() + "_"+ deviceID + "POSTER" + ".png";
            StorageReference imageRef = storage.child("EventPoster/" + imageName);
            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL directly from the task snapshot
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(uri -> {
                    event.put("poster", uri.toString());
                    AddEvent();

                }).addOnFailureListener(e -> {
                });
            }).addOnFailureListener(e -> {
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
                    Log.d("CreateEventActivity", "Event added with ID: " + documentReference.getId());
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
            selectedQRCode = data.getStringExtra("selectedQRCode");
            selectedPastEvent = data.getStringExtra("selectedEventID");
            qrReuseText.setText(selectedQRCode);
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
        if (eventName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (eventLocation == null) {
            Toast.makeText(this, "Please enter an event location", Toast.LENGTH_SHORT).show();
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
                    QRCode newqrCode = new QRCode();
                    updateData.put("qrCode", newqrCode.getString());

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
}