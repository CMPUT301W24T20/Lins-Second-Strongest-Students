package com.example.qrcodereader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qrcodereader.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

public class CameraFragment extends Fragment {

    //Used this link as a resource:
    //youtube.com/watch?v=L482ZAno-fY
    //Under GNU general public license

    private FragmentCameraBinding binding;
    private ImageButton capture;
    private PreviewView previewView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        previewView = getView().findViewById(R.id.camera_preview);
        capture = getView().findViewById(R.id.camera_capture_button);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void startCamera() {
        int aspectRatio = findAspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture listenableFuture = ProcessCameraProvider.getInstance(this.getContext());

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();
                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePhoto(imageCapture);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this.getContext()));
    }

    private int findAspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(height, width) / Math.min(height,width);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void takePhoto(ImageCapture imageCapture) {

    }

}
