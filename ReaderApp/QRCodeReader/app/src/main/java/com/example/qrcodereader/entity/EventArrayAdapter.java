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

    /**
     * Constructor for the EventArrayAdapter
     * @param context
     * @param events
     */
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

        String locationString = (event.getLocationName() != null) ? event.getLocationName() : "No location";
        locationName.setText(locationString);

        return view;
    }

    /**
     * Add event to the list of events
     * @param id the event id
     * @param name the event name
     * @param location the event location
     * @param locationName the event location name
     * @param time the event time
     * @param organizer the event organizer
     * @param organizerID the event organizer id
     * @param qrCode the event qr code
     * @param attendeeLimit the event attendee limit
     * @param attendees the event attendees
     */
    public void addEvent(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode, int attendeeLimit, Map<String, Long> attendees) {
        events.add(new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit,attendees));
        notifyDataSetChanged();
    }
}

