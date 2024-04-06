package com.example.qrcodereader.ui.admin;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

// Microsoft Copilot 2024 "Given EventDetailsAdminActivity adapt to user"
public class UserDetailsAdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DocumentReference docRefUser;

    private final String TAG = "UserDetailsAdminActivity";
    private ImageView Picture;

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView region;
    private Uri pictureUri;
    String userID;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        name = findViewById(R.id.name);
        Picture = findViewById(R.id.user_profile_photo);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        region = findViewById(R.id.phone_region);
        View view = LayoutInflater.from(this).inflate(R.layout.profile, null);

        FirebaseFirestore db = FirestoreManager.getInstance().getDb();
        userID = getIntent().getStringExtra("userID");
        docRefUser = db.collection("users").document(userID);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch the user details

                name.setText(CheckEmpty(documentSnapshot.getString("name")));
                email.setText(CheckEmpty(documentSnapshot.getString("email")));
                phone.setText(CheckEmpty(documentSnapshot.getString("phone")));
                region.setText(CheckEmpty(documentSnapshot.getString("phoneRegion")));

                String imageURL = documentSnapshot.getString("ProfilePic");
                if (imageURL != null) {
                    pictureUri = Uri.parse(imageURL);
                }

                Picasso.get().load(imageURL).resize(200, 200).centerInside().into(Picture);

                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        TextView removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(v -> {
            if (userID != null) {
                db.collection("users").document(userID)
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
            }
            finish();
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    private String CheckEmpty(String text){
        if (text == null || text.isEmpty()) {return "";}
        else {return text;}
    }
}

