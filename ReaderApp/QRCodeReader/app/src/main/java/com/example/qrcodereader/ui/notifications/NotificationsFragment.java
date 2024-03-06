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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;
import com.example.qrcodereader.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

/**
 * NotificationsFragment
 * Handles view for notification menu
 * Stores received firebase messages for reference
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = root.findViewById(R.id.notification_list);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);

        return root;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // See MyFirebaseMessagingService for broadcast
            //TO-DO:
            String notificationData = intent.getStringExtra("body"); //key of intent
            Log.d("Received", notificationData);
            listItems.add((notificationData));
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("BROADCAST_NOTIFICATION");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getActivity().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}