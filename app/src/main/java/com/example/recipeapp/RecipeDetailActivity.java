package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private WebView webView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        titleTextView = findViewById(R.id.detail_title);
        descriptionTextView = findViewById(R.id.detail_description);
        webView = findViewById(R.id.detail_webview);
        imageView = findViewById(R.id.detail_image);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String videoUrl = getIntent().getStringExtra("videoUrl");
        int imageResId = getIntent().getIntExtra("imageResId", -1);

        Log.d("RecipeDetailActivity", "Received: " +
                "Title: " + title +
                ", Desc: " + description +
                ", URL: " + videoUrl +
                ", ImageResId: " + imageResId);

        if (title != null && !title.isEmpty()) {
            titleTextView.setText(title);
            titleTextView.setVisibility(View.VISIBLE);
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        if (description != null && !description.isEmpty()) {
            descriptionTextView.setText(description);
            descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }

        if (imageResId != -1) {
            imageView.setImageResource(imageResId);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }

        String videoId = extractVideoId(videoUrl);
        if (videoId != null) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            webView.getSettings().setJavaScriptEnabled(true);
            String videoEmbedHtml = "<html><body style='margin:0;padding:0;'><iframe width='100%' height='100%' src='" + embedUrl + "' frameborder='0' allowfullscreen></iframe></body></html>";
            webView.loadData(videoEmbedHtml, "text/html", "utf-8");
            webView.setVisibility(View.VISIBLE);
        } else {
            webView.setVisibility(View.GONE);
        }
    }

    private String extractVideoId(String url) {
        if (url == null) return null;
        String videoId = null;
        if (url.contains("v=")) {
            String[] parts = url.split("v=");
            if (parts.length > 1) {
                videoId = parts[1];
                int ampersandPosition = videoId.indexOf('&');
                if (ampersandPosition != -1) {
                    videoId = videoId.substring(0, ampersandPosition);
                }
            }
        } else if (url.contains("youtu.be/")) {
            String[] parts = url.split("youtu.be/");
            if (parts.length > 1) {
                videoId = parts[1];
                int questionMarkPosition = videoId.indexOf('?');
                if (questionMarkPosition != -1) {
                    videoId = videoId.substring(0, questionMarkPosition);
                }
            }
        }
        return videoId;
    }
}