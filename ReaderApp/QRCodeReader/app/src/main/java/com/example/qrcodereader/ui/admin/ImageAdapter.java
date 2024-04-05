package com.example.qrcodereader.ui.admin;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodereader.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> mImageUrls;
    private SparseBooleanArray selectedPositions;

    public ImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
        selectedPositions = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        selectedPositions.put(position, !selectedPositions.get(position));
        notifyItemChanged(position); // Notify that specific item changed
    }

    public List<String> getSelectedImages() {
        List<String> selectedImages = new ArrayList<>();
        for (int i = 0; i < mImageUrls.size(); i++) {
            if (selectedPositions.get(i)) {
                selectedImages.add(mImageUrls.get(i));
            }
        }
        return selectedImages;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(int position) {
            if (selectedPositions.get(position)) {
                // Add a blue overlay on the selected image
                imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.sky_blue_translucent));
            } else {
                // Remove the blue overlay for unselected images
                imageView.clearColorFilter();
            }

            imageView.setOnClickListener(v -> {
                toggleSelection(getAdapterPosition());
            });
            Picasso.get().load(mImageUrls.get(position)).into(imageView);
        }
    }
}