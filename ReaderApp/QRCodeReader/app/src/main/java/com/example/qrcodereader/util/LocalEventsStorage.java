package com.example.qrcodereader.util;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.qrcodereader.entity.Event;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalEventsStorage {
    //private static final String FILE_NAME = "events.json";

    public static void saveEvents(Context context, ArrayList<Event> events, String fileName) {
        Gson gson = new Gson();
        String eventsJson = gson.toJson(events);

        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(eventsJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Event> loadEvents(Context context, String fileName) {
        Gson gson = new Gson();
        Type eventType = new TypeToken<ArrayList<Event>>() {}.getType();

        try (FileInputStream fis = context.openFileInput(fileName)) {
            InputStreamReader isr = new InputStreamReader(fis);
            return gson.fromJson(isr, eventType);
        } catch (IOException e) {
            e.printStackTrace();
            // Return an empty list if there's an issue loading the file
            return new ArrayList<>();
        }
    }
}
