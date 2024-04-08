package com.example.qrcodereader;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.entity.UserArrayAdapter;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


//This test was adapted from EventArrayAdapterTest by me, the test this is based off is written by ChatGPT4
@RunWith(AndroidJUnit4.class)
public class UserArrayAdapterTest {

    private UserArrayAdapter adapter;
    private ArrayList<User> users;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        users = new ArrayList<>();
        adapter = new UserArrayAdapter(context, users);
    }

    @Test
    public void testAddUser() {
        // Create a new user and add it to the adapter
        String userID = "1";
        String name = "Test User";
        String email = "test@example.com";
        String phoneRegion = "+1";
        String phone = "1234567890";
        String profilePic = "profile_pic_url";
        GeoPoint location = new GeoPoint(12.1, 12.1);

        User user = new User(userID, name, email, phoneRegion, phone, profilePic);
        adapter.add(user);

        // Check that the user was added to the adapter
        assertEquals(1, adapter.getCount());

        // Get the user from the adapter and check its details
        User addedUser = adapter.getItem(0);
        assertEquals(userID, addedUser.getUserID());
        assertEquals(name, addedUser.getName());
        assertEquals(email, addedUser.getEmail());
        assertEquals(phoneRegion, addedUser.getPhoneRegion());
        assertEquals(phone, addedUser.getPhone());
        assertEquals(profilePic, addedUser.getProfilePicture());
    }
}
