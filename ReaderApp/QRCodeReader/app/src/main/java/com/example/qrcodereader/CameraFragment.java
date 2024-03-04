package com.example.qrcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CameraFragment extends Fragment implements View.OnClickListener {
    /*
    CameraFragment
    Contains code for a fragment implementing a QR code scanner
     */

    public CameraFragment() {
        super(R.layout.fragment_camera);
    }

    Button scanButton;
    private View thisView;

    /*
     * startScan
     * Opens QR code reader
     */
    private void startScan() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(CameraFragment.this);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Code");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned Code: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set up fragment
        thisView = inflater.inflate(R.layout.fragment_camera, container, false);
        scanButton = (Button) thisView.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);
        return thisView;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Scanning...", Toast.LENGTH_LONG).show();
        startScan();
    }

}
