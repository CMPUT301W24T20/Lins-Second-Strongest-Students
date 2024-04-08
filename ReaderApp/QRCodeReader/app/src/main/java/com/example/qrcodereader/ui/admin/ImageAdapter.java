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
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
/**
 * Adapter for displaying images in a RecyclerView.
 * Supports single selection and long-click listeners.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<String> mImageUrls;
    private SparseBooleanArray selectedPositions;
    private int preloadCount = 6;
    private OnImageLongClickListener longClickListener;

    /**
     * Constructor for the ImageAdapter.
     *
     * @param context   The context.
     * @param imageUrls List of image URLs.
     */
    public ImageAdapter(Context context, List<String> imageUrls) {
        mContext = context;
        mImageUrls = imageUrls;
        selectedPositions = new SparseBooleanArray();
    }
    /**
     * Interface for long-click listener on images.
     */
    public interface OnImageLongClickListener {
        /**
         * Callback method for long-click event on an image.
         *
         * @param imageUrl The URL of the long-clicked image.
         */
        void onImageLongClick(String imageUrl);
    }
    /**
     * Sets the long-click listener for the images.
     *
     * @param listener The long-click listener.
     */
    public void setOnImageLongClickListener(OnImageLongClickListener listener) {
        if (listener != null) {
            this.longClickListener = listener;
        }
    }

    /**
     * Toggles selection of an image at the given position.
     *
     * @param position The position of the image to toggle.
     */
    public void toggleSelection(int position) {
        selectedPositions.put(position, !selectedPositions.get(position));
        notifyItemChanged(position); // Notify that specific item changed
    }

    /**
     * Gets the list of selected images.
     *
     * @return List of selected image URLs.
     */
    public List<String> getSelectedImages() {
        List<String> selectedImages = new ArrayList<>();
        for (int i = 0; i < mImageUrls.size(); i++) {
            if (selectedPositions.get(i)) {
                selectedImages.add(mImageUrls.get(i));
            }
        }
        return selectedImages;
    }

    /**
     * ViewHolder class for the images.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Binds the image at the given position to the ViewHolder.
     *
     * @param holder   The ViewHolder.
     * @param position The position of the image.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(position);

        int nextPosition = position + preloadCount;
        if (nextPosition < mImageUrls.size()) {
            Picasso.get()
                    .load(mImageUrls.get(nextPosition)).resize(100, 100).centerCrop()
                    .fetch(); // Asynchronously load the image without displaying it
        }
    }

    /**
     * Gets the number of images in the adapter.
     *
     * @return The number of images.
     */
    @Override
    public int getItemCount() {return mImageUrls.size();}

    /**
     * ViewHolder class for the images.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView imageView;

        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String imageUrl = mImageUrls.get(position);
                    longClickListener.onImageLongClick(imageUrl);
                    return true;
                }
            }
            return false;
        }

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(int position) {
            imageView.setTag(position);
            imageView.setOnLongClickListener(this); // Set the long click listener here

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
            Picasso.get().load(mImageUrls.get(position)).resize(100, 100).centerCrop().into(imageView);
        }
    }
}