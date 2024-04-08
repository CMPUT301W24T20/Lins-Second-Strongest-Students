package com.example.qrcodereader.util;

import android.content.Context;

import com.example.qrcodereader.entity.Event;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
/**
 * The LocalEventsStorage class provides methods to save and load events locally on the device.
 */
public class LocalEventsStorage {
    /**
     * Saves events locally on the device.
     *
     * @param context  The context of the application.
     * @param events   The list of events to be saved.
     * @param fileName The name of the file to save the events to.
     */
    public static void saveEvents(Context context, ArrayList<Event> events, String fileName) {
        Gson gson = new Gson();
        String eventsJson = gson.toJson(events);

        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(eventsJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads events from a local file on the device.
     *
     * @param context  The context of the application.
     * @param fileName The name of the file from which to load the events.
     * @return An ArrayList of Event objects loaded from the file.
     */
    public static ArrayList<Event> loadEvents(Context context, String fileName) {
        Gson gson = new Gson();

        try (FileInputStream fis = context.openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(fis);
            return gson.fromJson(isr, new TypeToken<ArrayList<Event>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            // Return an empty list if there's an issue loading the file
            return new ArrayList<>();
        }
    }
}
