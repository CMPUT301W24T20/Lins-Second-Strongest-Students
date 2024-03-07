package com.example.qrcodereader.ui.eventPage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventDateTime = Calendar.getInstance();

        EditText eventDate = findViewById(R.id.event_date);
        eventDate.setOnClickListener(v -> showDatePickerDialog(eventDate));

        EditText eventTime = findViewById(R.id.event_time);
        eventTime.setOnClickListener(v -> showTimePickerDialog(eventTime));


        eventLocation = new GeoPoint(1.0, 1.0);
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
        getLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            // Start the activity for result
            startActivityForResult(intent, 123);
        });



        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(v -> {
            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            CollectionReference usersRef = db.collection("users");
            DocumentReference userDocRef = usersRef.document(deviceID);

            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Example of retrieving data from the document
                        userName = document.getString("name");
                        Timestamp timeOfEvent = new Timestamp(eventDateTime.getTime());
                        EditText eventName = findViewById(R.id.event_name);

                        // Create a new QR code for the event
                        QRCode qrCode = new QRCode();

                        // Create a new list of attendees for the event
                        Map<String, Integer> attendees = new HashMap<>();


                        // Add the new event to the database
                        HashMap<String, Object> event = new HashMap<>();
                        event.put("attendees", attendees);
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
    }
}