package com.example.qrcodereader.ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AdminAllOptionsFrag extends DialogFragment {
    /**
     * This method creates the Dialog fragment of choices of pages for admin to go to
     * @param savedInstanceState the Bundle that is previous saved state
     * @return Return a new Dialog instance to be displayed by the fragment.
     */
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle("Choose Action");

        // Button to go to AttendeeEventActivity
        builder.setPositiveButton("View Events", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(requireActivity(), AdminEventActivity.class);
                startActivity(intent);
            }
        });

        // Button to go to OrganizerEventActivity
        builder.setNegativeButton("View Profiles", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(requireActivity(), AdminUserActivity.class);
                startActivity(intent);
            }
        });

        // Cancel button
        builder.setNeutralButton("View Pictures", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(requireActivity(), AdminImagesOptionActivity.class);
                requireActivity().startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
