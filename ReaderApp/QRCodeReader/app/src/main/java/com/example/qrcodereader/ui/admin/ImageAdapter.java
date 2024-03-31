package com.example.qrcodereader.ui.admin;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.example.qrcodereader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mImageUrls;
    private SparseBooleanArray SelectedPositions;

    public ImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
        SelectedPositions = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        SelectedPositions.put(position, !SelectedPositions.get(position));
        notifyDataSetChanged();
    }

    public List<String> getSelectedImages() {
        List<String> selectedImages = new ArrayList<>();
        for (int i = 0; i < mImageUrls.size(); i++) {
            if (SelectedPositions.get(i)) {
                selectedImages.add(mImageUrls.get(i));
            }
        }
        return selectedImages;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300)); // Adjust size as needed
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        if (SelectedPositions.get(position)) {
            // Add a blue overlay on the selected image
            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.sky_blue_translucent));
        } else {
            // Remove the blue overlay for unselected images
            imageView.clearColorFilter();
        }

        imageView.setOnClickListener(v -> {
            toggleSelection(position);
        });

        Picasso.get().load(mImageUrls.get(position)).into(imageView);
        return imageView;
    }
}