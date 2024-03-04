package com.example.qrcodereader.ui.eventPage;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrcodereader.R;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);

        Button generate_button = findViewById(R.id.generate_event_qr_button);

        generate_button.setOnClickListener(v -> {

        });


        Button cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(v -> finish());
    }
}