package com.example.qrcodereader.ui.admin;

import static android.view.View.GONE;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.qrcodereader.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ImageDetail extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_detail, null);

        ImageView image = view.findViewById(R.id.loadedImage);
        TextView deviceID = view.findViewById(R.id.phone);
        TextView eventDescriptor = view.findViewById(R.id.EventText);
        TextView event = view.findViewById(R.id.EventName);

        String imageURL = getArguments().getString("URL");
        String Name = getName(imageURL);

        if (Objects.equals(getArguments().getString("type"), "UploadedProfilePics")){
            eventDescriptor.setVisibility(GONE);
            deviceID.setText(Name.substring(0, 16));
        } else{
            event.setText(Name.substring(0,19));
            deviceID.setText(Name.substring(21, 37));
        }
        Picasso.get().load(imageURL).into(image);

        builder.setView(view)
                .setTitle(Name);

        return builder.create();
    }

    // how to get image name from token
    private String getName (String URL){
        String[] parts = URL.split("/");
        String fullImageName = parts[parts.length - 1];
        int index = fullImageName.indexOf("?");
        String imageNameWithPrefix = index != -1 ? fullImageName.substring(0, index) : fullImageName;
        String[] imageNameParts = imageNameWithPrefix.split("%2F");
        return imageNameParts.length > 1 ? imageNameParts[1] : imageNameWithPrefix;
    }
}
