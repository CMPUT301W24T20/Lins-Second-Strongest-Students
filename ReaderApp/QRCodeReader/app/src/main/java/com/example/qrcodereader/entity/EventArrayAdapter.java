package com.example.qrcodereader.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qrcodereader.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.squareup.picasso.Picasso;

/**
 *  The Array Adapter for displaying events in ListViews
 *  <p>
 *      Events are taking from database
 *  </p>
 *  <p>
 *      Used in AttendeeEventActivity.java, OrganizerEventActivity.java, BrowseEventActivity.java
 *  </p>
 *  @author Son and Duy
 */
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
        TextView timeName = view.findViewById(R.id.time_text);
        ImageView PosterPicture = view.findViewById(R.id.rectangle_1);

        eventName.setText(event.getEventName());
        organizerName.setText(event.getOrganizer());

        String locationString = (event.getLocationName() != null) ? event.getLocationName() : "No location";
        locationName.setText(locationString);
        timeName.setText(event.getTime().toDate().toString());

        String imagePoster = event.getPoster();
        //Picasso.get().load(imagePoster).centerInside().fit().into(PosterPicture);

        if (!Objects.equals(imagePoster, "") && !imagePoster.isEmpty()) {
            Picasso.get().load(imagePoster).centerInside().fit().into(PosterPicture);
        } else {
            // Load a default image or clear the ImageView if no poster exists
            Picasso.get().load(R.drawable._49e43ff77b9c6ecc64d8a9b55622ddd7_2).centerInside().fit().into(PosterPicture);
        }

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
     * @param poster the event poster
     */
    public void addEvent(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode, int attendeeLimit, Map<String, Long> attendees, String poster) {
        events.add(new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit,attendees, poster));
        notifyDataSetChanged();
    }
}

