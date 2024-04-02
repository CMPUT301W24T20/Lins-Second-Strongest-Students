package com.example.qrcodereader.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.UserArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminUserActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private User selectedUser = null;
    private boolean isFetching = false;
    private static final int PAGE_SIZE = 10;
    private DocumentSnapshot lastVisible;
    private ArrayList<User> userDataList;
    private UserArrayAdapter userArrayAdapter;
    private final String TAG = "AdminUserActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_user);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        ListView userList = findViewById(R.id.user_list);
        userDataList = new ArrayList<>();

        userArrayAdapter = new UserArrayAdapter(this, userDataList);
        userList.setAdapter(userArrayAdapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Get the item that was clicked
                selectedUser = userDataList.get(position);

                // Display a toast with the selected item
                Intent detailIntent = new Intent(AdminUserActivity.this, UserDetailsAdminActivity.class);
                detailIntent.putExtra("userID", selectedUser.getUserID());
                startActivity(detailIntent);
                selectedUser = null;
            }
        });
    }
}
