package com.example.recipeapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateRecipeActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, videoUrlEditText, imageUrlEditText;
    private Button updateButton;
    private FirebaseFirestore db;
    private String recipeId;
    private static final String TAG = "UpdateRecipeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.recipe_name);
        descriptionEditText = findViewById(R.id.recipe_description);
        videoUrlEditText = findViewById(R.id.recipe_video_url);
        imageUrlEditText = findViewById(R.id.recipe_image_url);
        updateButton = findViewById(R.id.update_recipe_button);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Recipe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get recipe data from intent
        recipeId = getIntent().getStringExtra("recipeId");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Pre-fill the form with existing data
        if (recipeId != null) {
            nameEditText.setText(name);
            descriptionEditText.setText(description);
            videoUrlEditText.setText(videoUrl);
            imageUrlEditText.setText(imageUrl);
        }

        updateButton.setOnClickListener(v -> updateRecipe());
    }

    private void updateRecipe() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String videoUrl = videoUrlEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Recipe name is required.");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Description is required.");
            return;
        }
        if (TextUtils.isEmpty(videoUrl)) {
            videoUrlEditText.setError("YouTube link is required.");
            return;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrlEditText.setError("Image URL is required.");
            return;
        }

        if (recipeId == null) {
            Toast.makeText(this, "Error: Recipe ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create recipe map
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("name", name);
        recipe.put("description", description);
        recipe.put("videoUrl", videoUrl);
        recipe.put("imageUrl", imageUrl);
        recipe.put("updatedAt", com.google.firebase.Timestamp.now());

        // Update in Firestore
        db.collection("recipes").document(recipeId)
                .update(recipe)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Recipe updated successfully");
                    Toast.makeText(UpdateRecipeActivity.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to previous activity
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating recipe", e);
                    Toast.makeText(UpdateRecipeActivity.this, "Error updating recipe: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
