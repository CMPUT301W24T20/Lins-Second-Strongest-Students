package com.example.qrcodereader;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.ui.notifications.NotificationAdapter;
import com.example.qrcodereader.ui.notifications.NotificationDetail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class NotificationAdapterTest {

    private NotificationAdapter adapter;
    private ArrayList<NotificationDetail> notifications;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(context, notifications);
    }

    @Test
    public void testAddNotification() {
        // Create a new notification and add it to the adapter
        String event = "Event ID";
        String title = "Test Title";
        String body = "Test Body";

        NotificationDetail notification = new NotificationDetail(event, title, body);
        adapter.add(notification);

        // Check that the notification was added to the adapter
        assertEquals(1, adapter.getCount());

        // Get the notification from the adapter and check its details
        NotificationDetail addedNotification = adapter.getItem(0);
        assertEquals(event, addedNotification.getEvent());
        assertEquals(title, addedNotification.getTitle());
        assertEquals(body, addedNotification.getBody());
    }
}
