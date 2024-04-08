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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"

/**
 * ArrayAdapter for displaying a list of users.
 * <p>
 * This class is used to populate a ListView with user information.
 * </p>
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;

    /**
     * Constructs a new UserArrayAdapter.
     *
     * @param context The context in which the adapter is being used.
     * @param users   The list of users to be displayed.
     */

    public UserArrayAdapter(Context context, ArrayList<User> users){
        super(context,0, users);
        this.users = users;
        this.context = context;
    }
    /**
     * Returns the view for the specified position in the list.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The view for the specified position.
     */
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
        ImageView ProfilePicture = view.findViewById(R.id.profile_picture);

        userId.setText(user.getUserID());
        userName.setText(user.getName());
        email.setText(user.getEmail());
        phoneRegion.setText(user.getPhoneRegion());
        phone.setText(user.getPhone());
        String imagePoster = user.getProfilePicture();
        Picasso.get().load(imagePoster).resize(130, 130).centerInside().into(ProfilePicture);

        return view;
    }
}
