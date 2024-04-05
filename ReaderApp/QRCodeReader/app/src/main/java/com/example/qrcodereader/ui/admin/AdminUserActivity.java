package com.example.qrcodereader.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.UserArrayAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

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

        fetchUsers();

        userList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Load more items if we've reached the bottom
                if (!isFetching && (firstVisibleItem + visibleItemCount >= totalItemCount)) {
                    fetchUsers();
                }
            }
        });

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

        TextView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    private void fetchUsers() {
        // Prevents fetching new data if previous request is still in progress
        if (isFetching) {
            return;
        }

        isFetching = true;

        Query query = usersRef.orderBy("time", Query.Direction.DESCENDING).limit(PAGE_SIZE);
        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Assuming User class has a constructor matching this data
                        User user = new User(doc.getId(), doc.getString("name"),
                                doc.getString("email"), doc.getString("phoneRegion"),
                                doc.getString("phone")
                                );
                        userDataList.add(user);
                    }
                    userArrayAdapter.notifyDataSetChanged();
                    int lastIndexOfQuery = queryDocumentSnapshots.size() - 1;
                    lastVisible = queryDocumentSnapshots.getDocuments().get(lastIndexOfQuery);
                }
                isFetching = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isFetching = false;
                Log.e("Firestore", "Error fetching users", e);
            }
        });
    }
}
