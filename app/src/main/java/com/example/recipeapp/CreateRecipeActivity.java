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

public class CreateRecipeActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, videoUrlEditText, imageUrlEditText;
    private Button createButton;
    private FirebaseFirestore db;
    private static final String TAG = "CreateRecipeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.recipe_name);
        descriptionEditText = findViewById(R.id.recipe_description);
        videoUrlEditText = findViewById(R.id.recipe_video_url);
        imageUrlEditText = findViewById(R.id.recipe_image_url);
        createButton = findViewById(R.id.create_recipe_button);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Recipe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        createButton.setOnClickListener(v -> createRecipe());
    }

    private void createRecipe() {
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

        // Create recipe map
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("name", name);
        recipe.put("description", description);
        recipe.put("videoUrl", videoUrl);
        recipe.put("imageUrl", imageUrl);
        recipe.put("createdAt", com.google.firebase.Timestamp.now());

        // Add to Firestore
        db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Recipe created with ID: " + documentReference.getId());
                    Toast.makeText(CreateRecipeActivity.this, "Recipe created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to previous activity
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error creating recipe", e);
                    Toast.makeText(CreateRecipeActivity.this, "Error creating recipe: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
