package com.example.qrcodereader.ui.eventPage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcodereader.DisplayQRCode;
import com.example.qrcodereader.entity.QRCode;

import com.example.qrcodereader.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CreateEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Calendar eventDateTime;

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

        //Button generate_button = findViewById(R.id.generate_event_qr_button);

        /*
        generate_button.setOnClickListener(v -> {
            QRCode qrCode = new QRCode();
            Intent intent = new Intent(this, DisplayQRCode.class);
            intent.putExtra("qrCode", qrCode.getBitmap());
            startActivity(intent);
        }); */

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(v -> {
            Timestamp timeOfEvent = new Timestamp(eventDateTime.getTime());
            EditText eventName = findViewById(R.id.event_name);
            EditText eventLocation = findViewById(R.id.event_location);

            // Create a new QR code for the event
            QRCode qrCode = new QRCode();

            // Create a new list of attendees for the event
            ArrayList<Map<String, Integer>> attendees = new ArrayList<Map<String, Integer>>();

            // Place holder for the location of the event
            double latitude = 53.5461;
            double longitude = 113.4938;
            GeoPoint locationGeoPoint = new GeoPoint(latitude, longitude);

            // Add the new event to the database
            HashMap<String, Object> event = new HashMap<>();
            event.put("attendees", attendees);
            event.put("location", locationGeoPoint);
            event.put("name", eventName);
            event.put("organizer", "EricTheGoat");
            event.put("qrCode", qrCode.getString());
            event.put("time", timeOfEvent);
            eventsRef.add(event);

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
}