package com.example.recipeapp;

public class Recipe {
    private String id; // Firestore document ID
    private String name;
    private String description;
    private String videoUrl;
    private String imageUrl; // Changed from imageResId to imageUrl for Firestore
    private int imageResId; // Keep for backward compatibility with local recipes

    // Default constructor required for Firestore
    public Recipe() {
    }

    // Constructor for local recipes (with drawable resource)
    public Recipe(String title, String description, String videoUrl, int imageResId) {
        this.name = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.imageResId = imageResId;
        this.imageUrl = ""; // Empty for local recipes
    }

    // Constructor for Firestore recipes (with image URL)
    public Recipe(String id, String name, String description, String videoUrl, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
        this.imageResId = 0; // No drawable resource for Firestore recipes
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return name; // Alias for backward compatibility
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isFirestoreRecipe() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
