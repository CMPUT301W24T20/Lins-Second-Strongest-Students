package com.example.qrcodereader.ui.notifications;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationsActivity
 * Handles view for notification menu
 * Stores received firebase messages for reference
 */
public class NotificationsActivity extends NavBar {

    private Button deleteOne;
    private Button clearAll;
    private Button returnButton;
    private NotificationAdapter adapter;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupNavigation();
        setContentView(R.layout.activity_notifications);
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID:", "=" + userID);

        deleteOne = findViewById(R.id.delete_button);
        clearAll = findViewById(R.id.clear_button);
        returnButton = findViewById(R.id.return_button);
        ListView listView = findViewById(R.id.notification_list);
        TextView noMessages = findViewById(R.id.no_messages);
        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("users").document(userID).collection("notifications")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<NotificationDetail> notifications = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    NotificationDetail notification = document.toObject(NotificationDetail.class);
                                    notifications.add(notification);
                                }
                                adapter = new NotificationAdapter(NotificationsActivity.this, notifications);
                                listView.setAdapter(adapter);
                            } else {
                                Log.w("NotifDocRetrieval", "Error getting documents.", task.getException());
                            }
                        }
                    });
        } catch (NullPointerException e) {
            noMessages.setVisibility(View.VISIBLE);
            Toast.makeText(NotificationsActivity.this, "No New Messages", Toast.LENGTH_SHORT).show();
        }

        if (adapter == null || adapter.getCount() < 1) {
            noMessages.setVisibility(View.VISIBLE);
            Toast.makeText(NotificationsActivity.this, "No New Messages", Toast.LENGTH_SHORT).show();
        }

        final Object[] lastTappedItem = new Object[1];

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastTappedItem[0] = parent.getItemAtPosition(position);
            }
        });

        deleteOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastTappedItem[0] != null) {
                    removeItem((NotificationDetail) lastTappedItem[0]);
                } else {
                    Toast.makeText(NotificationsActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter == null || adapter.getCount() == 0) {
                    Toast.makeText(NotificationsActivity.this, "Nothing to delete", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = adapter.getCount() - 1; i >= 0; i--) {
                        NotificationDetail item = adapter.getItem(i);
                        removeItem(item);
                    }
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notifications;
    }

    private void removeItem(NotificationDetail item) {
        adapter.remove(item);
        adapter.notifyDataSetChanged();
        item.delete();
    }

}