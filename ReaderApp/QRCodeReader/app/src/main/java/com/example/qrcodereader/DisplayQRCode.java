package com.example.qrcodereader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;

public class DisplayQRCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_qrcode);

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Button backButton = findViewById(R.id.backButton);

        // Get the QR code bitmap from the intent
        Bitmap qrCodeBitmap = getIntent().getParcelableExtra("qrCodeBitmap");

        // Set the QR code bitmap to the ImageView
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        backButton.setOnClickListener(v -> {
            // Finish this activity and return to the previous one
            finish();
        });
    }
}