package com.example.qrcodereader;

import java.util.ArrayList;
import java.util.Map;

/**
 * Notifier Class
 * Code for handling sending notifications to users
 */
public class Notifier {

    public void notifyUsers(ArrayList<Map.Entry<String, Long>> attendeesList) {

        for (Map.Entry<String, Long> attendee : attendeesList){
            notify(attendee.getKey());
        }

    }

    private void notify(String key) {



    }
}
