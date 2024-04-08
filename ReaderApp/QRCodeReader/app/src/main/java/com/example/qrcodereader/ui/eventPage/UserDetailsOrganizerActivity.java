package com.example.qrcodereader.ui.eventPage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserDetailsOrganizerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.user_details);

        TextView userName = findViewById(R.id.name);
        TextView userEmail = findViewById(R.id.email);
        TextView userPhoneRegion = findViewById(R.id.phone_region);
        TextView userPhone = findViewById(R.id.phone);
        ImageView userProfilePic = findViewById(R.id.user_profile_photo);

        String attendeeID = getIntent().getStringExtra("attendeeID");
        db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("users").document(attendeeID);

        // Removing remove button from layout
        TextView removeButton = findViewById(R.id.remove_button);
        removeButton.setVisibility(View.GONE);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phoneRegion= documentSnapshot.getString("phoneRegion");
                String phone= documentSnapshot.getString("phone");
                String pictureURL = documentSnapshot.getString("ProfilePic");
                userName.setText(name);
                userEmail.setText(email);
                userPhoneRegion.setText(phoneRegion);
                userPhone.setText(phone);
                Picasso.get().load(pictureURL).resize(200, 200).centerInside().into(userProfilePic);
                Log.d("Firestore", "Successfully fetch document: ");

            } else {
                Toast.makeText(UserDetailsOrganizerActivity.this, "Unsuccessfully fetch document", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(UserDetailsOrganizerActivity.this, "Unsuccessfully fetch document", Toast.LENGTH_SHORT).show();
            Log.d("Firestore", "Failed to fetch document");
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }
}