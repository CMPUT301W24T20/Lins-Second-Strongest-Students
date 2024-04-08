package com.example.qrcodereader.ui.admin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodereader.R;

import java.util.Arrays;
import java.util.List;

public class TestActivity extends Activity {
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    List<String> imageUrls = Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.test_container);
        setContentView(frameLayout);

        recyclerView = new RecyclerView(this);
        recyclerView.setId(R.id.recyclerView);
        frameLayout.addView(recyclerView);

        imageAdapter = new ImageAdapter(this, imageUrls);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(imageAdapter);
    }
}
