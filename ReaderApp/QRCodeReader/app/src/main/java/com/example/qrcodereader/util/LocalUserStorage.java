package com.example.qrcodereader.util;
import android.content.Context;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.example.qrcodereader.entity.User;

public class LocalUserStorage {
    private static final String FILE_NAME = "user.json";

    public static void saveUser(Context context, User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(userJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUser(Context context) {
        Gson gson = new Gson();

        try (FileInputStream fis = context.openFileInput(FILE_NAME)) {
            InputStreamReader isr = new InputStreamReader(fis);
            return gson.fromJson(isr, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

