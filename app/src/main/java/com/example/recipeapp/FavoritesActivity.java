package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private static final String TAG = "FavoritesActivity";
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> favoriteRecipes;
    private FavoritesManager favoritesManager;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.favorites_recycler_view);
        progressBar = findViewById(R.id.favorites_progress_bar);
        emptyTextView = findViewById(R.id.favorites_empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoritesManager = new FavoritesManager();
        db = FirebaseFirestore.getInstance();
        favoriteRecipes = new ArrayList<>();

        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload favorites when returning to this activity
        loadFavorites();
    }

    private void loadFavorites() {
        progressBar.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        
        favoritesManager.getUserFavorites(new FavoritesManager.OnFavoritesLoadListener() {
            @Override
            public void onFavoritesLoaded(List<String> recipeIds) {
                if (recipeIds.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    favoriteRecipes.clear();
                    updateRecyclerView();
                    return;
                }

                loadRecipeDetails(recipeIds);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                Toast.makeText(FavoritesActivity.this, "Error loading favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading favorites", e);
            }
        });
    }

    private void loadRecipeDetails(List<String> recipeIds) {
        favoriteRecipes.clear();
        final int[] loadedCount = {0};

        for (String recipeId : recipeIds) {
            db.collection("recipes").document(recipeId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String id = documentSnapshot.getId();
                            String name = documentSnapshot.getString("name");
                            String description = documentSnapshot.getString("description");
                            String videoUrl = documentSnapshot.getString("videoUrl");
                            String imageUrl = documentSnapshot.getString("imageUrl");

                            Recipe recipe = new Recipe(id, name, description, videoUrl, imageUrl);
                            favoriteRecipes.add(recipe);
                        }

                        loadedCount[0]++;
                        if (loadedCount[0] == recipeIds.size()) {
                            progressBar.setVisibility(View.GONE);
                            if (favoriteRecipes.isEmpty()) {
                                emptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                emptyTextView.setVisibility(View.GONE);
                            }
                            updateRecyclerView();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading recipe: " + recipeId, e);
                        loadedCount[0]++;
                        if (loadedCount[0] == recipeIds.size()) {
                            progressBar.setVisibility(View.GONE);
                            if (favoriteRecipes.isEmpty()) {
                                emptyTextView.setVisibility(View.VISIBLE);
                            }
                            updateRecyclerView();
                        }
                    });
        }
    }

    private void updateRecyclerView() {
        recipeAdapter = new RecipeAdapter(this, favoriteRecipes, this);
        recyclerView.setAdapter(recipeAdapter);
        Log.d(TAG, "Favorite recipes count: " + favoriteRecipes.size());
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("title", recipe.getTitle());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("videoUrl", recipe.getVideoUrl());
        intent.putExtra("imageResId", recipe.getImageResId());
        intent.putExtra("imageUrl", recipe.getImageUrl());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
