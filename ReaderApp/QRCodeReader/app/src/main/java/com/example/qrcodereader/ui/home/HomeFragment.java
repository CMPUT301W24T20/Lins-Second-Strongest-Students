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
import com.example.qrcodereader.MapView;
import com.example.qrcodereader.MapViewOrganizer;
import com.example.qrcodereader.R;
import com.example.qrcodereader.databinding.FragmentHomeBinding;
import com.example.qrcodereader.ui.admin.AdminEventActivity;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.example.qrcodereader.ui.profile.ProfileFragment;

/**
 * This is the Fragment where all the buttons to go to Events, Map, Profile, and Admin access lies
 * <p>
 *     When moving to another Fragment and moving back to opening screen, this is where the user is taken
 * </p>
 * @author Vinay and Son and Khushdeep
 */
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

        Button profile_button = root.findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment listfrag = new ProfileFragment();
                listfrag.show(getChildFragmentManager(), "Profile Page");
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

        Button mapButton = root.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Choose Map You Want to See");

                // Button to go to AttendeeEventActivity
                builder.setPositiveButton("Go to Map (Attendee)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), MapView.class);
                        startActivity(intent);
                    }
                });

                // Button to go to OrganizerEventActivity
                builder.setNegativeButton("Go to Map(Organizer)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), MapViewOrganizer.class);
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

        Button adminButton = root.findViewById(R.id.admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Choose Action (Click Outside to Cancel)");

                // Button to go to AttendeeEventActivity
                builder.setPositiveButton("View Events", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), AdminEventActivity.class);
                        startActivity(intent);
                    }
                });

                // Button to go to OrganizerEventActivity
                builder.setNegativeButton("View Profiles", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                // Cancel button
                builder.setNeutralButton("View Pictures", new DialogInterface.OnClickListener() {
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

    /**
     * This method is called when the Fragment is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}