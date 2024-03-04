package com.example.qrcodereader.ui.eventPage;

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


public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

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
            EditText eventName = findViewById(R.id.event_name);
            EditText eventLocation = findViewById(R.id.event_location);
            EditText attendeeLimit = findViewById(R.id.attendee_limit);

            Intent returnIntent = new Intent();
            returnIntent.putExtra("eventName", eventName.getText().toString());
            returnIntent.putExtra("eventLocation", eventLocation.getText().toString());
            //returnIntent.putExtra("attendeeLimit", attendeeLimit.getText());
            setResult(RESULT_OK, returnIntent);
            finish();

        });

        Button cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(v -> finish());
    }
}