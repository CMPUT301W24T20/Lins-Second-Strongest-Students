package com.example.qrcodereader.ui.eventPage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        TextView userPhone = findViewById(R.id.UserDeviceText);

        String attendeeID = getIntent().getStringExtra("attendeeID");
        db = FirebaseFirestore.getInstance();
        docRefUser = db.collection("events").document(attendeeID);

        // Removing remove button from layout
        TextView removeButton = findViewById(R.id.remove_button);
        removeButton.setVisibility(View.GONE);

        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phoneRegion= documentSnapshot.getString("phoneRegion");
                String phone= documentSnapshot.getString("phone");
                Log.d("Firestore", "Successfully fetch document: ");

                userName.setText(name);
                userEmail.setText(email);
                userPhoneRegion.setText(phoneRegion);
                userPhone.setText(phone);

//                List<String> attendeesList = new ArrayList<>(usersAttended.keySet());
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        this, android.R.layout.simple_list_item_1, attendeesList);
//                attendeesListView.setAdapter(adapter);

            }
        }).addOnFailureListener(e -> {
            Log.d("Firestore", "Failed to fetch document");
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());

    }
}
