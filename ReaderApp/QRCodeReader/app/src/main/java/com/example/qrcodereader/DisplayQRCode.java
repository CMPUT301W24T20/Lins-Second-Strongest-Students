package com.example.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.qrcodereader.entity.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity for displaying QR code
 * @author Duy
 */
public class DisplayQRCode extends AppCompatActivity {
    String type = "Check in";
    Bitmap qrCodeBitmap;
    Bitmap qrCodeBitmapPromotional;
    String displayingQR;
    String qrCode;
    String qrCodePromotional;
    // OpenAI, 2024, ChatGPT, given the code snippet, prompt to display the generated QR code from the bitmap
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        EdgeToEdge.enable(this);

        qrCode = getIntent().getStringExtra("qrCode");
        qrCodePromotional = getIntent().getStringExtra("promotionalQRCode");

        qrCodeBitmap = new QRCode(qrCode).getBitmap();
        qrCodeBitmapPromotional = new QRCode(qrCodePromotional).getBitmap();

        setContentView(R.layout.activity_display_qrcode);

        ImageView qrCodeImageView = findViewById(R.id.qrCodeImageView);
        ImageView backButton = findViewById(R.id.backButton);

        TextView typeTextView = findViewById(R.id.QR_type);

        TextView switchButton = findViewById(R.id.switch_button);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Check in")) {
                    type = "Promotional";
                    displayingQR = qrCodePromotional;

                    typeTextView.setText(type);
                    // Get the QR code bitmap from the intent
                    // Set the QR code bitmap to the ImageView
                    qrCodeImageView.setImageBitmap(qrCodeBitmapPromotional);
                } else {
                    type = "Check in";
                    displayingQR = qrCode;

                    typeTextView.setText(type);
                    // Get the QR code bitmap from the intent
                    // Set the QR code bitmap to the ImageView
                    qrCodeImageView.setImageBitmap(qrCodeBitmap);
                }
            }
        });

        // Get the QR code bitmap from the intent
        // Set the QR code bitmap to the ImageView
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        displayingQR = qrCode;

        backButton.setOnClickListener(v -> {
            // Finish this activity and return to the previous one
            finish();
        });

        // Share the QR code image
        TextView shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> {
            File file;
            if (type.equals("Check in")) {
                file = bitmapToFile(qrCodeBitmap, this);
            } else {
                file = bitmapToFile(qrCodeBitmapPromotional, this);
            }
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

    public String getDisplayingQR() {
        return displayingQR;
    }

    public String getType() {
        return type;
    }
}