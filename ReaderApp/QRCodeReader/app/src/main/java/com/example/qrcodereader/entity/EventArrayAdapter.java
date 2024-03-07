package com.example.qrcodereader.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.qrcodereader.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;


public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    //        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.events_content, parent,false);
        }

        Event event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_text);
        TextView organizerName = view.findViewById(R.id.organizer_text);
        TextView locationName = view.findViewById(R.id.event_location_text);

        eventName.setText(event.getEventName());
        organizerName.setText(event.getOrganizer());

        //GeoPoint geoPoint = event.getLocation();
        // Handle no location object
        //String locationString = (geoPoint != null) ? geoPoint.toString() : "No location";
        String locationString = (event.getLocationName() != null) ? event.getLocationName() : "No location";
        locationName.setText(locationString);

        return view;
    }

    public void addEvent(String eventID, String name, String organizer, GeoPoint location, Timestamp time) {
        events.add(new Event(eventID, name, organizer, location, time));
        notifyDataSetChanged();
    }

    public void addEvent(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode, Map<String, Long> attendees) {
        events.add(new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendees));
        notifyDataSetChanged();
    }
}

