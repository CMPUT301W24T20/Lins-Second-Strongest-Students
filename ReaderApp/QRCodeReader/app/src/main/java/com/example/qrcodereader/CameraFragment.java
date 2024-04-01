package com.example.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for housing the camera/scanner operations for scanning QR code
 * @author Vinay
 */
public class CameraFragment extends AppCompatActivity implements View.OnClickListener {
    /*
    CameraFragment
    Contains code for a fragment implementing a QR code scanner
     */
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CameraFragment() {
        super(R.layout.fragment_camera);
    }

    Button scanButton;
    private View thisView;
    private String userID;
    private ScanHandler scanHandler;

    /*
     * startScan
     * Opens QR code reader
     */
    private void startScan() {
        IntentIntegrator integrator = new IntentIntegrator(CameraFragment.this);
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
                Toast.makeText(CameraFragment.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CameraFragment.this, "Scanned Code: " + result.getContents(), Toast.LENGTH_LONG).show();
                scanHandler.scannedCode(result.getContents());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Set up fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_camera);
        scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        userID = MainActivity.userId;
        scanHandler = new ScanHandler(db, userID);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(CameraFragment.this, "Scanning...", Toast.LENGTH_LONG).show();
        startScan();
    }

}
