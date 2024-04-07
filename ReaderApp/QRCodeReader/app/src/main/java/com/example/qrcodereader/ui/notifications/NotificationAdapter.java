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

/**
 * NotificationAdapter
 * ArrayAdapter specialized to work with notifications
 * in NotificationActivity
 */
public class NotificationAdapter extends ArrayAdapter<NotificationDetail> {
    //Microsoft Copilot, 2024, adapter for image/title/body object
    public NotificationAdapter(Context context, List<NotificationDetail> notifications) {
        super(context, 0, notifications);
    }

    /**
     * NotificationAdapter(GetView)
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return View consisting of image, title text and body text
     */
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
            image.setImageResource(R.drawable._49e43ff77b9c6ecc64d8a9b55622ddd7_2);
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
