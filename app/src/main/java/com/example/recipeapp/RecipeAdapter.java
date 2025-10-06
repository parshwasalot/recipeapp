package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private OnRecipeClickListener listener;

    // Constructor
    public RecipeAdapter(Context context, List<Recipe> recipes, OnRecipeClickListener listener) {
        this.context = context;
        this.recipes = recipes;
        this.listener = listener;
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.descriptionTextView.setText(recipe.getDescription());
        
        // Load image: Check if it's a Firestore recipe (with URL) or local recipe (with drawable)
        if (recipe.isFirestoreRecipe()) {
            // Load image from URL using Glide
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imageView);
        } else {
            // Load local drawable resource
            holder.imageView.setImageResource(recipe.getImageResId());
        }

        // Set the click listener
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0; // Ensure recipes is not null
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView descriptionTextView;
        ImageView imageView;

        RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recipe_title);
            descriptionTextView = itemView.findViewById(R.id.recipe_description);
            imageView = itemView.findViewById(R.id.recipe_image);
        }
    }
}
