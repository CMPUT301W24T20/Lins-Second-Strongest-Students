package com.example.qrcodereader.ui.profile;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.util.ImageUpload;
import com.example.qrcodereader.R;
import com.google.firebase.firestore.DocumentReference;
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

    /**
     * This listener interface handles saving the inputs of this edit fragment into database events
     */
    public interface OnSaveClickListener {
        /**
         * Called when user clicks the positive(save) button in this edit fragment
         * @param EditName the possibly edited name entered by the user
         * @param EditRegion the possibly edited region selected by the user
         * @param EditPhone the possibly edited phone number entered by the user
         * @param EditEmail the possibly edited email address entered by the use
         * @param EditPicture the URI of the possibly changed profile picture selected by the user
         */
        void onSaveClicked(String EditName, String EditRegion, String EditPhone, String EditEmail, Uri EditPicture);
    }

    /**
     * This method sets the listener to be notified when the user clicks the positive(save) button in this edit fragment
     * @param listener the listener to be set
     */
    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.profile_edit_frag, null);

        EditText ETname = view.findViewById(R.id.name);
        EditText ETemail = view.findViewById(R.id.email);
        ETphone = view.findViewById(R.id.phone);
        Spinner Sregion = view.findViewById(R.id.SpinnerRegions);
        Picture = view.findViewById(R.id.ProfilePic);
        int errorColour = ContextCompat.getColor(requireContext(), R.color.red);

        /*
        OpenAI, ChatGPT, 03/25/24
        How do I retrieve all available phone region codes and set it to a spinner?
        */
        // ChatGPT code starts here
        Set<String> retrievedRegions = PhoneNumberUtil.getInstance().getSupportedRegions();
        Set<String> regions = new HashSet<>(retrievedRegions);
        regions.add(" ");
        List<String> supportedRegions = new ArrayList<>(regions);
        Collections.sort(supportedRegions);
        ArrayAdapter<String> SpinAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown, supportedRegions);
        SpinAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        Sregion.setAdapter(SpinAdapter);
        // ChatGPT code ends here

        Bundle args = getArguments();
        ETname.setText(args.getString("name", ""));
        ETemail.setText(args.getString("email", ""));
        ETphone.setText(args.getString("phone", ""));
        Sregion.setSelection(supportedRegions.indexOf(args.getString("region", "")));
        setRegionPhone(args.getString("region", ""));
        Uri imageUri = args.getParcelable("pfp");
        Picasso.get().load(imageUri).transform(new CircleTransformation()).resize(127, 127).into(Picture);

        String deviceID = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("users").document(deviceID);

        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show profile picture options
                ProfilePictureFrag pictureOption = new ProfilePictureFrag();
                pictureOption.show(getParentFragmentManager(), "Edit Profile Picture");
            }
        });

        Sregion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRegion = parentView.getItemAtPosition(position).toString();
                setRegionPhone(selectedRegion);
                String originalInput = ETphone.getText().toString();
                /*
                OpenAI, ChatGPT, 03/25/24
                Trim text in an edit text?
                */
                // ChatGPT code starts here
                String trimmedInput = originalInput.substring(0, Math.min(originalInput.length(), PhoneLength));
                ETphone.setText(trimmedInput);
                ETphone.setSelection(trimmedInput.length()); // move cursor
                // ChatGPT code ends here
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setTitle("Edit Profile Details")
                .setNegativeButton("Cancel", null) // do nothing and close
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    // able to press Save Button, it is not greyed out == input in edit texts are valid
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (uploaded != null) {
                            isUploaded(deviceID);
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
        /*
        OpenAI, ChatGPT, 03/25/24
        how to grey out positive button of dialogue fragment
        */
        // Reference to the ChatGPT code starts here
        ETphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Enable/disable positive(save) button based on text length
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(s.length() >= PhoneLength);

                if (s.length() < PhoneLength) {
                    // phone input not long enough
                    ETphone.setTextColor(errorColour);
                } else {
                    ETphone.setTextColor(Color.BLACK);
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
                // Enable/disable positive(save) button based on if email valid format or not
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(s.length()==0 || Patterns.EMAIL_ADDRESS.matcher(s).matches());

                if (s.length()>0 && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    // there is email input and it is not of valid email format
                    ETemail.setTextColor(errorColour);
                } else {
                    ETemail.setTextColor(Color.BLACK);
                }
            }
        });
        // Reference to the ChatGPT code ends here
        return alertDialog;
    }

    /**
     * meow
     */
    @Override
    public void isUploaded(String deviceID){
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
     * This method alters the length restriction of the EditText for phone input based on the phone region selected
     * @param regionCode the String of the phone region selected
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

    /**
     * This method alters the length restriction of the EditText for phone input based on the phone region selected
     * @param uploaded the Uri of image from user's photo gallery
     * @param URL the String of image URL from database that is default profile picture
     */
    public void setPicture(Uri uploaded, String URL){
        if (uploaded != null ){ // uploaded new profile picture
            Picasso.get().load(uploaded).transform(new CircleTransformation()).resize(127, 127).centerInside().into(Picture);
            this.uploaded = uploaded;
        } else{ // removed current profile picture, URL is the default profile picture that is replacing
            Picasso.get().load(URL).transform(new CircleTransformation()).resize(127, 127).centerInside().into(Picture);
            this.uploaded = Uri.parse(URL);
        }
    }

    /*
    OpenAI, ChatGPT, 04/01/24
    how do i trim an image to circle
    */
    // ChatGPT code starts here excluding the javadoc
    /**
     * A class that implements Transformation interface to transform a Bitmap into a circular shape
     * This transformation is used for displaying circular images
     */
    private static class CircleTransformation implements Transformation {
        /**
         * This method transforms the source Bitmap into a circular shape
         * @param source the source Bitmap to be transformed
         * @return a new Bitmap that is a circular transformation of the source Bitmap
         */
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
    // ChatGPT code ends here
}
