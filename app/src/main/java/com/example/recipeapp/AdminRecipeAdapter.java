package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdminRecipeAdapter extends RecyclerView.Adapter<AdminRecipeAdapter.AdminRecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private OnAdminRecipeActionListener listener;

    public interface OnAdminRecipeActionListener {
        void onEditRecipe(Recipe recipe);
        void onDeleteRecipe(Recipe recipe);
    }

    public AdminRecipeAdapter(Context context, List<Recipe> recipeList, OnAdminRecipeActionListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_recipe_item, parent, false);
        return new AdminRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());

        // Load image using Glide
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.recipeImage);
        }

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditRecipe(recipe);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteRecipe(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class AdminRecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription;
        Button editButton, deleteButton;

        public AdminRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.admin_recipe_image);
            recipeName = itemView.findViewById(R.id.admin_recipe_name);
            recipeDescription = itemView.findViewById(R.id.admin_recipe_description);
            editButton = itemView.findViewById(R.id.btn_edit_recipe);
            deleteButton = itemView.findViewById(R.id.btn_delete_recipe);
        }
    }
}
