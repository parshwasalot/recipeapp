package com.example.recipeapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private List<Recipe> filteredRecipeList;
    private View rootView; // To anchor the Snackbar
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout that contains the RecyclerView
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView); // Find RecyclerView in the fragment's view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use getContext() in fragments

        initializeRecipeList();
        loadFirestoreRecipes(); // Load recipes from Firestore
        showFilterDialog(); // Show filter dialog on start

        return rootView;
    }

    private void initializeRecipeList() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        // Initialize empty recipe list - will be populated from Firestore
        recipeList = new ArrayList<>();
    }

    private void loadFirestoreRecipes() {
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String videoUrl = document.getString("videoUrl");
                        String imageUrl = document.getString("imageUrl");

                        Recipe recipe = new Recipe(id, name, description, videoUrl, imageUrl);
                        recipeList.add(recipe);
                    }
                    Log.d("HomeFragment", "Loaded " + queryDocumentSnapshots.size() + " recipes from Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.w("HomeFragment", "Error loading Firestore recipes", e);
                });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("What are you looking for? (Meal type or ingredient)");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userInput = input.getText().toString().toLowerCase();
            filterRecipes(userInput);
        });

        builder.setNegativeButton("Show All", (dialog, which) -> {
            filteredRecipeList = new ArrayList<>(recipeList);
            updateRecyclerView();
            showReapplyFilterSnackbar();
        });

        builder.show();
    }

    private void filterRecipes(String filter) {
        filteredRecipeList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getTitle().toLowerCase().contains(filter) ||
                    recipe.getDescription().toLowerCase().contains(filter)) {
                filteredRecipeList.add(recipe);
            }
        }
        updateRecyclerView();
        showClearFilterSnackbar();
    }

    private void updateRecyclerView() {
        recipeAdapter = new RecipeAdapter(getContext(), filteredRecipeList, this);
        recyclerView.setAdapter(recipeAdapter);
        Log.d("HomeFragment", "Filtered recipe list size: " + filteredRecipeList.size());
    }

    private void showClearFilterSnackbar() {
        Snackbar.make(rootView, "Filters applied", Snackbar.LENGTH_INDEFINITE)
                .setAction("Clear Filters", v -> {
                    filteredRecipeList = new ArrayList<>(recipeList);
                    updateRecyclerView();
                    Snackbar.make(rootView, "Filters cleared", Snackbar.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showReapplyFilterSnackbar() {
        Snackbar.make(rootView, "Showing all recipes", Snackbar.LENGTH_INDEFINITE)
                .setAction("Apply Filter", v -> showFilterDialog())
                .show();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("title", recipe.getTitle());
        intent.putExtra("description", recipe.getDescription());
        intent.putExtra("videoUrl", recipe.getVideoUrl());
        intent.putExtra("imageResId", recipe.getImageResId());
        intent.putExtra("imageUrl", recipe.getImageUrl());
        startActivity(intent);
    }
}
