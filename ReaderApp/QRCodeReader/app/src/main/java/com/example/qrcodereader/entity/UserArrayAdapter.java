package com.example.qrcodereader.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcodereader.R;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;

    public UserArrayAdapter(Context context, ArrayList<User> users){
        super(context,0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.users_content, parent,false);
        }

        User user = users.get(position);

        TextView userId = view.findViewById(R.id.user_id);
        TextView userName = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView phoneRegion = view.findViewById(R.id.phone_region);
        TextView phone = view.findViewById(R.id.phone);
        ImageView ProfilePicture = view.findViewById(R.id.user_profile_photo);

        userId.setText(user.getUserID());
        userName.setText(user.getName());
        email.setText(user.getEmail());
        phoneRegion.setText(user.getPhoneRegion());
        phone.setText(user.getPhone());



//        String imagePoster = user.getProfilePicture();
//        Picasso.get().load(imagePoster).resize(130, 130).centerInside().into(ProfilePicture);

        return view;
    }
}
