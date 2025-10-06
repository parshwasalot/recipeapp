package com.example.recipeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private WebView webView;
    private ImageView imageView;
    private FloatingActionButton fabFavorite;
    private FavoritesManager favoritesManager;
    private String recipeId;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        titleTextView = findViewById(R.id.detail_title);
        descriptionTextView = findViewById(R.id.detail_description);
        webView = findViewById(R.id.detail_webview);
        imageView = findViewById(R.id.detail_image);
        fabFavorite = findViewById(R.id.fab_favorite);

        favoritesManager = new FavoritesManager();

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String videoUrl = getIntent().getStringExtra("videoUrl");
        int imageResId = getIntent().getIntExtra("imageResId", -1);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        recipeId = getIntent().getStringExtra("recipeId");

        Log.d("RecipeDetailActivity", "Received: " +
                "Title: " + title +
                ", Desc: " + description +
                ", URL: " + videoUrl +
                ", ImageResId: " + imageResId +
                ", ImageUrl: " + imageUrl);

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

        // Handle image loading - check if it's a URL or drawable resource
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Load image from URL using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else if (imageResId != -1) {
            // Load local drawable resource
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

        // Setup favorite button
        if (recipeId != null && !recipeId.isEmpty()) {
            checkFavoriteStatus();
            fabFavorite.setOnClickListener(v -> toggleFavorite());
        } else {
            fabFavorite.setVisibility(View.GONE);
        }
    }

    private void checkFavoriteStatus() {
        favoritesManager.isFavorite(recipeId, isFav -> {
            isFavorite = isFav;
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void toggleFavorite() {
        favoritesManager.toggleFavorite(recipeId, new FavoritesManager.OnFavoriteActionListener() {
            @Override
            public void onSuccess() {
                isFavorite = !isFavorite;
                updateFavoriteIcon();
                String message = isFavorite ? "Added to favorites" : "Removed from favorites";
                Toast.makeText(RecipeDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RecipeDetailActivity.this, "Error updating favorite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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