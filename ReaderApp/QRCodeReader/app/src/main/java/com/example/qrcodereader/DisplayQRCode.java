package com.example.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity for displaying QR code
 * @author Duy
 */
public class DisplayQRCode extends AppCompatActivity {
    // OpenAI, 2024, ChatGPT, given the code snippet, prompt to display the generated QR code from the bitmap
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_qrcode);

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Button backButton = findViewById(R.id.backButton);

        // Get the QR code bitmap from the intent
        Bitmap qrCodeBitmap = getIntent().getParcelableExtra("qrCode");

        // Set the QR code bitmap to the ImageView
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        backButton.setOnClickListener(v -> {
            // Finish this activity and return to the previous one
            finish();
        });

        // Share the QR code image
        Button shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> {
            File file = bitmapToFile(qrCodeBitmap, this);
            shareImageFile(file, this);
        });
    }

    public File bitmapToFile(Bitmap bitmap, Context context) {
        // Get the cache directory
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs(); // Make sure the directory exists


        // Create a file to save the bitmap
        File file = new File(cachePath, "shared_images.png");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream); // Save the bitmap
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public void shareImageFile(File file, Context context) {
        Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }


}