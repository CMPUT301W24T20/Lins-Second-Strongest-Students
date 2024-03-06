package com.example.qrcodereader.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;

public class ProfileFragment extends DialogFragment {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;

    private ImageView Picture;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_frag, null);

        // find Views
        EditText ETname = view.findViewById(R.id.name);
        EditText ETcontact = view.findViewById(R.id.contact);
        Button Upload = view.findViewById(R.id.UploadProfileButton);
        Button Remove = view.findViewById(R.id.RemoveProfilePicButton);
        Picture = view.findViewById(R.id.ProfilePic);
        Button locationSwitch = view.findViewById(R.id.LocationSwitch);

        Bundle bundle = getArguments();

        ETname.setText(bundle.getString("UserName"));

        // move switch

//        locationSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                intent.setData(uri);
//                intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
//                startActivity(intent);
//            }
//        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Location Access")
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // manipulate locationAccess field in DB
//                        updatelocationAccess(locationSwitch.isChecked());
                    }
                });

        return builder.create();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Picture.setImageURI(selectedImageUri); // Set the image directly from URI
                // maybe also associate it in DB
            }
        }
    }

}