package com.example.qrcodereader.ui.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrcodereader.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationDetail> {
    public NotificationAdapter(Context context, List<NotificationDetail> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NotificationDetail notification = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notification, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView body = (TextView) convertView.findViewById(R.id.body);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        // Populate the data into the template view using the data object
        title.setText(notification.getTitle());
        body.setText(notification.getBody());
        if (notification.getPoster() == null) {
            image.setImageBitmap(BitmapFactory.decodeFile("EventPoster/noEventPoster.png"));
        }
        new DownloadImageTask(image)
                .execute(notification.getPoster());

        // Return the completed view to render on screen
        return convertView;
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        //Microsoft Copilot, 2024, set imageView from URL
        private WeakReference<ImageView> bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = new WeakReference<>(bmImage);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView imageView = bmImage.get();
            if (imageView != null && result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }


}
