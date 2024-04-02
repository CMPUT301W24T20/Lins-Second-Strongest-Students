package com.example.qrcodereader.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
import androidx.core.content.res.ResourcesCompat;
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
import com.squareup.picasso.Transformation;

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

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View customTitleView = inflater.inflate(R.layout.profile_edit_title_layout, null);

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
                Picasso.get().load(imageURL).transform(new CircleTransformation()).resize(100, 100).centerInside().into(Picture);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user " + e.getMessage());
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
                .setCustomTitle(customTitleView)
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
                            onSaveClickListener.onSaveClicked(
                                    ETname.getText().toString(),
                                    ETemail.getText().toString(),
                                    ETphone.getText().toString(),
                                    Sregion.getSelectedItem().toString(),
                                    uploaded);
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button saveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                Typeface customFont = ResourcesCompat.getFont(requireContext(), R.font.alata);

                cancelButton.setTypeface(customFont);
                saveButton.setTypeface(customFont);
            }
        });

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
            String imageName = deviceID + "PROFILEPICTURE.png";

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("UploadedProfilePics/" + imageName);
            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    image = uri.toString();
                    docRefUser.update("ProfilePic",  image);
                });
                Log.d(TAG, "Image uploaded successfully: " + imageName);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error uploading image " + imageName + ": " + e.getMessage());
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

    public void setPicture(Uri uploaded, String URL){
        if (uploaded != null ){ // uploaded new profile picture
            Picasso.get().load(uploaded).transform(new CircleTransformation()).resize(100, 100).centerInside().into(Picture);
            this.uploaded = uploaded;
        } else{ // removed current profile picture, URL is the default profile picture that is replacing
            Picasso.get().load(URL).transform(new CircleTransformation()).resize(100, 100).centerInside().into(Picture);
            this.uploaded = Uri.parse(URL);
        }
    }

    // how do i trim an image to circle april 1
    public class CircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            // Create a circular canvas
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float radius = size / 2f;
            canvas.drawCircle(radius, radius, radius, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {return "circle";} // key method not used in app but is required implementation due to interface Transformation
    }
}
