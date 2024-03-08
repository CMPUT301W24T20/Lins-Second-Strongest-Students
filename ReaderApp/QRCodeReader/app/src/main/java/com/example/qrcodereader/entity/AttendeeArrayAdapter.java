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


/**
 * ArrayAdapter for attendees of events
 * <p>
 *     This class is mainly used in EventDetailsOrganizerActivity.java
 * </p>
 * <p>
 *     This class displays the Attendees map field of Events in a ListView as well as who have checked in and the number of time they checked in
 * </p>
 * @author Son
 */

/*
            OpenAI, ChatGpt, 06/03/24
            "I need an array adapter that displays a map field of String and Long in a ListView"
        */
public class AttendeeArrayAdapter extends ArrayAdapter<Map.Entry<String, Long>> {
    private ArrayList<Map.Entry<String, Long>> attendees;
    private Context context;
    public AttendeeArrayAdapter(@NonNull Context context, ArrayList<Map.Entry<String, Long>> attendees) {
        super(context, 0, attendees);
        this.attendees = attendees;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.attendees_content, parent, false);
        }

        Map.Entry<String, Long> attendee = attendees.get(position);

        TextView attendeeIdTextView = convertView.findViewById(R.id.attendee_id);
        TextView attendeeNumberTextView = convertView.findViewById(R.id.attendee_number);
        TextView attendeeCheckTextView = convertView.findViewById(R.id.attendee_check);

        if (attendee != null) {
            attendeeIdTextView.setText(attendee.getKey());
            attendeeNumberTextView.setText(String.valueOf(attendee.getValue()));
            if (attendee.getValue() > 0) {
                attendeeCheckTextView.setText("Yes");
            } else {
                attendeeCheckTextView.setText("No");
            }
        }

        return convertView;
    }
}
