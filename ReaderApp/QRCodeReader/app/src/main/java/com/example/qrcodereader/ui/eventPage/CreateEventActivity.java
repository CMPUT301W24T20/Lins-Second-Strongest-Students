package com.example.qrcodereader.ui.eventPage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcodereader.DisplayQRCode;
import com.example.qrcodereader.entity.QRCode;

import com.example.qrcodereader.R;
//import com.google.android.libraries.places.api.Places;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CreateEventActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //Button generate_button = findViewById(R.id.generate_event_qr_button);

        /*
        generate_button.setOnClickListener(v -> {
            QRCode qrCode = new QRCode();
            Intent intent = new Intent(this, DisplayQRCode.class);
            intent.putExtra("qrCode", qrCode.getBitmap());
            startActivity(intent);
        }); */

        if (!Places.isInitialized()) {
            String apiKey = getString(R.string.google_maps_api_key);
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

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

        // Set checkbox change listener
        qrReuseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If checkbox is checked, make qrReuseText fully opaque
                qrReuseText.setAlpha(1.0f);
                Intent intent = new Intent(CreateEventActivity.this, CreateEventActivityBrowsePastEvent.class);
                startActivityForResult(intent, 234); // Use a unique request code for this activity
            } else {
                // If checkbox is unchecked, make qrReuseText faded
                qrReuseText.setAlpha(0.5f);
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

                            // Create a new QR code for the event
                            QRCode qrCode = new QRCode();

                            // Create a new list of attendees for the event
                            Map<String, Integer> attendees = new HashMap<>();

                            // Add the new event to the database
                            HashMap<String, Object> event = new HashMap<>();
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
                            event.put("qrCode", qrCode.getString());
                            event.put("time", timeOfEvent);

                            eventsRef.add(event)
                                    .addOnSuccessListener(documentReference -> {
                                        // This block will be executed if the document is successfully written to Firestore
                                        Log.d("CreateEventActivity", "Event added with ID: " + documentReference.getId());
                                        // Optionally, inform the user of success via UI, such as a Toast
                                        Toast.makeText(CreateEventActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                                        // You can finish the activity or clear the form here if desired
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // This block will be executed if there's an error during the write operation
                                        Log.e("CreateEventActivity", "Error adding event", e);
                                        // Optionally, inform the user of the failure via UI, such as a Toast
                                        Toast.makeText(CreateEventActivity.this, "Failed to add event.", Toast.LENGTH_SHORT).show();
                                    });
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

    private void showDatePickerDialog(EditText eventDate) {
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
    }

    /**
     * This method shows a TimePickerDialog to get the time from the user
     * @param eventTime the EditText where the time will be displayed
     */
    private void showTimePickerDialog(EditText eventTime) {
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
    }

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
            qrReuseText.setText(selectedQRCode);
        }
    }

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
        else if (eventDateTime.before(Calendar.getInstance())) {
            Toast.makeText(this, "Please enter an event date and time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}