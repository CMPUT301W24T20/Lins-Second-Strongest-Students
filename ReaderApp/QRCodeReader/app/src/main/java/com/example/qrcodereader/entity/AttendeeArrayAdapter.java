package com.example.qrcodereader.entity;

import com.example.qrcodereader.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendeeArrayAdapter extends ArrayAdapter<Map.Entry<String, Long>> {
    private ArrayList<Map.Entry<String, Long>> attendees;
    private Context context;
    public AttendeeArrayAdapter(@NonNull Context context, ArrayList<Map.Entry<String, Long>> attendeesList) {
        super(context, 0, attendeesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attendees_content, parent, false);
        }

        Map.Entry<String, Long> attendee = getItem(position);

        TextView attendeeIdTextView = convertView.findViewById(R.id.attendee_id);
        TextView attendeeNumberTextView = convertView.findViewById(R.id.attendee_number);

        if (attendee != null) {
            attendeeIdTextView.setText(attendee.getKey());
            attendeeNumberTextView.setText(String.valueOf(attendee.getValue()));
        }

        return convertView;
    }
}
