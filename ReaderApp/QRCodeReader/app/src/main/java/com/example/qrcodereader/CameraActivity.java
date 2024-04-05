package com.example.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Fragment for housing the camera/scanner operations for scanning QR code
 * @author Vinay
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    CameraActivity
    Contains code for a fragment implementing a QR code scanner
     */
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CameraActivity() {
        super(R.layout.fragment_camera);
    }

    Button scanButton;
    private View thisView;
    private String userID = AttendeeEventActivity.userID;
    private ScanHandler scanHandler;

    /*
     * startScan
     * Opens QR code reader
     */
    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(CameraActivity.this);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Code");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(CameraActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CameraActivity.this, "Scanned Code: " + result.getContents(), Toast.LENGTH_LONG).show();
                scanHandler.scannedCode(result.getContents());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Set up fragment
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.fragment_camera);
        scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        scanHandler = new ScanHandler(db, userID);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(CameraActivity.this, "Scanning...", Toast.LENGTH_LONG).show();
        startScan();
    }

}
