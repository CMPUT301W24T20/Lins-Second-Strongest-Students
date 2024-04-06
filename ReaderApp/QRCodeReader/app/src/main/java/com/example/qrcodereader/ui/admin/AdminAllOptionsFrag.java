package com.example.qrcodereader.ui.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.MainActivity;

public class AdminAllOptionsFrag extends DialogFragment {

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
                dialog.dismiss();
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
