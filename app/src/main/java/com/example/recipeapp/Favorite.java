package com.example.recipeapp;

import com.google.firebase.Timestamp;

public class Favorite {
    private String userId;
    private String recipeId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor required for Firestore
    public Favorite() {
    }

    public Favorite(String userId, String recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
