package com.example.recipeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageRecipesActivity extends AppCompatActivity implements AdminRecipeAdapter.OnAdminRecipeActionListener {

    private RecyclerView recyclerView;
    private AdminRecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAddRecipe;
    private static final String TAG = "ManageRecipesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipes);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Manage Recipes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recyclerView = findViewById(R.id.admin_recipes_recycler_view);
        fabAddRecipe = findViewById(R.id.fab_add_recipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        recipeAdapter = new AdminRecipeAdapter(this, recipeList, this);
        recyclerView.setAdapter(recipeAdapter);

        // Load recipes from Firestore
        loadRecipes();

        // FAB click listener
        fabAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRecipesActivity.this, CreateRecipeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload recipes when returning to this activity
        loadRecipes();
    }

    private void loadRecipes() {
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recipeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String videoUrl = document.getString("videoUrl");
                        String imageUrl = document.getString("imageUrl");

                        Recipe recipe = new Recipe(id, name, description, videoUrl, imageUrl);
                        recipeList.add(recipe);
                    }
                    recipeAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + recipeList.size() + " recipes");
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error loading recipes", e);
                    Toast.makeText(ManageRecipesActivity.this, "Error loading recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onEditRecipe(Recipe recipe) {
        Intent intent = new Intent(ManageRecipesActivity.this, UpdateRecipeActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("name", recipe.getName());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("videoUrl", recipe.getVideoUrl());
        intent.putExtra("imageUrl", recipe.getImageUrl());
        startActivity(intent);
    }

    @Override
    public void onDeleteRecipe(Recipe recipe) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete \"" + recipe.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteRecipe(recipe.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteRecipe(String recipeId) {
        db.collection("recipes").document(recipeId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Recipe deleted successfully");
                    Toast.makeText(ManageRecipesActivity.this, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show();
                    loadRecipes(); // Reload the list
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error deleting recipe", e);
                    Toast.makeText(ManageRecipesActivity.this, "Error deleting recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
