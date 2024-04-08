package com.example.qrcodereader.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.UserArrayAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// Microsoft Copilot 4/8/2024 "Provided code for previous implementation rewrite it so that query doesn't fail & add javaDocs"
/**
 * Activity for admin to browse users. Only admins have access.
 */
public class AdminUserActivity extends AppCompatActivity {

    private static final String TAG = "AdminUserActivity";
    private static final int PAGE_SIZE = 10;

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private ArrayList<User> userDataList;
    private UserArrayAdapter userArrayAdapter;
    private DocumentSnapshot lastVisible;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);
        getSupportActionBar().hide();

        db = FirestoreManager.getInstance().getDb();
        usersRef = FirestoreManager.getInstance().getUserCollection();

        ListView userList = findViewById(R.id.user_content);
        userDataList = new ArrayList<>();
        userArrayAdapter = new UserArrayAdapter(this, userDataList);
        userList.setAdapter(userArrayAdapter);

        Log.d(TAG, "Starting to fetch users...");
        fetchUsers();

        userList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isFetching && (firstVisibleItem + visibleItemCount >= totalItemCount)) {
                    Log.d(TAG, "Reached bottom of list, fetching more users...");
                    fetchUsers();
                }
            }
        });

        userList.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userDataList.get(position);
            Intent detailIntent = new Intent(AdminUserActivity.this, UserDetailsAdminActivity.class);
            FirestoreManager.getInstance().setUserDocRef(selectedUser.getUserID());
            startActivity(detailIntent);
        });

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    private void fetchUsers() {
        if (isFetching) {
            Log.d(TAG, "Fetch already in progress, skipping this fetch request.");
            return;
        }

        isFetching = true;

        Query query = usersRef.limit(PAGE_SIZE);
        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot doc : documents) {
                    User user = new User(doc.getId(), doc.getString("name"),
                            doc.getString("email"), doc.getString("phoneRegion"),
                            doc.getString("phone"), doc.getString("ProfilePic"));
                    userDataList.add(user);
                }
                Log.d(TAG, "Fetched " + documents.size() + " users.");
                userArrayAdapter.notifyDataSetChanged();
                if (!documents.isEmpty()) {
                    lastVisible = documents.get(documents.size() - 1);
                }
            } else {
                Log.e(TAG, "Error fetching users", task.getException());
            }
            isFetching = false;
        });
    }
    // Microsoft Copilot 2024 "Activity lifecycle issue, want user list to reset once I come back given code"
    @Override
    protected void onResume() {
        super.onResume();

        // Clear the existing user data list
        userDataList.clear();

        // Fetch the users again
        fetchUsers();
    }
}
