package com.example.qrcodereader.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;
import com.example.qrcodereader.databinding.FragmentHomeBinding;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //Moved + adapted from MainActivity

        // I'm working on this part - Duy
        Button profile_button = root.findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrganizerEventActivity.class);
                startActivity(intent);
            }
        });

        Button MyEventButton = root.findViewById(R.id.my_event_button);
        MyEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Choose an action");

                // Button to go to AttendeeEventActivity
                builder.setPositiveButton("Go to Your Event Page (Attendee)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), AttendeeEventActivity.class);
                        startActivity(intent);
                    }
                });

                // Button to go to OrganizerEventActivity
                builder.setNegativeButton("Go to Your Event Page (Organizer)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), OrganizerEventActivity.class);
                        startActivity(intent);
                    }
                });

                // Cancel button
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}