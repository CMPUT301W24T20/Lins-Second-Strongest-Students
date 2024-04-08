package com.example.qrcodereader.util;
import android.content.Context;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.example.qrcodereader.entity.User;
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
/**
 * The LocalUserStorage class provides methods to save and load user data locally on the device.
 */
public class LocalUserStorage {
    private static final String FILE_NAME = "user.json";
    /**
     * Saves user data locally on the device.
     *
     * @param context The context of the application.
     * @param user    The user object to be saved.
     */
    public static void saveUser(Context context, User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(userJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Loads user data from a local file on the device.
     *
     * @param context The context of the application.
     * @return The User object loaded from the file, or null if the file doesn't exist or there's an error.
     */
    public static User loadUser(Context context) {
        Gson gson = new Gson();

        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            InputStreamReader isr = new InputStreamReader(fis);
            return gson.fromJson(isr, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            // Return an empty list if there's an issue loading the file
            return null;
        }
    }
}

