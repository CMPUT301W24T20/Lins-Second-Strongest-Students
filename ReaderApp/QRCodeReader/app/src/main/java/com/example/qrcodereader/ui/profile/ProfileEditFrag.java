package com.example.qrcodereader.ui.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.ImageUpload;
import com.example.qrcodereader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.example.qrcodereader.util.AppDataHolder;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

/**
 * Fragment for displaying the profile of user
 * @author Tiana
 */
public class ProfileEditFrag extends DialogFragment implements ImageUpload {
    private ImageView Picture;
    private String image;
    private DocumentReference docRefUser;
    private Uri uploaded;
    private int PhoneLength;
    private EditText ETphone;
    private OnSaveClickListener onSaveClickListener;



    public interface OnSaveClickListener {

        void onSaveClicked(String EditName, String EditRegion, String EditPhone, String EditEmail, Uri EditPicture);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_edit_frag, null);

        // find Views
        EditText ETname = view.findViewById(R.id.name);
        EditText ETemail = view.findViewById(R.id.email);
        ETphone = view.findViewById(R.id.phone);
        Spinner Sregion = view.findViewById(R.id.SpinnerRegions);
        Picture = view.findViewById(R.id.ProfilePic);
        int errorColour = ContextCompat.getColor(requireContext(), R.color.red_light);

        Set<String> retrievedRegions = PhoneNumberUtil.getInstance().getSupportedRegions();
        Set<String> regions = new HashSet<>(retrievedRegions);
        regions.add(" ");
        List<String> supportedRegions = new ArrayList<>(regions);
        Collections.sort(supportedRegions);
        ArrayAdapter<String> SpinAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, supportedRegions);
        // Specify the layout to use when the list of choices appears
        SpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sregion.setAdapter(SpinAdapter);

        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("users").document(deviceID);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Sregion.setSelection(supportedRegions.indexOf(documentSnapshot.getString("phoneRegion")));
                String UserRegion = documentSnapshot.getString("phoneRegion");
                setRegionPhone(UserRegion);

                ETname.setText(CheckEmpty(documentSnapshot.getString("name")));
                ETemail.setText(CheckEmpty(documentSnapshot.getString("email")));
                ETphone.setText(CheckEmpty(documentSnapshot.getString("phone")));

                image = documentSnapshot.getString("ProfilePic");

                String imageURL = documentSnapshot.getString("ProfilePic");
                Picasso.get().load(imageURL).resize(100, 100).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
//                    Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        // upload profile pic
        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePictureFrag pictureOption = new ProfilePictureFrag();
                pictureOption.show(getParentFragmentManager(), "Edit Profile Picture");
            }
        });


        // march 25 trim text in an edit text ? programatically?
        Sregion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRegion = parentView.getItemAtPosition(position).toString();
                setRegionPhone(selectedRegion);

                // if pre-existing text, trim down to region phone number length
                String originalInput = ETphone.getText().toString();
                String trimmedInput = originalInput.substring(0, Math.min(originalInput.length(), PhoneLength));
                ETphone.setText(trimmedInput);
                ETphone.setSelection(trimmedInput.length()); // move cursor
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Profile")
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    // able to press Save Button, it is not greyed out == input in edit texts are valid
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (uploaded != null) {
                            isUploaded();
                        }
                        docRefUser.update("name",  ETname.getText().toString());
                        docRefUser.update("email",  ETemail.getText().toString());
                        docRefUser.update("phone",  ETphone.getText().toString());
                        docRefUser.update("phoneRegion",Sregion.getSelectedItem().toString());

                        if (onSaveClickListener != null) {
                            onSaveClickListener.onSaveClicked(ETname.getText().toString(),
                                    ETemail.getText().toString(),
                                    ETphone.getText().toString(),
                                    Sregion.getSelectedItem().toString(),
                                    uploaded);
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();

        // how to grey out postive button of dialogue fragment 3/25/24
        ETphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Enable/disable positive button based on text length
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(s.length() >= PhoneLength);
                if (s.length() < PhoneLength) {
                    // Text is not long enough, change EditText's background color to red
                    ETphone.setBackgroundColor(errorColour);
                } else {
                    // Text is long enough, reset EditText's background color
                    ETphone.setBackgroundColor(Color.WHITE);
                }
            }
        });

        ETemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Enable/disable positive button based on if email valid format or not
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(s.length()==0 || Patterns.EMAIL_ADDRESS.matcher(s).matches());
                if (s.length()>0 && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    // there is email input and it is not of right format
                    ETemail.setBackgroundColor(errorColour);
                } else {
                    // email input is valid
                    ETemail.setBackgroundColor(Color.WHITE);
                }
            }
        });

        return alertDialog;
    }
    @Override
    public void isUploaded(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Context context = getContext();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            InputStream is = contentResolver.openInputStream(uploaded);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            byte[] imageData = baos.toByteArray();
            String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            String imageName = deviceID + "PROFILE" + ".png";

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("UploadedProfilePics/" + imageName);
            UploadTask uploadTask = imageRef.putBytes(imageData);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                image = uri.toString();
                docRefUser.update("ProfilePic",  image);
            }).addOnFailureListener(e -> {
                // Handle getting download URL failure
            });


        } catch (FileNotFoundException e) {e.printStackTrace();
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    /**
     * meow
     */
    private String CheckEmpty(String text){
        if (text == null || text.length() == 0) {return "";}
        else {return text;}
    }

    /**
     * meowwwwwwwwwww
     */
    private void setRegionPhone(String regionCode){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.getExampleNumberForType(regionCode, PhoneNumberUtil.PhoneNumberType.FIXED_LINE);
        if (phoneNumber != null) {
            PhoneLength = (phoneNumberUtil.getNationalSignificantNumber(phoneNumber)).length();
        } else {
            PhoneLength =  0; // If no phone number found for region (i.e. " "), length is 0
        }
        // limit length of input based on region selected
        ETphone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PhoneLength)});
    }

    public void setPicture(Uri uploaded, String URL, int check){
        if (check == 0){ // uploaded new profile picture
            Picture.setImageURI(uploaded);
        } else{ // removed current profile picture, URL is the default profile picture that is replacing
            Picasso.get().load(URL).resize(100, 100).centerInside().into(Picture);
        }
        this.uploaded = uploaded;
    }
}
