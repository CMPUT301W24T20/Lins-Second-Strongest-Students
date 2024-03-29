package com.example.qrcodereader.ui.notifications;

import static androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED;
import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.MyFirebaseMessagingService;
import com.example.qrcodereader.R;
import com.example.qrcodereader.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationsFragment
 * Handles view for notification menu
 * Stores received firebase messages for reference
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private Button deleteOne;
    private Button clearAll;
    private NotificationAdapter adapter;
    private final String userID = MainActivity.userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ID:", "=" + userID);
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        deleteOne = view.findViewById(R.id.delete_button);
        clearAll = view.findViewById(R.id.clear_button);
        ListView listView = view.findViewById(R.id.notification_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                            adapter = new NotificationAdapter(getContext(), notifications);
                            listView.setAdapter(adapter);
                        } else {
                            Log.w("NotifDocRetrieval", "Error getting documents.", task.getException());
                        }
                    }
                });

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
                    Toast.makeText(getActivity(), "No item selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <= adapter.getCount(); i++) {
                    NotificationDetail item = adapter.getItem(i);
                    removeItem(item);
                }
            }
        });

        return view;

    }

    private void removeItem(NotificationDetail item) {
        adapter.remove(item);
        adapter.notifyDataSetChanged();
        item.delete();
    }

    /**
     * onDestroyView
     * Handles view destruction
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}